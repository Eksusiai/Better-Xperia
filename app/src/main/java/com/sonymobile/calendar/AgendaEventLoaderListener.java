package com.sonymobile.calendar;

import com.sonyericsson.calendar.util.EventInfo;

/* JADX INFO: loaded from: classes2.dex */
public interface AgendaEventLoaderListener {
    void onEventSelected(EventInfo eventInfo);

    void onEventsFound();

    void onNoEventsFound();

    void onPositionSelected(int i, boolean z);
}
