package com.sonymobile.calendar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CalendarGridViewBase extends CalendarViewBase {
    private static final double CURRENT_TIME_OFFSET = 1.25d;
    private static final double DEFAULT_START_HOUR = 7.75d;
    private int hourHeight;
    private int mHourHeightLandscapeValue;
    private int mHourHeightPortraitValue;
    private ScaleGestureDetector pinchDetector;
    private CalendarTimeLine timeLine;

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected abstract int getDayCount();

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected abstract Time[] getDays(Time time);

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected abstract Time getNextViewTime(Time time, boolean z);

    public CalendarGridViewBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.hourHeight = (int) getResources().getDimension(R.dimen.hour_height);
        this.pinchDetector = new ScaleGestureDetector(context, new PinchDetector());
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() > 1) {
            this.pinchDetector.onTouchEvent(motionEvent);
            motionEvent.setAction(3);
            super.dispatchTouchEvent(motionEvent);
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void initColumnContainer() {
        super.initColumnContainer();
        int i = this.hourHeight;
        if (i != 0) {
            setHourHeight(i);
        }
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public void goToDate(Time time, boolean z) {
        super.goToDate(time, z);
        if (!z) {
            this.calendarFragment.onTransitionComplete();
        }
        this.timeLine.setDisplayedDate(this.displayedDate);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase, android.widget.ScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        this.timeLine.setViewPortHeight(View.MeasureSpec.getSize(i2));
        super.onMeasure(i, i2);
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        this.timeLine.setScrollOffset(i2);
        super.onScrollChanged(i, i2, i3, i4);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void inflateLayout(LayoutInflater layoutInflater) {
        layoutInflater.inflate(R.layout.calendar_view_grid_base, this);
        initTimeLine();
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected ICalendarColumnContainer getNewCalendarColumnContainer() {
        DayColumnContainer dayColumnContainer = new DayColumnContainer(getContext(), getDayCount(), this, this.columnsLayout, isR2L(), this instanceof DayView);
        int i = this.hourHeight;
        if (i != 0) {
            dayColumnContainer.setHourHeight(i);
        }
        return dayColumnContainer;
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void onSwipeCompleted(boolean z) {
        ICalendarColumnContainer iCalendarColumnContainer = this.columnContainers[z ? (char) 2 : (char) 0];
        iCalendarColumnContainer.setViewPortSize(this.swipeView.getMeasuredWidth(), getHeight(), true);
        iCalendarColumnContainer.setHourHeight(((DayColumnContainer) this.columnContainers[1]).getHourHeight());
        this.calendarFragment.onTransitionComplete();
        this.timeLine.setDisplayedDate(this.displayedDate);
    }

    private void initTimeLine() {
        CalendarTimeLine calendarTimeLine = (CalendarTimeLine) findViewById(R.id.calendar_timeline);
        this.timeLine = calendarTimeLine;
        calendarTimeLine.setShouldDisplayForFullWeek(this instanceof WeekView);
        this.timeLine.setDisplayedDate(this.displayedDate);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void scrollToSelectedPosition(boolean z) {
        int hourHeight = ((DayColumnContainer) this.columnContainers[1]).getHourHeight();
        this.hourHeight = hourHeight;
        int iMin = (int) Math.min(((double) hourHeight) * getStartHour(), (this.hourHeight * 24) - getHeight());
        if (!z) {
            scrollTo(0, iMin);
            return;
        }
        ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(this, "scrollY", getScrollY(), iMin);
        objectAnimatorOfInt.setDuration(300L);
        objectAnimatorOfInt.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimatorOfInt.start();
    }

    public void setHourHeight(int i) {
        for (ICalendarColumnContainer iCalendarColumnContainer : this.columnContainers) {
            iCalendarColumnContainer.setHourHeight(i);
        }
        this.hourHeight = i;
    }

    public int getHourHeight() {
        return this.hourHeight;
    }

    public int getHourHeightPortraitValue() {
        return this.mHourHeightPortraitValue;
    }

    public int getHourHeightLandscapeValue() {
        return this.mHourHeightLandscapeValue;
    }

    public void setHourHeightPortraitValue(int i) {
        this.mHourHeightPortraitValue = i;
    }

    public void setHourHeightLandscapeValue(int i) {
        this.mHourHeightLandscapeValue = i;
    }

    private double getStartHour() {
        Time[] days = getDays(this.displayedDate);
        Time time = new SafeTime(this.displayedDate.timezone);
        time.setToNow();
        for (Time time2 : days) {
            if (time2.monthDay == time.monthDay && time2.month == time.month && time2.year == time.year) {
                return Math.max(((double) time.hour) - CURRENT_TIME_OFFSET, 0.0d);
            }
        }
        return DEFAULT_START_HOUR;
    }

    private class PinchDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private PinchDetector() {
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float currentSpanY = scaleGestureDetector.getCurrentSpanY() / scaleGestureDetector.getPreviousSpanY();
            int hourHeight = (int) (((DayColumnContainer) CalendarGridViewBase.this.columnContainers[0]).getHourHeight() * currentSpanY);
            for (ICalendarColumnContainer iCalendarColumnContainer : CalendarGridViewBase.this.columnContainers) {
                if (!iCalendarColumnContainer.setHourHeight(hourHeight)) {
                    return true;
                }
            }
            CalendarGridViewBase calendarGridViewBase = CalendarGridViewBase.this;
            calendarGridViewBase.scrollTo(0, (int) (((calendarGridViewBase.getScrollY() + scaleGestureDetector.getFocusY()) * currentSpanY) - scaleGestureDetector.getFocusY()));
            return true;
        }
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public void swipeTo(int i) {
        super.swipeTo(i);
        this.timeLine.setVisibilityOfCircle(false);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    public void onSwipeCentered() {
        this.timeLine.setVisibilityOfCircle(true);
    }

    public void invalidateTimeLine() {
        CalendarTimeLine calendarTimeLine = this.timeLine;
        if (calendarTimeLine != null) {
            calendarTimeLine.invalidate();
        }
    }

    public void invalidateCurrentView() {
        if (this.columnContainers == null || this.columnContainers[1] == null) {
            return;
        }
        this.columnContainers[1].invalidate();
    }

    @Override // android.widget.ScrollView, android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.setHourHeight(this.hourHeight);
        return savedState;
    }

    @Override // android.widget.ScrollView, android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (this.mHourHeightPortraitValue == 0 && this.mHourHeightLandscapeValue == 0) {
            setHourHeight(savedState.getHourHeight());
        }
    }

    private static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.sonymobile.calendar.CalendarGridViewBase.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        private int mHourHeight;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mHourHeight = parcel.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mHourHeight);
        }

        public int getHourHeight() {
            return this.mHourHeight;
        }

        public void setHourHeight(int i) {
            this.mHourHeight = i;
        }
    }
}
