package com.sonymobile.calendar.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.EventLoaderService;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class EventUtils {
    public static final String ORGANIZER_CLASS = "com.sonyericsson.organizer.Organizer";
    public static final String ORGANIZER_PACKAGE = "com.sonyericsson.organizer";
    public static final String START_ALARM = "start_alarm";

    public static ArrayList<EventInfo> getEventsForDay(int i) {
        return EventLoaderService.getInstance().getEvents(i);
    }

    public static ArrayList<EventInfo> getAllDayEventsForDay(int i) {
        return EventLoaderService.getInstance().getAllDayEvents(i);
    }

    public static void startAlarmApp(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(ORGANIZER_PACKAGE, ORGANIZER_CLASS));
        intent.setFlags(268435456);
        context.startActivity(intent);
    }
}
