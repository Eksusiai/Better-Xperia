package com.sonymobile.gagtmhelper;

/* JADX INFO: loaded from: classes2.dex */
public class GaGtmLog {
    private static volatile boolean mEnabled = false;

    private GaGtmLog() {
    }

    public static void enable(boolean z) {
        mEnabled = z;
    }

    public static boolean isEnabled() {
        return mEnabled;
    }
}
