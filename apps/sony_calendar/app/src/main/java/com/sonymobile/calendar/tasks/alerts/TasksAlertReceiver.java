package com.sonymobile.calendar.tasks.alerts;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.PowerManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.jobs.ProviderChangeJobService;
import com.sonymobile.calendar.tasks.activity.TasksNotificationActivity;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class TasksAlertReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_DIGEST_MAX_LENGTH = 6;
    private static final String TAG = "TasksAlertReceiver";
    static PowerManager.WakeLock mStartingService;
    static final Object mStartingServiceSync = new Object();
    private static final Pattern mBlankLinePattern = Pattern.compile("^\\s*$[\n\r]", 8);
    private static final long[] NO_VIBRATE = {0};

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: a=" + intent.getAction() + " " + intent.toString());
        if (PermissionUtils.isCalendarGranted(context) && Utils.classExists(context, "com.sonymobile.provider.TasksContract$TasksAlerts")) {
            prepareService(context, intent);
        }
    }

    public static void prepareService(Context context, Intent intent) {
        Intent intent2 = new Intent();
        intent2.setClass(context, TasksAlertWork.class);
        intent2.putExtras(intent);
        intent2.putExtra("action", intent.getAction());
        Uri data = intent.getData();
        if (data != null) {
            intent2.putExtra("uri", data.toString());
        }
        beginStartingService(context, intent2);
    }

    public static void beginStartingService(Context context, Intent intent) {
        synchronized (mStartingServiceSync) {
            if (mStartingService == null) {
                PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "StartingTasksAlertWork");
                mStartingService = wakeLockNewWakeLock;
                wakeLockNewWakeLock.setReferenceCounted(false);
            }
            mStartingService.acquire();
            try {
                WorkManager.getInstance(context).enqueue(new OneTimeWorkRequest.Builder(TasksAlertWork.class).setInputData(new Data.Builder().putInt("JobId", 2).putString("action", intent.getStringExtra("action")).putString("uri", intent.getStringExtra("uri")).build()).build());
                if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                    ProviderChangeJobService.scheduleJob(context, 4);
                }
            } catch (SecurityException e) {
                mStartingService.release();
                Log.d(TAG, "SecurityException when starting service caught", e);
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

    private static PendingIntent createClickTaskIntent(Context context, long j, long j2, int i, int i2) {
        return createShowTasksDismissAlarmsIntent(context, j, j2, i, i2, TasksAlertUtils.TASKS_CLICK_ACTION);
    }

    private static PendingIntent createDeleteTaskIntent(Context context, long j, long j2, int i, int i2) {
        return createDismissAlarmsIntent(context, j, j2, i, i2, TasksAlertUtils.TASKS_DELETE_ACTION);
    }

    private static PendingIntent createDismissAlarmsIntent(Context context, long j, long j2, int i, int i2, String str) {
        Intent intent = new Intent();
        intent.setClass(context, TasksDismissAlarmsService.class);
        intent.putExtra(TasksAlertUtils.TASK_ID_KEY, j);
        intent.putExtra(TasksAlertUtils.ALARM_TIME_KEY, j2);
        intent.putExtra("complete", i);
        intent.putExtra("notificationid", i2);
        Uri.Builder builderBuildUpon = TasksContract.Tasks.CONTENT_URI.buildUpon();
        ContentUris.appendId(builderBuildUpon, j);
        ContentUris.appendId(builderBuildUpon, j2);
        intent.setData(builderBuildUpon.build());
        intent.setAction(str);
        return PendingIntent.getService(context, 0, intent, 201326592);
    }

    private static PendingIntent createShowTasksDismissAlarmsIntent(Context context, long j, long j2, int i, int i2, String str) {
        Intent intentBuildTaskViewIntent = TasksAlertUtils.buildTaskViewIntent(context.getApplicationContext(), j, j2);
        intentBuildTaskViewIntent.putExtra(LaunchActivity.ARG_FROM_NOTIFICATION, 2);
        intentBuildTaskViewIntent.putExtra(TasksAlertUtils.TASK_ID_KEY, j);
        intentBuildTaskViewIntent.putExtra(TasksAlertUtils.ALARM_TIME_KEY, j2);
        intentBuildTaskViewIntent.putExtra("complete", i);
        intentBuildTaskViewIntent.putExtra("notificationid", i2);
        Uri.Builder builderBuildUpon = TasksContract.Tasks.CONTENT_URI.buildUpon();
        ContentUris.appendId(builderBuildUpon, j);
        ContentUris.appendId(builderBuildUpon, j2);
        intentBuildTaskViewIntent.setData(builderBuildUpon.build());
        intentBuildTaskViewIntent.setAction(str);
        return PendingIntent.getActivity(context, 0, intentBuildTaskViewIntent, 201326592);
    }

    public static TasksAlertWork.NotificationWrapper makeExpandingNotification(Context context, String str, String str2, String str3, long j, long j2, boolean z, int i, int i2, boolean z2, boolean z3, int i3) {
        CharSequence charSequence;
        String strTrim = str3;
        Notification.Builder builderMakeBasicNotificationBuilder = makeBasicNotificationBuilder(context, str, str2, j, j2, z, i, i2, z2, false, true, i3);
        builderMakeBasicNotificationBuilder.setChannelId("notification_tasks");
        builderMakeBasicNotificationBuilder.addAction(R.drawable.notification_mark_complete, context.getString(R.string.task_complete_button_text), createDismissAlarmsIntent(context, j2, j, i, i2, TasksAlertUtils.TASKS_COMPLETE_ACTION));
        builderMakeBasicNotificationBuilder.setContentIntent(createClickTaskIntent(context, j2, j, i, i2));
        builderMakeBasicNotificationBuilder.addAction(R.drawable.ic_reminder_dismiss_light, context.getString(R.string.task_dismiss_button_text), createDismissAlarmsIntent(context, j2, j, i, i2, TasksAlertUtils.TASKS_DELETE_ACTION));
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle(builderMakeBasicNotificationBuilder);
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
        return new TasksAlertWork.NotificationWrapper(bigTextStyle.build(), i2, j2, j, z2);
    }

    private static Notification.Builder makeBasicNotificationBuilder(Context context, String str, String str2, long j, long j2, boolean z, int i, int i2, boolean z2, boolean z3, boolean z4, int i3) {
        String string = TextUtils.isEmpty(str) ? context.getResources().getString(R.string.no_title_label) : str;
        PendingIntent pendingIntentCreateDeleteTaskIntent = createDeleteTaskIntent(context, j2, j, i, i2);
        int i4 = R.drawable.stat_notify_tasks;
        if (i3 == 2) {
            i4 = R.drawable.stat_notify_tasks_high;
        }
        return new Notification.Builder(context).setContentTitle(string).setContentText(str2).setColor(UiUtils.getPrimaryColor(context)).setSmallIcon(i4).setDeleteIntent(pendingIntentCreateDeleteTaskIntent).setPriority(1);
    }

    public static TasksAlertWork.NotificationWrapper makeDigestNotification(Context context, ArrayList<TasksAlertWork.NotificationInfo> arrayList, String str, int i, int i2) {
        if (arrayList == null || arrayList.size() < 1) {
            return null;
        }
        Resources resources = context.getResources();
        int size = arrayList.size();
        long[] jArr = new long[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            jArr[i3] = arrayList.get(i3).taskId;
        }
        PendingIntent pendingIntentCreateClickAlertGroupIntent = createClickAlertGroupIntent(context, jArr, i);
        PendingIntent pendingIntentCreateCompletedIntent = createCompletedIntent(context, jArr, i);
        Intent intent = new Intent();
        intent.setClass(context, TasksDismissAlarmsService.class);
        intent.setAction(TasksAlertUtils.TASKS_DELETE_ALL_ACTION);
        intent.putExtra(TasksAlertUtils.TASK_IDS_KEY, jArr);
        intent.putExtra("notificationid", i);
        PendingIntent service = PendingIntent.getService(context, 0, intent, 201326592);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setChannelId("notification_tasks");
        if (!TextUtils.isEmpty(str)) {
            builder.setContentText(str);
        }
        builder.setSmallIcon(R.drawable.stat_notify_tasks_multiple);
        builder.setColor(UiUtils.getPrimaryColor(context));
        builder.setContentIntent(pendingIntentCreateClickAlertGroupIntent);
        String quantityString = resources.getQuantityString(R.plurals.number_tasks, size, Integer.valueOf(size));
        builder.setContentTitle(quantityString);
        builder.addAction(R.drawable.notification_mark_complete, context.getString(R.string.task_complete_all_button_text), pendingIntentCreateCompletedIntent);
        builder.addAction(R.drawable.ic_reminder_dismiss_light, context.getString(R.string.task_dismiss_all_button_text), service);
        builder.setDeleteIntent(service);
        builder.setPriority(1);
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle(builder);
        inboxStyle.setBigContentTitle(quantityString);
        int i4 = 0;
        for (TasksAlertWork.NotificationInfo notificationInfo : arrayList) {
            if (i4 >= 6) {
                break;
            }
            String string = notificationInfo.taskName;
            int i5 = notificationInfo.complete;
            if (TextUtils.isEmpty(string)) {
                string = context.getResources().getString(R.string.no_title_label);
            }
            TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(context, R.style.NotificationPrimaryText);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) string);
            spannableStringBuilder.setSpan(textAppearanceSpan, 0, spannableStringBuilder.length(), 256);
            if (i5 == 1) {
                StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(-7829368);
                spannableStringBuilder.setSpan(strikethroughSpan, 0, spannableStringBuilder.length(), 33);
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, spannableStringBuilder.length(), 33);
            }
            inboxStyle.addLine(spannableStringBuilder);
            i4++;
        }
        TasksAlertWork.NotificationWrapper notificationWrapper = new TasksAlertWork.NotificationWrapper(inboxStyle.build());
        for (TasksAlertWork.NotificationInfo notificationInfo2 : arrayList) {
            notificationWrapper.add(new TasksAlertWork.NotificationWrapper(null, 0, notificationInfo2.taskId, notificationInfo2.dueDateMillis, false));
        }
        return notificationWrapper;
    }

    private static PendingIntent createClickAlertGroupIntent(Context context, long[] jArr, int i) {
        Intent intent = new Intent();
        intent.setClass(context, TasksNotificationActivity.class);
        intent.setAction(TasksAlertUtils.TASKS_CLICK_ACTION);
        intent.putExtra(TasksAlertUtils.TASK_IDS_KEY, jArr);
        return PendingIntent.getActivity(context, 0, intent, 201326592);
    }

    private static PendingIntent createCompletedIntent(Context context, long[] jArr, int i) {
        Intent intent = new Intent();
        intent.setClass(context, TasksDismissAlarmsService.class);
        intent.setAction(TasksAlertUtils.TASKS_COMPLETE_ACTION);
        intent.putExtra(TasksAlertUtils.TASK_IDS_KEY, jArr);
        intent.putExtra("notificationid", i);
        return PendingIntent.getService(context, 0, intent, 201326592);
    }
}
