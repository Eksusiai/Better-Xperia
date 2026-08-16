package com.sonymobile.accuweather;

/* JADX INFO: loaded from: classes.dex */
public interface WeatherListener {
    void onWeatherNetworkError();

    void onWeatherUpdateFailed();

    void onWeatherUpdateFinished(WeatherInfo weatherInfo);

    void onWeatherUpdateFinished(WeatherSet weatherSet);

    void onWeatherUpdating();
}
