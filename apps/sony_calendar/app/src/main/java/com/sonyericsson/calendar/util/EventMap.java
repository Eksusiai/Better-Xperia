package com.sonyericsson.calendar.util;

import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class EventMap extends HashMap<Integer, ArrayList<EventInfo>> {
    private static final long serialVersionUID = 1;

    public static ArrayList<EventInfo> getNewEventList() {
        return new ArrayList<>();
    }
}
