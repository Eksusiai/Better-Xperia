package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes2.dex */
public class MonthWeekView extends MonthView {
    public MonthWeekView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.sonymobile.calendar.MonthView, com.sonymobile.calendar.CalendarViewBase
    protected Time[] getDays(Time time) {
        return WeekUtils.getDays(getContext(), time, true);
    }

    @Override // com.sonymobile.calendar.MonthView, com.sonymobile.calendar.CalendarViewBase
    protected Time getNextViewTime(Time time, boolean z) {
        return WeekUtils.getNextViewTime(time, z);
    }
}
