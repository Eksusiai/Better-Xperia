package com.linkedin.platform.listeners;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ApiResponse {
    private static final String DATA = "responseData";
    private static final String LOCATION = "Location";
    private static final String STATUS_CODE = "StatusCode";
    private static final String TAG = "ApiResponse";
    private final String locationHeader;
    private final String responseData;
    private final int statusCode;

    public static synchronized ApiResponse buildApiResponse(JSONObject jSONObject) {
        try {
            return new ApiResponse(jSONObject.optInt(STATUS_CODE), jSONObject.getString(DATA), jSONObject.optString("Location"));
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    public ApiResponse(int i, String str, String str2) {
        this.statusCode = i;
        this.responseData = str;
        this.locationHeader = str2;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getResponseDataAsString() {
        return this.responseData;
    }

    public JSONObject getResponseDataAsJson() {
        String str = this.responseData;
        if (str != null && !"".equals(str)) {
            try {
                return new JSONObject(this.responseData);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage(), e);
            }
        }
        return null;
    }

    public String getLocationHeader() {
        return this.locationHeader;
    }

    public String toString() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(STATUS_CODE, this.statusCode);
            jSONObject.put(DATA, this.responseData);
            jSONObject.put("Location", this.locationHeader);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return jSONObject.toString();
    }
}
