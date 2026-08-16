package com.android.ex.chips;

import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class ChipsUtil {
    public static boolean supportsChipsUi() {
        return Build.VERSION.SDK_INT >= 14;
    }
}
