package com.linkedin.platform.internals;

import android.content.Context;
import android.content.pm.PackageManager;

/* JADX INFO: loaded from: classes.dex */
public class LIAppVersion {
    public static final String LI_APP_PACKAGE_NAME = "com.linkedin.android";

    public static boolean isLIAppCurrent(Context context) {
        return isLIAppCurrent(context, "com.linkedin.android");
    }

    private static boolean isLIAppCurrent(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 1).versionCode >= 161;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
