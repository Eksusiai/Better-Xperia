package com.sonymobile.lunar.lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/* JADX INFO: loaded from: classes2.dex */
public final class LunarContract {
    public static final String ACCOUNT_TYPE_LOCAL = "LOCAL";
    public static final String ACTION_EVENT_REMINDER = "android.intent.action.EVENT_REMINDER";
    public static final String ACTION_HANDLE_CUSTOM_EVENT = "android.provider.calendar.action.HANDLE_CUSTOM_EVENT";
    public static final String AUTHORITY = "com.sonymobile.lunar";
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar");
    public static final String EXTRA_CUSTOM_APP_URI = "customAppUri";
    public static final String EXTRA_EVENT_ALL_DAY = "allDay";
    public static final String EXTRA_EVENT_BEGIN_TIME = "beginTime";
    public static final String EXTRA_EVENT_END_TIME = "endTime";
    private static final String TAG = "Calendar";

    public interface AttendeesColumns {
        public static final String ATTENDEE_EMAIL = "attendeeEmail";
        public static final String ATTENDEE_IDENTITY = "attendeeIdentity";
        public static final String ATTENDEE_ID_NAMESPACE = "attendeeIdNamespace";
        public static final String ATTENDEE_NAME = "attendeeName";
        public static final String ATTENDEE_RELATIONSHIP = "attendeeRelationship";
        public static final String ATTENDEE_STATUS = "attendeeStatus";
        public static final int ATTENDEE_STATUS_ACCEPTED = 1;
        public static final int ATTENDEE_STATUS_DECLINED = 2;
        public static final int ATTENDEE_STATUS_INVITED = 3;
        public static final int ATTENDEE_STATUS_NONE = 0;
        public static final int ATTENDEE_STATUS_TENTATIVE = 4;
        public static final String ATTENDEE_TYPE = "attendeeType";
        public static final String EVENT_ID = "event_id";
        public static final int RELATIONSHIP_ATTENDEE = 1;
        public static final int RELATIONSHIP_NONE = 0;
        public static final int RELATIONSHIP_ORGANIZER = 2;
        public static final int RELATIONSHIP_PERFORMER = 3;
        public static final int RELATIONSHIP_SPEAKER = 4;
        public static final int TYPE_NONE = 0;
        public static final int TYPE_OPTIONAL = 2;
        public static final int TYPE_REQUIRED = 1;
        public static final int TYPE_RESOURCE = 3;
    }

    protected interface CalendarAlertsColumns {
        public static final String ALARM_TIME = "alarmTime";
        public static final String BEGIN = "begin";
        public static final String CREATION_TIME = "creationTime";
        public static final String DEFAULT_SORT_ORDER = "begin ASC,title ASC";
        public static final String END = "end";
        public static final String EVENT_ID = "event_id";
        public static final String MINUTES = "minutes";
        public static final String NOTIFY_TIME = "notifyTime";
        public static final String RECEIVED_TIME = "receivedTime";
        public static final String STATE = "state";
        public static final int STATE_DISMISSED = 2;
        public static final int STATE_FIRED = 1;
        public static final int STATE_SCHEDULED = 0;
    }

    public interface CalendarCacheColumns {
        public static final String KEY = "key";
        public static final String VALUE = "value";
    }

    public interface CalendarColumns {
        public static final String ALLOWED_ATTENDEE_TYPES = "allowedAttendeeTypes";
        public static final String ALLOWED_AVAILABILITY = "allowedAvailability";
        public static final String ALLOWED_REMINDERS = "allowedReminders";
        public static final String CALENDAR_ACCESS_LEVEL = "calendar_access_level";
        public static final String CALENDAR_COLOR = "calendar_color";
        public static final String CALENDAR_COLOR_KEY = "calendar_color_index";
        public static final String CALENDAR_DISPLAY_NAME = "calendar_displayName";
        public static final String CALENDAR_TIME_ZONE = "calendar_timezone";
        public static final int CAL_ACCESS_CONTRIBUTOR = 500;
        public static final int CAL_ACCESS_EDITOR = 600;
        public static final int CAL_ACCESS_FREEBUSY = 100;
        public static final int CAL_ACCESS_NONE = 0;
        public static final int CAL_ACCESS_OVERRIDE = 400;
        public static final int CAL_ACCESS_OWNER = 700;
        public static final int CAL_ACCESS_READ = 200;
        public static final int CAL_ACCESS_RESPOND = 300;
        public static final int CAL_ACCESS_ROOT = 800;
        public static final String CAN_MODIFY_TIME_ZONE = "canModifyTimeZone";
        public static final String CAN_ORGANIZER_RESPOND = "canOrganizerRespond";
        public static final String IS_PRIMARY = "isPrimary";
        public static final String MAX_REMINDERS = "maxReminders";
        public static final String OWNER_ACCOUNT = "ownerAccount";
        public static final String SYNC_EVENTS = "sync_events";
        public static final String VISIBLE = "visible";
    }

