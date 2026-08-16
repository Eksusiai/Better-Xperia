package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes2.dex */
public class DayView extends CalendarGridViewBase {
    @Override // com.sonymobile.calendar.CalendarGridViewBase, com.sonymobile.calendar.CalendarViewBase
    protected int getDayCount() {
        return 1;
    }

    @Override // com.sonymobile.calendar.CalendarGridViewBase, com.sonymobile.calendar.CalendarViewBase
    protected Time[] getDays(Time time) {
        return new Time[]{time};
    }

    public DayView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.sonymobile.calendar.CalendarGridViewBase, com.sonymobile.calendar.CalendarViewBase
    protected Time getNextViewTime(Time time, boolean z) {
        Time time2 = new SafeTime(time);
        time2.monthDay += z ? 1 : -1;
        time2.normalize(false);
        return time2;
    }
}
