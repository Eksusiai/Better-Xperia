package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.Holiday;
import com.sonymobile.calendar.birthday.BirthdayService;
import com.sonymobile.calendar.birthday.ContactBirthday;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class AllDayColumnView extends LinearLayout implements IDayColumnView {
    private static final long ADD_EVENT_SHOW_TIME = 3000;
    private static final float EVENT_PADDING_AND_MARGINS = 5.0f;
    private static final float GRID_LINE_WIDTH = 1.0f;
    private CalendarAddEventView addEventView;
    private int columnHeight;
    private int columnWidth;
    private ICalendarColumnsLayout columnsLayout;
    private Time day;
    private CalendarEventNavigator eventNavigator;
    private AllDayEventViewBase[] events;
    private ViewIndex index;
    private boolean isDayView;
    private boolean isFocusable;
    private boolean isR2L;
    private AllDayColumnContainer mColumnContainer;
    private int mMaxNumberOfAllEventsCollapsed;
    private AllDayEventViewMoreEvents mShowMore;
    private View.OnClickListener onBirthDayClickListener;
    private View.OnClickListener onEventClickListener;
    private View.OnClickListener onMoreEventClickListener;
    private Paint paint;
    private CountDownTimer showAddEventTimer;
    private boolean showFullEventText;
    private GestureDetector tapDetector;
    private AllDayEventViewBase tempEvent;
    private long tempEventId;

    public AllDayColumnView(Context context, CalendarEventNavigator calendarEventNavigator, ICalendarColumnsLayout iCalendarColumnsLayout, boolean z, boolean z2, boolean z3, ViewIndex viewIndex, AllDayColumnContainer allDayColumnContainer) {
        super(context);
        this.tempEvent = null;
        this.tempEventId = -1L;
        this.onEventClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.AllDayColumnView.2
            private static final long CLICK_DELAY = 500;
            private long lastClickTime = 0;

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (this.lastClickTime + CLICK_DELAY > jCurrentTimeMillis) {
                    return;
                }
                this.lastClickTime = jCurrentTimeMillis;
                if (AllDayColumnView.this.eventNavigator == null) {
                    return;
                }
                AllDayEventView allDayEventView = (AllDayEventView) view;
                Utils.setDisplayTime(Long.valueOf(((EventInfo[]) allDayEventView.getNavigationInfo())[0].localBegin));
                AllDayColumnView.this.eventNavigator.goToEventDetails(((EventInfo[]) allDayEventView.getNavigationInfo())[0], UiUtils.makeZoomAnimationOnViewBundle(allDayEventView));
            }
        };
        this.onMoreEventClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.AllDayColumnView.3
            private static final long CLICK_DELAY = 500;
            private long lastClickTime = 0;

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (this.lastClickTime + CLICK_DELAY > jCurrentTimeMillis) {
                    return;
                }
                this.lastClickTime = jCurrentTimeMillis;
                AllDayColumnView.this.mColumnContainer.expandAllDayColumns();
            }
        };
        this.onBirthDayClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.AllDayColumnView.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AllDayColumnView.this.eventNavigator == null || !(view instanceof AllDayBirthdayView)) {
                    return;
                }
                AllDayColumnView.this.eventNavigator.goToBirthdays(((Long) ((AllDayBirthdayView) view).getNavigationInfo()).longValue());
            }
        };
        this.eventNavigator = calendarEventNavigator;
        this.columnsLayout = iCalendarColumnsLayout;
        this.isR2L = z;
        this.showFullEventText = z2;
        this.isDayView = z3;
        this.index = viewIndex;
        this.mColumnContainer = allDayColumnContainer;
        setOrientation(1);
        setWillNotDraw(false);
        this.tapDetector = new GestureDetector(getContext(), new TapDetector());
        initMeasures();
        initPaint();
        this.mMaxNumberOfAllEventsCollapsed = getResources().getInteger(R.integer.max_number_of_all_day_events_collapsed);
    }

    public void setColumnWidth(int i, boolean z) {
        this.columnWidth = i;
        if (z) {
            showLessAllDayEvents();
        }
        if (this.isDayView) {
            setLayoutParams(new LinearLayout.LayoutParams(0, this.columnHeight, 1.0f));
            int dimension = (int) getResources().getDimension(R.dimen.day_column_event_padding);
            setPadding(dimension, 0, dimension, 0);
        } else {
            setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
        }
        requestLayout();
        invalidate();
    }

    private int getHeightForEvents(int i) {
        return Math.round(i * (getResources().getDimension(R.dimen.week_view_event_title_text_size) + (getContext().getResources().getDisplayMetrics().density * EVENT_PADDING_AND_MARGINS)));
    }

    public void update(Time time) {
        Time time2 = new SafeTime(time.timezone);
        this.day = time2;
        time2.set(time.monthDay, time.month, time.year);
        this.day.normalize(false);
        updateEvents();
        this.mColumnContainer.resetExpanded();
        this.mColumnContainer.updateMoreIcon();
    }

    @Override // com.sonymobile.calendar.IDayColumnView
    public void removeAddEventView() {
        if (this.addEventView != null) {
            this.showAddEventTimer.cancel();
            removeView(this.addEventView);
            this.addEventView = null;
            this.showAddEventTimer = null;
        }
    }

    @Override // com.sonymobile.calendar.IDayColumnView
    public Time getDate() {
        return this.day;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.tapDetector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    @Override // com.sonymobile.calendar.IDayColumnView
    public void goToCreateEventScreen() {
        this.eventNavigator.goToCreateEvent(this.day.toMillis(false), true);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        drawEdges(canvas);
        super.onDraw(canvas);
    }

    public boolean isToday(long j) {
        Time time = this.day;
        if (time == null) {
            return false;
        }
        long millis = j - time.toMillis(false);
        return millis >= 0 && millis < 86400000;
    }

    public boolean prepareForTempEvent(EventInfo eventInfo) {
        this.tempEventId = eventInfo.id;
        hideEvent(eventInfo.id);
        return eventInfo.allDay == 1 && isToday(eventInfo.localBegin);
    }

    public void addTempEvent(EventInfo eventInfo) {
        AllDayEventView allDayEventView = new AllDayEventView(getContext(), new EventInfo[]{eventInfo}, this.eventNavigator, this.day, true, ViewIndex.MIDDLE);
        this.tempEvent = allDayEventView;
        addView(allDayEventView);
    }

    public void removeTempEvent() {
        AllDayEventViewBase allDayEventViewBase = this.tempEvent;
        if (allDayEventViewBase == null) {
            return;
        }
        removeView(allDayEventViewBase);
        this.tempEvent = null;
    }

    private void initMeasures() {
        this.columnHeight = getHeightForEvents(this.mMaxNumberOfAllEventsCollapsed);
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setAntiAlias(true);
    }

    private void drawEdges(Canvas canvas) {
        this.paint.setStrokeWidth(getResources().getDimension(R.dimen.all_day_icon_border_width));
        this.paint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
        float f = this.isR2L ? this.columnWidth - 1.0f : 0.0f;
        canvas.drawLine(f, 0.0f, f, canvas.getHeight(), this.paint);
        canvas.drawLine(0.0f, 0.0f, canvas.getWidth(), 0.0f, this.paint);
        canvas.drawLine(0.0f, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), this.paint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.sonymobile.calendar.AllDayColumnView$1] */
    public void showAddEventView() {
        CalendarAddEventView calendarAddEventView = new CalendarAddEventView(getContext(), 0, this.eventNavigator, this.day, true);
        this.addEventView = calendarAddEventView;
        addView(calendarAddEventView);
        this.showAddEventTimer = new CountDownTimer(ADD_EVENT_SHOW_TIME, 1000L) { // from class: com.sonymobile.calendar.AllDayColumnView.1
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                AllDayColumnView.this.removeAddEventView();
            }
        }.start();
    }

    private void hideEvent(long j) {
        AllDayEventViewBase[] allDayEventViewBaseArr = this.events;
        if (allDayEventViewBaseArr == null || allDayEventViewBaseArr.length == 0 || allDayEventViewBaseArr[0] == null || j == -1 || (allDayEventViewBaseArr[0] instanceof AllDayBirthdayView)) {
            return;
        }
        for (EventInfo eventInfo : (EventInfo[]) allDayEventViewBaseArr[0].getNavigationInfo()) {
            if (eventInfo.id == j) {
                this.events[0].setVisibility(8);
            }
        }
    }

    private void removeAllEventViews() {
        AllDayEventViewBase[] allDayEventViewBaseArr = this.events;
        if (allDayEventViewBaseArr == null) {
            return;
        }
        for (AllDayEventViewBase allDayEventViewBase : allDayEventViewBaseArr) {
            removeView(allDayEventViewBase);
        }
        removeView(this.mShowMore);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int iMin;
        int childCount = getChildCount();
        boolean z = true;
        if (childCount <= 1) {
            iMin = i2;
            z = false;
        } else {
            iMin = i2 / Math.min(this.mMaxNumberOfAllEventsCollapsed, childCount);
        }
        int i3 = 0;
        int i4 = 0;
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                measureChildWithMargins(childAt, i, 0, iMin, 0);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
                int iMax = Math.max(i4, childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                int iCombineMeasuredStates = combineMeasuredStates(i3, childAt.getMeasuredState());
                if ((childAt instanceof AllDayEventViewBase) && !(childAt instanceof HolidayDayView)) {
                    ((AllDayEventViewBase) childAt).setTitleSingleLine(z);
                }
                i4 = iMax;
                i3 = iCombineMeasuredStates;
            }
        }
        setMeasuredDimension(resolveSizeAndState(this.columnWidth, i, i3), resolveSizeAndState(this.columnHeight, i2, i3 << 16));
    }

    private void updateEvents() {
        removeAllEventViews();
        initMeasures();
        addEvents(EventLoaderService.getInstance().getAllDayEvents(Time.getJulianDay(this.day.toMillis(false), this.day.gmtoff)), BirthdayService.INSTANCE.getBirthdays(this.day.month, this.day.monthDay), FreeDayService.getInstance().getHolidayNameArray(Time.getJulianDay(this.day.toMillis(false), this.day.gmtoff)));
        hideEvent(this.tempEventId);
    }

    private void addEvents(ArrayList<EventInfo> arrayList, ArrayList<ContactBirthday> arrayList2, ArrayList<Holiday> arrayList3) {
        int size = arrayList.size();
        int size2 = arrayList2.size();
        int size3 = arrayList3.size();
        initMeasures();
        if (size2 == 0 && size == 0 && size3 == 0) {
            this.isFocusable = false;
            this.events = null;
            return;
        }
        int i = size + size2 + size3;
        this.events = new AllDayEventViewBase[i];
        for (int i2 = 0; i2 < size3; i2++) {
            addHolidayDayEventView(i2, arrayList3.get(i2));
        }
        for (int i3 = 0; i3 < size; i3++) {
            addAllDayEventView(size3 + i3, arrayList.get(i3));
        }
        for (int i4 = 0; i4 < size2; i4++) {
            addBirthdayView(size3 + size + i4, arrayList2.get(i4));
        }
        int i5 = 0;
        while (i5 < i) {
            int i6 = this.mMaxNumberOfAllEventsCollapsed;
            this.events[i5].setVisibility(i <= i6 || i5 < i6 + (-1) ? 0 : 8);
            i5++;
        }
        int i7 = this.mMaxNumberOfAllEventsCollapsed;
        if (i > i7) {
            addAllDayEventViewMoreView((i + 1) - i7);
        }
    }

    private void addHolidayDayEventView(int i, Holiday holiday) {
        HolidayDayView holidayDayView = new HolidayDayView(getContext(), holiday, this.day, false, this.index);
        this.events[i] = holidayDayView;
        holidayDayView.setVisibility(8);
        AllDayEventViewBase[] allDayEventViewBaseArr = this.events;
        if (allDayEventViewBaseArr.length < this.mMaxNumberOfAllEventsCollapsed) {
            addView(holidayDayView, this.columnWidth, this.columnHeight / allDayEventViewBaseArr.length);
        } else {
            addView(holidayDayView);
        }
        this.events[i].setFocusable(this.isFocusable);
    }

    private void addAllDayEventViewMoreView(int i) {
        AllDayEventViewMoreEvents allDayEventViewMoreEvents = new AllDayEventViewMoreEvents(getContext(), this.day, this.showFullEventText, this.index, i);
        this.mShowMore = allDayEventViewMoreEvents;
        allDayEventViewMoreEvents.setVisibility(0);
        this.mShowMore.setOnClickListener(this.onMoreEventClickListener);
        addView(this.mShowMore);
        this.mShowMore.setFocusable(this.isFocusable);
    }

    private void addAllDayEventView(int i, EventInfo... eventInfoArr) {
        AllDayEventView allDayEventView = new AllDayEventView(getContext(), eventInfoArr, this.eventNavigator, this.day, this.showFullEventText, this.index);
        this.events[i] = allDayEventView;
        allDayEventView.setVisibility(8);
        allDayEventView.setOnClickListener(this.onEventClickListener);
        AllDayEventViewBase[] allDayEventViewBaseArr = this.events;
        if (allDayEventViewBaseArr.length < this.mMaxNumberOfAllEventsCollapsed) {
            addView(allDayEventView, this.columnWidth, this.columnHeight / allDayEventViewBaseArr.length);
        } else {
            addView(allDayEventView);
        }
        this.events[i].setFocusable(this.isFocusable);
    }

    private void addBirthdayView(int i, ContactBirthday... contactBirthdayArr) {
        AllDayBirthdayView allDayBirthdayView = new AllDayBirthdayView(getContext(), contactBirthdayArr, this.day, this.showFullEventText, this.isR2L, this.index);
        allDayBirthdayView.setOnClickListener(this.onBirthDayClickListener);
        this.events[i] = allDayBirthdayView;
        allDayBirthdayView.setVisibility(8);
        AllDayEventViewBase[] allDayEventViewBaseArr = this.events;
        if (allDayEventViewBaseArr.length < this.mMaxNumberOfAllEventsCollapsed) {
            addView(allDayBirthdayView, this.columnWidth, this.columnHeight / allDayEventViewBaseArr.length);
        } else {
            addView(allDayBirthdayView);
        }
        this.events[i].setFocusable(this.isFocusable);
    }

    public void expandAllDayEvents() {
        AllDayEventViewBase[] allDayEventViewBaseArr;
        AllDayEventViewBase[] allDayEventViewBaseArr2 = this.events;
        if (allDayEventViewBaseArr2 != null && allDayEventViewBaseArr2.length > this.mMaxNumberOfAllEventsCollapsed) {
            int i = 0;
            while (true) {
                allDayEventViewBaseArr = this.events;
                if (i >= allDayEventViewBaseArr.length) {
                    break;
                }
                if (allDayEventViewBaseArr[i] != null) {
                    allDayEventViewBaseArr[i].setVisibility(0);
                }
                i++;
            }
            this.columnHeight = getHeightForEvents(allDayEventViewBaseArr.length);
            this.mShowMore.setVisibility(8);
        }
        requestLayout();
        invalidate();
    }

    public void showLessAllDayEvents() {
        AllDayEventViewBase[] allDayEventViewBaseArr = this.events;
        if (allDayEventViewBaseArr != null && allDayEventViewBaseArr.length > this.mMaxNumberOfAllEventsCollapsed) {
            int i = 0;
            while (true) {
                AllDayEventViewBase[] allDayEventViewBaseArr2 = this.events;
                if (i >= allDayEventViewBaseArr2.length) {
                    break;
                }
                if (i < this.mMaxNumberOfAllEventsCollapsed - 1) {
                    allDayEventViewBaseArr2[i].setVisibility(0);
                } else {
                    allDayEventViewBaseArr2[i].setVisibility(8);
                }
                i++;
            }
            AllDayEventViewMoreEvents allDayEventViewMoreEvents = this.mShowMore;
            if (allDayEventViewMoreEvents != null) {
                allDayEventViewMoreEvents.setVisibility(0);
            }
            this.columnHeight = getHeightForEvents(this.mMaxNumberOfAllEventsCollapsed);
        }
        requestLayout();
        invalidate();
    }

    public int numOfAllDayEvents() {
        AllDayEventViewBase[] allDayEventViewBaseArr = this.events;
        if (allDayEventViewBaseArr != null) {
            return allDayEventViewBaseArr.length;
        }
        return 0;
    }

    public void updateFocusability(boolean z) {
        this.isFocusable = z;
        setDescendantFocusability(z ? 262144 : 393216);
        AllDayEventViewBase[] allDayEventViewBaseArr = this.events;
        if (allDayEventViewBaseArr != null) {
            boolean z2 = false;
            for (AllDayEventViewBase allDayEventViewBase : allDayEventViewBaseArr) {
                if (allDayEventViewBase != null) {
                    allDayEventViewBase.setFocusable(z);
                }
            }
            if (z && this.events.length != 0) {
                z2 = true;
            }
            this.isFocusable = z2;
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
            if (AllDayColumnView.this.columnsLayout != null) {
                AllDayColumnView.this.columnsLayout.onDayClicked(AllDayColumnView.this);
            }
            AllDayColumnView.this.showAddEventView();
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            AllDayColumnView.this.goToCreateEventScreen();
        }
    }
}
