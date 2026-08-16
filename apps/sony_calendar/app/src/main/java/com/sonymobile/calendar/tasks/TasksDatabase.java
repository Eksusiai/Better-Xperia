package com.sonymobile.calendar.tasks;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.text.format.Time;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tasks.model.TasksListItem;
import com.sonymobile.calendar.tasks.utils.TimeFormatterUtil;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class TasksDatabase {
    private static final String ALLOWED_REMINDER_TYPES = "0,1";

    public enum DirtyType {
        UNKNOWN,
        INSERT,
        UPDATE,
        DELETE
    }

    public static boolean createTaskListsInDatabaseIfNeeded(Context context) {
        Cursor cursorQuery = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            cursorQuery = contentResolver.query(TasksContract.TaskLists.CONTENT_URI, new String[]{"_id"}, "account_type=\"LOCAL\"", null, TasksContract.TaskLists.DEFAULT_SORT_ORDER);
            if (cursorQuery == null || cursorQuery.getCount() >= 1) {
                return false;
            }
            String string = context.getResources().getString(R.string.task_app_name_txt);
            ContentValues contentValues = new ContentValues();
            contentValues.put(TasksContract.TaskListsColumns.TASKLIST_DISPLAY_NAME, string);
            contentValues.put("account_name", string);
            contentValues.put("account_type", "LOCAL");
            contentValues.put(TasksContract.TaskListsColumns.SYNC_TASKS, (Integer) 1);
            contentValues.put("visible", (Integer) 1);
            contentValues.put("canOrganizerRespond", (Integer) 0);
            contentValues.put("canModifyTimeZone", (Integer) 0);
            contentValues.put("maxReminders", (Integer) 1);
            contentValues.put("allowedReminders", ALLOWED_REMINDER_TYPES);
            contentValues.put(TasksContract.TaskListsColumns.TASKLIST_TIME_ZONE, Time.getCurrentTimezone());
            contentResolver.insert(TasksContract.TaskLists.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", string).appendQueryParameter("account_type", "LOCAL").build(), contentValues);
            return true;
        } finally {
            Utils.closeCursor(cursorQuery);
        }
    }

    public static long addNewTask(Context context, TasksListItem tasksListItem) {
        ContentProviderOperation.Builder builderNewInsert = ContentProviderOperation.newInsert(TasksContract.Tasks.CONTENT_URI);
        ContentValues contentValues = new ContentValues();
        contentValues.put("subject", tasksListItem.taskName);
        contentValues.put(TasksContract.TasksColumns.BODY_DATA, tasksListItem.taskDescription);
        contentValues.put("complete", Integer.valueOf(tasksListItem.completed));
        contentValues.put(TasksContract.TasksColumns.IMPORTANCE, Integer.valueOf(tasksListItem.priority));
        contentValues.put(TasksContract.TasksColumns.TASKLIST_ID, Integer.valueOf(tasksListItem.accountId));
        if (tasksListItem.dueDate > 0) {
            contentValues.put(TasksContract.TasksColumns.DUE_DATE, Long.valueOf(tasksListItem.dueDate));
            contentValues.put(TasksContract.TasksColumns.UTC_DUE_DATE, Long.valueOf(getDueDate(tasksListItem)));
        }
        builderNewInsert.withValues(contentValues);
        ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
        arrayList.add(builderNewInsert.build());
        if (tasksListItem.reminderDate > 0) {
            arrayList.add(ContentProviderOperation.newInsert(TasksContract.Reminders.CONTENT_URI).withValueBackReference("task_id", 0).withValue(TasksContract.RemindersColumns.REMINDER_TIME, Long.valueOf(getReminderDate(tasksListItem.reminderDate))).withValue(TasksContract.RemindersColumns.UTC_REMINDER_TIME, Long.valueOf(tasksListItem.reminderDate)).build());
        }
        try {
            ContentProviderResult[] contentProviderResultArrApplyBatch = context.getContentResolver().applyBatch(TasksContract.AUTHORITY, arrayList);
            if (contentProviderResultArrApplyBatch == null || contentProviderResultArrApplyBatch.length <= 0 || contentProviderResultArrApplyBatch[0].uri == null) {
                return -1L;
            }
            return ContentUris.parseId(contentProviderResultArrApplyBatch[0].uri);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            return -1L;
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return -1L;
        } catch (NullPointerException e3) {
            e3.printStackTrace();
            return -1L;
        }
    }

    public static long updateAccountForTask(Context context, TasksListItem tasksListItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("subject", tasksListItem.taskName);
        contentValues.put(TasksContract.TasksColumns.BODY_DATA, tasksListItem.taskDescription);
        contentValues.put("complete", Integer.valueOf(tasksListItem.completed));
        contentValues.put(TasksContract.TasksColumns.IMPORTANCE, Integer.valueOf(tasksListItem.priority));
        contentValues.put(TasksContract.TasksColumns.TASKLIST_ID, Integer.valueOf(tasksListItem.accountId));
        if (tasksListItem.dueDate > 0) {
            contentValues.put(TasksContract.TasksColumns.DUE_DATE, Long.valueOf(tasksListItem.dueDate));
            contentValues.put(TasksContract.TasksColumns.UTC_DUE_DATE, Long.valueOf(getDueDate(tasksListItem)));
        } else if (tasksListItem.dueDate == 0) {
            contentValues.putNull(TasksContract.TasksColumns.DUE_DATE);
            contentValues.putNull(TasksContract.TasksColumns.UTC_DUE_DATE);
        }
        ContentResolver contentResolver = context.getContentResolver();
        Uri uriInsert = contentResolver.insert(TasksContract.Tasks.CONTENT_URI, contentValues);
        deleteTask(context, tasksListItem.taskId);
        long id = ContentUris.parseId(uriInsert);
        if (tasksListItem.reminderDate > 0) {
            long j = tasksListItem.reminderDate;
            long reminderDate = getReminderDate(j);
            contentValues.clear();
            contentValues.put("task_id", Long.valueOf(id));
            contentValues.put(TasksContract.RemindersColumns.REMINDER_TIME, Long.valueOf(j));
            contentValues.put(TasksContract.RemindersColumns.UTC_REMINDER_TIME, Long.valueOf(reminderDate));
            contentResolver.insert(TasksContract.Reminders.CONTENT_URI, contentValues);
        }
        return id;
    }

    /* JADX WARN: Code duplicated, block: B:32:0x00d2  */
    public static boolean updateExistingTask(Context context, TasksListItem tasksListItem, DirtyType dirtyType) {
        int i;
        ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
        ContentValues contentValues = new ContentValues();
        if (tasksListItem.taskName == null) {
            tasksListItem.taskName = "";
        }
        if (tasksListItem.taskDescription == null) {
            tasksListItem.taskDescription = "";
        }
        contentValues.put("subject", tasksListItem.taskName);
        contentValues.put(TasksContract.TasksColumns.BODY_DATA, tasksListItem.taskDescription);
        if (tasksListItem.priority != -1) {
            contentValues.put(TasksContract.TasksColumns.IMPORTANCE, Integer.valueOf(tasksListItem.priority));
        }
        if (tasksListItem.accountId != -1) {
            contentValues.put(TasksContract.TasksColumns.TASKLIST_ID, Integer.valueOf(tasksListItem.accountId));
        }
        if (tasksListItem.dueDate > 0) {
            contentValues.put(TasksContract.TasksColumns.DUE_DATE, Long.valueOf(tasksListItem.dueDate));
            contentValues.put(TasksContract.TasksColumns.UTC_DUE_DATE, Long.valueOf(getDueDate(tasksListItem)));
        } else if (tasksListItem.dueDate == 0) {
            contentValues.putNull(TasksContract.TasksColumns.DUE_DATE);
            contentValues.putNull(TasksContract.TasksColumns.UTC_DUE_DATE);
        }
        if (contentValues.size() > 0) {
            arrayList.add(ContentProviderOperation.newUpdate(ContentUris.withAppendedId(TasksContract.Tasks.CONTENT_URI, tasksListItem.taskId)).withValues(contentValues).build());
        }
        long j = tasksListItem.reminderDate;
        long reminderDate = getReminderDate(j);
        ContentProviderResult[] contentProviderResultArrApplyBatch;
        try {
            Cursor cursorQuery = context.getContentResolver().query(TasksContract.Reminders.CONTENT_URI, new String[]{"_id"}, "task_id=?", new String[]{String.valueOf(tasksListItem.taskId)}, null);
            try {
                if (cursorQuery != null) {
                    if (cursorQuery.moveToFirst()) {
                        i = cursorQuery.getInt(cursorQuery.getColumnIndexOrThrow("_id"));
                    } else {
                        i = -1;
                    }
                } else {
                    i = -1;
                }
            } finally {
                Utils.closeCursor(cursorQuery);
            }
            if (i != -1) {
                int i2 = AnonymousClass1.$SwitchMap$com$sonymobile$calendar$tasks$TasksDatabase$DirtyType[dirtyType.ordinal()];
                if (i2 == 1) {
                    arrayList.add(ContentProviderOperation.newUpdate(ContentUris.withAppendedId(TasksContract.Reminders.CONTENT_URI, i)).withValue(TasksContract.RemindersColumns.REMINDER_TIME, Long.valueOf(reminderDate)).withValue(TasksContract.RemindersColumns.UTC_REMINDER_TIME, Long.valueOf(j)).build());
                } else if (i2 == 2) {
                    arrayList.add(ContentProviderOperation.newDelete(ContentUris.withAppendedId(TasksContract.Reminders.CONTENT_URI, i)).build());
                    deleteTasksAlerts(context, tasksListItem.taskId);
                }
            } else if (dirtyType == DirtyType.INSERT || dirtyType == DirtyType.UPDATE) {
                arrayList.add(ContentProviderOperation.newInsert(TasksContract.Reminders.CONTENT_URI).withValue("task_id", Long.valueOf(tasksListItem.taskId)).withValue(TasksContract.RemindersColumns.REMINDER_TIME, Long.valueOf(reminderDate)).withValue(TasksContract.RemindersColumns.UTC_REMINDER_TIME, Long.valueOf(j)).build());
            }
            contentProviderResultArrApplyBatch = context.getContentResolver().applyBatch(TasksContract.AUTHORITY, arrayList);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            return false;
        } catch (RemoteException e2) {
            e2.printStackTrace();
            return false;
        }
        return contentProviderResultArrApplyBatch != null;
    }

    /* JADX INFO: renamed from: com.sonymobile.calendar.tasks.TasksDatabase$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$calendar$tasks$TasksDatabase$DirtyType;

        static {
            int[] iArr = new int[DirtyType.values().length];
            $SwitchMap$com$sonymobile$calendar$tasks$TasksDatabase$DirtyType = iArr;
            try {
                iArr[DirtyType.UPDATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$tasks$TasksDatabase$DirtyType[DirtyType.DELETE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public static int completeAllFiredAlarms(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("complete", (Integer) 1);
        ArrayList<Long> taskIds = TasksItemProvider.getInstance().getTaskIds();
        StringBuilder sb = new StringBuilder();
        if (taskIds.isEmpty()) {
            return -1;
        }
        sb.append("_id");
        sb.append("=");
        sb.append(taskIds.get(0));
        for (int i = 1; i < taskIds.size(); i++) {
            sb.append(" OR ");
            sb.append("_id");
            sb.append("=");
            sb.append(taskIds.get(i));
        }
        return context.getContentResolver().update(TasksContract.Tasks.CONTENT_URI, contentValues, sb.toString(), null);
    }

    public static int dimissAllFiredAlarms(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", (Integer) 2);
        return context.getContentResolver().update(TasksContract.TasksAlerts.CONTENT_URI, contentValues, "state=1", null);
    }

    public static int completeTask(Context context, long j, boolean z) {
        ContentValues contentValues = new ContentValues();
        if (z) {
            contentValues.put("complete", (Boolean) true);
            contentValues.put(TasksContract.TasksColumns.DATE_COMPLETED, Long.valueOf(System.currentTimeMillis()));
        } else {
            contentValues.put("complete", (Integer) 0);
            contentValues.put(TasksContract.TasksColumns.DATE_COMPLETED, "");
        }
        return context.getContentResolver().update(TasksContract.Tasks.CONTENT_URI, contentValues, "_id=" + j, null);
    }

    public static TasksListItem getReminderDataForTask(Context context, TasksListItem tasksListItem) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursorQuery = null;
        try {
            cursorQuery = contentResolver.query(TasksContract.Reminders.CONTENT_URI, new String[]{TasksContract.RemindersColumns.UTC_REMINDER_TIME, "method"}, "task_id=?", new String[]{String.valueOf(tasksListItem.taskId)}, null);
            if (cursorQuery != null && cursorQuery.moveToFirst()) {
                tasksListItem.reminderDate = cursorQuery.getLong(cursorQuery.getColumnIndex(TasksContract.RemindersColumns.UTC_REMINDER_TIME));
                tasksListItem.reminderMethod = cursorQuery.getInt(cursorQuery.getColumnIndex("method"));
            }
            return tasksListItem;
        } finally {
            Utils.closeCursor(cursorQuery);
        }
    }

    public static int dismissTask(Context context, long j) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", (Integer) 2);
        return context.getContentResolver().update(TasksContract.TasksAlerts.CONTENT_URI, contentValues, "task_id=" + j, null);
    }

    public static int deleteTasksAlerts(Context context, long j) {
        return context.getContentResolver().delete(TasksContract.TasksAlerts.CONTENT_URI, "task_id=" + j, null);
    }

    public static int deleteTask(Context context, long j) {
        return context.getContentResolver().delete(TasksContract.Tasks.CONTENT_URI, "_id=" + j, null);
    }

    private static long getDueDate(TasksListItem tasksListItem) {
        return TimeFormatterUtil.transDateTime2Date(tasksListItem.dueDate, TimeZone.getDefault());
    }

    private static long getReminderDate(long j) {
        return TimeFormatterUtil.transTimeZone(j, TimeZone.getTimeZone("UTC"), TimeZone.getDefault());
    }

    public static int deleteAllCompletedTasks(Context context) {
        return context.getContentResolver().delete(TasksContract.Tasks.CONTENT_URI, "complete=1", null);
    }
}
