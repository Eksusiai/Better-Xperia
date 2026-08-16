package com.sonymobile.calendar.alerts;
import com.sonymobile.calendar.SafeTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import com.sonymobile.calendar.EventInfoActivity;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class AlertUtils {
    public static final String EVENT_END_KEY = "eventend";
    public static final String EVENT_IDS_KEY = "eventids";
    public static final String EVENT_ID_KEY = "eventid";
    public static final String EVENT_START_KEY = "eventstart";
    public static final int EXPIRED_GROUP_NOTIFICATION_ID = 0;
    public static final String FIRST_EVENT_START_TIME = "FIRST_EVENT_START_TIME";
    public static final String IS_LUNAR_EVENT = "isLunarEvent";
    public static final String IS_LUNAR_EVENTS = "isLunarEvents";
    public static final String NOTIFICATION_ID_KEY = "notificationid";
    public static final String SHOW_EVENT_KEY = "showevent";

    public static void scheduleAlarm(Context context, AlarmManager alarmManager, long j, boolean z) {
        scheduleAlarmHelper(context, alarmManager, j, false, z);
    }

    static void scheduleNextNotificationRefresh(Context context, AlarmManager alarmManager, long j) {
        scheduleAlarmHelper(context, alarmManager, j, true, false);
    }

    private static void scheduleAlarmHelper(Context context, AlarmManager alarmManager, long j, boolean z, boolean z2) {
        Uri uri;
        int i;
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService("alarm");
        }
        Intent intent = new Intent(LunarContract.ACTION_EVENT_REMINDER);
        intent.setClass(context, AlertReceiver.class);
        if (z) {
            i = 1;
        } else {
            if (z2) {
                uri = LunarContract.CalendarAlerts.CONTENT_URI;
            } else {
                uri = CalendarContract.CalendarAlerts.CONTENT_URI;
            }
            Uri.Builder builderBuildUpon = uri.buildUpon();
            ContentUris.appendId(builderBuildUpon, j);
            intent.setData(builderBuildUpon.build());
            i = 0;
        }
        intent.putExtra("alarmTime", j);
        Utils.setAlarm(alarmManager, i, j, PendingIntent.getBroadcast(context, 0, intent, 201326592));
    }

    static String formatTimeLocation(Context context, long j, long j2, boolean z, String str) {
        int i;
        String timeZone = Utils.getTimeZone(context, null);
        Time time = new SafeTime(timeZone);
        time.setToNow();
        int julianDay = Time.getJulianDay(time.toMillis(false), time.gmtoff);
        time.set(j);
        int julianDay2 = Time.getJulianDay(time.toMillis(false), time.gmtoff);
        if (z) {
            i = 8192;
        } else {
            i = DateFormat.is24HourFormat(context) ? 129 : 1;
        }
        if (julianDay2 < julianDay || julianDay2 > julianDay + 1) {
            i |= 16;
        }
        int i2 = i;
        StringBuilder sb = new StringBuilder(Utils.formatDateRange(context, j, j, i2));
        if (!z && timeZone != Time.getCurrentTimezone()) {
            time.set(j);
            sb.append(" ").append(TimeZone.getTimeZone(timeZone).getDisplayName(time.isDst != 0, 0, Locale.getDefault()));
        }
        if (julianDay2 == julianDay + 1) {
            sb.append(", ");
            sb.append(context.getString(R.string.tomorrow));
        }
        if (str != null && !TextUtils.isEmpty(str.trim())) {
            sb.append(" ").append(context.getString(R.string.label_at_location)).append(" ");
            sb.append(str.trim());
        } else {
            sb.append(" ").append(context.getString(R.string.label_dash)).append(" ");
            sb.append(Utils.formatDateRange(context, j2, j2, i2));
        }
        return sb.toString();
    }

    public static ContentValues makeContentValues(long j, long j2, long j3, long j4, int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("event_id", Long.valueOf(j));
        contentValues.put("begin", Long.valueOf(j2));
        contentValues.put("end", Long.valueOf(j3));
        contentValues.put("alarmTime", Long.valueOf(j4));
        contentValues.put("creationTime", Long.valueOf(System.currentTimeMillis()));
        contentValues.put("receivedTime", (Integer) 0);
        contentValues.put("notifyTime", (Integer) 0);
        contentValues.put("state", (Integer) 0);
        contentValues.put("minutes", Integer.valueOf(i));
        return contentValues;
    }

    public static Intent buildEventViewIntent(Context context, long j, long j2, long j3, boolean z, long j4) {
        Uri uri;
        Intent intent = new Intent("android.intent.action.VIEW");
        if (z) {
            uri = LunarContract.CONTENT_URI;
        } else {
            uri = CalendarContract.CONTENT_URI;
        }
        Uri.Builder builderBuildUpon = uri.buildUpon();
        builderBuildUpon.appendEncodedPath("events/" + j);
        intent.setData(builderBuildUpon.build());
        if (context.getResources().getBoolean(R.bool.tablet_mode)) {
            intent.putExtra(Utils.INTENT_KEY_DETAIL_VIEW, true);
            intent.setFlags(131072);
            intent.setClass(context, LaunchActivity.class);
        } else {
            intent.setClass(context, EventInfoActivity.class);
        }
        intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, j2);
        intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, j3);
        intent.putExtra("_id", j4);
        return intent;
    }
}
