package com.sonymobile.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sonyericsson.calendar.util.DatabaseUtils;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarBootBroadcastReceiver extends BroadcastReceiver {
    static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BOOT_COMPLETED)) {
            DatabaseUtils.createCalendarInDatabaseIfNeeded(context);
        }
    }
}
