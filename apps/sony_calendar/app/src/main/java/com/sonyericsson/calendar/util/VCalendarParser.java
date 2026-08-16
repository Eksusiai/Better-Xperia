package com.sonyericsson.calendar.util;
import com.sonymobile.calendar.SafeTime;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import com.sonymobile.calendar.QuotedPrintableCodec;
import com.sonymobile.lunar.lib.LunarContract;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class VCalendarParser extends CalendarParser {
    private static final String TAG = "VCalendarParser";
    private static VCalendarParser sInstance;

    private VCalendarParser() {
    }

    public static VCalendarParser getInstance() {
        if (sInstance == null) {
            sInstance = new VCalendarParser();
        }
        return sInstance;
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void handleTag(String str, String str2, boolean z) throws IOException {
        if (str == null || !canHandleTag(str)) {
            if (this.currentItem == null || str != null) {
                return;
            }
            if (this.currentItem.previousTag.contains(CalendarConstants.DESCRIPTION)) {
                z = true;
            }
            this.currentItem.handleTag(null, str2, z);
            return;
        }
        if (z) {
            this.currentItem.handleTag(null, str2, z);
            return;
        }
        if (str.equals(CalendarConstants.BEGIN)) {
            handleBegin(str2);
            return;
        }
        if (str.equals("END")) {
            handleEnd(str2);
        } else if (str.equals(CalendarConstants.VERSION)) {
            handleVersion(str2);
        } else {
            if (this.parsingOngoing) {
                this.currentItem.handleTag(str, str2, z);
                return;
            }
            throw new IOException("Couldn't parse tag " + str2);
        }
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void insertCalEvent(Context context, List<CalendarObject> list, long j) {
        for (CalendarObject calendarObject : list) {
            if (calendarObject.start == -1 && calendarObject.end == -1) {
                CalendarParser.OnReadyHandler onReadyHandlerPoll = this.handlers.poll();
                Log.w(TAG, "Parsing vcal file error, unset params start and end, parsing stopped");
                if (onReadyHandlerPoll != null) {
                    onReadyHandlerPoll.onReady(false);
                    return;
                }
                return;
            }
            Time time = new SafeTime();
            time.set(calendarObject.start);
            if (!isEventDuplicate(calendarObject, EventLoaderService.getInstance().getEvents(Time.getJulianDay(calendarObject.start, time.gmtoff)))) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LunarContract.EventsColumns.CALENDAR_ID, Long.valueOf(j));
                if (!TextUtils.isEmpty(calendarObject.summary)) {
                    contentValues.put(LunarContract.EventsColumns.TITLE, decodeQuotedPrintable(calendarObject.summary));
                }
                if (!TextUtils.isEmpty(calendarObject.description)) {
                    contentValues.put("description", decodeQuotedPrintable(calendarObject.description));
                }
                if (!TextUtils.isEmpty(calendarObject.location)) {
                    contentValues.put(LunarContract.EventsColumns.EVENT_LOCATION, decodeQuotedPrintable(calendarObject.location));
                }
                contentValues.put(LunarContract.EventsColumns.DTSTART, Long.valueOf(calendarObject.start));
                contentValues.put(LunarContract.EventsColumns.DTEND, Long.valueOf(calendarObject.end));
                contentValues.put(LunarContract.EventsColumns.EVENT_TIMEZONE, calendarObject.getTimeZone());
                contentValues.put(LunarContract.EventsColumns.HAS_ATTENDEE_DATA, (Integer) 1);
                ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
                arrayList.add(ContentProviderOperation.newInsert(CalendarContract.Events.CONTENT_URI).withValues(contentValues).build());
                arrayList.add(addSelfAttendee(j));
                try {
                    context.getContentResolver().applyBatch("com.android.calendar", arrayList);
                } catch (OperationApplicationException e) {
                    Log.w(TAG, "Ignoring unexpected exception", e);
                } catch (RemoteException e2) {
                    Log.w(TAG, "Ignoring unexpected remote exception", e2);
                } catch (IllegalArgumentException e3) {
                    Log.w(TAG, e3.getMessage());
                    this.handlers.poll().onReady(false);
                    return;
                }
            }
        }
        while (!this.handlers.isEmpty()) {
            this.handlers.remove().onReady((this.itemList == null || this.itemList.isEmpty()) ? false : true);
        }
    }

    public static String decodeQuotedPrintable(String str) {
        return str.startsWith(CalendarConstants.QUOTED_PRINTABLE) ? QuotedPrintableCodec.decode(str.replace(CalendarConstants.QUOTED_PRINTABLE, "")) : str;
    }

    private ContentProviderOperation addSelfAttendee(long j) {
        String str = CalendarInstanceService.getInstance().getCalendarById(j).ownerAccount;
        ContentValues contentValues = new ContentValues();
        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_EMAIL, str);
        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, (Integer) 2);
        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_TYPE, (Integer) 1);
        contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_STATUS, (Integer) 1);
        ContentProviderOperation.Builder builderWithValues = ContentProviderOperation.newInsert(CalendarContract.Attendees.CONTENT_URI).withValues(contentValues);
        builderWithValues.withValueBackReference("event_id", 0);
        return builderWithValues.build();
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void parseRow(String str, boolean z) throws Exception {
        String strSubstring;
        int iIndexOf = str.indexOf(CalendarConstants.COLON);
        if (iIndexOf == -1 || z) {
            strSubstring = null;
        } else {
            strSubstring = str.substring(0, iIndexOf);
            str = str.substring(iIndexOf + 1);
        }
        if (strSubstring != null && strSubstring.contains(";")) {
            if (strSubstring.contains(CalendarConstants.QUOTED_PRINTABLE)) {
                str = CalendarConstants.QUOTED_PRINTABLE + str;
            }
            strSubstring = strSubstring.substring(0, strSubstring.indexOf(";"));
        }
        handleTag(strSubstring, str, z);
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void handleBegin(String str) throws IOException {
        if (str.equals(CalendarConstants.VCALENDAR)) {
            if (this.itemList == null && !this.parsingOngoing) {
                this.itemList = new LinkedList();
                this.parsingOngoing = true;
                return;
            }
            throw new IOException("Error parsing new object");
        }
        if (this.currentItem != null) {
            throw new IOException("Error parsing new object");
        }
        if (str.equals(CalendarConstants.VTODO)) {
            this.currentItem = new VCal(1);
        } else if (str.equals(CalendarConstants.VEVENT)) {
            this.currentItem = new VCal(2);
        }
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void handleEnd(String str) throws IOException {
        if (str.equals(CalendarConstants.VCALENDAR)) {
            if (this.parsingOngoing && this.currentItem == null) {
                this.parsingOngoing = false;
                return;
            }
            throw new IOException("Bad state of vCalendar object " + str);
        }
        if (str.equals(CalendarConstants.VEVENT)) {
            if (this.parsingOngoing && this.currentItem != null && this.currentItem.type == 2) {
                this.itemList.add(this.currentItem);
                this.currentItem = null;
                return;
            }
            throw new IOException("Bad state of vCalendar object " + str);
        }
        if (str.equals(CalendarConstants.VTODO)) {
            if (this.parsingOngoing && this.currentItem != null && this.currentItem.type == 1) {
                this.itemList.add(this.currentItem);
                this.currentItem = null;
                return;
            }
            throw new IOException("Bad state of vCalendar object " + str);
        }
        throw new IOException("Unhandled object type " + str);
    }

    private boolean canHandleTag(String str) {
        return (str.equals(CalendarConstants.GEO) || str.equals(CalendarConstants.PRODID) || str.equals(CalendarConstants.DAYLIGHT) || str.equals(CalendarConstants.TZ)) ? false : true;
    }

    protected void handleVersion(String str) throws IOException {
        if (!str.equals("1.0")) {
            throw new IOException("Unsupported vCalendar version " + str);
        }
    }
}
