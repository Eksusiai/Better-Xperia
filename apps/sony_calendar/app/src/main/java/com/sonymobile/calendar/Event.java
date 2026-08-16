package com.sonymobile.calendar;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class Event implements Comparable<Event>, Cloneable {
    private static final int PROJECTION_ALL_DAY_INDEX = 2;
    private static final int PROJECTION_BEGIN_INDEX = 6;
    private static final int PROJECTION_CALENDAR_ID = 19;
    private static final int PROJECTION_COLOR_INDEX = 3;
    private static final int PROJECTION_END_DAY_INDEX = 10;
    private static final int PROJECTION_END_INDEX = 7;
    private static final int PROJECTION_END_MINUTE_INDEX = 12;
    private static final int PROJECTION_EVENT_ID_INDEX = 5;
    private static final int PROJECTION_EVENT_STATUS = 20;
    private static final int PROJECTION_GUESTS_CAN_INVITE_OTHERS_INDEX = 18;
    private static final int PROJECTION_HAS_ALARM_INDEX = 13;
    private static final int PROJECTION_LOCATION_INDEX = 1;
    private static final int PROJECTION_ORGANIZER_INDEX = 17;
    private static final int PROJECTION_RDATE_INDEX = 15;
    private static final int PROJECTION_RRULE_INDEX = 14;
    private static final int PROJECTION_SELF_ATTENDEE_STATUS_INDEX = 16;
    private static final int PROJECTION_START_DAY_INDEX = 9;
    private static final int PROJECTION_START_MINUTE_INDEX = 11;
    private static final int PROJECTION_TIMEZONE_INDEX = 4;
    private static final int PROJECTION_TITLE_INDEX = 0;
    public boolean allDay;
    public int color;
    public int endDay;
    public long endMillis;
    public int endTime;
    public boolean guestsCanModify;
    public boolean hasAlarm;
    public boolean isRepeating;
    public CharSequence location;
    private int mColumn;
    private int mMaxColumns;
    public String organizer;
    public int selfAttendeeStatus;
    public int startDay;
    public long startMillis;
    public int startTime;
    public CharSequence title;

    public static int findFirstZeroBit(long j) {
        for (int i = 0; i < 64; i++) {
            if (((1 << i) & j) == 0) {
                return i;
            }
        }
        return 64;
    }

    public final Object clone() throws CloneNotSupportedException {
        super.clone();
        Event event = new Event();
        event.title = this.title;
        event.color = this.color;
        event.location = this.location;
        event.allDay = this.allDay;
        event.startDay = this.startDay;
        event.endDay = this.endDay;
        event.startTime = this.startTime;
        event.endTime = this.endTime;
        event.startMillis = this.startMillis;
        event.endMillis = this.endMillis;
        event.hasAlarm = this.hasAlarm;
        event.isRepeating = this.isRepeating;
        event.selfAttendeeStatus = this.selfAttendeeStatus;
        event.organizer = this.organizer;
        event.guestsCanModify = this.guestsCanModify;
        return event;
    }

    public static final Event newInstance() {
        Event event = new Event();
        event.title = null;
        event.color = 0;
        event.location = null;
        event.allDay = false;
        event.startDay = 0;
        event.endDay = 0;
        event.startTime = 0;
        event.endTime = 0;
        event.startMillis = 0L;
        event.endMillis = 0L;
        event.hasAlarm = false;
        event.isRepeating = false;
        event.selfAttendeeStatus = 0;
        return event;
    }

    @Override // java.lang.Comparable
    public final int compareTo(Event event) {
        int i;
        int i2;
        int i3 = this.startDay;
        int i4 = event.startDay;
        if (i3 < i4) {
            return -1;
        }
        if (i3 > i4) {
            return 1;
        }
        int i5 = this.startTime;
        int i6 = event.startTime;
        if (i5 < i6) {
            return -1;
        }
        if (i5 > i6 || (i = this.endDay) < (i2 = event.endDay)) {
            return 1;
        }
        if (i > i2) {
            return -1;
        }
        int i7 = this.endTime;
        int i8 = event.endTime;
        if (i7 < i8) {
            return 1;
        }
        if (i7 > i8) {
            return -1;
        }
        boolean z = this.allDay;
        if (z && !event.allDay) {
            return -1;
        }
        if (!z && event.allDay) {
            return 1;
        }
        boolean z2 = this.guestsCanModify;
        if (z2 && !event.guestsCanModify) {
            return -1;
        }
        if (!z2 && event.guestsCanModify) {
            return 1;
        }
        int iCompareStrings = compareStrings(this.title, event.title);
        if (iCompareStrings != 0) {
            return iCompareStrings;
        }
        int iCompareStrings2 = compareStrings(this.location, event.location);
        if (iCompareStrings2 != 0) {
            return iCompareStrings2;
        }
        int iCompareStrings3 = compareStrings(this.organizer, event.organizer);
        if (iCompareStrings3 != 0) {
            return iCompareStrings3;
        }
        return 0;
    }

    public boolean equals(Object obj) {
        return obj != null && (obj instanceof Event) && compareTo((Event) obj) == 0;
    }

    public int hashCode() {
        return (int) this.startMillis;
    }

    private int compareStrings(CharSequence charSequence, CharSequence charSequence2) {
        return (charSequence != null ? charSequence.toString() : "").compareTo(charSequence2 != null ? charSequence2.toString() : "");
    }

    public static void computePositions(ArrayList<Event> arrayList, boolean z) {
        if (arrayList == null) {
            return;
        }
        doComputePositions(arrayList, false, z);
        doComputePositions(arrayList, true, z);
    }

    private static void doComputePositions(ArrayList<Event> arrayList, boolean z, boolean z2) {
        boolean z3;
        boolean z4;
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        int i = 0;
        long j = 0;
        for (Event event : arrayList) {
            boolean z5 = event.allDay;
            if (z2) {
                z3 = false;
            } else {
                boolean zIsEventSpanOver24Hour = Utils.isEventSpanOver24Hour(event);
                z3 = zIsEventSpanOver24Hour;
                z5 = event.allDay || zIsEventSpanOver24Hour;
            }
            if (z5 == z) {
                long startMillis = event.getStartMillis();
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    Event event2 = (Event) it.next();
                    boolean z6 = event2.getEndMillis() <= startMillis;
                    if (z3) {
                        z4 = z3;
                        z6 = event2.getEndMillis() + (((long) (1440 - event2.endTime)) * 60000) <= startMillis;
                    } else {
                        z4 = z3;
                    }
                    if (z6) {
                        long j2 = (~(1 << event2.getColumn())) & j;
                        it.remove();
                        j = j2;
                    }
                    z3 = z4;
                }
                if (arrayList2.isEmpty()) {
                    Iterator it2 = arrayList3.iterator();
                    while (it2.hasNext()) {
                        ((Event) it2.next()).setMaxColumns(i);
                    }
                    arrayList3.clear();
                    i = 0;
                    j = 0;
                }
                int iFindFirstZeroBit = findFirstZeroBit(j);
                if (iFindFirstZeroBit == 64) {
                    iFindFirstZeroBit = 63;
                }
                j |= 1 << iFindFirstZeroBit;
                event.setColumn(iFindFirstZeroBit);
                arrayList2.add(event);
                arrayList3.add(event);
                int size = arrayList2.size();
                if (i < size) {
                    i = size;
                }
            }
        }
        Iterator it3 = arrayList3.iterator();
        while (it3.hasNext()) {
            ((Event) it3.next()).setMaxColumns(i);
        }
    }

    public String getTitleAndLocation() {
        String string = this.title.toString();
        CharSequence charSequence = this.location;
        if (charSequence == null) {
            return string;
        }
        String string2 = charSequence.toString();
        return !string.endsWith(string2) ? string + ", " + string2 : string;
    }

    public void setColumn(int i) {
        this.mColumn = i;
    }

    public int getColumn() {
        return this.mColumn;
    }

    public void setMaxColumns(int i) {
        this.mMaxColumns = i;
    }

    public int getMaxColumns() {
        return this.mMaxColumns;
    }

    public void setStartMillis(long j) {
        this.startMillis = j;
    }

    public long getStartMillis() {
        return this.startMillis;
    }

    public void setEndMillis(long j) {
        this.endMillis = j;
    }

    public long getEndMillis() {
        return this.endMillis;
    }

    private static void addInToEventsList(Context context, Cursor cursor, int i, int i2, ArrayList<Event> arrayList) {
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            if (cursor.getInt(20) != 2) {
                Event event = new Event();
                event.title = cursor.getString(0);
                event.location = cursor.getString(1);
                event.allDay = cursor.getInt(2) != 0;
                event.organizer = cursor.getString(17);
                event.guestsCanModify = cursor.getInt(18) != 0;
                CharSequence charSequence = event.title;
                if (charSequence == null || charSequence.length() == 0) {
                    event.title = context.getResources().getString(R.string.no_title_label);
                }
                if (!cursor.isNull(3)) {
                    event.color = cursor.getInt(3);
                } else {
                    event.color = ContextCompat.getColor(context, R.color.event_center);
                }
                long j = cursor.getLong(6);
                long j2 = cursor.getLong(7);
                event.startMillis = j;
                event.startTime = cursor.getInt(11);
                event.startDay = cursor.getInt(9);
                event.endMillis = j2;
                event.endTime = cursor.getInt(12);
                int i3 = cursor.getInt(10);
                event.endDay = i3;
                if (event.startDay <= i2 && i3 >= i) {
                    event.hasAlarm = cursor.getInt(13) != 0;
                    String string = cursor.getString(14);
                    String string2 = cursor.getString(15);
                    if (!TextUtils.isEmpty(string) || !TextUtils.isEmpty(string2)) {
                        event.isRepeating = true;
                    } else {
                        event.isRepeating = false;
                    }
                    event.selfAttendeeStatus = cursor.getInt(16);
                    arrayList.add(event);
                }
            }
        }
    }

    private static void sortEventList(ArrayList<Event> arrayList) {
        Collections.sort(arrayList, new Comparator<Event>() { // from class: com.sonymobile.calendar.Event.1
            @Override // java.util.Comparator
            public int compare(Event event, Event event2) {
                int i = (int) (event.startMillis - event2.startMillis);
                if (i != 0) {
                    return i;
                }
                int i2 = (int) (event2.endMillis - event.endMillis);
                if (i2 != 0) {
                    return i2;
                }
                if (event.title == null || event2.title == null) {
                    return 0;
                }
                return ((String) event.title).compareTo((String) event2.title);
            }
        });
    }
}
