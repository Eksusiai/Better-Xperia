package com.linkedin.platform.internals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes.dex */
public class AppStore {
    public static void goAppStore(final Activity activity, boolean z) {
        if (!z) {
            goToAppStore(activity);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.update_linkedin_app_message).setTitle(R.string.update_linkedin_app_title);
        builder.setPositiveButton(R.string.update_linkedin_app_download, new DialogInterface.OnClickListener() { // from class: com.linkedin.platform.internals.AppStore.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                AppStore.goToAppStore(activity);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.update_linkedin_app_cancel, new DialogInterface.OnClickListener() { // from class: com.linkedin.platform.internals.AppStore.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void goToAppStore(Activity activity) {
        try {
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(SupportedAppStore.fromDeviceManufacturer().getAppStoreUri())));
        } catch (ActivityNotFoundException unused) {
        }
    }

    private enum SupportedAppStore {
        amazonAppstore("amazon", "amzn://apps/android?p=com.linkedin.android"),
        googlePlay("google", "market://details?id=com.linkedin.android"),
        samsungApps("samsung", "samsungapps://ProductDetail/com.linkedin.android");

        private final String appStoreUri;
        private final String deviceManufacturer;

        SupportedAppStore(String str, String str2) {
            this.deviceManufacturer = str;
            this.appStoreUri = str2;
        }

        public String getDeviceManufacturer() {
            return this.deviceManufacturer;
        }

        public String getAppStoreUri() {
            return this.appStoreUri;
        }

        public static SupportedAppStore fromDeviceManufacturer() {
            for (SupportedAppStore supportedAppStore : values()) {
                if (supportedAppStore.getDeviceManufacturer().equalsIgnoreCase(Build.MANUFACTURER)) {
                    return supportedAppStore;
                }
            }
            return googlePlay;
        }
    }
}
