package com.sonyericsson.calendar.util;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.text.format.Time;
import com.sonymobile.calendar.Utils;

/* JADX INFO: loaded from: classes.dex */
public class DaySpan {
    public int endJulianDay;
    public int startJulianDay;
    public String timezone;

    public DaySpan(Time time, Time time2) {
        String str = time.timezone;
        this.timezone = str;
        this.startJulianDay = getJulianDayFromTime(time, str);
        this.endJulianDay = getJulianDayFromTime(time2, this.timezone);
    }

    public DaySpan(Context context, Time time, Time time2) {
        String timeZone = Utils.getTimeZone(context, null);
        this.timezone = timeZone;
        this.startJulianDay = getJulianDayFromTime(time, timeZone);
        this.endJulianDay = getJulianDayFromTime(time2, this.timezone);
    }

    private int getJulianDayFromTime(Time time, String str) {
        Time time2 = new SafeTime(str);
        time2.set(time.monthDay, time.month, time.year);
        return Time.getJulianDay(time2.normalize(true), time2.gmtoff);
    }

    public boolean contains(DaySpan daySpan) {
        return this.startJulianDay <= daySpan.startJulianDay && this.endJulianDay >= daySpan.endJulianDay;
    }
}
