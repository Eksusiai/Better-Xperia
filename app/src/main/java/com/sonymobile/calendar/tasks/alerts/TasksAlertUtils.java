package com.sonymobile.calendar.tasks.alerts;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tasks.activity.TasksEditActivity;
import com.sonymobile.calendar.tasks.utils.TimeFormatterUtil;
import com.sonymobile.provider.TasksContract;

/* JADX INFO: loaded from: classes2.dex */
public class TasksAlertUtils {
    public static final String ALARM_TIME_KEY = "alarmtime";
    public static final String COMPLETE_KEY = "complete";
    public static final int EXPIRED_GROUP_NOTIFICATION_ID = 100000;
    public static final String NOTIFICATION_ID_KEY = "notificationid";
    public static final String SHOW_TASK_KEY = "showtask";
    public static final String TASKS_CLICK_ACTION = "com.sonymobile.tasks.CLICK";
    public static final String TASKS_COMPLETE_ACTION = "com.sonymobile.tasks.COMPLETE";
    public static final String TASKS_DELETE_ACTION = "com.sonymobile.tasks.DELETE";
    public static final String TASKS_DELETE_ALL_ACTION = "com.sonymobile.tasks.DELETEALL";
    public static final String TASK_IDS_KEY = "taskids";
    public static final String TASK_ID_KEY = "taskid";

    public static void scheduleAlarm(Context context, AlarmManager alarmManager, long j) {
        scheduleAlarmHelper(context, alarmManager, j, false);
    }

    private static void scheduleAlarmHelper(Context context, AlarmManager alarmManager, long j, boolean z) {
        int i;
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService("alarm");
        }
        Intent intent = new Intent(TasksContract.ACTION_TASK_REMINDER);
        intent.setClass(context, TasksAlertReceiver.class);
        if (z) {
            i = 1;
        } else {
            Uri.Builder builderBuildUpon = TasksContract.TasksAlerts.CONTENT_URI.buildUpon();
            ContentUris.appendId(builderBuildUpon, j);
            intent.setData(builderBuildUpon.build());
            i = 0;
        }
        intent.putExtra("alarmTime", j);
        Utils.setAlarm(alarmManager, i, j, PendingIntent.getBroadcast(context, 0, intent, 201326592));
    }

    static String formatTimeLocation(Context context, long j, int i) {
        String str = context.getResources().getString(R.string.task_edit_due_date_item_txt) + ": ";
        if (j == 0) {
            return str + context.getResources().getString(R.string.task_list_group_title_no_due_date_txt);
        }
        return str + TimeFormatterUtil.getFormattedDate(context, j, true);
    }

    public static Intent buildTaskViewIntent(Context context, long j, long j2) {
        Intent intent = new Intent("android.intent.action.VIEW");
        Uri.Builder builderBuildUpon = TasksContract.CONTENT_URI.buildUpon();
        builderBuildUpon.appendEncodedPath("tasks/" + j);
        intent.setData(builderBuildUpon.build());
        intent.putExtra(TasksEditActivity.TASK_ID, j);
        intent.putExtra(ALARM_TIME_KEY, j2);
        if (Utils.isTabletDevice(context)) {
            intent.setClass(context, LaunchActivity.class);
        } else {
            intent.setClass(context, TasksEditActivity.class);
        }
        intent.setFlags(268435456);
        return intent;
    }

    public static boolean wasStartedFromNotification(Activity activity) {
        return TASKS_CLICK_ACTION.equals(activity.getIntent().getAction());
    }
}
