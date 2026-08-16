package com.sonymobile.calendar;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.Holiday;

/* JADX INFO: loaded from: classes2.dex */
public class HolidayDayView extends AllDayEventViewBase {
    private Holiday mHoliday;
    private EventInfo[] mHolidayEvent;
    private String mHolidayName;

    public HolidayDayView(Context context, Holiday holiday, Time time, boolean z, ViewIndex viewIndex) {
        super(context, time, z, viewIndex);
        this.mHoliday = holiday;
        this.mHolidayName = holiday.description;
        initLayout(-1);
        makeEventInfo();
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initIcon() {
        ((ImageView) findViewById(R.id.event_icon)).setVisibility(8);
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initContentDescription() {
        setContentDescription(this.mHolidayName);
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected String getTitleText() {
        return this.mHolidayName;
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected Object getNavigationInfo() {
        return this.mHolidayEvent;
    }

    private void makeEventInfo() {
        this.mHolidayEvent = new EventInfo[]{new EventInfo()};
        this.mHolidayEvent[0].instanceId = -1L;
        this.mHolidayEvent[0].title = this.mHolidayName;
        this.mHolidayEvent[0].eventLocation = "";
        this.mHolidayEvent[0].allDay = 1;
        this.mHolidayEvent[0].hasAlarm = 0;
        this.mHolidayEvent[0].color = 0;
        this.mHolidayEvent[0].rrule = "FREQ=YEARLY;";
        this.mHolidayEvent[0].begin = Utils.convertDayToMidnight(this.day).toMillis(true);
        this.mHolidayEvent[0].localBegin = Utils.convertDayToMidnight(this.day).toMillis(true);
        this.mHolidayEvent[0].id = 0L;
        this.mHolidayEvent[0].startDay = this.mHoliday.getStartDate();
        this.mHolidayEvent[0].endDay = this.mHoliday.getEndDate();
        this.mHolidayEvent[0].selfAttendeeStatus = 0;
        this.mHolidayEvent[0].calendarId = 1;
        this.mHolidayEvent[0].guestCanModify = 0;
        this.mHolidayEvent[0].visibility = 1;
        this.mHolidayEvent[0].accessLevel = 0;
        this.mHolidayEvent[0].ownerAccount = "";
        this.mHolidayEvent[0].eventOrganizer = "";
        this.mHolidayEvent[0].hasEventColor = false;
        this.mHolidayEvent[0].isLunarEvent = false;
        this.mHolidayEvent[0].description = this.mHolidayName;
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initTextView(boolean z) {
        this.mTitleView = (TextView) findViewById(R.id.event_title);
        this.mTitleView.setText(getTitleText());
        this.mTitleView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        this.mTitleView.setEllipsize(TextUtils.TruncateAt.END);
        setTitleSingleLine(true);
    }
}
