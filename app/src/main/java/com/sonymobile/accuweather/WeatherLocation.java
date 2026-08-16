package com.sonymobile.accuweather;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class WeatherLocation implements Parcelable {
    public static final String KEY_CITY_COUNTRY = "key_city_country";
    public static final String KEY_CITY_ID = "key_city_id";
    public static final String KEY_CITY_NAME = "key_city_name";
    public static final String KEY_CITY_STATE = "key_city_state";
    public static final String KEY_POSITION_RECENT = "key_position_recent";
    private static final String TAG = "WeatherLocation";
    public String cityId;
    public String country;
    public String name;
    public int position;
    public StringBuilder sb = new StringBuilder();
    public String state;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WeatherLocation(String str, String str2, String str3) {
        this.name = str;
        this.state = str2;
        this.cityId = str3;
    }

    public WeatherLocation(String str, String str2, String str3, int i) {
        this.name = str;
        this.state = str2;
        this.cityId = str3;
        this.position = i;
    }

    public WeatherLocation(String str, String str2, String str3, String str4) {
        this.name = str;
        this.country = str2;
        this.state = str3;
        this.cityId = str4;
    }

    public static boolean isDefaultLocation(String str, String str2) {
        try {
            return isDefaultLocation(fromJSON(new JSONObject(str)), str2);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            return false;
        }
    }

    public static boolean isDefaultLocation(WeatherLocation weatherLocation, String str) {
        return weatherLocation != null && weatherLocation.name.equals(str);
    }

    public String toString() {
        if (!TextUtils.isEmpty(this.state)) {
            this.sb.setLength(0);
            return this.sb.append(this.name).append(" / ").append(this.state).toString();
        }
        return this.name;
    }

    public JSONObject getJSONObject() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(KEY_CITY_ID, this.cityId);
            jSONObject.put(KEY_CITY_NAME, this.name);
            Object obj = this.country;
            if (obj == null) {
                obj = JSONObject.NULL;
            }
            jSONObject.put(KEY_CITY_COUNTRY, obj);
            jSONObject.put(KEY_CITY_STATE, this.state);
            jSONObject.put(KEY_POSITION_RECENT, this.position);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
        return jSONObject;
    }

    public static WeatherLocation currentLocation(String str) {
        return new WeatherLocation(str, "", "");
    }

    public int hashCode() {
        String str = this.cityId;
        return 31 + (str == null ? 0 : str.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WeatherLocation weatherLocation = (WeatherLocation) obj;
        String str = this.cityId;
        if (str == null) {
            if (weatherLocation.cityId != null) {
                return false;
            }
        } else if (!str.equals(weatherLocation.cityId)) {
            return false;
        }
        return true;
    }

    public static WeatherLocation fromJSON(JSONObject jSONObject) {
        try {
            String string = jSONObject.getString(KEY_CITY_NAME);
            String string2 = jSONObject.getString(KEY_CITY_ID);
            try {
                jSONObject.getString(KEY_CITY_COUNTRY);
            } catch (JSONException unused) {
            }
            return new WeatherLocation(string, jSONObject.getString(KEY_CITY_STATE), string2, jSONObject.getInt(KEY_POSITION_RECENT));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            return null;
        }
    }

    public static WeatherLocation fromString(String str) {
        try {
            return fromJSON(new JSONObject(str));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            return null;
        }
    }

    public static WeatherLocation getCurrentLocation(String str) {
        try {
            return fromJSON(new JSONObject(str));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            return null;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.country);
        parcel.writeString(this.state);
        parcel.writeString(this.cityId);
        parcel.writeInt(this.position);
    }
}
