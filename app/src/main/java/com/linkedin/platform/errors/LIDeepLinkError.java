package com.linkedin.platform.errors;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class LIDeepLinkError {
    private static final String TAG = "com.linkedin.platform.errors.LIDeepLinkError";
    private LIAppErrorCode errorCode;
    private String errorMsg;

    public LIDeepLinkError(String str, String str2) {
        this.errorCode = LIAppErrorCode.findErrorCode(str);
        this.errorMsg = str2;
    }

    public LIDeepLinkError(LIAppErrorCode lIAppErrorCode, String str) {
        this.errorCode = lIAppErrorCode;
        this.errorMsg = str;
    }

    public String toString() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(ApiErrorResponse.ERROR_CODE, this.errorCode.name());
            jSONObject.put("errorMessage", this.errorMsg);
            return jSONObject.toString(2);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }
}
