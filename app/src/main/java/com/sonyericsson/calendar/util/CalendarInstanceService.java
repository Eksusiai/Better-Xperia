package com.sonyericsson.calendar.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.SparseIntArray;
import com.sonymobile.calendar.CalendarItem;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class CalendarInstanceService extends AsyncServiceBase {
    public static final int DEFAULT_COLOR_ID = -1;
    private static final String IS_PRIMARY = "\"primary\"";
    private static final String[] PROJECTION = {"_id", "account_name", "ownerAccount", "calendar_displayName", LunarContract.CalendarColumns.CALENDAR_COLOR, "visible", LunarContract.CalendarColumns.SYNC_EVENTS, "account_type", "(account_name=ownerAccount) AS \"primary\""};
    private static final String SELECTION = "sync_events=1";
    private static final String SELECTION_WRITABLE = "calendar_access_level>=500 AND sync_events=1";
    private static CalendarInstanceService instance;
    private boolean calendarColorChanged;
    Map<Long, CalendarItem> mCalendars = new HashMap();
    private Map<Long, Integer> calendarColors = new HashMap();
    private SparseIntArray mColorKeyMap = new SparseIntArray();
    private long mLocalCalendarId = 1;

    private CalendarInstanceService() {
    }

    public int getColorKey(int i) {
        return this.mColorKeyMap.get(i, -1);
    }

    public void updateColorKeyMap(SparseIntArray sparseIntArray) {
        this.mColorKeyMap = sparseIntArray;
    }

    public static CalendarInstanceService getInstance() {
        if (instance == null) {
            instance = new CalendarInstanceService();
        }
        return instance;
    }

    public void updateVisibility(Context context, boolean z, long j, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("visible", Integer.valueOf(z ? 1 : 0));
        performAsyncUpdate(context, null, ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, j), contentValues, null, null, 0L);
        if (LunarAvailabilityManager.isLunarAvailable(context) && str.equals("LOCAL")) {
            performAsyncUpdate(context, null, ContentUris.withAppendedId(LunarContract.Calendars.CONTENT_URI, 1L), contentValues, null, null, 0L);
        }
    }

    public void updateEventColor(Context context, int i, long j, boolean z, String str, String str2) {
        Uri uri;
        ContentValues contentValues = new ContentValues();
        contentValues.put(LunarContract.EventsColumns.EVENT_COLOR, Integer.valueOf(i));
        contentValues.put("dirty", (Integer) 0);
        if (z) {
            uri = LunarContract.Events.CONTENT_URI;
        } else {
            uri = CalendarContract.Events.CONTENT_URI;
        }
        performAsyncUpdate(context, null, DatabaseUtils.asSyncAdapter(ContentUris.withAppendedId(uri, j), str, str2), contentValues, null, null, 0L);
    }

    public void updateCalendarColor(Context context, int i, boolean z, String str, String str2, long j) {
        Uri uriAsSyncAdapter = DatabaseUtils.asSyncAdapter(CalendarContract.Calendars.CONTENT_URI, str, str2);
        ContentValues contentValues = new ContentValues();
        contentValues.put(LunarContract.CalendarColumns.CALENDAR_COLOR, Integer.valueOf(i));
        String[] strArr = {Long.toString(j)};
        this.calendarColorChanged = true;
        performAsyncUpdate(context, null, uriAsSyncAdapter, contentValues, "_id=?", strArr, 0L);
    }

    public void updateCalendarColorByKey(Context context, int i, boolean z, long j) {
        if (this.mColorKeyMap == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(LunarContract.CalendarColumns.CALENDAR_COLOR_KEY, Integer.valueOf(this.mColorKeyMap.get(i)));
        Uri uriWithAppendedId = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, j);
        this.calendarColorChanged = true;
        performAsyncUpdate(context, null, uriWithAppendedId, contentValues, null, null, 0L);
    }

    public void requestLoad(Context context, final IAsyncServiceResultHandler iAsyncServiceResultHandler, boolean z) {
        performAsyncQuery(context, new AsyncServiceBase.OnReadyHandler() { // from class: com.sonyericsson.calendar.util.CalendarInstanceService.1
            @Override // com.sonyericsson.calendar.util.AsyncServiceBase.OnReadyHandler
            public void onReady(boolean z2) {
                iAsyncServiceResultHandler.onResult(Boolean.valueOf(z2), 0);
            }
        }, CalendarContract.Calendars.CONTENT_URI, PROJECTION, z ? SELECTION_WRITABLE : SELECTION, null, null);
    }

    public Map<Long, CalendarItem> getCalendars() {
        return this.mCalendars;
    }

    public CalendarItem getCalendarById(long j) {
        return this.mCalendars.get(Long.valueOf(j));
    }

    @Override // com.sonyericsson.calendar.util.AsyncServiceBase
    protected boolean handleResultData(Cursor cursor) {
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow("_id");
        int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("calendar_displayName");
        int columnIndexOrThrow3 = cursor.getColumnIndexOrThrow(LunarContract.CalendarColumns.CALENDAR_COLOR);
        int columnIndexOrThrow4 = cursor.getColumnIndexOrThrow("account_name");
        int columnIndexOrThrow5 = cursor.getColumnIndexOrThrow("account_type");
        int columnIndexOrThrow6 = cursor.getColumnIndexOrThrow("visible");
        int columnIndexOrThrow7 = cursor.getColumnIndexOrThrow("ownerAccount");
        HashMap map = new HashMap();
        int columnIndexOrThrow8 = cursor.getColumnIndexOrThrow("_id");
        int columnIndexOrThrow9 = cursor.getColumnIndexOrThrow(LunarContract.CalendarColumns.CALENDAR_COLOR);
        int columnIndexOrThrow10 = cursor.getColumnIndexOrThrow("account_type");
        HashMap map2 = new HashMap();
        while (cursor.moveToNext()) {
            String string = cursor.getString(columnIndexOrThrow2);
            CalendarItem calendarItem = new CalendarItem();
            calendarItem.displayName = string;
            HashMap map3 = map2;
            calendarItem.calendarId = cursor.getLong(columnIndexOrThrow);
            calendarItem.color = cursor.getInt(columnIndexOrThrow3);
            calendarItem.accountName = cursor.getString(columnIndexOrThrow4);
            calendarItem.accountType = cursor.getString(columnIndexOrThrow5);
            calendarItem.isVisible = cursor.getInt(columnIndexOrThrow6) == 1;
            calendarItem.ownerAccount = cursor.getString(columnIndexOrThrow7);
            map.put(Long.valueOf(cursor.getLong(columnIndexOrThrow)), calendarItem);
            if (cursor.getString(columnIndexOrThrow10).equalsIgnoreCase("LOCAL")) {
                this.mLocalCalendarId = cursor.getLong(columnIndexOrThrow8);
            }
            map3.put(Long.valueOf(cursor.getLong(columnIndexOrThrow8)), Integer.valueOf(cursor.getInt(columnIndexOrThrow9)));
            map2 = map3;
        }
        HashMap map4 = map2;
        if (!hasDataChanged(map)) {
            return false;
        }
        this.calendarColorChanged = false;
        this.mCalendars = map;
        this.calendarColors = map4;
        return true;
    }

    public Map getCalendarColors() {
        return this.calendarColors;
    }

    private boolean hasDataChanged(Map<Long, CalendarItem> map) {
        Map<Long, CalendarItem> map2 = this.mCalendars;
        if (map2 == null || map2.size() != map.size() || this.calendarColorChanged) {
            return true;
        }
        Iterator<Map.Entry<Long, CalendarItem>> it = this.mCalendars.entrySet().iterator();
        while (it.hasNext()) {
            Long key = it.next().getKey();
            if (!map.containsKey(key) || !map.get(key).equals(this.mCalendars.get(key))) {
                return true;
            }
        }
        return false;
    }
}
