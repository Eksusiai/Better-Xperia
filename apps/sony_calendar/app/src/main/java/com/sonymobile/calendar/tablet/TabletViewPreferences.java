package com.sonymobile.calendar.tablet;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.sonymobile.calendar.CustomSwitchPreference;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class TabletViewPreferences extends GeneralPreferences {
    private static final String BUILD_VERSION = "build_version";

    @Override // com.sonymobile.calendar.GeneralPreferences
    public int getXmlResource() {
        return R.xml.tablet_calendarview_preferences;
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    public void migrateOldPreferences(SharedPreferences sharedPreferences) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    public void setupAlerts(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    public void setupDefaultReminder(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    public void setupQuickResponse(PreferenceScreen preferenceScreen) {
    }

    @Override // com.sonymobile.calendar.GeneralPreferences, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Preference preferenceFindPreference = findPreference(BUILD_VERSION);
        if (preferenceFindPreference != null) {
            try {
                preferenceFindPreference.setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException unused) {
                preferenceFindPreference.setSummary("?");
            }
        }
    }

    @Override // com.sonymobile.calendar.GeneralPreferences
    protected void goToPreference(Preference preference) {
        if (preference instanceof CustomSwitchPreference) {
            CustomSwitchPreference customSwitchPreference = (CustomSwitchPreference) preference;
            launchFragment(null, customSwitchPreference.getFragment(), customSwitchPreference.getTabletTitle());
        }
    }

    @Override // com.sonymobile.calendar.GeneralPreferences, com.sonymobile.calendar.CustomSwitchPreferenceListener
    public void onPreferenceRowClicked(String str) {
        CustomSwitchPreference customSwitchPreference = (CustomSwitchPreference) getPreferenceManager().findPreference(str);
        launchFragment(null, customSwitchPreference.getFragment(), customSwitchPreference.getTabletTitle());
    }

    @Override // com.sonymobile.calendar.GeneralPreferences, com.sonymobile.calendar.NoLocationServicesDialogListener
    public void noLocationDialogConfirmed() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("launchLocation", true);
        launchFragment(bundle, TabletWeatherPreferences.class.getName(), getString(R.string.preference_title_weather_forecast));
    }

    public void launchFragment(Bundle bundle, String str, String str2) {
        ((TabletCalendarPreferenceActivity) getActivity()).presentPreferencePanel(bundle, str, str2);
    }
}
