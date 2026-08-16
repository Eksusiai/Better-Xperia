package com.linkedin.platform;

import android.util.Log;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class AccessToken implements Serializable {
    private static final String ACCESS_TOKEN_VALUE = "accessTokenValue";
    private static final String EXPIRES_ON = "expiresOn";
    private static final String TAG = "AccessToken";
    private final String accessTokenValue;
    private final long expiresOn;

    public static synchronized AccessToken buildAccessToken(String str) {
        if (str != null) {
            if (!"".equals(str)) {
                try {
                    return new AccessToken(new JSONObject(str));
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    public static synchronized AccessToken buildAccessToken(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        try {
            return new AccessToken(jSONObject);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    private AccessToken(JSONObject jSONObject) throws JSONException {
        this.accessTokenValue = jSONObject.getString(ACCESS_TOKEN_VALUE);
        this.expiresOn = jSONObject.getLong(EXPIRES_ON);
    }

    public AccessToken(String str, long j) {
        this.accessTokenValue = str;
        this.expiresOn = j;
    }

    public String getValue() {
        return this.accessTokenValue;
    }

    public long getExpiresOn() {
        return this.expiresOn;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > getExpiresOn();
    }

    public String toString() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(ACCESS_TOKEN_VALUE, this.accessTokenValue);
            jSONObject.put(EXPIRES_ON, this.expiresOn);
            return jSONObject.toString();
        } catch (JSONException unused) {
            return null;
        }
    }
}
