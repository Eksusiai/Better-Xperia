package com.sonyericsson.calendar.util;

import android.content.Context;
import android.text.format.Time;
import android.util.Pair;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.PermissionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public class EventLoaderService {
    private boolean hideDeclined;
    private static EventMap eventCache = new EventMap();
    private static EventMap allDayEventCache = new EventMap();
    private static String usedTimezone = "";
    private static EventLoaderService instance = null;
    private ArrayList<DaySpan> mDaySpanCache = new ArrayList<>();
    private Map<DaySpan, Queue<IAsyncServiceResultHandler>> handlers = new HashMap();

    private EventLoaderService() {
    }

    public static EventLoaderService getInstance() {
        if (instance == null) {
            instance = new EventLoaderService();
        }
        return instance;
    }

    public void setHideDeclinedEvents(boolean z) {
        if (z != this.hideDeclined) {
            clearEventCache();
            this.hideDeclined = z;
        }
    }

    private void clearEventCache() {
        eventCache.clear();
    }

    public void requestLoad(Context context, Time time, Time time2, IAsyncServiceResultHandler iAsyncServiceResultHandler, boolean z) {
        DaySpan daySpan = new DaySpan(context, time, time2);
        String timeZone = Utils.getTimeZone(context, null);
        if (!timeZone.equals(usedTimezone)) {
            setUsedTimezone(timeZone);
            clearEventCache();
            z = true;
        }
        iAsyncServiceResultHandler.onResult(null, 0);
        if (z || !hasEventsForTimeSpan(daySpan)) {
            if (!isLoadingDaySpan(daySpan, iAsyncServiceResultHandler)) {
                LinkedList linkedList = new LinkedList();
                linkedList.add(iAsyncServiceResultHandler);
                this.handlers.put(daySpan, linkedList);
            }
            if (PermissionUtils.isCalendarGranted(context)) {
                new EventLoader(context).requestEvents(daySpan, new EventLoaderResultHandler(), this.hideDeclined);
            } else {
                this.mDaySpanCache.add(daySpan);
            }
        }
    }

    public void start(Context context) {
        Iterator<DaySpan> it = this.mDaySpanCache.iterator();
        while (it.hasNext()) {
            new EventLoader(context).requestEvents(it.next(), new EventLoaderResultHandler(), this.hideDeclined);
        }
    }

    private boolean isLoadingDaySpan(DaySpan daySpan, IAsyncServiceResultHandler iAsyncServiceResultHandler) {
        for (Map.Entry<DaySpan, Queue<IAsyncServiceResultHandler>> entry : this.handlers.entrySet()) {
            if (daySpan.contains(entry.getKey())) {
                entry.getValue().add(iAsyncServiceResultHandler);
                return true;
            }
        }
        return false;
    }

    public ArrayList<EventInfo> getEvents(int i) {
        ArrayList<EventInfo> arrayList = eventCache.get(Integer.valueOf(i));
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        return (ArrayList) arrayList.clone();
    }

    public ArrayList<EventInfo> getAllDayEvents(int i) {
        ArrayList<EventInfo> arrayList = allDayEventCache.get(Integer.valueOf(i));
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        return new ArrayList<>(new HashSet(arrayList));
    }

    private boolean hasEventsForTimeSpan(DaySpan daySpan) {
        for (int i = daySpan.startJulianDay; i <= daySpan.endJulianDay; i++) {
            if (eventCache.get(Integer.valueOf(i)) == null || allDayEventCache.get(Integer.valueOf(i)) == null) {
                return false;
            }
        }
        return true;
    }

    private class EventLoaderResultHandler implements IAsyncServiceResultHandler {
        private EventLoaderResultHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            Pair pair = (Pair) obj;
            EventLoaderService.eventCache.putAll((Map) pair.first);
            EventLoaderService.allDayEventCache.putAll((Map) pair.second);
            Queue queue = (Queue) EventLoaderService.this.handlers.remove(obj2);
            if (queue != null) {
                while (!queue.isEmpty()) {
                    ((IAsyncServiceResultHandler) queue.remove()).onResult(null, 0);
                }
            }
        }
    }

    public void stop() {
        setInstance(null);
    }

    public static void setUsedTimezone(String str) {
        usedTimezone = str;
    }

    public static void setInstance(EventLoaderService eventLoaderService) {
        instance = eventLoaderService;
    }
}
