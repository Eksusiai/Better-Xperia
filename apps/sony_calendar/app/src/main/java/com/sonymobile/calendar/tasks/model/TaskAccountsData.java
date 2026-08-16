package com.sonymobile.calendar.tasks.model;

import android.content.Context;
import com.sonymobile.calendar.tasks.settings.TaskAccountManager;

/* JADX INFO: loaded from: classes2.dex */
public class TaskAccountsData {
    public static boolean hasOnlyLocalAccount(Context context) {
        return TaskAccountManager.getInstance().getAccountLists(context).size() <= 1;
    }
}