    protected interface CalendarMetaDataColumns {
        public static final String LOCAL_TIMEZONE = "localTimezone";
        public static final String MAX_EVENTDAYS = "maxEventDays";
        public static final String MAX_INSTANCE = "maxInstance";
        public static final String MIN_EVENTDAYS = "minEventDays";
        public static final String MIN_INSTANCE = "minInstance";
    }

    protected interface CalendarSyncColumns {
        public static final String CAL_SYNC1 = "cal_sync1";
        public static final String CAL_SYNC10 = "cal_sync10";
        public static final String CAL_SYNC2 = "cal_sync2";
        public static final String CAL_SYNC3 = "cal_sync3";
        public static final String CAL_SYNC4 = "cal_sync4";
        public static final String CAL_SYNC5 = "cal_sync5";
        public static final String CAL_SYNC6 = "cal_sync6";
        public static final String CAL_SYNC7 = "cal_sync7";
        public static final String CAL_SYNC8 = "cal_sync8";
        public static final String CAL_SYNC9 = "cal_sync9";
    }

    protected interface ColorsColumns extends BaseColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String COLOR = "color";
        public static final String COLOR_KEY = "color_index";
        public static final String COLOR_TYPE = "color_type";
        public static final String DATA = "data";
        public static final int TYPE_CALENDAR = 0;
        public static final int TYPE_EVENT = 1;
    }

    protected interface EventDaysColumns {
        public static final String ENDDAY = "endDay";
        public static final String STARTDAY = "startDay";
    }

    public interface EventsColumns {
        public static final int ACCESS_CONFIDENTIAL = 1;
        public static final int ACCESS_DEFAULT = 0;
        public static final String ACCESS_LEVEL = "accessLevel";
        public static final int ACCESS_PRIVATE = 2;
        public static final int ACCESS_PUBLIC = 3;
        public static final String ALL_DAY = "allDay";
        public static final String AVAILABILITY = "availability";
        public static final int AVAILABILITY_BUSY = 0;
        public static final int AVAILABILITY_FREE = 1;
        public static final int AVAILABILITY_TENTATIVE = 2;
        public static final String CALENDAR_ID = "calendar_id";
        public static final String CAN_INVITE_OTHERS = "canInviteOthers";
        public static final String COMMENT = "comment";
        public static final String CUSTOM_APP_PACKAGE = "customAppPackage";
        public static final String CUSTOM_APP_URI = "customAppUri";
        public static final String DESCRIPTION = "description";
        public static final String DISPLAY_COLOR = "displayColor";
        public static final String DTEND = "dtend";
        public static final String DTSTART = "dtstart";
        public static final String DURATION = "duration";
        public static final String EVENT_COLOR = "eventColor";
        public static final String EVENT_COLOR_KEY = "eventColor_index";
        public static final String EVENT_END_TIMEZONE = "eventEndTimezone";
        public static final String EVENT_LOCATION = "eventLocation";
        public static final String EVENT_TIMEZONE = "eventTimezone";
        public static final String EXDATE = "exdate";
        public static final String EXRULE = "exrule";
        public static final String GUESTS_CAN_INVITE_OTHERS = "guestsCanInviteOthers";
        public static final String GUESTS_CAN_MODIFY = "guestsCanModify";
        public static final String GUESTS_CAN_SEE_GUESTS = "guestsCanSeeGuests";
        public static final String HAS_ALARM = "hasAlarm";
        public static final String HAS_ATTENDEE_DATA = "hasAttendeeData";
        public static final String HAS_EXTENDED_PROPERTIES = "hasExtendedProperties";
        public static final String IS_ORGANIZER = "isOrganizer";
        public static final String LAST_DATE = "lastDate";
        public static final String LAST_SYNCED = "lastSynced";
        public static final String ORGANIZER = "organizer";
        public static final String ORIGINAL_ALL_DAY = "originalAllDay";
        public static final String ORIGINAL_ID = "original_id";
        public static final String ORIGINAL_INSTANCE_TIME = "originalInstanceTime";
        public static final String ORIGINAL_SYNC_ID = "original_sync_id";
        public static final String RDATE = "rdate";
        public static final String RRULE = "rrule";
        public static final String SELF_ATTENDEE_STATUS = "selfAttendeeStatus";
        public static final String STATUS = "eventStatus";
        public static final int STATUS_CANCELED = 2;
        public static final int STATUS_CONFIRMED = 1;
        public static final int STATUS_TENTATIVE = 0;
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
        public static final String TITLE = "title";
        public static final String UID_2445 = "uid2445";
    }

    protected interface EventsRawTimesColumns {
        public static final String DTEND_2445 = "dtend2445";
        public static final String DTSTART_2445 = "dtstart2445";
        public static final String EVENT_ID = "event_id";
        public static final String LAST_DATE_2445 = "lastDate2445";
        public static final String ORIGINAL_INSTANCE_TIME_2445 = "originalInstanceTime2445";
    }

    protected interface ExtendedPropertiesColumns {
        public static final String EVENT_ID = "event_id";
        public static final String NAME = "name";
        public static final String VALUE = "value";
    }

    protected interface RemindersColumns {
        public static final String EVENT_ID = "event_id";
        public static final String METHOD = "method";
        public static final int METHOD_ALARM = 4;
        public static final int METHOD_ALERT = 1;
        public static final int METHOD_DEFAULT = 0;
        public static final int METHOD_EMAIL = 2;
        public static final int METHOD_SMS = 3;
        public static final String MINUTES = "minutes";
        public static final int MINUTES_DEFAULT = -1;
    }

    protected interface SyncColumns extends CalendarSyncColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String CAN_PARTIALLY_UPDATE = "canPartiallyUpdate";
        public static final String DELETED = "deleted";
        public static final String DIRTY = "dirty";
        public static final String _SYNC_ID = "_sync_id";
    }

    private LunarContract() {
    }

    public static final class Calendars implements BaseColumns, SyncColumns, CalendarColumns {
        public static final String DEFAULT_SORT_ORDER = "calendar_displayName";
        public static final String NAME = "name";
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/calendars");
        public static final String CALENDAR_LOCATION = "calendar_location";
        public static final String[] SYNC_WRITABLE_COLUMNS = {"account_name", "account_type", "_sync_id", "dirty", "ownerAccount", "maxReminders", "allowedReminders", "canModifyTimeZone", "canOrganizerRespond", "canPartiallyUpdate", CALENDAR_LOCATION, CalendarColumns.CALENDAR_TIME_ZONE, CalendarColumns.CALENDAR_ACCESS_LEVEL, "deleted", CalendarSyncColumns.CAL_SYNC1, CalendarSyncColumns.CAL_SYNC2, CalendarSyncColumns.CAL_SYNC3, CalendarSyncColumns.CAL_SYNC4, CalendarSyncColumns.CAL_SYNC5, CalendarSyncColumns.CAL_SYNC6, CalendarSyncColumns.CAL_SYNC7, CalendarSyncColumns.CAL_SYNC8, CalendarSyncColumns.CAL_SYNC9, CalendarSyncColumns.CAL_SYNC10};

        private Calendars() {
        }
    }

    public static final class Attendees implements BaseColumns, AttendeesColumns, EventsColumns {
        private static final String ATTENDEES_WHERE = "event_id=?";
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/attendees");

        private Attendees() {
        }

        public static final Cursor query(ContentResolver contentResolver, long j, String[] strArr) {
            return contentResolver.query(CONTENT_URI, strArr, ATTENDEES_WHERE, new String[]{Long.toString(j)}, null);
        }
    }

    public static final class Events implements BaseColumns, SyncColumns, EventsColumns, CalendarColumns {
        private static final String DEFAULT_SORT_ORDER = "";
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/events");
        public static final Uri CONTENT_EXCEPTION_URI = Uri.parse("content://com.sonymobile.lunar/exception");
        public static String[] PROVIDER_WRITABLE_COLUMNS = {"account_name", "account_type", CalendarSyncColumns.CAL_SYNC1, CalendarSyncColumns.CAL_SYNC2, CalendarSyncColumns.CAL_SYNC3, CalendarSyncColumns.CAL_SYNC4, CalendarSyncColumns.CAL_SYNC5, CalendarSyncColumns.CAL_SYNC6, CalendarSyncColumns.CAL_SYNC7, CalendarSyncColumns.CAL_SYNC8, CalendarSyncColumns.CAL_SYNC9, CalendarSyncColumns.CAL_SYNC10, "allowedReminders", "allowedAttendeeTypes", "allowedAvailability", CalendarColumns.CALENDAR_ACCESS_LEVEL, CalendarColumns.CALENDAR_COLOR, CalendarColumns.CALENDAR_TIME_ZONE, "canModifyTimeZone", "canOrganizerRespond", "calendar_displayName", "canPartiallyUpdate", CalendarColumns.SYNC_EVENTS, "visible"};
        public static final String[] SYNC_WRITABLE_COLUMNS = {"_sync_id", "dirty", "sync_data1", "sync_data2", "sync_data3", "sync_data4", "sync_data5", "sync_data6", "sync_data7", "sync_data8", "sync_data9", "sync_data10"};

        private Events() {
        }
    }

    public static final class Instances implements BaseColumns, EventsColumns, CalendarColumns {
        public static final String BEGIN = "begin";
        private static final String DEFAULT_SORT_ORDER = "begin ASC";
        public static final String END = "end";
        public static final String END_DAY = "endDay";
        public static final String END_MINUTE = "endMinute";
        public static final String EVENT_ID = "event_id";
        public static final String START_DAY = "startDay";
        public static final String START_MINUTE = "startMinute";
        private static final String WHERE_CALENDARS_SELECTED = "visible=?";
        private static final String[] WHERE_CALENDARS_ARGS = {"1"};
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/instances/when");
        public static final Uri CONTENT_BY_DAY_URI = Uri.parse("content://com.sonymobile.lunar/instances/whenbyday");
        public static final Uri CONTENT_SEARCH_URI = Uri.parse("content://com.sonymobile.lunar/instances/search");
        public static final Uri CONTENT_SEARCH_BY_DAY_URI = Uri.parse("content://com.sonymobile.lunar/instances/searchbyday");

        private Instances() {
        }

        public static final Cursor query(ContentResolver contentResolver, String[] strArr, long j, long j2) {
            Uri.Builder builderBuildUpon = CONTENT_URI.buildUpon();
            ContentUris.appendId(builderBuildUpon, j);
            ContentUris.appendId(builderBuildUpon, j2);
            return contentResolver.query(builderBuildUpon.build(), strArr, WHERE_CALENDARS_SELECTED, WHERE_CALENDARS_ARGS, DEFAULT_SORT_ORDER);
        }

        public static final Cursor query(ContentResolver contentResolver, String[] strArr, long j, long j2, String str) {
            Uri.Builder builderBuildUpon = CONTENT_SEARCH_URI.buildUpon();
            ContentUris.appendId(builderBuildUpon, j);
            ContentUris.appendId(builderBuildUpon, j2);
            return contentResolver.query(builderBuildUpon.appendPath(str).build(), strArr, WHERE_CALENDARS_SELECTED, WHERE_CALENDARS_ARGS, DEFAULT_SORT_ORDER);
        }
    }

    public static final class CalendarCache implements CalendarCacheColumns {
        public static final String KEY_TIMEZONE_INSTANCES = "timezoneInstances";
        public static final String KEY_TIMEZONE_INSTANCES_PREVIOUS = "timezoneInstancesPrevious";
        public static final String KEY_TIMEZONE_TYPE = "timezoneType";
        public static final String TIMEZONE_TYPE_AUTO = "auto";
        public static final String TIMEZONE_TYPE_HOME = "home";
        public static final Uri URI = Uri.parse("content://com.sonymobile.lunar/properties");

        private CalendarCache() {
        }
    }

    public static final class CalendarMetaData implements CalendarMetaDataColumns, BaseColumns {
        private CalendarMetaData() {
        }
    }

    public static final class EventDays implements EventDaysColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/instances/groupbyday");
        private static final String SELECTION = "selected=1";

        private EventDays() {
        }

        public static final Cursor query(ContentResolver contentResolver, int i, int i2, String[] strArr) {
            if (i2 < 1) {
                return null;
            }
            Uri.Builder builderBuildUpon = CONTENT_URI.buildUpon();
            ContentUris.appendId(builderBuildUpon, i);
            ContentUris.appendId(builderBuildUpon, (i2 + i) - 1);
            return contentResolver.query(builderBuildUpon.build(), strArr, SELECTION, null, "startDay");
        }
    }

    public static final class Reminders implements BaseColumns, RemindersColumns, EventsColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/reminders");
        private static final String REMINDERS_WHERE = "event_id=?";

        private Reminders() {
        }

        public static final Cursor query(ContentResolver contentResolver, long j, String[] strArr) {
            return contentResolver.query(CONTENT_URI, strArr, REMINDERS_WHERE, new String[]{Long.toString(j)}, null);
        }
    }

    public static final class CalendarAlerts implements BaseColumns, CalendarAlertsColumns, EventsColumns, CalendarColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/calendar_alerts");
        public static final Uri CONTENT_URI_BY_INSTANCE = Uri.parse("content://com.sonymobile.lunar/calendar_alerts/by_instance");
        private static final boolean DEBUG = false;
        private static final String SORT_ORDER_ALARMTIME_ASC = "alarmTime ASC";
        public static final String TABLE_NAME = "CalendarAlerts";
        private static final String WHERE_ALARM_EXISTS = "event_id=? AND begin=? AND alarmTime=?";
        private static final String WHERE_FINDNEXTALARMTIME = "alarmTime>=?";
        private static final String WHERE_RESCHEDULE_MISSED_ALARMS = "state=0 AND alarmTime<? AND alarmTime>? AND end>=?";

        private CalendarAlerts() {
        }

        public static final Uri insert(ContentResolver contentResolver, long j, long j2, long j3, long j4, int i) {
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
            return contentResolver.insert(CONTENT_URI, contentValues);
        }

        /* JADX WARN: Code duplicated, block: B:12:0x002f  */
        public static final long findNextAlarmTime(ContentResolver contentResolver, long j) {
            long j2;
            Cursor cursorQuery = contentResolver.query(CONTENT_URI, new String[]{"alarmTime"}, WHERE_FINDNEXTALARMTIME, new String[]{Long.toString(j)}, SORT_ORDER_ALARMTIME_ASC);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        j2 = cursorQuery.getLong(0);
                    } else {
                        j2 = -1;
                    }
                } finally {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                }
            } else {
                j2 = -1;
            }
            return j2;
        }

        public static final void rescheduleMissedAlarms(ContentResolver contentResolver, Context context, AlarmManager alarmManager) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            Cursor cursorQuery = contentResolver.query(CONTENT_URI, new String[]{"alarmTime"}, WHERE_RESCHEDULE_MISSED_ALARMS, new String[]{Long.toString(jCurrentTimeMillis), Long.toString(jCurrentTimeMillis - 86400000), Long.toString(jCurrentTimeMillis)}, SORT_ORDER_ALARMTIME_ASC);
            if (cursorQuery == null) {
                return;
            }
            long j = -1;
            while (cursorQuery.moveToNext()) {
                try {
                    long j2 = cursorQuery.getLong(0);
                    if (j != j2) {
                        scheduleAlarm(context, alarmManager, j2);
                        j = j2;
                    }
                } finally {
                    cursorQuery.close();
                }
            }
        }

        public static void scheduleAlarm(Context context, AlarmManager alarmManager, long j) {
            if (alarmManager == null) {
                alarmManager = (AlarmManager) context.getSystemService("alarm");
            }
            Intent intent = new Intent(LunarContract.ACTION_EVENT_REMINDER);
            intent.setComponent(new ComponentName(com.sonymobile.calendar.BuildConfig.APPLICATION_ID, "com.sonymobile.calendar.alerts.AlertReceiver"));
            intent.setData(ContentUris.withAppendedId(LunarContract.CONTENT_URI, j));
            intent.putExtra("alarmTime", j);
            alarmManager.set(0, j, PendingIntent.getBroadcast(context, 0, intent, 67108864));
        }

        public static final boolean alarmExists(ContentResolver contentResolver, long j, long j2, long j3) {
            boolean z = false;
            Cursor cursorQuery = contentResolver.query(CONTENT_URI, new String[]{"alarmTime"}, WHERE_ALARM_EXISTS, new String[]{Long.toString(j), Long.toString(j2), Long.toString(j3)}, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.getCount() > 0) {
                        z = true;
                    }
                } finally {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                }
            }
            return z;
        }
    }

    public static final class Colors implements ColorsColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/colors");
        public static final String TABLE_NAME = "Colors";

        private Colors() {
        }
    }

    public static final class ExtendedProperties implements BaseColumns, ExtendedPropertiesColumns, EventsColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://com.sonymobile.lunar/extendedproperties");

        private ExtendedProperties() {
        }
    }

    public static final class EventsRawTimes implements BaseColumns, EventsRawTimesColumns {
        private EventsRawTimes() {
        }
    }
}
