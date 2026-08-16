package com.sonymobile.calendar.utils;

import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class ProductUtil {
    private static final String DEVICE_NAME_PROPERTY = "ro.semc.product.device";
    private static final String PREFIX_NEW_NAME = "Z";
    private static final String TAG = "ProductUtil";
    private static final String TARGET_DEVICE_YODO_NAME = "ZD0";
    private static volatile Method mGetMethod;
    private static volatile Class<?> mSystemProperties;

    public static boolean isNoDeleteAllEvents() {
        String systemProperty = getSystemProperty(DEVICE_NAME_PROPERTY, "");
        if (systemProperty.length() == 2) {
            systemProperty = "Z" + systemProperty;
        }
        return systemProperty.compareTo(TARGET_DEVICE_YODO_NAME) >= 0;
    }

    private static Class<?> getSystemPropertiesClass() throws ClassNotFoundException {
        if (mSystemProperties == null) {
            mSystemProperties = Class.forName("android.os.SystemProperties");
        }
        return mSystemProperties;
    }

    private static Method getMethod() throws Exception {
        if (mGetMethod == null) {
            mGetMethod = getSystemPropertiesClass().getDeclaredMethod("get", String.class);
        }
        return mGetMethod;
    }

    private static String getSystemProperty(String str, String str2) {
        try {
            String str3 = (String) getMethod().invoke(null, str);
            return !TextUtils.isEmpty(str3) ? str3 : str2;
        } catch (Exception unused) {
            Log.d(TAG, "Unable to read system properties");
            return str2;
        }
    }
}
