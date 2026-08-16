package com.sonymobile.accuweather;

import android.text.format.Time;
import com.sonyericsson.calendar.util.CalendarConstants;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class Utils {
    public static int getHourOfDay(long j, double d) {
        if (j < 0) {
            return -1;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone(getTimeZone(d)));
        gregorianCalendar.setTimeInMillis(j);
        return gregorianCalendar.get(11);
    }

    private static String getTimeZone(double d) {
        if (d == -2.147483648E9d) {
            d = 0.0d;
        }
        int i = (int) (d * 60.0d);
        int i2 = i / 60;
        int i3 = i % 60;
        String str = i3 == 0 ? ":00" : CalendarConstants.COLON + i3;
        String str2 = i2 < 0 ? "" : "+";
        StringBuilder sb = new StringBuilder();
        sb.append("GMT").append(str2).append("" + i2).append(str);
        return sb.toString();
    }

    public static boolean areDatesEqual(Time time, Time time2) {
        return time.year == time2.year && time.month == time2.month && time.monthDay == time2.monthDay;
    }
}
