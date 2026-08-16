package com.sonyericsson.calendar.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import com.sonymobile.calendar.CalendarItem;
import com.sonymobile.calendar.tasks.model.TaskAccount;
import com.sonymobile.calendar.tasks.settings.TaskAccountManager;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class CalendarColorService extends AsyncServiceBase {
    public static final int REQUEST_COLOR = 1;
    public static final int REQUEST_LOAD = 0;
    private static final String SELECTION = "sync_events=?";
    private static Map<Integer, Long> mTaskAccountIdToCalendarId;
    private Map<Long, Integer> calendarColors = new HashMap();
    private long mLocalCalendarId = 1;
    private ArrayList<TaskAccount> mTaskAccounts;
    private static final String[] PROJECTION = {"_id", LunarContract.CalendarColumns.CALENDAR_COLOR, "account_type"};
    private static final String[] SELECTION_ARGS = {"1"};
    private static CalendarColorService instance = null;
    private static boolean isHandleResult = true;

    private CalendarColorService() {
    }

    public static CalendarColorService getInstance() {
        if (instance == null) {
            instance = new CalendarColorService();
        }
        return instance;
    }

    public void requestLoad(Context context, final IAsyncServiceResultHandler iAsyncServiceResultHandler, final int i, boolean z) {
        if (isDataLoaded() && !z) {
            iAsyncServiceResultHandler.onResult(null, Integer.valueOf(i));
        }
        performAsyncQuery(context, new AsyncServiceBase.OnReadyHandler() { // from class: com.sonyericsson.calendar.util.CalendarColorService.1
            @Override // com.sonyericsson.calendar.util.AsyncServiceBase.OnReadyHandler
            public void onReady(boolean z2) {
                iAsyncServiceResultHandler.onResult(Boolean.valueOf(z2), Integer.valueOf(i));
            }
        }, CalendarContract.Calendars.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initializeTaskAccountIdMapping() {
        Map<Long, CalendarItem> calendars = CalendarInstanceService.getInstance().getCalendars();
        HashMap map = new HashMap();
        for (CalendarItem calendarItem : calendars.values()) {
            map.put(calendarItem.displayName, Long.valueOf(calendarItem.calendarId));
        }
        mTaskAccountIdToCalendarId = new HashMap();
        for (TaskAccount taskAccount : this.mTaskAccounts) {
            if (map.containsKey(taskAccount.name)) {
                mTaskAccountIdToCalendarId.put(Integer.valueOf(taskAccount.id), (Long) map.get(taskAccount.name));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getCalendarIdFromTaskId(int i) {
        return mTaskAccountIdToCalendarId.containsKey(Integer.valueOf(i)) ? mTaskAccountIdToCalendarId.get(Integer.valueOf(i)).longValue() : this.mLocalCalendarId;
    }

    public void requestColorByTaskAccountId(Context context, final IAsyncServiceResultHandler iAsyncServiceResultHandler, final int i, final int i2, boolean z, final boolean z2) {
        if (mTaskAccountIdToCalendarId != null) {
            requestColor(context, iAsyncServiceResultHandler, i, getCalendarIdFromTaskId(i2), z, z2);
        } else {
            this.mTaskAccounts = TaskAccountManager.getInstance().getAccountLists(context);
            performAsyncQuery(context, new AsyncServiceBase.OnReadyHandler() { // from class: com.sonyericsson.calendar.util.CalendarColorService.2
                @Override // com.sonyericsson.calendar.util.AsyncServiceBase.OnReadyHandler
                public void onReady(boolean z3) {
                    CalendarColorService.this.initializeTaskAccountIdMapping();
                    iAsyncServiceResultHandler.onResult(Integer.valueOf(CalendarColorService.this.getColor(CalendarColorService.this.getCalendarIdFromTaskId(i2), z2)), Integer.valueOf(i));
                }
            }, CalendarContract.Calendars.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, null);
        }
    }

    public void clearCache() {
        setmTaskAccountIdToCalendarId(null);
    }

    public void requestColor(Context context, final IAsyncServiceResultHandler iAsyncServiceResultHandler, final int i, final long j, boolean z, final boolean z2) {
        if (isDataLoaded() && !z) {
            iAsyncServiceResultHandler.onResult(Integer.valueOf(getColor(j, z2)), Integer.valueOf(i));
        } else {
            performAsyncQuery(context, new AsyncServiceBase.OnReadyHandler() { // from class: com.sonyericsson.calendar.util.CalendarColorService.3
                @Override // com.sonyericsson.calendar.util.AsyncServiceBase.OnReadyHandler
                public void onReady(boolean z3) {
                    iAsyncServiceResultHandler.onResult(Integer.valueOf(CalendarColorService.this.getColor(j, z2)), Integer.valueOf(i));
                }
            }, CalendarContract.Calendars.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, null);
        }
    }

    public int getColor(long j) {
        return getColor(j, false);
    }

    public int getColor(long j, boolean z) {
        if (isHandleResult) {
            this.calendarColors = CalendarInstanceService.getInstance().getCalendarColors();
        }
        Map<Long, Integer> map = this.calendarColors;
        if (z) {
            j = this.mLocalCalendarId;
        }
        Integer num = map.get(Long.valueOf(j));
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    @Override // com.sonyericsson.calendar.util.AsyncServiceBase
    protected boolean isDataLoaded() {
        return this.calendarColors.size() > 0 && super.isDataLoaded();
    }

    @Override // com.sonyericsson.calendar.util.AsyncServiceBase
    protected boolean handleResultData(Cursor cursor) {
        isHandleResult = false;
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow("_id");
        int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow(LunarContract.CalendarColumns.CALENDAR_COLOR);
        int columnIndexOrThrow3 = cursor.getColumnIndexOrThrow("account_type");
        HashMap map = new HashMap();
        while (cursor.moveToNext()) {
            if (cursor.getString(columnIndexOrThrow3).equalsIgnoreCase("LOCAL")) {
                this.mLocalCalendarId = cursor.getLong(columnIndexOrThrow);
            }
            map.put(Long.valueOf(cursor.getLong(columnIndexOrThrow)), Integer.valueOf(cursor.getInt(columnIndexOrThrow2)));
        }
        if (!hasDataChanged(map)) {
            return false;
        }
        this.calendarColors = map;
        return true;
    }

    private boolean hasDataChanged(Map<Long, Integer> map) {
        Map<Long, Integer> map2 = this.calendarColors;
        if (map2 == null || map2.size() != map.size()) {
            return true;
        }
        Iterator<Map.Entry<Long, Integer>> it = this.calendarColors.entrySet().iterator();
        while (it.hasNext()) {
            Long key = it.next().getKey();
            if (!map.containsKey(key) || !map.get(key).equals(this.calendarColors.get(key))) {
                return true;
            }
        }
        return false;
    }

    public static void setmTaskAccountIdToCalendarId(Map<Integer, Long> map) {
        mTaskAccountIdToCalendarId = map;
    }
}
