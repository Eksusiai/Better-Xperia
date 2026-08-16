package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes2.dex */
public class AllDayWeekView extends AllDayViewBase {
    public AllDayWeekView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.sonymobile.calendar.AllDayViewBase, com.sonymobile.calendar.CalendarViewBase
    protected int getDayCount() {
        return WeekUtils.getDayCount(getContext());
    }

    @Override // com.sonymobile.calendar.AllDayViewBase, com.sonymobile.calendar.CalendarViewBase
    protected Time[] getDays(Time time) {
        return WeekUtils.getDays(getContext(), time, false);
    }

    @Override // com.sonymobile.calendar.AllDayViewBase, com.sonymobile.calendar.CalendarViewBase
    protected Time getNextViewTime(Time time, boolean z) {
        return WeekUtils.getNextViewTime(time, z);
    }
}
