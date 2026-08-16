package com.linkedin.platform.errors;

import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class ApiErrorResponse {
    public static final String ERROR_CODE = "errorCode";
    public static final String MESSAGE = "message";
    public static final String REQUEST_ID = "requestId";
    public static final String STATUS = "status";
    private static final String TAG = "com.linkedin.platform.errors.ApiErrorResponse";
    public static final String TIMESTAMP = "timestamp";
    public final int errorCode;
    private JSONObject jsonApiErrorResponse;
    public final String message;
    public final String requestId;
    public final int status;
    public final long timestamp;

    private ApiErrorResponse(JSONObject jSONObject, int i, String str, String str2, int i2, long j) {
        this.jsonApiErrorResponse = jSONObject;
        this.errorCode = i;
        this.message = str;
        this.requestId = str2;
        this.status = i2;
        this.timestamp = j;
    }

    public static ApiErrorResponse build(byte[] bArr) throws JSONException {
        return build(new JSONObject(new String(bArr)));
    }

    public static ApiErrorResponse build(JSONObject jSONObject) throws JSONException {
        return new ApiErrorResponse(jSONObject, jSONObject.optInt(ERROR_CODE, -1), jSONObject.optString(MESSAGE), jSONObject.optString(REQUEST_ID), jSONObject.optInt("status", -1), jSONObject.optLong(TIMESTAMP, 0L));
    }

    public int getErrorCode() {
        return this.jsonApiErrorResponse.optInt(ERROR_CODE, -1);
    }

    public String getMessage() {
        return this.jsonApiErrorResponse.optString(MESSAGE);
    }

    public String getRequestId() {
        return this.jsonApiErrorResponse.optString(REQUEST_ID);
    }

    public int getStatus() {
        return this.jsonApiErrorResponse.optInt("status", -1);
    }

    public long getTimestamp() {
        return this.jsonApiErrorResponse.optLong(TIMESTAMP, 0L);
    }

    public String toString() {
        try {
            return this.jsonApiErrorResponse.toString(2);
        } catch (JSONException unused) {
            return null;
        }
    }
}
