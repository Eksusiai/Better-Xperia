package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.widget.ImageView;
import android.widget.TextView;
import com.sonyericsson.calendar.util.EventInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarEventDayView extends CalendarEventView {
    public CalendarEventDayView(Context context, EventInfo eventInfo, CalendarEventNavigator calendarEventNavigator, int i, int i2) {
        super(context, eventInfo, calendarEventNavigator, i, i2);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        scaleEventTextSize(this.mTitleTextView);
        int paddingStart = (i - this.mTitleTextView.getPaddingStart()) - this.mTitleTextView.getPaddingEnd();
        if (this.eventInfo.isAlarmEvent) {
            scaleAlarmImageView(i2, i);
            paddingStart -= (this.mAlarmIconImageView.getWidth() + this.mAlarmIconImageView.getPaddingStart()) + this.mAlarmIconImageView.getPaddingEnd();
        }
        determineTextViewLines(calculateAvailableRows(i2, this.mTitleTextView.getLineHeight()), paddingStart);
    }

    @Override // com.sonymobile.calendar.CalendarEventView
    protected void setupViews() {
        SpannableString spannableString;
        if (this.eventInfo.isAlarmEvent) {
            this.mTitleTextView = (TextView) findViewById(R.id.alarm_event_description);
            this.mAlarmIconImageView = (ImageView) findViewById(R.id.alarm_event_icon);
        } else {
            this.mTitleTextView = (TextView) findViewById(R.id.event_title_and_location);
        }
        String strDetermineTitle = determineTitle();
        if (TextUtils.isEmpty(this.eventInfo.eventLocation)) {
            spannableString = new SpannableString(strDetermineTitle);
        } else {
            spannableString = new SpannableString(strDetermineTitle + System.lineSeparator() + this.eventInfo.eventLocation);
        }
        spannableString.setSpan(new StyleSpan(1), 0, strDetermineTitle.length(), 0);
        this.mTitleTextView.setText(spannableString);
        this.mTitleTextView.post(new Runnable() { // from class: com.sonymobile.calendar.CalendarEventDayView.1
            @Override // java.lang.Runnable
            public void run() {
                if (CalendarEventDayView.this.getWidth() != 0) {
                    int width = (CalendarEventDayView.this.getWidth() - CalendarEventDayView.this.mTitleTextView.getPaddingStart()) - CalendarEventDayView.this.mTitleTextView.getPaddingEnd();
                    if (CalendarEventDayView.this.eventInfo.isAlarmEvent) {
                        width -= (CalendarEventDayView.this.mAlarmIconImageView.getWidth() + CalendarEventDayView.this.mAlarmIconImageView.getPaddingStart()) + CalendarEventDayView.this.mAlarmIconImageView.getPaddingEnd();
                    }
                    CalendarEventDayView calendarEventDayView = CalendarEventDayView.this;
                    calendarEventDayView.determineTextViewLines(calendarEventDayView.calculateAvailableRows(calendarEventDayView.getHeight(), CalendarEventDayView.this.mTitleTextView.getLineHeight()), width);
                }
            }
        });
    }

    @Override // com.sonymobile.calendar.CalendarEventView
    protected int getLayoutResource() {
        return this.eventInfo.isAlarmEvent ? R.layout.calendar_day_event_alarm_item : R.layout.calendar_day_event_item;
    }

    int calculateAvailableRows(int i, int i2) {
        return i / i2;
    }

    private boolean canItFitInRow(Paint paint, String str, int i) {
        return ((int) paint.measureText(str)) <= i;
    }

    private List<String> rowMaker(Paint paint, String str, int i) {
        String str2;
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(str)) {
            return arrayList;
        }
        if (canItFitInRow(paint, str, i)) {
            arrayList.add(str);
            return arrayList;
        }
        while (true) {
            String str3 = "";
            while (!TextUtils.isEmpty(str)) {
                int iIndexOf = str.indexOf(" ") + 1;
                if (iIndexOf != 0) {
                    str2 = str3 + str.substring(0, iIndexOf);
                } else {
                    str2 = str3 + str;
                }
                if (canItFitInRow(paint, str2, i)) {
                    if (iIndexOf != 0) {
                        str = str.substring(iIndexOf);
                    } else {
                        arrayList.add(str2);
                        str = "";
                    }
                    str3 = str2;
                } else if (!TextUtils.isEmpty(str3)) {
                    arrayList.add(str3);
                    if (iIndexOf == 0) {
                        arrayList.add(str);
                        str = "";
                        str3 = str;
                    }
                } else if (iIndexOf != 0) {
                    str = breakWord(paint, str2, i, arrayList) + str.substring(iIndexOf);
                } else {
                    str = breakWord(paint, str2, i, arrayList);
                }
            }
            return arrayList;
        }
    }

    String breakWord(Paint paint, String str, int i, List<String> list) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        while (canItFitInRow(paint, sb.toString() + str.substring(0, 1), i)) {
            sb.append(str.substring(0, 1));
            str = str.substring(1);
        }
        if (length == str.length()) {
            return "";
        }
        list.add(sb.toString());
        return !canItFitInRow(paint, str, i) ? breakWord(paint, str, i, list) : str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void determineTextViewLines(int i, int i2) {
        String string;
        SpannableString spannableString;
        List<String> listRowMaker = rowMaker(this.mTitleTextView.getPaint(), determineTitle(), i2);
        List<String> listRowMaker2 = rowMaker(this.mTitleTextView.getPaint(), this.eventInfo.eventLocation, i2);
        StringBuilder sb = new StringBuilder();
        buildTextRows(sb, listRowMaker);
        String string2 = sb.toString();
        if (listRowMaker2.isEmpty()) {
            string = "";
        } else {
            sb.setLength(0);
            buildTextRows(sb, listRowMaker2);
            string = sb.toString();
        }
        if (TextUtils.isEmpty(string)) {
            spannableString = new SpannableString(string2 + System.lineSeparator());
        } else {
            spannableString = new SpannableString(string2 + string + System.lineSeparator());
        }
        spannableString.setSpan(new StyleSpan(1), 0, string2.length(), 0);
        this.mTitleTextView.setText(spannableString);
        int size = listRowMaker.size() + listRowMaker2.size();
        if (size <= i) {
            i = size;
        }
        this.mTitleTextView.setLines(i);
    }

    void buildTextRows(StringBuilder sb, List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next() + System.lineSeparator());
        }
    }
}
