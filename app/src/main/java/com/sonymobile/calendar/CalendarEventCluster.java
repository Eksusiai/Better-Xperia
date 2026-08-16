package com.sonymobile.calendar;

import com.sonyericsson.calendar.util.EventInfo;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarEventCluster extends ArrayList<CalendarEventView> {
    private static final long serialVersionUID = 1;
    private int columnCount;

    public int getColumnCount() {
        return this.columnCount;
    }

    public EventInfo[] getEventInfos() {
        int size = size();
        EventInfo[] eventInfoArr = new EventInfo[size];
        for (int i = 0; i < size; i++) {
            eventInfoArr[i] = get(i).getEventInfo();
        }
        return eventInfoArr;
    }

    public void optimize(boolean z) {
        CalendarEventCluster calendarEventCluster = (CalendarEventCluster) clone();
        this.columnCount = 0;
        while (!calendarEventCluster.isEmpty()) {
            CalendarEventView calendarEventViewRemove = calendarEventCluster.remove(0);
            if (calendarEventViewRemove.getVisibility() != 8) {
                calendarEventViewRemove.setClusterColumnIndex(this.columnCount);
                long endTimeEventCardInMillis = calendarEventViewRemove.getEndTimeEventCardInMillis();
                int i = 0;
                while (i < calendarEventCluster.size()) {
                    if (calendarEventCluster.get(i).getVisibility() != 8 && endTimeEventCardInMillis <= calendarEventCluster.get(i).getLocalStartTimeMillis()) {
                        calendarEventCluster.get(i).setClusterColumnIndex(this.columnCount);
                        endTimeEventCardInMillis = calendarEventCluster.get(i).getEndTimeEventCardInMillis();
                        calendarEventCluster.remove(i);
                        i--;
                    }
                    i++;
                }
                this.columnCount++;
            }
        }
        if (z) {
            mirrorColumnOrder();
        }
    }

    private void mirrorColumnOrder() {
        for (CalendarEventView calendarEventView : this) {
            calendarEventView.setClusterColumnIndex((this.columnCount - 1) - calendarEventView.getClusterColumnIndex());
        }
    }
}
