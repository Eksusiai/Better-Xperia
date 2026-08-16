package com.sonymobile.calendar;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.CalendarUtils;
import android.widget.AbsListView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.google.common.collect.Lists;
import com.sonyericsson.calendar.util.AlarmCalendarCursorLoader;
import com.sonyericsson.calendar.util.CustomizeConfig;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.lunar.lib.LunarContract;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public class AgendaEventLoader implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnScrollListener {
    private static final String AGENDA_SORT_ORDER = "startDay ASC, begin ASC, title ASC";
    private static final int DAYS_TO_SEARCH_QUERY = 73000;
    private static final int LOADER_LUNAR = 0;
    private static final int LOADER_SOLAR = 1;
    private static final int MAX_DAYS_TO_QUERY = 150;
    private static final int MAX_NUMBER_OF_EVENTS = 2000;
    private static final int MAX_QUERY_RETRIES = 3;
    private static final int MINIMUM_DAYS_TO_QUERY = 7;
    private static final int MIN_LIST_SIZE = 20;
    private static final int NOTHING_SELECTED = -1;
    private static final int SCROLL_BOTH = 0;
    private static final int SCROLL_DOWNWARDS = 2;
    private static final int SCROLL_UPWARDS = 1;
    private AgendaAdapter arrayAdapter;
    private boolean isInEventPickerFragment;
    private AgendaEventLoaderListener loaderListener;
    private Activity mActivity;
    private Fragment mFragment;
    private boolean mHideDeclined;
    private String mQuery;
    private long selectedEventInstanceIdFromMonth;
    private static final String[] PROJECTION = {"_id", LunarContract.EventsColumns.TITLE, LunarContract.EventsColumns.EVENT_LOCATION, "allDay", LunarContract.EventsColumns.HAS_ALARM, LunarContract.CalendarColumns.CALENDAR_COLOR, LunarContract.EventsColumns.EVENT_COLOR, "rrule", "begin", "end", "event_id", "startDay", "endDay", LunarContract.EventsColumns.SELF_ATTENDEE_STATUS, LunarContract.EventsColumns.CALENDAR_ID, LunarContract.EventsColumns.GUESTS_CAN_MODIFY, LunarContract.CalendarColumns.CALENDAR_ACCESS_LEVEL, "ownerAccount", LunarContract.EventsColumns.ORGANIZER, "description"};
    static final AtomicInteger NEXT_ID = new AtomicInteger(0);
    private int startDay = 0;
    private int endDay = 0;
    private int prevVisibleItem = -1;
    private boolean isListInMonthView = false;
    private int numberOfDays = 30;
    private String latestQuery = null;
    private Uri queryUri = null;
    private String querySelection = null;
    private int newlyAddedItems = 0;
    private int queryAboveRetries = 0;
    private int queryBelowRetries = 0;
    private int latestScroll = -1;
    private long removeItemId = -1;
    private boolean lockScroll = false;
    private long scrollSelectionInstanceId = -1;
    private long mAgendaInstanceId = -1;
    private boolean mAgendaInitialScroll = false;
    private boolean setSelectionFromTopCalled = false;
    private boolean firstUpdateRefresh = true;
    private boolean isTodayPress = false;
    private boolean eventSelectedFromMonth = false;
    private ArrayList<EventInfo> newEvents = Lists.newArrayList();
    private AbsListView absListView = null;
    private Uri queryLunarUri = null;
    private boolean mSolarLoadFinished = false;
    private boolean mLunarLoadFinished = false;
    private boolean mAWholeLoad = true;
    private int mSolarEventCount = 0;
    private int mLunarEventCount = 0;
    private boolean isLoaded = false;
    private boolean isAtTop = true;
    final int loaderId = NEXT_ID.getAndIncrement() * 2;
    private final Handler handler = new Handler() { // from class: com.sonymobile.calendar.AgendaEventLoader.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (AgendaEventLoader.this.mActivity == null || AgendaEventLoader.this.mActivity.isDestroyed()) {
                return;
            }
            if (message.obj == null) {
                AgendaEventLoader.this.loaderListener.onEventSelected(null);
                return;
            }
            EventInfo eventForId = (EventInfo) message.obj;
            if (AgendaEventLoader.this.selectedEventInstanceIdFromMonth < 1) {
                AgendaEventLoader.this.selectedEventInstanceIdFromMonth = eventForId.instanceId;
            } else {
                eventForId = getEventForId();
            }
            AgendaEventLoader.this.loaderListener.onEventSelected(eventForId);
        }

        private EventInfo getEventForId() {
            for (EventInfo eventInfo : AgendaEventLoader.this.eventList) {
                if (eventInfo.instanceId == AgendaEventLoader.this.selectedEventInstanceIdFromMonth) {
                    return eventInfo;
                }
            }
            return null;
        }
    };
    private ArrayList<EventInfo> eventList = Lists.newArrayList();

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    public AgendaEventLoader(Context context, Fragment fragment, boolean z, long j) {
        this.selectedEventInstanceIdFromMonth = -1L;
        this.isInEventPickerFragment = false;
        this.selectedEventInstanceIdFromMonth = j;
        this.isInEventPickerFragment = z;
        this.mActivity = (Activity) context;
        this.mFragment = fragment;
        this.arrayAdapter = new AgendaAdapter(context, z ? R.layout.agenda_item_pickable : R.layout.agenda_item, this.eventList, false);
    }

    public void onResume() {
        this.removeItemId = -1L;
        if (!CustomizeConfig.isShowingPermissionAcceptanceDialogEnabled(this.mActivity) || Utils.getSharedPreference((Context) this.mActivity, GeneralPreferences.KEY_USE_WIFI_AND_MOBILE_DATA, false)) {
            this.arrayAdapter.updateWeather();
        }
        this.arrayAdapter.onResume();
    }

    public void onPause() {
        this.arrayAdapter.onPause();
    }

    public void setIsInEventPickerFragment(EventPickerFragment eventPickerFragment) {
        this.arrayAdapter.setIsInEventPickerFragment(eventPickerFragment);
    }

    public void setAgendaEventLoaderListener(AgendaEventLoaderListener agendaEventLoaderListener) {
        this.loaderListener = agendaEventLoaderListener;
    }

    public AgendaAdapter getAgendaAdapter() {
        return this.arrayAdapter;
    }

    public EventInfo getItem(int i) {
        EventInfo eventInfo = null;
        try {
            EventInfo item = this.arrayAdapter.getItem(i);
            try {
                item.setAccessLevel();
                return item;
            } catch (IndexOutOfBoundsException unused) {
                eventInfo = item;
                return eventInfo;
            }
        } catch (IndexOutOfBoundsException unused2) {
            return eventInfo;
        }
    }

    public void setSelectedEventInstanceId(long j) {
        this.selectedEventInstanceIdFromMonth = j;
        this.arrayAdapter.setSelectedEventInstanceId(j);
    }

    public void removeItem(int i, long j, long j2) {
        this.removeItemId = j;
        this.eventList.remove(i);
        this.arrayAdapter.notifyDataSetChanged();
        if (this.selectedEventInstanceIdFromMonth == j2 && this.mActivity.getResources().getBoolean(R.bool.tablet_mode)) {
            loadEventFromPosition(i);
        }
    }

    public void removeRepeatingItems(long j) {
        this.removeItemId = j;
        ArrayList arrayListNewArrayList = Lists.newArrayList();
        for (int i = 0; i < this.eventList.size(); i++) {
            if (this.eventList.get(i).id == j) {
                if (this.selectedEventInstanceIdFromMonth == this.eventList.get(i).instanceId && this.mActivity.getResources().getBoolean(R.bool.tablet_mode)) {
                    this.loaderListener.onEventSelected(null);
                }
                arrayListNewArrayList.add(this.eventList.get(i));
            }
        }
        this.eventList.removeAll(arrayListNewArrayList);
        this.arrayAdapter.notifyDataSetChanged();
    }

    public void loadEventFromPosition(int i) {
        EventInfo item = getItem(i);
        this.loaderListener.onEventSelected(item);
        if (item != null) {
            setSelectedEventInstanceId(item.instanceId);
        }
    }

    private Uri buildQueryUri(int i, int i2, String str) {
        Uri uri;
        if (str == null) {
            uri = CalendarContract.Instances.CONTENT_BY_DAY_URI;
        } else {
            uri = CalendarContract.Instances.CONTENT_SEARCH_BY_DAY_URI;
        }
        Uri.Builder builderBuildUpon = uri.buildUpon();
        ContentUris.appendId(builderBuildUpon, i);
        ContentUris.appendId(builderBuildUpon, i2);
        if (str != null) {
            builderBuildUpon.appendPath(str);
        }
        return builderBuildUpon.build();
    }

    private String buildQuerySelection() {
        if (this.mHideDeclined) {
            return "visible=1 AND selfAttendeeStatus!=2 AND (eventStatus!=2 or eventStatus is null)";
        }
        return "visible=1 AND (eventStatus!=2 or eventStatus is null)";
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = this.queryUri;
        if (i % 2 == 0) {
            uri = this.queryLunarUri;
        }
        Uri uri2 = uri;
        if (Utils.showAlarms(this.mActivity)) {
            return new AlarmCalendarCursorLoader(this.mActivity, uri2, PROJECTION, this.querySelection, null, AGENDA_SORT_ORDER, false);
        }
        return new CursorLoader(this.mActivity, uri2, PROJECTION, this.querySelection, null, AGENDA_SORT_ORDER);
    }

    public void setIsListInMonthView(boolean z) {
        this.isListInMonthView = z;
        this.arrayAdapter.setIsListInMonthView(z);
    }

    public void setHideDeclinedEvents(boolean z) {
        this.mHideDeclined = z;
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        boolean z;
        boolean z3;
        int i;
        int i2;
        int i3;
        int i4;
        if (!this.isLoaded || cursor == null || this.mActivity == null) {
            return;
        }
        if (cursor.getCount() > MAX_NUMBER_OF_EVENTS) {
            queryDatabaseForPeriod(MAX_DAYS_TO_QUERY, MAX_DAYS_TO_QUERY);
            return;
        }
        if (loader.getId() % 2 != 0) {
            this.mSolarLoadFinished = true;
            this.mSolarEventCount = cursor.getCount();
            z = false;
        } else {
            this.mLunarLoadFinished = true;
            this.mLunarEventCount = cursor.getCount();
            z = true;
        }
        if (this.mSolarLoadFinished && this.mSolarEventCount <= 0 && this.mLunarLoadFinished && this.mLunarEventCount <= 0) {
            this.eventList.clear();
            this.loaderListener.onNoEventsFound();
            Message messageObtain = Message.obtain();
            messageObtain.obj = null;
            this.handler.sendMessage(messageObtain);
            return;
        }
        this.loaderListener.onEventsFound();
        if (this.mAWholeLoad) {
            this.newEvents.clear();
        }
        populateEventList(cursor, z);
        CalendarUtils.updateEventSelfAttendeeStatus(this.mActivity.getBaseContext(), this.newEvents);
        if (!this.mSolarLoadFinished || !this.mLunarLoadFinished) {
            this.mAWholeLoad = false;
            return;
        }
        this.mAWholeLoad = true;
        if (!this.isListInMonthView && this.latestQuery == null) {
            if (this.newlyAddedItems > 0) {
                if (this.newEvents.size() < 20 && (i3 = this.queryBelowRetries) < 3 && (i4 = this.queryAboveRetries) < 3) {
                    this.queryBelowRetries = i3 + 1;
                    this.queryAboveRetries = i4 + 1;
                    int i5 = this.numberOfDays;
                    refreshByScroll(-i5, i5, null);
                    return;
                }
                this.queryAboveRetries = 0;
                this.queryBelowRetries = 0;
            } else {
                int i6 = this.latestScroll;
                if (i6 == 1 && (i2 = this.queryAboveRetries) < 3) {
                    this.queryAboveRetries = i2 + 1;
                    int i7 = this.numberOfDays;
                    refreshByScroll(-i7, -i7, null);
                    return;
                } else if (i6 == 2 && (i = this.queryBelowRetries) < 3) {
                    this.queryBelowRetries = i + 1;
                    int i8 = this.numberOfDays;
                    refreshByScroll(i8, i8, null);
                    return;
                }
            }
            this.lockScroll = false;
        }
        sortEventList();
        this.eventList.clear();
        this.arrayAdapter.notifyDataSetChanged();
        this.eventList.addAll(this.newEvents);
        this.arrayAdapter.notifyDataSetChanged();
        if (this.newlyAddedItems != 0 || this.isTodayPress) {
            this.newlyAddedItems = 0;
            this.isTodayPress = false;
            if (this.scrollSelectionInstanceId == -1) {
                Time time = new SafeTime(Utils.getTimeZone(this.mActivity, null));
                time.set(Utils.getDisplayTime());
                time.set(0, 0, 0, time.monthDay, time.month, time.year);
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
                long timeInMillis = (calendar.getTimeInMillis() + ((long) (calendar.get(15) + calendar.get(16)))) % 86400000;
                long timeInMillis2 = calendar.getTimeInMillis() - timeInMillis;
                long millis = time.toMillis(false);
                if (time.toMillis(false) == timeInMillis2) {
                    millis = calendar.getTimeInMillis();
                }
                time.setJulianDay(this.startDay);
                int i9 = 0;
                int i10 = 0;
                boolean z4 = false;
                boolean z2 = false;
                while (true) {
                    if (i9 < this.eventList.size()) {
                        EventInfo eventInfo = this.eventList.get(i9);
                        if (eventInfo.instanceId == this.selectedEventInstanceIdFromMonth) {
                            stopScroll(this.absListView);
                            z3 = false;
                            this.loaderListener.onPositionSelected(i9, false);
                            if (this.isListInMonthView) {
                                z4 = true;
                            }
                        } else {
                            z3 = false;
                        }
                        boolean z5 = this.isListInMonthView ? time.toMillis(z3) == timeInMillis2 : true;
                        if ((eventInfo.localEnd < millis || !z5 || eventInfo.allDay == 1 || this.selectedEventInstanceIdFromMonth >= 1) && (eventInfo.allDay <= 0 || eventInfo.localBegin + timeInMillis < millis)) {
                            i10 = i9;
                            i9++;
                        } else {
                            stopScroll(this.absListView);
                            this.loaderListener.onPositionSelected(0, false);
                            if (!this.isListInMonthView) {
                                this.scrollSelectionInstanceId = eventInfo.instanceId;
                                this.mAgendaInitialScroll = true;
                                this.mAgendaInstanceId = eventInfo.instanceId;
                            }
                            if (this.mActivity.getResources().getBoolean(R.bool.tablet_mode) && !this.isListInMonthView && !this.isInEventPickerFragment) {
                                Message messageObtain2 = Message.obtain();
                                messageObtain2.obj = eventInfo;
                                this.handler.sendMessage(messageObtain2);
                            }
                            z2 = true;
                        }
                    } else {
                        i9 = i10;
                        z2 = z4;
                    }
                    if (z2) {
                        return;
                    }
                    cursor.moveToFirst();
                    if (this.isListInMonthView) {
                        stopScroll(this.absListView);
                        this.loaderListener.onPositionSelected(0, false);
                        return;
                    }
                    if (this.eventList.isEmpty()) {
                        return;
                    }
                    stopScroll(this.absListView);
                    EventInfo eventInfo2 = this.eventList.get(i9);
                    this.loaderListener.onPositionSelected(i9, false);
                    if (!this.mActivity.getResources().getBoolean(R.bool.tablet_mode) || this.isInEventPickerFragment) {
                        return;
                    }
                    Message messageObtain3 = Message.obtain();
                    messageObtain3.obj = eventInfo2;
                    this.handler.sendMessage(messageObtain3);
                    return;
                }
            }
            for (int i11 = 0; i11 < this.eventList.size(); i11++) {
                EventInfo eventInfo3 = this.eventList.get(i11);
                if (eventInfo3.instanceId == this.scrollSelectionInstanceId && eventInfo3.isLunarEvent == Utils.getAgendSelectedIsLunarEvent()) {
                    this.setSelectionFromTopCalled = true;
                    this.loaderListener.onPositionSelected(i11, true);
                    if (!this.eventSelectedFromMonth) {
                        break;
                    }
                    this.eventSelectedFromMonth = false;
                    Message messageObtain4 = Message.obtain();
                    messageObtain4.obj = eventInfo3;
                    this.handler.sendMessage(messageObtain4);
                    break;
                }
            }
            this.mAgendaInitialScroll = false;
        }
    }

    private void queryDatabaseForPeriod(int i, int i2) {
        Time time = new SafeTime(Utils.getTimeZone(this.mActivity, null));
        time.set(System.currentTimeMillis());
        Utils.setDisplayTime(Long.valueOf(time.toMillis(true)));
        int julianDay = Time.getJulianDay(time.toMillis(false), time.gmtoff);
        int i3 = i2 + julianDay;
        this.endDay = i3;
        int i4 = julianDay - i;
        this.startDay = i4;
        this.queryUri = buildQueryUri(i4, i3, null);
        boolean zIsLunarAvailable = LunarAvailabilityManager.isLunarAvailable(this.mActivity);
        if (zIsLunarAvailable) {
            this.queryLunarUri = buildQueryLunarUri(this.startDay, this.endDay, null);
        }
        this.lockScroll = true;
        restartLoader(zIsLunarAvailable);
    }

    private void populateEventList(Cursor cursor, boolean z) {
        addSingleDayEvents(cursor, z);
        if (!this.isListInMonthView) {
            addMultipleDaysEvents(cursor, z);
        }
        cursor.moveToFirst();
    }

    private void addSingleDayEvents(Cursor cursor, boolean z) {
        Time time = new SafeTime(Utils.getTimeZone(this.mActivity, null));
        time.set(Utils.getDisplayTime());
        time.set(0, 0, 0, time.monthDay, time.month, time.year);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            EventInfo eventInfo = new EventInfo();
            eventInfo.instanceId = cursor.getLong(0);
            eventInfo.title = cursor.getString(1);
            eventInfo.eventLocation = cursor.getString(2);
            eventInfo.allDay = cursor.getInt(3);
            eventInfo.hasAlarm = cursor.getInt(4);
            int i = cursor.getInt(6);
            eventInfo.hasEventColor = i != 0;
            if (!eventInfo.hasEventColor) {
                i = cursor.getInt(5);
            }
            eventInfo.color = i;
            eventInfo.rrule = cursor.getString(7);
            eventInfo.begin = cursor.getLong(8);
            eventInfo.startDay = cursor.getInt(11);
            time.setJulianDay(this.isListInMonthView ? this.startDay : eventInfo.startDay);
            long millis = time.toMillis(true);
            eventInfo.localBegin = millis - eventInfo.begin > 0 ? millis : eventInfo.begin;
            eventInfo.end = cursor.getLong(9);
            eventInfo.localEnd = eventInfo.end - millis >= 86400000 ? millis : eventInfo.end;
            if (eventInfo.allDay != 1) {
                eventInfo.allDay = (millis - eventInfo.begin <= 0 || eventInfo.end - millis < 86400000) ? 0 : 1;
            }
            if (eventInfo.end - millis >= 86400000 && eventInfo.allDay != 1) {
                eventInfo.localEnd = millis + 86400000;
            }
            eventInfo.id = cursor.getInt(10);
            eventInfo.endDay = cursor.getInt(12);
            eventInfo.selfAttendeeStatus = cursor.getInt(13);
            eventInfo.calendarId = cursor.getInt(14);
            eventInfo.guestCanModify = cursor.getInt(15);
            eventInfo.visibility = cursor.getInt(16);
            eventInfo.ownerAccount = cursor.getString(17);
            eventInfo.eventOrganizer = cursor.getString(18);
            eventInfo.description = cursor.getString(19);
            eventInfo.isLunarEvent = z;
            if (Utils.showAlarms(this.mActivity)) {
                eventInfo.isAlarmEvent = cursor.getInt(4) == -1;
            }
            if (!this.newEvents.contains(eventInfo)) {
                this.newEvents.add(eventInfo);
                if (this.removeItemId != eventInfo.id) {
                    this.newlyAddedItems++;
                }
            }
        }
    }

    private void addMultipleDaysEvents(Cursor cursor, boolean z) {
        byte b;
        Time time = new SafeTime(Utils.getTimeZone(this.mActivity, null));
        time.set(Utils.getDisplayTime());
        time.set(0, 0, 0, time.monthDay, time.month, time.year);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            int i = 12;
            if (cursor.getLong(12) - cursor.getLong(11) != 0) {
                int i2 = cursor.getInt(12);
                int i3 = cursor.getInt(11) + 1;
                int i4 = 1;
                while (i3 <= i2) {
                    time.setJulianDay(i3);
                    long millis = time.toMillis(false);
                    EventInfo eventInfo = new EventInfo();
                    eventInfo.instanceId = cursor.getLong(0);
                    eventInfo.title = cursor.getString(1);
                    eventInfo.eventLocation = cursor.getString(2);
                    eventInfo.hasAlarm = cursor.getInt(4);
                    int i5 = cursor.getInt(6);
                    eventInfo.hasEventColor = i5 != 0;
                    if (!eventInfo.hasEventColor) {
                        i5 = cursor.getInt(5);
                    }
                    eventInfo.color = i5;
                    eventInfo.rrule = cursor.getString(7);
                    eventInfo.allDay = cursor.getInt(3);
                    eventInfo.begin = cursor.getLong(8);
                    eventInfo.end = cursor.getLong(9);
                    eventInfo.id = cursor.getInt(10);
                    eventInfo.startDay = cursor.getInt(11) + i4;
                    eventInfo.localBegin = millis;
                    eventInfo.endDay = cursor.getInt(i);
                    eventInfo.localEnd = i3 == i2 ? eventInfo.end : millis + 86400000;
                    eventInfo.selfAttendeeStatus = cursor.getInt(13);
                    eventInfo.calendarId = cursor.getInt(14);
                    eventInfo.guestCanModify = cursor.getInt(15);
                    eventInfo.visibility = cursor.getInt(16);
                    eventInfo.ownerAccount = cursor.getString(17);
                    eventInfo.eventOrganizer = cursor.getString(18);
                    eventInfo.isLunarEvent = z;
                    if (millis - eventInfo.begin > 0 && eventInfo.end - millis >= 86400000) {
                        eventInfo.allDay = 1;
                    }
                    if (Utils.showAlarms(this.mActivity)) {
                        b = -1;
                        eventInfo.isAlarmEvent = cursor.getInt(4) == -1;
                    } else {
                        b = -1;
                    }
                    if (!this.newEvents.contains(eventInfo)) {
                        this.newEvents.add(eventInfo);
                        if (this.removeItemId != eventInfo.id) {
                            this.newlyAddedItems++;
                        }
                    }
                    i4++;
                    i3++;
                    i2 = i2;
                    i = 12;
                }
            }
        }
    }

    private void sortEventList() {
        Collections.sort(this.newEvents, new EventInfo.EventInfoComparator(true, this.isListInMonthView));
    }

    public void goToDay(Time time, String str) {
        int julianDay = Time.getJulianDay(time.toMillis(false), time.gmtoff);
        this.arrayAdapter.setTime(time);
        this.queryAboveRetries = 0;
        this.queryBelowRetries = 0;
        this.scrollSelectionInstanceId = -1L;
        this.startDay = julianDay;
        this.endDay = julianDay;
        this.queryUri = buildQueryUri(julianDay, julianDay, str);
        boolean zIsLunarAvailable = LunarAvailabilityManager.isLunarAvailable(this.mActivity);
        if (zIsLunarAvailable) {
            this.queryLunarUri = buildQueryLunarUri(this.startDay, this.endDay, str);
        }
        this.querySelection = buildQuerySelection();
        restartLoader(zIsLunarAvailable);
    }

    public void refresh(int i, String str, boolean z) {
        this.mQuery = str;
        Time time = new SafeTime(Utils.getTimeZone(this.mActivity, null));
        if (this.firstUpdateRefresh || !z) {
            boolean z2 = !z;
            this.isTodayPress = z2;
            if (z2) {
                Utils.setAgendaSelectedEventInstanceId(-1L);
            }
            this.firstUpdateRefresh = false;
            if (!this.isListInMonthView) {
                this.queryAboveRetries = 0;
                this.queryBelowRetries = 0;
                this.latestQuery = str;
                this.scrollSelectionInstanceId = -1L;
                this.selectedEventInstanceIdFromMonth = -1L;
                if (Utils.getAgendaSelectedEventInstanceId() > 0 && this.mActivity.getResources().getBoolean(R.bool.tablet_mode)) {
                    time.set(Utils.getAgendaSelectedBegin());
                    i = Time.getJulianDay(time.toMillis(false), time.gmtoff);
                    this.selectedEventInstanceIdFromMonth = Utils.getAgendaSelectedEventInstanceId();
                    this.eventSelectedFromMonth = true;
                }
                if (TextUtils.isEmpty(str)) {
                    this.startDay = i - DAYS_TO_SEARCH_QUERY;
                    this.endDay = i + DAYS_TO_SEARCH_QUERY;
                } else {
                    this.startDay = i - 300;
                    this.endDay = i + 300;
                }
                this.queryUri = buildQueryUri(this.startDay, this.endDay, str);
                boolean zIsLunarAvailable = LunarAvailabilityManager.isLunarAvailable(this.mActivity);
                if (zIsLunarAvailable) {
                    this.queryLunarUri = buildQueryLunarUri(this.startDay, this.endDay, str);
                }
                this.querySelection = buildQuerySelection();
                restartLoader(zIsLunarAvailable);
                return;
            }
            goToDay(time, str);
        }
    }

    private void refreshByScroll(int i, int i2, String str) {
        int i3 = this.endDay + i2;
        this.endDay = i3;
        int i4 = this.startDay + i;
        this.startDay = i4;
        int i5 = i3 - i4;
        if (i5 < 0) {
            this.endDay = i3 + Math.abs(i5) + 7;
        }
        this.queryUri = buildQueryUri(this.startDay, this.endDay, str);
        boolean zIsLunarAvailable = LunarAvailabilityManager.isLunarAvailable(this.mActivity);
        if (zIsLunarAvailable) {
            this.queryLunarUri = buildQueryLunarUri(this.startDay, this.endDay, str);
        }
        this.lockScroll = true;
        restartLoader(zIsLunarAvailable);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
        this.mActivity = null;
    }

    private void stopScroll(AbsListView absListView) {
        try {
            Field declaredField = AbsListView.class.getDeclaredField("mFlingRunnable");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(absListView);
            if (obj != null) {
                Method declaredMethod = Class.forName("android.widget.AbsListView$FlingRunnable").getDeclaredMethod("endFling", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(obj, new Object[0]);
            }
        } catch (ReflectiveOperationException unused) {
        }
    }

    public boolean isAtTop() {
        return this.isAtTop;
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (absListView.getChildAt(0) != null) {
            this.isAtTop = absListView.getFirstVisiblePosition() == 0 && absListView.getChildAt(0).getTop() >= 0;
        } else {
            this.isAtTop = true;
        }
        this.absListView = absListView;
        EventInfo item = getItem(i);
        if (item == null) {
            return;
        }
        long j = item.instanceId;
        long j2 = this.mAgendaInstanceId;
        if (j == j2 || j2 == -1) {
            this.mAgendaInitialScroll = false;
        }
        if (!this.mAgendaInitialScroll) {
            this.scrollSelectionInstanceId = item.instanceId;
        }
        if (this.isListInMonthView || i3 <= i2 || this.lockScroll || this.setSelectionFromTopCalled) {
            this.setSelectionFromTopCalled = false;
            return;
        }
        if (i3 > 2 && this.prevVisibleItem != i) {
            boolean z = i3 - (i2 + i) < 20;
            boolean z2 = i < 20;
            if (z2 || z) {
                int i4 = MAX_DAYS_TO_QUERY / ((this.newlyAddedItems / ((this.endDay - this.startDay) + 1)) + 1);
                this.numberOfDays = i4;
                if (i4 < 7) {
                    this.numberOfDays = 7;
                }
                if (z2 && z && this.queryAboveRetries < 3 && this.queryBelowRetries < 3) {
                    this.latestScroll = 0;
                    int i5 = this.numberOfDays;
                    refreshByScroll(-i5, i5, this.mQuery);
                } else if (z2 && this.queryAboveRetries < 3) {
                    this.latestScroll = 1;
                    refreshByScroll(-this.numberOfDays, 0, this.mQuery);
                } else if (z && this.queryBelowRetries < 3) {
                    this.latestScroll = 2;
                    refreshByScroll(0, this.numberOfDays, this.mQuery);
                }
            }
        }
        this.prevVisibleItem = i;
    }

    public void onCalendarsChanged() {
        Activity activity;
        if (this.queryUri == null || (activity = this.mActivity) == null) {
            return;
        }
        restartLoader(LunarAvailabilityManager.isLunarAvailable(activity));
    }

    private void restartLoader(boolean z) {
        if (!this.mFragment.isAdded() || this.mFragment.isDetached() || this.mFragment.isRemoving()) {
            return;
        }
        this.mSolarLoadFinished = false;
        this.isLoaded = true;
        this.mFragment.getLoaderManager().restartLoader(this.loaderId + 1, null, this);
        if (z) {
            this.mLunarLoadFinished = false;
            this.mFragment.getLoaderManager().restartLoader(this.loaderId + 0, null, this);
        } else {
            this.mLunarLoadFinished = true;
        }
        this.mSolarEventCount = 0;
        this.mLunarEventCount = 0;
    }

    private Uri buildQueryLunarUri(int i, int i2, String str) {
        Uri uri;
        if (str == null) {
            uri = LunarContract.Instances.CONTENT_BY_DAY_URI;
        } else {
            uri = LunarContract.Instances.CONTENT_SEARCH_BY_DAY_URI;
        }
        Uri.Builder builderBuildUpon = uri.buildUpon();
        ContentUris.appendId(builderBuildUpon, i);
        ContentUris.appendId(builderBuildUpon, i2);
        if (str != null) {
            builderBuildUpon.appendPath(str);
        }
        return builderBuildUpon.build();
    }

    public int getCount() {
        return this.arrayAdapter.getCount();
    }

    public int getPositionForEvent(long j) {
        for (int i = 0; i < this.eventList.size(); i++) {
            if (this.eventList.get(i).id == j) {
                return i;
            }
        }
        return -1;
    }
}
