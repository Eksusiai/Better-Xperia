package com.sonyericsson.calendar.util;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.Time;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.google.common.base.Charsets;
import com.sonymobile.calendar.QuotedPrintableCodec;
import com.sonymobile.calendar.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public abstract class CalendarParser {
    public static final int MAX_LINE_LENGTH = 76;
    private long calendarId;
    protected List<CalendarObject> itemList = null;
    protected CalendarObject currentItem = null;
    protected boolean parsingOngoing = false;
    protected Queue<OnReadyHandler> handlers = new LinkedList();
    private Uri fileUri = null;

    protected interface OnReadyHandler {
        void onReady(boolean z);
    }

    protected abstract void handleBegin(String str) throws IOException;

    protected abstract void handleEnd(String str) throws IOException;

    protected abstract void handleTag(String str, String str2, boolean z) throws IOException;

    protected abstract void insertCalEvent(Context context, List<CalendarObject> list, long j);

    protected abstract void parseRow(String str, boolean z) throws Exception;

    protected CalendarParser() {
    }

    public Uri getFileUri() {
        return this.fileUri;
    }

    public void parseUri(Context context, Uri uri, long j, final IAsyncServiceResultHandler iAsyncServiceResultHandler) {
        this.calendarId = j;
        this.handlers.add(new OnReadyHandler() { // from class: com.sonyericsson.calendar.util.CalendarParser.1
            @Override // com.sonyericsson.calendar.util.CalendarParser.OnReadyHandler
            public void onReady(boolean z) {
                iAsyncServiceResultHandler.onResult(Boolean.valueOf(z), null);
                CalendarParser.this.itemList = null;
                CalendarParser.this.currentItem = null;
                CalendarParser.this.parsingOngoing = false;
            }
        });
        new ParseCalAsync(context, j).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:38:0x005e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public List<CalendarObject> parseFromCalFile(Context context, Uri uri) throws IOException {
        if (uri == null) {
            Toast.makeText(context, context.getString(R.string.operation_failed), 1).show();
            return new LinkedList();
        }
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            return this.itemList;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    boolean zShouldAppendThis = shouldAppendThis(line);
                    if (zShouldAppendThis) {
                        line = removeAppendThisSign(line);
                    }
                    try {
                        parseRow(removeAppendNextSign(line), zShouldAppendThis);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return this.itemList;
            } finally {
                bufferedReader.close();
            }
        } finally {
            inputStream.close();
        }
    }

    private boolean shouldAppendThis(String str) {
        return str.startsWith(CalendarConstants.LWSP_TAB) || str.startsWith(" ");
    }

    private String removeAppendNextSign(String str) {
        return str.endsWith("=") ? str.substring(0, str.length() - 1) : str;
    }

    private String removeAppendThisSign(String str) {
        if (str.startsWith(CalendarConstants.LWSP_TAB)) {
            return str.substring(1);
        }
        return str.startsWith(" ") ? str.substring(1) : str;
    }

    private class ParseCalAsync extends AsyncTask<Uri, Void, List<CalendarObject>> {
        private long calenderId;
        private Context context;

        public ParseCalAsync(Context context, long j) {
            this.context = context;
            this.calenderId = j;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<CalendarObject> list) {
            CalendarParser.this.loadExistingEvents(this.context, this.calenderId, list);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<CalendarObject> doInBackground(Uri... uriArr) {
            try {
                return CalendarParser.this.parseFromCalFile(this.context, uriArr[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return new LinkedList();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadExistingEvents(final Context context, final long j, final List<CalendarObject> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        long j2 = Long.MAX_VALUE;
        long j3 = 0;
        for (CalendarObject calendarObject : list) {
            if (calendarObject.start < j2) {
                j2 = calendarObject.start;
            }
            if (calendarObject.end > j3) {
                j3 = calendarObject.end;
            }
        }
        Time time = new SafeTime();
        time.set(j2);
        Time time2 = new SafeTime();
        time2.set(j3);
        EventLoaderService.getInstance().requestLoad(context, time, time2, new IAsyncServiceResultHandler() { // from class: com.sonyericsson.calendar.util.CalendarParser.2
            @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
            public void onResult(Object obj, Object obj2) {
                CalendarParser.this.insertCalEvent(context, list, j);
            }
        }, false);
    }

    protected boolean isEventDuplicate(CalendarObject calendarObject, ArrayList<EventInfo> arrayList) {
        for (EventInfo eventInfo : arrayList) {
            if (!TextUtils.isEmpty(calendarObject.summary) && !TextUtils.isEmpty(eventInfo.title) && VCalendarParser.decodeQuotedPrintable(calendarObject.summary).equals(eventInfo.title) && calendarObject.start == eventInfo.begin && calendarObject.end == eventInfo.end && this.calendarId == eventInfo.calendarId) {
                return true;
            }
        }
        return false;
    }

    public void writeToCalFile(ArrayList<EventInfo> arrayList, final IAsyncServiceResultHandler iAsyncServiceResultHandler, Context context) {
        if (arrayList == null || arrayList.isEmpty()) {
            throw new IllegalArgumentException("ArrayList is empty or null");
        }
        this.handlers.add(new OnReadyHandler() { // from class: com.sonyericsson.calendar.util.CalendarParser.3
            @Override // com.sonyericsson.calendar.util.CalendarParser.OnReadyHandler
            public void onReady(boolean z) {
                iAsyncServiceResultHandler.onResult(Boolean.valueOf(z), null);
            }
        });
        new WriteCalAsync(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri parseToCalFile(ArrayList<EventInfo> arrayList, Context context) throws IOException {
        File file = new File(context.getFilesDir().getAbsolutePath(), "vCalendar.vcs");
        file.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8));
        try {
            writeToFile(bufferedWriter, CalendarConstants.BEGIN, CalendarConstants.VCALENDAR);
            writeToFile(bufferedWriter, CalendarConstants.VERSION, "1.0");
            for (EventInfo eventInfo : arrayList) {
                writeToFile(bufferedWriter, CalendarConstants.BEGIN, CalendarConstants.VEVENT);
                writeToFile(bufferedWriter, CalendarConstants.DTSTART, parseTime(Long.valueOf(eventInfo.begin)));
                writeToFile(bufferedWriter, CalendarConstants.DTEND, parseTime(Long.valueOf(eventInfo.end)));
                writeToFileWrapped(bufferedWriter, CalendarConstants.LOCATION, eventInfo.eventLocation, QuotedPrintableCodec.needQPEncode(eventInfo.eventLocation));
                writeToFileWrapped(bufferedWriter, CalendarConstants.SUMMARY, eventInfo.title, QuotedPrintableCodec.needQPEncode(eventInfo.title));
                writeToFileWrapped(bufferedWriter, CalendarConstants.DESCRIPTION, eventInfo.description, true);
                writeToFile(bufferedWriter, CalendarConstants.CATEGORIES, CalendarConstants.MISCELLANEOUS);
                if (parseRRule(eventInfo) != null) {
                    writeToFile(bufferedWriter, CalendarConstants.RRULE, parseRRule(eventInfo));
                }
                writeToFile(bufferedWriter, "END", CalendarConstants.VEVENT);
            }
            writeToFile(bufferedWriter, "END", CalendarConstants.VCALENDAR);
            Uri uriForFile = FileProvider.getUriForFile(context, "com.sonymobile.calendar.VCalendarParser", file);
            bufferedWriter.close();
            return uriForFile;
        } catch (Throwable th) {
            try {
                bufferedWriter.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private void writeToFile(BufferedWriter bufferedWriter, String str, String str2) throws IOException {
        bufferedWriter.write(str + CalendarConstants.COLON + str2 + CalendarConstants.CRLF);
    }

    private void writeToFileWrapped(BufferedWriter bufferedWriter, String str, String str2, boolean z) throws IOException {
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        String str3 = "";
        String str4 = z ? "=" : "";
        if (str == null) {
            str3 = CalendarConstants.LWSP_TAB;
        } else if (!z) {
            str2 = str + CalendarConstants.COLON + str2;
        } else {
            str2 = str + ";ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8" + CalendarConstants.COLON + QuotedPrintableCodec.encode(str2);
        }
        if (str2.length() > 76) {
            int qPLineSplitPoint = z ? getQPLineSplitPoint(str2) : 76;
            bufferedWriter.write(str3 + str2.substring(0, qPLineSplitPoint) + str4 + CalendarConstants.CRLF);
            writeToFileWrapped(bufferedWriter, null, str2.substring(qPLineSplitPoint, str2.length()), z);
            return;
        }
        bufferedWriter.write((str3 + str2) + CalendarConstants.CRLF);
    }

    private int getQPLineSplitPoint(String str) {
        for (int i = 1; i <= 3; i++) {
            int i2 = 76 - i;
            if (String.valueOf(str.charAt(i2)).equals("=")) {
                return i2;
            }
        }
        return 76;
    }

    private String parseTime(Long l) {
        return new SimpleDateFormat(CalendarConstants.CALENDAR_DATE_FORMAT).format(new Date(Long.valueOf(l.longValue() - VCal.getTimeZoneOffset(new Date(l.longValue()))).longValue()));
    }

    private String parseRRule(EventInfo eventInfo) {
        if (eventInfo.rrule != null) {
            try {
                return RecurrenceRuleParser.parseRruleToVCal(eventInfo.rrule);
            } catch (IllegalArgumentException unused) {
            }
        }
        return null;
    }

    private class WriteCalAsync extends AsyncTask<ArrayList<EventInfo>, Void, Uri> {
        private Context context;

        public WriteCalAsync(Context context) {
            this.context = context.getApplicationContext();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Uri uri) {
            CalendarParser.this.fileUri = uri;
            while (!CalendarParser.this.handlers.isEmpty()) {
                CalendarParser.this.handlers.remove().onReady(uri != null);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        @SafeVarargs
        public final Uri doInBackground(ArrayList<EventInfo>... arrayListArr) {
            try {
                return CalendarParser.this.parseToCalFile(arrayListArr[0], this.context);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
