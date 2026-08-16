package com.sonymobile.calendar;

import android.view.MotionEvent;

/* JADX INFO: loaded from: classes2.dex */
public class BindSVManager {
    private static CalendarScrollView calendarSVA;
    private static CalendarScrollView calendarSVB;
    private static long curTime;
    private static int lastSource;
    private static long lastTime;

    public static void dispatch(MotionEvent motionEvent, int i) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        curTime = jCurrentTimeMillis;
        if (lastSource == i || jCurrentTimeMillis - lastTime >= 100) {
            calendarSVA.callTouch(motionEvent);
            calendarSVB.callTouch(motionEvent);
            lastSource = i;
            lastTime = curTime;
        }
    }

    public static void setCalendarSVA(CalendarScrollView calendarScrollView) {
        calendarSVA = calendarScrollView;
    }

    public static void setCalendarSVB(CalendarScrollView calendarScrollView) {
        calendarSVB = calendarScrollView;
    }
}
