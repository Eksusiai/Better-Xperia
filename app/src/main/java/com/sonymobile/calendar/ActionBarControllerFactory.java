package com.sonymobile.calendar;

import android.content.Context;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarControllerFactory {
    public static ActionBarControllerBase getController(Context context) {
        if (Utils.isTabletDevice(context)) {
            return new ActionBarControllerTablet();
        }
        return new ActionBarControllerPhone();
    }
}
