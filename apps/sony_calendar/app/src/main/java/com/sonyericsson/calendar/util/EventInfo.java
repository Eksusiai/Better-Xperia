package com.sonyericsson.calendar.util;

import android.database.Cursor;
import android.text.TextUtils;
import java.io.Serializable;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class EventInfo {
    public static final int ACCESS_LEVEL_DELETE = 1;
    public static final int ACCESS_LEVEL_EDIT = 2;
    public static final int ACCESS_LEVEL_NONE = 0;
    private static final int EVENT_INDEX_ACCESS_LEVEL = 11;
    private static final int EVENT_INDEX_ALL_DAY = 3;
    protected static final int EVENT_INDEX_BEGIN = 18;
    protected static final int EVENT_INDEX_CALENDAR_COLOR = 12;
    private static final int EVENT_INDEX_CALENDAR_ID = 4;
    private static final int EVENT_INDEX_DESCRIPTION = 8;
    protected static final int EVENT_INDEX_END = 19;
    protected static final int EVENT_INDEX_EVENT_COLOR = 17;
    private static final int EVENT_INDEX_EVENT_LOCATION = 9;
    protected static final int EVENT_INDEX_EVENT_TIMEZONE = 7;
    private static final int EVENT_INDEX_GUESTS_CAN_MODIFY = 14;
    public static final int EVENT_INDEX_HAS_ALARM = 10;
    protected static final int EVENT_INDEX_HAS_ATTENDEE_DATA = 13;
    private static final int EVENT_INDEX_ID = 0;
    private static final int EVENT_INDEX_ORGANIZER = 15;
    private static final int EVENT_INDEX_RRULE = 2;
    protected static final int EVENT_INDEX_SYNC_ACCOUNT_TYPE = 16;
    protected static final int EVENT_INDEX_SYNC_ID = 6;
    private static final int EVENT_INDEX_TITLE = 1;
    public int accessLevel;
    public int allDay;
    public long begin;
    public int calendarId;
    public int color;
    public String description;
    public long end;
    public int endDay;
    public String eventLocation;
    public String eventOrganizer;
    public int guestCanModify;
    public int hasAlarm;
    public boolean hasEventColor;
    public long id;
    public long instanceId;
    public boolean isAlarmEvent;
    public boolean isLunarEvent;
    public long localBegin;
    public long localEnd;
    public String ownerAccount;
    public String rrule;
    public int selfAttendeeStatus;
    public int startDay;
    public String title;
    public int visibility;

    public EventInfo() {
        this.isLunarEvent = false;
        this.isAlarmEvent = false;
    }

    public EventInfo(EventInfo eventInfo) {
        this.isLunarEvent = false;
        this.isAlarmEvent = false;
        this.instanceId = eventInfo.instanceId;
        this.title = eventInfo.title;
        this.eventLocation = eventInfo.eventLocation;
        this.allDay = eventInfo.allDay;
        this.hasAlarm = eventInfo.hasAlarm;
        this.color = eventInfo.color;
        this.rrule = eventInfo.rrule;
        this.begin = eventInfo.begin;
        this.localBegin = eventInfo.localBegin;
        this.end = eventInfo.end;
        this.id = eventInfo.id;
        this.startDay = eventInfo.startDay;
        this.endDay = eventInfo.endDay;
        this.selfAttendeeStatus = eventInfo.selfAttendeeStatus;
        this.calendarId = eventInfo.calendarId;
        this.guestCanModify = eventInfo.guestCanModify;
        this.visibility = eventInfo.visibility;
        this.accessLevel = eventInfo.accessLevel;
        this.ownerAccount = eventInfo.ownerAccount;
        this.eventOrganizer = eventInfo.eventOrganizer;
        this.hasEventColor = eventInfo.hasEventColor;
        this.isLunarEvent = eventInfo.isLunarEvent;
        this.description = eventInfo.description;
        this.isAlarmEvent = eventInfo.isAlarmEvent;
    }

    public void setAccessLevel() {
        this.accessLevel = 1;
        String str = this.ownerAccount;
        if (this.visibility < 500) {
            this.accessLevel = 0;
            return;
        }
        if (this.guestCanModify == 1) {
            this.accessLevel = 2;
        } else {
            if (TextUtils.isEmpty(str) || !str.equalsIgnoreCase(this.eventOrganizer)) {
                return;
            }
            this.accessLevel = 2;
        }
    }

    public EventInfo(Cursor cursor, boolean z) {
        this.isLunarEvent = false;
        this.isAlarmEvent = false;
        this.title = cursor.getString(1);
        this.eventLocation = cursor.getString(9);
        this.allDay = cursor.getInt(3);
        this.hasAlarm = cursor.getInt(10);
        this.rrule = cursor.getString(2);
        this.begin = cursor.getLong(18);
        this.end = cursor.getLong(19);
        this.id = cursor.getInt(0);
        this.calendarId = cursor.getInt(4);
        this.guestCanModify = cursor.getInt(14);
        this.visibility = cursor.getInt(11);
        this.eventOrganizer = cursor.getString(15);
        this.description = cursor.getString(8);
        this.isLunarEvent = z;
        setAccessLevel();
    }

    public int hashCode() {
        int i = (this.color + 31) * 31;
        long j = this.end;
        int i2 = (i + ((int) (j ^ (j >>> 32)))) * 31;
        String str = this.eventLocation;
        int iHashCode = str == null ? 0 : str.hashCode();
        long j2 = this.id;
        int i3 = (((i2 + iHashCode) * 31) + ((int) (j2 ^ (j2 >>> 32)))) * 31;
        long j3 = this.instanceId;
        int i4 = (i3 + ((int) (j3 ^ (j3 >>> 32)))) * 31;
        int i5 = this.isLunarEvent ? 1231 : 1237;
        long j4 = this.localBegin;
        int i6 = (((i4 + i5) * 31) + ((int) (j4 ^ (j4 >>> 32)))) * 31;
        String str2 = this.title;
        return i6 + (str2 != null ? str2.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EventInfo eventInfo = (EventInfo) obj;
        if (this.color != eventInfo.color || this.end != eventInfo.end) {
            return false;
        }
        String str = this.eventLocation;
        if (str == null) {
            if (eventInfo.eventLocation != null) {
                return false;
            }
        } else if (!str.equals(eventInfo.eventLocation)) {
            return false;
        }
        if (this.id != eventInfo.id || this.instanceId != eventInfo.instanceId || this.isLunarEvent != eventInfo.isLunarEvent || this.localBegin != eventInfo.localBegin) {
            return false;
        }
        String str2 = this.title;
        if (str2 == null) {
            if (eventInfo.title != null) {
                return false;
            }
        } else if (!str2.equals(eventInfo.title)) {
            return false;
        }
        return true;
    }

    public static class EventInfoComparator implements Comparator<EventInfo>, Serializable {
        private static final long serialVersionUID = 1;
        private final boolean mIsListInMonthView;
        private final boolean mShouldPutAllDayFirst;

        public EventInfoComparator(boolean z, boolean z2) {
            this.mShouldPutAllDayFirst = z;
            this.mIsListInMonthView = z2;
        }

        @Override // java.util.Comparator
        public int compare(EventInfo eventInfo, EventInfo eventInfo2) {
            int iCompare = Integer.compare(eventInfo.startDay, eventInfo2.startDay);
            if (iCompare != 0 && !this.mIsListInMonthView) {
                return iCompare;
            }
            int iCompare2 = Integer.compare(eventInfo2.allDay, eventInfo.allDay);
            if (this.mShouldPutAllDayFirst && iCompare2 != 0) {
                return iCompare2;
            }
            int iCompare3 = Long.compare(eventInfo.localBegin, eventInfo2.localBegin);
            if (iCompare3 != 0) {
                return iCompare3;
            }
            return (eventInfo.title != null ? eventInfo.title : "").compareTo(eventInfo2.title != null ? eventInfo2.title : "");
        }
    }
}
