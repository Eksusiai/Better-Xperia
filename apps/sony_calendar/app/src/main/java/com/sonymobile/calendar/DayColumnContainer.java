package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import android.widget.LinearLayout;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class DayColumnContainer extends LinearLayout implements ICalendarColumnContainer {
    private boolean blockRelayout;
    private DayColumnView[] dayColumns;
    private Time[] displayedDates;
    private CalendarEventNavigator eventNavigator;
    private boolean isR2L;

    public DayColumnContainer(Context context, int i, CalendarEventNavigator calendarEventNavigator, ICalendarColumnsLayout iCalendarColumnsLayout, boolean z, boolean z2) {
        super(context);
        this.blockRelayout = false;
        this.eventNavigator = calendarEventNavigator;
        this.isR2L = z;
        init(i, iCalendarColumnsLayout, z2);
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void updateView(Time[] timeArr, boolean z) {
        this.displayedDates = (Time[]) timeArr.clone();
        EventLoaderService eventLoaderService = EventLoaderService.getInstance();
        Context context = getContext();
        Time[] timeArr2 = this.displayedDates;
        eventLoaderService.requestLoad(context, timeArr2[0], timeArr2[timeArr2.length - 1], new EventLoaderResultHandler(), z);
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void reloadEvents() {
        EventLoaderService eventLoaderService = EventLoaderService.getInstance();
        Context context = getContext();
        Time[] timeArr = this.displayedDates;
        eventLoaderService.requestLoad(context, timeArr[0], timeArr[timeArr.length - 1], new EventLoaderResultHandler(), true);
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public boolean setHourHeight(int i) {
        for (DayColumnView dayColumnView : this.dayColumns) {
            if (!dayColumnView.setHourHeight(i)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void setViewPortSize(int i, int i2, boolean z) {
        int iDetermineMinHeight = determineMinHeight(i2);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.hour_height);
        DayColumnView[] dayColumnViewArr = this.dayColumns;
        float length = i / dayColumnViewArr.length;
        float f = 0.0f;
        int i3 = 0;
        for (DayColumnView dayColumnView : dayColumnViewArr) {
            f += length;
            int iRound = Math.round(f) - i3;
            dayColumnView.setColumnWidth(iRound);
            dayColumnView.setHourHeightLimits(iDetermineMinHeight, dimensionPixelSize);
            i3 += iRound;
        }
    }

    private int determineMinHeight(int i) {
        return Utils.isInLandscapeMode(getContext()) ? i / 9 : i / 16;
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void blockRelayout() {
        this.blockRelayout = true;
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void updateFocusability(boolean z) {
        setDescendantFocusability(z ? 262144 : 393216);
        for (DayColumnView dayColumnView : this.dayColumns) {
            dayColumnView.updateFocusability(z);
        }
    }

    @Override // android.view.View, com.sonymobile.calendar.ICalendarColumnContainer
    public void invalidate() {
        for (DayColumnView dayColumnView : this.dayColumns) {
            dayColumnView.invalidate();
        }
    }

    public int getHourHeight() {
        return this.dayColumns[0].getHourHeight();
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void removeAddEventView() {
        for (DayColumnView dayColumnView : this.dayColumns) {
            dayColumnView.removeAddEventView();
        }
    }

    public DayColumnView addTempEvent(EventInfo eventInfo) {
        DayColumnView dayColumnView = null;
        for (DayColumnView dayColumnView2 : this.dayColumns) {
            if (dayColumnView2.prepareForTempEvent(eventInfo)) {
                dayColumnView2.addTempEvent(eventInfo);
                dayColumnView = dayColumnView2;
            }
        }
        return dayColumnView;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.blockRelayout) {
            this.blockRelayout = false;
        } else {
            super.onLayout(z, i, i2, i3, i4);
        }
    }

    private void init(int i, ICalendarColumnsLayout iCalendarColumnsLayout, boolean z) {
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        this.dayColumns = new DayColumnView[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.dayColumns[i2] = new DayColumnView(getContext(), this.eventNavigator, iCalendarColumnsLayout, this.isR2L, i == 1, z);
            addView(this.dayColumns[i2]);
        }
    }

    private class EventLoaderResultHandler implements IAsyncServiceResultHandler {
        private EventLoaderResultHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            int iMin = Math.min(DayColumnContainer.this.dayColumns.length, DayColumnContainer.this.displayedDates.length);
            for (int i = 0; i < iMin; i++) {
                DayColumnContainer.this.dayColumns[i].update(DayColumnContainer.this.displayedDates[i]);
            }
        }
    }

    public DayColumnView[] getDayColumns() {
        DayColumnView[] dayColumnViewArr = this.dayColumns;
        return (DayColumnView[]) Arrays.copyOf(dayColumnViewArr, dayColumnViewArr.length);
    }
}
