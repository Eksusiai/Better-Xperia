package com.sonymobile.calendar.tablet;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;

/* JADX INFO: loaded from: classes2.dex */
public class TabletReminderPreferences extends GeneralPreferences {
    private static final String BUILD_VERSION = "build_version";

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected int getXmlResource() {
        return R.xml.tablet_reminder_preferences;
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void migrateOldPreferences(SharedPreferences sharedPreferences) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupAlarms(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupBirthdays(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupDaysInWeekSetting(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupDeleteAllEvent(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupHideDeclined(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupHomeTz(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupNationalHolidaysSetting(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupReadContacts(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupRetrieveLocationInfo(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupUseWiFiAndMobileData(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupWeather(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupWeekNumbers(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupWeekStart(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupWeekendSetting(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void updateNationalHolidaysSummary() {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void updateWeekendSummary() {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        TabletCalendarPreferenceActivity tabletCalendarPreferenceActivity = (TabletCalendarPreferenceActivity) getActivity();
        Preference preferenceFindPreference = findPreference(BUILD_VERSION);
        if (preferenceFindPreference != null) {
            try {
                preferenceFindPreference.setSummary(tabletCalendarPreferenceActivity.getPackageManager().getPackageInfo(tabletCalendarPreferenceActivity.getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException unused) {
                preferenceFindPreference.setSummary("?");
            }
        }
    }

    @Override // com.sonymobile.calendar.GeneralPreferences, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (Utils.KEY_QUICK_RESPONSES.equals(preference.getKey())) {
            ((TabletCalendarPreferenceActivity) getActivity()).presentPreferencePanel(null, preference.getFragment(), ((Object) preference.getTitle()) + "");
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.sonymobile.calendar.GeneralPreferences, androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        super.onPreferenceChange(preference, obj);
        return true;
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void setupAlerts(PreferenceScreen preferenceScreen) {
        super.setupAlerts(preferenceScreen);
    }
}
