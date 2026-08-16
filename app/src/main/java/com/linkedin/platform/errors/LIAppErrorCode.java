package com.linkedin.platform.errors;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public enum LIAppErrorCode {
    NONE("none"),
    INVALID_REQUEST("Invalid request"),
    NETWORK_UNAVAILABLE("Unavailable network connection"),
    USER_CANCELLED("User canceled action"),
    UNKNOWN_ERROR("Unknown or not defined error"),
    SERVER_ERROR("Server side error"),
    LINKEDIN_APP_NOT_FOUND("LinkedIn application not found"),
    NOT_AUTHENTICATED("User is not authenticated in LinkedIn app");

    private static Map<String, LIAppErrorCode> liAuthErrorCodeHashMap = buildMap();
    private String description;

    private static Map<String, LIAppErrorCode> buildMap() {
        HashMap map = new HashMap();
        for (LIAppErrorCode lIAppErrorCode : values()) {
            map.put(lIAppErrorCode.name(), lIAppErrorCode);
        }
        return map;
    }

    LIAppErrorCode(String str) {
        this.description = str;
    }

    public String getDescription() {
        return this.description;
    }

    public static LIAppErrorCode findErrorCode(String str) {
        LIAppErrorCode lIAppErrorCode = liAuthErrorCodeHashMap.get(str);
        return lIAppErrorCode == null ? UNKNOWN_ERROR : lIAppErrorCode;
    }
}
