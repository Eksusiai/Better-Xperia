package com.sonymobile.accuweather;

import android.text.format.Time;
import android.util.Log;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class WeatherSet {
    private static final String KEY_CURRENT_CONDITION = "key_current_condition";
    private static final String KEY_CURRENT_GMT_OFFSET = "key_current_gmt_offset";
    private static final String KEY_FORECAST_CONDITIONS = "key_forecast_conditions";
    private static final String KEY_FORECAST_URL = "key_forecast_url";
    private static final String KEY_LOCATION_NAME = "key_location_name";
    private static final String KEY_LOCATION_STATE = "key_location_state";
    private static final String TAG = "WeatherSet";
    private double mCurrentGmtOffset;
    private String mExtendedForecastUrl;
    private boolean mIsDaylightSaving;
    private String mLocationName;
    private String mLocationState;
    private String mTimezoneName;
    private CurrentCondition myCurrentCondition = null;
    private ArrayList<ForecastCondition> myForecastConditions = new ArrayList<>();

    public String getExtendedForecastUrl() {
        return this.mExtendedForecastUrl;
    }

    public void setExtendedForecastUrl(String str) {
        this.mExtendedForecastUrl = str;
    }

    public CurrentCondition getWeatherCurrentCondition() {
        return this.myCurrentCondition;
    }

    public void setWeatherCurrentCondition(CurrentCondition currentCondition) {
        this.myCurrentCondition = currentCondition;
    }

    public ArrayList<ForecastCondition> getWeatherForecastConditions() {
        return this.myForecastConditions;
    }

    public void addForecastCondition(ForecastCondition forecastCondition) {
        this.myForecastConditions.add(forecastCondition);
    }

    public void setDaylightSavingFlag(boolean z) {
        this.mIsDaylightSaving = z;
    }

    public boolean isDaylightSaving() {
        return this.mIsDaylightSaving;
    }

    public void setTimezoneName(String str) {
        this.mTimezoneName = str;
    }

    public String getTimezoneName() {
        return this.mTimezoneName;
    }

    private ForecastCondition getWeatherForeCastConditionForDate(Time time) {
        for (ForecastCondition forecastCondition : this.myForecastConditions) {
            if (Utils.areDatesEqual(forecastCondition.getForecastDay(), time)) {
                return forecastCondition;
            }
        }
        return null;
    }

    public Object getWeatherInfoForDate(Time time) {
        if (Utils.areDatesEqual(this.myCurrentCondition.getForecastDay(), time)) {
            return this.myCurrentCondition;
        }
        return getWeatherForeCastConditionForDate(time);
    }

    public ForecastCondition getLastWeatherForecastCondition() {
        ArrayList<ForecastCondition> arrayList = this.myForecastConditions;
        return arrayList.get(arrayList.size() - 1);
    }

    public String getCurrentLocation() {
        return this.mLocationName;
    }

    public void setCurrentLocation(String str) {
        this.mLocationName = str;
    }

    public String gerCurrentLocationState() {
        return this.mLocationState;
    }

    public void setCurrentLocationState(String str) {
        this.mLocationState = str;
    }

    public double getCurrentGmtOffset() {
        return this.mCurrentGmtOffset;
    }

    public void setCurrentGmtOffset(double d) {
        this.mCurrentGmtOffset = d;
    }

    public JSONObject toJSONObject(int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(KEY_CURRENT_CONDITION, this.myCurrentCondition.toJSONObject());
            jSONObject.put(KEY_LOCATION_NAME, this.mLocationName);
            jSONObject.put(KEY_LOCATION_STATE, this.mLocationState);
            jSONObject.put(KEY_FORECAST_CONDITIONS, storeForecastConditions(i));
            jSONObject.put(KEY_CURRENT_GMT_OFFSET, this.mCurrentGmtOffset);
            jSONObject.put(KEY_FORECAST_URL, this.mExtendedForecastUrl);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
        return jSONObject;
    }

    public void fromCachedString(String str, int i) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            setCurrentLocationState(jSONObject.getString(KEY_LOCATION_STATE));
            setCurrentLocation(jSONObject.getString(KEY_LOCATION_NAME));
            setWeatherCurrentCondition(CurrentCondition.fromCachedString(jSONObject.getString(KEY_CURRENT_CONDITION)));
            this.myForecastConditions = restoreForecastConditions(jSONObject.getString(KEY_FORECAST_CONDITIONS), i);
            setWeatherCurrentCondition(CurrentCondition.fromCachedString(jSONObject.getString(KEY_CURRENT_CONDITION)));
            this.myForecastConditions = restoreForecastConditions(jSONObject.getString(KEY_FORECAST_CONDITIONS), i);
            setCurrentGmtOffset(jSONObject.getDouble(KEY_CURRENT_GMT_OFFSET));
            setExtendedForecastUrl(jSONObject.getString(KEY_FORECAST_URL));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
    }

    private JSONObject storeForecastConditions(int i) {
        JSONObject jSONObject = new JSONObject();
        for (int i2 = 0; i2 < i; i2++) {
            try {
                jSONObject.put("" + i2, this.myForecastConditions.get(i2).toJSONObject().toString());
            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
            }
        }
        return jSONObject;
    }

    private ArrayList<ForecastCondition> restoreForecastConditions(String str, int i) {
        ArrayList<ForecastCondition> arrayList = new ArrayList<>();
        for (int i2 = 0; i2 < i; i2++) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                ForecastCondition forecastCondition = new ForecastCondition();
                forecastCondition.fromCachedString(jSONObject.getString("" + i2));
                arrayList.add(forecastCondition);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
            }
        }
        return arrayList;
    }

    public boolean isNightTime(long j) {
        if (j < 0) {
            return false;
        }
        int hourOfDay = Utils.getHourOfDay(j, getCurrentGmtOffset());
        return hourOfDay >= 21 || hourOfDay <= 5;
    }
}
