package com.sonymobile.calendar;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes2.dex */
public class HintPromoteUtils {
    private static final String KEY_SHOW_HINT_AND_PROMO_IN_LISTVIEW = "preferences_show_hint_and_promo";
    private static final int SHOW_HINT_PROMO_VIEW = 1;

    public static boolean shouldShowHintAndPromoInListView(Context context) {
        return GeneralPreferences.getSharedPreferences(context).getInt(KEY_SHOW_HINT_AND_PROMO_IN_LISTVIEW, 1) == 1;
    }

    public static void removeHintAndPromoteFromListView(Context context) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(context).edit();
        editorEdit.putInt(KEY_SHOW_HINT_AND_PROMO_IN_LISTVIEW, 0);
        editorEdit.commit();
    }
}
