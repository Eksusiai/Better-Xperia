package com.sonymobile.calendar;

import android.text.format.Time;

/* JADX INFO: loaded from: classes2.dex */
public interface ICalendarColumnContainer {
    void blockRelayout();

    void invalidate();

    void reloadEvents();

    void removeAddEventView();

    boolean setHourHeight(int i);

    void setViewPortSize(int i, int i2, boolean z);

    void updateFocusability(boolean z);

    void updateView(Time[] timeArr, boolean z);
}
