package com.sonymobile.calendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.CalendarUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.sonyericsson.calendar.util.CustomizeConfig;
import com.sonymobile.accuweather.WeatherLocation;
import com.sonymobile.calendar.birthday.BirthdayPreferences;
import com.sonymobile.calendar.datetimepicker.date.DatePickerDialog;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;
import com.sonymobile.lunar.lib.LunarUtils;
import com.sonymobile.provider.TasksContract;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class Utils {
    private static final float ALPHA_RANGE = 255.0f;
    public static final String APPWIDGET_DATA_TYPE = "vnd.android.data/update";
    private static final int DEFAULT_WEEK_OFFSET = 999;
    private static final String EAS_ACCOUNT_TYPE_SUFFIX = "exchange";
    public static final int EPOCH_DAY_UPPER_LIMIT = 31;
    public static final int EPOCH_MONTH_UPPER_LIMIT = 11;
    public static final int EPOCH_YEAR_UPPER_LIMIT = 2100;
    public static final String FORMAT_DATE_NORMAL = "cccc d";
    public static final String FORMAT_DATE_YEAR = "yyyy";
    public static final String FORMAT_DAY_DASH_MONTH = "MMMd";
    public static final String FORMAT_DAY_DASH_MONTH_DATE = "EEEE - MMM d";
    private static final int FORMAT_JAPANESE_CHINESE_SHORT_DATE = 24;
    private static final int FORMAT_JAPANESE_MONTH_DATE = 32794;
    private static final int FORMAT_KOREAN_DATE = 20;
    public static final String FORMAT_LONG_DATE = "MMMM d yyyy";
    public static final String FORMAT_MONTH_DASH_YEAR = "MMMMd - yyyy";
    public static final String FORMAT_SHORT_DATE = "MMMM d yyyy";
    public static final String FORMAT_TIMEZONE_GMT = "ZZZZ";
    public static final int FORMAT_TITLE_DAY = 1;
    public static final int FORMAT_TITLE_MONTH = 3;
    public static final int FORMAT_TITLE_MONTH_WITHOUT_YEAR = 5;
    public static final int FORMAT_TITLE_WEEK = 2;
    public static final int FORMAT_TITLE_YEAR = 4;
    public static final String FORMAT_WEEKDAY_LONG = "cccc";
    public static final String FORMAT_WEEK_DAY_DASH_MONTH_DAY = "EEEE ";
    public static final String FORMAT_WEEK_DAY_ONE_LETTER = "ccccc";
    public static final String FORMAT_WEEK_DAY_THREE_LETTERS = "c";
    private static final String GOOGLE_ACCOUNT_TYPE_SUFFIX = "google";
    public static final String INTENT_KEY_DETAIL_VIEW = "DETAIL_VIEW";
    public static final String KEY_QUICK_RESPONSES = "preferences_quick_responses";
    static final String MACHINE_GENERATED_ADDRESS = "calendar.google.com";
    private static final int MINIMUM_DAYS_IN_FIRST_WEEK_MONDAY = 4;
    private static final int MINIMUM_DAYS_IN_FIRST_WEEK_SATURDAY = 2;
    private static final int MINIMUM_DAYS_IN_FIRST_WEEK_SUNDAY = 3;
    public static final String NOMINATIVE_CASE = "LLLL";
    public static final String NOMINATIVE_CASE_SHORT = "LLL";
    private static final int NO_RES_ID = 0;
    private static final int ONE_FRAGMENT = 1;
    public static final Time PRE_EPOCH;
    public static final int SYNC_CALENDAR = 1;
    public static final int SYNC_TASKS = 2;
    private static final String TAG = "Utils";
    public static final int TIME_ROUND_30_MIN = 30;
    public static final String UNICODE_FIGURE_DASH = "–";
    public static final String UNICODE_SPACE = " ";
    public static final String[] englishNthDay;
    private static long sAgendaSelectedBegin;
    private static boolean sAgendaSelectedIsLunarEvent;
    private static final String SHARED_PREFS_NAME = "com.sonymobile.calendar_preferences";
    private static final CalendarUtils.TimeZoneUtils mTZUtils = new CalendarUtils.TimeZoneUtils(SHARED_PREFS_NAME);
    public static final AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(1.0f);
    private static long calendarDisplayTime = 0;
    private static long sAgendaSelectedEventInstanceId = 0;
    public static final Uri CONTENT_ALARMS_PROVIDER_URI = Uri.parse("content://com.sonyericsson.alarm/alarmProvider");
    public static final Uri CONTENT_ALARMS_BY_DAY_URI = Uri.parse("content://com.sonyericsson.alarm/whenbyday");

    public static boolean isMonthClearOfEpochUpperLimit(int i, int i2) {
        return i < EPOCH_YEAR_UPPER_LIMIT || (i == EPOCH_YEAR_UPPER_LIMIT && i2 < EPOCH_MONTH_UPPER_LIMIT);
    }

    public static boolean isWeekClearOfEpochUpperLimit(int i, int i2, int i3) {
        return i < EPOCH_YEAR_UPPER_LIMIT || (i == EPOCH_YEAR_UPPER_LIMIT && (i2 < EPOCH_MONTH_UPPER_LIMIT || i3 < EPOCH_DAY_UPPER_LIMIT));
    }

    static {
        Time time = new SafeTime();
        PRE_EPOCH = time;
        time.set(1969, 11, 31);
        englishNthDay = new String[]{"", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th", "31st"};
    }

    public static boolean isIntentRecipientAvailable(Context context, Intent intent) {
        return isPackageListAccessEnable(context) && context.getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
    }

    public static void setTimeZone(Context context, String str) {
        mTZUtils.setTimeZone(context, str);
    }

    public static String getTimeZone(Context context, Runnable runnable) {
        return mTZUtils.getTimeZone(context, runnable);
    }

    public static String formatDateRange(Context context, long j, long j2, int i) {
        return mTZUtils.formatDateRange(context, j, j2, i);
    }

    public static String[] getSharedPreference(Context context, String str, String[] strArr) {
        Set<String> stringSet = GeneralPreferences.getSharedPreferences(context).getStringSet(str, null);
        return stringSet != null ? (String[]) stringSet.toArray(new String[stringSet.size()]) : strArr;
    }

    public static String getSharedPreference(Context context, String str, String str2) {
        return GeneralPreferences.getSharedPreferences(context).getString(str, str2);
    }

    public static int getSharedPreference(Context context, String str, int i) {
        return GeneralPreferences.getSharedPreferences(context).getInt(str, i);
    }

    public static boolean getSharedPreference(Context context, String str, boolean z) {
        return GeneralPreferences.getSharedPreferences(context).getBoolean(str, z);
    }

    public static boolean isDataTrafficEnabled(Context context) {
        if (CustomizeConfig.isShowingPermissionAcceptanceDialogEnabled(context)) {
            return getSharedPreference(context, GeneralPreferences.KEY_USE_WIFI_AND_MOBILE_DATA, false);
        }
        return true;
    }

    public static boolean isPackageListAccessEnable(Context context) {
        if (CustomizeConfig.isShowingPermissionAcceptanceDialogEnabled(context)) {
            return getSharedPreference(context, GeneralPreferences.KEY_ACCESS_PACKAGE_LIST, false);
        }
        return true;
    }

    public static boolean isReadContactsEnabled(Context context) {
        return PermissionUtils.isReadContactsGranted(context);
    }

    public static boolean isLocationEnabled(Context context) {
        if (!PermissionUtils.isAccessLocationGranted(context)) {
            return false;
        }
        if (CustomizeConfig.isShowingPermissionAcceptanceDialogEnabled(context)) {
            return getSharedPreference(context, GeneralPreferences.KEY_RETRIEVE_LOCATION_INFO, false);
        }
        return true;
    }

    public static boolean isCurrentLocationSet(Context context) {
        try {
            String string = GeneralPreferences.getSharedPreferences(context).getString(WeatherPreferences.KEY_CURRENT_LOCATION, null);
            return string == null || WeatherLocation.fromJSON(new JSONObject(string)).name.equals(getCurrentLocationString(context));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            return false;
        }
    }

    public static boolean isSharedPreferenceContain(Context context, String str) {
        return GeneralPreferences.getSharedPreferences(context).contains(str);
    }

    public static void setSharedPreference(Context context, String str, String str2) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(context).edit();
        editorEdit.putString(str, str2);
        editorEdit.apply();
    }

    static void setSharedPreference(Context context, String str, String[] strArr) {
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(context);
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Collections.addAll(linkedHashSet, strArr);
        sharedPreferences.edit().putStringSet(str, linkedHashSet).apply();
    }

    public static void setSharedPreference(Context context, String str, int i) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(context).edit();
        editorEdit.putInt(str, i);
        editorEdit.apply();
    }

    public static void setSharedPreference(Context context, String str, boolean z) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(context).edit();
        editorEdit.putBoolean(str, z);
        editorEdit.apply();
    }

    public static final long timeFromIntentInMillis(Intent intent) {
        Uri data = intent.getData();
        long longExtra = intent.getLongExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, -1L);
        if (longExtra == -1 && data != null && data.isHierarchical()) {
            List<String> pathSegments = data.getPathSegments();
            if (pathSegments.size() == 2 && pathSegments.get(0).equals("time")) {
                try {
                    longExtra = Long.parseLong(data.getLastPathSegment());
                } catch (NumberFormatException unused) {
                    Log.i("Calendar", "timeFromIntentInMillis: Data existed but no valid time found. Using current time.");
                }
            }
        }
        return longExtra <= 0 ? System.currentTimeMillis() : longExtra;
    }

    public static String join(List<?> list, String str) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Object obj : list) {
            if (z) {
                z = false;
            } else {
                sb.append(str);
            }
            sb.append(obj.toString());
        }
        return sb.toString();
    }

    public static int getFirstDayOfWeek(Context context) {
        int firstDayOfWeek;
        if (context == null) {
            return 0;
        }
        String string = GeneralPreferences.getSharedPreferences(context).getString(GeneralPreferences.KEY_WEEK_START_DAY, GeneralPreferences.WEEK_START_DEFAULT);
        if (GeneralPreferences.WEEK_START_DEFAULT.equals(string)) {
            firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
        } else {
            firstDayOfWeek = Integer.parseInt(string);
        }
        if (firstDayOfWeek == 7) {
            return 6;
        }
        return firstDayOfWeek == 2 ? 1 : 0;
    }

    public static int getWeekNumberOfDay(Context context, Time time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar.setTimeInMillis(time.toMillis(false));
        return getWeekNumberOfDay(context, calendar);
    }

    public static int getWeekNumberOfDay(Context context, Calendar calendar) {
        Calendar calendar2 = Calendar.getInstance(calendar.getTimeZone());
        int firstDayOfWeek = getFirstDayOfWeek(context);
        if (firstDayOfWeek == 0) {
            calendar2.setFirstDayOfWeek(1);
            calendar2.setMinimalDaysInFirstWeek(3);
        } else if (firstDayOfWeek == 6) {
            calendar2.setFirstDayOfWeek(7);
            calendar2.setMinimalDaysInFirstWeek(2);
        } else {
            calendar2.setFirstDayOfWeek(2);
            calendar2.setMinimalDaysInFirstWeek(4);
        }
        calendar2.setTimeInMillis(calendar.getTimeInMillis());
        return calendar2.get(3);
    }

    public static void processBundledBeginEventTime(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(LunarContract.EXTRA_EVENT_BEGIN_TIME)) {
            return;
        }
        setDisplayTime(Long.valueOf(bundle.getLong(LunarContract.EXTRA_EVENT_BEGIN_TIME)));
    }

    public static void setDisplayTime(Long l) {
        calendarDisplayTime = l.longValue();
    }

    public static long getDisplayTime() {
        long j = calendarDisplayTime;
        return j == 0 ? System.currentTimeMillis() : j;
    }

    public static boolean isEventSpanOver24Hour(Event event) {
        return event.endMillis - event.startMillis >= 86400000;
    }

    public static String getCalendarDisplayName(Context context, String str, String str2) {
        if (context == null) {
            return " ";
        }
        return (str == null || !str.equals("LOCAL")) ? str2 : context.getResources().getString(R.string.calendar_strings_default_calendar_txt);
    }

    public static String getTaskAccountDisplayName(Context context, String str, String str2) {
        if (str == null || context == null) {
            return "";
        }
        if (!str.equals("LOCAL")) {
            return str2;
        }
        if (context.getResources().getBoolean(R.bool.tablet_mode)) {
            return context.getResources().getString(R.string.task_default_account_tablet_txt);
        }
        return context.getResources().getString(R.string.task_default_account_txt);
    }

    public static String getTimeZoneDisplayName(String str) {
        return getTimezoneDisplayName(TimeZone.getTimeZone(str));
    }

    public static String getTimezoneDisplayName(TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_TIMEZONE_GMT, Locale.getDefault());
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(new Date());
    }

    public static long convertAlldayUtcToLocal(Time time, long j, String str) {
        if (time == null) {
            time = new SafeTime();
        }
        time.timezone = "UTC";
        time.set(j);
        time.timezone = str;
        return time.normalize(true);
    }

    public static Time convertDayToMidnight(Time time) {
        Time time2 = new SafeTime(time);
        time2.hour = 0;
        time2.minute = 0;
        time2.second = 0;
        return time2;
    }

    public static Time getEndOfDayTime(Time time) {
        Time time2 = new SafeTime(time);
        time2.hour = 23;
        time2.minute = 59;
        time2.second = 59;
        return time2;
    }

    public static long convertMillisToMidnightUTC(Context context, long j) {
        Time time = new SafeTime("UTC");
        Time time2 = new SafeTime(getTimeZone(context, null));
        time2.set(j);
        time.set(time2.monthDay, time2.month, time2.year);
        return time.toMillis(true);
    }

    public static long convertAlldayLocalToUTC(Time time, long j, String str) {
        if (time == null) {
            time = new SafeTime();
        }
        time.timezone = str;
        time.set(j);
        time.timezone = "UTC";
        return time.normalize(true);
    }

    public static String getWidgetUpdateAction(Context context) {
        return context.getPackageName() + ".APPWIDGET_UPDATE";
    }

    public static String getWidgetScheduledUpdateAction(Context context) {
        return context.getPackageName() + ".APPWIDGET_SCHEDULED_UPDATE";
    }

    public static boolean getHideDeclinedEvents(Context context) {
        return GeneralPreferences.getSharedPreferences(context).getBoolean(GeneralPreferences.KEY_HIDE_DECLINED, false);
    }

    public static int interpolate(int i) {
        return Math.round(accelerateInterpolator.getInterpolation(i / ALPHA_RANGE) * ALPHA_RANGE);
    }

    public static String getSearchAuthority(Context context) {
        return context.getPackageName() + ".CalendarRecentSuggestionsProvider";
    }

    public static int dp2px(Context context, float f) {
        return (int) (f * context.getResources().getDisplayMetrics().density);
    }

    public static long getAgendaSelectedEventInstanceId() {
        return sAgendaSelectedEventInstanceId;
    }

    public static void setAgendaSelectedEventInstanceId(long j) {
        sAgendaSelectedEventInstanceId = j;
    }

    public static long getAgendaSelectedBegin() {
        return sAgendaSelectedBegin;
    }

    public static void setAgendaSelectedBegin(long j) {
        sAgendaSelectedBegin = j;
    }

    public static String[] getQuickResponses(Context context) {
        String[] sharedPreference = getSharedPreference(context, "preferences_quick_responses_" + context.getResources().getConfiguration().locale.getDisplayName(), (String[]) null);
        return sharedPreference == null ? context.getResources().getStringArray(R.array.quick_response_defaults) : sharedPreference;
    }

    public static void setQuickResponses(Context context, String[] strArr) {
        setSharedPreference(context, "preferences_quick_responses_" + context.getResources().getConfiguration().locale.getDisplayName(), strArr);
    }

    public static Intent createEmailAttendeesIntent(Resources resources, String str, String str2, List<String> list, List<String> list2, String str3) {
        if (list.size() <= 0) {
            if (list2.size() <= 0) {
                throw new IllegalArgumentException("Both toEmails and ccEmails are empty.");
            }
            list = list2;
            list2 = null;
        }
        String str4 = str != null ? resources.getString(R.string.clr_strings_subject_reply_txt) + str : null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("mailto");
        if (list.size() > 1) {
            for (int i = 1; i < list.size(); i++) {
                builder.appendQueryParameter("to", list.get(i));
            }
        }
        if (str4 != null) {
            builder.appendQueryParameter("subject", str4);
        }
        if (str2 != null) {
            builder.appendQueryParameter(TasksContract.TasksColumns.BODY_DATA, str2);
        }
        if (list2 != null && list2.size() > 0) {
            Iterator<String> it = list2.iterator();
            while (it.hasNext()) {
                builder.appendQueryParameter("cc", it.next());
            }
        }
        String string = builder.toString();
        if (string.startsWith("mailto:")) {
            StringBuilder sb = new StringBuilder(string);
            sb.insert(7, Uri.encode(list.get(0)));
            string = sb.toString();
        }
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(string));
        intent.putExtra("fromAccountString", str3);
        return Intent.createChooser(intent, resources.getString(R.string.email_picker_label));
    }

    public static boolean deviceCanPerformCall(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getPhoneType() != 0;
    }

    public static boolean isEmailableFrom(String str, String str2) {
        return isValidEmail(str) && !str.equals(str2);
    }

    public static boolean isValidEmail(String str) {
        return (TextUtils.isEmpty(str) || str.endsWith(MACHINE_GENERATED_ADDRESS)) ? false : true;
    }

    public static boolean shouldShowTodayLabel(Context context, String str) {
        Time time = new SafeTime(str);
        time.set(getDisplayTime());
        time.normalize(false);
        return getOffset(time) != 0;
    }

    public static boolean isAnyLocationProviderEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(TasksContract.TaskLists.LOCATION);
        if (locationManager != null) {
            return locationManager.isProviderEnabled("network") || locationManager.isProviderEnabled("gps");
        }
        return false;
    }

    public static int getOffset(Time time) {
        Time time2 = new SafeTime(time.timezone);
        time2.setToNow();
        return Time.getJulianDay(time.normalize(false), time.gmtoff) - Time.getJulianDay(time2.normalize(false), time2.gmtoff);
    }

    public static String getNaturalLanguageWeekLabel(Context context, Time time) {
        int weekOffset = getWeekOffset(time);
        if (weekOffset == -1) {
            return context.getString(R.string.header_last_week);
        }
        if (weekOffset == 0) {
            return context.getString(R.string.header_this_week);
        }
        if (weekOffset != 1) {
            return null;
        }
        return context.getString(R.string.header_next_week);
    }

    private static int getWeekOffset(Time time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        int i = calendar.get(3);
        int i2 = calendar.get(1);
        calendar.setTimeInMillis(time.toMillis(true));
        int i3 = calendar.get(3);
        if (i2 == calendar.get(1)) {
            return i3 - i;
        }
        return 999;
    }

    public static String getDisplayNameForAccountType(Context context, String str) {
        AuthenticatorDescription[] authenticatorTypes = AccountManager.get(context).getAuthenticatorTypes();
        HashMap map = new HashMap();
        for (AuthenticatorDescription authenticatorDescription : authenticatorTypes) {
            map.put(authenticatorDescription.type, authenticatorDescription);
        }
        if (TextUtils.equals(str, "LOCAL")) {
            return context.getResources().getString(R.string.account_settings_default_label);
        }
        CharSequence labelForType = getLabelForType(str, context, map);
        if (labelForType != null) {
            return labelForType.toString();
        }
        Log.e(TAG, "Account type label is null");
        return context.getResources().getString(R.string.account_settings_default_label);
    }

    private static CharSequence getLabelForType(String str, Context context, Map<String, AuthenticatorDescription> map) {
        if (map.containsKey(str)) {
            try {
                AuthenticatorDescription authenticatorDescription = map.get(str);
                return context.createPackageContext(authenticatorDescription.packageName, 0).getResources().getText(authenticatorDescription.labelId);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            } catch (Resources.NotFoundException e2) {
                Log.e(TAG, Log.getStackTraceString(e2));
            }
        }
        return null;
    }

    public static int compareToTodayMidnight(Time time) {
        Time time2 = new SafeTime();
        time2.setToNow();
        time2.hour = 0;
        time2.minute = 0;
        time2.second = 0;
        time2.normalize(true);
        return Time.compare(time, time2);
    }

    public static boolean isDateToday(Time time) {
        Time time2 = new SafeTime();
        time2.setToNow();
        time2.normalize(false);
        return areDatesEqual(time, time2);
    }

    public static boolean areDatesEqual(Time time, Time time2) {
        return time != null && time2 != null && time.year == time2.year && time.month == time2.month && time.monthDay == time2.monthDay;
    }

    public static Location getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(TasksContract.TaskLists.LOCATION);
        String[] strArr = {"gps", "network"};
        Location location = null;
        long time = 0;
        for (int i = 0; i < 2; i++) {
            String str = strArr[i];
            Location lastKnownLocation = locationManager.getLastKnownLocation(str);
            if (lastKnownLocation != null && lastKnownLocation.getTime() > time) {
                location = lastKnownLocation;
                time = lastKnownLocation.getTime();
            } else {
                locationManager.requestLocationUpdates(str, 0L, 0.0f, new LocationListener() { // from class: com.sonymobile.calendar.Utils.1
                    @Override // android.location.LocationListener
                    public void onLocationChanged(Location location2) {
                    }

                    @Override // android.location.LocationListener
                    public void onProviderDisabled(String str2) {
                    }

                    @Override // android.location.LocationListener
                    public void onProviderEnabled(String str2) {
                    }

                    @Override // android.location.LocationListener
                    public void onStatusChanged(String str2, int i2, Bundle bundle) {
                    }
                });
            }
        }
        return location;
    }

    public static boolean isPackageEnabled(String str, Context context) {
        if (!isPackageListAccessEnable(context)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationEnabledSetting(str) == 1 || packageManager.getApplicationEnabledSetting(str) == 0;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    public static String getWeekDaySpanLabel(Context context, Time time, int i) {
        Time[] days = WeekUtils.getDays(context, time, false);
        return DateUtils.formatDateRange(context, new Formatter(new StringBuilder(10), Locale.getDefault()), days[0].toMillis(false), days[days.length - 1].toMillis(false), i, getTimeZone(context, null)).toString();
    }

    public static String getLunarWeekDaySpanLabel(Context context, Time time) {
        int i;
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar.setTimeInMillis(time.toMillis(false));
        int firstDayOfWeek = getFirstDayOfWeek(context);
        if (firstDayOfWeek == 0) {
            i = time.weekDay;
        } else if (firstDayOfWeek == 6) {
            i = (time.weekDay + 1) % 7;
        } else {
            i = (time.weekDay + 6) % 7;
        }
        calendar.add(6, -i);
        int i2 = calendar.get(1);
        long timeInMillis = calendar.getTimeInMillis();
        calendar.add(6, 6);
        int i3 = calendar.get(1);
        long timeInMillis2 = calendar.getTimeInMillis();
        int i4 = i2 == i3 ? 65556 : 0;
        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar2.setTimeInMillis(timeInMillis);
        Calendar calendar3 = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar3.setTimeInMillis(timeInMillis2);
        Time time2 = new SafeTime(time.timezone);
        time2.set(timeInMillis);
        Time time3 = new SafeTime(time.timezone);
        time3.set(timeInMillis2);
        int julianDay = Time.getJulianDay(timeInMillis, time2.gmtoff);
        int julianDay2 = Time.getJulianDay(timeInMillis2, time3.gmtoff);
        if (timeInMillis != timeInMillis2) {
            if ((time3.second | time3.hour | time3.minute) == 0 && julianDay2 - julianDay <= 1) {
                calendar3.add(5, -1);
            }
        }
        sb.append(DateUtils.formatDateRange(context, timeInMillis, timeInMillis2, i4));
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar2.getTime(), time.timezone);
        String str = LunarUtils.sLunarYearStrings[lunarDateConvertSolarDateToLunarDate.mYear - 1901];
        String lunarMonthString = LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate);
        String lunarDayString = LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate);
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate2 = LunarUtils.convertSolarDateToLunarDate(calendar3.getTime(), time.timezone);
        String str2 = LunarUtils.sLunarYearStrings[lunarDateConvertSolarDateToLunarDate2.mYear - 1901];
        String lunarMonthString2 = LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate2);
        String lunarDayString2 = LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate2);
        String string = context.getResources().getString(R.string.lunar_to);
        sb.append(" | ");
        if (!str.equalsIgnoreCase(str2)) {
            sb.append(str).append(lunarMonthString).append(lunarDayString).append(string).append(str2).append(lunarMonthString2).append(lunarDayString2);
        } else {
            sb.append(str);
            if (!lunarMonthString.equalsIgnoreCase(lunarMonthString2)) {
                sb.append(lunarMonthString).append(lunarDayString).append(string).append(lunarMonthString2).append(lunarDayString2);
            } else {
                sb.append(lunarMonthString);
                if (!lunarDayString.equalsIgnoreCase(lunarDayString2)) {
                    sb.append(lunarDayString).append(string).append(lunarDayString2);
                } else {
                    sb.append(lunarDayString);
                }
            }
        }
        return sb.toString();
    }

    public static boolean isUsingWeatherBrokerForChina(Context context) {
        return checkPackageAvailable(context, LunarContract.AUTHORITY);
    }

    public static boolean checkPackageAvailable(Context context, String str) {
        if (!isPackageListAccessEnable(context)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        boolean z = false;
        boolean z2 = false;
        try {
            packageManager.getApplicationInfo(str, 0);
            z2 = packageManager.getApplicationEnabledSetting(str) == 3 || packageManager.getApplicationEnabledSetting(str) == 2;
            z = true;
        } catch (PackageManager.NameNotFoundException unused2) {
            z = false;
        }
        return z && !z2;
    }

    public static void setAgendSelectedIsLunarEvent(boolean z) {
        sAgendaSelectedIsLunarEvent = z;
    }

    public static boolean getAgendSelectedIsLunarEvent() {
        return sAgendaSelectedIsLunarEvent;
    }

    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            view.clearFocus();
            ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public static boolean isTabletDevice(Context context) {
        return context.getResources().getBoolean(R.bool.tablet_mode);
    }

    public static boolean isInLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    public static boolean isLunar(Context context) {
        return LunarAvailabilityManager.isLunarAvailable(context);
    }

    public static boolean showWeekNumbers(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(GeneralPreferences.KEY_SHOW_WEEK_NUMBER, false);
    }

    public static boolean showAlarms(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(GeneralPreferences.KEY_SHOW_ALARMS, false);
    }

    public static boolean isAlarmsProviderAvailable(Context context) {
        try {
            context.getContentResolver().query(CONTENT_ALARMS_PROVIDER_URI, null, null, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getDayText(Context context, Time time, boolean z) {
        int i = z ? 22 : 20;
        long millis = time.toMillis(true);
        return DateUtils.formatDateRange(context, new Formatter(new StringBuilder(50), Locale.getDefault()), millis, millis, i, time.timezone).toString();
    }

    public static String getWeekText(Context context, Time time, boolean z) {
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar.setTimeInMillis(time.toMillis(false));
        if (z) {
            int weekNumberOfDay = getWeekNumberOfDay(context, calendar);
            String string = context.getString(R.string.week_view);
            if (isWeekNumberFirst(context.getResources())) {
                sb.append(weekNumberOfDay).append(" ").append(string);
            } else {
                sb.append(string);
                if (sb.length() > 0) {
                    sb.append(" ").append(weekNumberOfDay);
                }
            }
            return sb.toString();
        }
        return getWeekDaySpanLabel(context, time, 524296);
    }

    public static String getWeekText(Context context, Time time) {
        return getWeekText(context, time, showWeekNumbers(context));
    }

    public static String getMonthText(Context context, Time time) {
        return DateUtils.formatDateTime(context, time.toMillis(false), 36);
    }

    public static String getMonthTextWithouYear(Context context, Time time) {
        return DateUtils.formatDateTime(context, time.toMillis(false), 40);
    }

    public static String getYearText(Context context, Time time) {
        return new SimpleDateFormat(FORMAT_DATE_YEAR, Locale.getDefault()).format(new Date(time.toMillis(false))) + context.getString(R.string.year_label);
    }

    public static String getActionBarDateTitle(Context context, Time time, int i) {
        String dayText;
        if (i == 1) {
            dayText = getDayText(context, time, false);
        } else if (i == 2 || i == 3) {
            dayText = getMonthText(context, time);
        } else if (i != 4) {
            dayText = i != 5 ? "" : getMonthTextWithouYear(context, time);
        } else {
            dayText = getYearText(context, time);
        }
        return isLunar(context) ? dayText + getLunarText(time, i) : dayText;
    }

    public static String getHeaderText(Context context, Time time, int i) {
        String dayText;
        if (i == 1) {
            dayText = getDayText(context, time, true);
        } else {
            if (i == 2) {
                if (isLunar(context)) {
                    return getLunarWeekDaySpanLabel(context, time);
                }
                return getWeekDaySpanLabel(context, time, 524292);
            }
            if (i == 3) {
                dayText = getMonthText(context, time);
            } else {
                dayText = i != 4 ? "" : getYearText(context, time);
            }
        }
        return isLunar(context) ? dayText + getLunarText(time, i) : dayText;
    }

    public static String getLunarText(Time time, int i) {
        Date date = new Date(time.toMillis(false));
        String lunarYearInfo = LunarUtils.getLunarYearInfo(date);
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(date);
        if (lunarDateConvertSolarDateToLunarDate == null) {
            return " | ";
        }
        if (i == 1) {
            return " | " + lunarYearInfo + LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate) + LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate);
        }
        if (i == 2 || i == 3) {
            return " | " + lunarYearInfo + LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate);
        }
        return i != 4 ? " | " : " | " + lunarYearInfo;
    }

    public static long getHTZSelectionTimeFromIntent(Context context, Intent intent, String str) {
        Time time = new SafeTime();
        time.set(timeFromIntentInMillis(intent));
        Time time2 = new SafeTime();
        changeTimeZoneKeepSourceDateTime(time, time2, str);
        return time2.normalize(true);
    }

    public static void changeTimeZoneKeepSourceDateTime(Time time, Time time2, String str) {
        time2.set(time.second, time.minute, time.hour, time.monthDay, time.month, time.year);
        time2.timezone = str;
    }

    public static void correctSelectedTimeWithCurrent(Time time, Time time2) {
        time.set(time2.second, time2.minute, time2.hour, time.monthDay, time.month, time.year);
    }

    public static long correctSelectedDateTimeWithCurrent(Time time) {
        Time time2 = new SafeTime(time.timezone);
        time2.setToNow();
        boolean zAreDatesEqual = areDatesEqual(time2, time);
        int i = time.monthDay;
        correctSelectedTimeWithCurrent(time, time2);
        if (zAreDatesEqual) {
            time.set(time.toMillis(true) + 60000);
        }
        long nextRoundedTimeMillis = getNextRoundedTimeMillis(time, 30);
        time.set(nextRoundedTimeMillis);
        if (zAreDatesEqual || i == time.monthDay) {
            return nextRoundedTimeMillis;
        }
        time.monthDay--;
        time.normalize(true);
        return time.toMillis(true);
    }

    public static long getNextRoundedTimeMillis(Time time, int i) {
        Time time2 = new SafeTime(time);
        int i2 = time2.minute % i;
        if (i2 > 0) {
            time2.minute += i - i2;
        }
        return time2.normalize(true);
    }

    public static void updateAndRoundHour(Time time) {
        Time time2 = new SafeTime();
        time2.setToNow();
        time.hour = time2.hour;
        time.minute = time2.minute;
        time.set(getNextRoundedTimeMillis(time, 30));
    }

    public static String getDateString(Context context, Time time, String str) {
        Date date = new Date(time.toMillis(false));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZone(context, null)));
        return simpleDateFormat.format(date);
    }

    public static String getDateString(Context context, Time time, ViewType viewType) {
        String adjacentDayLabel = getAdjacentDayLabel(context, time);
        if (adjacentDayLabel != null) {
            return adjacentDayLabel;
        }
        if (Locale.getDefault().getLanguage().equals(Locale.JAPAN.getLanguage()) && ViewType.MONTH.equals(viewType)) {
            long millis = time.toMillis(true);
            return formatDateRange(context, millis, millis, FORMAT_JAPANESE_MONTH_DATE);
        }
        boolean zEquals = context.getResources().getConfiguration().locale.toString().equals("zh_CN_#Hans");
        StringBuilder sb = new StringBuilder();
        String str = FORMAT_DAY_DASH_MONTH_DATE;
        String string = sb.append(FORMAT_DAY_DASH_MONTH_DATE).append(context.getResources().getString(R.string.day_view)).toString();
        if (zEquals) {
            if (!ViewType.MONTH.equals(viewType)) {
                string = "cccc";
            }
            return getDateString(context, time, string);
        }
        if (!ViewType.MONTH.equals(viewType)) {
            str = "cccc";
        }
        return getDateString(context, time, str);
    }

    public static String getDateStringConsiderYear(Context context, Time time, Time time2) {
        String dateString = getDateString(context, time2, ViewType.MONTH);
        return time.year == time2.year ? dateString : dateString + " " + getYearText(context, time2);
    }

    public static String getDayTitleText(Context context, Time time) {
        return getDateString(context, time, ViewType.DAY);
    }

    public static String getDayDateString(Context context, Time time, String str) {
        Date date = new Date(time.toMillis(false));
        long millis = time.toMillis(true);
        if (Locale.getDefault().getLanguage().equals(Locale.JAPAN.getLanguage()) || Locale.getDefault().getLanguage().equals(Locale.TAIWAN.getLanguage())) {
            return formatDateRange(context, millis, millis, 24);
        }
        if (Locale.getDefault().getLanguage().equals(Locale.KOREA.getLanguage())) {
            return formatDateRange(context, millis, millis, 20);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZone(context, null)));
        String string = context.getString(R.string.day_label);
        if (LunarAvailabilityManager.isLunarAvailable(context)) {
            return simpleDateFormat.format(date) + string;
        }
        return simpleDateFormat.format(date);
    }

    public static long getAllDayInMillis(int i, int i2, int i3) {
        Time time = new SafeTime();
        time.set(i, i2 - 1, i3);
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        time.allDay = true;
        time.timezone = "UTC";
        return time.normalize(true);
    }

    public static String getCurrentLocationString(Context context) {
        return context.getString(R.string.preferences_weather_current_location_description);
    }

    public static void scheduleSync(int i, String str, String str2, boolean z, boolean z2) {
        if (str == null || str.length() == 0) {
            return;
        }
        Account account = new Account(str, str2);
        Bundle bundle = new Bundle();
        bundle.putBoolean("upload", z);
        bundle.putBoolean("force", z2);
        if ((i & 1) != 0) {
            ContentResolver.requestSync(account, CalendarContract.Calendars.CONTENT_URI.getAuthority(), bundle);
        }
        if ((i & 2) != 0) {
            ContentResolver.requestSync(account, TasksContract.TaskLists.CONTENT_URI.getAuthority(), bundle);
        }
    }

    public static boolean isWeekNumberFirst(Resources resources) {
        Locale locale = resources.getConfiguration().locale;
        return locale.equals(Locale.TRADITIONAL_CHINESE) || locale.equals(Locale.SIMPLIFIED_CHINESE) || locale.equals(Locale.TAIWAN) || locale.toString().equals("zh_HK") || locale.toString().equals("zh_CN_#Hans");
    }

    public static float getSystemScale(Context context) {
        try {
            return Settings.System.getFloat(context.getContentResolver(), "font_scale");
        } catch (Settings.SettingNotFoundException unused) {
            return 1.0f;
        }
    }

    public static String appendStringsWithDash(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(" ").append(UNICODE_FIGURE_DASH).append(" ").append(str2);
        return sb.toString();
    }

    public static String appendStringsWithSpaces(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(" ").append(" ").append(" ").append(str2);
        return sb.toString();
    }

    public static String getAdjacentDayLabel(Context context, Time time) {
        int offset = getOffset(time);
        if (offset == -1) {
            return context.getString(R.string.header_yesterday);
        }
        if (offset == 0) {
            return context.getString(R.string.header_today);
        }
        if (offset != 1) {
            return null;
        }
        return context.getString(R.string.header_tomorrow);
    }

    public static String getWeekWithWeekNumber(Context context, Time time) {
        String naturalLanguageWeekLabel = getNaturalLanguageWeekLabel(context, time);
        return naturalLanguageWeekLabel == null ? getWeekText(context, time, showWeekNumbers(context)) : naturalLanguageWeekLabel;
    }

    public static String getWeekForToolbarTitle(Context context, Time time) {
        String naturalLanguageWeekLabel = getNaturalLanguageWeekLabel(context, time);
        String weekText = getWeekText(context, time, false);
        if (naturalLanguageWeekLabel != null) {
            return appendStringsWithDash(naturalLanguageWeekLabel, weekText);
        }
        return showWeekNumbers(context) ? appendStringsWithSpaces(weekText, getWeekText(context, time, showWeekNumbers(context))) : weekText;
    }

    public static String getWeekDayWithNumber(Context context, Time time) {
        String adjacentDayLabel = getAdjacentDayLabel(context, time);
        Date date = new Date(time.toMillis(false));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATE_NORMAL, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZone(context, null)));
        String str = simpleDateFormat.format(date);
        return adjacentDayLabel != null ? appendStringsWithDash(adjacentDayLabel, str) : str;
    }

    public static String getWeekWithAppendedWeekNumber(Context context, Time time, int i) {
        String headerText = getHeaderText(context, time, 2);
        return showWeekNumbers(context) ? appendStringsWithSpaces(headerText, getWeekText(context, time, showWeekNumbers(context))) : headerText;
    }

    public static Time getCurrentTime(Context context) {
        Time time = new SafeTime();
        time.timezone = getTimeZone(context, null);
        time.setToNow();
        return time;
    }

    public static boolean isKeyboardAttached(Context context) {
        return context.getResources().getConfiguration().keyboard != 1;
    }

    public static boolean showWelcomeScreen(Context context) {
        return GeneralPreferences.getSharedPreferences(context).getBoolean(GeneralPreferences.KEY_SHOW_WELCOME_SCREEN, true);
    }

    public static void disableWelcomeScreen(Context context) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(context).edit();
        editorEdit.putBoolean(GeneralPreferences.KEY_SHOW_WELCOME_SCREEN, false);
        editorEdit.commit();
    }

    public static boolean shouldAddDefaultHolidays(Context context) {
        return GeneralPreferences.getSharedPreferences(context).getBoolean(GeneralPreferences.KEY_ADD_DEFAULT_HOLIDAYS, true);
    }

    public static void defaultHolidaysAdded(Context context) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(context).edit();
        editorEdit.putBoolean(GeneralPreferences.KEY_ADD_DEFAULT_HOLIDAYS, false);
        editorEdit.commit();
    }

    public static boolean isDayEvenInWeek(Context context, Time time) {
        return time != null && ((((time.weekDay + 7) - getFirstDayOfWeek(context)) % 7) & 1) == 1;
    }

    private static String constructReminderLabel(Context context, int i, boolean z) {
        int i2;
        Resources resources = context.getResources();
        String preparedReminderLabel = getPreparedReminderLabel(resources, i);
        if (preparedReminderLabel != null) {
            return preparedReminderLabel;
        }
        if (i % 60 != 0 || i == 0) {
            i2 = z ? R.plurals.Nmins : R.plurals.Nminutes;
        } else if (i % CalendarViewBase.MINUTES_PER_DAY != 0) {
            i /= 60;
            i2 = R.plurals.Nhours;
        } else {
            i /= CalendarViewBase.MINUTES_PER_DAY;
            i2 = R.plurals.Ndays;
        }
        return String.format(resources.getQuantityString(i2, i), Integer.valueOf(i));
    }

    public static String constructReminderLabelOrCustom(Context context, int i, boolean z) {
        Resources resources = context.getResources();
        String strConstructReminderLabel = constructReminderLabel(context, i, z);
        return Arrays.asList(resources.getStringArray(R.array.reminder_minutes_labels)).contains(strConstructReminderLabel) ? strConstructReminderLabel : strConstructReminderLabel + "(" + resources.getString(R.string.customType).toLowerCase() + ")";
    }

    private static String getPreparedReminderLabel(Resources resources, int i) {
        String[] stringArray = resources.getStringArray(R.array.reminder_minutes_values);
        String[] stringArray2 = resources.getStringArray(R.array.reminder_minutes_labels);
        String string = Integer.toString(i);
        for (int i2 = 0; i2 < stringArray.length; i2++) {
            if (stringArray[i2].equals(string)) {
                return stringArray2[i2];
            }
        }
        return null;
    }

    public static long getAvailiableMemory() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    public static boolean classExists(Context context, String str) {
        try {
            context.getClassLoader().loadClass(str);
            return true;
        } catch (Exception unused) {
            Log.d("SemcCalendar", "Class not found: " + str);
            return false;
        }
    }

    public static void setAlarm(AlarmManager alarmManager, int i, long j, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(i, j, pendingIntent);
        } else {
            alarmManager.set(i, j, pendingIntent);
        }
    }

    public static boolean isExchangeAccountType(String str) {
        if (str == null) {
            return false;
        }
        return str.toLowerCase().endsWith(EAS_ACCOUNT_TYPE_SUFFIX);
    }

    public static boolean isGoogleAccountType(String str) {
        if (str == null) {
            return false;
        }
        return str.toLowerCase().endsWith(GOOGLE_ACCOUNT_TYPE_SUFFIX);
    }

    public static boolean isBirthdayShowingEnabled(Context context) {
        return GeneralPreferences.getSharedPreferences(context).getBoolean(GeneralPreferences.KEY_BIRTHDAYS, true) && GeneralPreferences.getSharedPreferences(context).getBoolean(BirthdayPreferences.KEY_BIRTHDAY_CONTACTS, true);
    }

    public static ActionBar getSupportActionBar(FragmentActivity fragmentActivity) {
        if (fragmentActivity instanceof AppCompatActivity) {
            return ((AppCompatActivity) fragmentActivity).getSupportActionBar();
        }
        return null;
    }

    public static void setActionBarOptionsHomeAsUp(FragmentActivity fragmentActivity) {
        setActionBarOptionsHomeAsUp(fragmentActivity, 0);
    }

    public static void setActionBarOptionsHomeAsUp(FragmentActivity fragmentActivity, int i) {
        ActionBar supportActionBar = getSupportActionBar(fragmentActivity);
        if (supportActionBar != null) {
            supportActionBar.setDisplayOptions(4);
            if (i != 0) {
                supportActionBar.setTitle(i);
            }
        }
    }

    public static void setActionBarOptionsHomeAsUpTitleDisplayed(FragmentActivity fragmentActivity, int i) {
        ActionBar supportActionBar = getSupportActionBar(fragmentActivity);
        if (supportActionBar != null) {
            supportActionBar.setDisplayOptions(12);
            if (i != 0) {
                supportActionBar.setTitle(i);
            }
        }
    }

    public static AppCompatActivity getActivityFromWrapper(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static boolean isBuildVersionLollipopOrPlus() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static void clearBackStack(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        for (Fragment fragment2 : fragmentManager.getFragments()) {
            fragmentManager.popBackStackImmediate();
        }
    }

    public static void setToolbarBackNavigation(final FragmentActivity fragmentActivity, Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.Utils.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
                if (supportFragmentManager.getBackStackEntryCount() > 1) {
                    supportFragmentManager.popBackStackImmediate();
                } else {
                    fragmentActivity.onBackPressed();
                }
            }
        });
    }

    public static void restoreDatePickerDialog(Context context, DatePickerDialog datePickerDialog, DatePickerDialog.OnDateSetListener onDateSetListener) {
        if (UiUtils.isSub320dpScreen(context)) {
            datePickerDialog.dismiss();
        } else {
            datePickerDialog.setOnDateSetListener(onDateSetListener);
        }
    }

    public static void restoreDatePickerDialogFragment(Context context, DatePickerDialogFragment datePickerDialogFragment, DatePickerDialogListener datePickerDialogListener) {
        if (!UiUtils.isSub320dpScreen(context)) {
            datePickerDialogFragment.dismiss();
        } else {
            datePickerDialogFragment.setDateListener(datePickerDialogListener);
        }
    }
}
