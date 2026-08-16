package com.sonymobile.calendar;

import android.text.format.Time;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public final class TimeUtils {
    private TimeUtils() {
    }

    public static long toMillis(Time time, boolean ignoreDst) {
        Calendar calendar = Calendar.getInstance(getTimeZone(time));
        calendar.clear();
        calendar.set(time.year, time.month, time.monthDay, time.hour, time.minute, time.second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static void setMillis(Time time, long millis) {
        Calendar calendar = Calendar.getInstance(getTimeZone(time));
        calendar.clear();
        calendar.setTimeInMillis(millis);
        time.second = calendar.get(Calendar.SECOND);
        time.minute = calendar.get(Calendar.MINUTE);
        time.hour = calendar.get(Calendar.HOUR_OF_DAY);
        time.monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        time.month = calendar.get(Calendar.MONTH);
        time.year = calendar.get(Calendar.YEAR);
        time.isDst = calendar.get(Calendar.DST_OFFSET) != 0 ? 1 : 0;
        time.gmtoff = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / 1000;
        time.weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        time.yearDay = calendar.get(Calendar.DAY_OF_YEAR) - 1;
    }

    public static void normalize(Time time, boolean ignoreDst) {
        Calendar calendar = Calendar.getInstance(getTimeZone(time));
        calendar.clear();
        calendar.set(time.year, time.month, time.monthDay, time.hour, time.minute, time.second);
        calendar.set(Calendar.MILLISECOND, 0);
        time.second = calendar.get(Calendar.SECOND);
        time.minute = calendar.get(Calendar.MINUTE);
        time.hour = calendar.get(Calendar.HOUR_OF_DAY);
        time.monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        time.month = calendar.get(Calendar.MONTH);
        time.year = calendar.get(Calendar.YEAR);
        time.isDst = calendar.get(Calendar.DST_OFFSET) != 0 ? 1 : 0;
        time.gmtoff = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / 1000;
        time.weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        time.yearDay = calendar.get(Calendar.DAY_OF_YEAR) - 1;
    }

    private static TimeZone getTimeZone(Time time) {
        try {
            if (time.timezone != null && time.timezone.length() > 0) {
                return TimeZone.getTimeZone(time.timezone);
            }
        } catch (Exception unused) {
        }
        return TimeZone.getDefault();
    }
}