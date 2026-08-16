package com.sonymobile.calendar.alerts;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.text.TextUtils;
import androidx.core.app.NotificationManagerCompat;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class DismissAlarmsService extends IntentService {
    private static final String TIME_CONTENT_URI = "content://com.android.calendar/time/";

    @Override // android.app.IntentService, android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public DismissAlarmsService() {
        super("DismissAlarmsService");
    }

    @Override // android.app.IntentService
    public void onHandleIntent(Intent intent) {
        Uri uri;
        String str;
        long longExtra = intent.getLongExtra(AlertUtils.EVENT_ID_KEY, -1L);
        intent.getLongExtra(AlertUtils.EVENT_START_KEY, -1L);
        intent.getLongExtra(AlertUtils.EVENT_END_KEY, -1L);
        intent.getBooleanExtra(AlertUtils.SHOW_EVENT_KEY, false);
        long[] longArrayExtra = intent.getLongArrayExtra(AlertUtils.EVENT_IDS_KEY);
        boolean[] booleanArrayExtra = intent.getBooleanArrayExtra(AlertUtils.IS_LUNAR_EVENTS);
        int intExtra = intent.getIntExtra("notificationid", -1);
        boolean booleanExtra = intent.getBooleanExtra(AlertUtils.IS_LUNAR_EVENT, false);
        intent.getLongExtra("_id", -1L);
        intent.getLongExtra(AlertUtils.FIRST_EVENT_START_TIME, System.currentTimeMillis());
        boolean z = longArrayExtra != null && longArrayExtra.length > 0 && booleanArrayExtra != null && booleanArrayExtra.length > 0;
        if (PermissionUtils.isCalendarGranted(this)) {
            if (booleanExtra) {
                uri = LunarContract.CalendarAlerts.CONTENT_URI;
            } else {
                uri = CalendarContract.CalendarAlerts.CONTENT_URI;
            }
            String str2 = "";
            if (longExtra != -1) {
                str = "state=1 AND event_id=" + longExtra;
            } else if (z) {
                String[] strArr = new String[2];
                buildMultipleEventsQuery(strArr, longArrayExtra, booleanArrayExtra);
                String str3 = strArr[0];
                str2 = strArr[1];
                str = str3;
            } else {
                str = "state=1";
            }
            ContentResolver contentResolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put("state", (Integer) 2);
            if (z) {
                if (!TextUtils.isEmpty(str)) {
                    contentResolver.update(CalendarContract.CalendarAlerts.CONTENT_URI, contentValues, str, null);
                }
                if (LunarAvailabilityManager.isLunarAvailable(this) && !TextUtils.isEmpty(str2)) {
                    contentResolver.update(LunarContract.CalendarAlerts.CONTENT_URI, contentValues, str2, null);
                }
            } else {
                contentResolver.update(uri, contentValues, str, null);
            }
        }
        if (intExtra != -1) {
            NotificationManagerCompat.from(this).cancel(intExtra);
        }
        stopSelf();
    }

    private void buildMultipleEventsQuery(String[] strArr, long[] jArr, boolean[] zArr) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append("state");
        sb.append("=");
        sb.append(1);
        sb.append(" AND (");
        sb2.append("state");
        sb2.append("=");
        sb2.append(1);
        sb2.append(" AND (");
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < zArr.length; i3++) {
            if (zArr[i3]) {
                i2++;
                sb2.append("event_id");
                sb2.append("=");
                sb2.append(jArr[i3]);
                sb2.append(" OR ");
            } else {
                i++;
                sb.append("event_id");
                sb.append("=");
                sb.append(jArr[i3]);
                sb.append(" OR ");
            }
        }
        if (i > 0) {
            sb.delete(sb.length() - 4, sb.length());
            sb.append(")");
            strArr[0] = sb.toString();
        } else {
            strArr[0] = "";
        }
        if (i2 > 0) {
            sb2.delete(sb2.length() - 4, sb2.length());
            sb2.append(")");
            strArr[1] = sb2.toString();
            return;
        }
        strArr[1] = "";
    }
}
