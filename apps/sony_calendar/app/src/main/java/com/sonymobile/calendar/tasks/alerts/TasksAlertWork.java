package com.sonymobile.calendar.tasks.alerts;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.NotificationUtils;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class TasksAlertWork extends Worker {
    private static final String ACTIVE_ALERTS_SELECTION = "(state=? OR state=?) AND alarmTime<=";
    private static final String ACTIVE_ALERTS_SORT = "due_date DESC, importance DESC";
    private static final int ALERT_INDEX_ALARM_TIME = 4;
    private static final int ALERT_INDEX_BODY_DATA = 8;
    private static final int ALERT_INDEX_COMPLETE = 7;
    private static final int ALERT_INDEX_DUE_DATE = 5;
    private static final int ALERT_INDEX_ID = 0;
    private static final int ALERT_INDEX_IMPORTANCE = 6;
    private static final int ALERT_INDEX_STATE = 2;
    private static final int ALERT_INDEX_SUBJECT = 3;
    private static final int ALERT_INDEX_TASK_ID = 1;
    private static final int ALERT_INDEX_UTC_DUE_DATE = 9;
    public static final int MAX_NOTIFICATIONS = 20;
    public static final String NOTIFICATION_TASKS_CHANNEL_ID = "notification_tasks";
    private static final String SORT_ORDER_ALARMTIME_ASC = "alarmTime ASC";
    private static final String TAG = "TasksAlertWork";
    private static final String WHERE_RESCHEDULE_MISSED_ALARMS = "state=0 AND alarmTime<?";
    private static NotificationUtils mNotificationUtils;
    private volatile Context mContext;
    private volatile ServiceHandler mServiceHandler;
    private volatile Looper mServiceLooper;
    static final String[] ALERT_PROJECTION = {"_id", "task_id", "state", "subject", "alarmTime", TasksContract.TasksColumns.DUE_DATE, TasksContract.TasksColumns.IMPORTANCE, "complete", TasksContract.TasksColumns.BODY_DATA, TasksContract.TasksColumns.UTC_DUE_DATE};
    private static final String[] ACTIVE_ALERTS_SELECTION_ARGS = {Integer.toString(1), Integer.toString(0)};

    public interface NotificationMgr {
        void cancel(int i);

        void cancel(String str, int i);

        void cancelAll();

        void notify(int i, NotificationWrapper notificationWrapper);

        void notify(String str, int i, NotificationWrapper notificationWrapper);
    }

    public static class NotificationWrapper {
        Notification mNotification;
        ArrayList<NotificationWrapper> mNw;

        public NotificationWrapper(Notification notification, int i, long j, long j2, boolean z) {
            this.mNotification = notification;
        }

        public NotificationWrapper(Notification notification) {
            this.mNotification = notification;
        }

        public void add(NotificationWrapper notificationWrapper) {
            if (this.mNw == null) {
                this.mNw = new ArrayList<>();
            }
            this.mNw.add(notificationWrapper);
        }
    }

    public static class NotificationMgrWrapper implements NotificationMgr {
        NotificationManager mNm;

        public NotificationMgrWrapper(NotificationManager notificationManager) {
            this.mNm = notificationManager;
        }

        @Override // com.sonymobile.calendar.tasks.alerts.TasksAlertWork.NotificationMgr
        public void cancel(int i) {
            this.mNm.cancel(i);
        }

        @Override // com.sonymobile.calendar.tasks.alerts.TasksAlertWork.NotificationMgr
        public void cancel(String str, int i) {
            this.mNm.cancel(str, i);
        }

        @Override // com.sonymobile.calendar.tasks.alerts.TasksAlertWork.NotificationMgr
        public void cancelAll() {
            this.mNm.cancelAll();
        }

        @Override // com.sonymobile.calendar.tasks.alerts.TasksAlertWork.NotificationMgr
        public void notify(int i, NotificationWrapper notificationWrapper) {
            this.mNm.notify(i, notificationWrapper.mNotification);
        }

        @Override // com.sonymobile.calendar.tasks.alerts.TasksAlertWork.NotificationMgr
        public void notify(String str, int i, NotificationWrapper notificationWrapper) {
            this.mNm.notify(str, i, notificationWrapper.mNotification);
        }
    }

    void processMessage(Message message) {
        String string = ((Data) message.obj).getString("action");
        if (string == null) {
            return;
        }
        if (string.equals("android.intent.action.PROVIDER_CHANGED") || string.equals(TasksContract.ACTION_TASK_REMINDER) || string.equals("android.intent.action.LOCALE_CHANGED")) {
            updateAlertNotification(this.mContext);
        } else if (string.equals("android.intent.action.BOOT_COMPLETED") || string.equals("android.intent.action.TIME_SET")) {
            doTimeChanged();
        } else {
            Log.w(TAG, "Invalid action: " + string);
        }
    }

    public static boolean updateAlertNotification(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        NotificationMgrWrapper notificationMgrWrapper = new NotificationMgrWrapper((NotificationManager) context.getSystemService("notification"));
        long jCurrentTimeMillis = System.currentTimeMillis();
        Cursor cursorQuery = contentResolver.query(TasksContract.TasksAlerts.CONTENT_URI, ALERT_PROJECTION, ACTIVE_ALERTS_SELECTION + jCurrentTimeMillis, ACTIVE_ALERTS_SELECTION_ARGS, ACTIVE_ALERTS_SORT);
        if (cursorQuery == null || cursorQuery.getCount() == 0) {
            Utils.closeCursor(cursorQuery);
            notificationMgrWrapper.cancelAll();
            return false;
        }
        return generateAlerts(context, notificationMgrWrapper, cursorQuery, jCurrentTimeMillis, 20);
    }

    public static boolean generateAlerts(Context context, NotificationMgr notificationMgr, Cursor cursor, long j, int i) {
        int i2;
        NotificationWrapper notificationWrapperMakeDigestNotification;
        setupNotificationChannel(context);
        ArrayList arrayList = new ArrayList();
        int iProcessQuery = processQuery(cursor, context, j, arrayList);
        if (arrayList.size() > 0) {
            int size = arrayList.size();
            String digestTitle = getDigestTitle(arrayList);
            if (size == 1) {
                NotificationInfo notificationInfo = (NotificationInfo) arrayList.get(0);
                notificationWrapperMakeDigestNotification = TasksAlertReceiver.makeExpandingNotification(context, notificationInfo.taskName, TasksAlertUtils.formatTimeLocation(context, notificationInfo.dueDateMillis, notificationInfo.importance), notificationInfo.description, notificationInfo.dueDateMillis, notificationInfo.taskId, notificationInfo.newAlert, notificationInfo.complete, TasksAlertUtils.EXPIRED_GROUP_NOTIFICATION_ID, true, true, notificationInfo.importance);
                i2 = TasksAlertUtils.EXPIRED_GROUP_NOTIFICATION_ID;
            } else {
                i2 = 100000;
                notificationWrapperMakeDigestNotification = TasksAlertReceiver.makeDigestNotification(context, arrayList, digestTitle, TasksAlertUtils.EXPIRED_GROUP_NOTIFICATION_ID, iProcessQuery);
            }
            if (iProcessQuery > 0) {
                notificationMgr.cancel(i2);
            }
            if (notificationWrapperMakeDigestNotification == null) {
                return false;
            }
            notificationMgr.notify(i2, notificationWrapperMakeDigestNotification);
            return true;
        }
        notificationMgr.cancelAll();
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:19:0x007a  */
    static int processQuery(Cursor cursor, Context context, long j, ArrayList<NotificationInfo> arrayList) {
        int i;
        boolean z;
        ContentResolver contentResolver = context.getContentResolver();
        int i2 = 0;
        int i3 = 0;
        while (cursor.moveToNext()) {
            try {
                long j2 = cursor.getLong(i2);
                long j3 = cursor.getLong(1);
                long j4 = cursor.getLong(9);
                String string = cursor.getString(3);
                String string2 = cursor.getString(8);
                int i4 = cursor.getInt(6);
                int i5 = cursor.getInt(7);
                Uri uriWithAppendedId = ContentUris.withAppendedId(TasksContract.TasksAlerts.CONTENT_URI, j2);
                int i6 = cursor.getInt(2);
                ContentValues contentValues = new ContentValues();
                if (i6 == 0) {
                    i3++;
                    contentValues.put("receivedTime", Long.valueOf(j));
                    z = true;
                    i = 1;
                } else {
                    i = -1;
                    z = false;
                }
                if (i != -1) {
                    contentValues.put("state", Integer.valueOf(i));
                    i6 = i;
                }
                if (i6 == 1) {
                    contentValues.put("notifyTime", Long.valueOf(j));
                }
                if (contentValues.size() > 0) {
                    contentResolver.update(uriWithAppendedId, contentValues, null, null);
                }
                if (i6 == 1) {
                    NotificationInfo notificationInfo = new NotificationInfo(string, string2, j4, j3, i4, z, i5);
                    if (i5 == 0) {
                        arrayList.add(notificationInfo);
                    }
                }
                i2 = 0;
            } catch (Throwable th) {
                Utils.closeCursor(cursor);
                throw th;
            }
        }
        Utils.closeCursor(cursor);
        return i3;
    }

    private static void setupNotificationChannel(Context context) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        mNotificationUtils = notificationUtils;
        notificationUtils.createTaskChannels(context);
    }

    private static String getDigestTitle(ArrayList<NotificationInfo> arrayList) {
        StringBuilder sb = new StringBuilder();
        for (NotificationInfo notificationInfo : arrayList) {
            if (!TextUtils.isEmpty(notificationInfo.taskName)) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(notificationInfo.taskName);
            }
        }
        return sb.toString();
    }

    static class NotificationInfo {
        int complete;
        String description;
        long dueDateMillis;
        int importance;
        boolean newAlert;
        long taskId;
        String taskName;

        NotificationInfo(String str, String str2, long j, long j2, int i, boolean z, int i2) {
            this.taskName = str;
            this.description = str2;
            this.dueDateMillis = j;
            this.taskId = j2;
            this.importance = i;
            this.newAlert = z;
            this.complete = i2;
        }
    }

    private void doTimeChanged() {
        rescheduleMissedAlarms(this.mContext.getContentResolver(), this.mContext, (AlarmManager) this.mContext.getSystemService("alarm"));
        updateAlertNotification(this.mContext);
    }

    public static void rescheduleMissedAlarms(ContentResolver contentResolver, Context context, AlarmManager alarmManager) {
        Cursor cursorQuery = contentResolver.query(TasksContract.TasksAlerts.CONTENT_URI, new String[]{"alarmTime"}, WHERE_RESCHEDULE_MISSED_ALARMS, new String[]{Long.toString(System.currentTimeMillis())}, SORT_ORDER_ALARMTIME_ASC);
        if (cursorQuery == null) {
            return;
        }
        long j = -1;
        while (cursorQuery.moveToNext()) {
            try {
                long j2 = cursorQuery.getLong(0);
                if (j != j2) {
                    TasksAlertUtils.scheduleAlarm(context, alarmManager, j2);
                    j = j2;
                }
            } finally {
                cursorQuery.close();
            }
        }
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            TasksAlertWork.this.processMessage(message);
            TasksAlertWork.this.mServiceLooper.quit();
            TasksAlertWork.this.setUsed();
            TasksAlertReceiver.finishStartingService(TasksAlertWork.this);
        }
    }

    public TasksAlertWork(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("TasksAlertWork", 10);
        handlerThread.start();
        this.mServiceLooper = handlerThread.getLooper();
        this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
    }

    @Override // androidx.work.Worker
    public ListenableWorker.Result doWork() {
        Data inputData = getInputData();
        if (2 != inputData.getInt("JobId", 99)) {
            return ListenableWorker.Result.failure();
        }
        Message messageObtainMessage = this.mServiceHandler.obtainMessage();
        messageObtainMessage.arg1 = 0;
        messageObtainMessage.obj = inputData;
        this.mServiceHandler.sendMessage(messageObtainMessage);
        return ListenableWorker.Result.success();
    }
}
