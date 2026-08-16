package com.sonymobile.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.SyncStateContract;

/* JADX INFO: loaded from: classes2.dex */
public final class TasksContract {
    public static final String ACCOUNT_TYPE_LOCAL = "LOCAL";
    public static final String ACTION_TASK_REMINDER = "com.sonymobile.intent.action.TASK_REMINDER";
    public static final String AUTHORITY = "com.sonymobile.tasks.provider";
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    public static final String CATEGORY_TOKENIZER_DELIMITER = "\\";
    public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.tasks.provider");

    protected interface ColorsColumns extends SyncStateContract.Columns {
        public static final String COLOR = "color";
        public static final String COLOR_KEY = "color_index";
        public static final String COLOR_TYPE = "color_type";
        public static final int TYPE_TASK = 1;
        public static final int TYPE_TASKLIST = 0;
    }

    protected interface ExtendedPropertiesColumns {
        public static final String NAME = "name";
        public static final String TASK_ID = "task_id";
        public static final String VALUE = "value";
    }

    public interface RemindersColumns {
        public static final String METHOD = "method";
        public static final int METHOD_ALARM = 4;
        public static final int METHOD_ALERT = 1;
        public static final int METHOD_DEFAULT = 0;
        public static final int METHOD_EMAIL = 2;
        public static final int METHOD_SMS = 3;
        public static final String REMINDER_TIME = "reminder_time";
        public static final String TASK_ID = "task_id";
        public static final String UTC_REMINDER_TIME = "utc_reminder_time";
    }

