package com.sonymobile.calendar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class MonthView extends CalendarViewBase implements OnDateBoxSelectedListener, FocusedViewNavigator {
    private static final int BOX_COUNT = 42;
    private boolean isTransitioning;
    private Time mAuxiliaryTime;

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected int getDayCount() {
        return 0;
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected float getLeftPanelWidth() {
        return 0.0f;
    }

    public MonthView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isTransitioning = false;
        this.mAuxiliaryTime = null;
        setFillViewport(true);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public void setCalendarFragment(ICalendarFragment iCalendarFragment) {
        super.setCalendarFragment(iCalendarFragment);
        for (ICalendarColumnContainer iCalendarColumnContainer : this.columnContainers) {
            if (iCalendarColumnContainer != null) {
                ((DateBoxGrid) iCalendarColumnContainer).setOnDateBoxSelectedListener(this);
            }
        }
    }

    @Override // android.widget.ScrollView, android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.calendarFragment.drawingCompleted();
    }

    public void forceRefresh(int i) {
        this.mAuxiliaryTime = this.displayedDate;
        ((DateBoxGrid) this.columnContainers[i]).invalidateGrid();
    }

    public void invalidateGrid(int i) {
        if (Utils.areDatesEqual(this.mAuxiliaryTime, this.displayedDate)) {
            return;
        }
        forceRefresh(i);
    }

    public void resetAuxiliaryTime() {
        this.mAuxiliaryTime = Utils.PRE_EPOCH;
    }

    public void clearOldSelections() {
        ((DateBoxGrid) this.columnContainers[0]).clearSelection();
        ((DateBoxGrid) this.columnContainers[2]).clearSelection();
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public void goToDate(Time time, boolean z) {
        super.goToDate(time, z);
        if (z) {
            return;
        }
        this.calendarFragment.onTransitionComplete();
    }

    @Override // com.sonymobile.calendar.OnDateBoxSelectedListener
    public void onDateSelected(DateBoxView dateBoxView, boolean z) {
        if (!z || this.calendarFragment == null) {
            return;
        }
        this.calendarFragment.onCalendarClicked(dateBoxView.getDate(), false);
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.isTransitioning) {
            ((MonthAgendaTransitionView) findViewById(R.id.transition_view)).layout(i, i2, i3, i4);
        }
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return shouldIgnoreForDrawer(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    public void prepareTransition(Time time, int i, int i2) {
        DateBoxGrid dateBoxGrid = (DateBoxGrid) this.columnContainers[1];
        dateBoxGrid.setViewPortSize(getMeasuredWidth(), i, false);
        MonthAgendaTransitionView monthAgendaTransitionView = (MonthAgendaTransitionView) findViewById(R.id.transition_view);
        monthAgendaTransitionView.prepareTransition(time, i, i2, getMeasuredWidth(), dateBoxGrid);
        monthAgendaTransitionView.setVisibility(0);
        this.swipeView.setVisibility(8);
        this.isTransitioning = true;
    }

    public void onTransitionCompleted() {
        MonthAgendaTransitionView monthAgendaTransitionView = (MonthAgendaTransitionView) findViewById(R.id.transition_view);
        monthAgendaTransitionView.onTransitionCompleted();
        monthAgendaTransitionView.setVisibility(8);
        this.swipeView.setVisibility(0);
        this.isTransitioning = false;
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected Time[] getDays(Time time) {
        int i;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar.setTimeInMillis(time.toMillis(false));
        calendar.set(5, 1);
        Time time2 = new SafeTime(time.timezone);
        time2.set(calendar.getTimeInMillis());
        int firstDayOfWeek = Utils.getFirstDayOfWeek(getContext());
        if (firstDayOfWeek == 0) {
            i = time2.weekDay;
        } else if (firstDayOfWeek == 6) {
            i = (time2.weekDay + 1) % 7;
        } else {
            i = (time2.weekDay + 6) % 7;
        }
        calendar.add(6, -i);
        Time[] timeArr = new Time[42];
        timeArr[0] = new SafeTime(time.timezone);
        timeArr[0].set(calendar.getTimeInMillis());
        for (int i2 = 1; i2 < 42; i2++) {
            Time time3 = new SafeTime(timeArr[i2 - 1]);
            time3.monthDay++;
            time3.normalize(true);
            timeArr[i2] = time3;
        }
        return timeArr;
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected Time getNextViewTime(Time time, boolean z) {
        Time time2 = new SafeTime(time);
        if (z) {
            time2.month++;
            time2.monthDay = 1;
        } else {
            int i = time2.month - 1;
            time2.month = i;
            if (i < 0) {
                time2.month += 12;
                time2.year--;
            }
            time2.monthDay = time2.getActualMaximum(4);
        }
        time2.normalize(true);
        return (Time.isEpoch(time) && Time.isEpoch(time2)) ? time : time2;
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void scrollToSelectedPosition(boolean z) {
        if (getContext().getResources().getConfiguration().orientation == 2) {
            int positionOfSelection = ((DateBoxGrid) this.columnContainers[1]).getPositionOfSelection();
            if (!z) {
                scrollTo(0, positionOfSelection);
                return;
            }
            ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(this, "scrollY", getScrollY(), positionOfSelection);
            objectAnimatorOfInt.setDuration(300L);
            objectAnimatorOfInt.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorOfInt.start();
        }
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void inflateLayout(LayoutInflater layoutInflater) {
        layoutInflater.inflate(R.layout.month_view, this);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected ICalendarColumnContainer getNewCalendarColumnContainer() {
        DateBoxGrid dateBoxGrid = new DateBoxGrid(getContext(), this, isR2L(), this);
        dateBoxGrid.setOnDateBoxSelectedListener(this);
        return dateBoxGrid;
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void onSwipeCompleted(boolean z) {
        this.calendarFragment.onTransitionComplete();
    }

    private boolean shouldIgnoreForDrawer(MotionEvent motionEvent) {
        if (isR2L()) {
            if (motionEvent.getX() > getWidth() * 0.98f) {
                return true;
            }
        } else if (motionEvent.getX() < getWidth() * 0.02f) {
            return true;
        }
        return false;
    }

    @Override // com.sonymobile.calendar.FocusedViewNavigator
    public void goToNextView() {
        this.calendarFragment.goToNext(0.0f);
    }

    @Override // com.sonymobile.calendar.FocusedViewNavigator
    public void goToPreviousView() {
        this.calendarFragment.goToPrevious(0.0f);
    }

    public int getCurrentDateBoxGridCellWidth() {
        return ((DateBoxGrid) this.columnContainers[1]).getCellWidth();
    }

    public String[] getCurrentDateBoxDayLabels() {
        return ((DateBoxGrid) this.columnContainers[1]).getDayLabels();
    }

    public int[] getCurrentDateBoxDayLabelColors() {
        return ((DateBoxGrid) this.columnContainers[1]).getDayLabelsColor();
    }
}
