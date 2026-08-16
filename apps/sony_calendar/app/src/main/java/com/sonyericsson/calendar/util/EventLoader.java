package com.sonyericsson.calendar.util;
import com.sonymobile.calendar.SafeTime;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.CalendarUtils;
import android.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.google.common.collect.Lists;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class EventLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int CALENDARS_INDEX_ACCESS_LEVEL = 16;
    public static final int CALENDARS_INDEX_OWNER_ACCOUNT = 17;
    public static final int INDEX_ALL_DAY = 3;
    public static final int INDEX_BEGIN = 8;
    public static final int INDEX_CALENDAR_COLOR = 5;
    public static final int INDEX_CALENDAR_ID = 14;
    public static final int INDEX_END = 9;
    public static final int INDEX_END_DAY = 12;
    public static final int INDEX_EVENT_COLOR = 6;
    public static final int INDEX_EVENT_DESCRIPTION = 19;
    public static final int INDEX_EVENT_ID = 10;
    public static final int INDEX_EVENT_LOCATION = 2;
    public static final int INDEX_EVENT_ORGANIZER = 18;
    public static final int INDEX_GUEST_CAN_MODIFY = 15;
    public static final int INDEX_HAS_ALARM = 4;
    public static final int INDEX_INSTANCE_ID = 0;
    public static final int INDEX_RRULE = 7;
    public static final int INDEX_SELF_ATTENDEE_STATUS = 13;
    public static final int INDEX_START_DAY = 11;
    public static final int INDEX_TITLE = 1;
    static final String[] PROJECTION = {"_id", LunarContract.EventsColumns.TITLE, LunarContract.EventsColumns.EVENT_LOCATION, "allDay", LunarContract.EventsColumns.HAS_ALARM, LunarContract.CalendarColumns.CALENDAR_COLOR, LunarContract.EventsColumns.EVENT_COLOR, "rrule", "begin", "end", "event_id", "startDay", "endDay", LunarContract.EventsColumns.SELF_ATTENDEE_STATUS, LunarContract.EventsColumns.CALENDAR_ID, LunarContract.EventsColumns.GUESTS_CAN_MODIFY, LunarContract.CalendarColumns.CALENDAR_ACCESS_LEVEL, "ownerAccount", LunarContract.EventsColumns.ORGANIZER, "description"};
    private static final int REQUEST_TIMEOUT = 1500;
    private static final String SORT_ORDER = "startDay ASC, begin ASC, title ASC";
    private Context context;
    private DaySpan daySpan;
    private IAsyncServiceResultHandler handler;
    private boolean hideDeclined;
    private TimeOutHandler timeOutHandler;
    private TimeOutHandler timeOutHandlerLunar;
    private Uri queryUri = null;
    private String querySelection = null;
    private Uri queryLunarUri = null;
    private boolean mSolarLoadFinished = false;
    private boolean mLunarLoadFinished = false;
    private EventMap lists = new EventMap();
    private EventMap allDayLists = new EventMap();
    private final Object mLock = new Object();

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public EventLoader(Context context) {
        this.context = context;
    }

    public void requestEvents(DaySpan daySpan, IAsyncServiceResultHandler iAsyncServiceResultHandler, boolean z) {
        this.hideDeclined = z;
        this.daySpan = daySpan;
        this.queryUri = buildQueryUri(daySpan.startJulianDay, daySpan.endJulianDay);
        this.querySelection = buildQuerySelection();
        this.handler = iAsyncServiceResultHandler;
        boolean zIsLunarAvailable = LunarAvailabilityManager.isLunarAvailable(this.context);
        if (zIsLunarAvailable) {
            this.queryLunarUri = buildQueryLunarUri(daySpan.startJulianDay, daySpan.endJulianDay);
        }
        synchronized (this.mLock) {
            this.lists.clear();
            this.allDayLists.clear();
        }
        restartLoader(zIsLunarAvailable);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = this.queryUri;
        if (i % 2 == 0) {
            uri = this.queryLunarUri;
        }
        Uri uri2 = uri;
        if (Utils.showAlarms(this.context)) {
            return new AlarmCalendarCursorLoader(this.context, uri2, PROJECTION, this.querySelection, null, SORT_ORDER, false);
        }
        return new CursorLoader(this.context, uri2, PROJECTION, this.querySelection, null, SORT_ORDER);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        boolean z = loader.getId() % 2 == 0;
        if (z) {
            this.timeOutHandlerLunar.stop(loader.getId());
        } else {
            this.timeOutHandler.stop(loader.getId());
        }
        if (cursor == null || cursor.isClosed()) {
            return;
        }
        new SortEventsAsyncTask(this.handler, z).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, copyCursor(cursor));
        Utils.closeCursor(cursor);
    }

    private MatrixCursor copyCursor(Cursor cursor) {
        MatrixCursor matrixCursor = new MatrixCursor(PROJECTION);
        int columnCount = cursor.getColumnCount();
        if (cursor.moveToFirst()) {
            do {
                Object[] objArr = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    objArr[i] = cursor.getString(i);
                }
                matrixCursor.addRow(objArr);
            } while (cursor.moveToNext());
        }
        return matrixCursor;
    }

    private Uri buildQueryUri(int i, int i2) {
        Uri.Builder builderBuildUpon = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon();
        ContentUris.appendId(builderBuildUpon, i);
        ContentUris.appendId(builderBuildUpon, i2);
        return builderBuildUpon.build();
    }

    private String buildQuerySelection() {
        if (this.hideDeclined) {
            return "visible=1 AND selfAttendeeStatus!=2 AND (eventStatus!=2 or eventStatus is null)";
        }
        return "visible=1 AND (eventStatus!=2 or eventStatus is null)";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:35:0x00b5 A[Catch: all -> 0x00e8, TryCatch #0 {, blocks: (B:4:0x0007, B:5:0x0021, B:7:0x0027, B:8:0x003a, B:10:0x0040, B:12:0x0048, B:13:0x004d, B:15:0x0055, B:20:0x0064, B:22:0x0070, B:33:0x00af, B:35:0x00b5, B:23:0x0077, B:25:0x007d, B:27:0x0083, B:30:0x008a, B:31:0x008e, B:17:0x005b, B:37:0x00c0, B:39:0x00cf, B:41:0x00d5, B:42:0x00d8, B:43:0x00e6), top: B:48:0x0007 }] */
    public Pair<EventMap, EventMap> handleResult(Cursor cursor, boolean z) {
        Pair<EventMap, EventMap> pair;
        synchronized (this.mLock) {
            Time time = new SafeTime(this.daySpan.timezone);
            Time time2 = new SafeTime(this.daySpan.timezone);
            ArrayList arrayListNewArrayList = Lists.newArrayList();
            for (int i = this.daySpan.startJulianDay; i <= this.daySpan.endJulianDay; i++) {
                time.setJulianDay(i);
                boolean z2 = false;
                time.normalize(false);
                ArrayList<EventInfo> eventList = getEventList(this.lists, i);
                ArrayList<EventInfo> eventList2 = getEventList(this.allDayLists, i);
                while (cursor.moveToNext()) {
                    if (cursor.getInt(11) > i) {
                        cursor.moveToPrevious();
                        break;
                    }
                    if ((cursor.getInt(11) <= i && cursor.getInt(12) >= i) || cursor.getInt(4) == -1) {
                        EventInfo eventInfoConvertToEventInfo = convertToEventInfo(cursor, time, z);
                        if (isAllDayEvent(cursor, time)) {
                            eventInfoConvertToEventInfo.allDay = 1;
                            eventList2.add(eventInfoConvertToEventInfo);
                        } else {
                            if (eventInfoConvertToEventInfo.startDay == eventInfoConvertToEventInfo.endDay) {
                                if (cursor.getInt(11) > i || cursor.getInt(12) < i) {
                                    time2.setJulianDay(cursor.getInt(11));
                                    eventInfoConvertToEventInfo.localBegin = Math.max(time2.toMillis(z2), eventInfoConvertToEventInfo.begin);
                                    getEventList(this.lists, eventInfoConvertToEventInfo.startDay).add(eventInfoConvertToEventInfo);
                                } else {
                                    eventList.add(eventInfoConvertToEventInfo);
                                }
                            }
                            if (eventInfoConvertToEventInfo.startDay != eventInfoConvertToEventInfo.endDay) {
                                addMultipleDaysEvents(eventInfoConvertToEventInfo, this.lists, this.allDayLists);
                            }
                            eventList = eventList;
                            z2 = false;
                        }
                        if (eventInfoConvertToEventInfo.startDay != eventInfoConvertToEventInfo.endDay) {
                            addMultipleDaysEvents(eventInfoConvertToEventInfo, this.lists, this.allDayLists);
                        }
                        eventList = eventList;
                        z2 = false;
                    }
                }
                arrayListNewArrayList.addAll(eventList2);
                arrayListNewArrayList.addAll(eventList);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            CalendarUtils.updateEventSelfAttendeeStatus(this.context, arrayListNewArrayList);
            pair = new Pair<>(this.lists, this.allDayLists);
        }
        return pair;
    }

    private boolean isAllDayEvent(Cursor cursor, Time time) {
        return cursor.getInt(3) == 1;
    }

    private void addMultipleDaysEvents(EventInfo eventInfo, EventMap eventMap, EventMap eventMap2) {
        if (this.daySpan.startJulianDay != this.daySpan.endJulianDay) {
            int iMax = Math.max(eventInfo.startDay, this.daySpan.startJulianDay);
            if (iMax == eventInfo.startDay && eventInfo.allDay != 1) {
                createEventCopyForFirstDay(eventInfo, eventMap, eventMap2);
            }
            if (this.daySpan.startJulianDay <= eventInfo.startDay) {
                iMax++;
            }
            while (iMax < eventInfo.endDay && iMax <= this.daySpan.endJulianDay) {
                createEventCopyForMiddleDay(eventInfo, eventMap2, iMax);
                iMax++;
            }
            if (iMax <= this.daySpan.endJulianDay) {
                createEventCopyForLastDay(eventInfo, eventMap, eventMap2);
                return;
            }
            return;
        }
        if (eventInfo.allDay == 0) {
            int i = this.daySpan.startJulianDay;
            if (i == eventInfo.startDay) {
                createEventCopyForFirstDay(eventInfo, eventMap, eventMap2);
            } else if (i == eventInfo.endDay) {
                createEventCopyForLastDay(eventInfo, eventMap, eventMap2);
            } else {
                createEventCopyForMiddleDay(eventInfo, eventMap2, i);
            }
        }
    }

    private void createEventCopyForFirstDay(EventInfo eventInfo, EventMap eventMap, EventMap eventMap2) {
        Time time = new SafeTime(this.daySpan.timezone);
        time.setJulianDay(eventInfo.startDay);
        EventInfo eventInfo2 = new EventInfo(eventInfo);
        eventInfo2.endDay = eventInfo.startDay;
        eventInfo2.localEnd = time.normalize(false) + 86400000;
        if (eventInfo2.localEnd - eventInfo2.begin >= 86400000) {
            eventInfo2.allDay = 1;
            getEventList(eventMap2, eventInfo.startDay).add(eventInfo2);
        } else {
            eventInfo2.allDay = 0;
            getEventList(eventMap, eventInfo.startDay).add(eventInfo2);
        }
    }

    private void createEventCopyForMiddleDay(EventInfo eventInfo, EventMap eventMap, int i) {
        Time time = new SafeTime(this.daySpan.timezone);
        time.setJulianDay(eventInfo.endDay);
        EventInfo eventInfo2 = new EventInfo(eventInfo);
        eventInfo2.startDay = i;
        eventInfo2.localBegin = time.normalize(false);
        eventInfo2.allDay = 1;
        getEventList(eventMap, i).add(eventInfo2);
    }

    private void createEventCopyForLastDay(EventInfo eventInfo, EventMap eventMap, EventMap eventMap2) {
        Time time = new SafeTime(this.daySpan.timezone);
        time.setJulianDay(eventInfo.endDay);
        EventInfo eventInfo2 = new EventInfo(eventInfo);
        eventInfo2.startDay = eventInfo.endDay;
        eventInfo2.localBegin = time.normalize(false);
        if (eventInfo2.end - eventInfo2.localBegin >= 86400000) {
            eventInfo2.allDay = 1;
            getEventList(eventMap2, eventInfo.endDay).add(eventInfo2);
        } else {
            eventInfo2.allDay = 0;
            getEventList(eventMap, eventInfo.endDay).add(eventInfo2);
        }
    }

    private ArrayList<EventInfo> getEventList(EventMap eventMap, int i) {
        ArrayList<EventInfo> arrayList = eventMap.get(Integer.valueOf(i));
        if (arrayList != null) {
            return arrayList;
        }
        ArrayList<EventInfo> newEventList = EventMap.getNewEventList();
        eventMap.put(Integer.valueOf(i), newEventList);
        return newEventList;
    }

    private EventInfo convertToEventInfo(Cursor cursor, Time time, boolean z) {
        EventInfo eventInfo = new EventInfo();
        eventInfo.isAlarmEvent = cursor.getInt(4) == -1;
        if (eventInfo.isAlarmEvent) {
            long j = cursor.getLong(8);
            eventInfo.end = j;
            eventInfo.begin = j;
            eventInfo.localBegin = Math.max(time.toMillis(false), eventInfo.begin);
            int i = cursor.getInt(11);
            eventInfo.endDay = i;
            eventInfo.startDay = i;
            Time time2 = new SafeTime();
            time2.set(eventInfo.begin);
            String string = (time2.hour < 10 ? new StringBuilder().append("0") : new StringBuilder().append("")).append(time2.hour).toString();
            String string2 = (time2.minute < 10 ? new StringBuilder().append("0") : new StringBuilder().append("")).append(time2.minute).toString();
            if (TextUtils.isEmpty(cursor.getString(1))) {
                eventInfo.description = "" + string + CalendarConstants.COLON + string2;
            } else {
                eventInfo.description = "" + string + CalendarConstants.COLON + string2 + " • " + cursor.getString(1);
            }
            eventInfo.title = cursor.getString(1);
            eventInfo.color = cursor.getInt(5);
        } else {
            eventInfo.instanceId = cursor.getLong(0);
            eventInfo.title = cursor.getString(1);
            eventInfo.eventLocation = cursor.getString(2);
            eventInfo.allDay = cursor.getInt(3);
            eventInfo.hasAlarm = cursor.getInt(4);
            int i2 = cursor.getInt(6);
            eventInfo.hasEventColor = i2 != 0;
            if (!eventInfo.hasEventColor) {
                i2 = cursor.getInt(5);
            }
            eventInfo.color = i2;
            eventInfo.rrule = cursor.getString(7);
            eventInfo.begin = cursor.getLong(8);
            eventInfo.startDay = cursor.getInt(11);
            eventInfo.localBegin = Math.max(time.toMillis(false), eventInfo.begin);
            eventInfo.end = cursor.getLong(9);
            eventInfo.id = cursor.getInt(10);
            eventInfo.endDay = cursor.getInt(12);
            eventInfo.selfAttendeeStatus = cursor.getInt(13);
            eventInfo.calendarId = cursor.getInt(14);
            eventInfo.guestCanModify = cursor.getInt(15);
            eventInfo.visibility = cursor.getInt(16);
            eventInfo.ownerAccount = cursor.getString(17);
            eventInfo.eventOrganizer = cursor.getString(18);
            eventInfo.isLunarEvent = z;
            eventInfo.setAccessLevel();
        }
        return eventInfo;
    }

    private class SortEventsAsyncTask extends AsyncTask<Cursor, Void, Pair<EventMap, EventMap>> {
        private IAsyncServiceResultHandler handler;
        private boolean isLunarEvent;

        public SortEventsAsyncTask(IAsyncServiceResultHandler iAsyncServiceResultHandler, boolean z) {
            this.handler = iAsyncServiceResultHandler;
            this.isLunarEvent = z;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Pair<EventMap, EventMap> doInBackground(Cursor... cursorArr) {
            return EventLoader.this.handleResult(cursorArr[0], this.isLunarEvent);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Pair<EventMap, EventMap> pair) {
            if (this.isLunarEvent) {
                EventLoader.this.mLunarLoadFinished = true;
            } else {
                EventLoader.this.mSolarLoadFinished = true;
            }
            if (EventLoader.this.mSolarLoadFinished && EventLoader.this.mLunarLoadFinished) {
                EventLoader.this.sortEventLists();
                IAsyncServiceResultHandler iAsyncServiceResultHandler = this.handler;
                if (iAsyncServiceResultHandler != null) {
                    iAsyncServiceResultHandler.onResult(pair, EventLoader.this.daySpan);
                }
                super.onPostExecute(pair);
            }
        }
    }

    private Uri buildQueryLunarUri(int i, int i2) {
        Uri.Builder builderBuildUpon = LunarContract.Instances.CONTENT_BY_DAY_URI.buildUpon();
        ContentUris.appendId(builderBuildUpon, i);
        ContentUris.appendId(builderBuildUpon, i2);
        return builderBuildUpon.build();
    }

    private void restartLoader(boolean z) {
        Random random = new Random();
        this.mSolarLoadFinished = false;
        int iNextInt = (random.nextInt(1000) * 2) + 1;
        this.timeOutHandler = new TimeOutHandler(this.context, iNextInt, this);
        ((AppCompatActivity) this.context).getSupportLoaderManager().restartLoader(iNextInt, null, this);
        if (z) {
            this.mLunarLoadFinished = false;
            int iNextInt2 = random.nextInt(1000) * 2;
            this.timeOutHandlerLunar = new TimeOutHandler(this.context, iNextInt2, this);
            ((AppCompatActivity) this.context).getSupportLoaderManager().restartLoader(iNextInt2, null, this);
            return;
        }
        this.mLunarLoadFinished = true;
    }

    private void sortEventList(ArrayList<EventInfo> arrayList) {
        Collections.sort(arrayList, new EventInfo.EventInfoComparator(false, false));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sortEventLists() {
        synchronized (this.mLock) {
            Iterator<Map.Entry<Integer, ArrayList<EventInfo>>> it = this.lists.entrySet().iterator();
            while (it.hasNext()) {
                sortEventList(it.next().getValue());
            }
            Iterator<Map.Entry<Integer, ArrayList<EventInfo>>> it2 = this.allDayLists.entrySet().iterator();
            while (it2.hasNext()) {
                sortEventList(it2.next().getValue());
            }
        }
    }

    private static class TimeOutHandler {
        private Context context;
        private CountDownTimer countDownTimer;
        private EventLoader eventLoader;
        private int id;

        public TimeOutHandler(Context context, int i, EventLoader eventLoader) {
            CountDownTimer countDownTimer = new CountDownTimer(1500L, 1500L) { // from class: com.sonyericsson.calendar.util.EventLoader.TimeOutHandler.1
                @Override // android.os.CountDownTimer
                public void onTick(long j) {
                }

                @Override // android.os.CountDownTimer
                public void onFinish() {
                    ((AppCompatActivity) TimeOutHandler.this.context).getSupportLoaderManager().restartLoader(TimeOutHandler.this.id, null, TimeOutHandler.this.eventLoader);
                }
            };
            this.countDownTimer = countDownTimer;
            this.context = context;
            this.id = i;
            this.eventLoader = eventLoader;
            countDownTimer.start();
        }

        public void stop(int i) {
            if (i == this.id) {
                this.countDownTimer.cancel();
            }
        }
    }
}
