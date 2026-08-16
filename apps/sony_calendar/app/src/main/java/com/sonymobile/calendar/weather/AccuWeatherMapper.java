package com.sonymobile.calendar.weather;

import android.util.SparseIntArray;
import com.sonymobile.accuweather.AbstractWeatherMapper;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class AccuWeatherMapper implements AbstractWeatherMapper {
    private static volatile AccuWeatherMapper mInstance;
    private final SparseIntArray mIconMapper = new SparseIntArray();
    private final SparseIntArray mDescriptionMapper = new SparseIntArray();

    @Override // com.sonymobile.accuweather.AbstractWeatherMapper
    public int getIcon(int i) {
        return this.mIconMapper.get(i, -1);
    }

    @Override // com.sonymobile.accuweather.AbstractWeatherMapper
    public int getText(int i) {
        return this.mDescriptionMapper.get(i, -1);
    }

    public static AccuWeatherMapper getInstance() {
        if (mInstance == null) {
            synchronized (AccuWeatherMapper.class) {
                if (mInstance == null) {
                    mInstance = new AccuWeatherMapper();
                }
            }
        }
        return mInstance;
    }

    private AccuWeatherMapper() {
        mapIcons();
        mapDescriptions();
    }

    private void mapDescriptions() {
        this.mDescriptionMapper.put(1, R.string.accessibility_weather_sunny);
        this.mDescriptionMapper.put(30, R.string.accessibility_weather_sunny);
        this.mDescriptionMapper.put(5, R.string.accessibility_weather_sun_haze);
        this.mDescriptionMapper.put(2, R.string.accessibility_weather_sun_partly_cloudy);
        this.mDescriptionMapper.put(3, R.string.accessibility_weather_sun_mostly_cloudy);
        this.mDescriptionMapper.put(4, R.string.accessibility_weather_sun_partly_cloudy);
        this.mDescriptionMapper.put(21, R.string.accessibility_weather_sun_partly_cloudy);
        this.mDescriptionMapper.put(14, R.string.accessibility_weather_partly_sunny_with_showers);
        this.mDescriptionMapper.put(6, R.string.accessibility_weather_sun_mostly_cloudy);
        this.mDescriptionMapper.put(7, R.string.accessibility_weather_cloudy);
        this.mDescriptionMapper.put(8, R.string.accessibility_weather_night_mostly_cloudy);
        this.mDescriptionMapper.put(11, R.string.accessibility_weather_fog);
        this.mDescriptionMapper.put(12, R.string.accessibility_weather_showers);
        this.mDescriptionMapper.put(13, R.string.accessibility_weather_showers);
        this.mDescriptionMapper.put(39, R.string.accessibility_weather_showers);
        this.mDescriptionMapper.put(40, R.string.accessibility_weather_night_mostly_cloudy_with_showers);
        this.mDescriptionMapper.put(18, R.string.accessibility_weather_rain);
        this.mDescriptionMapper.put(15, R.string.accessibility_weather_thunder);
        this.mDescriptionMapper.put(16, R.string.accessibility_weather_thunder);
        this.mDescriptionMapper.put(17, R.string.accessibility_weather_thunder);
        this.mDescriptionMapper.put(41, R.string.accessibility_weather_thunder);
        this.mDescriptionMapper.put(42, R.string.accessibility_weather_thunder);
        this.mDescriptionMapper.put(32, R.string.accessibility_weather_windy);
        this.mDescriptionMapper.put(33, R.string.accessibility_weather_night);
        this.mDescriptionMapper.put(37, R.string.accessibility_weather_night_haze);
        this.mDescriptionMapper.put(34, R.string.accessibility_weather_night_partly_cloudy);
        this.mDescriptionMapper.put(35, R.string.accessibility_weather_night_partly_cloudy);
        this.mDescriptionMapper.put(36, R.string.accessibility_weather_night_partly_cloudy);
        this.mDescriptionMapper.put(38, R.string.accessibility_weather_night_mostly_cloudy);
        this.mDescriptionMapper.put(25, R.string.accessibility_weather_sleet);
        this.mDescriptionMapper.put(26, R.string.accessibility_weather_snow);
        this.mDescriptionMapper.put(29, R.string.accessibility_weather_sleet);
        this.mDescriptionMapper.put(19, R.string.accessibility_weather_sleet);
        this.mDescriptionMapper.put(20, R.string.accessibility_weather_sleet);
        this.mDescriptionMapper.put(43, R.string.accessibility_weather_sleet);
        this.mDescriptionMapper.put(44, R.string.accessibility_weather_sleet);
        this.mDescriptionMapper.put(22, R.string.accessibility_weather_snow);
        this.mDescriptionMapper.put(23, R.string.accessibility_weather_snow);
        this.mDescriptionMapper.put(24, R.string.accessibility_weather_ice);
        this.mDescriptionMapper.put(31, R.string.accessibility_weather_ice);
    }

    private void mapIcons() {
        this.mIconMapper.put(1, R.drawable.sunny);
        this.mIconMapper.put(30, R.drawable.sunny_hot);
        this.mIconMapper.put(5, R.drawable.sun_haze);
        this.mIconMapper.put(2, R.drawable.sun_partly_cloudy);
        this.mIconMapper.put(3, R.drawable.sun_mostly_cloudy);
        this.mIconMapper.put(4, R.drawable.sun_partly_cloudy);
        this.mIconMapper.put(21, R.drawable.sun_partly_cloudy);
        this.mIconMapper.put(14, R.drawable.partly_sunny_with_showers);
        this.mIconMapper.put(6, R.drawable.mostly_cloudy);
        this.mIconMapper.put(7, R.drawable.cloudy);
        this.mIconMapper.put(8, R.drawable.cloudy);
        this.mIconMapper.put(11, R.drawable.fog);
        this.mIconMapper.put(12, R.drawable.showers);
        this.mIconMapper.put(13, R.drawable.mostly_cloudy_with_showers);
        this.mIconMapper.put(39, R.drawable.night_partly_cloudy_with_showers);
        this.mIconMapper.put(40, R.drawable.night_mostly_cloudy_with_showers);
        this.mIconMapper.put(18, R.drawable.rain);
        this.mIconMapper.put(15, R.drawable.thunder);
        this.mIconMapper.put(16, R.drawable.thunder);
        this.mIconMapper.put(17, R.drawable.thunder);
        this.mIconMapper.put(41, R.drawable.thunder);
        this.mIconMapper.put(42, R.drawable.thunder);
        this.mIconMapper.put(32, R.drawable.windy);
        this.mIconMapper.put(33, R.drawable.night);
        this.mIconMapper.put(37, R.drawable.night_haze);
        this.mIconMapper.put(34, R.drawable.night_partly_cloudy);
        this.mIconMapper.put(35, R.drawable.night_partly_cloudy);
        this.mIconMapper.put(36, R.drawable.night_partly_cloudy);
        this.mIconMapper.put(38, R.drawable.night_mostly_cloudy);
        this.mIconMapper.put(25, R.drawable.sleet);
        this.mIconMapper.put(26, R.drawable.snow);
        this.mIconMapper.put(29, R.drawable.sleet);
        this.mIconMapper.put(19, R.drawable.sleet);
        this.mIconMapper.put(20, R.drawable.sleet);
        this.mIconMapper.put(43, R.drawable.sleet);
        this.mIconMapper.put(44, R.drawable.sleet);
        this.mIconMapper.put(22, R.drawable.snow);
        this.mIconMapper.put(23, R.drawable.snow);
        this.mIconMapper.put(24, R.drawable.ice);
        this.mIconMapper.put(31, R.drawable.ice);
    }
}
