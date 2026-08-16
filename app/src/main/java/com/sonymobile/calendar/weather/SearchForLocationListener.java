package com.sonymobile.calendar.weather;

import com.sonymobile.accuweather.WeatherLocation;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public interface SearchForLocationListener {
    void getWeatherLocationFromSearch(WeatherLocation weatherLocation);

    void getWeatherLocationsFromSearch(List<WeatherLocation> list);
}
