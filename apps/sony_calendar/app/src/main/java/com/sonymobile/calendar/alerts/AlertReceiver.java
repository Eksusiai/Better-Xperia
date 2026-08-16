package com.sonymobile.calendar.alerts;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.CalendarContract;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.GaUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import com.sonyericsson.calendar.util.DatabaseUtils;
import com.sonymobile.calendar.EventInfoFragment;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.jobs.ProviderChangeJobService;
import com.sonymobile.calendar.utils.NotificationUtils;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class AlertReceiver extends BroadcastReceiver {
    public static final String ACTION_CLICK = "com.sonymobile.calendar.CLICK";
    public static final String ACTION_DELETE = "com.sonymobile.calendar.DELETE";
    public static final String ACTION_DISMISS_OLD_REMINDERS = "removeOldReminders";
    private static final String ACTION_MAIL = "com.sonymobile.calendar.MAIL";
    private static final int ATTENDEES_INDEX_EMAIL = 0;
    private static final int ATTENDEES_INDEX_STATUS = 1;
    private static final String ATTENDEES_SORT_ORDER = "attendeeName ASC, attendeeEmail ASC";
    private static final String ATTENDEES_WHERE = "event_id=?";
    private static final int EVENT_INDEX_ACCOUNT_NAME = 1;
    private static final int EVENT_INDEX_OWNER_ACCOUNT = 0;
    private static final int EVENT_INDEX_TITLE = 2;
    private static final String EXTRA_EVENT_ID = "eventid";
    private static final int NOTIFICATION_DIGEST_MAX_LENGTH = 3;
    private static final String TAG = "AlertReceiver";
    private static final String TIME_CONTENT_URI = "content://com.android.calendar/time/";
    static PowerManager.WakeLock mStartingService;
    static final Object mStartingServiceSync = new Object();
    private static final Pattern mBlankLinePattern = Pattern.compile("^\\s*$[\n\r]", 8);
    private static final String[] ATTENDEES_PROJECTION = {LunarContract.AttendeesColumns.ATTENDEE_EMAIL, LunarContract.AttendeesColumns.ATTENDEE_STATUS};
    private static final String[] EVENT_PROJECTION = {"ownerAccount", "account_name", LunarContract.EventsColumns.TITLE};

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (ACTION_MAIL.equals(intent.getAction())) {
            context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            long longExtra = intent.getLongExtra("eventid", -1L);
            if (longExtra != -1) {
                Intent intent2 = new Intent(context, (Class<?>) QuickResponseActivity.class);
                intent2.putExtra(EventInfoFragment.FORWARD_EVENT_ID, longExtra);
                intent2.addFlags(268435456);
                context.startActivity(intent2);
                return;
            }
            return;
        }
        if (PermissionUtils.isCalendarGranted(context)) {
            Intent intent3 = new Intent();
            intent3.setClass(context, AlertWork.class);
            intent3.putExtras(intent);
            intent3.putExtra("action", intent.getAction());
            Uri data = intent.getData();
            if (data != null) {
                intent3.putExtra("uri", data.toString());
            }
            beginStartingService(context, intent3);
        }
    }

    public static void beginStartingService(Context context, Intent intent) {
        synchronized (mStartingServiceSync) {
            if (mStartingService == null) {
                PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "StartingAlertWork");
                mStartingService = wakeLockNewWakeLock;
                wakeLockNewWakeLock.setReferenceCounted(false);
            }
            mStartingService.acquire();
            try {
                WorkManager.getInstance(context).enqueue(new OneTimeWorkRequest.Builder(AlertWork.class).setInputData(new Data.Builder().putInt("JobId", 1).putString("action", intent.getStringExtra("action")).putString("uri", intent.getStringExtra("uri")).build()).build());
                if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                    ProviderChangeJobService.scheduleJob(context, 3);
                }
            } catch (SecurityException e) {
                mStartingService.release();
                Log.d(TAG, "SecurityException when starting service caught", e);
            } catch (RuntimeException e2) {
                mStartingService.release();
                Log.d(TAG, "RuntimeException when starting service caught", e2);
            }
        }
    }

    public static void finishStartingService(Worker worker) {
        synchronized (mStartingServiceSync) {
            if (mStartingService != null && worker.isUsed()) {
                mStartingService.release();
            }
        }
    }

    private static PendingIntent createClickEventIntent(Context context, long j, long j2, long j3, int i, boolean z, long j4) {
        return createShowEventDismissAlarmsIntent(context, j, j2, j3, i, ACTION_CLICK, z, j4);
    }

    private static PendingIntent createDeleteEventIntent(Context context, long j, long j2, long j3, int i, boolean z, long j4) {
        return createDismissAlarmsIntent(context, j, j2, j3, i, ACTION_DELETE, z, j4);
    }

    private static PendingIntent createDismissAlarmsIntent(Context context, long j, long j2, long j3, int i, String str, boolean z, long j4) {
        Intent intent = new Intent();
        intent.setClass(context, DismissAlarmsService.class);
        intent.putExtra("eventid", j);
        intent.putExtra(AlertUtils.EVENT_START_KEY, j2);
        intent.putExtra(AlertUtils.EVENT_END_KEY, j3);
        intent.putExtra("notificationid", i);
        intent.putExtra(AlertUtils.IS_LUNAR_EVENT, z);
        intent.putExtra("_id", j4);
        Uri.Builder builderBuildUpon = (z ? LunarContract.Events.CONTENT_URI : CalendarContract.Events.CONTENT_URI).buildUpon();
        ContentUris.appendId(builderBuildUpon, j);
        ContentUris.appendId(builderBuildUpon, j2);
        intent.setData(builderBuildUpon.build());
        intent.setAction(str);
        return PendingIntent.getService(context, 0, intent, 201326592);
    }

    private static PendingIntent createShowEventDismissAlarmsIntent(Context context, long j, long j2, long j3, int i, String str, boolean z, long j4) {
        Intent intent = new Intent();
        if (j != -1) {
            Intent intentBuildEventViewIntent = AlertUtils.buildEventViewIntent(context, j, j2, j3, z, j4);
            intentBuildEventViewIntent.putExtra(LaunchActivity.ARG_FROM_NOTIFICATION, 1);
            intentBuildEventViewIntent.putExtra("eventid", j);
            intentBuildEventViewIntent.putExtra(AlertUtils.EVENT_START_KEY, j2);
            intentBuildEventViewIntent.putExtra(AlertUtils.EVENT_END_KEY, j3);
            intentBuildEventViewIntent.putExtra(AlertUtils.SHOW_EVENT_KEY, false);
            intentBuildEventViewIntent.putExtra("notificationid", i);
            intentBuildEventViewIntent.putExtra(AlertUtils.IS_LUNAR_EVENT, z);
            intentBuildEventViewIntent.putExtra("_id", j4);
            return TaskStackBuilder.create(context).addNextIntent(new Intent(context, (Class<?>) LaunchActivity.class)).addNextIntent(intentBuildEventViewIntent).getPendingIntent(0, 201326592);
        }
        return PendingIntent.getActivity(context, 0, intent, 201326592);
    }

    private static PendingIntent createSnoozeIntent(Context context, long j, long j2, long j3, int i, boolean z) {
        Intent intent = new Intent();
        intent.setClass(context, SnoozeSelectActivity.class);
        intent.setFlags(268435456);
        intent.putExtra("eventid", j);
        intent.putExtra(AlertUtils.EVENT_START_KEY, j2);
        intent.putExtra(AlertUtils.EVENT_END_KEY, j3);
        intent.putExtra("notificationid", i);
        intent.putExtra(AlertUtils.IS_LUNAR_EVENT, z);
        Uri.Builder builderBuildUpon = (z ? LunarContract.Events.CONTENT_URI : CalendarContract.Events.CONTENT_URI).buildUpon();
        ContentUris.appendId(builderBuildUpon, j);
        ContentUris.appendId(builderBuildUpon, j2);
        intent.setData(builderBuildUpon.build());
        return PendingIntent.getActivity(context, 0, intent, 201326592);
    }

    public static AlertWork.NotificationWrapper makeBasicNotification(Context context, String str, String str2, long j, long j2, long j3, int i, boolean z, long j4, NotificationChannel notificationChannel) {
        NotificationCompat.Builder builderMakeBasicNotificationBuilder = makeBasicNotificationBuilder(context, str, str2, j, j2, j3, i, false, false, z, j4, notificationChannel);
        builderMakeBasicNotificationBuilder.setLocalOnly(true);
        return new AlertWork.NotificationWrapper(builderMakeBasicNotificationBuilder, j3, j, j2);
    }

    private static NotificationCompat.Builder makeBasicNotificationBuilder(Context context, String str, String str2, long j, long j2, long j3, int i, boolean z, boolean z2, boolean z3, long j4, NotificationChannel notificationChannel) {
        Resources resources = context.getResources();
        String string = (str == null || str.length() == 0) ? resources.getString(R.string.no_title_label) : str;
        PendingIntent pendingIntentCreateClickEventIntent = createClickEventIntent(context, j3, j, j2, i, z3, j4);
        PendingIntent pendingIntentCreateDeleteEventIntent = createDeleteEventIntent(context, j3, j, j2, i, z3, j4);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setChannelId(notificationChannel.getId());
        builder.setContentTitle(string);
        builder.setContentText(str2);
        builder.setSmallIcon(R.drawable.stat_notify_calendar);
        builder.setColor(UiUtils.getPrimaryColor(context));
        builder.setContentIntent(pendingIntentCreateClickEventIntent);
        builder.setDeleteIntent(pendingIntentCreateDeleteEventIntent);
        if (z2) {
            builder.addAction(R.drawable.ic_alarm_holo_dark, resources.getString(R.string.snooze_label), createSnoozeIntent(context, j3, j, j2, i, z3));
            PendingIntent pendingIntentCreateBroadcastMailIntent = z3 ? null : createBroadcastMailIntent(context, j3, string);
            if (pendingIntentCreateBroadcastMailIntent != null) {
                builder.addAction(R.drawable.ic_menu_email_holo_dark, resources.getString(R.string.email_guests_label), pendingIntentCreateBroadcastMailIntent);
            } else {
                builder.addAction(R.drawable.ic_reminder_dismiss_light, resources.getString(R.string.snooze_menu_dismiss), createDismissAlarmsIntent(context, j3, j, j2, i, ACTION_DELETE, z3, j4));
            }
        }
        builder.setWhen(0L);
        GaUtils.sendEvent(GaUtils.NOTIFICATION_CATEGORY, GaUtils.NOTIFICATION_CREATED_TAG);
        return builder;
    }

    public static AlertWork.NotificationWrapper makeExpandingNotification(Context context, String str, String str2, String str3, long j, long j2, long j3, int i, boolean z, boolean z2, long j4, NotificationChannel notificationChannel) {
        CharSequence charSequence;
        String strTrim = str3;
        NotificationCompat.Builder builderMakeBasicNotificationBuilder = makeBasicNotificationBuilder(context, str, str2, j, j2, j3, i, z, true, z2, j4, notificationChannel);
        builderMakeBasicNotificationBuilder.setLocalOnly(true);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle(builderMakeBasicNotificationBuilder);
        if (strTrim != null) {
            strTrim = mBlankLinePattern.matcher(strTrim).replaceAll("").trim();
        }
        if (TextUtils.isEmpty(strTrim)) {
            charSequence = str2;
        } else {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) str2);
            spannableStringBuilder.append((CharSequence) "\n\n");
            spannableStringBuilder.setSpan(new RelativeSizeSpan(0.5f), str2.length(), spannableStringBuilder.length(), 0);
            spannableStringBuilder.append((CharSequence) strTrim);
            charSequence = spannableStringBuilder;
        }
        bigTextStyle.bigText(charSequence);
        return new AlertWork.NotificationWrapper(builderMakeBasicNotificationBuilder, j3, j, j2);
    }

    private static PendingIntent createMultipleDeleteIntent(Context context, long[] jArr, long j, boolean[] zArr) {
        Intent intent = new Intent();
        intent.setClass(context, DismissAlarmsService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(AlertUtils.EVENT_IDS_KEY, jArr);
        intent.putExtra(AlertUtils.FIRST_EVENT_START_TIME, j);
        intent.putExtra(AlertUtils.IS_LUNAR_EVENTS, zArr);
        return PendingIntent.getService(context, 0, intent, 201326592);
    }

    private static PendingIntent createShowCalendarMultipleDeleteIntent(Context context, long[] jArr, long j, boolean[] zArr) {
        Intent intent = new Intent();
        if (jArr != null && jArr.length > 0 && zArr != null && zArr.length > 0) {
            intent = new Intent(context, (Class<?>) LaunchActivity.class);
            Uri uri = Uri.parse(TIME_CONTENT_URI + j);
            intent.setAction("android.intent.action.VIEW");
            intent.putExtra(LaunchActivity.ARG_FROM_NOTIFICATION, 1);
            intent.setData(uri);
            intent.setFlags(268435456);
            intent.putExtra(AlertUtils.EVENT_IDS_KEY, jArr);
            intent.putExtra(AlertUtils.FIRST_EVENT_START_TIME, j);
            intent.putExtra(AlertUtils.IS_LUNAR_EVENTS, zArr);
            intent.putExtra(AlertUtils.SHOW_EVENT_KEY, true);
        }
        return PendingIntent.getActivity(context, 0, intent, 201326592);
    }

    public static AlertWork.NotificationWrapper makeDigestNotification(Context context, ArrayList<AlertWork.NotificationInfo> arrayList, String str) {
        if (arrayList == null || arrayList.size() < 1) {
            return null;
        }
        Resources resources = context.getResources();
        int size = arrayList.size();
        long[] jArr = new long[arrayList.size()];
        boolean[] zArr = new boolean[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            jArr[i] = arrayList.get(i).eventId;
            zArr[i] = arrayList.get(i).isLunarEvent;
        }
        long j = arrayList.get(0).startMillis;
        if (str == null || str.length() == 0) {
            str = resources.getString(R.string.no_title_label);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentText(str);
        builder.setSmallIcon(R.drawable.stat_notify_calendar_multiple);
        builder.setColor(UiUtils.getPrimaryColor(context));
        builder.setDeleteIntent(createMultipleDeleteIntent(context, jArr, j, zArr));
        builder.setContentIntent(createShowCalendarMultipleDeleteIntent(context, jArr, j, zArr));
        builder.setContentTitle(resources.getQuantityString(R.plurals.Nevents, size, Integer.valueOf(size)));
        builder.setChannelId(NotificationUtils.NOTIFICATION_NOTICEABLE_CHANNEL_ID);
        builder.setLocalOnly(true);
        return new AlertWork.NotificationWrapper(builder.build());
    }

    private static Cursor getEventCursor(Context context, long j) {
        return context.getContentResolver().query(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, j), EVENT_PROJECTION, null, null, null);
    }

    private static Cursor getAttendeesCursor(Context context, long j) {
        return context.getContentResolver().query(CalendarContract.Attendees.CONTENT_URI, ATTENDEES_PROJECTION, ATTENDEES_WHERE, new String[]{Long.toString(j)}, ATTENDEES_SORT_ORDER);
    }

    /* JADX WARN: Code duplicated, block: B:11:0x0018  */
    private static PendingIntent createBroadcastMailIntent(Context context, long j, String str) {
        String string;
        Cursor eventCursor = getEventCursor(context, j);
        if (eventCursor != null) {
            try {
                if (eventCursor.moveToFirst()) {
                    string = eventCursor.getString(1);
                } else {
                    string = null;
                }
            } catch (Throwable th) {
                Utils.closeCursor(eventCursor);
                throw th;
            }
        } else {
            string = null;
        }
        Utils.closeCursor(eventCursor);
        if (TextUtils.equals(string, DatabaseUtils.ACCOUNT_NAME)) {
            return null;
        }
        Cursor attendeesCursor = getAttendeesCursor(context, j);
        if (attendeesCursor != null) {
            try {
                if (attendeesCursor.moveToFirst()) {
                    while (!Utils.isEmailableFrom(attendeesCursor.getString(0), string)) {
                        if (!attendeesCursor.moveToNext()) {
                        }
                    }
                    Intent intent = new Intent(ACTION_MAIL);
                    intent.setClass(context, AlertReceiver.class);
                    intent.putExtra("eventid", j);
                    return PendingIntent.getBroadcast(context, Long.valueOf(j).hashCode(), intent, 335544320);
                }
            } finally {
                Utils.closeCursor(attendeesCursor);
            }
        }
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:11:0x0023  */
    static Intent createEmailIntent(Context context, long j, String str) {
        String string;
        String string2;
        String str2;
        Cursor eventCursor = getEventCursor(context, j);
        if (eventCursor != null) {
            try {
                if (eventCursor.moveToFirst()) {
                    String string3 = eventCursor.getString(0);
                    string = eventCursor.getString(1);
                    string2 = eventCursor.getString(2);
                    str2 = string3;
                } else {
                    string = null;
                    string2 = null;
                    str2 = null;
                }
            } catch (Throwable th) {
                Utils.closeCursor(eventCursor);
                throw th;
            }
        } else {
            string = null;
            string2 = null;
            str2 = null;
        }
        Utils.closeCursor(eventCursor);
        String string4 = TextUtils.isEmpty(string2) ? context.getResources().getString(R.string.no_title_label) : string2;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Cursor attendeesCursor = getAttendeesCursor(context, j);
        if (attendeesCursor != null) {
            try {
                if (attendeesCursor.moveToFirst()) {
                    do {
                        int i = attendeesCursor.getInt(1);
                        String string5 = attendeesCursor.getString(0);
                        if (i == 2) {
                            addIfEmailable(arrayList2, string5, string);
                        } else {
                            addIfEmailable(arrayList, string5, string);
                        }
                    } while (attendeesCursor.moveToNext());
                }
            } catch (Throwable th2) {
                Utils.closeCursor(attendeesCursor);
                throw th2;
            }
        }
        Utils.closeCursor(attendeesCursor);
        Intent intentCreateEmailAttendeesIntent = (str2 == null || (arrayList.size() <= 0 && arrayList2.size() <= 0)) ? null : Utils.createEmailAttendeesIntent(context.getResources(), string4, str, arrayList, arrayList2, str2);
        if (intentCreateEmailAttendeesIntent == null) {
            return null;
        }
        intentCreateEmailAttendeesIntent.addFlags(268468224);
        return intentCreateEmailAttendeesIntent;
    }

    private static void addIfEmailable(List<String> list, String str, String str2) {
        if (Utils.isEmailableFrom(str, str2)) {
            list.add(str);
        }
    }
}
