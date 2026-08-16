package com.sonymobile.accuweather;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.location.Location;
import android.text.format.Time;
import android.util.Log;
import com.sonyericsson.calendar.util.CalendarConstants;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AccuWeatherForecast extends AccuWeatherBroker {
    public static final String TAG = "AccuWeatherForecast";
    private static String TAG_CITY_NAME = "LocalizedName";
    private static String TAG_COUNTRY_NAME = "Country";
    private static String TAG_CURRENT_CONDITION_ICON = "WeatherIcon";
    private static String TAG_CURRENT_CONDITION_LOCAL_TIME = "LocalObservationDateTime";
    private static String TAG_DAILY_FORECASTS = "DailyForecasts";
    private static String TAG_DAY = "Day";
    private static String TAG_DAYLIGHT_SAVING = "IsDaylightSaving";
    private static String TAG_DAY_DATE = "Date";
    private static String TAG_ICON = "Icon";
    private static String TAG_IS_DAY_TIME = "IsDayTime";
    private static String TAG_KEY = "Key";
    private static String TAG_MAXIMUM_TEMPERATURE = "Maximum";
    private static String TAG_MINIMUM_TEMPERATURE = "Minimum";
    private static String TAG_MOBILE_LINK = "MobileLink";
    private static String TAG_NIGHT = "Night";
    private static String TAG_TEMPERATURE = "Temperature";
    private static String TAG_TEMPERATURE_METRIC = "Metric";
    private static String TAG_TEMPERATURE_VALUE = "Value";
    private static String TAG_TIMEZONE = "TimeZone";
    private static String TAG_TIMEZONE_NAME = "Name";
    private static String TAG_WEATHER_TEXT = "WeatherText";
    public static String mCityName = "";
    private static String mCountryName = "";

    @Override // com.sonymobile.accuweather.AccuWeatherBroker
    public List<WeatherLocation> getLocations(Context context, String str, boolean z) throws DataNotFoundException {
        return null;
    }

    @Override // com.sonymobile.accuweather.AccuWeatherBroker
    public WeatherSet getWeatherData(Context context, Location location) {
        String string = "";
        try {
            JSONObject jSONObject = new JSONObject(getTokenizer(getCurrentConditionUrlByLocation(context, location)));
            string = jSONObject.getString(TAG_KEY);
            mCityName = jSONObject.getString(TAG_CITY_NAME);
            mCountryName = jSONObject.getJSONObject(TAG_COUNTRY_NAME).getString(TAG_CITY_NAME);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } catch (JSONException e2) {
            Log.e(TAG, "JSONException", e2);
        }
        return getWeatherData(context, string);
    }

    @Override // com.sonymobile.accuweather.AccuWeatherBroker
    public WeatherSet getWeatherData(Context context, String str) {
        return getWeatherSet(getCurrentConditionUrlByCityId(context, str), getForecastUrlByCityId(context, str), getLocationDetailsByCityId(context, str));
    }

    private WeatherSet getWeatherSet(String str, String str2, String str3) {
        WeatherSet weatherSet = new WeatherSet();
        try {
            JSONArray jSONArray = new JSONArray(getTokenizer(str));
            JSONObject jSONObject = jSONArray.getJSONObject(0);
            JSONObject jSONObject2 = new JSONObject(getTokenizer(str2));
            JSONObject jSONObject3 = new JSONObject(getTokenizer(str3));
            Time time = formatTime(jSONObject.getString(TAG_CURRENT_CONDITION_LOCAL_TIME));
            Integer numValueOf = Integer.valueOf(Integer.parseInt(jSONObject.getString(TAG_CURRENT_CONDITION_ICON)));
            Boolean boolValueOf = Boolean.valueOf(Boolean.parseBoolean(jSONObject.getString(TAG_IS_DAY_TIME)));
            Double dValueOf = Double.valueOf(Double.parseDouble(jSONObject.getJSONObject(TAG_TEMPERATURE).getJSONObject(TAG_TEMPERATURE_METRIC).getString(TAG_TEMPERATURE_VALUE)));
            String string = jSONObject.getString(TAG_MOBILE_LINK);
            String string2 = jSONObject.getString(TAG_WEATHER_TEXT);
            weatherSet.setCurrentLocation(mCityName);
            weatherSet.setCurrentLocationState(mCountryName);
            weatherSet.setWeatherCurrentCondition(new CurrentCondition());
            weatherSet.getWeatherCurrentCondition().setDaytime(boolValueOf.booleanValue());
            weatherSet.getWeatherCurrentCondition().setForecastDay(time);
            weatherSet.getWeatherCurrentCondition().setTemperature(dValueOf.intValue());
            weatherSet.getWeatherCurrentCondition().setWeatherType(numValueOf.intValue());
            weatherSet.setExtendedForecastUrl(string);
            weatherSet.setCurrentGmtOffset(getTimeOffset(jSONObject.getString(TAG_CURRENT_CONDITION_LOCAL_TIME)));
            weatherSet.getWeatherCurrentCondition().setWeatherText(string2);
            JSONArray jSONArray2 = jSONObject2.getJSONArray(TAG_DAILY_FORECASTS);
            for (int i = 0; i < jSONArray2.length(); i++) {
                JSONObject jSONObject4 = jSONArray2.getJSONObject(i);
                Time time2 = formatTime(jSONObject4.getString(TAG_DAY_DATE));
                String string3 = jSONObject4.getString(TAG_MOBILE_LINK);
                JSONObject jSONObject5 = jSONObject4.getJSONObject(TAG_TEMPERATURE);
                JSONObject jSONObject6 = jSONObject5.getJSONObject(TAG_MAXIMUM_TEMPERATURE);
                JSONObject jSONObject7 = jSONObject5.getJSONObject(TAG_MINIMUM_TEMPERATURE);
                Double dValueOf2 = Double.valueOf(Double.parseDouble(jSONObject6.getString(TAG_TEMPERATURE_VALUE)));
                Double dValueOf3 = Double.valueOf(Double.parseDouble(jSONObject7.getString(TAG_TEMPERATURE_VALUE)));
                Integer numValueOf2 = Integer.valueOf(Integer.parseInt(jSONObject4.getJSONObject(TAG_DAY).getString(TAG_ICON)));
                Integer numValueOf3 = Integer.valueOf(Integer.parseInt(jSONObject4.getJSONObject(TAG_NIGHT).getString(TAG_ICON)));
                ForecastCondition forecastCondition = new ForecastCondition();
                forecastCondition.setForecastDay(time2);
                forecastCondition.setForecastUrl(string3);
                if (boolValueOf.booleanValue()) {
                    forecastCondition.getDaytime().setHighTemperature(fahrenheit2Celsius(dValueOf2.intValue()));
                    forecastCondition.getDaytime().setLowTemperature(fahrenheit2Celsius(dValueOf3.intValue()));
                    forecastCondition.getDaytime().setWeatherCondition(numValueOf2.intValue());
                } else {
                    forecastCondition.getNighttime().setHighTemperature(fahrenheit2Celsius(dValueOf2.intValue()));
                    forecastCondition.getNighttime().setLowTemperature(fahrenheit2Celsius(dValueOf3.intValue()));
                    forecastCondition.getNighttime().setWeatherCondition(numValueOf3.intValue());
                }
                weatherSet.addForecastCondition(forecastCondition);
            }
            JSONObject jSONObject8 = jSONObject3.getJSONObject(TAG_TIMEZONE);
            weatherSet.setTimezoneName(jSONObject8.getString(TAG_TIMEZONE_NAME));
            weatherSet.setDaylightSavingFlag(Boolean.valueOf(Boolean.parseBoolean(jSONObject8.getString(TAG_DAYLIGHT_SAVING))).booleanValue());
        } catch (Exception unused) {
            Log.i(TAG, "could not get WeatherSet");
        }
        return weatherSet;
    }

    private int getTimeOffset(String str) {
        String strSubstring = str.substring(str.length() - 6, str.length());
        int i = ((Integer.parseInt(strSubstring.substring(1, strSubstring.indexOf(CalendarConstants.COLON))) * 60) + Integer.parseInt(strSubstring.substring(strSubstring.indexOf(CalendarConstants.COLON) + 1))) * 60 * 1000;
        return strSubstring.charAt(0) == '-' ? i * (-1) : i;
    }

    private String getCurrentConditionUrlByLocation(Context context, Location location) {
        Objects.requireNonNull(location, "location must not be null");
        String str = "" + location.getLatitude();
        String str2 = "" + location.getLongitude();
        StringBuilder sb = new StringBuilder();
        sb.append(RequestBuilder.buildLocationRequestUrl(context, location.getLatitude(), location.getLongitude(), false).toString());
        if (PARTNER != null) {
            sb.append("&").append(PARTNER);
        }
        return sb.toString();
    }

    private String getCurrentConditionUrlByCityId(Context context, String str) {
        Objects.requireNonNull(str, "cityId must not be null");
        StringBuilder sb = new StringBuilder();
        sb.append(RequestBuilder.buildCurrentRequestUrl(context, str, false).toString());
        if (PARTNER != null) {
            sb.append("&").append(PARTNER);
        }
        return sb.toString();
    }

    private String getForecastUrlByCityId(Context context, String str) {
        Objects.requireNonNull(str, "cityId must not be null");
        StringBuilder sb = new StringBuilder();
        sb.append(RequestBuilder.buildForecastRequestUrl(context, str, false).toString());
        if (PARTNER != null) {
            sb.append("&").append(PARTNER);
        }
        return sb.toString();
    }

    private String getLocationDetailsByCityId(Context context, String str) {
        if (str == null) {
            throw new IllegalArgumentException("cityId must not be null");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(RequestBuilder.buildLocationDetailsRequestUrlFromId(context, str, false).toString());
        if (PARTNER != null) {
            sb.append("&").append(PARTNER);
        }
        return sb.toString();
    }

    private static Time formatTime(String str) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            Time time = new SafeTime();
            time.set(date.getTime());
            time.normalize(false);
            return time;
        } catch (ParseException e) {
            Log.e(TAG, "Time parse exception", e);
            return null;
        }
    }

    private int fahrenheit2Celsius(int i) {
        return ((i - 32) * 5) / 9;
    }

    @Override // com.sonymobile.accuweather.AccuWeatherBroker
    public boolean isWeatherDataInitialized(WeatherSet weatherSet) {
        return (weatherSet.getWeatherForecastConditions() == null || weatherSet.getWeatherForecastConditions().isEmpty()) ? false : true;
    }
}
