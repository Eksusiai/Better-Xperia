package com.sonymobile.calendar.tasks;

import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tasks.model.TaskAccount;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SyncManager {
    private static SyncManager instance;

    private SyncManager() {
    }

    public static SyncManager getInstance() {
        if (instance == null) {
            instance = new SyncManager();
        }
        return instance;
    }

    public void performSync(List<TaskAccount> list) {
        for (TaskAccount taskAccount : list) {
            if (!taskAccount.isLocal()) {
                Utils.scheduleSync(2, taskAccount.name, taskAccount.type, false, true);
            }
        }
    }
}
