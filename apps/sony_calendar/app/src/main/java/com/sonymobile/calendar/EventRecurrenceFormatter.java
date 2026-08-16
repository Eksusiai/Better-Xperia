package com.sonymobile.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import com.sonyericsson.calendar.util.EventRecurrence;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class EventRecurrenceFormatter {
    public static String getRepeatString(Context context, EventRecurrence eventRecurrence) {
        Resources resources = context.getResources();
        int i = eventRecurrence.freq;
        if (i == 4) {
            return resources.getString(R.string.daily);
        }
        if (i == 5) {
            int[] dayIndices = WeekUtils.getDayIndices(context);
            String[] sharedPreference = Utils.getSharedPreference(context, GeneralPreferences.KEY_WEEKEND_DAYS, context.getResources().getStringArray(R.array.preferences_weekend_default));
            Calendar calendar = Calendar.getInstance();
            if (eventRecurrence.repeatsOnEveryWeekDay(dayIndices)) {
                calendar.set(7, dayIndices[0] + 1);
                String displayName = calendar.getDisplayName(7, 1, Locale.getDefault());
                calendar.set(7, dayIndices[dayIndices.length - 1] + 1);
                return String.format(resources.getString(R.string.every_weekday), displayName, calendar.getDisplayName(7, 1, Locale.getDefault()));
            }
            if (eventRecurrence.repeatsOnEveryWeekEnd(sharedPreference)) {
                Arrays.sort(sharedPreference, new Comparator<String>() { // from class: com.sonymobile.calendar.EventRecurrenceFormatter.1
                    @Override // java.util.Comparator
                    public int compare(String str, String str2) {
                        return Integer.parseInt(str) - Integer.parseInt(str2);
                    }
                });
                calendar.set(7, Integer.parseInt(sharedPreference[0]) + 1);
                String displayName2 = calendar.getDisplayName(7, 1, Locale.getDefault());
                calendar.set(7, Integer.parseInt(sharedPreference[sharedPreference.length - 1]) + 1);
                return String.format(resources.getString(R.string.clr_strings_every_weekend_txt), displayName2, calendar.getDisplayName(7, 1, Locale.getDefault()));
            }
            if (eventRecurrence.repeatsBiweekly()) {
                String string = resources.getString(R.string.biweekly);
                StringBuilder sb = new StringBuilder();
                int i2 = eventRecurrence.bydayCount - 1;
                if (i2 >= 0) {
                    for (int i3 = 0; i3 < i2; i3++) {
                        sb.append(dayToString(eventRecurrence.byday[i3]));
                        sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
                    }
                    sb.append(dayToString(eventRecurrence.byday[i2]));
                    return String.format(string, sb.toString());
                }
            } else {
                String string2 = resources.getString(R.string.weekly);
                StringBuilder sb2 = new StringBuilder();
                int i4 = eventRecurrence.bydayCount - 1;
                if (i4 >= 0) {
                    for (int i5 = 0; i5 < i4; i5++) {
                        sb2.append(dayToString(eventRecurrence.byday[i5]));
                        sb2.append(RecurrenceRuleParser.VALUE_SEPARATOR);
                    }
                    sb2.append(dayToString(eventRecurrence.byday[i4]));
                    return String.format(string2, sb2.toString());
                }
                if (eventRecurrence.startDate == null) {
                    return null;
                }
                return String.format(string2, dayToString(EventRecurrence.timeDay2Day(eventRecurrence.startDate.weekDay)));
            }
        } else if (i != 6) {
            if (i != 7) {
                return null;
            }
            return resources.getString(R.string.yearly_plain);
        }
        return resources.getString(R.string.monthly);
    }

    private static String dayToString(int i) {
        return DateUtils.getDayOfWeekString(dayToUtilDay(i), 10);
    }

    private static int dayToUtilDay(int i) {
        if (i == 65536) {
            return 1;
        }
        if (i == 131072) {
            return 2;
        }
        if (i == 262144) {
            return 3;
        }
        if (i == 524288) {
            return 4;
        }
        if (i == 1048576) {
            return 5;
        }
        if (i == 2097152) {
            return 6;
        }
        if (i == 4194304) {
            return 7;
        }
        throw new IllegalArgumentException("bad day argument: " + i);
    }
}
