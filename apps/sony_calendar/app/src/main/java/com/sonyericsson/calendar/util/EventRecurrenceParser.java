package com.sonyericsson.calendar.util;

import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class EventRecurrenceParser {
    private static final long MILISECONDS_IN_WEEK = 604800000;

    public static long findNewStartTimeInChangedRule(String str, long j, long j2) {
        long j3;
        long dateInMonthly;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(j2);
        EventRecurrence eventRecurrence = new EventRecurrence();
        eventRecurrence.parse(str);
        int i = eventRecurrence.freq;
        if (i != 5) {
            if (i != 6) {
                if (i != 7) {
                    return j;
                }
                calendar.set(2, calendar2.get(2));
                dateInMonthly = setMonthDateInYearly(calendar, calendar2.get(5));
                if (dateInMonthly < j) {
                    calendar.set(1, calendar.get(1) + 1);
                    return calendar.getTimeInMillis();
                }
            } else {
                if (eventRecurrence.bydayNum != null) {
                    return findStartTimeInBydayNumOfMonthly(calendar, calendar2);
                }
                dateInMonthly = setDateInMonthly(calendar, calendar2.get(5));
                if (dateInMonthly < j) {
                    return setMonth(calendar, calendar.get(2) + 1);
                }
            }
            return dateInMonthly;
        }
        if (1 == eventRecurrence.bydayCount || eventRecurrence.repeatsBiweekly()) {
            int i2 = calendar2.get(7) - calendar.get(7);
            if (i2 < 0) {
                i2 += 7;
            }
            j3 = (((long) i2) * MILISECONDS_IN_WEEK) / 7;
        } else {
            boolean z = false;
            boolean z2 = false;
            for (int i3 : eventRecurrence.byday) {
                if (i3 == 4194304) {
                    z = true;
                } else if (i3 == 65536) {
                    z2 = true;
                }
                if (z && z2) {
                    break;
                }
            }
            int i4 = calendar.get(7);
            if (i4 == 1 && !z) {
                j3 = 86400000;
            } else {
                if (i4 != 7 || z2) {
                    return j;
                }
                j3 = 172800000;
            }
        }
        return j + j3;
    }

    private static long setMonth(Calendar calendar, int i) {
        if (12 == i) {
            calendar.set(2, 0);
            calendar.set(1, calendar.get(1) + 1);
        } else {
            calendar.set(2, i);
        }
        return calendar.getTimeInMillis();
    }

    private static long findStartTimeInBydayNumOfMonthly(Calendar calendar, Calendar calendar2) {
        int i = calendar2.get(7) - calendar.get(7);
        if (i < 0) {
            i += 7;
        }
        long timeInMillis = calendar.getTimeInMillis() + ((((long) i) * MILISECONDS_IN_WEEK) / 7);
        Calendar.getInstance().setTimeInMillis(timeInMillis);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTimeInMillis(timeInMillis);
        calendar3.set(5, 1);
        setMonth(calendar3, calendar3.get(2) + 1);
        int iFindOrdinalOfDayOfWeekInMonth = findOrdinalOfDayOfWeekInMonth(timeInMillis);
        int iFindOrdinalOfDayOfWeekInMonth2 = findOrdinalOfDayOfWeekInMonth(calendar2.getTimeInMillis());
        if (iFindOrdinalOfDayOfWeekInMonth2 >= iFindOrdinalOfDayOfWeekInMonth) {
            int iFindOrdinalOfDayOfWeekInMonth3 = iFindOrdinalOfDayOfWeekInMonth2 - iFindOrdinalOfDayOfWeekInMonth;
            while (true) {
                timeInMillis += ((long) iFindOrdinalOfDayOfWeekInMonth3) * MILISECONDS_IN_WEEK;
                if (timeInMillis < calendar3.getTimeInMillis()) {
                    break;
                }
                setMonth(calendar3, calendar3.get(2) + 1);
                iFindOrdinalOfDayOfWeekInMonth3 = iFindOrdinalOfDayOfWeekInMonth2 - findOrdinalOfDayOfWeekInMonth(timeInMillis);
            }
        } else {
            long timeInMillis2 = calendar3.getTimeInMillis();
            int i2 = calendar2.get(7) - calendar3.get(7);
            if (i2 < 0) {
                i2 += 7;
            }
            timeInMillis = timeInMillis2 + ((((long) i2) * MILISECONDS_IN_WEEK) / 7);
            setMonth(calendar3, calendar3.get(2) + 1);
            int iFindOrdinalOfDayOfWeekInMonth4 = findOrdinalOfDayOfWeekInMonth(timeInMillis);
            while (true) {
                timeInMillis += ((long) (iFindOrdinalOfDayOfWeekInMonth2 - iFindOrdinalOfDayOfWeekInMonth4)) * MILISECONDS_IN_WEEK;
                if (timeInMillis < calendar3.getTimeInMillis()) {
                    break;
                }
                setMonth(calendar3, calendar3.get(2) + 1);
                iFindOrdinalOfDayOfWeekInMonth4 = findOrdinalOfDayOfWeekInMonth(timeInMillis);
            }
        }
        return timeInMillis;
    }

    private static long setMonthDateInYearly(Calendar calendar, int i) {
        while (i > calendar.getActualMaximum(5)) {
            calendar.set(1, calendar.get(1) + 1);
        }
        calendar.set(5, i);
        return calendar.getTimeInMillis();
    }

    private static long setDateInMonthly(Calendar calendar, int i) {
        if (i > calendar.getActualMaximum(5)) {
            setMonth(calendar, calendar.get(2) + 1);
        }
        calendar.set(5, i);
        return calendar.getTimeInMillis();
    }

    private static int findOrdinalOfDayOfWeekInMonth(long j) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        calendar2.setTimeInMillis(j);
        calendar2.set(5, 1);
        long timeInMillis = calendar2.getTimeInMillis();
        int i = calendar.get(7) - calendar2.get(7);
        if (i < 0) {
            i += 7;
        }
        return ((int) ((calendar.getTimeInMillis() - (timeInMillis + ((((long) i) * MILISECONDS_IN_WEEK) / 7))) / MILISECONDS_IN_WEEK)) + 1;
    }
}
