package android.util;

import android.app.ActivityManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import androidx.core.app.ActivityCompat;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.WeatherPreferences;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.weather.WeatherForecast;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class CalendarUtils {
    private static final boolean DEBUG = false;
    private static final String[] PROJECTION_ATTENDEES = {"event_id", LunarContract.AttendeesColumns.ATTENDEE_EMAIL, LunarContract.AttendeesColumns.ATTENDEE_STATUS};
    private static final int SQLITE_MAX_VARIABLE_NUMBER = 980;
    private static final String TAG = "CalendarUtils";

    public static class TimeZoneUtils {
        private static final String CALENAR_PROVIDER_PROCESS = "com.android.providers.calendar";
        public static final String KEY_HOME_TZ = "preferences_home_tz";
        public static final String KEY_HOME_TZ_ENABLED = "preferences_home_tz_enabled";
        private AsyncTZHandler mHandler;
        private final String mPrefsName;
        private static final String[] TIMEZONE_TYPE_ARGS = {LunarContract.CalendarCache.KEY_TIMEZONE_TYPE};
        private static final String[] TIMEZONE_INSTANCES_ARGS = {LunarContract.CalendarCache.KEY_TIMEZONE_INSTANCES};
        private static StringBuilder mSB = new StringBuilder(50);
        private static Formatter mF = new Formatter(mSB, Locale.getDefault());
        private static volatile boolean mFirstTZRequest = true;
        private static volatile boolean mTZQueryInProgress = false;
        private static volatile boolean mUseHomeTZ = false;
        private static volatile String mHomeTZ = Time.getCurrentTimezone();
        private static HashSet<Runnable> mTZCallbacks = new HashSet<>();
        private static int mToken = 1;

        private class AsyncTZHandler extends AsyncQueryHandler {
            public AsyncTZHandler(ContentResolver contentResolver) {
                super(contentResolver);
            }

            private boolean isProcessAlive(Context context, String str) {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
                if (runningAppProcesses == null) {
                    return false;
                }
                Iterator<ActivityManager.RunningAppProcessInfo> it = runningAppProcesses.iterator();
                while (it.hasNext()) {
                    if (str.equals(it.next().processName)) {
                        return true;
                    }
                }
                return false;
            }

            /* JADX WARN: Code duplicated, block: B:48:0x00ad A[Catch: all -> 0x0018, TryCatch #3 {, blocks: (B:6:0x0009, B:7:0x0016, B:14:0x0033, B:15:0x0036, B:46:0x00a7, B:48:0x00ad, B:49:0x00cb, B:50:0x00d6, B:52:0x00dc, B:54:0x00e4, B:55:0x00e8, B:56:0x00ef, B:39:0x008d, B:58:0x00f1, B:59:0x00f4, B:11:0x001b, B:13:0x0026, B:17:0x0038, B:19:0x0045, B:21:0x004b, B:23:0x005b, B:27:0x0066, B:29:0x006c, B:45:0x0096, B:31:0x0071, B:33:0x0079, B:35:0x007f, B:37:0x0089), top: B:63:0x0007, inners: #0 }] */
            /* JADX WARN: Code duplicated, block: B:52:0x00dc A[Catch: all -> 0x0018, TryCatch #3 {, blocks: (B:6:0x0009, B:7:0x0016, B:14:0x0033, B:15:0x0036, B:46:0x00a7, B:48:0x00ad, B:49:0x00cb, B:50:0x00d6, B:52:0x00dc, B:54:0x00e4, B:55:0x00e8, B:56:0x00ef, B:39:0x008d, B:58:0x00f1, B:59:0x00f4, B:11:0x001b, B:13:0x0026, B:17:0x0038, B:19:0x0045, B:21:0x004b, B:23:0x005b, B:27:0x0066, B:29:0x006c, B:45:0x0096, B:31:0x0071, B:33:0x0079, B:35:0x007f, B:37:0x0089), top: B:63:0x0007, inners: #0 }] */
            /* JADX WARN: Code duplicated, block: B:82:0x00e4 A[SYNTHETIC] */
            /* JADX WARN: Code duplicated, block: B:84:0x00d6 A[SYNTHETIC] */
            @Override // android.content.AsyncQueryHandler
            protected void onQueryComplete(int i, Object obj, Cursor cursor) {
                synchronized (TimeZoneUtils.mTZCallbacks) {
                    boolean z = true;
                    try {
                        if (cursor == null) {
                            boolean unused = TimeZoneUtils.mTZQueryInProgress = false;
                            TimeZoneUtils.mTZCallbacks.clear();
                            boolean unused2 = TimeZoneUtils.mFirstTZRequest = true;
                            return;
                        }
                        try {
                            if (!isProcessAlive((Context) obj, TimeZoneUtils.CALENAR_PROVIDER_PROCESS)) {
                                boolean unused3 = TimeZoneUtils.mTZQueryInProgress = false;
                                TimeZoneUtils.mTZCallbacks.clear();
                                cursor.close();
                                cursor.close();
                                return;
                            }
                            int columnIndexOrThrow = cursor.getColumnIndexOrThrow(LunarContract.CalendarCacheColumns.KEY);
                            int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("value");
                            boolean z2 = false;
                            while (cursor.moveToNext()) {
                                try {
                                    String string = cursor.getString(columnIndexOrThrow);
                                    String string2 = cursor.getString(columnIndexOrThrow2);
                                    if (TextUtils.equals(string, LunarContract.CalendarCache.KEY_TIMEZONE_TYPE)) {
                                        boolean z3 = !TextUtils.equals(string2, LunarContract.CalendarCache.TIMEZONE_TYPE_AUTO);
                                        if (z3 != TimeZoneUtils.mUseHomeTZ) {
                                            try {
                                                boolean unused4 = TimeZoneUtils.mUseHomeTZ = z3;
                                                z2 = true;
                                            } catch (NullPointerException unused5) {
                                                Log.e(CalendarUtils.TAG, "A fatal error occurs, calendar will run again");
                                                boolean unused6 = TimeZoneUtils.mTZQueryInProgress = false;
                                                TimeZoneUtils.mTZCallbacks.clear();
                                                cursor.close();
                                                z2 = z;
                                                if (z2) {
                                                    SharedPreferences sharedPreferences = CalendarUtils.getSharedPreferences((Context) obj, TimeZoneUtils.this.mPrefsName);
                                                    CalendarUtils.setSharedPreference(sharedPreferences, TimeZoneUtils.KEY_HOME_TZ_ENABLED, TimeZoneUtils.mUseHomeTZ);
                                                    CalendarUtils.setSharedPreference(sharedPreferences, TimeZoneUtils.KEY_HOME_TZ, TimeZoneUtils.mHomeTZ);
                                                }
                                                boolean unused7 = TimeZoneUtils.mTZQueryInProgress = false;
                                                for (Runnable runnable : TimeZoneUtils.mTZCallbacks) {
                                                    if (runnable != null) {
                                                        runnable.run();
                                                    }
                                                }
                                                TimeZoneUtils.mTZCallbacks.clear();
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (TextUtils.equals(string, LunarContract.CalendarCache.KEY_TIMEZONE_INSTANCES_PREVIOUS) && !TextUtils.isEmpty(string2) && !TextUtils.equals(TimeZoneUtils.mHomeTZ, string2)) {
                                        String unused8 = TimeZoneUtils.mHomeTZ = string2;
                                        z2 = true;
                                    }
                                } catch (NullPointerException unused9) {
                                    z = z2;
                                }
                            }
                            cursor.close();
                            if (z2) {
                                SharedPreferences sharedPreferences2 = CalendarUtils.getSharedPreferences((Context) obj, TimeZoneUtils.this.mPrefsName);
                                CalendarUtils.setSharedPreference(sharedPreferences2, TimeZoneUtils.KEY_HOME_TZ_ENABLED, TimeZoneUtils.mUseHomeTZ);
                                CalendarUtils.setSharedPreference(sharedPreferences2, TimeZoneUtils.KEY_HOME_TZ, TimeZoneUtils.mHomeTZ);
                            }
                            boolean unused10 = TimeZoneUtils.mTZQueryInProgress = false;
                            for (Runnable runnable2 : TimeZoneUtils.mTZCallbacks) {
                                if (runnable2 != null) {
                                    runnable2.run();
                                }
                            }
                            TimeZoneUtils.mTZCallbacks.clear();
                        } catch (NullPointerException unused11) {
                            z = false;
                        }
                    } catch (Throwable th) {
                        cursor.close();
                        throw th;
                    }
                }
            }
        }

        public TimeZoneUtils(String str) {
            this.mPrefsName = str;
        }

        public String formatDateRange(Context context, long j, long j2, int i) {
            String string;
            String timeZone = (i & 8192) != 0 ? "UTC" : getTimeZone(context, null);
            synchronized (mSB) {
                mSB.setLength(0);
                string = DateUtils.formatDateRange(context, mF, j, j2, i, timeZone).toString();
            }
            return string;
        }

        public void setTimeZone(Context context, String str) {
            boolean z;
            if (TextUtils.isEmpty(str)) {
                return;
            }
            synchronized (mTZCallbacks) {
                if (LunarContract.CalendarCache.TIMEZONE_TYPE_AUTO.equals(str)) {
                    z = mUseHomeTZ;
                    mUseHomeTZ = false;
                } else {
                    boolean z2 = (mUseHomeTZ && TextUtils.equals(mHomeTZ, str)) ? false : true;
                    mUseHomeTZ = true;
                    mHomeTZ = str;
                    z = z2;
                }
            }
            if (z) {
                SharedPreferences sharedPreferences = CalendarUtils.getSharedPreferences(context, this.mPrefsName);
                CalendarUtils.setSharedPreference(sharedPreferences, KEY_HOME_TZ_ENABLED, mUseHomeTZ);
                CalendarUtils.setSharedPreference(sharedPreferences, KEY_HOME_TZ, mHomeTZ);
                ContentValues contentValues = new ContentValues();
                AsyncTZHandler asyncTZHandler = this.mHandler;
                if (asyncTZHandler != null) {
                    asyncTZHandler.cancelOperation(mToken);
                }
                this.mHandler = new AsyncTZHandler(context.getContentResolver());
                int i = mToken + 1;
                mToken = i;
                if (i == 0) {
                    mToken = 1;
                }
                contentValues.put("value", mUseHomeTZ ? LunarContract.CalendarCache.TIMEZONE_TYPE_HOME : LunarContract.CalendarCache.TIMEZONE_TYPE_AUTO);
                AsyncTZHandler asyncTZHandler2 = this.mHandler;
                int i2 = mToken;
                Uri uri = CalendarContract.CalendarCache.URI;
                String[] strArr = TIMEZONE_TYPE_ARGS;
                asyncTZHandler2.startUpdate(i2, null, uri, contentValues, "key=?", strArr);
                boolean zIsLunarPluginExist = LunarAvailabilityManager.isLunarPluginExist(context);
                if (zIsLunarPluginExist) {
                    this.mHandler.startUpdate(mToken, null, LunarContract.CalendarCache.URI, contentValues, "key=?", strArr);
                }
                if (mUseHomeTZ) {
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put("value", mHomeTZ);
                    AsyncTZHandler asyncTZHandler3 = this.mHandler;
                    int i3 = mToken;
                    Uri uri2 = CalendarContract.CalendarCache.URI;
                    String[] strArr2 = TIMEZONE_INSTANCES_ARGS;
                    asyncTZHandler3.startUpdate(i3, null, uri2, contentValues2, "key=?", strArr2);
                    if (zIsLunarPluginExist) {
                        this.mHandler.startUpdate(mToken, null, LunarContract.CalendarCache.URI, contentValues2, "key=?", strArr2);
                    }
                }
            }
        }

        public String getTimeZone(Context context, Runnable runnable) {
            synchronized (mTZCallbacks) {
                if (mFirstTZRequest) {
                    mTZQueryInProgress = true;
                    mFirstTZRequest = false;
                    SharedPreferences sharedPreferences = CalendarUtils.getSharedPreferences(context, this.mPrefsName);
                    mUseHomeTZ = sharedPreferences.getBoolean(KEY_HOME_TZ_ENABLED, false);
                    mHomeTZ = sharedPreferences.getString(KEY_HOME_TZ, Time.getCurrentTimezone());
                    if (this.mHandler == null) {
                        this.mHandler = new AsyncTZHandler(context.getContentResolver());
                    }
                    this.mHandler.startQuery(0, context, CalendarContract.CalendarCache.URI, new String[]{LunarContract.CalendarCacheColumns.KEY, "value"}, null, null, null);
                }
                if (mTZQueryInProgress) {
                    mTZCallbacks.add(runnable);
                }
            }
            return mUseHomeTZ ? mHomeTZ : Time.getCurrentTimezone();
        }
    }

    public static void setSharedPreference(SharedPreferences sharedPreferences, String str, String str2) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putString(str, str2);
        editorEdit.apply();
    }

    public static void setSharedPreference(SharedPreferences sharedPreferences, String str, boolean z) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putBoolean(str, z);
        editorEdit.apply();
    }

    public static SharedPreferences getSharedPreferences(Context context, String str) {
        return context.getSharedPreferences(str, 0);
    }

    public static boolean isCelsius(Context context) {
        return GeneralPreferences.getSharedPreferences(context).getString(WeatherPreferences.KEY_WEATHER_UNIT, context.getString(R.string.preferences_default_weather_unit)).equals(WeatherForecast.VALUE_CELSIUS);
    }

    public static void updateEventSelfAttendeeStatus(Context context, List<EventInfo> list) {
        if (list.isEmpty()) {
            return;
        }
        int i = 0;
        if (list.size() > SQLITE_MAX_VARIABLE_NUMBER) {
            Log.w(TAG, "updateEventSelfAttendeeStatus() received more then SQLITE_MAX_VARIABLE_NUMBER events so list will be shortened");
            list = list.subList(0, SQLITE_MAX_VARIABLE_NUMBER);
        }
        int size = list.size();
        String[] strArr = new String[size];
        StringBuilder sbAppend = new StringBuilder("event_id").append(" IN (");
        for (EventInfo eventInfo : list) {
            sbAppend.append("?");
            if (i < size - 1) {
                sbAppend.append(", ");
            }
            strArr[i] = Long.toString(eventInfo.id);
            i++;
        }
        sbAppend.append(")");
        if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_CALENDAR") != 0) {
            Log.w(TAG, "Unable to query attendees due to lack of permissions!");
            return;
        }
        Cursor cursorQuery = context.getContentResolver().query(CalendarContract.Attendees.CONTENT_URI, PROJECTION_ATTENDEES, sbAppend.toString(), strArr, null);
        if (cursorQuery != null) {
            try {
                if (cursorQuery.moveToFirst()) {
                    do {
                        int i2 = cursorQuery.getInt(cursorQuery.getColumnIndex("event_id"));
                        int i3 = cursorQuery.getInt(cursorQuery.getColumnIndex(LunarContract.AttendeesColumns.ATTENDEE_STATUS));
                        String string = cursorQuery.getString(cursorQuery.getColumnIndex(LunarContract.AttendeesColumns.ATTENDEE_EMAIL));
                        for (EventInfo eventInfo2 : list) {
                            if (eventInfo2.id == i2 && string != null && string.equalsIgnoreCase(eventInfo2.ownerAccount)) {
                                eventInfo2.selfAttendeeStatus = i3;
                            }
                        }
                    } while (cursorQuery.moveToNext());
                }
            } catch (Throwable th) {
                if (cursorQuery != null) {
                    try {
                        cursorQuery.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (cursorQuery != null) {
            cursorQuery.close();
        }
    }
}
