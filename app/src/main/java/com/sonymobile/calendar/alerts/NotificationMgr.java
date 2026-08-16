package com.sonymobile.calendar.alerts;

/* JADX INFO: loaded from: classes2.dex */
public interface NotificationMgr {
    void cancel(int i);

    void cancel(String str, int i);

    void cancelAll();

    void notify(int i, AlertWork.NotificationWrapper notificationWrapper);

    void notify(String str, int i, AlertWork.NotificationWrapper notificationWrapper);
}
