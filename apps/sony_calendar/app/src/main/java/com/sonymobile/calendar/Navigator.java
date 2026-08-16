package com.sonymobile.calendar;

import android.text.format.Time;

/* JADX INFO: loaded from: classes2.dex */
public interface Navigator {
    CharSequence getDateString();

    long getSelectedTimeInMillis();

    void goTo(Time time, boolean z);

    void goToNext(float f);

    void goToPrevious(float f);

    void goToToday();

    void updateActionBar(Time time);
}
