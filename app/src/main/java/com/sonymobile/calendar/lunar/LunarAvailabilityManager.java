package com.sonymobile.calendar.lunar;

import android.content.Context;
import android.content.pm.PackageManager;
import com.sonymobile.calendar.Utils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class LunarAvailabilityManager {
    private static final boolean DEBUG = false;
    private static final String LUNAR_PLUGIN_PACKAGE_NAME = "com.sonymobile.lunar";
    private static final String TAG = "LunarAvailabilityManager";
    private static final Map<Api, Boolean> sApis = new HashMap();

    public enum Api {
        LUNARCALENDAR_LIB("com.sonymobile.lunar.lib.LunarUtils"),
        SOLAR_LUNAR_DATEPICKER_DIALOG("com.sonymobile.lunar.lib.LunisolarDatePickerDialog");

        private final String mStringIdentifier;

        Api(String str) {
            this.mStringIdentifier = str;
        }

        public boolean checkForAvailability() {
            try {
                Class.forName(this.mStringIdentifier);
                return true;
            } catch (Exception unused) {
                return false;
            }
        }
    }

    private static boolean isApiAvailable(Api api) {
        Boolean bool = sApis.get(api);
        if (bool == null) {
            return doLookup(api);
        }
        return bool.booleanValue();
    }

    private static boolean doLookup(Api api) {
        boolean zCheckForAvailability = api.checkForAvailability();
        sApis.put(api, Boolean.valueOf(zCheckForAvailability));
        return zCheckForAvailability;
    }

    public static boolean isLunarPluginExist(Context context) {
        boolean z;
        boolean z2;
        PackageManager packageManager = context.getPackageManager();
        if (!Utils.isPackageListAccessEnable(context)) {
            return false;
        }
        try {
            packageManager.getApplicationInfo("com.sonymobile.lunar", 0);
            z = packageManager.getApplicationEnabledSetting("com.sonymobile.lunar") == 3 || packageManager.getApplicationEnabledSetting("com.sonymobile.lunar") == 2;
            z2 = true;
        } catch (PackageManager.NameNotFoundException unused) {
            z = false;
            z2 = false;
        }
        return z2 && !z;
    }

    public static boolean isLunarAvailable(Context context, Api api) {
        return isLunarAvailable(context) && isApiAvailable(api);
    }

    public static boolean isLunarAvailable(Context context) {
        if (context == null || !isLunarPluginExist(context)) {
            return false;
        }
        Locale locale = context.getResources().getConfiguration().locale;
        if (locale == null || !"zh".equalsIgnoreCase(locale.getLanguage())) {
            return false;
        }
        String strCountry = locale.getCountry();
        String strScript = locale.getScript();
        if (strCountry != null && (strCountry.equalsIgnoreCase("TW") || strCountry.equalsIgnoreCase("HK") || strCountry.equalsIgnoreCase("MO"))) {
            return false;
        }
        if (strScript != null && !strScript.isEmpty() && !strScript.equalsIgnoreCase("Hans")) {
            return false;
        }
        return true;
    }
}
