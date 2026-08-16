package com.sonymobile.calendar;

import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.util.Log;
import com.sonyericsson.calendar.util.CalendarConstants;

/* JADX INFO: loaded from: classes2.dex */
public class PermissionsHelper {
    private static final String PERMISION_HELPER_TAG = "PermissionsHelper";

    public static String getPermissionMessage(String str, PackageManager packageManager) {
        StringBuilder sb = new StringBuilder();
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(str, 128);
            sb.append(System.lineSeparator()).append(permissionInfo.loadLabel(packageManager)).append(CalendarConstants.COLON).append(System.lineSeparator()).append(permissionInfo.loadDescription(packageManager)).append(System.lineSeparator());
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            Log.e(PERMISION_HELPER_TAG, e.toString(), e);
        }
        return sb.toString();
    }
}