    protected interface SyncColumns extends TaskSyncColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String CAN_PARTIALLY_UPDATE = "canPartiallyUpdate";
        public static final String DELETED = "deleted";
        public static final String DIRTY = "dirty";
        public static final String _SYNC_ID = "_sync_id";
    }

    public interface TaskListsColumns {
        public static final String ALLOWED_ATTENDEE_TYPES = "allowedAttendeeTypes";
        public static final String ALLOWED_AVAILABILITY = "allowedAvailability";
        public static final String ALLOWED_REMINDERS = "allowedReminders";
        public static final String CAN_MODIFY_TIME_ZONE = "canModifyTimeZone";
        public static final String CAN_ORGANIZER_RESPOND = "canOrganizerRespond";
        public static final String MAX_REMINDERS = "maxReminders";
        public static final String OWNER_ACCOUNT = "ownerAccount";
        public static final String SYNC_TASKS = "sync_tasks";
        public static final int TASKLIST_ACCESS_CONTRIBUTOR = 500;
        public static final int TASKLIST_ACCESS_EDITOR = 600;
        public static final int TASKLIST_ACCESS_FREEBUSY = 100;
        public static final String TASKLIST_ACCESS_LEVEL = "tasklist_access_level";
        public static final int TASKLIST_ACCESS_NONE = 0;
        public static final int TASKLIST_ACCESS_OVERRIDE = 400;
        public static final int TASKLIST_ACCESS_OWNER = 700;
        public static final int TASKLIST_ACCESS_READ = 200;
        public static final int TASKLIST_ACCESS_RESPOND = 300;
        public static final int TASKLIST_ACCESS_ROOT = 800;
        public static final String TASKLIST_COLOR = "tasklist_color";
        public static final String TASKLIST_COLOR_KEY = "tasklist_color_index";
        public static final String TASKLIST_DISPLAY_NAME = "tasklist_displayName";
        public static final String TASKLIST_TIME_ZONE = "tasklist_timezone";
        public static final String VISIBLE = "visible";
    }

    protected interface TaskSyncColumns {
        public static final String _SYNC1 = "_sync1";
        public static final String _SYNC10 = "_sync10";
        public static final String _SYNC2 = "_sync2";
        public static final String _SYNC3 = "_sync3";
        public static final String _SYNC4 = "_sync4";
        public static final String _SYNC5 = "_sync5";
        public static final String _SYNC6 = "_sync6";
        public static final String _SYNC7 = "_sync7";
        public static final String _SYNC8 = "_sync8";
        public static final String _SYNC9 = "_sync9";
    }

    protected interface TasksAlertsColumns {
        public static final String ALARM_TIME = "alarmTime";
        public static final String CREATION_TIME = "creationTime";
        public static final String DEFAULT_SORT_ORDER = "alarmTime ASC,subject ASC";
        public static final String NOTIFY_TIME = "notifyTime";
        public static final String RECEIVED_TIME = "receivedTime";
        public static final String STATE = "state";
        public static final int STATE_DISMISSED = 2;
        public static final int STATE_FIRED = 1;
        public static final int STATE_SCHEDULED = 0;
        public static final String TASK_ID = "task_id";
    }

    public interface TasksColumns {
        public static final String BODY_DATA = "body";
        public static final String BODY_SIZE = "body_size";
        public static final String BODY_TRUNCATED = "body_truncated";
        public static final String BODY_TYPE = "body_type";
        public static final int BODY_TYPE_HTML = 2;
        public static final int BODY_TYPE_MIME = 4;
        public static final int BODY_TYPE_PLAIN_TEXT = 1;
        public static final int BODY_TYPE_RTF = 3;
        public static final String CATEGORIES = "categories";
        public static final String COMPLETE = "complete";
        public static final String DATE_COMPLETED = "date_completed";
        public static final String DUE_DATE = "due_date";
        public static final String EXDATE = "exdate";
        public static final String EXRULE = "exrule";
        public static final String HAS_EXTENDED_PROPERTIES = "hasExtendedProperties";
        public static final String HAS_REMINDER = "has_reminder";
        public static final String IMPORTANCE = "importance";
        public static final int IMPORTANCE_HIGH = 2;
        public static final int IMPORTANCE_LOW = 0;
        public static final int IMPORTANCE_NORMAL = 1;
        public static final String LAST_DATE = "lastDate";
        public static final String LAST_SYNCED = "lastSynced";
        public static final String ORDINAL_DATE = "ordinal_date";
        public static final String ORIGINAL_ALL_DAY = "originalAllDay";
        public static final String ORIGINAL_ID = "original_id";
        public static final String ORIGINAL_INSTANCE_TIME = "originalInstanceTime";
        public static final String ORIGINAL_SYNC_ID = "original_sync_id";
        public static final String PARENT_TASKID = "parent_Id";
        public static final String RDATE = "rdate";
        public static final String RRULE = "rrule";
        public static final String SENSITIVITY = "sensitivity";
        public static final int SENSITIVITY_CONFIDENTIAL = 3;
        public static final int SENSITIVITY_NORMAL = 0;
        public static final int SENSITIVITY_PERSONAL = 1;
        public static final int SENSITIVITY_PRIVATE = 2;
        public static final String START_DATE = "start_date";
        public static final String SUBJECT = "subject";
        public static final String SUB_ORDINAL_DATE = "sub_ordinal_date";
        public static final String SYNC_DATA1 = "sync_data1";
        public static final String SYNC_DATA10 = "sync_data10";
        public static final String SYNC_DATA2 = "sync_data2";
        public static final String SYNC_DATA3 = "sync_data3";
        public static final String SYNC_DATA4 = "sync_data4";
        public static final String SYNC_DATA5 = "sync_data5";
        public static final String SYNC_DATA6 = "sync_data6";
        public static final String SYNC_DATA7 = "sync_data7";
        public static final String SYNC_DATA8 = "sync_data8";
        public static final String SYNC_DATA9 = "sync_data9";
        public static final String TASKLIST_ID = "tasklist_id";
        public static final String TASK_TIMEZONE = "taskTimezone";
        public static final String UTC_DUE_DATE = "utc_due_date";
        public static final String UTC_START_DATE = "utc_start_date";
    }

    private TasksContract() {
    }

    public static final class TaskLists implements BaseColumns, SyncColumns, TaskListsColumns {
        public static final String DEFAULT_SORT_ORDER = "tasklist_displayName ASC";
        public static final String NAME = "name";
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.tasks.provider/tasklists");
        public static final String LOCATION = "location";
        public static final String[] SYNC_WRITABLE_COLUMNS = {"account_name", "account_type", "_sync_id", "dirty", "ownerAccount", "maxReminders", "allowedReminders", "canModifyTimeZone", "canOrganizerRespond", LOCATION, TaskListsColumns.TASKLIST_TIME_ZONE, TaskListsColumns.TASKLIST_ACCESS_LEVEL, "deleted", TaskSyncColumns._SYNC1, TaskSyncColumns._SYNC2, TaskSyncColumns._SYNC3, TaskSyncColumns._SYNC4, TaskSyncColumns._SYNC5, TaskSyncColumns._SYNC6, TaskSyncColumns._SYNC7, TaskSyncColumns._SYNC8, TaskSyncColumns._SYNC9, TaskSyncColumns._SYNC10};

        private TaskLists() {
        }
    }

    public static final class Tasks implements BaseColumns, SyncColumns, TasksColumns, TaskListsColumns {
        public static final String DEFAULT_SORT_ORDER = "due_date ASC";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TasksContract.CONTENT_URI, "tasks");
        public static final Uri CONTENT_EXCEPTION_URI = Uri.parse("content://com.sonymobile.tasks.provider/exception");

        private Tasks() {
        }
    }

    public static final class Reminders implements BaseColumns, RemindersColumns, TasksColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.tasks.provider/reminders");

        private Reminders() {
        }
    }

    public static final class TasksAlerts implements BaseColumns, TasksAlertsColumns, TasksColumns, TaskListsColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.tasks.provider/tasks_alerts");
        public static final String TABLE_NAME = "TasksAlerts";

        private TasksAlerts() {
        }
    }

    public static final class Colors implements ColorsColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.tasks.provider/colors");
        public static final String TABLE_NAME = "Colors";

        private Colors() {
        }
    }

    public static final class ExtendedProperties implements BaseColumns, ExtendedPropertiesColumns, TasksColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.tasks.provider/extendedproperties");
        public static final String TABLE_NAME = "ExtendedProperties";

        private ExtendedProperties() {
        }
    }

    public static final class SyncState implements SyncStateContract.Columns {
        private static final String CONTENT_DIRECTORY = "syncstate";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TasksContract.CONTENT_URI, CONTENT_DIRECTORY);

        private SyncState() {
        }
    }
}
