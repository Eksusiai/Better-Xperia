package com.sonymobile.accuweather;

import android.content.Context;
import android.location.Location;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONTokener;

/* JADX INFO: loaded from: classes.dex */
public abstract class AccuWeatherBroker {
    protected static final String CURRENT_CONDITIONS = "currentconditions/v1";
    protected static final String CURRENT_CONDITION_BY_LOCATION = "v1/cities/geoposition";
    protected static final String FORECAST_FIVE_DAYS = "forecasts/v1/daily/5day";
    protected static String PARTNER = null;
    public static final String PARTNER_PARAMETER_CALENDAR = "partner=sonymobilecalendar";
    public static final String PARTNER_PARAMETER_ORGANIZER = "partner=sonymobilewclock";
    protected static final String PATH_CITYFIND = "v1/cities/search.json";
    protected static final String QUERY_KEY_LOCATION = "locations";
    protected static final String QUERY_KEY_METRIC = "metric";
    public static final String TAG_JSON_LOCATION_EMPTY = "{\"key_city_state\":\"\",\"key_position_recent\":0,\"key_city_id\":\"\",\"key_city_name\":\"Current location\"}";
    protected static final String URL_HOST = "https://api.accuweather.com";

    public abstract List<WeatherLocation> getLocations(Context context, String str, boolean z) throws DataNotFoundException;

    public abstract WeatherSet getWeatherData(Context context, Location location) throws DataNotFoundException;

    public abstract WeatherSet getWeatherData(Context context, String str) throws DataNotFoundException;

    public abstract boolean isWeatherDataInitialized(WeatherSet weatherSet);

    public static void setPartnerString(String str) {
        if (PARTNER == null) {
            PARTNER = str;
        }
    }

    protected JSONTokener getTokenizer(String str) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openStreamHook(str, 3000)));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = bufferedReader.readLine();
            if (line != null) {
                sb.append(line);
            } else {
                return new JSONTokener(sb.toString());
            }
        }
    }

    protected InputStream openStreamHook(String str, int i) throws IOException {
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(str).openConnection();
        if (httpsURLConnection == null) {
            throw new IOException();
        }
        if (i > 0) {
            httpsURLConnection.setConnectTimeout(i);
            httpsURLConnection.setReadTimeout(i);
        }
        return httpsURLConnection.getInputStream();
    }
}
