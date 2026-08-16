package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class WeekUtils {
    public static Time[] getDays(Context context, Time time, boolean z) {
        int[] dayIndices = getDayIndices(context, z, true);
        int i = 0;
        Time firstDisplayedDay = getFirstDisplayedDay(context, time, dayIndices[0], z);
        int length = dayIndices.length;
        Time[] timeArr = new Time[length];
        int i2 = dayIndices[0];
        while (i < length) {
            Time time2 = new SafeTime(firstDisplayedDay);
            time2.monthDay += ((dayIndices[i] - i2) + 7) % 7;
            time2.normalize(true);
            timeArr[i] = time2;
            i2 = dayIndices[i];
            i++;
            firstDisplayedDay = time2;
        }
        return timeArr;
    }

    public static int getDayCount(Context context) {
        if (Utils.getSharedPreference(context, GeneralPreferences.KEY_DAYS_IN_WEEK, "7").equals("7")) {
            return 7;
        }
        return 7 - Utils.getSharedPreference(context, GeneralPreferences.KEY_WEEKEND_DAYS, context.getResources().getStringArray(R.array.preferences_weekend_default)).length;
    }

    public static Time getNextViewTime(Time time, boolean z) {
        Time time2 = new SafeTime(time);
        time2.monthDay += z ? 7 : -7;
        time2.normalize(true);
        return time2;
    }

    private static int[] getDayIndices(Context context, boolean z, boolean z2) {
        int firstDayOfWeek = Utils.getFirstDayOfWeek(context);
        int[] iArr = new int[7];
        for (int i = 0; i < 7; i++) {
            iArr[i] = firstDayOfWeek;
            firstDayOfWeek = (firstDayOfWeek + 1) % 7;
        }
        if (z || (Utils.getSharedPreference(context, GeneralPreferences.KEY_DAYS_IN_WEEK, "7").equals("7") && z2)) {
            return iArr;
        }
        String[] sharedPreference = Utils.getSharedPreference(context, GeneralPreferences.KEY_WEEKEND_DAYS, context.getResources().getStringArray(R.array.preferences_weekend_default));
        int[] iArr2 = new int[7 - sharedPreference.length];
        int i2 = 0;
        for (int i3 = 0; i3 < 7; i3++) {
            if (!exists(sharedPreference, iArr[i3])) {
                iArr2[i2] = iArr[i3];
                i2++;
            }
        }
        return iArr2;
    }

    public static int[] getDayIndices(Context context) {
        return getDayIndices(context, false, false);
    }

    private static boolean exists(String[] strArr, int i) {
        for (String str : strArr) {
            if (Integer.parseInt(str) == i) {
                return true;
            }
        }
        return false;
    }

    private static Time getFirstDisplayedDay(Context context, Time time, int i, boolean z) {
        int i2;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar.setTimeInMillis(time.toMillis(false));
        int firstDayOfWeek = Utils.getFirstDayOfWeek(context);
        if (firstDayOfWeek == 0) {
            i2 = time.weekDay;
        } else if (firstDayOfWeek == 6) {
            if (Utils.getSharedPreference(context, GeneralPreferences.KEY_DAYS_IN_WEEK, "7").equals("7") || z) {
                i2 = (time.weekDay + 1) % 7;
            } else {
                i2 = time.weekDay % 7;
            }
        } else {
            i2 = (time.weekDay + 6) % 7;
        }
        calendar.add(6, -i2);
        calendar.set(7, i + 1);
        Time time2 = new SafeTime(time.timezone);
        time2.set(calendar.getTimeInMillis());
        return time2;
    }
}
