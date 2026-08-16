package com.sonymobile.accuweather;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AccuWeatherSearchForLocation extends AccuWeatherBroker {
    private static final String ADMINISTRATIVE_NAME_LOCALIZED_NAME = "AdministrativeArea";
    private static final String CITY_KEY = "Key";
    private static final String COUNTRY = "Country";
    private static final String COUNTRY_ID = "ID";
    private static final String LOCALIZED_NAME = "LocalizedName";
    private static final String TAG = "AccuWeatherSearchForLocation";
    private static HashMap<String, Integer> mLocationMap;

    @Override // com.sonymobile.accuweather.AccuWeatherBroker
    public WeatherSet getWeatherData(Context context, Location location) {
        return null;
    }

    @Override // com.sonymobile.accuweather.AccuWeatherBroker
    public WeatherSet getWeatherData(Context context, String str) {
        return null;
    }

    static {
        HashMap<String, Integer> map = new HashMap<>();
        mLocationMap = map;
        map.put("en", 1);
        mLocationMap.put("es", 2);
        mLocationMap.put("fr", 3);
        mLocationMap.put("da", 4);
        mLocationMap.put("pt", 5);
        mLocationMap.put("nl", 6);
        mLocationMap.put("nb", 7);
        mLocationMap.put("it", 8);
        mLocationMap.put("de", 9);
        mLocationMap.put("sv", 10);
        mLocationMap.put("fi", 11);
        mLocationMap.put("zh_HK", 12);
        mLocationMap.put("zh_CN", 13);
        mLocationMap.put("zh_TW", 14);
        mLocationMap.put("sk", 17);
        mLocationMap.put("ro", 18);
        mLocationMap.put("cs", 19);
        mLocationMap.put("hu", 20);
        mLocationMap.put("pl", 21);
        mLocationMap.put("ca", 22);
        mLocationMap.put("hi", 24);
        mLocationMap.put("ru", 25);
        mLocationMap.put("ar", 26);
        mLocationMap.put("el", 27);
        mLocationMap.put("en_GB", 28);
        mLocationMap.put("ja", 29);
        mLocationMap.put("ko", 30);
        mLocationMap.put("tr", 31);
        mLocationMap.put("fr_CA", 32);
        mLocationMap.put("iw", 33);
    }

    @Override // com.sonymobile.accuweather.AccuWeatherBroker
    public List<WeatherLocation> getLocations(Context context, String str, boolean z) {
        String cityRequestUrl = getCityRequestUrl(context, str, false, z);
        ArrayList arrayList = new ArrayList();
        try {
            JSONArray jSONArray = new JSONArray(getTokenizer(cityRequestUrl));
            if (jSONArray.length() == 0) {
                jSONArray = new JSONArray(getTokenizer(getCityRequestUrl(context, str, true, z)));
            }
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                String string = jSONObject.getString(LOCALIZED_NAME);
                JSONObject jSONObject2 = new JSONObject(jSONObject.getString(COUNTRY));
                String string2 = jSONObject2.getString(LOCALIZED_NAME);
                String string3 = jSONObject2.getString(COUNTRY_ID);
                String string4 = new JSONObject(jSONObject.getString(ADMINISTRATIVE_NAME_LOCALIZED_NAME)).getString(LOCALIZED_NAME);
                String string5 = jSONObject.getString(CITY_KEY);
                if (string3.equals("CN") || string3.equals("HK") || string3.equals("MO") || string3.equals("TW")) {
                    string2 = "";
                    string4 = string2;
                }
                arrayList.add(new WeatherLocation(string, string2, string4, string5));
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException", e);
        } catch (IOException e2) {
            Log.e(TAG, "IOException", e2);
        } catch (JSONException e3) {
            Log.e(TAG, "JSONException", e3);
        }
        return arrayList;
    }

    public String getCityRequestUrl(Context context, String str, boolean z, boolean z2) {
        Objects.requireNonNull(str, "searchTerm must not be null");
        int iIndexOf = str.indexOf(47);
        if (iIndexOf != -1 && iIndexOf != 0) {
            str = str.substring(0, iIndexOf - 1);
        }
        try {
            URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            str.replace(" ", "%20");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(RequestBuilder.buildFindLocationRequestUrl(context, str, z, z2).toString());
        if (PARTNER != null) {
            sb.append("&").append(PARTNER);
        }
        return sb.toString();
    }

    private static int getLanguageCode() {
        String language = Locale.getDefault().getLanguage();
        Integer num = mLocationMap.get(language + "_" + Locale.getDefault().getCountry());
        if (num == null) {
            num = mLocationMap.get(language);
        }
        if (num != null) {
            return num.intValue();
        }
        return 1;
    }

    @Override // com.sonymobile.accuweather.AccuWeatherBroker
    public boolean isWeatherDataInitialized(WeatherSet weatherSet) {
        return (weatherSet.getWeatherForecastConditions() == null || weatherSet.getWeatherForecastConditions().isEmpty()) ? false : true;
    }
}
