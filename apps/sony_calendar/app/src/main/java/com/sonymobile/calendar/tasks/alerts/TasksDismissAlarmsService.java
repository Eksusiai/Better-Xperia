package com.sonymobile.calendar.tasks.alerts;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import com.google.common.primitives.Longs;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import com.sonymobile.calendar.tasks.TasksDatabase;
import com.sonymobile.provider.TasksContract;

/* JADX INFO: loaded from: classes2.dex */
public class TasksDismissAlarmsService extends IntentService {
    private static final int NOTIFICATION_ID_NONE = -1;
    private static final int TASK_ID_NONE = -1;

    @Override // android.app.IntentService, android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    public TasksDismissAlarmsService() {
        super("DismissAlarmsService");
    }

    @Override // android.app.IntentService
    public void onHandleIntent(Intent intent) {
        long longExtra = intent.getLongExtra(TasksAlertUtils.TASK_ID_KEY, -1L);
        long[] longArrayExtra = intent.getLongArrayExtra(TasksAlertUtils.TASK_IDS_KEY);
        int intExtra = intent.getIntExtra("notificationid", -1);
        intent.getLongExtra(TasksAlertUtils.ALARM_TIME_KEY, 0L);
        intent.getBooleanExtra(TasksAlertUtils.SHOW_TASK_KEY, false);
        if (TasksAlertUtils.TASKS_COMPLETE_ACTION.equals(intent.getAction())) {
            if (longExtra != -1) {
                TasksDatabase.completeTask(getBaseContext(), longExtra, true);
            } else if (longArrayExtra != null) {
                onMultipleTasksCompleted(longArrayExtra);
            }
        } else if (longExtra != -1) {
            TasksDatabase.dismissTask(getBaseContext(), longExtra);
        } else if (longArrayExtra != null) {
            onMultipleTaskAlertsDismissed(longArrayExtra);
        }
        if (intExtra != -1) {
            ((NotificationManager) getSystemService("notification")).cancel(intExtra);
        }
        stopSelf();
    }

    private void onMultipleTasksCompleted(long[] jArr) {
        String strBuildQuerySelection = buildQuerySelection("_id", jArr);
        ContentValues contentValues = new ContentValues();
        contentValues.put("complete", (Integer) 1);
        contentValues.put(TasksContract.TasksColumns.DATE_COMPLETED, Long.valueOf(System.currentTimeMillis()));
        getContentResolver().update(TasksContract.Tasks.CONTENT_URI, contentValues, strBuildQuerySelection, null);
        onMultipleTaskAlertsDismissed(jArr);
    }

    private void onMultipleTaskAlertsDismissed(long[] jArr) {
        String strBuildQuerySelection = buildQuerySelection("task_id", jArr);
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", (Integer) 2);
        getContentResolver().update(TasksContract.TasksAlerts.CONTENT_URI, contentValues, strBuildQuerySelection, null);
    }

    private String buildQuerySelection(String str, long[] jArr) {
        return String.format("%s IN (%s)", str, Longs.join(RecurrenceRuleParser.VALUE_SEPARATOR, jArr));
    }
}
