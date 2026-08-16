package com.sonymobile.calendar.editevent;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.DayColumnContainer;
import com.sonymobile.calendar.DayColumnView;
import com.sonymobile.calendar.DayView;
import com.sonymobile.calendar.ICalendarColumnContainer;

/* JADX INFO: loaded from: classes2.dex */
public class TabletEditEventDayView extends DayView {
    private TabletEditEventDayFragment editEventFragment;
    private EventInfo mTempEvent;
    private GestureDetector tapDetector;
    private DayColumnView tempEventOwnerColumn;

    public TabletEditEventDayView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.tapDetector = new GestureDetector(getContext(), new TapDetector());
        this.swipeView.setIsInEditEvent(true);
    }

    public void init(TabletEditEventDayFragment tabletEditEventDayFragment, EventInfo eventInfo) {
        this.editEventFragment = tabletEditEventDayFragment;
        setTempEvent(eventInfo);
    }

    @Override // com.sonymobile.calendar.CalendarGridViewBase, com.sonymobile.calendar.CalendarViewBase
    public void goToDate(Time time, boolean z) {
        super.goToDate(time, z);
        updateTempEvent();
    }

    public void setIsLockedToDate(boolean z) {
        this.swipeView.setEnabled(!z);
    }

    public void setTempEvent(EventInfo eventInfo) {
        this.mTempEvent = eventInfo;
        updateTempEvent();
    }

    @Override // com.sonymobile.calendar.CalendarGridViewBase, android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        super.dispatchTouchEvent(motionEvent);
        this.tapDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override // com.sonymobile.calendar.CalendarGridViewBase, com.sonymobile.calendar.CalendarViewBase
    protected void onSwipeCompleted(boolean z) {
        super.onSwipeCompleted(z);
        setTempEvent(this.mTempEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getMillisForClickedHourAt(float f) {
        int scrollY = (int) ((getScrollY() + f) / ((DayColumnContainer) this.columnContainers[1]).getHourHeight());
        Time time = new SafeTime(getSelectedTime().timezone);
        time.set(0, 0, scrollY, this.displayedDate.monthDay, this.displayedDate.month, this.displayedDate.year);
        return time.normalize(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTempEvent() {
        DayColumnView dayColumnViewAddTempEvent;
        DayColumnView dayColumnView = this.tempEventOwnerColumn;
        if (dayColumnView != null) {
            dayColumnView.removeTempEvent();
        }
        for (ICalendarColumnContainer iCalendarColumnContainer : this.columnContainers) {
            if (iCalendarColumnContainer != null && (dayColumnViewAddTempEvent = ((DayColumnContainer) iCalendarColumnContainer).addTempEvent(this.mTempEvent)) != null) {
                this.tempEventOwnerColumn = dayColumnViewAddTempEvent;
            }
        }
    }

    private class TapDetector extends GestureDetector.SimpleOnGestureListener {
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        private TapDetector() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            try {
                TabletEditEventDayView.this.mTempEvent.allDay = 0;
                TabletEditEventDayView.this.mTempEvent.localBegin = TabletEditEventDayView.this.getMillisForClickedHourAt(motionEvent.getY());
                TabletEditEventDayView.this.mTempEvent.end = TabletEditEventDayView.this.mTempEvent.localBegin + 3600000;
                TabletEditEventDayView.this.updateTempEvent();
                if (TabletEditEventDayView.this.editEventFragment == null) {
                    return true;
                }
                TabletEditEventDayView.this.editEventFragment.updateEditEventView(TabletEditEventDayView.this.mTempEvent);
                return true;
            } catch (IllegalArgumentException unused) {
                return false;
            }
        }
    }
}
