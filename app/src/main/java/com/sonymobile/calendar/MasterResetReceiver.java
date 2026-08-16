package com.sonymobile.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class MasterResetReceiver extends BroadcastReceiver {
    private static final String MASTER_RESET = "com.sonyericsson.settings.MASTERRESET";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null || !action.equals(MASTER_RESET)) {
            return;
        }
        Utils.setTimeZone(context, LunarContract.CalendarCache.TIMEZONE_TYPE_AUTO);
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(context).edit();
        editorEdit.clear();
        editorEdit.commit();
    }
}
