package com.sonymobile.calendar;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ScrollView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.birthday.BirthdayActivity;
import com.sonymobile.calendar.editevent.EditEventActivity;
import com.sonymobile.calendar.editevent.TabletEditEventActivity;
import com.sonymobile.calendar.tablet.TabletEventInfoActivity;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CalendarViewBase extends ScrollView implements CalendarEventNavigator, CalendarColumnsSizeHandler {
    public static final int CURRENT_VIEW = 1;
    public static final int MINUTES_PER_DAY = 1440;
    public static final int NEXT_VIEW = 2;
    private static final String PERIOD_SPACE = ". ";
    public static final int PREVIOUS_VIEW = 0;
    protected static final long TRANSITION_DURATION = 300;
    private boolean adjacentViewsLoaded;
    protected ICalendarFragment calendarFragment;
    protected ICalendarColumnContainer[] columnContainers;
    protected CalendarColumnsLayout columnsLayout;
    protected Time displayedDate;
    protected Time nextViewDate;
    protected Time previousViewDate;
    protected CalendarSwipeView swipeView;
    protected Runnable updateTimeZone;

    protected abstract int getDayCount();

    protected abstract Time[] getDays(Time time);

    protected abstract ICalendarColumnContainer getNewCalendarColumnContainer();

    protected abstract Time getNextViewTime(Time time, boolean z);

    protected abstract void inflateLayout(LayoutInflater layoutInflater);

    @Override // android.widget.ScrollView, android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int i, Rect rect) {
        return true;
    }

    protected void onSwipeCentered() {
    }

    protected abstract void onSwipeCompleted(boolean z);

    protected abstract void scrollToSelectedPosition(boolean z);

    public CalendarViewBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.updateTimeZone = new Runnable() { // from class: com.sonymobile.calendar.CalendarViewBase.5
            @Override // java.lang.Runnable
            public void run() {
                long millis = CalendarViewBase.this.displayedDate != null ? CalendarViewBase.this.displayedDate.toMillis(false) : System.currentTimeMillis();
                CalendarViewBase.this.displayedDate = new SafeTime(Utils.getTimeZone(CalendarViewBase.this.getContext(), this));
                CalendarViewBase.this.displayedDate.set(millis);
                CalendarViewBase.this.displayedDate.normalize(true);
            }
        };
        this.displayedDate = new SafeTime(Utils.getTimeZone(context, this.updateTimeZone));
        inflateLayout(LayoutInflater.from(getContext()));
        initColumnContainer();
        initSwipeView();
        setDescendantFocusability(262144);
        setFocusable(false);
    }

    public void setCalendarFragment(ICalendarFragment iCalendarFragment) {
        this.calendarFragment = iCalendarFragment;
        this.swipeView.setNavigator(iCalendarFragment);
        this.swipeView.setCalendarSwipeListener(iCalendarFragment);
        this.columnsLayout.setCalendarFragment(iCalendarFragment);
    }

    public void swipeTo(int i) {
        this.swipeView.scrollTo(i, 0);
    }

    @Override // com.sonymobile.calendar.CalendarColumnsSizeHandler
    public void onWidthChanged() {
        scrollToSelectedPosition(false);
        this.swipeView.scrollToCenterView();
    }

    public long getSelectedTimeInMillis() {
        return this.displayedDate.normalize(true);
    }

    public Time getSelectedTime() {
        return this.displayedDate;
    }

    public void invalidateColumns() {
        this.columnsLayout.removeAllViews();
        initColumnContainer();
        updateAdjacentViews();
    }

    public void removeAddEventView() {
        this.columnsLayout.removeAddEventView();
    }

    public void reloadEvents() {
        for (ICalendarColumnContainer iCalendarColumnContainer : this.columnContainers) {
            if (iCalendarColumnContainer != null) {
                iCalendarColumnContainer.reloadEvents();
            }
        }
    }

    public void goToDate(final Time time, boolean z) {
        final boolean z2 = Time.compare(this.displayedDate, time) <= 0;
        this.displayedDate.set(time);
        this.previousViewDate = getNextViewTime(time, false);
        this.nextViewDate = getNextViewTime(time, true);
        if (!z) {
            this.columnContainers[1].updateView(getDays(time), true);
            if (this.adjacentViewsLoaded) {
                this.columnContainers[2].updateView(getDays(this.nextViewDate), true);
                this.columnContainers[0].updateView(getDays(this.previousViewDate), true);
            }
            post(new Runnable() { // from class: com.sonymobile.calendar.CalendarViewBase.1
                @Override // java.lang.Runnable
                public void run() {
                    CalendarViewBase.this.swipeView.scrollToCenterView();
                }
            });
            updateFocusability();
            return;
        }
        if (this.adjacentViewsLoaded) {
            blockRelayout();
            this.columnContainers[z2 ? (char) 2 : (char) 0].updateView(getDays(time), true);
            this.swipeView.animateSwipe(z2, new SwipeAnimationDoneHandler() { // from class: com.sonymobile.calendar.CalendarViewBase.2
                @Override // com.sonymobile.calendar.SwipeAnimationDoneHandler
                public void onSwipeAnimationDone(boolean z3) {
                    final int i;
                    int i2;
                    if (z3) {
                        CalendarViewBase calendarViewBase = CalendarViewBase.this;
                        calendarViewBase.goToDate(calendarViewBase.displayedDate, false);
                        return;
                    }
                    Time time2 = CalendarViewBase.this.previousViewDate;
                    Time time3 = CalendarViewBase.this.nextViewDate;
                    char c = 2;
                    if (z2) {
                        i = 2;
                        i2 = 2;
                        c = 0;
                    } else {
                        time2 = CalendarViewBase.this.nextViewDate;
                        time3 = CalendarViewBase.this.previousViewDate;
                        i = 0;
                        i2 = 0;
                    }
                    final Time time3Final = time3;
                    CalendarViewBase.this.columnsLayout.removeView((View) CalendarViewBase.this.columnContainers[c]);
                    CalendarViewBase.this.columnContainers[c] = CalendarViewBase.this.columnContainers[1];
                    CalendarViewBase.this.columnContainers[c].updateView(CalendarViewBase.this.getDays(time2), true);
                    CalendarViewBase.this.columnContainers[1] = CalendarViewBase.this.columnContainers[i];
                    CalendarViewBase.this.post(new Runnable() { // from class: com.sonymobile.calendar.CalendarViewBase.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            CalendarViewBase.this.swipeView.scrollToCenterView();
                            CalendarViewBase.this.columnContainers[i].updateView(CalendarViewBase.this.getDays(time3Final), true);
                        }
                    });
                    CalendarViewBase.this.columnContainers[i] = CalendarViewBase.this.getNewCalendarColumnContainer();
                    CalendarViewBase.this.swipeView.blockScrollBy();
                    CalendarViewBase.this.columnsLayout.addView((View) CalendarViewBase.this.columnContainers[i], i2);
                    CalendarViewBase.this.updateFocusability();
                    CalendarViewBase.this.columnContainers[i].updateView(CalendarViewBase.this.getDays(time), false);
                    CalendarViewBase.this.onSwipeCompleted(z2);
                    CalendarViewBase.this.scrollToSelectedPosition(true);
                }
            });
        }
    }

    public Time goToNext() {
        boolean zIsWeekClearOfEpochUpperLimit = Utils.isWeekClearOfEpochUpperLimit(this.displayedDate.year, this.displayedDate.month, this.displayedDate.monthDay);
        if (this.adjacentViewsLoaded) {
            if (zIsWeekClearOfEpochUpperLimit) {
                blockRelayout();
                this.displayedDate = getNextViewTime(this.displayedDate, true);
                this.swipeView.animateSwipe(true, new SwipeAnimationDoneHandler() { // from class: com.sonymobile.calendar.CalendarViewBase.3
                    @Override // com.sonymobile.calendar.SwipeAnimationDoneHandler
                    public void onSwipeAnimationDone(boolean z) {
                        if (z) {
                            CalendarViewBase.this.swipeView.scrollTo(CalendarViewBase.this.swipeView.getWidth(), 0);
                            CalendarViewBase calendarViewBase = CalendarViewBase.this;
                            calendarViewBase.goToDate(calendarViewBase.displayedDate, false);
                            return;
                        }
                        CalendarViewBase.this.columnsLayout.removeView((View) CalendarViewBase.this.columnContainers[0]);
                        CalendarViewBase.this.columnContainers[0] = CalendarViewBase.this.columnContainers[1];
                        CalendarViewBase.this.columnContainers[1] = CalendarViewBase.this.columnContainers[2];
                        CalendarViewBase.this.swipeView.scrollToCenterView();
                        CalendarViewBase.this.columnContainers[2] = CalendarViewBase.this.getNewCalendarColumnContainer();
                        CalendarViewBase.this.columnsLayout.addView((View) CalendarViewBase.this.columnContainers[2]);
                        CalendarViewBase.this.updateFocusability();
                        CalendarViewBase calendarViewBase2 = CalendarViewBase.this;
                        CalendarViewBase.this.columnContainers[2].updateView(CalendarViewBase.this.getDays(calendarViewBase2.getNextViewTime(calendarViewBase2.displayedDate, true)), true);
                        CalendarViewBase.this.onSwipeCompleted(true);
                        CalendarViewBase.this.scrollToSelectedPosition(true);
                    }
                });
            } else {
                this.swipeView.animateBack();
            }
        }
        return new SafeTime(this.displayedDate);
    }

    public Time goToPrevious() {
        if (this.adjacentViewsLoaded) {
            blockRelayout();
            this.displayedDate = getNextViewTime(this.displayedDate, false);
            this.swipeView.animateSwipe(false, new SwipeAnimationDoneHandler() { // from class: com.sonymobile.calendar.CalendarViewBase.4
                @Override // com.sonymobile.calendar.SwipeAnimationDoneHandler
                public void onSwipeAnimationDone(boolean z) {
                    if (z) {
                        CalendarViewBase.this.swipeView.scrollTo(CalendarViewBase.this.swipeView.getWidth(), 0);
                        CalendarViewBase calendarViewBase = CalendarViewBase.this;
                        calendarViewBase.goToDate(calendarViewBase.displayedDate, false);
                        return;
                    }
                    CalendarViewBase.this.columnsLayout.removeView((View) CalendarViewBase.this.columnContainers[2]);
                    CalendarViewBase.this.columnContainers[2] = CalendarViewBase.this.columnContainers[1];
                    CalendarViewBase.this.columnContainers[1] = CalendarViewBase.this.columnContainers[0];
                    CalendarViewBase.this.swipeView.scrollToCenterView();
                    CalendarViewBase.this.columnContainers[0] = CalendarViewBase.this.getNewCalendarColumnContainer();
                    CalendarViewBase.this.updateFocusability();
                    CalendarViewBase.this.columnsLayout.addView((View) CalendarViewBase.this.columnContainers[0], 0);
                    CalendarViewBase calendarViewBase2 = CalendarViewBase.this;
                    CalendarViewBase.this.columnContainers[0].updateView(CalendarViewBase.this.getDays(calendarViewBase2.getNextViewTime(calendarViewBase2.displayedDate, false)), true);
                    CalendarViewBase.this.onSwipeCompleted(false);
                    CalendarViewBase.this.scrollToSelectedPosition(true);
                }
            });
        }
        return new SafeTime(this.displayedDate);
    }

    public void animateBack() {
        this.swipeView.animateBack();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFocusability() {
        this.columnContainers[0].updateFocusability(false);
        this.columnContainers[1].updateFocusability(true);
        this.columnContainers[2].updateFocusability(false);
    }

    @Override // com.sonymobile.calendar.CalendarEventNavigator
    public void goToCreateEvent(long j, boolean z) {
        long j2;
        Intent intent = new Intent("android.intent.action.INSERT");
        if (Utils.isTabletDevice(getContext())) {
            intent.setClass(getContext(), TabletEditEventActivity.class);
        } else {
            intent.setClassName(getContext(), EditEventActivity.class.getName());
        }
        if (z) {
            j = Utils.convertMillisToMidnightUTC(getContext(), j);
            j2 = 86400000;
        } else {
            j2 = 3600000;
        }
        intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, j);
        intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, j2 + j);
        intent.putExtra("allDay", z);
        getContext().startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(), new Pair[0]).toBundle());
    }

    @Override // com.sonymobile.calendar.CalendarEventNavigator
    public void deleteEvent(EventInfo eventInfo) {
        new DeleteEventHelper(getContext(), false).delete(eventInfo.begin, eventInfo.end, eventInfo.id, -1, eventInfo.isLunarEvent);
    }

    @Override // com.sonymobile.calendar.CalendarEventNavigator
    public void goToEditEvent(EventInfo eventInfo) {
        Intent intent = new Intent("android.intent.action.EDIT");
        intent.setClassName(getContext(), EditEventActivity.class.getName());
        intent.setData(ContentUris.withAppendedId(eventInfo.isLunarEvent ? LunarContract.Events.CONTENT_URI : CalendarContract.Events.CONTENT_URI, eventInfo.id));
        intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, eventInfo.begin);
        intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, eventInfo.end);
        getContext().startActivity(intent);
    }

    @Override // com.sonymobile.calendar.CalendarEventNavigator
    public void goToEventDetails(EventInfo eventInfo, Bundle bundle) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(ContentUris.withAppendedId(eventInfo.isLunarEvent ? LunarContract.Events.CONTENT_URI : CalendarContract.Events.CONTENT_URI, eventInfo.id));
        if (Utils.isTabletDevice(getContext())) {
            intent.setClassName(getContext(), TabletEventInfoActivity.class.getName());
        } else {
            intent.setClassName(getContext(), EventInfoActivity.class.getName());
        }
        intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, eventInfo.begin);
        intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, eventInfo.end);
        if (bundle == null) {
            intent.putExtra(UiUtils.SLIDE_UP_ANIMATION, true);
        }
        getContext().startActivity(intent, bundle);
    }

    @Override // com.sonymobile.calendar.CalendarEventNavigator
    public void showEventPreview(EventInfo[] eventInfoArr, float f, float f2) {
        new EventPreviewPopup(getContext(), eventInfoArr, this.calendarFragment.getCalendarLeftTop(), this.calendarFragment.getCalendarDimensions(), this).show(this, f, f2);
    }

    @Override // com.sonymobile.calendar.CalendarEventNavigator
    public void goToBirthdays(long j) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.putExtra(BirthdayActivity.DISPLAYED_TIME, j);
        intent.setClassName(getContext(), BirthdayActivity.class.getName());
        intent.setFlags(537001984);
        getContext().startActivity(intent);
    }

    public void sendWindowChangedAccessibilityEvent() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (isShown() && accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(32);
        }
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 32) {
            accessibilityEvent.getText().add(getResources().getString(R.string.app_label));
            accessibilityEvent.getText().add(PERIOD_SPACE);
            accessibilityEvent.getText().add(this.calendarFragment.getDateString());
            return true;
        }
        return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int size = (int) (View.MeasureSpec.getSize(i) - getLeftPanelWidth());
        int size2 = View.MeasureSpec.getSize(i2);
        for (ICalendarColumnContainer iCalendarColumnContainer : this.columnContainers) {
            if (iCalendarColumnContainer != null) {
                iCalendarColumnContainer.setViewPortSize(size, size2, false);
            }
        }
        super.onMeasure(i, i2);
    }

    protected float getLeftPanelWidth() {
        return getResources().getDimension(R.dimen.calendar_left_panel_width);
    }

    protected void initColumnContainer() {
        CalendarColumnsLayout calendarColumnsLayout = (CalendarColumnsLayout) findViewById(R.id.calendar_columns_container);
        this.columnsLayout = calendarColumnsLayout;
        calendarColumnsLayout.setCalendarColumnsReadyHandler(this);
        this.columnContainers = new ICalendarColumnContainer[3];
        for (int i = 0; i < 3; i++) {
            this.columnContainers[i] = getNewCalendarColumnContainer();
            this.columnsLayout.addView((View) this.columnContainers[i]);
        }
        updateFocusability();
    }

    protected void updateAdjacentViews() {
        if (this.previousViewDate == null || this.nextViewDate == null) {
            return;
        }
        this.adjacentViewsLoaded = true;
        this.swipeView.blockScrollBy();
        this.columnContainers[0].updateView(getDays(this.previousViewDate), true);
        this.columnContainers[2].updateView(getDays(this.nextViewDate), true);
        updateFocusability();
    }

    private void initSwipeView() {
        CalendarSwipeView calendarSwipeView = (CalendarSwipeView) findViewById(R.id.calendar_swipe_view);
        this.swipeView = calendarSwipeView;
        calendarSwipeView.setIsR2L(isR2L());
    }

    private void blockRelayout() {
        this.columnContainers[0].blockRelayout();
        this.columnContainers[1].blockRelayout();
        this.columnContainers[2].blockRelayout();
    }

    protected boolean isR2L() {
        return CalendarApplication.isR2L(getResources());
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            return super.onTouchEvent(motionEvent);
        } catch (IllegalArgumentException unused) {
            return true;
        }
    }
}
