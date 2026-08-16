package com.sonymobile.accuweather;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface WeatherLocationListener {
    void onWeatherLocationUpdateFailed();

    void onWeatherLocationUpdateFinished(List<WeatherLocation> list);
}
