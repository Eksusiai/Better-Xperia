package com.sonymobile.calendar.alerts;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.os.BuildCompat;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.PeriodicWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.lunar.LuniSolarCursorManager;
import com.sonymobile.calendar.utils.NotificationUtils;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class AlertWork extends Worker {
    private static final String ACTIVE_ALERTS_SELECTION = "(state=? OR state=?) AND alarmTime<=";
    private static final String ACTIVE_ALERTS_SORT = "begin DESC, end DESC";
    private static final int ALERT_INDEX_ALARM_TIME = 7;
    private static final int ALERT_INDEX_ALL_DAY = 6;
    private static final int ALERT_INDEX_BEGIN = 9;
    private static final int ALERT_INDEX_DESCRIPTION = 12;
    private static final int ALERT_INDEX_END = 10;
    private static final int ALERT_INDEX_EVENT_ID = 1;
    private static final int ALERT_INDEX_EVENT_LOCATION = 4;
    private static final int ALERT_INDEX_ID = 0;
    private static final int ALERT_INDEX_IS_LUNAREVENT = 13;
    private static final int ALERT_INDEX_MINUTES = 8;
    private static final int ALERT_INDEX_SELF_ATTENDEE_STATUS = 5;
    private static final int ALERT_INDEX_STATE = 2;
    private static final int ALERT_INDEX_TITLE = 3;
    private static final int CHANNEL_HIGH = 1;
    static final boolean DEBUG = false;
    private static final String DISMISS_OLD_SELECTION = "end<? AND state=?";
    private static final int EVENT_STATUS = 11;
    private static final String LUNAR_EVENT = "isLunarEvent";
    public static final int MAX_NOTIFICATIONS = 21;
    private static final int MINUTE_MS = 60000;
    private static final int MIN_DEPRIORITIZE_GRACE_PERIOD_MS = 900000;
    private static final String TAG = "AlertService";
    private static NotificationUtils mNotificationUtils;
    private static NotificationChannel mNotifyChannelHigh;
    private volatile Context mContext;
    private volatile ServiceHandler mServiceHandler;
    private volatile Looper mServiceLooper;
    private static final String[] ALERT_PROJECTION = {"_id", "event_id", "state", LunarContract.EventsColumns.TITLE, LunarContract.EventsColumns.EVENT_LOCATION, LunarContract.EventsColumns.SELF_ATTENDEE_STATUS, "allDay", "alarmTime", "minutes", "begin", "end", LunarContract.EventsColumns.STATUS, "description"};
    private static final String[] EVENT_PROJECTION = {"begin", "end", "event_id", "_id"};
    public static final long[] CALENDAR_NOTIFICATION_VIBRATION_PATTERN = {0, 300, 100, 100};
    private static final long[] NO_VIBRATE = {0};
    private static final String[] ACTIVE_ALERTS_SELECTION_ARGS = {Integer.toString(1), Integer.toString(0)};

    public static class NotificationWrapper {
        long mBegin;
        long mEnd;
        long mEventId;
        Notification mNotification;
        ArrayList<NotificationWrapper> mNw;

        public NotificationWrapper(NotificationCompat.Builder builder, long j, long j2, long j3) {
            builder.setCategory("event");
            this.mNotification = builder.build();
            this.mEventId = j;
            this.mBegin = j2;
            this.mEnd = j3;
        }

        public NotificationWrapper(Notification notification) {
            this.mNotification = notification;
        }

        public void add(NotificationWrapper notificationWrapper) {
            if (this.mNw == null) {
                this.mNw = new ArrayList<>();
            }
            this.mNw.add(notificationWrapper);
        }
    }

    public static class NotificationMgrWrapper implements NotificationMgr {
        NotificationManagerCompat mNm;

        public NotificationMgrWrapper(NotificationManagerCompat notificationManagerCompat) {
            this.mNm = notificationManagerCompat;
        }

        @Override // com.sonymobile.calendar.alerts.NotificationMgr
        public void cancel(int i) {
            this.mNm.cancel(i);
        }

        @Override // com.sonymobile.calendar.alerts.NotificationMgr
        public void cancel(String str, int i) {
            this.mNm.cancel(str, i);
        }

        @Override // com.sonymobile.calendar.alerts.NotificationMgr
        public void cancelAll() {
            this.mNm.cancelAll();
        }

        @Override // com.sonymobile.calendar.alerts.NotificationMgr
        public void notify(int i, NotificationWrapper notificationWrapper) {
            this.mNm.notify(i, notificationWrapper.mNotification);
        }

        @Override // com.sonymobile.calendar.alerts.NotificationMgr
        public void notify(String str, int i, NotificationWrapper notificationWrapper) {
            this.mNm.notify(str, i, notificationWrapper.mNotification);
        }
    }

    void processMessage(Message message) {
        String string = ((Data) message.obj).getString("action");
        if (string == null) {
        }
        string.hashCode();
        switch (string) {
            case "android.intent.action.EVENT_REMINDER":
            case "android.intent.action.LOCALE_CHANGED":
            case "android.intent.action.PROVIDER_CHANGED":
                updateAlertNotification(this.mContext);
                break;
            case "removeOldReminders":
                dismissOldAlerts(this.mContext);
                break;
            case "android.intent.action.TIME_SET":
            case "android.intent.action.BOOT_COMPLETED":
                doTimeChanged();
                break;
            default:
                Log.w(TAG, "Invalid action: " + string);
                break;
        }
    }

    static void dismissOldAlerts(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        long jCurrentTimeMillis = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", (Integer) 2);
        contentResolver.update(CalendarContract.CalendarAlerts.CONTENT_URI, contentValues, DISMISS_OLD_SELECTION, new String[]{Long.toString(jCurrentTimeMillis), Integer.toString(0)});
        if (LunarAvailabilityManager.isLunarAvailable(context)) {
            contentResolver.update(LunarContract.CalendarAlerts.CONTENT_URI, contentValues, DISMISS_OLD_SELECTION, new String[]{Long.toString(jCurrentTimeMillis), Integer.toString(0)});
        }
    }

    static boolean updateAlertNotification(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        long jCurrentTimeMillis = System.currentTimeMillis();
        NotificationMgrWrapper notificationMgrWrapper = new NotificationMgrWrapper(NotificationManagerCompat.from(context));
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(context);
        if (!sharedPreferences.getBoolean(GeneralPreferences.KEY_ALERTS, true)) {
            notificationMgrWrapper.cancelAll();
            return true;
        }
        Uri uri = CalendarContract.CalendarAlerts.CONTENT_URI;
        String[] strArr = ALERT_PROJECTION;
        String str = ACTIVE_ALERTS_SELECTION + jCurrentTimeMillis;
        String[] strArr2 = ACTIVE_ALERTS_SELECTION_ARGS;
        Cursor cursorQuery = contentResolver.query(uri, strArr, str, strArr2, ACTIVE_ALERTS_SORT);
        Cursor cursorQuery2 = LunarAvailabilityManager.isLunarAvailable(context) ? contentResolver.query(LunarContract.CalendarAlerts.CONTENT_URI, strArr, ACTIVE_ALERTS_SELECTION + jCurrentTimeMillis, strArr2, ACTIVE_ALERTS_SORT) : null;
        MatrixCursor matrixCursorProcessCursor = new LuniSolarCursorManager(cursorQuery, cursorQuery2, LunarAvailabilityManager.isLunarAvailable(context)) { // from class: com.sonymobile.calendar.alerts.AlertWork.1
            @Override // com.sonymobile.calendar.lunar.LuniSolarCursorManager
            public boolean isSolarCursorProper(Cursor cursor, Cursor cursor2) {
                return cursor.getLong(9) >= cursor2.getLong(9) && cursor.getLong(10) >= cursor2.getLong(10);
            }

            @Override // com.sonymobile.calendar.lunar.LuniSolarCursorManager
            public void mergeCursor(MatrixCursor matrixCursor, Cursor cursor, boolean z) {
                matrixCursor.addRow(new Object[]{Long.valueOf(cursor.getLong(0)), Long.valueOf(cursor.getLong(1)), Integer.valueOf(cursor.getInt(2)), cursor.getString(3), cursor.getString(4), Integer.valueOf(cursor.getInt(5)), Integer.valueOf(cursor.getInt(6)), Long.valueOf(cursor.getLong(7)), Integer.valueOf(cursor.getInt(8)), Long.valueOf(cursor.getLong(9)), Long.valueOf(cursor.getLong(10)), Integer.valueOf(cursor.getInt(11)), cursor.getString(12), Integer.valueOf(z ? 1 : 0)});
            }

            @Override // com.sonymobile.calendar.lunar.LuniSolarCursorManager
            public MatrixCursor initMatrixCursor() {
                int length = AlertWork.ALERT_PROJECTION.length;
                String[] strArr3 = new String[length + 1];
                System.arraycopy(AlertWork.ALERT_PROJECTION, 0, strArr3, 0, length);
                strArr3[length] = "isLunarEvent";
                return new MatrixCursor(strArr3);
            }
        }.processCursor();
        if (matrixCursorProcessCursor == null || matrixCursorProcessCursor.getCount() == 0) {
            Utils.closeCursor(cursorQuery);
            Utils.closeCursor(cursorQuery2);
            Utils.closeCursor(matrixCursorProcessCursor);
            notificationMgrWrapper.cancelAll();
            return false;
        }
        return generateAlerts(context, notificationMgrWrapper, sharedPreferences, matrixCursorProcessCursor, jCurrentTimeMillis, 21);
    }

    public static boolean generateAlerts(Context context, NotificationMgr notificationMgr, SharedPreferences sharedPreferences, Cursor cursor, long j, int i) {
        Context context2;
        boolean z;
        long j2;
        NotificationWrapper notificationWrapperMakeDigestNotification;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        int iProcessQuery = processQuery(cursor, context, j, arrayList, arrayList2, arrayList3);
        if (arrayList.size() + arrayList2.size() + arrayList3.size() == 0) {
            notificationMgr.cancelAll();
            return true;
        }
        boolean z2 = iProcessQuery == 0;
        redistributeBuckets(arrayList, arrayList2, arrayList3, i);
        setupNotificationChannel(context);
        if (mNotifyChannelHigh == null) {
            mNotifyChannelHigh = NotificationUtils.getNotifyChannel(1);
        }
        long jMin = Long.MAX_VALUE;
        int i2 = 0;
        while (i2 < arrayList.size()) {
            NotificationInfo notificationInfo = (NotificationInfo) arrayList.get(i2);
            postNotification(notificationInfo, AlertUtils.formatTimeLocation(context, notificationInfo.startMillis, notificationInfo.endMillis, notificationInfo.allDay, notificationInfo.location), context, z2, mNotifyChannelHigh, notificationMgr, (int) notificationInfo.eventId);
            jMin = Math.min(jMin, getNextRefreshTime(notificationInfo, j));
            i2++;
            arrayList = arrayList;
            arrayList2 = arrayList2;
        }
        ArrayList arrayList4 = arrayList2;
        long jMin2 = jMin;
        for (int size = arrayList4.size() - 1; size >= 0; size--) {
            NotificationInfo notificationInfo2 = (NotificationInfo) arrayList4.get(size);
            postNotification(notificationInfo2, AlertUtils.formatTimeLocation(context, notificationInfo2.startMillis, notificationInfo2.endMillis, notificationInfo2.allDay, notificationInfo2.location), context, z2, mNotifyChannelHigh, notificationMgr, (int) notificationInfo2.eventId);
            jMin2 = Math.min(jMin2, getNextRefreshTime(notificationInfo2, j));
        }
        int size2 = arrayList3.size();
        if (size2 > 0) {
            String digestTitle = getDigestTitle(arrayList3);
            if (size2 == 1) {
                NotificationInfo notificationInfo3 = (NotificationInfo) arrayList3.get(0);
                z = true;
                j2 = jMin2;
                notificationWrapperMakeDigestNotification = AlertReceiver.makeBasicNotification(context, notificationInfo3.eventName, AlertUtils.formatTimeLocation(context, notificationInfo3.startMillis, notificationInfo3.endMillis, notificationInfo3.allDay, notificationInfo3.location), notificationInfo3.startMillis, notificationInfo3.endMillis, notificationInfo3.eventId, (int) notificationInfo3.eventId, notificationInfo3.isLunarEvent, notificationInfo3.instanceId, mNotifyChannelHigh);
                context2 = context;
            } else {
                context2 = context;
                z = true;
                j2 = jMin2;
                notificationWrapperMakeDigestNotification = AlertReceiver.makeDigestNotification(context2, arrayList3, digestTitle);
            }
            if (notificationWrapperMakeDigestNotification != null) {
                notificationMgr.notify((int) notificationWrapperMakeDigestNotification.mEventId, notificationWrapperMakeDigestNotification);
            }
            jMin2 = j2;
        } else {
            context2 = context;
            z = true;
            notificationMgr.cancel(0);
        }
        if (jMin2 < Long.MAX_VALUE && jMin2 > j) {
            AlertUtils.scheduleNextNotificationRefresh(context2, null, jMin2);
        } else if (jMin2 < j) {
            Log.e(TAG, "Illegal state: next notification refresh time found to be in the past.");
        }
        return z;
    }

    static void redistributeBuckets(ArrayList<NotificationInfo> arrayList, ArrayList<NotificationInfo> arrayList2, ArrayList<NotificationInfo> arrayList3, int i) {
        if (arrayList.size() > i) {
            arrayList3.addAll(0, arrayList2);
            List<NotificationInfo> listSubList = arrayList.subList(0, arrayList.size() - i);
            arrayList3.addAll(0, listSubList);
            arrayList2.clear();
            listSubList.clear();
        }
        if (arrayList2.size() + arrayList.size() > i) {
            List<NotificationInfo> listSubList2 = arrayList2.subList(i - arrayList.size(), arrayList2.size());
            arrayList3.addAll(0, listSubList2);
            listSubList2.clear();
        }
        if (BuildCompat.isAtLeastN()) {
            arrayList2.addAll(arrayList3);
            arrayList3.clear();
        }
    }

    private static void logEventIdsBumped(List<NotificationInfo> list, List<NotificationInfo> list2) {
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            Iterator<NotificationInfo> it = list.iterator();
            while (it.hasNext()) {
                sb.append(it.next().eventId);
                sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
            }
        }
        if (list2 != null) {
            Iterator<NotificationInfo> it2 = list2.iterator();
            while (it2.hasNext()) {
                sb.append(it2.next().eventId);
                sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
            }
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 1);
        }
        if (sb.length() > 0) {
            Log.d(TAG, "Reached max postings, bumping event IDs {" + sb.toString() + "} to digest.");
        }
    }

    private static long getNextRefreshTime(NotificationInfo notificationInfo, long j) {
        long gracePeriodMs = notificationInfo.startMillis + getGracePeriodMs(notificationInfo.startMillis, notificationInfo.endMillis);
        long jMin = gracePeriodMs > j ? Math.min(Long.MAX_VALUE, gracePeriodMs) : Long.MAX_VALUE;
        if (notificationInfo.endMillis > j && notificationInfo.endMillis > gracePeriodMs) {
            jMin = Math.min(jMin, notificationInfo.endMillis);
        }
        return Math.min(jMin, notificationInfo.endMillis + 60000);
    }

    /* JADX WARN: Code duplicated, block: B:68:0x01a3  */
    static int processQuery(Cursor cursor, Context context, long j, ArrayList<NotificationInfo> arrayList, ArrayList<NotificationInfo> arrayList2, ArrayList<NotificationInfo> arrayList3) {
        Uri uri;
        int i;
        int i2;
        int i3;
        String id;
        long gracePeriodMs;
        long jConvertAlldayUtcToLocal;
        boolean z;
        ContentResolver contentResolver = context.getContentResolver();
        HashMap map = new HashMap();
        try {
            cursor.moveToPosition(-1);
            int i4 = 0;
            int i5 = 0;
            while (cursor.moveToNext()) {
                long j2 = cursor.getLong(i4);
                long j3 = cursor.getLong(1);
                cursor.getInt(8);
                String string = cursor.getString(3);
                String string2 = cursor.getString(12);
                String string3 = cursor.getString(4);
                int i6 = cursor.getInt(5) == 2 ? 1 : i4;
                long j4 = cursor.getLong(9);
                int i7 = i5;
                long j5 = cursor.getLong(10);
                boolean z2 = cursor.getInt(13) == 1;
                if (z2) {
                    uri = LunarContract.CalendarAlerts.CONTENT_URI;
                } else {
                    uri = CalendarContract.CalendarAlerts.CONTENT_URI;
                }
                Uri uriWithAppendedId = ContentUris.withAppendedId(uri, j2);
                cursor.getLong(7);
                int i8 = cursor.getInt(2);
                boolean z3 = cursor.getInt(6) != 0;
                Uri.Builder builderBuildUpon = CalendarContract.Instances.CONTENT_URI.buildUpon();
                ContentUris.appendId(builderBuildUpon, j4);
                ContentUris.appendId(builderBuildUpon, j5);
                Cursor cursorQuery = contentResolver.query(builderBuildUpon.build(), EVENT_PROJECTION, "event_id=?", new String[]{"" + j3}, null);
                if (cursorQuery != null) {
                    try {
                        cursorQuery.moveToPosition(-1);
                        long j6 = -1;
                        while (cursorQuery.moveToNext()) {
                            j6 = cursorQuery.getLong(cursorQuery.getColumnIndex("_id"));
                        }
                        Utils.closeCursor(cursorQuery);
                        ContentValues contentValues = new ContentValues();
                        if (j5 < j || i6 != 0) {
                            i = i7;
                            i2 = -1;
                            i3 = 2;
                        } else if (i8 == 0) {
                            i = i7 + 1;
                            contentValues.put("receivedTime", Long.valueOf(j));
                            i2 = -1;
                            i3 = 1;
                        } else {
                            i = i7;
                            i2 = -1;
                            i3 = -1;
                        }
                        if (i3 != i2) {
                            contentValues.put("state", Integer.valueOf(i3));
                        } else {
                            i3 = i8;
                        }
                        if (i3 == 1) {
                            contentValues.put("notifyTime", Long.valueOf(j));
                        }
                        if (contentValues.size() > 0) {
                            contentResolver.update(uriWithAppendedId, contentValues, null, null);
                        }
                        if (i3 != 1) {
                            i = i;
                        } else {
                            NotificationInfo notificationInfo = new NotificationInfo(string, string3, string2, j4, j5, j3, z3, z2, j6);
                            if (z3) {
                                id = TimeZone.getDefault().getID();
                                jConvertAlldayUtcToLocal = Utils.convertAlldayUtcToLocal(null, j4, id);
                                gracePeriodMs = 900000;
                            } else {
                                id = null;
                                gracePeriodMs = getGracePeriodMs(j4, j5);
                                jConvertAlldayUtcToLocal = j4;
                            }
                            if (map.containsKey(Long.valueOf(j3))) {
                                NotificationInfo notificationInfo2 = (NotificationInfo) map.get(Long.valueOf(j3));
                                long jConvertAlldayUtcToLocal2 = notificationInfo2.startMillis;
                                if (z3) {
                                    jConvertAlldayUtcToLocal2 = Utils.convertAlldayUtcToLocal(null, notificationInfo2.startMillis, id);
                                }
                                long j7 = jConvertAlldayUtcToLocal2 - j;
                                long j8 = jConvertAlldayUtcToLocal - j;
                                if (j8 >= 0 || j7 <= 0) {
                                    if (Math.abs(j8) < Math.abs(j7)) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                } else if (Math.abs(j8) < PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS) {
                                    z = true;
                                } else {
                                    z = false;
                                }
                                if (z) {
                                    arrayList.remove(notificationInfo2);
                                    arrayList2.remove(notificationInfo2);
                                }
                            }
                            map.put(Long.valueOf(j3), notificationInfo);
                            if (jConvertAlldayUtcToLocal > j - gracePeriodMs) {
                                arrayList.add(notificationInfo);
                            } else if (z3 && id != null && DateUtils.isToday(jConvertAlldayUtcToLocal)) {
                                arrayList2.add(notificationInfo);
                            } else {
                                arrayList3.add(notificationInfo);
                            }
                        }
                        i5 = i;
                    } catch (Throwable th) {
                        Utils.closeCursor(cursorQuery);
                        throw th;
                    }
                } else {
                    Utils.closeCursor(cursorQuery);
                    i5 = i7;
                }
                i4 = 0;
            }
            int i9 = i5;
            Utils.closeCursor(cursor);
            return i9;
        } catch (Throwable th2) {
            Utils.closeCursor(cursor);
            throw th2;
        }
    }

    private static long getGracePeriodMs(long j, long j2) {
        return Math.max(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, (j2 - j) / 4);
    }

    private static void setupNotificationChannel(Context context) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        mNotificationUtils = notificationUtils;
        notificationUtils.createChannels();
    }

    private static String getDigestTitle(ArrayList<NotificationInfo> arrayList) {
        StringBuilder sb = new StringBuilder();
        for (NotificationInfo notificationInfo : arrayList) {
            if (!TextUtils.isEmpty(notificationInfo.eventName)) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(notificationInfo.eventName);
            }
        }
        return sb.toString();
    }

    private static void postNotification(NotificationInfo notificationInfo, String str, Context context, boolean z, NotificationChannel notificationChannel, NotificationMgr notificationMgr, int i) {
        String tickerText = getTickerText(notificationInfo.eventName, notificationInfo.location);
        NotificationWrapper notificationWrapperMakeExpandingNotification = AlertReceiver.makeExpandingNotification(context, notificationInfo.eventName, str, notificationInfo.description, notificationInfo.startMillis, notificationInfo.endMillis, notificationInfo.eventId, i, z, notificationInfo.isLunarEvent, notificationInfo.instanceId, notificationChannel);
        if (!z && !TextUtils.isEmpty(tickerText)) {
            notificationWrapperMakeExpandingNotification.mNotification.tickerText = tickerText;
        }
        notificationMgr.notify(i, notificationWrapperMakeExpandingNotification);
    }

    private static String getTickerText(String str, String str2) {
        return !TextUtils.isEmpty(str2) ? str + " - " + str2 : str;
    }

    static class NotificationInfo {
        long alertId;
        boolean allDay;
        String description;
        long endMillis;
        long eventId;
        String eventName;
        long instanceId;
        boolean isLunarEvent;
        String location;
        long startMillis;

        NotificationInfo(String str, String str2, String str3, long j, long j2, long j3, boolean z, boolean z2, long j4) {
            this.eventName = str;
            this.location = str2;
            this.description = str3;
            this.startMillis = j;
            this.endMillis = j2;
            this.eventId = j3;
            this.allDay = z;
            this.isLunarEvent = z2;
            this.instanceId = j4;
        }
    }

    private void doTimeChanged() {
        rescheduleMissedAlarms(this.mContext.getContentResolver(), this.mContext, (AlarmManager) this.mContext.getSystemService("alarm"));
        updateAlertNotification(this.mContext);
    }

    public static final void rescheduleMissedAlarms(ContentResolver contentResolver, Context context, AlarmManager alarmManager) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j = jCurrentTimeMillis - 86400000;
        String[] strArr = {"alarmTime"};
        Cursor cursorQuery = contentResolver.query(CalendarContract.CalendarAlerts.CONTENT_URI, strArr, "state=0 AND alarmTime<? AND alarmTime>? AND end>=?", new String[]{Long.toString(jCurrentTimeMillis), Long.toString(j), Long.toString(jCurrentTimeMillis)}, "alarmTime ASC");
        Cursor cursorQuery2 = LunarAvailabilityManager.isLunarAvailable(context) ? contentResolver.query(LunarContract.CalendarAlerts.CONTENT_URI, strArr, "state=0 AND alarmTime<? AND alarmTime>? AND end>=?", new String[]{Long.toString(jCurrentTimeMillis), Long.toString(j), Long.toString(jCurrentTimeMillis)}, "alarmTime ASC") : null;
        if (cursorQuery == null && cursorQuery2 == null) {
            return;
        }
        long j2 = -1;
        if (cursorQuery != null) {
            long j3 = -1;
            while (cursorQuery.moveToNext()) {
                try {
                    long j4 = cursorQuery.getLong(0);
                    if (j3 != j4) {
                        AlertUtils.scheduleAlarm(context, alarmManager, j4, false);
                        j3 = j4;
                    }
                } catch (Throwable th) {
                    Utils.closeCursor(cursorQuery);
                    throw th;
                }
            }
            Utils.closeCursor(cursorQuery);
        }
        if (cursorQuery2 != null) {
            while (cursorQuery2.moveToNext()) {
                try {
                    long j5 = cursorQuery2.getLong(0);
                    if (j2 != j5) {
                        AlertUtils.scheduleAlarm(context, alarmManager, j5, true);
                        j2 = j5;
                    }
                } finally {
                    Utils.closeCursor(cursorQuery2);
                }
            }
        }
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AlertWork.this.processMessage(message);
            AlertWork.this.mServiceLooper.quit();
            AlertWork.this.setUsed();
            AlertReceiver.finishStartingService(AlertWork.this);
        }
    }

    public AlertWork(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("AlertWork", 10);
        handlerThread.start();
        this.mServiceLooper = handlerThread.getLooper();
        if (this.mServiceLooper != null) {
            this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
        }
    }

    @Override // androidx.work.Worker
    public ListenableWorker.Result doWork() {
        Data inputData = getInputData();
        if (1 != inputData.getInt("JobId", 99)) {
            return ListenableWorker.Result.failure();
        }
        Message messageObtainMessage = this.mServiceHandler.obtainMessage();
        messageObtainMessage.arg1 = 0;
        messageObtainMessage.obj = inputData;
        this.mServiceHandler.sendMessage(messageObtainMessage);
        return ListenableWorker.Result.success();
    }
}
