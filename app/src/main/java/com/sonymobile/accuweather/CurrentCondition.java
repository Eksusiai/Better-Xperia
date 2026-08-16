package com.sonymobile.accuweather;
import com.sonymobile.calendar.SafeTime;

import android.text.format.Time;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CurrentCondition {
    private static final String KEY_FORECAST_DAY = "key_forecast_day";
    private static final String KEY_IS_DAYTIME = "key_is_daytime";
    private static final String KEY_TEMPERATURE = "key_temperature";
    private static final String KEY_WEATHER_TEXT = "key_weather_text";
    private static final String KEY_WEATHER_TYPE = "key_weather_type";
    private static final String TAG = "CurrentCondition";
    private boolean mDaytime;
    private Time mForecastDay;
    private int mTemperature;
    private String mWeatherText;
    private int mWeatherType;

    public void setDaytime(boolean z) {
        this.mDaytime = z;
    }

    public boolean isDaytime() {
        return this.mDaytime;
    }

    public int getWeatherType() {
        return this.mWeatherType;
    }

    public void setWeatherType(int i) {
        this.mWeatherType = i;
    }

    public int getTemperature() {
        return this.mTemperature;
    }

    public void setTemperature(int i) {
        this.mTemperature = i;
    }

    public void setForecastDay(Time time) {
        this.mForecastDay = time;
    }

    public Time getForecastDay() {
        return this.mForecastDay;
    }

    public String getWeatherText() {
        return this.mWeatherText;
    }

    public void setWeatherText(String str) {
        this.mWeatherText = str;
    }

    public JSONObject toJSONObject() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(KEY_IS_DAYTIME, this.mDaytime);
            jSONObject.put(KEY_TEMPERATURE, this.mTemperature);
            jSONObject.put(KEY_WEATHER_TYPE, this.mWeatherType);
            jSONObject.put(KEY_FORECAST_DAY, this.mForecastDay.toMillis(false));
            jSONObject.put(KEY_WEATHER_TEXT, this.mWeatherText);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
        return jSONObject;
    }

    public static CurrentCondition fromCachedString(String str) {
        CurrentCondition currentCondition = new CurrentCondition();
        try {
            JSONObject jSONObject = new JSONObject(str);
            currentCondition.setDaytime(jSONObject.getBoolean(KEY_IS_DAYTIME));
            currentCondition.setTemperature(jSONObject.getInt(KEY_TEMPERATURE));
            currentCondition.setWeatherType(jSONObject.getInt(KEY_WEATHER_TYPE));
            currentCondition.setWeatherText(jSONObject.getString(KEY_WEATHER_TEXT));
            Time time = new SafeTime();
            time.set(jSONObject.getLong(KEY_FORECAST_DAY));
            currentCondition.setForecastDay(time);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
        return currentCondition;
    }

    public int hashCode() {
        Time time = this.mForecastDay;
        return (((((((time == null ? 0 : time.hashCode()) + 31) * 31) + (this.mDaytime ? 1231 : 1237)) * 31) + this.mTemperature) * 31) + this.mWeatherType;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CurrentCondition currentCondition = (CurrentCondition) obj;
        Time time = this.mForecastDay;
        if (time == null) {
            if (currentCondition.mForecastDay != null) {
                return false;
            }
        } else if (!Utils.areDatesEqual(time, currentCondition.mForecastDay)) {
            return false;
        }
        return this.mDaytime == currentCondition.mDaytime && this.mTemperature == currentCondition.mTemperature && this.mWeatherType == currentCondition.mWeatherType;
    }
}
