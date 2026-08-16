package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.sonyericsson.calendar.util.WeatherService;
import com.sonymobile.accuweather.WeatherLocation;
import com.sonymobile.calendar.weather.WeatherForecast;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherPreferences extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, WeatherLocationSelectedListener, DialogInterface.OnClickListener {
    public static final String KEY_CURRENT_LOCATION = "key_current_location";
    public static final String KEY_HOME_LOCATION = "key_home_location";
    public static final String KEY_WEATHER_LOCATION = "preferences_weather_location";
    public static final String KEY_WEATHER_SET = "key_weather_set";
    public static final String KEY_WEATHER_UNIT = "preferences_weather_unit";
    public static final String KEY_WEATHER_UPDATE = "preferences_weather_update";
    private static final int REQUEST_OPEN_SETTING_LOCATION = 1;
    private Switch actionBarSwitch;
    private Toolbar activityToolbar;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.WeatherPreferences.1
        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (Utils.isUsingWeatherBrokerForChina(WeatherPreferences.this.getActivity())) {
                if (z && !Utils.isAnyLocationProviderEnabled(WeatherPreferences.this.getActivity())) {
                    WeatherPreferences.this.startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 1);
                    return;
                }
            } else if (z && !Utils.isAnyLocationProviderEnabled(WeatherPreferences.this.getActivity()) && Utils.isCurrentLocationSet(WeatherPreferences.this.getActivity())) {
                WeatherPreferences.this.createDialog();
                return;
            }
            WeatherPreferences.this.applyPreference(z);
            WeatherPreferences.this.updateStatus(z);
            WeatherPreferences.this.applyPreference(z);
        }
    };
    private TextView mOnOffLabel;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.weather_preferences);
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setupSwitchLayout();
        boolean z = true;
        if (!GeneralPreferences.getSharedPreferences(getActivity()).getBoolean(GeneralPreferences.KEY_WEATHER, true) || (!Utils.isAnyLocationProviderEnabled(getActivity()) && Utils.isCurrentLocationSet(getActivity()))) {
            z = false;
        }
        this.actionBarSwitch.setChecked(z);
        applyPreference(z);
        updateStatus(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStatus(boolean z) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.findPreference(KEY_WEATHER_LOCATION).setEnabled(CalendarApplication.getWeatherFactory().getWeatherLocationEntryEnabled(z));
        preferenceManager.findPreference(KEY_WEATHER_UPDATE).setEnabled(z);
        preferenceManager.findPreference(KEY_WEATHER_UNIT).setEnabled(z);
        if (Utils.isTabletDevice(getActivity())) {
            return;
        }
        if (z) {
            this.mOnOffLabel.setText(R.string.on_label);
        } else {
            this.mOnOffLabel.setText(R.string.off_label);
        }
    }

    private void updateSummary() {
        WeatherLocation weatherLocationFromJSON;
        PreferenceManager preferenceManager = getPreferenceManager();
        ListPreference listPreference = (ListPreference) preferenceManager.findPreference(KEY_WEATHER_UPDATE);
        listPreference.setSummary(listPreference.getEntry());
        ListPreference listPreference2 = (ListPreference) preferenceManager.findPreference(KEY_WEATHER_UNIT);
        listPreference2.setSummary(listPreference2.getEntry());
        listPreference.setOnPreferenceChangeListener(this);
        listPreference2.setOnPreferenceChangeListener(this);
        preferenceManager.findPreference(KEY_WEATHER_LOCATION).setOnPreferenceClickListener(this);
        Preference preferenceFindPreference = preferenceManager.findPreference(KEY_WEATHER_LOCATION);
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(getActivity());
        String string = WeatherLocation.currentLocation(Utils.getCurrentLocationString(getActivity())).getJSONObject().toString();
        String string2 = sharedPreferences.getString(KEY_CURRENT_LOCATION, string);
        String string3 = sharedPreferences.getString(KEY_HOME_LOCATION, string);
        try {
            if (!string2.equals(string)) {
                weatherLocationFromJSON = WeatherLocation.fromJSON(new JSONObject(string2));
            } else {
                weatherLocationFromJSON = WeatherLocation.fromJSON(new JSONObject(string3));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            weatherLocationFromJSON = null;
        }
        if (weatherLocationFromJSON != null) {
            preferenceFindPreference.setSummary(getString(R.string.preferences_weather_current_location_description) + ": " + weatherLocationFromJSON.toString());
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        updateSummary();
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    protected void setupCustomActionbar(View view, ActionBar.LayoutParams layoutParams) {
        ActionBar supportActionBar = Utils.getSupportActionBar(getActivity());
        supportActionBar.setCustomView((View) null);
        supportActionBar.setTitle(getString(R.string.preferences_display_weather));
        if (Utils.isTabletDevice(getActivity())) {
            supportActionBar.setDisplayOptions(16, 16);
            supportActionBar.setCustomView(view, layoutParams);
        }
        supportActionBar.setDisplayShowTitleEnabled(true);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupSwitchLayout() {
        ActionBar.LayoutParams layoutParams;
        if (Utils.isTabletDevice(getActivity())) {
            Switch r0 = new Switch(getActivity());
            this.actionBarSwitch = r0;
            r0.setFocusable(true);
            this.actionBarSwitch.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.actionbar_switch_padding), 0);
            this.actionBarSwitch.setOnCheckedChangeListener(this.checkedChangeListener);
            layoutParams = new ActionBar.LayoutParams(-2, -2, 8388629);
        } else {
            View viewInflate = LayoutInflater.from(getActivity()).inflate(R.layout.preference_toolbar, this.activityToolbar);
            Switch r3 = (Switch) viewInflate.findViewById(R.id.pref_switch);
            this.actionBarSwitch = r3;
            r3.setFocusable(true);
            this.actionBarSwitch.setOnCheckedChangeListener(this.checkedChangeListener);
            this.mOnOffLabel = (TextView) viewInflate.findViewById(R.id.on_off_label);
            this.activityToolbar.setVisibility(0);
            layoutParams = null;
        }
        setupCustomActionbar(this.actionBarSwitch, layoutParams);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (1 == i && !Utils.isAnyLocationProviderEnabled(getActivity())) {
            Switch r0 = this.actionBarSwitch;
            if (r0 != null) {
                r0.setChecked(false);
            }
            applyPreference(false);
        } else {
            Switch r1 = this.actionBarSwitch;
            if (r1 != null) {
                r1.setChecked(true);
            }
            applyPreference(true);
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference.getKey().equals(KEY_WEATHER_UPDATE)) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(obj.toString())]);
            return true;
        }
        if (!preference.getKey().equals(KEY_WEATHER_UNIT)) {
            return false;
        }
        ListPreference listPreference2 = (ListPreference) preference;
        listPreference2.setValue((String) obj);
        listPreference2.setSummary(listPreference2.getEntry());
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (!preference.getKey().equals(KEY_WEATHER_LOCATION)) {
            return false;
        }
        showWeatherLocation();
        return true;
    }

    protected void showWeatherLocation() {
        WeatherLocationFragment weatherLocationFragment = new WeatherLocationFragment();
        weatherLocationFragment.setTargetFragment(this, 1234);
        FragmentTransaction fragmentTransactionBeginTransaction = getFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.pref_content_frame, weatherLocationFragment);
        fragmentTransactionBeginTransaction.addToBackStack("");
        fragmentTransactionBeginTransaction.commit();
        if (Utils.isTabletDevice(getActivity())) {
            return;
        }
        this.activityToolbar.setVisibility(8);
    }

    @Override // com.sonymobile.calendar.WeatherLocationSelectedListener
    public void onCitySelected(WeatherLocation weatherLocation) {
        getPreferenceManager().findPreference(KEY_WEATHER_LOCATION).setSummary(weatherLocation.toString());
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(getActivity()).edit();
        editorEdit.putString(KEY_CURRENT_LOCATION, weatherLocation.getJSONObject().toString());
        editorEdit.putString(WeatherForecast.KEY_STORED_WEATHER_INFO, null);
        editorEdit.apply();
        WeatherService.getInstance().markWeatherInfoAsDirty();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != -2) {
            if (i != -1) {
                return;
            }
            showWeatherLocation();
        } else {
            this.actionBarSwitch.setChecked(false);
            applyPreference(false);
            updateStatus(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle(getString(R.string.no_location_provider_enabled_dialog_title));
        builder.setMessage(getString(R.string.no_location_provider_enabled_dialog_message));
        builder.setPositiveButton(getString(R.string.dialog_button_add), this);
        builder.setNegativeButton(getString(R.string.clr_strings_button_title_cancel_txt), this);
        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.sonymobile.calendar.WeatherPreferences.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                WeatherPreferences.this.actionBarSwitch.setChecked(false);
                WeatherPreferences.this.applyPreference(false);
            }
        });
        alertDialogCreate.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyPreference(boolean z) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(getActivity()).edit();
        editorEdit.putBoolean(GeneralPreferences.KEY_WEATHER, z);
        editorEdit.apply();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        FragmentActivity activity = getActivity();
        if (activity instanceof CalendarSettingsActivity) {
            CalendarSettingsActivity calendarSettingsActivity = (CalendarSettingsActivity) activity;
            calendarSettingsActivity.updateToolbar(true);
            this.activityToolbar = calendarSettingsActivity.getToolbar();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
    }
}
