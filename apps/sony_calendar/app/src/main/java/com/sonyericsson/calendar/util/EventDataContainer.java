package com.sonyericsson.calendar.util;

import android.database.Cursor;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes.dex */
public class EventDataContainer extends EventInfo {
    public int eventColor;
    public String eventTimezone;
    public int hasAttendeeData;
    public long startDayMillis;
    public String syncAccountType;
    public String syncId;

    public EventDataContainer(Cursor cursor, boolean z) {
        super(cursor, z);
        int i = cursor.getInt(17);
        this.eventColor = i;
        this.hasEventColor = i != 0;
        this.color = this.hasEventColor ? this.eventColor : cursor.getInt(12);
        this.syncId = cursor.getString(6);
        this.eventTimezone = cursor.getString(7);
        this.hasAttendeeData = cursor.getInt(13);
        this.syncAccountType = cursor.getString(16);
        this.startDayMillis = cursor.getLong(cursor.getColumnIndex(LunarContract.EventsColumns.DTSTART));
    }

    @Override // com.sonyericsson.calendar.util.EventInfo
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // com.sonyericsson.calendar.util.EventInfo
    public int hashCode() {
        return super.hashCode();
    }
}
