package com.sonymobile.calendar.linkedin.model;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInMySelf {
    private static final String EMAIL_ADDRESS = "emailAddress";
    private String mEmail;

    public static LinkedInMySelf getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private LinkedInMySelf() {
        this.mEmail = "";
    }

    public String getEmail() {
        return this.mEmail;
    }

    public void setEmail(String str) {
        this.mEmail = str;
    }

    public void setEmail(JSONObject jSONObject) {
        try {
            if (jSONObject.has(EMAIL_ADDRESS)) {
                this.mEmail = jSONObject.getString(EMAIL_ADDRESS);
            }
        } catch (JSONException unused) {
        }
    }

    public boolean hasEmail() {
        return !TextUtils.isEmpty(this.mEmail);
    }

    private static class SingletonHolder {
        private static final LinkedInMySelf INSTANCE = new LinkedInMySelf();

        private SingletonHolder() {
        }
    }
}
