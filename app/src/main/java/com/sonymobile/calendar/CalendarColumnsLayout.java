package com.sonymobile.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarColumnsLayout extends LinearLayout implements ICalendarColumnsLayout {
    private ICalendarFragment calendarFragment;
    private IDayColumnView columnWithAddEvent;
    private CalendarColumnsSizeHandler columnsReadyHandler;

    public CalendarColumnsLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.columnWithAddEvent = null;
    }

    public void setCalendarColumnsReadyHandler(CalendarColumnsSizeHandler calendarColumnsSizeHandler) {
        this.columnsReadyHandler = calendarColumnsSizeHandler;
    }

    public void setCalendarFragment(ICalendarFragment iCalendarFragment) {
        this.calendarFragment = iCalendarFragment;
    }

    @Override // com.sonymobile.calendar.ICalendarColumnsLayout
    public void removeAddEventView() {
        IDayColumnView iDayColumnView = this.columnWithAddEvent;
        if (iDayColumnView != null) {
            iDayColumnView.removeAddEventView();
        }
    }

    @Override // com.sonymobile.calendar.ICalendarColumnsLayout
    public void onDayClicked(IDayColumnView iDayColumnView) {
        ICalendarFragment iCalendarFragment = this.calendarFragment;
        if (iCalendarFragment != null) {
            iCalendarFragment.onCalendarClicked(iDayColumnView.getDate(), false);
        }
        this.columnWithAddEvent = iDayColumnView;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        CalendarColumnsSizeHandler calendarColumnsSizeHandler = this.columnsReadyHandler;
        if (calendarColumnsSizeHandler == null || i == i3) {
            return;
        }
        calendarColumnsSizeHandler.onWidthChanged();
    }
}
