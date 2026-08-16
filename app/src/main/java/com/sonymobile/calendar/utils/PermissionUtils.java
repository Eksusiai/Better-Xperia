package com.sonymobile.calendar.utils;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.CustomizeConfig;
import com.sonyericsson.calendar.util.EmailIntentUtil;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.permissions.PermissionItem;

/* JADX INFO: loaded from: classes2.dex */
public class PermissionUtils {
    private static final String ACCESS_EMAIL_PROVIDER_PERMISSION = "PACKAGE_NAME_SENTINEL.permission.ACCESS_PROVIDER";
    private static final String PACKAGE_NAME_SENTINEL = "PACKAGE_NAME_SENTINEL";
    public static final int REQUEST_CODE_NONE = 0;
    private static final String SEND_SOMC_EMAIL_PERMISSION = "com.sonymobile.email.intent.permission.SEND_CALENDAR_EMAIL";

    public static boolean isReadContactsGranted(Context context) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CONTACTS") == 0) {
            return !CustomizeConfig.isShowingPermissionAcceptanceDialogEnabled(context) || Utils.getSharedPreference(context, GeneralPreferences.KEY_READ_CONTACTS, false);
        }
        return false;
    }

    public static PermissionItem getEssentialCalendarPermissionItem(Context context) {
        return new PermissionItem("android.permission.WRITE_CALENDAR", context.getString(R.string.calendar_permision_group_title), context.getString(R.string.calendar_permision_group_description), R.drawable.ic_calendar_event, true);
    }

    public static boolean isAccessLocationGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_COARSE_LOCATION") == 0;
    }

    public static boolean isCalendarGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.READ_CALENDAR") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.WRITE_CALENDAR") == 0;
    }

    public static boolean isSendSomcEmailGranted(Context context) {
        ComponentName availableEmailComponent = EmailIntentUtil.getAvailableEmailComponent(context);
        if (availableEmailComponent == null) {
            return false;
        }
        return ContextCompat.checkSelfPermission(context, SEND_SOMC_EMAIL_PERMISSION) == 0 && ContextCompat.checkSelfPermission(context, ACCESS_EMAIL_PROVIDER_PERMISSION.replace(PACKAGE_NAME_SENTINEL, availableEmailComponent.getPackageName())) == 0;
    }

    public static void reportFullyDrawnIfPermitted(AppCompatActivity appCompatActivity) {
        if (ContextCompat.checkSelfPermission(appCompatActivity, "android.permission.UPDATE_DEVICE_STATS") == 0) {
            appCompatActivity.reportFullyDrawn();
        } else {
            Log.i(".reportFullyDrawn(): ", "android.permission.UPDATE_DEVICE_STATS not granted!");
        }
    }
}
