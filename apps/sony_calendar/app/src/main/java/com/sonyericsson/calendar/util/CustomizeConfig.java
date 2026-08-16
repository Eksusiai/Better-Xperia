package com.sonyericsson.calendar.util;

import android.content.Context;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes.dex */
public class CustomizeConfig {
    private static CustomizeConfig mConfig = new CustomizeConfig();

    public boolean getShowDataUsage(Context context) {
        if (context == null || context.getResources() == null) {
            return false;
        }
        return context.getResources().getBoolean(R.bool.config_showDataUsageWarning);
    }

    public static CustomizeConfig getInstance() {
        return mConfig;
    }

    public static boolean isShowingPermissionAcceptanceDialogEnabled(Context context) {
        if (context == null) {
            return false;
        }
        return context.getResources().getBoolean(R.bool.enable_showing_permission_acceptance_dialog);
    }
}
