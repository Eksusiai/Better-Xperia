package com.sonymobile.calendar.tasks.utils;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class TimeFormatterUtil {
    public static final String FORMAT_WEEKDAY = "cccc";
    public static final String FORMAT_WEEKDAY_ABBREV = "c";
    public static final int NO_DUE_DATE = -4;
    public static final int WEEK_FRIDAY = 5;
    public static final int WEEK_LATER = 7;
    public static final int WEEK_MONDAY = 1;
    public static final int WEEK_PAST = -3;
    public static final int WEEK_SATURDAY = 6;
    public static final int WEEK_SUNDAY = 0;
    public static final int WEEK_THURSDAY = 4;
    public static final int WEEK_TODAY = -2;
    public static final int WEEK_TOMORROW = -1;
    public static final int WEEK_TUESDAY = 2;
    public static final int WEEK_WEDNESDAY = 3;

    public static int compareWithThisWeek(long j) {
        if (j == 0) {
            return 2;
        }
        Time time = new SafeTime();
        time.set(j);
        int julianDay = Time.getJulianDay(j, time.gmtoff);
        int i = time.weekDay;
        long jCurrentTimeMillis = System.currentTimeMillis();
        time.set(jCurrentTimeMillis);
        int julianDay2 = Time.getJulianDay(jCurrentTimeMillis, time.gmtoff);
        int i2 = time.weekDay;
        if (julianDay < julianDay2) {
            return -1;
        }
        return (julianDay - julianDay2 <= 6 && i - i2 > 0) ? 0 : 1;
    }

    public static long transTimeZone(long j, TimeZone timeZone, TimeZone timeZone2) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.setTimeInMillis(j);
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar(timeZone2);
        gregorianCalendar2.set(gregorianCalendar.get(1), gregorianCalendar.get(2), gregorianCalendar.get(5), gregorianCalendar.get(10), gregorianCalendar.get(12), gregorianCalendar.get(13));
        gregorianCalendar2.set(14, 0);
        return gregorianCalendar2.getTimeInMillis();
    }

    public static long transDateTime2Date(long j, TimeZone timeZone) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.setTimeInMillis(j);
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar(timeZone);
        gregorianCalendar2.set(gregorianCalendar.get(1), gregorianCalendar.get(2), gregorianCalendar.get(5));
        gregorianCalendar2.set(11, 0);
        gregorianCalendar2.set(12, 0);
        gregorianCalendar2.set(13, 0);
        gregorianCalendar2.set(14, 0);
        return gregorianCalendar2.getTimeInMillis();
    }

    public static String getWeekDay(Context context, long j, boolean z) {
        return new SimpleDateFormat(z ? "c" : "cccc", Locale.getDefault()).format(new Date(j));
    }

    public static String getFormattedDate(Context context, long j, boolean z) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(j);
        return getWeekDay(context, j, z) + " " + DateFormat.getDateFormat(context).format(calendar.getTime());
    }

    public static String getFormattedTime(Context context, long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        return DateFormat.getTimeFormat(context).format(calendar.getTime());
    }

    public static int getDayInCurrentWeek(long j) {
        if (j == 0) {
            return -4;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        Time time = new SafeTime();
        time.set(jCurrentTimeMillis);
        int julianDay = Time.getJulianDay(jCurrentTimeMillis, time.gmtoff);
        time.set(j);
        int julianDay2 = Time.getJulianDay(j, time.gmtoff) - julianDay;
        if (julianDay2 < 0) {
            return -3;
        }
        if (julianDay2 > 6) {
            return 7;
        }
        if (julianDay2 == 0) {
            return -2;
        }
        if (julianDay2 != 1) {
            return time.weekDay;
        }
        return -1;
    }
}
