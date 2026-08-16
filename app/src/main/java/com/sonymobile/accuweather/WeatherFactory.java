package com.sonymobile.accuweather;

import android.content.Context;
import android.location.Location;
import java.util.AbstractCollection;

/* JADX INFO: loaded from: classes.dex */
public abstract class WeatherFactory {
    private static final int MAX_NUMBER_OF_FORECAST_DAYS = 5;
    protected Context mContext;

    public abstract AccuWeatherBroker createWeatherBroker();

    public abstract AbstractWeatherMapper createWeatherMapper();

    public abstract Location getCurrentLocation();

    public int getMaxNumberOfForecastDays() {
        return 5;
    }

    public abstract int getNumberOfForecastDays(AbstractCollection<?> abstractCollection);

    public abstract String getWeatherInformationDialogText();

    public abstract String getWeatherInformationDialogTitle();

    public abstract boolean getWeatherLocationEntryEnabled(boolean z);

    public abstract boolean isLocationAvailable(Location location);

    public abstract boolean isWeatherBrokerAvailable();

    public abstract boolean launchThirdPartyActivityForecast(WeatherInfo weatherInfo, boolean z);

    public WeatherFactory(Context context) {
        this.mContext = context;
    }
}
