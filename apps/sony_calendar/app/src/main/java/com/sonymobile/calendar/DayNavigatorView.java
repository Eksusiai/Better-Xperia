package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.text.format.Time;
import android.util.AttributeSet;
import androidx.appcompat.app.AppCompatActivity;
import com.sonymobile.calendar.tablet.TabletDayNavigatorDayContainer;
import com.sonymobile.calendar.utils.PermissionUtils;

/* JADX INFO: loaded from: classes2.dex */
public class DayNavigatorView extends WeekNavigatorView implements Navigator {
    private boolean goToNext;
    private boolean goToPrevious;

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return null;
    }

    @Override // com.sonymobile.calendar.WeekNavigatorView, com.sonymobile.calendar.CalendarViewBase
    protected int getDayCount() {
        return 7;
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public void swipeTo(int i) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
    }

    public DayNavigatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.goToNext = false;
        this.goToPrevious = false;
    }

    @Override // com.sonymobile.calendar.WeekNavigatorView, com.sonymobile.calendar.CalendarViewBase
    protected Time[] getDays(Time time) {
        return WeekUtils.getDays(getContext(), time, true);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public void setCalendarFragment(ICalendarFragment iCalendarFragment) {
        this.calendarFragment = iCalendarFragment;
        this.swipeView.setNavigator(this);
        this.columnsLayout.setCalendarFragment(iCalendarFragment);
        for (ICalendarColumnContainer iCalendarColumnContainer : this.columnContainers) {
            ((DayNavigatorDayContainer) iCalendarColumnContainer).setNavigator(iCalendarFragment);
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        this.calendarFragment.goTo(time, z);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
        if (Utils.isWeekClearOfEpochUpperLimit(this.displayedDate.year, this.displayedDate.month, this.displayedDate.monthDay)) {
            this.goToNext = true;
            this.displayedDate = getNextViewTime(this.displayedDate, true);
            this.calendarFragment.goTo(this.displayedDate, true);
            return;
        }
        this.swipeView.animateBack();
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
        this.goToPrevious = true;
        this.displayedDate = getNextViewTime(this.displayedDate, false);
        this.calendarFragment.goTo(this.displayedDate, true);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public void goToDate(Time time, boolean z) {
        if (this.goToNext) {
            this.columnContainers[2].updateView(getDays(time), false);
            this.displayedDate = getNextViewTime(time, false);
            super.goToNext();
            this.goToNext = false;
            return;
        }
        if (this.goToPrevious) {
            this.columnContainers[0].updateView(getDays(time), false);
            this.displayedDate = getNextViewTime(time, true);
            super.goToPrevious();
            this.goToPrevious = false;
            return;
        }
        super.goToDate(time, false);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public Time goToNext() {
        if (!((DayNavigatorDayContainer) this.columnContainers[1]).selectNextDay(true)) {
            ((DayNavigatorDayContainer) this.columnContainers[2]).selectNextDay(true);
            this.displayedDate.monthDay -= 6;
            this.displayedDate.normalize(false);
            super.goToNext();
        } else {
            this.displayedDate.monthDay++;
            this.displayedDate.normalize(false);
        }
        return this.displayedDate;
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public Time goToPrevious() {
        if (!((DayNavigatorDayContainer) this.columnContainers[1]).selectNextDay(false)) {
            ((DayNavigatorDayContainer) this.columnContainers[0]).selectNextDay(false);
            this.displayedDate.monthDay += 6;
            this.displayedDate.normalize(false);
            super.goToPrevious();
        } else {
            this.displayedDate.monthDay--;
            this.displayedDate.normalize(false);
        }
        return this.displayedDate;
    }

    @Override // com.sonymobile.calendar.WeekNavigatorView, com.sonymobile.calendar.CalendarViewBase
    protected void onSwipeCompleted(boolean z) {
        updateAllViews(this.displayedDate);
        super.onSwipeCompleted(z);
    }

    @Override // com.sonymobile.calendar.WeekNavigatorView, com.sonymobile.calendar.CalendarViewBase
    protected ICalendarColumnContainer getNewCalendarColumnContainer() {
        DayNavigatorDayContainer dayNavigatorDayContainer;
        if (getResources().getBoolean(R.bool.tablet_mode)) {
            dayNavigatorDayContainer = new TabletDayNavigatorDayContainer(getContext(), getDayCount(), isR2L(), this);
        } else {
            dayNavigatorDayContainer = new DayNavigatorDayContainer(getContext(), getDayCount(), isR2L(), this);
        }
        dayNavigatorDayContainer.setNavigator(this.calendarFragment);
        return dayNavigatorDayContainer;
    }

    private void updateAllViews(Time time) {
        this.displayedDate.set(time);
        Time nextViewTime = getNextViewTime(time, false);
        Time nextViewTime2 = getNextViewTime(time, true);
        this.columnContainers[1].updateView(getDays(time), true);
        this.columnContainers[0].updateView(getDays(nextViewTime), true);
        this.columnContainers[2].updateView(getDays(nextViewTime2), true);
    }

    @Override // com.sonymobile.calendar.WeekNavigatorView, com.sonymobile.calendar.FocusedViewNavigator
    public void goToNextView() {
        goToNext(0.0f);
    }

    @Override // com.sonymobile.calendar.WeekNavigatorView, com.sonymobile.calendar.FocusedViewNavigator
    public void goToPreviousView() {
        goToPrevious(0.0f);
    }

    @Override // com.sonymobile.calendar.WeekNavigatorView, android.widget.ScrollView, android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        PermissionUtils.reportFullyDrawnIfPermitted((AppCompatActivity) getContext());
    }
}
