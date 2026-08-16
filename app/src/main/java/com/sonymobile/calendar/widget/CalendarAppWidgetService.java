package com.sonymobile.calendar.widget;
import com.sonymobile.calendar.SafeTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.sonyericsson.calendar.util.AlarmCalendarCursorLoader;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.birthday.ContactBirthday;
import com.sonymobile.calendar.birthday.ContactBirthdayLoaderBase;
import com.sonymobile.calendar.birthday.ContactPhotoService;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.lunar.LuniSolarCursorManager;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarAppWidgetService extends RemoteViewsService {
    static final int EVENT_MAX_COUNT = 2200;
    static final int EVENT_MIN_COUNT = 2000;
    static final String[] EVENT_PROJECTION = {"allDay", "begin", "end", LunarContract.EventsColumns.TITLE, LunarContract.EventsColumns.EVENT_LOCATION, "event_id", "startDay", "endDay", LunarContract.EventsColumns.SELF_ATTENDEE_STATUS, LunarContract.CalendarColumns.CALENDAR_COLOR, LunarContract.Instances.START_MINUTE, LunarContract.Instances.END_MINUTE, LunarContract.EventsColumns.EVENT_COLOR, "rrule", "_id", LunarContract.EventsColumns.HAS_ALARM};
    private static final String EVENT_SELECTION = "visible=1";
    private static final String EVENT_SELECTION_HIDE_DECLINED = "visible=1 AND selfAttendeeStatus!=2";
    private static final String EVENT_SORT_ORDER = "startDay ASC, startMinute ASC, endDay ASC, endMinute ASC LIMIT 2200";
    static final int INDEX_ALL_DAY = 0;
    static final int INDEX_BEGIN = 1;
    static final int INDEX_CALENDAR_COLOR = 9;
    static final int INDEX_END = 2;
    static final int INDEX_END_DAY = 7;
    static final int INDEX_END_MINUTE = 11;
    static final int INDEX_EVENT_COLOR = 12;
    static final int INDEX_EVENT_ID = 5;
    static final int INDEX_EVENT_INSTANCE_ID = 14;
    static final int INDEX_EVENT_LOCATION = 4;
    static final int INDEX_EVENT_RRULE = 13;
    static final int INDEX_HAS_ALARM = 15;
    static final int INDEX_IS_LUNAREVENT = 16;
    static final int INDEX_SELF_ATTENDEE_STATUS = 8;
    static final int INDEX_START_DAY = 6;
    static final int INDEX_START_MINUTE = 10;
    static final int INDEX_TITLE = 3;
    static final String LUNAR_EVENT = "is_lunarEvent";
    static final int MAX_DAYS = 3650;
    private static final long SEARCH_DURATION = 315360000000L;
    private static final String TAG = "CalendarWidget";
    private static final long UPDATE_TIME_NO_EVENTS = 21600000;
    static final int WIDGET_UPDATE_THROTTLE = 500;

    @Override // android.widget.RemoteViewsService
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CalendarFactory(getApplicationContext(), intent);
    }

    public static class CalendarFactory extends BroadcastReceiver implements RemoteViewsService.RemoteViewsFactory, Loader.OnLoadCompleteListener<Cursor> {
        private static final boolean LOGD = false;
        public static final int VIEW_TYPE_COUNT = 5;
        private static MatrixCursor mCursor = null;
        private static volatile Integer mLock = 0;
        private static CalendarAppWidgetModel mModel = null;
        private static long sLastUpdateTime = 21600000;
        private int mAppWidgetId;
        private Cursor mBirthdayEventsCursor;
        private Context mContext;
        private int mDeclinedColor;
        private String mLabelAtLocation;
        private String mLabelDash;
        private int mLastLock;
        private CursorLoader mLoader;
        private Cursor mLunarCursor;
        private Cursor mSolarCursor;
        private CursorLoader mLunarLoader = null;
        private Handler mHandler = new Handler();
        private CursorLoader mBirthdayLoader = null;
        private final BroadcastReceiver mbroadcastReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.widget.CalendarAppWidgetService.CalendarFactory.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                CalendarFactory.this.mContext = context;
                if (CalendarFactory.this.mLoader == null || (LunarAvailabilityManager.isLunarAvailable(CalendarFactory.this.mContext) && CalendarFactory.this.mLunarLoader == null)) {
                    CalendarFactory.this.mAppWidgetId = -1;
                    CalendarFactory.this.initLoader();
                } else {
                    CalendarFactory.this.mHandler.removeCallbacks(CalendarFactory.this.mUpdateLoader);
                    CalendarFactory.this.mHandler.post(CalendarFactory.this.mUpdateLoader);
                }
            }
        };
        private final Runnable mTimezoneChanged = new Runnable() { // from class: com.sonymobile.calendar.widget.CalendarAppWidgetService.CalendarFactory.2
            @Override // java.lang.Runnable
            public void run() {
                if (CalendarFactory.this.mLoader != null) {
                    CalendarFactory.this.mLoader.forceLoad();
                }
                if (CalendarFactory.this.mLunarLoader != null) {
                    CalendarFactory.this.mLunarLoader.forceLoad();
                }
                if (CalendarFactory.this.mBirthdayLoader != null) {
                    CalendarFactory.this.mBirthdayLoader.forceLoad();
                }
            }
        };
        private final Runnable mUpdateLoader = new Runnable() { // from class: com.sonymobile.calendar.widget.CalendarAppWidgetService.CalendarFactory.3
            @Override // java.lang.Runnable
            public void run() {
                boolean zIsLunarAvailable = LunarAvailabilityManager.isLunarAvailable(CalendarFactory.this.mContext);
                if (CalendarFactory.this.mLoader != null) {
                    if (!zIsLunarAvailable || (zIsLunarAvailable && CalendarFactory.this.mLunarLoader != null)) {
                        CalendarFactory.this.mLoader.setUri(CalendarFactory.this.createLoaderUri());
                        String str = Utils.getHideDeclinedEvents(CalendarFactory.this.mContext) ? CalendarAppWidgetService.EVENT_SELECTION_HIDE_DECLINED : CalendarAppWidgetService.EVENT_SELECTION;
                        CalendarFactory.this.mLoader.setSelection(str);
                        if (CalendarFactory.this.mLunarLoader != null) {
                            CalendarFactory.this.mLunarLoader.setUri(CalendarFactory.this.createLunarLoaderUri());
                            CalendarFactory.this.mLunarLoader.setSelection(str);
                        }
                        synchronized (CalendarFactory.mLock) {
                            CalendarFactory.this.mLastLock = CalendarFactory.mLock = Integer.valueOf(CalendarFactory.mLock.intValue() + 1).intValue();
                        }
                        CalendarFactory.this.mLoader.forceLoad();
                        if (zIsLunarAvailable) {
                            CalendarFactory.this.mLunarLoader.forceLoad();
                        }
                        if (CalendarFactory.this.mBirthdayLoader != null) {
                            CalendarFactory.this.mBirthdayLoader.forceLoad();
                        }
                    }
                }
            }
        };

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public int getViewTypeCount() {
            return 5;
        }

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public void onDataSetChanged() {
        }

        protected CalendarFactory(Context context, Intent intent) {
            this.mContext = context;
            this.mAppWidgetId = intent.getIntExtra("appWidgetId", 0);
            this.mDeclinedColor = ContextCompat.getColor(context, R.color.appwidget_item_declined_color);
        }

        public CalendarFactory() {
        }

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public void onCreate() {
            initLoader();
            initResources();
            registerReceiver(this.mContext);
        }

        protected void registerReceiver(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.DATE_CHANGED");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
            context.registerReceiver(this.mbroadcastReceiver, intentFilter, 2);
        }

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public void onDestroy() {
            synchronized (mLock) {
                Utils.closeCursor(mCursor);
                Utils.closeCursor(this.mLunarCursor);
                Utils.closeCursor(this.mSolarCursor);
                Utils.closeCursor(this.mBirthdayEventsCursor);
                this.mBirthdayEventsCursor = null;
                this.mSolarCursor = null;
                this.mLunarCursor = null;
                mCursor = null;
                CursorLoader cursorLoader = this.mLoader;
                if (cursorLoader != null) {
                    cursorLoader.reset();
                }
                CursorLoader cursorLoader2 = this.mLunarLoader;
                if (cursorLoader2 != null) {
                    cursorLoader2.reset();
                }
                CursorLoader cursorLoader3 = this.mBirthdayLoader;
                if (cursorLoader3 != null) {
                    cursorLoader3.reset();
                }
                this.mContext.unregisterReceiver(this.mbroadcastReceiver);
            }
        }

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public RemoteViews getLoadingView() {
            return new RemoteViews(this.mContext.getPackageName(), R.layout.widget_loading);
        }

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public RemoteViews getViewAt(int i) {
            RemoteViews remoteViews;
            String str;
            if (i < 0 || i >= getCount()) {
                return getLoadingView();
            }
            String packageName = this.mContext.getPackageName();
            CalendarAppWidgetModel calendarAppWidgetModel = mModel;
            if (calendarAppWidgetModel == null) {
                RemoteViews remoteViews2 = new RemoteViews(packageName, R.layout.widget_loading);
                remoteViews2.setOnClickFillInIntent(R.id.appwidget_loading, CalendarAppWidgetProvider.getLaunchFillInIntent(this.mContext, 0L, 0L, 0L, false, -1L));
                return remoteViews2;
            }
            if ((calendarAppWidgetModel.mBirthDayInfos.isEmpty() && mModel.mEventInfos.isEmpty()) || mModel.mRowInfos.isEmpty() || mModel.mDayInfos.isEmpty()) {
                return new RemoteViews(packageName, R.layout.widget_no_events);
            }
            CalendarAppWidgetModel.RowInfo rowInfo = mModel.mRowInfos.get(i);
            if (rowInfo.mType == 0) {
                remoteViews = new RemoteViews(packageName, R.layout.widget_day_list_item);
                CalendarAppWidgetModel.DayInfo dayInfo = mModel.mDayInfos.get(rowInfo.mIndex);
                Time time = new SafeTime();
                time.setToNow();
                Time time2 = new SafeTime();
                time2.setJulianDay(dayInfo.mJulianDay);
                time2.normalize(true);
                String dateStringConsiderYear = Utils.getDateStringConsiderYear(this.mContext, time, time2);
                if (dayInfo.isFreeDay) {
                    remoteViews.setTextColor(R.id.monthYearHeader, FreeDayService.getInstance().getHolidayColor(this.mContext));
                } else {
                    remoteViews.setTextColor(R.id.monthYearHeader, ContextCompat.getColor(this.mContext, R.color.white));
                }
                updateTextView(remoteViews, R.id.dayOfWeek, 0, dateStringConsiderYear);
            } else if (rowInfo.mType == 2) {
                remoteViews = new RemoteViews(packageName, R.layout.widget_birthday_list_item);
                CalendarAppWidgetModel.DayInfo dayInfo2 = mModel.mDayInfos.get(rowInfo.mIndex);
                int iYearMonthDayToYear = CalendarAppWidgetModel.DayInfo.yearMonthDayToYear(dayInfo2.mYearDayMonth);
                List<ContactBirthday> list = mModel.mBirthDayInfos.get(Integer.valueOf(CalendarAppWidgetModel.DayInfo.yearMonthDayToMonthDay(dayInfo2.mYearDayMonth)));
                if (list.size() == 1) {
                    ContactBirthday contactBirthday = list.get(0);
                    str = contactBirthday.name + " (" + contactBirthday.getAge(iYearMonthDayToYear) + ")";
                } else {
                    str = String.format(this.mContext.getResources().getString(R.string.multiple_birthdays_label), Integer.valueOf(list.size()));
                }
                updateTextView(remoteViews, R.id.birthday_title, 0, str);
                if (list.size() < 3) {
                    setDoublePhotos(this.mContext, remoteViews, list);
                } else {
                    setQuadPhotos(this.mContext, remoteViews, list);
                }
                remoteViews.setOnClickFillInIntent(android.R.id.content, CalendarAppWidgetProvider.getBirthdayFillInIntent(this.mContext, dayInfo2.mMilis));
            } else {
                CalendarAppWidgetModel.EventInfo eventInfo = mModel.mEventInfos.get(rowInfo.mIndex);
                if (eventInfo.isAlarmEvent) {
                    remoteViews = new RemoteViews(packageName, R.layout.widget_list_alarm_item);
                    remoteViews.setInt(R.id.alarm_icon, "setColorFilter", ContextCompat.getColor(this.mContext, R.color.white));
                    updateTextView(remoteViews, R.id.begin_time, 0, eventInfo.beginTime);
                    if (eventInfo.beginAmPm != null) {
                        updateTextView(remoteViews, R.id.begin_am_pm, 0, eventInfo.beginAmPm);
                    } else {
                        remoteViews.setViewVisibility(R.id.begin_am_pm, 8);
                    }
                    if (TextUtils.isEmpty(eventInfo.title)) {
                        updateViewVisibility(remoteViews, R.id.event_title, 8);
                        updateViewVisibility(remoteViews, R.id.alarm_indicator, 8);
                    } else {
                        updateTextView(remoteViews, R.id.event_title, 0, eventInfo.title);
                    }
                    remoteViews.setOnClickFillInIntent(R.id.alarm_list_content, CalendarAppWidgetProvider.getLaunchAlarmIntent());
                } else {
                    remoteViews = new RemoteViews(packageName, R.layout.widget_list_item);
                    updateTextView(remoteViews, R.id.event_title, 0, eventInfo.title);
                    if (!TextUtils.isEmpty(eventInfo.where)) {
                        updateTextView(remoteViews, R.id.dash_or_at, 0, this.mLabelAtLocation);
                        updateTextView(remoteViews, R.id.end_time, 0, eventInfo.where);
                        remoteViews.setViewVisibility(R.id.end_am_pm, 8);
                    } else {
                        int i2 = eventInfo.allDay ? 8 : 0;
                        updateTextView(remoteViews, R.id.dash_or_at, i2, this.mLabelDash);
                        updateTextView(remoteViews, R.id.end_time, 0, eventInfo.endTime);
                        updateTextView(remoteViews, R.id.end_am_pm, i2, eventInfo.endAmPm);
                    }
                    if (eventInfo.selfAttendeeStatus == 2) {
                        remoteViews.setTextColor(R.id.event_title, this.mDeclinedColor);
                        remoteViews.setTextColor(R.id.when, this.mDeclinedColor);
                        remoteViews.setTextColor(R.id.end_time, this.mDeclinedColor);
                    }
                    remoteViews.setInt(R.id.calendar_indicator_dot, "setColorFilter", eventInfo.color);
                    long jConvertAlldayLocalToUTC = eventInfo.start;
                    long jConvertAlldayLocalToUTC2 = eventInfo.end;
                    String str2 = eventInfo.rrule;
                    remoteViews.setViewVisibility(R.id.repeat_icon, (TextUtils.isEmpty(str2) || str2.equals("0")) ? 8 : 0);
                    if (eventInfo.allDay) {
                        String timeZone = Utils.getTimeZone(this.mContext, null);
                        Time time3 = new SafeTime();
                        jConvertAlldayLocalToUTC = Utils.convertAlldayLocalToUTC(time3, jConvertAlldayLocalToUTC, timeZone);
                        jConvertAlldayLocalToUTC2 = Utils.convertAlldayLocalToUTC(time3, jConvertAlldayLocalToUTC2, timeZone);
                        remoteViews.setViewVisibility(R.id.begin_am_pm, 8);
                        updateTextView(remoteViews, R.id.begin_time, 0, eventInfo.beginTime);
                    } else {
                        if (eventInfo.beginAmPm != null) {
                            updateTextView(remoteViews, R.id.begin_am_pm, 0, eventInfo.beginAmPm);
                        } else {
                            remoteViews.setViewVisibility(R.id.begin_am_pm, 8);
                        }
                        updateTextView(remoteViews, R.id.begin_time, 0, eventInfo.beginTime);
                    }
                    remoteViews.setOnClickFillInIntent(android.R.id.content, CalendarAppWidgetProvider.getLaunchFillInIntent(this.mContext, eventInfo.id, jConvertAlldayLocalToUTC, jConvertAlldayLocalToUTC2, eventInfo.isLunarEvent, eventInfo.instanceId));
                }
            }
            return remoteViews;
        }

        private static void setDoublePhotos(Context context, RemoteViews remoteViews, List<ContactBirthday> list) {
            remoteViews.setViewVisibility(R.id.doublePhotoFrame, 0);
            remoteViews.setViewVisibility(R.id.quadPhotoFrame, 8);
            ContactPhotoService contactPhotoService = ContactPhotoService.getInstance();
            setPhoto(remoteViews, contactPhotoService.loadPhotoForContactBirthday(context, list.get(0).contactId), R.id.doublePhotoSlot1);
            if (list.size() > 1) {
                setPhoto(remoteViews, contactPhotoService.loadPhotoForContactBirthday(context, list.get(1).contactId), R.id.doublePhotoSlot2);
            } else {
                remoteViews.setViewVisibility(R.id.doublePhotoSlot2, 8);
            }
        }

        private void setQuadPhotos(Context context, RemoteViews remoteViews, List<ContactBirthday> list) {
            remoteViews.setViewVisibility(R.id.quadPhotoFrame, 0);
            remoteViews.setViewVisibility(R.id.doublePhotoFrame, 8);
            ContactPhotoService contactPhotoService = ContactPhotoService.getInstance();
            setPhoto(remoteViews, contactPhotoService.loadPhotoForContactBirthday(context, list.get(0).contactId), R.id.quadPhotoSlot1);
            setPhoto(remoteViews, contactPhotoService.loadPhotoForContactBirthday(context, list.get(1).contactId), R.id.quadPhotoSlot2);
            setPhoto(remoteViews, contactPhotoService.loadPhotoForContactBirthday(context, list.get(2).contactId), R.id.quadPhotoSlot3);
            if (list.size() == 3) {
                remoteViews.setViewVisibility(R.id.quadPhotoSlot4, 4);
                return;
            }
            if (list.size() == 4) {
                setPhoto(remoteViews, contactPhotoService.loadPhotoForContactBirthday(context, list.get(3).contactId), R.id.quadPhotoSlot4);
                return;
            }
            remoteViews.setViewVisibility(R.id.quadPhotoSlot4, 0);
            Drawable drawableWrap = DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.ic_more_small));
            DrawableCompat.setTint(drawableWrap, ContextCompat.getColor(context, R.color.white));
            remoteViews.setImageViewBitmap(R.id.quadPhotoSlot4, UiUtils.drawableToBitmap(drawableWrap));
        }

        private static void setPhoto(RemoteViews remoteViews, Bitmap bitmap, int i) {
            remoteViews.setViewVisibility(i, 0);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
            float f = width;
            float f2 = height;
            RectF rectF = new RectF(0.0f, 0.0f, f, f2);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            new Canvas(bitmapCreateBitmap).drawRoundRect(rectF, f, f2, paint);
            remoteViews.setImageViewBitmap(i, bitmapCreateBitmap);
        }

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public int getCount() {
            CalendarAppWidgetModel calendarAppWidgetModel = mModel;
            if (calendarAppWidgetModel == null) {
                return 1;
            }
            return Math.max(1, calendarAppWidgetModel.mRowInfos.size());
        }

        @Override // android.widget.RemoteViewsService.RemoteViewsFactory
        public long getItemId(int i) {
            CalendarAppWidgetModel calendarAppWidgetModel = mModel;
            if (calendarAppWidgetModel != null && !calendarAppWidgetModel.mRowInfos.isEmpty()) {
                try {
                    CalendarAppWidgetModel.RowInfo rowInfo = mModel.mRowInfos.get(i);
                    if (rowInfo.mType == 0) {
                        return rowInfo.mIndex;
                    }
                    if (rowInfo.mType == 2) {
                        return rowInfo.mIndex;
                    }
                    CalendarAppWidgetModel.EventInfo eventInfo = mModel.mEventInfos.get(rowInfo.mIndex);
                    return ((((long) ((int) (eventInfo.id ^ (eventInfo.id >>> 32)))) + 31) * 31) + ((long) ((int) (eventInfo.start ^ (eventInfo.start >>> 32))));
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
            return 0L;
        }

        public void initLoader() {
            if (PermissionUtils.isCalendarGranted(this.mContext)) {
                if (PermissionUtils.isReadContactsGranted(this.mContext) && Utils.isBirthdayShowingEnabled(this.mContext)) {
                    CursorLoader cursorLoader = new CursorLoader(this.mContext, ContactsContract.Data.CONTENT_URI, ContactBirthdayLoaderBase.getProjection(), ContactBirthdayLoaderBase.CONTACTS_SELECTION, ContactBirthdayLoaderBase.getContactsArguments(), null);
                    this.mBirthdayLoader = cursorLoader;
                    cursorLoader.setUpdateThrottle(500L);
                    this.mBirthdayLoader.registerListener(this.mAppWidgetId, this);
                    this.mBirthdayLoader.startLoading();
                }
                Uri uriCreateLoaderUri = createLoaderUri();
                String str = Utils.getHideDeclinedEvents(this.mContext) ? CalendarAppWidgetService.EVENT_SELECTION_HIDE_DECLINED : CalendarAppWidgetService.EVENT_SELECTION;
                if (Utils.showAlarms(this.mContext)) {
                    this.mLoader = new AlarmCalendarCursorLoader(this.mContext, uriCreateLoaderUri, CalendarAppWidgetService.EVENT_PROJECTION, str, null, CalendarAppWidgetService.EVENT_SORT_ORDER, true);
                } else {
                    this.mLoader = new CursorLoader(this.mContext, uriCreateLoaderUri, CalendarAppWidgetService.EVENT_PROJECTION, str, null, CalendarAppWidgetService.EVENT_SORT_ORDER);
                }
                this.mLoader.setUpdateThrottle(500L);
                synchronized (mLock) {
                    Integer numValueOf = Integer.valueOf(mLock.intValue() + 1);
                    mLock = numValueOf;
                    this.mLastLock = numValueOf.intValue();
                }
                this.mLoader.registerListener(this.mAppWidgetId, this);
                this.mLoader.startLoading();
                if (LunarAvailabilityManager.isLunarAvailable(this.mContext)) {
                    CursorLoader cursorLoader2 = new CursorLoader(this.mContext, createLunarLoaderUri(), CalendarAppWidgetService.EVENT_PROJECTION, str, null, CalendarAppWidgetService.EVENT_SORT_ORDER);
                    this.mLunarLoader = cursorLoader2;
                    cursorLoader2.setUpdateThrottle(500L);
                    this.mLunarLoader.registerListener(this.mAppWidgetId, this);
                    this.mLunarLoader.startLoading();
                }
            }
        }

        private void initResources() {
            this.mLabelAtLocation = this.mContext.getString(R.string.label_at_location);
            this.mLabelDash = this.mContext.getString(R.string.label_dash);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Uri createLoaderUri() {
            long jCurrentTimeMillis = System.currentTimeMillis();
            return Uri.withAppendedPath(CalendarContract.Instances.CONTENT_BY_DAY_URI, Integer.toString(Time.getJulianDay(jCurrentTimeMillis - 86400000, 0L)) + "/" + Time.getJulianDay(jCurrentTimeMillis + CalendarAppWidgetService.SEARCH_DURATION + 86400000, 0L));
        }

        protected static CalendarAppWidgetModel buildAppWidgetModel(Context context, Cursor cursor, Cursor cursor2, String str) {
            CalendarAppWidgetModel calendarAppWidgetModel = new CalendarAppWidgetModel(context, str);
            calendarAppWidgetModel.buildFromCursors(cursor, cursor2, str);
            return calendarAppWidgetModel;
        }

        private long calculateUpdateTime(CalendarAppWidgetModel calendarAppWidgetModel, long j, String str) {
            long nextMidnightTimeMillis = getNextMidnightTimeMillis(str);
            for (CalendarAppWidgetModel.EventInfo eventInfo : calendarAppWidgetModel.mEventInfos) {
                long j2 = eventInfo.start;
                long j3 = eventInfo.end;
                if (j < j2) {
                    nextMidnightTimeMillis = Math.min(nextMidnightTimeMillis, j2);
                } else if (j < j3) {
                    nextMidnightTimeMillis = Math.min(nextMidnightTimeMillis, j3);
                }
            }
            return nextMidnightTimeMillis;
        }

        private static long getNextMidnightTimeMillis(String str) {
            Time time = new SafeTime();
            time.setToNow();
            time.monthDay++;
            time.hour = 0;
            time.minute = 0;
            time.second = 0;
            long jNormalize = time.normalize(true);
            time.timezone = str;
            time.setToNow();
            time.monthDay++;
            time.hour = 0;
            time.minute = 0;
            time.second = 0;
            return Math.min(jNormalize, time.normalize(true));
        }

        static void updateTextView(RemoteViews remoteViews, int i, int i2, String str) {
            updateViewVisibility(remoteViews, i, i2);
            if (i2 == 0) {
                remoteViews.setTextViewText(i, str);
            }
        }

        static void updateViewVisibility(RemoteViews remoteViews, int i, int i2) {
            remoteViews.setViewVisibility(i, i2);
        }

        @Override // androidx.loader.content.Loader.OnLoadCompleteListener
        public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null || cursor.getColumnNames() == null) {
                return;
            }
            synchronized (mLock) {
                if (this.mLastLock != mLock.intValue()) {
                    Utils.closeCursor(cursor);
                    return;
                }
                String authority = loader instanceof CursorLoader ? ((CursorLoader) loader).getUri().getAuthority() : "com.android.calendar";
                if (authority.equalsIgnoreCase("com.android.calendar")) {
                    Utils.closeCursor(this.mSolarCursor);
                    this.mSolarCursor = cursor;
                } else if (authority.equalsIgnoreCase(LunarContract.AUTHORITY)) {
                    Utils.closeCursor(this.mLunarCursor);
                    this.mLunarCursor = cursor;
                } else if (authority.equalsIgnoreCase("com.android.contacts")) {
                    Utils.closeCursor(this.mBirthdayEventsCursor);
                    this.mBirthdayEventsCursor = cursor;
                }
                long jCurrentTimeMillis = System.currentTimeMillis();
                LuniSolarCursorManager luniSolarCursorManager = new LuniSolarCursorManager(this.mSolarCursor, this.mLunarCursor, LunarAvailabilityManager.isLunarAvailable(this.mContext)) { // from class: com.sonymobile.calendar.widget.CalendarAppWidgetService.CalendarFactory.4
                    @Override // com.sonymobile.calendar.lunar.LuniSolarCursorManager
                    public boolean isSolarCursorProper(Cursor cursor2, Cursor cursor3) {
                        int i = cursor2.getInt(6);
                        int i2 = cursor2.getInt(10);
                        int i3 = cursor2.getInt(7);
                        int i4 = cursor2.getInt(11);
                        int i5 = cursor3.getInt(6);
                        int i6 = cursor3.getInt(10);
                        int i7 = cursor3.getInt(7);
                        int i8 = cursor3.getInt(11);
                        if (i < i5) {
                            return true;
                        }
                        if (i > i5) {
                            return false;
                        }
                        if (i2 < i6) {
                            return true;
                        }
                        if (i2 > i6) {
                            return false;
                        }
                        if (i3 < i7) {
                            return true;
                        }
                        return i3 <= i7 && i4 < i8;
                    }

                    @Override // com.sonymobile.calendar.lunar.LuniSolarCursorManager
                    public void mergeCursor(MatrixCursor matrixCursor, Cursor cursor2, boolean z) {
                        matrixCursor.addRow(new Object[]{Integer.valueOf(cursor2.getInt(0)), Long.valueOf(cursor2.getLong(1)), Long.valueOf(cursor2.getLong(2)), cursor2.getString(3), cursor2.getString(4), Integer.valueOf(cursor2.getInt(5)), Integer.valueOf(cursor2.getInt(6)), Integer.valueOf(cursor2.getInt(7)), Integer.valueOf(cursor2.getInt(8)), Integer.valueOf(cursor2.getInt(9)), Integer.valueOf(cursor2.getInt(10)), Integer.valueOf(cursor2.getInt(11)), Integer.valueOf(cursor2.getInt(12)), cursor2.getString(13), Long.valueOf(cursor2.getLong(14)), Integer.valueOf(cursor2.getInt(15)), Integer.valueOf(z ? 1 : 0)});
                    }

                    @Override // com.sonymobile.calendar.lunar.LuniSolarCursorManager
                    public MatrixCursor initMatrixCursor() {
                        int length = CalendarAppWidgetService.EVENT_PROJECTION.length;
                        String[] strArr = new String[length + 1];
                        System.arraycopy(CalendarAppWidgetService.EVENT_PROJECTION, 0, strArr, 0, length);
                        strArr[length] = CalendarAppWidgetService.LUNAR_EVENT;
                        return new MatrixCursor(strArr);
                    }
                };
                if (!authority.equalsIgnoreCase("com.android.contacts")) {
                    Utils.closeCursor(mCursor);
                    mCursor = luniSolarCursorManager.processCursor();
                } else if (mCursor == null) {
                    return;
                }
                String timeZone = Utils.getTimeZone(this.mContext, this.mTimezoneChanged);
                CalendarAppWidgetModel calendarAppWidgetModelBuildAppWidgetModel = buildAppWidgetModel(this.mContext, this.mBirthdayEventsCursor, mCursor, timeZone);
                mModel = calendarAppWidgetModelBuildAppWidgetModel;
                long jCalculateUpdateTime = calculateUpdateTime(calendarAppWidgetModelBuildAppWidgetModel, jCurrentTimeMillis, timeZone);
                if (jCalculateUpdateTime < jCurrentTimeMillis) {
                    Log.w(CalendarAppWidgetService.TAG, "Encountered bad trigger time " + CalendarAppWidgetService.formatDebugTime(jCalculateUpdateTime, jCurrentTimeMillis));
                    jCalculateUpdateTime = CalendarAppWidgetService.UPDATE_TIME_NO_EVENTS + jCurrentTimeMillis;
                }
                AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
                PendingIntent updateIntent = CalendarAppWidgetProvider.getUpdateIntent(this.mContext);
                alarmManager.cancel(updateIntent);
                Utils.setAlarm(alarmManager, 1, jCalculateUpdateTime, updateIntent);
                Time time = new SafeTime(Utils.getTimeZone(this.mContext, null));
                time.setToNow();
                if (time.normalize(true) != sLastUpdateTime) {
                    Time time2 = new SafeTime(Utils.getTimeZone(this.mContext, null));
                    time2.set(sLastUpdateTime);
                    time2.normalize(true);
                    if (time.year != time2.year || time.yearDay != time2.yearDay) {
                        Intent intent = new Intent(Utils.getWidgetUpdateAction(this.mContext));
                        intent.setClassName(this.mContext, this.mContext.getPackageName() + ".widget.CalendarAppWidgetProvider");
                        this.mContext.sendBroadcast(intent);
                    }
                    sLastUpdateTime = time.toMillis(true);
                }
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.mContext);
                int i = this.mAppWidgetId;
                if (i == -1) {
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(CalendarAppWidgetProvider.getComponentName(this.mContext)), R.id.events_list);
                } else {
                    appWidgetManager.notifyAppWidgetViewDataChanged(i, R.id.events_list);
                }
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.mContext = context;
            if (this.mLoader == null || (LunarAvailabilityManager.isLunarAvailable(context) && this.mLunarLoader == null)) {
                this.mAppWidgetId = -1;
                initLoader();
            } else {
                this.mHandler.removeCallbacks(this.mUpdateLoader);
                this.mHandler.post(this.mUpdateLoader);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Uri createLunarLoaderUri() {
            long jCurrentTimeMillis = System.currentTimeMillis();
            return Uri.withAppendedPath(LunarContract.Instances.CONTENT_URI, Long.toString(jCurrentTimeMillis - 86400000) + "/" + (jCurrentTimeMillis + CalendarAppWidgetService.SEARCH_DURATION + 86400000));
        }
    }

    static String formatDebugTime(long j, long j2) {
        Time time = new SafeTime();
        time.set(j);
        long j3 = j - j2;
        if (j3 > 60000) {
            return String.format("[%d] %s (%+d mins)", Long.valueOf(j), time.format("%H:%M:%S"), Long.valueOf(j3 / 60000));
        }
        return String.format("[%d] %s (%+d secs)", Long.valueOf(j), time.format("%H:%M:%S"), Long.valueOf(j3 / 1000));
    }
}
