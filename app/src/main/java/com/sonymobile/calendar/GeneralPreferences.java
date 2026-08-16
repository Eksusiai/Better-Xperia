package com.sonymobile.calendar;

import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.legacy.app.FragmentCompat;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.sonyericsson.calendar.util.CustomizeConfig;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonymobile.calendar.alerts.AlertReceiver;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.linkedin.backend.LinkedInCommunicationService;
import com.sonymobile.calendar.linkedin.model.LinkedInMySelf;
import com.sonymobile.calendar.linkedin.ui.LinkedInSignOutDialogFragment;
import com.sonymobile.calendar.utils.NotificationUtils;
import com.sonymobile.calendar.utils.ProductUtil;
import com.sonymobile.calendar.widget.CalendarAppWidgetProvider;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/* JADX INFO: loaded from: classes2.dex */
public class GeneralPreferences extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener, CustomSwitchPreferenceListener, NoLocationServicesDialogListener, LinkedInSignOutDialogFragment.LinkedInSignOutListener, FragmentCompat.OnRequestPermissionsResultCallback {
    private static final String ALERT_TYPE_ALERTS = "0";
    private static final String ALERT_TYPE_OFF = "2";
    private static final String ALERT_TYPE_STATUS_BAR = "1";
    public static final String EVENT_DESCRIPTION_SCALE_FACTOR = "event_description_scale_factor";
    public static final String KEY_ACCESS_PACKAGE_LIST = "preference_access_package_list";
    public static final String KEY_ADD_DEFAULT_HOLIDAYS = "key_add_default_holidays";
    public static final String KEY_ALERTS = "preferences_alerts";
    public static final String KEY_ALERTS_CATEGORY = "preferences_alerts_category";
    private static final String KEY_ALERTS_TYPE = "preferences_alerts_type";
    public static final String KEY_BIRTHDAYS = "preferences_birthday_display";
    public static final String KEY_CURRENT_LANGUAGE = "preferences_current_language";
    public static final String KEY_DAYS_IN_WEEK = "preferences_week_days";
    public static final String KEY_DEFAULT_CALENDAR = "preference_defaultCalendar";
    public static final String KEY_DEFAULT_REMINDER = "preferences_default_reminder";
    public static final String KEY_DEFAULT_SORT_TYPE = "preference_defaultSortType";
    public static final String KEY_DEFAULT_TASKS_ACCOUNT = "preference_defaultTasksAccount";
    private static final String KEY_DELETE_ALL_EVENT = "preferences_delete_all_event";
    private static final String KEY_GENERAL_PEREFERENCE_CATEGORY = "preferences_general_title";
    public static final String KEY_HIDE_DECLINED = "preferences_hide_declined";
    static final String KEY_HOME_TZ = "preferences_home_tz";
    static final String KEY_HOME_TZ_ENABLED = "preferences_home_tz_enabled";
    private static final String KEY_LINKEDIN = "preferences_linkedin";
    public static final String KEY_NATIONAL_HOLIDAYS = "preferences_national_holidays";
    public static final String KEY_NOTIFICATION_CHANNEL_SETTINGS = "preferences_notification_channel_settings";
    protected static final String KEY_QUICK_RESPONSE = "preferences_quick_responses";
    public static final String KEY_READ_CONTACTS = "preferences_read_contacts";
    public static final String KEY_RETRIEVE_LOCATION_INFO = "preferences_retrieve_location_info";
    public static final String KEY_SHOW_ALARMS = "preferences_show_alarms";
    public static final String KEY_SHOW_WEEK_NUMBER = "preferences_show_week_number";
    public static final String KEY_THEME = "preferences_theme";
    public static final String THEME_VALUE_LIGHT = "light";
    public static final String THEME_VALUE_DARK = "dark";
    public static final String THEME_VALUE_SYSTEM = "system";
    public static final String KEY_SHOW_WELCOME_SCREEN = "preferences_show_welcome_screen";
    public static final String KEY_START_RESOLUTION = "preferred_startResolution";
    public static final String KEY_USE_WIFI_AND_MOBILE_DATA = "preferences_use_wifi_and_mobile_data";
    public static final String KEY_WEATHER = "preferences_weather_display";
    public static final String KEY_WEATHER_HIDE_DIALOG = "key_weather_hide_dialog";
    public static final String KEY_WEEKEND_DAYS = "preferences_weekend";
    public static final String KEY_WEEK_START_DAY = "preferences_week_start_day";
    public static final String KEY_WIDGET_TRANSPARENCY = "preferences_widget_transparency";
    private static final String MEDIA_INTERNAL = "media/internal";
    private static final int REQUEST_OPEN_SETTING_LOCATION = 1;
    static final String SHARED_PREFS_NAME = "com.sonymobile.calendar_preferences";
    public static final String WEEK_START_DEFAULT = "-1";
    private static CharSequence[][] mTimezones;
    private String[] dayLabels;
    private int dayRotation;
    SwitchPreference mAlert;
    CustomSwitchPreference mBirthdays;
    ListPreference mDaysInWeek;
    ListPreference mDefaultReminder;
    DeleteAllEventPreference mDeleteAllEventPreference;
    SwitchPreference mHideDeclined;
    ListPreference mHomeTZ;
    Preference mLinkedInPreference;
    MultiSelectListPreference mNationalHolidays;
    Preference mNotificationChannel;
    PreferenceScreen mQuickResponsePreference;
    SwitchPreference mReadContacts;
    SwitchPreference mRetrieveLocationInfo;
    protected Preference mRingtone;
    SwitchPreference mShowAlarms;
    SwitchPreference mShowWeekNumber;
    SwitchPreference mUseHomeTZ;
    SwitchPreference mUseWiFiAndMobileData;
    ListPreference mVibrateWhen;
    CustomSwitchPreference mWeather;
    ListPreference mWeekStart;
    ListPreference mTheme;
    MultiSelectListPreference mWeekend;
    private boolean mIsExternalStorage = false;
    private BroadcastReceiver linkedInOwnEmailBroadcastReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.GeneralPreferences.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(LinkedInCommunicationService.REQUEST_SUCCESS_PARAMETER, false) && LinkedInUtils.isLinkedInSyncValid(GeneralPreferences.this.getActivity())) {
                GeneralPreferences.this.mLinkedInPreference.setSummary(String.format(GeneralPreferences.this.getString(R.string.preferences_linkedin_summary_deactivate), intent.getStringExtra(LinkedInCommunicationService.LINKEDIN_OWN_EMAIL_PARAMETER)));
            }
        }
    };

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_NAME, 0);
    }

    public static void setDefaultValues(Context context) {
        if (!ProductUtil.isNoDeleteAllEvents()) {
            PreferenceManager.setDefaultValues(context, SHARED_PREFS_NAME, 0, R.xml.general_preferences, false);
        } else {
            PreferenceManager.setDefaultValues(context, SHARED_PREFS_NAME, 0, R.xml.general_preferences_nodeleteallevents, false);
        }
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if (sharedPreferences.getString(WeatherPreferences.KEY_WEATHER_UNIT, null) == null) {
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            editorEdit.putString(WeatherPreferences.KEY_WEATHER_UNIT, context.getString(R.string.preferences_default_weather_unit));
            editorEdit.apply();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceManager preferenceManager = getPreferenceManager();
        SharedPreferences sharedPreferences = getSharedPreferences(getActivity());
        preferenceManager.setSharedPreferencesName(SHARED_PREFS_NAME);
        addPreferencesFromResource(getXmlResource());
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        setupNationalHolidaysSetting(preferenceScreen);
        setupDaysInWeekSetting(preferenceScreen);
        setupWeekendSetting(preferenceScreen);
        setupAlerts(preferenceScreen);
        setupDefaultReminder(preferenceScreen);
        setupQuickResponse(preferenceScreen);
        setupHomeTz(preferenceScreen);
        setupHideDeclined(preferenceScreen);
        setupWeekStart(preferenceScreen);
        setupTheme(preferenceScreen);
        setupDeleteAllEvent(preferenceScreen);
        setupWeather(preferenceScreen);
        setupBirthdays(preferenceScreen);
        setupWeekNumbers(preferenceScreen);
        setupAlarms(preferenceScreen);
        migrateOldPreferences(sharedPreferences);
        setupReadContacts(preferenceScreen);
        setupUseWiFiAndMobileData(preferenceScreen);
        setupRetrieveLocationInfo(preferenceScreen);
    }

    protected void setUpActionBar() {
        ActionBar supportActionBar = Utils.getSupportActionBar(getActivity());
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setCustomView(R.layout.actionbar_title);
        ((TextView) supportActionBar.getCustomView().findViewById(R.id.actionbar_title_text)).setText(R.string.preferences_title);
        supportActionBar.setDisplayShowCustomEnabled(true);
    }

    protected void setupWeather(PreferenceScreen preferenceScreen) {
        this.mWeather = (CustomSwitchPreference) preferenceScreen.findPreference(KEY_WEATHER);
        if (CalendarApplication.getWeatherFactory().isWeatherBrokerAvailable()) {
            this.mWeather.setOnPreferenceChangeListener(this);
            this.mWeather.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.sonymobile.calendar.GeneralPreferences.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    GeneralPreferences.this.goToPreference(preference);
                    return true;
                }
            });
            return;
        }
        this.mWeather.setEnabled(false);
        SharedPreferences sharedPreferences = getSharedPreferences(getActivity());
        if (sharedPreferences.getBoolean(KEY_WEATHER, true)) {
            sharedPreferences.edit().putBoolean(KEY_WEATHER, false).commit();
        }
    }

    protected void setupBirthdays(PreferenceScreen preferenceScreen) {
        CustomSwitchPreference customSwitchPreference = (CustomSwitchPreference) preferenceScreen.findPreference(KEY_BIRTHDAYS);
        this.mBirthdays = customSwitchPreference;
        customSwitchPreference.setOnPreferenceChangeListener(this);
        this.mBirthdays.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.sonymobile.calendar.GeneralPreferences.2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                GeneralPreferences.this.goToPreference(preference);
                return true;
            }
        });
    }

    protected void setupWeekNumbers(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(KEY_SHOW_WEEK_NUMBER);
        this.mShowWeekNumber = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    protected void setupAlarms(PreferenceScreen preferenceScreen) {
        if (Utils.isAlarmsProviderAvailable(preferenceScreen.getContext())) {
            SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(KEY_SHOW_ALARMS);
            this.mShowAlarms = switchPreference;
            switchPreference.setOnPreferenceChangeListener(this);
            return;
        }
        removeAlarmsPreference();
    }

    protected void setupUseWiFiAndMobileData(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(KEY_USE_WIFI_AND_MOBILE_DATA);
        this.mUseWiFiAndMobileData = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    protected void setupRetrieveLocationInfo(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(KEY_RETRIEVE_LOCATION_INFO);
        this.mRetrieveLocationInfo = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    protected void setupReadContacts(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(KEY_READ_CONTACTS);
        this.mReadContacts = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    protected void setupDeleteAllEvent(PreferenceScreen preferenceScreen) {
        this.mDeleteAllEventPreference = (DeleteAllEventPreference) preferenceScreen.findPreference(KEY_DELETE_ALL_EVENT);
    }

    protected void setupWeekStart(PreferenceScreen preferenceScreen) {
        ListPreference listPreference = (ListPreference) preferenceScreen.findPreference(KEY_WEEK_START_DAY);
        this.mWeekStart = listPreference;
        if (listPreference.getValue().equals(WEEK_START_DEFAULT)) {
            this.mWeekStart.setValue(String.valueOf(Calendar.getInstance().getFirstDayOfWeek()));
        }
        ListPreference listPreference2 = this.mWeekStart;
        listPreference2.setSummary(listPreference2.getEntry());
        this.mWeekStart.setOnPreferenceChangeListener(this);
    }

    protected void setupTheme(PreferenceScreen preferenceScreen) {
        ListPreference listPreference = (ListPreference) preferenceScreen.findPreference(KEY_THEME);
        this.mTheme = listPreference;
        if (listPreference != null) {
            listPreference.setSummary(listPreference.getEntry());
            listPreference.setOnPreferenceChangeListener(this);
        }
    }

    protected void setupHideDeclined(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(KEY_HIDE_DECLINED);
        this.mHideDeclined = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    protected void setupHomeTz(PreferenceScreen preferenceScreen) {
        CharSequence[][] allTimezones;
        this.mUseHomeTZ = (SwitchPreference) preferenceScreen.findPreference("preferences_home_tz_enabled");
        ListPreference listPreference = (ListPreference) preferenceScreen.findPreference("preferences_home_tz");
        this.mHomeTZ = listPreference;
        String value = listPreference.getValue();
        synchronized (GeneralPreferences.class) {
            allTimezones = new TimezoneAdapter(getActivity(), value, System.currentTimeMillis()).getAllTimezones();
            mTimezones = allTimezones;
        }
        this.mHomeTZ.setEntryValues(allTimezones[0]);
        this.mHomeTZ.setEntries(mTimezones[1]);
        CharSequence entry = this.mHomeTZ.getEntry();
        if (TextUtils.isEmpty(entry)) {
            this.mHomeTZ.setValue(Utils.getTimeZone(getActivity(), null).toString());
            ListPreference listPreference2 = this.mHomeTZ;
            listPreference2.setSummary(listPreference2.getEntry());
        } else {
            this.mHomeTZ.setSummary(entry);
        }
        this.mUseHomeTZ.setOnPreferenceChangeListener(this);
        this.mHomeTZ.setOnPreferenceChangeListener(this);
    }

    protected void setupQuickResponse(PreferenceScreen preferenceScreen) {
        this.mQuickResponsePreference = (PreferenceScreen) preferenceScreen.findPreference("preferences_quick_responses");
    }

    protected void setupDefaultReminder(PreferenceScreen preferenceScreen) {
        ListPreference listPreference = (ListPreference) preferenceScreen.findPreference(KEY_DEFAULT_REMINDER);
        this.mDefaultReminder = listPreference;
        listPreference.setSummary(listPreference.getEntry());
        this.mDefaultReminder.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        DeleteAllEventPreference deleteAllEventPreference = this.mDeleteAllEventPreference;
        if (deleteAllEventPreference != null) {
            deleteAllEventPreference.dismissAlertDialog();
        }
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        getActivity().registerReceiver(this.linkedInOwnEmailBroadcastReceiver, new IntentFilter(LinkedInCommunicationService.MESSAGE_OWN_EMAIL), 4);
        if (sharedPreferences != null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
        setUpActionBar();
        SharedPreferences sharedPreferences2 = getSharedPreferences(getActivity());
        if (sharedPreferences2.getBoolean(KEY_WEATHER, true) && !Utils.isAnyLocationProviderEnabled(getActivity()) && Utils.isCurrentLocationSet(getActivity())) {
            sharedPreferences2.edit().putBoolean(KEY_WEATHER, false).apply();
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof CalendarSettingsActivity) {
            ((CalendarSettingsActivity) activity).updateToolbar(false);
        }
        if (LinkedInUtils.isLinkedInEnabled(getActivity())) {
            setupLinkedInSetting(getPreferenceScreen());
        } else {
            removeLinkedInPreference();
        }
        if (!CustomizeConfig.isShowingPermissionAcceptanceDialogEnabled(getActivity())) {
            removePermissionPreferences();
        }
        if (this.mAlert != null) {
            setupNotificationChannel(getPreferenceScreen());
        }
    }

    private void removeLinkedInPreference() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference preferenceFindPreference = preferenceScreen.findPreference(KEY_LINKEDIN);
        this.mLinkedInPreference = preferenceFindPreference;
        if (preferenceFindPreference != null) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_GENERAL_PEREFERENCE_CATEGORY);
            if (preferenceCategory != null) {
                preferenceCategory.removePreference(this.mLinkedInPreference);
            } else {
                preferenceScreen.removePreference(this.mLinkedInPreference);
            }
        }
    }

    private void removeAlarmsPreference() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(KEY_SHOW_ALARMS);
        this.mShowAlarms = switchPreference;
        if (switchPreference != null) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_GENERAL_PEREFERENCE_CATEGORY);
            if (preferenceCategory != null) {
                preferenceCategory.removePreference(this.mShowAlarms);
            } else {
                preferenceScreen.removePreference(this.mShowAlarms);
            }
        }
    }

    private void removePermissionPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        boolean zIsTabletDevice = Utils.isTabletDevice(getActivity());
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_GENERAL_PEREFERENCE_CATEGORY);
        SwitchPreference switchPreference = this.mReadContacts;
        if (switchPreference != null) {
            if (zIsTabletDevice) {
                preferenceScreen.removePreference(switchPreference);
            } else {
                preferenceCategory.removePreference(switchPreference);
            }
        }
        SwitchPreference switchPreference2 = this.mUseWiFiAndMobileData;
        if (switchPreference2 != null) {
            if (zIsTabletDevice) {
                preferenceScreen.removePreference(switchPreference2);
            } else {
                preferenceCategory.removePreference(switchPreference2);
            }
        }
        SwitchPreference switchPreference3 = this.mRetrieveLocationInfo;
        if (switchPreference3 != null) {
            if (zIsTabletDevice) {
                preferenceScreen.removePreference(switchPreference3);
            } else {
                preferenceCategory.removePreference(switchPreference3);
            }
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        DeleteAllEventPreference deleteAllEventPreference = this.mDeleteAllEventPreference;
        if (deleteAllEventPreference != null) {
            bundle.putBoolean("dialogShown", deleteAllEventPreference.isDialogShowing());
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle == null || !bundle.getBoolean("dialogShown", false)) {
            return;
        }
        this.mDeleteAllEventPreference.onClick();
    }

    protected int getXmlResource() {
        return !ProductUtil.isNoDeleteAllEvents() ? R.xml.general_preferences : R.xml.general_preferences_nodeleteallevents;
    }

    protected void setupAlerts(PreferenceScreen preferenceScreen) {
        this.mAlert = (SwitchPreference) preferenceScreen.findPreference(KEY_ALERTS);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
        getActivity().unregisterReceiver(this.linkedInOwnEmailBroadcastReceiver);
    }

    protected void setupNotificationChannel(PreferenceScreen preferenceScreen) {
        if (this.mNotificationChannel == null) {
            this.mNotificationChannel = preferenceScreen.findPreference(KEY_NOTIFICATION_CHANNEL_SETTINGS);
        }
        this.mNotificationChannel.setEnabled(true);
        if (this.mAlert.isChecked()) {
            return;
        }
        removeNotificationPref();
    }

    protected void removeNotificationPref() {
        Preference preferenceFindPreference = getPreferenceScreen().findPreference(KEY_NOTIFICATION_CHANNEL_SETTINGS);
        this.mNotificationChannel = preferenceFindPreference;
        if (preferenceFindPreference != null) {
            preferenceFindPreference.setEnabled(false);
        }
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        SwitchPreference switchPreference;
        CustomSwitchPreference customSwitchPreference;
        FragmentActivity activity = getActivity();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (str.equals(KEY_ALERTS) && activity != null) {
            Intent intent = new Intent();
            intent.setClass(activity, AlertReceiver.class);
            if (this.mAlert.isChecked()) {
                intent.setAction(AlertReceiver.ACTION_DISMISS_OLD_REMINDERS);
                setupNotificationChannel(preferenceScreen);
            } else {
                intent.setAction(LunarContract.ACTION_EVENT_REMINDER);
            }
            activity.sendBroadcast(intent);
        }
        if (str.equals(KEY_READ_CONTACTS) && (switchPreference = this.mReadContacts) != null && (customSwitchPreference = this.mBirthdays) != null) {
            customSwitchPreference.setEnabled(switchPreference.isChecked());
        }
        if (activity != null) {
            BackupManager.dataChanged(activity.getPackageName());
            if (str.equals(KEY_WIDGET_TRANSPARENCY)) {
                Intent intent2 = new Intent(activity, (Class<?>) CalendarAppWidgetProvider.class);
                intent2.setAction(CalendarAppWidgetProvider.APPWIDGET_TRANSPARENCY_VALUE_CHANGED);
                getActivity().sendBroadcast(intent2);
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mUseHomeTZ) {
            Utils.setTimeZone(getActivity(), ((Boolean) obj).booleanValue() ? this.mHomeTZ.getValue() : LunarContract.CalendarCache.TIMEZONE_TYPE_AUTO);
            return true;
        }
        SwitchPreference switchPreference = this.mHideDeclined;
        if (preference == switchPreference) {
            switchPreference.setChecked(((Boolean) obj).booleanValue());
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            Intent intent = new Intent(Utils.getWidgetScheduledUpdateAction(appCompatActivity));
            intent.setClassName(appCompatActivity, appCompatActivity.getPackageName() + ".widget.CalendarAppWidgetService$CalendarFactory");
            intent.setDataAndType(CalendarContract.CONTENT_URI, Utils.APPWIDGET_DATA_TYPE);
            appCompatActivity.sendBroadcast(intent);
            return true;
        }
        SwitchPreference switchPreference2 = this.mShowWeekNumber;
        if (preference == switchPreference2) {
            switchPreference2.setChecked(((Boolean) obj).booleanValue());
            return true;
        }
        SwitchPreference switchPreference3 = this.mShowAlarms;
        if (preference == switchPreference3) {
            switchPreference3.setChecked(((Boolean) obj).booleanValue());
            return true;
        }
        ListPreference listPreference = this.mHomeTZ;
        if (preference == listPreference) {
            String str = (String) obj;
            listPreference.setValue(str);
            ListPreference listPreference2 = this.mHomeTZ;
            listPreference2.setSummary(listPreference2.getEntry());
            Utils.setTimeZone(getActivity(), str);
            return false;
        }
        ListPreference listPreference3 = this.mWeekStart;
        if (preference == listPreference3) {
            listPreference3.setValue((String) obj);
            ListPreference listPreference4 = this.mWeekStart;
            listPreference4.setSummary(listPreference4.getEntry());
            return false;
        }
        ListPreference listPreferenceTheme = this.mTheme;
        if (preference == listPreferenceTheme) {
            listPreferenceTheme.setValue((String) obj);
            listPreferenceTheme.setSummary(listPreferenceTheme.getEntry());
            CalendarApplication.applyThemeMode();
            if (getActivity() != null) {
                getActivity().recreate();
            }
            return false;
        }
        ListPreference listPreference5 = this.mDefaultReminder;
        if (preference == listPreference5) {
            listPreference5.setValue((String) obj);
            ListPreference listPreference6 = this.mDefaultReminder;
            listPreference6.setSummary(listPreference6.getEntry());
            return false;
        }
        if (preference == this.mRingtone && this.mIsExternalStorage) {
            return true;
        }
        MultiSelectListPreference multiSelectListPreference = this.mNationalHolidays;
        if (preference == multiSelectListPreference) {
            multiSelectListPreference.setValues((HashSet) obj);
            updateNationalHolidaysSummary();
            return false;
        }
        MultiSelectListPreference multiSelectListPreference2 = this.mWeekend;
        if (preference == multiSelectListPreference2) {
            multiSelectListPreference2.setValues((Set) obj);
            updateWeekendSummary();
            updateDaysInWeek(this.mWeekend.getValues().size());
            return true;
        }
        ListPreference listPreference7 = this.mDaysInWeek;
        if (preference == listPreference7) {
            listPreference7.setValue(String.valueOf(obj));
            updateDaysInWeekSummary();
            return true;
        }
        ListPreference listPreference8 = this.mVibrateWhen;
        if (preference == listPreference8) {
            listPreference8.setValue((String) obj);
            ListPreference listPreference9 = this.mVibrateWhen;
            listPreference9.setSummary(listPreference9.getEntry());
            return false;
        }
        SwitchPreference switchPreference4 = this.mReadContacts;
        if (preference == switchPreference4) {
            switchPreference4.setChecked(((Boolean) obj).booleanValue());
            return true;
        }
        SwitchPreference switchPreference5 = this.mUseWiFiAndMobileData;
        if (preference == switchPreference5) {
            switchPreference5.setChecked(((Boolean) obj).booleanValue());
            return true;
        }
        SwitchPreference switchPreference6 = this.mRetrieveLocationInfo;
        if (preference == switchPreference6) {
            switchPreference6.setChecked(((Boolean) obj).booleanValue());
        }
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!getResources().getBoolean(R.bool.tablet_mode) && preference == this.mQuickResponsePreference) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
            Fragment fragmentFindFragmentById = fragmentManager.findFragmentById(R.id.pref_content_frame);
            if (fragmentFindFragmentById != null) {
                fragmentTransactionBeginTransaction.remove(fragmentFindFragmentById);
            }
            fragmentTransactionBeginTransaction.add(R.id.pref_content_frame, new QuickResponseSettings()).addToBackStack(null).commit();
            return true;
        }
        if (!getResources().getBoolean(R.bool.tablet_mode) && preference == this.mNotificationChannel) {
            new NotificationUtils(getActivity());
            startActivity(NotificationUtils.getHighNotificationSettingsIntent(getActivity()));
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    protected void setupNationalHolidaysSetting(PreferenceScreen preferenceScreen) {
        if (getResources().getBoolean(R.bool.support_country_holiday)) {
            MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preferenceScreen.findPreference(KEY_NATIONAL_HOLIDAYS);
            this.mNationalHolidays = multiSelectListPreference;
            preferenceScreen.removePreference(multiSelectListPreference);
            this.mNationalHolidays.setOnPreferenceChangeListener(this);
            updateNationalHolidaysSummary();
            return;
        }
        removeNationalHolidaysSetting();
    }

    private void removeNationalHolidaysSetting() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preferenceScreen.findPreference(KEY_NATIONAL_HOLIDAYS);
        this.mNationalHolidays = multiSelectListPreference;
        if (multiSelectListPreference != null) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_GENERAL_PEREFERENCE_CATEGORY);
            if (preferenceCategory != null) {
                preferenceCategory.removePreference(this.mNationalHolidays);
            } else {
                preferenceScreen.removePreference(this.mNationalHolidays);
            }
        }
    }

    protected void updateNationalHolidaysSummary() {
        HashSet<String> hashSet = new HashSet(this.mNationalHolidays.getValues());
        CharSequence[] entries = this.mNationalHolidays.getEntries();
        StringBuilder sb = new StringBuilder();
        for (String str : hashSet) {
            if (this.mNationalHolidays.findIndexOfValue(str) != -1) {
                sb.append(entries[this.mNationalHolidays.findIndexOfValue(str)]);
                sb.append(", ");
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        if (!TextUtils.isEmpty(sb)) {
            FreeDayService.getInstance().requestLoad(getActivity(), null, 0, true);
        }
        this.mNationalHolidays.setSummary(sb);
    }

    protected void setupWeekendSetting(PreferenceScreen preferenceScreen) {
        this.mWeekend = (MultiSelectListPreference) preferenceScreen.findPreference(KEY_WEEKEND_DAYS);
        this.dayLabels = getResources().getStringArray(R.array.preferences_weekend_days_labels);
        String[] strArr = {String.valueOf(1), String.valueOf(2), String.valueOf(3), String.valueOf(4), String.valueOf(5), String.valueOf(6), String.valueOf(0)};
        this.dayRotation = 0;
        int firstDayOfWeek = Utils.getFirstDayOfWeek(preferenceScreen.getContext());
        if (firstDayOfWeek == 0) {
            this.dayRotation = 6;
            this.dayLabels = (String[]) rotateArray(this.dayLabels, 6);
            strArr = (String[]) rotateArray(strArr, 6);
        } else if (firstDayOfWeek == 6) {
            this.dayRotation = 5;
            this.dayLabels = (String[]) rotateArray(this.dayLabels, 5);
            strArr = (String[]) rotateArray(strArr, 5);
        }
        this.mWeekend.setEntries(this.dayLabels);
        this.mWeekend.setEntryValues(strArr);
        this.mWeekend.setOnPreferenceChangeListener(this);
        updateWeekendSummary();
        updateDaysInWeek(this.mWeekend.getValues().size());
    }

    protected void updateWeekendSummary() {
        TreeSet treeSet = new TreeSet(this.mWeekend.getValues());
        TreeSet treeSet2 = new TreeSet();
        Iterator it = treeSet.iterator();
        while (it.hasNext()) {
            treeSet2.add(String.valueOf((Integer.parseInt((String) it.next()) + (6 - this.dayRotation)) % 7));
        }
        StringBuilder sb = new StringBuilder();
        Iterator it2 = treeSet2.iterator();
        while (it2.hasNext()) {
            sb.append(this.dayLabels[Integer.parseInt((String) it2.next())]);
            sb.append(", ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        this.mWeekend.setSummary(sb);
    }

    protected void setupDaysInWeekSetting(PreferenceScreen preferenceScreen) {
        ListPreference listPreference = (ListPreference) preferenceScreen.findPreference(KEY_DAYS_IN_WEEK);
        this.mDaysInWeek = listPreference;
        this.mDaysInWeek.setSummary(listPreference.getEntry());
        this.mDaysInWeek.setOnPreferenceChangeListener(this);
    }

    protected void setupLinkedInSetting(PreferenceScreen preferenceScreen) {
        this.mLinkedInPreference = preferenceScreen.findPreference(KEY_LINKEDIN);
        final boolean zIsLinkedInSyncValid = LinkedInUtils.isLinkedInSyncValid(getActivity());
        Preference preference = this.mLinkedInPreference;
        if (preference != null) {
            if (zIsLinkedInSyncValid) {
                this.mLinkedInPreference.setSummary(String.format(getString(R.string.preferences_linkedin_summary_deactivate), LinkedInMySelf.getInstance().getEmail()));
            } else {
                preference.setSummary(getString(R.string.preferences_linkedin_summary));
            }
            this.mLinkedInPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.sonymobile.calendar.GeneralPreferences.3
                private static final long CLICK_DELAY = 500;
                private long mLastClick;

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference2) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (Math.abs(this.mLastClick - jCurrentTimeMillis) < CLICK_DELAY) {
                        return false;
                    }
                    this.mLastClick = jCurrentTimeMillis;
                    if (zIsLinkedInSyncValid) {
                        LinkedInSignOutDialogFragment linkedInSignOutDialogFragmentNewInstance = LinkedInSignOutDialogFragment.newInstance();
                        linkedInSignOutDialogFragmentNewInstance.setLinkedInSignOutListener(GeneralPreferences.this);
                        linkedInSignOutDialogFragmentNewInstance.show(GeneralPreferences.this.getChildFragmentManager(), LinkedInSignOutDialogFragment.TAG);
                        return true;
                    }
                    LinkedInUtils.startSyncWithLinkedInActivity(GeneralPreferences.this.getActivity(), LinkedInUtils.LinkedInActivationEntrypoint.SETTINGS);
                    return true;
                }
            });
        }
    }

    @Override // com.sonymobile.calendar.linkedin.ui.LinkedInSignOutDialogFragment.LinkedInSignOutListener
    public void onSignOut() {
        setupLinkedInSetting(getPreferenceScreen());
    }

    protected void migrateOldPreferences(SharedPreferences sharedPreferences) {
        if (sharedPreferences.contains(KEY_ALERTS) || !sharedPreferences.contains(KEY_ALERTS_TYPE)) {
            return;
        }
        String string = sharedPreferences.getString(KEY_ALERTS_TYPE, ALERT_TYPE_STATUS_BAR);
        if (string.equals(ALERT_TYPE_OFF)) {
            this.mAlert.setChecked(false);
        } else if (string.equals(ALERT_TYPE_STATUS_BAR) || string.equals(ALERT_TYPE_ALERTS)) {
            this.mAlert.setChecked(true);
        }
        sharedPreferences.edit().remove(KEY_ALERTS_TYPE).commit();
    }

    private <T> T[] rotateArray(T[] tArr, int i) {
        int length = tArr.length;
        T[] tArr2 = (T[]) Arrays.copyOf(tArr, length);
        for (int i2 = 0; i2 < length; i2++) {
            tArr2[i2] = tArr[(i2 + i) % length];
        }
        return tArr2;
    }

    private void updateDaysInWeek(int i) {
        if (i == 7) {
            this.mDaysInWeek.setValue(getString(R.string.preferences_week_days_default));
            updateDaysInWeekSummary();
            this.mDaysInWeek.setEnabled(false);
            return;
        }
        this.mDaysInWeek.setEnabled(true);
    }

    private void updateDaysInWeekSummary() {
        String strValueOf = String.valueOf(this.mDaysInWeek.getEntry());
        if (strValueOf == null || strValueOf.trim().length() <= 0) {
            return;
        }
        this.mDaysInWeek.setSummary(strValueOf);
    }

    @Override // com.sonymobile.calendar.CustomSwitchPreferenceListener
    public void onPreferenceRowClicked(String str) {
        Fragment fragmentInstantiate = Fragment.instantiate(getActivity(), getPreferenceManager().findPreference(str).getFragment());
        FragmentTransaction fragmentTransactionBeginTransaction = getFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.pref_content_frame, fragmentInstantiate);
        fragmentTransactionBeginTransaction.addToBackStack(null);
        fragmentTransactionBeginTransaction.commit();
    }

    @Override // com.sonymobile.calendar.NoLocationServicesDialogListener
    public void noLocationDialogConfirmed() {
        Utils.getSupportActionBar(getActivity()).setTitle("");
        WeatherPreferences weatherPreferences = new WeatherPreferences();
        WeatherLocationFragment weatherLocationFragment = new WeatherLocationFragment();
        weatherLocationFragment.setTargetFragment(weatherPreferences, 1234);
        FragmentTransaction fragmentTransactionBeginTransaction = getFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.pref_content_frame, weatherPreferences);
        fragmentTransactionBeginTransaction.addToBackStack(null);
        fragmentTransactionBeginTransaction.commit();
        FragmentTransaction fragmentTransactionBeginTransaction2 = getFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction2.replace(R.id.pref_content_frame, weatherLocationFragment);
        fragmentTransactionBeginTransaction2.addToBackStack(null);
        fragmentTransactionBeginTransaction2.commit();
    }

    @Override // com.sonymobile.calendar.NoLocationServicesDialogListener
    public void noLocationDialogCancelled() {
        SharedPreferences.Editor editorEdit = getSharedPreferences(getActivity()).edit();
        editorEdit.putBoolean(KEY_WEATHER, false);
        editorEdit.apply();
    }

    @Override // com.sonymobile.calendar.NoLocationServicesDialogListener
    public void launchSettingLocationActivity() {
        startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 1);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (1 == i && !Utils.isAnyLocationProviderEnabled(getActivity())) {
            SharedPreferences.Editor editorEdit = getSharedPreferences(getActivity()).edit();
            editorEdit.putBoolean(KEY_WEATHER, false);
            editorEdit.apply();
        }
        LinkedInUtils.handleSyncWithLinkedInResult(getActivity(), i, i2, intent);
        super.onActivityResult(i, i2, intent);
    }

    protected void goToPreference(Preference preference) {
        Fragment fragmentInstantiate = Fragment.instantiate(getActivity(), preference.getFragment());
        FragmentTransaction fragmentTransactionBeginTransaction = getFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.pref_content_frame, fragmentInstantiate);
        fragmentTransactionBeginTransaction.addToBackStack(null);
        fragmentTransactionBeginTransaction.commit();
    }

    public static void saveEventDescriptionScaleFactor(Context context, float f) {
        SharedPreferences.Editor editorEdit = getSharedPreferences(context).edit();
        editorEdit.putFloat(EVENT_DESCRIPTION_SCALE_FACTOR, f);
        editorEdit.apply();
    }

    public static float getEventDescriptionScaleFactor(Context context, float f) {
        return getSharedPreferences(context).getFloat(EVENT_DESCRIPTION_SCALE_FACTOR, f);
    }

    @Override // androidx.fragment.app.Fragment
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener
    public void onDisplayPreferenceDialog(Preference preference) {
        boolean z = preference instanceof CustomMultiSelectListPreference;
        super.onDisplayPreferenceDialog(preference);
    }
}
