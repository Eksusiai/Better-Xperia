package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;

/* JADX INFO: loaded from: classes2.dex */
public class DayNavigatorDayContainer extends WeekNavigatorDayContainer implements NavigatorDaySelectedListener {
    private Navigator navigator;

    public DayNavigatorDayContainer(Context context, int i, boolean z, FocusedViewNavigator focusedViewNavigator) {
        super(context, i, z, focusedViewNavigator, false);
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public boolean selectNextDay(boolean z) {
        this.mAlpha = 0;
        this.mTimeStep = System.nanoTime();
        Thread thread = new Thread(new WeekNavigatorDayContainer.ViewInvalidator());
        thread.setPriority(1);
        thread.start();
        if (this.mSelectedIndex != -1) {
            ((DayNavigatorDayView) this.mDayBoxes[this.mSelectedIndex]).setIsSelected(false, false);
            this.mWasSelectedIndex = this.mSelectedIndex;
        } else if (!z) {
            this.mSelectedIndex = this.mDayBoxes.length;
        }
        this.mSelectedIndex += z ? 1 : -1;
        if (this.mSelectedIndex < 0 || this.mSelectedIndex >= this.mDayBoxes.length) {
            this.mSelectedIndex = -1;
            this.mWasSelectedIndex = -1;
            invalidate();
            return false;
        }
        ((DayNavigatorDayView) this.mDayBoxes[this.mSelectedIndex]).setIsSelected(true, false);
        invalidate();
        return true;
    }

    @Override // com.sonymobile.calendar.NavigatorDaySelectedListener
    public void onDaySelected(Time time, int i, boolean z) {
        Navigator navigator;
        if (i == this.mSelectedIndex) {
            return;
        }
        this.mAlpha = 0;
        this.mTimeStep = System.nanoTime();
        Thread thread = new Thread(new WeekNavigatorDayContainer.ViewInvalidator());
        thread.setPriority(1);
        thread.start();
        this.mWasSelectedIndex = this.mSelectedIndex;
        this.mSelectedIndex = i;
        if (z && (navigator = this.navigator) != null) {
            navigator.goTo(time, true);
        }
        invalidate();
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayContainer
    protected void initDays(int i, boolean z) {
        this.mDayBoxes = new DayNavigatorDayView[i];
        for (int i2 = 0; i2 < i; i2++) {
            DayNavigatorDayView dayNavigatorDayView = new DayNavigatorDayView(getContext(), z, getViewIndex(i2, i), this.mViewNavigator);
            dayNavigatorDayView.setNavigatorDayClickedListener(this, i2);
            this.mDayBoxes[i2] = dayNavigatorDayView;
            addView(this.mDayBoxes[i2]);
        }
    }

    protected ViewIndex getViewIndex(int i, int i2) {
        if (i == 0) {
            return ViewIndex.FIRST;
        }
        if (i == i2 - 1) {
            return ViewIndex.LAST;
        }
        return ViewIndex.MIDDLE;
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayContainer, com.sonymobile.calendar.ICalendarColumnContainer
    public void updateFocusability(boolean z) {
        setFocusable(z);
        setDescendantFocusability(z ? 262144 : 393216);
    }
}
