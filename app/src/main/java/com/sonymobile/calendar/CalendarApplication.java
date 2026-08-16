package com.sonymobile.calendar;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.BuildCompat;
import com.sonymobile.accuweather.ApiKey;
import com.sonymobile.accuweather.RequestBuilder;
import com.sonymobile.accuweather.WeatherFactory;
import com.sonymobile.calendar.tasks.settings.SettingsActivity;
import com.sonymobile.calendar.weather.WeatherFactoryAccu;
import java.math.BigInteger;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarApplication extends Application {
    private static final int COLOR_EXCHANGE = -16896;
    private static final int COLOR_GOOGLE = -1625295;
    private static final int COLOR_SYNC_ML = -16757867;
    private static WeatherFactory mWeatherFactory;
    private static CalendarApplication sCalendarApp;

    static {
        RequestBuilder.init(BuildConfig.URL_PREFIX);
    }

    static int getCalendarColorFromSyncAccount(String str) {
        if (str == null) {
            return -16777216;
        }
        if (str.equals("LOCAL") || str.contains("sonyericsson")) {
            return COLOR_SYNC_ML;
        }
        return str.contains("google") ? COLOR_GOOGLE : COLOR_EXCHANGE;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        sCalendarApp = this;
        GeneralPreferences.setDefaultValues(this);
        applyThemeMode();
        String str = String.format("%064X", new BigInteger(1, ApiKey.getSignatureSHA256(this)));
        if (BuildConfig.TESTKEY_SHA256.equalsIgnoreCase(str)) {
            ApiKey.API_KEY_PART_A = getString(R.string.TESTKEY_APIKEY_PART_A);
            ApiKey.API_KEY_PART_B = BuildConfig.TESTKEY_API_KEY_PART_B;
            ApiKey.API_KEY_PART_C = BuildConfig.TESTKEY_API_KEY_PART_C;
        } else if (BuildConfig.LIVE_SHA256.equalsIgnoreCase(str)) {
            ApiKey.API_KEY_PART_A = getString(R.string.LIVE_APIKEY_PART_A);
            ApiKey.API_KEY_PART_B = BuildConfig.LIVE_API_KEY_PART_B;
            ApiKey.API_KEY_PART_C = BuildConfig.LIVE_API_KEY_PART_C;
        }
        mWeatherFactory = new WeatherFactoryAccu(this);
    }

    public static boolean isR2L(Resources resources) {
        return resources.getBoolean(R.bool.is_rtl);
    }

    public static void applyThemeMode() {
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(sCalendarApp);
        String theme = sharedPreferences.getString(GeneralPreferences.KEY_THEME, GeneralPreferences.THEME_VALUE_SYSTEM);
        if (GeneralPreferences.THEME_VALUE_LIGHT.equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (GeneralPreferences.THEME_VALUE_DARK.equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    public static boolean isIsInMultiWindowMode(Activity activity) {
        return BuildCompat.isAtLeastN() && activity.isInMultiWindowMode();
    }

    public static WeatherFactory getWeatherFactory() {
        return mWeatherFactory;
    }

    public static void launchSettingsActivity() {
        Context applicationContext = sCalendarApp.getApplicationContext();
        applicationContext.startActivity(new Intent(applicationContext, (Class<?>) SettingsActivity.class));
    }
}
