package com.linkedin.platform.errors;

import com.android.volley.VolleyError;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class LIApiError extends Exception {
    private ApiErrorResponse apiErrorResponse;
    private ErrorType errorType;
    private int httpStatusCode;
    private VolleyError volleyError;

    public enum ErrorType {
        accessTokenIsNotSet,
        apiErrorResponse,
        other
    }

    public static LIApiError buildLiApiError(VolleyError volleyError) {
        return new LIApiError(volleyError);
    }

    public LIApiError(String str, Throwable th) {
        this(ErrorType.other, str, th);
    }

    public LIApiError(ErrorType errorType, String str, Throwable th) {
        super(str, th);
        this.httpStatusCode = -1;
        this.errorType = errorType;
    }

    public LIApiError(VolleyError volleyError) {
        super(volleyError.getMessage(), volleyError.fillInStackTrace());
        this.httpStatusCode = -1;
        this.volleyError = volleyError;
        if (volleyError.networkResponse != null) {
            this.httpStatusCode = volleyError.networkResponse.statusCode;
            try {
                this.apiErrorResponse = ApiErrorResponse.build(volleyError.networkResponse.data);
                this.errorType = ErrorType.apiErrorResponse;
            } catch (JSONException unused) {
                this.errorType = ErrorType.other;
            }
        }
    }

    public ApiErrorResponse getApiErrorResponse() {
        return this.apiErrorResponse;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    @Override // java.lang.Throwable
    public String toString() {
        ApiErrorResponse apiErrorResponse = this.apiErrorResponse;
        return apiErrorResponse == null ? "exceptionMsg: " + super.getMessage() : apiErrorResponse.toString();
    }
}
