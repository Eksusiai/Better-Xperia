package com.sonymobile.calendar.utils;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.provider.Settings;

/* JADX INFO: loaded from: classes2.dex */
public class PackageUtils {
    private static final String DIALER_DEFAULT_APPLICATION = "dialer_default_application";
    private static final String DIALER_PACKAGE_NAME = "com.sonymobile.android.dialer";
    private static final String PHONEBOOK_CLASS_NAME = "com.sonyericsson.android.socialphonebook.activities.PhoneActivity";
    private static final String PHONEBOOK_PACKAGE_NAME = "com.sonyericsson.android.socialphonebook";
    public static final String SKETCH_PACKAGE_NAME = "com.sonymobile.sketch";
    private static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
    private static final String SMS_PACKAGE_NAME = "com.sonyericsson.conversations";

    public enum AppType {
        DIALER,
        SMS,
        SKETCH
    }

    /* JADX INFO: renamed from: com.sonymobile.calendar.utils.PackageUtils$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$calendar$utils$PackageUtils$AppType;

        static {
            int[] iArr = new int[AppType.values().length];
            $SwitchMap$com$sonymobile$calendar$utils$PackageUtils$AppType = iArr;
            try {
                iArr[AppType.DIALER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$utils$PackageUtils$AppType[AppType.SMS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$utils$PackageUtils$AppType[AppType.SKETCH.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public static Drawable resolveAppIcon(Context context, AppType appType) {
        Intent intentResolveAppIntent;
        PackageManager packageManager;
        ResolveInfo resolveInfoResolveActivity;
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$calendar$utils$PackageUtils$AppType[appType.ordinal()];
        if (i == 1) {
            intentResolveAppIntent = resolveAppIntent(context, DIALER_PACKAGE_NAME, DIALER_DEFAULT_APPLICATION);
            if (intentResolveAppIntent == null) {
                intentResolveAppIntent = new Intent();
                intentResolveAppIntent.setComponent(new ComponentName(PHONEBOOK_PACKAGE_NAME, PHONEBOOK_CLASS_NAME));
            }
        } else if (i == 2) {
            intentResolveAppIntent = resolveAppIntent(context, SMS_PACKAGE_NAME, SMS_DEFAULT_APPLICATION);
        } else {
            intentResolveAppIntent = i != 3 ? null : resolveAppIntent(context, SKETCH_PACKAGE_NAME, SKETCH_PACKAGE_NAME);
        }
        if (intentResolveAppIntent == null || (resolveInfoResolveActivity = (packageManager = context.getPackageManager()).resolveActivity(intentResolveAppIntent, 0)) == null) {
            return null;
        }
        return resolveInfoResolveActivity.loadIcon(packageManager);
    }

    private static Intent resolveAppIntent(Context context, String str, String str2) {
        ContentResolver contentResolver = context.getContentResolver();
        PackageManager packageManager = context.getPackageManager();
        String string = Settings.Secure.getString(contentResolver, str2);
        if (string != null) {
            str = string;
        }
        return packageManager.getLaunchIntentForPackage(str);
    }
}
