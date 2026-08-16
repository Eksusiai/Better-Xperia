package com.sonymobile.calendar;

import android.app.Activity;
import android.content.Context;
import android.text.format.Time;
import android.widget.LinearLayout;
import com.sonyericsson.calendar.util.DaySpan;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.birthday.BirthdayService;
import com.sonymobile.calendar.weather.WeatherIcon;

/* JADX INFO: loaded from: classes2.dex */
public class AllDayColumnContainer extends LinearLayout implements ICalendarColumnContainer {
    private boolean blockRelayout;
    private AllDayColumnView[] dayColumns;
    private Time[] displayedDates;
    private CalendarEventNavigator eventNavigator;
    private boolean isR2L;
    private AllDayViewBase mAllDayViewBase;
    private boolean mIsExpanded;
    private boolean showFullEventText;
    private WeatherIcon weatherIcon;

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public boolean setHourHeight(int i) {
        return false;
    }

    public AllDayColumnContainer(Context context, int i, CalendarEventNavigator calendarEventNavigator, ICalendarColumnsLayout iCalendarColumnsLayout, boolean z, boolean z2, AllDayViewBase allDayViewBase) {
        super(context);
        this.blockRelayout = false;
        this.eventNavigator = calendarEventNavigator;
        this.isR2L = z;
        this.showFullEventText = z2;
        this.mAllDayViewBase = allDayViewBase;
        init(i, iCalendarColumnsLayout);
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void updateView(Time[] timeArr, boolean z) {
        this.displayedDates = (Time[]) timeArr.clone();
        EventLoaderService eventLoaderService = EventLoaderService.getInstance();
        Context context = getContext();
        Time[] timeArr2 = this.displayedDates;
        Time time = timeArr2[0];
        Time time2 = timeArr2[timeArr2.length - 1];
        eventLoaderService.requestLoad(context, time, time2, new EventLoaderResultHandler(), z);
        if (Utils.isReadContactsEnabled(getContext())) {
            BirthdayService birthdayService = BirthdayService.INSTANCE;
            Activity activity = (Activity) getContext();
            Time[] timeArr3 = this.displayedDates;
            birthdayService.requestBirthdays(activity, new DaySpan(timeArr3[0], timeArr3[timeArr3.length - 1]), new BirthdayResultHandler());
        }
        for (int i = 0; i < timeArr.length; i++) {
            FreeDayService.getInstance().requestHolidayName(getContext(), timeArr[i].year, timeArr[i].month, timeArr[i].monthDay, new FreeDayServiceResultHandler(), 1);
        }
        WeatherIcon weatherIcon = this.weatherIcon;
        if (weatherIcon != null) {
            weatherIcon.setWeatherInfo(timeArr[0], false);
        }
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void reloadEvents() {
        EventLoaderService eventLoaderService = EventLoaderService.getInstance();
        Context context = getContext();
        Time[] timeArr = this.displayedDates;
        eventLoaderService.requestLoad(context, timeArr[0], timeArr[timeArr.length - 1], new EventLoaderResultHandler(), true);
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void setViewPortSize(int i, int i2, boolean z) {
        float length = i / this.dayColumns.length;
        WeatherIcon weatherIcon = this.weatherIcon;
        float width = length - (weatherIcon == null ? 0 : weatherIcon.getWidth());
        float f = 0.0f;
        float f2 = 0.0f;
        for (AllDayColumnView allDayColumnView : this.dayColumns) {
            f += width;
            int iRound = Math.round(f - f2);
            allDayColumnView.setColumnWidth(iRound, z);
            f2 += iRound;
        }
        setLayoutParams(new LinearLayout.LayoutParams(i, -2));
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void blockRelayout() {
        this.blockRelayout = true;
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void updateFocusability(boolean z) {
        setDescendantFocusability(z ? 262144 : 393216);
        for (AllDayColumnView allDayColumnView : this.dayColumns) {
            allDayColumnView.updateFocusability(z);
        }
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void removeAddEventView() {
        for (AllDayColumnView allDayColumnView : this.dayColumns) {
            allDayColumnView.removeAddEventView();
        }
    }

    public AllDayColumnView addTempEvent(EventInfo eventInfo) {
        AllDayColumnView allDayColumnView = null;
        for (AllDayColumnView allDayColumnView2 : this.dayColumns) {
            if (allDayColumnView2.prepareForTempEvent(eventInfo)) {
                allDayColumnView2.addTempEvent(eventInfo);
                allDayColumnView = allDayColumnView2;
            }
        }
        return allDayColumnView;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.blockRelayout) {
            this.blockRelayout = false;
        } else {
            super.onLayout(z, i, i2, i3, i4);
        }
    }

    private boolean isDayView() {
        return this.dayColumns.length == 1;
    }

    private void init(int i, ICalendarColumnsLayout iCalendarColumnsLayout) {
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        this.dayColumns = new AllDayColumnView[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.dayColumns[i2] = new AllDayColumnView(getContext(), this.eventNavigator, iCalendarColumnsLayout, this.isR2L, this.showFullEventText, isDayView(), getViewIndex(i2, i), this);
            addView(this.dayColumns[i2]);
        }
        if (isDayView()) {
            this.weatherIcon = new WeatherIcon(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2, 0.0f);
            layoutParams.setMarginStart(Math.round(getResources().getDimension(R.dimen.weather_margin_day_view)));
            this.weatherIcon.setLayoutParams(layoutParams);
            addView(this.weatherIcon);
        }
    }

    public void expandAllDayColumns() {
        int i = 0;
        while (true) {
            AllDayColumnView[] allDayColumnViewArr = this.dayColumns;
            if (i < allDayColumnViewArr.length) {
                allDayColumnViewArr[i].expandAllDayEvents();
                i++;
            } else {
                this.mIsExpanded = true;
                updateMoreIcon();
                return;
            }
        }
    }

    public void showLessAllDayEvents() {
        int i = 0;
        while (true) {
            AllDayColumnView[] allDayColumnViewArr = this.dayColumns;
            if (i < allDayColumnViewArr.length) {
                allDayColumnViewArr[i].showLessAllDayEvents();
                i++;
            } else {
                this.mIsExpanded = false;
                updateMoreIcon();
                return;
            }
        }
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public void resetExpanded() {
        this.mIsExpanded = false;
    }

    public void updateMoreIcon() {
        this.mAllDayViewBase.updateMoreIcon();
    }

    public boolean shouldShowMoreIcon() {
        int integer = getResources().getInteger(R.integer.max_number_of_all_day_events_collapsed);
        int i = 0;
        while (true) {
            AllDayColumnView[] allDayColumnViewArr = this.dayColumns;
            if (i >= allDayColumnViewArr.length) {
                return false;
            }
            if (allDayColumnViewArr[i].numOfAllDayEvents() > integer) {
                return true;
            }
            i++;
        }
    }

    private ViewIndex getViewIndex(int i, int i2) {
        if (i == 0) {
            return ViewIndex.FIRST;
        }
        if (i == i2 - 1) {
            return ViewIndex.LAST;
        }
        return ViewIndex.MIDDLE;
    }

    private class EventLoaderResultHandler implements IAsyncServiceResultHandler {
        private EventLoaderResultHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            int iMin = Math.min(AllDayColumnContainer.this.dayColumns.length, AllDayColumnContainer.this.displayedDates.length);
            for (int i = 0; i < iMin; i++) {
                AllDayColumnContainer.this.dayColumns[i].update(AllDayColumnContainer.this.displayedDates[i]);
            }
            AllDayColumnContainer.this.updateMoreIcon();
        }
    }

    private class BirthdayResultHandler implements IAsyncServiceResultHandler {
        private BirthdayResultHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (obj != null) {
                int iMin = Math.min(AllDayColumnContainer.this.dayColumns.length, AllDayColumnContainer.this.displayedDates.length);
                for (int i = 0; i < iMin; i++) {
                    AllDayColumnContainer.this.dayColumns[i].update(AllDayColumnContainer.this.displayedDates[i]);
                }
                AllDayColumnContainer.this.updateMoreIcon();
            }
        }
    }

    private class FreeDayServiceResultHandler implements IAsyncServiceResultHandler {
        private FreeDayServiceResultHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() == 2 && obj != null && ((Boolean) obj).booleanValue()) {
                for (int i = 0; i < AllDayColumnContainer.this.dayColumns.length; i++) {
                    AllDayColumnContainer.this.dayColumns[i].update(AllDayColumnContainer.this.displayedDates[i]);
                }
                AllDayColumnContainer.this.updateMoreIcon();
            }
        }
    }
}
