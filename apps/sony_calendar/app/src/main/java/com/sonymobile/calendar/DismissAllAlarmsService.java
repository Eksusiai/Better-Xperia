package com.sonymobile.calendar;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CalendarContract;

/* JADX INFO: loaded from: classes2.dex */
public class DismissAllAlarmsService extends IntentService {
    private static final int COLUMN_INDEX_STATE = 0;
    private static final String[] PROJECTION = {"state"};

    @Override // android.app.IntentService, android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public DismissAllAlarmsService() {
        super("DismissAllAlarmsService");
    }

    @Override // android.app.IntentService
    public void onHandleIntent(Intent intent) {
        Uri uri = CalendarContract.CalendarAlerts.CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROJECTION[0], (Integer) 2);
        contentResolver.update(uri, contentValues, "state=1", null);
        stopSelf();
    }
}
