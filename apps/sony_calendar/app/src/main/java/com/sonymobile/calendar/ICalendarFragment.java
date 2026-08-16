package com.sonymobile.calendar;

import android.text.format.Time;

/* JADX INFO: loaded from: classes2.dex */
public interface ICalendarFragment extends CalendarSwipeListener, Navigator {
    void drawingCompleted();

    int[] getCalendarDimensions();

    int[] getCalendarLeftTop();

    void onCalendarClicked(Time time, boolean z);

    void onTransitionComplete();
}
