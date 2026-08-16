package com.sonymobile.calendar.editevent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.AllDayColumnContainer;
import com.sonymobile.calendar.AllDayColumnView;
import com.sonymobile.calendar.AllDayDayView;
import com.sonymobile.calendar.ICalendarColumnContainer;

/* JADX INFO: loaded from: classes2.dex */
public class TabletEditEventAllDayView extends AllDayDayView {
    private TabletEditEventDayFragment editEventFragment;
    private EventInfo mTempEvent;
    private GestureDetector tapDetector;
    private AllDayColumnView tempEventOwnerColumn;

    public TabletEditEventAllDayView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.tapDetector = new GestureDetector(getContext(), new TapDetector());
        this.swipeView.setIsInEditEvent(true);
    }

    public void init(TabletEditEventDayFragment tabletEditEventDayFragment, EventInfo eventInfo) {
        this.editEventFragment = tabletEditEventDayFragment;
        this.mTempEvent = eventInfo;
    }

    public void setIsLockedToDate(boolean z) {
        this.swipeView.setEnabled(!z);
    }

    public void setTempEvent(EventInfo eventInfo) {
        this.mTempEvent = eventInfo;
        updateTempEvent();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        super.dispatchTouchEvent(motionEvent);
        this.tapDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override // com.sonymobile.calendar.AllDayViewBase, com.sonymobile.calendar.CalendarViewBase
    protected void onSwipeCompleted(boolean z) {
        super.onSwipeCompleted(z);
        setTempEvent(this.mTempEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTempEvent() {
        AllDayColumnView allDayColumnView = this.tempEventOwnerColumn;
        if (allDayColumnView != null) {
            allDayColumnView.removeTempEvent();
        }
        for (ICalendarColumnContainer iCalendarColumnContainer : this.columnContainers) {
            AllDayColumnView allDayColumnViewAddTempEvent = ((AllDayColumnContainer) iCalendarColumnContainer).addTempEvent(this.mTempEvent);
            if (allDayColumnViewAddTempEvent != null) {
                this.tempEventOwnerColumn = allDayColumnViewAddTempEvent;
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
            TabletEditEventAllDayView.this.mTempEvent.allDay = 1;
            TabletEditEventAllDayView.this.updateTempEvent();
            if (TabletEditEventAllDayView.this.editEventFragment != null) {
                TabletEditEventAllDayView.this.editEventFragment.updateEditEventView(TabletEditEventAllDayView.this.mTempEvent);
            }
            return true;
        }
    }
}
