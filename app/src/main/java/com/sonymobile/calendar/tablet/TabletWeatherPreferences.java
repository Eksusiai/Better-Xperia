package com.sonymobile.calendar.tablet;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WeatherLocationFragment;
import com.sonymobile.calendar.WeatherPreferences;

/* JADX INFO: loaded from: classes2.dex */
public class TabletWeatherPreferences extends WeatherPreferences {
    @Override // com.sonymobile.calendar.WeatherPreferences
    protected void setupCustomActionbar(View view, ActionBar.LayoutParams layoutParams) {
        ActionBar supportActionBar = Utils.getSupportActionBar(getActivity());
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.preferences_display_weather);
            supportActionBar.setDisplayOptions(16, 16);
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setCustomView(view, layoutParams);
        }
    }

    @Override // com.sonymobile.calendar.WeatherPreferences, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() == null || !getArguments().containsKey("launchLocation")) {
            return;
        }
        launchFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        ActionBar supportActionBar = Utils.getSupportActionBar(getActivity());
        if (supportActionBar != null) {
            supportActionBar.setDisplayOptions(4, 4);
            supportActionBar.setDisplayShowCustomEnabled(false);
        }
    }

    @Override // com.sonymobile.calendar.WeatherPreferences
    protected void showWeatherLocation() {
        launchFragment();
    }

    private void launchFragment() {
        WeatherLocationFragment weatherLocationFragment = new WeatherLocationFragment();
        weatherLocationFragment.setTargetFragment(this, weatherLocationFragment.getId());
        ((TabletCalendarPreferenceActivity) getActivity()).replacePreferenceFragment(weatherLocationFragment);
    }
}
