package com.sonyericsson.calendar.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import com.sonymobile.accuweather.AbstractWeatherMapper;
import com.sonymobile.accuweather.CurrentCondition;
import com.sonymobile.accuweather.ForecastCondition;
import com.sonymobile.accuweather.WeatherInfo;
import com.sonymobile.accuweather.WeatherListener;
import com.sonymobile.accuweather.WeatherSet;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WeatherPreferences;
import com.sonymobile.calendar.weather.WeatherForecast;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public class WeatherService implements WeatherListener {
    public static final int REQUEST_LOAD = 0;
    private static WeatherService instance;
    private Queue<OnReadyHandler> handlers = new LinkedList();
    private WeatherSet currentSet = null;
    private boolean isLoading = false;

    protected interface OnReadyHandler {
        void onReady(boolean z);
    }

    @Override // com.sonymobile.accuweather.WeatherListener
    public void onWeatherUpdateFinished(WeatherInfo weatherInfo) {
    }

    private WeatherService() {
    }

    public static WeatherService getInstance() {
        if (instance == null) {
            instance = new WeatherService();
        }
        return instance;
    }

    public static int celsius2Fahrenheit(int i) {
        return ((i * 9) / 5) + 32;
    }

    public static boolean getDefaultEnabledStatus(Context context) {
        return !CustomizeConfig.getInstance().getShowDataUsage(context);
    }

    public void requestWeather(Context context, final IAsyncServiceResultHandler iAsyncServiceResultHandler, final int i, boolean z) {
        boolean zIsDataTrafficEnabled = Utils.isDataTrafficEnabled(context);
        if (!GeneralPreferences.getSharedPreferences(context).getBoolean(GeneralPreferences.KEY_WEATHER, getDefaultEnabledStatus(context)) || !zIsDataTrafficEnabled) {
            this.currentSet = null;
            iAsyncServiceResultHandler.onResult(true, Integer.valueOf(i));
            return;
        }
        if (!GeneralPreferences.getSharedPreferences(context).getString(GeneralPreferences.KEY_CURRENT_LANGUAGE, "").equals(Locale.getDefault().getLanguage())) {
            this.currentSet = null;
        }
        if (!z && !isWeatherDirty(context)) {
            iAsyncServiceResultHandler.onResult(true, Integer.valueOf(i));
            return;
        }
        if (!this.isLoading) {
            new WeatherForecast(context, this, true);
        }
        addHandler(new OnReadyHandler() { // from class: com.sonyericsson.calendar.util.WeatherService.1
            @Override // com.sonyericsson.calendar.util.WeatherService.OnReadyHandler
            public void onReady(boolean z2) {
                iAsyncServiceResultHandler.onResult(Boolean.valueOf(z2), Integer.valueOf(i));
            }
        });
    }

    private void addHandler(OnReadyHandler onReadyHandler) {
        this.handlers.add(onReadyHandler);
    }

    public WeatherInfo getWeatherInfo(Time time) {
        if (this.currentSet != null && isForecastDate(time)) {
            AbstractWeatherMapper abstractWeatherMapperCreateWeatherMapper = CalendarApplication.getWeatherFactory().createWeatherMapper();
            WeatherInfo weatherInfo = new WeatherInfo();
            Object weatherInfoForDate = this.currentSet.getWeatherInfoForDate(time);
            weatherInfo.mCityName = this.currentSet.getCurrentLocation();
            if (weatherInfoForDate instanceof CurrentCondition) {
                CurrentCondition currentCondition = (CurrentCondition) weatherInfoForDate;
                weatherInfo.highTemperature = currentCondition.getTemperature();
                int weatherType = currentCondition.getWeatherType();
                weatherInfo.iconResource = abstractWeatherMapperCreateWeatherMapper.getIcon(weatherType);
                weatherInfo.iconStringDescription = abstractWeatherMapperCreateWeatherMapper.getText(weatherType);
                weatherInfo.forecastUrl = this.currentSet.getExtendedForecastUrl();
                weatherInfo.isToday = true;
                return weatherInfo;
            }
            if (weatherInfoForDate instanceof ForecastCondition) {
                ForecastCondition forecastCondition = (ForecastCondition) weatherInfoForDate;
                boolean zIsDaytime = this.currentSet.getWeatherCurrentCondition().isDaytime();
                weatherInfo.highTemperature = forecastCondition.getPeriod(zIsDaytime).getHighTemperature();
                weatherInfo.lowTemperature = forecastCondition.getPeriod(zIsDaytime).getLowTemperature();
                int weatherCondition = forecastCondition.getPeriod(zIsDaytime).getWeatherCondition();
                weatherInfo.iconResource = abstractWeatherMapperCreateWeatherMapper.getIcon(weatherCondition);
                weatherInfo.iconStringDescription = abstractWeatherMapperCreateWeatherMapper.getText(weatherCondition);
                weatherInfo.forecastUrl = forecastCondition.getForecastUrl();
                weatherInfo.isToday = false;
                return weatherInfo;
            }
        }
        return null;
    }

    private boolean isForecastDate(Time time) {
        int offset = Utils.getOffset(time);
        return offset >= 0 && offset < CalendarApplication.getWeatherFactory().getMaxNumberOfForecastDays();
    }

    public boolean isWeatherDirty(Context context) {
        return this.currentSet == null || refreshNeeded(context);
    }

    private boolean refreshNeeded(Context context) {
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(context);
        return System.currentTimeMillis() > Long.parseLong(sharedPreferences.getString(WeatherForecast.KEY_LAST_UPDATE, "0")) + Long.parseLong(sharedPreferences.getString(WeatherPreferences.KEY_WEATHER_UPDATE, "1800000"));
    }

    public void markWeatherInfoAsDirty() {
        this.currentSet = null;
    }

    @Override // com.sonymobile.accuweather.WeatherListener
    public void onWeatherUpdating() {
        this.isLoading = true;
    }

    @Override // com.sonymobile.accuweather.WeatherListener
    public void onWeatherUpdateFailed() {
        while (!this.handlers.isEmpty()) {
            this.handlers.remove().onReady(false);
        }
        this.isLoading = false;
    }

    @Override // com.sonymobile.accuweather.WeatherListener
    public void onWeatherNetworkError() {
        while (!this.handlers.isEmpty()) {
            this.handlers.remove().onReady(false);
        }
        this.isLoading = false;
    }

    @Override // com.sonymobile.accuweather.WeatherListener
    public void onWeatherUpdateFinished(WeatherSet weatherSet) {
        this.currentSet = weatherSet;
        while (!this.handlers.isEmpty()) {
            this.handlers.remove().onReady(true);
        }
        this.isLoading = false;
    }
}
