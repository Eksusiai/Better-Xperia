package com.sonymobile.calendar;

import android.content.Context;
import android.widget.LinearLayout;
import com.sonyericsson.calendar.util.EventInfo;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarEventWeekView extends CalendarEventView {
    public CalendarEventWeekView(Context context, EventInfo eventInfo, CalendarEventNavigator calendarEventNavigator, int i, int i2) {
        super(context, eventInfo, calendarEventNavigator, i, i2);
    }

    @Override // com.sonymobile.calendar.CalendarEventView
    protected int getLayoutResource() {
        return this.eventInfo.isAlarmEvent ? R.layout.calendar_week_event_alarm_item : R.layout.calendar_week_event_item;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        scaleEventTextSize(this.mTitleTextView);
        if (this.eventInfo.isAlarmEvent) {
            scaleAlarmImageView(i2, i);
        }
        if (i < getResources().getDimension(R.dimen.week_view_min_width)) {
            this.mTitleTextView.setVisibility(4);
            return;
        }
        this.mTitleTextView.setVisibility(0);
        this.mTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(i, i2));
        this.mTitleTextView.setLines(((i2 - this.mTitleTextView.getCompoundPaddingTop()) - this.mTitleTextView.getCompoundPaddingBottom()) / this.mTitleTextView.getLineHeight());
        if (i3 == 0) {
            this.mTitleTextView.post(new Runnable() { // from class: com.sonymobile.calendar.CalendarEventWeekView.1
                @Override // java.lang.Runnable
                public void run() {
                    CalendarEventWeekView.this.mTitleTextView.requestLayout();
                }
            });
        }
    }

    @Override // com.sonymobile.calendar.CalendarEventView
    protected String determineTitle() {
        if (this.eventInfo.isAlarmEvent && !Utils.isTabletDevice(getContext())) {
            return this.eventInfo.title;
        }
        return super.determineTitle();
    }
}
