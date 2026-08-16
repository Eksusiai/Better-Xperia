package com.sonymobile.accuweather;
import com.sonymobile.calendar.SafeTime;

import android.text.format.Time;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ForecastCondition {
    private static final String KEY_DAY_CONDITION = "key_day_condition";
    private static final String KEY_DAY_HIGH_TEMP = "key_day_high_temp";
    private static final String KEY_DAY_LOW_TEMP = "key_day_low_temp";
    private static final String KEY_FORECAST_DAY = "key_forecast_day";
    private static final String KEY_FORECAST_URL = "key_forecast_url";
    private static final String KEY_NIGHT_CONDITION = "key_night_condition";
    private static final String KEY_NIGHT_HIGH_TEMP = "key_night_high_temp";
    private static final String KEY_NIGHT_LOW_TEMP = "key_night_low_temp";
    private static final String TAG = "ForecastCondition";
    private Time forecastDay;
    private String forecastUrl;
    private Daytime mDaytime = new Daytime();
    private Nighttime mNighttime = new Nighttime();

    public Daytime getDaytime() {
        return this.mDaytime;
    }

    public Nighttime getNighttime() {
        return this.mNighttime;
    }

    public void setDaytime(Daytime daytime) {
        this.mDaytime = daytime;
    }

    public void setNighttime(Nighttime nighttime) {
        this.mNighttime = nighttime;
    }

    public Period getPeriod(boolean z) {
        if (z) {
            return this.mDaytime;
        }
        return this.mNighttime;
    }

    public Time getForecastDay() {
        return this.forecastDay;
    }

    public void setForecastDay(Time time) {
        this.forecastDay = time;
    }

    public void setForecastUrl(String str) {
        this.forecastUrl = str;
    }

    public String getForecastUrl() {
        return this.forecastUrl;
    }

    public JSONObject toJSONObject() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(KEY_DAY_CONDITION, this.mDaytime.getWeatherCondition());
            jSONObject.put(KEY_DAY_HIGH_TEMP, this.mDaytime.getHighTemperature());
            jSONObject.put(KEY_DAY_LOW_TEMP, this.mDaytime.getLowTemperature());
            jSONObject.put(KEY_NIGHT_CONDITION, this.mNighttime.getWeatherCondition());
            jSONObject.put(KEY_NIGHT_HIGH_TEMP, this.mNighttime.getHighTemperature());
            jSONObject.put(KEY_NIGHT_LOW_TEMP, this.mNighttime.getLowTemperature());
            jSONObject.put(KEY_FORECAST_DAY, this.forecastDay.toMillis(false));
            jSONObject.put(KEY_FORECAST_URL, this.forecastUrl);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
        return jSONObject;
    }

    public void fromCachedString(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            this.mDaytime.setWeatherCondition(jSONObject.getInt(KEY_DAY_CONDITION));
            this.mDaytime.setHighTemperature(jSONObject.getInt(KEY_DAY_HIGH_TEMP));
            this.mDaytime.setLowTemperature(jSONObject.getInt(KEY_DAY_LOW_TEMP));
            this.mNighttime.setWeatherCondition(jSONObject.getInt(KEY_NIGHT_CONDITION));
            this.mNighttime.setHighTemperature(jSONObject.getInt(KEY_NIGHT_HIGH_TEMP));
            this.mNighttime.setLowTemperature(jSONObject.getInt(KEY_NIGHT_LOW_TEMP));
            Time time = new SafeTime();
            this.forecastDay = time;
            time.set(jSONObject.getLong(KEY_FORECAST_DAY));
            this.forecastUrl = jSONObject.getString(KEY_FORECAST_URL);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
    }

    public class Period {
        private int mHighTemperature;
        private int mLowTemperature;
        private int mWeatherCondition;

        public Period() {
        }

        public int getHighTemperature() {
            return this.mHighTemperature;
        }

        public void setHighTemperature(int i) {
            this.mHighTemperature = i;
        }

        public int getLowTemperature() {
            return this.mLowTemperature;
        }

        public void setLowTemperature(int i) {
            this.mLowTemperature = i;
        }

        public void setWeatherCondition(int i) {
            this.mWeatherCondition = i;
        }

        public int getWeatherCondition() {
            return this.mWeatherCondition;
        }

        public int hashCode() {
            return ((((this.mHighTemperature + 31) * 31) + this.mLowTemperature) * 31) + this.mWeatherCondition;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Period period = (Period) obj;
            return this.mHighTemperature == period.mHighTemperature && this.mLowTemperature == period.mLowTemperature && this.mWeatherCondition == period.mWeatherCondition;
        }
    }

    public class Daytime extends Period {
        public Daytime() {
            super();
        }
    }

    public class Nighttime extends Period {
        public Nighttime() {
            super();
        }
    }

    public int hashCode() {
        Time time = this.forecastDay;
        int iHashCode = ((time == null ? 0 : time.hashCode()) + 31) * 31;
        String str = this.forecastUrl;
        int iHashCode2 = (iHashCode + (str == null ? 0 : str.hashCode())) * 31;
        Daytime daytime = this.mDaytime;
        int iHashCode3 = (iHashCode2 + (daytime == null ? 0 : daytime.hashCode())) * 31;
        Nighttime nighttime = this.mNighttime;
        return iHashCode3 + (nighttime != null ? nighttime.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ForecastCondition forecastCondition = (ForecastCondition) obj;
        Time time = this.forecastDay;
        if (time == null) {
            if (forecastCondition.forecastDay != null) {
                return false;
            }
        } else if (!Utils.areDatesEqual(time, forecastCondition.forecastDay)) {
            return false;
        }
        String str = this.forecastUrl;
        if (str == null) {
            if (forecastCondition.forecastUrl != null) {
                return false;
            }
        } else if (!str.equals(forecastCondition.forecastUrl)) {
            return false;
        }
        Daytime daytime = this.mDaytime;
        if (daytime == null) {
            if (forecastCondition.mDaytime != null) {
                return false;
            }
        } else if (!daytime.equals(forecastCondition.mDaytime)) {
            return false;
        }
        Nighttime nighttime = this.mNighttime;
        if (nighttime == null) {
            if (forecastCondition.mNighttime != null) {
                return false;
            }
        } else if (!nighttime.equals(forecastCondition.mNighttime)) {
            return false;
        }
        return true;
    }
}
