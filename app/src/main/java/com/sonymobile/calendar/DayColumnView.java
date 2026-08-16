package com.sonymobile.calendar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonymobile.calendar.utils.EventUtils;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class DayColumnView extends ViewGroup implements IDayColumnView {
    private static final long ADD_EVENT_SHOW_TIME = 3000;
    private static final int EVENT_MARGIN = 1;
    private static final float GRID_LINE_WIDTH = 2.0f;
    private CalendarAddEventView addEventView;
    private ArrayList<CalendarEventCluster> clusterList;
    private int columnWidth;
    private ICalendarColumnsLayout columnsLayout;
    private Time day;
    private CalendarEventNavigator eventNavigator;
    private CalendarEventView[] events;
    private boolean extendedLayout;
    private int hourHeight;
    private boolean isDayView;
    private boolean isFocusable;
    private boolean isR2L;
    private float latestTouchY;
    private int maxHourHeight;
    private int minHourHeight;
    private boolean needRelayout;
    private View.OnClickListener onEventClickListener;
    private Paint paint;
    private CountDownTimer showAddEventTimer;
    private GestureDetector tapDetector;
    private CalendarEventView tempEvent;
    private long tempEventId;

    public DayColumnView(Context context, CalendarEventNavigator calendarEventNavigator, ICalendarColumnsLayout iCalendarColumnsLayout, boolean z, boolean z2, boolean z3) {
        super(context);
        this.tempEvent = null;
        this.tempEventId = -1L;
        this.maxHourHeight = 1200;
        this.onEventClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.DayColumnView.3
            private static final long CLICK_DELAY = 1000;
            private long lastClickTime = 0;

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (Math.abs(jCurrentTimeMillis - this.lastClickTime) < CLICK_DELAY) {
                    return;
                }
                this.lastClickTime = jCurrentTimeMillis;
                if (DayColumnView.this.eventNavigator == null) {
                    return;
                }
                CalendarEventView calendarEventView = (CalendarEventView) view;
                Utils.setDisplayTime(Long.valueOf(calendarEventView.getLocalStartTimeMillis()));
                if (DayColumnView.this.isDayView || (!calendarEventView.shouldShowPreview() && calendarEventView.getClusterIndex() == -1)) {
                    DayColumnView.this.goToEventDetailsScreen(calendarEventView);
                    return;
                }
                int[] iArr = new int[2];
                calendarEventView.getLocationInWindow(iArr);
                float[] lastTouchLocation = calendarEventView.getLastTouchLocation();
                lastTouchLocation[0] = lastTouchLocation[0] + iArr[0];
                lastTouchLocation[1] = lastTouchLocation[1] + iArr[1];
                if (calendarEventView.getClusterIndex() == -1) {
                    if (!DayColumnView.this.isDayView) {
                        DayColumnView.this.goToEventDetailsScreen(calendarEventView);
                        return;
                    } else {
                        DayColumnView.this.eventNavigator.showEventPreview(new EventInfo[]{calendarEventView.getEventInfo()}, lastTouchLocation[0], lastTouchLocation[1]);
                        return;
                    }
                }
                DayColumnView.this.eventNavigator.showEventPreview(((CalendarEventCluster) DayColumnView.this.clusterList.get(calendarEventView.getClusterIndex())).getEventInfos(), lastTouchLocation[0], lastTouchLocation[1]);
            }
        };
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1.0f));
        setWillNotDraw(false);
        this.eventNavigator = calendarEventNavigator;
        this.columnsLayout = iCalendarColumnsLayout;
        this.isR2L = z;
        this.extendedLayout = z2;
        this.isDayView = z3;
        this.tapDetector = new GestureDetector(getContext(), new TapDetector());
        initMeasures();
        initPaint();
        setDescendantFocusability(262144);
    }

    public void zoomToDefault(long j) {
        if (j == 0) {
            setHourHeight(getDefaultHourHeight(this));
            return;
        }
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(this.hourHeight, getDefaultHourHeight(this));
        valueAnimatorOfInt.setDuration(j);
        valueAnimatorOfInt.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.sonymobile.calendar.DayColumnView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DayColumnView.this.setHourHeight(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
        valueAnimatorOfInt.start();
    }

    public void setColumnWidth(int i) {
        this.columnWidth = i;
    }

    public void setHourHeightLimits(int i, int i2) {
        if (i > this.minHourHeight) {
            this.minHourHeight = i;
        }
        this.maxHourHeight = i2;
        normalizeHourHeight();
    }

    private void normalizeHourHeight() {
        this.hourHeight = Math.min(this.maxHourHeight, Math.max(this.minHourHeight, this.hourHeight));
    }

    public boolean setHourHeight(int i) {
        updateEventHeight(i);
        if (i == this.hourHeight || i < this.minHourHeight || i > this.maxHourHeight) {
            return false;
        }
        this.hourHeight = i;
        this.needRelayout = true;
        requestLayout();
        return true;
    }

    public int getHourHeight() {
        return this.hourHeight;
    }

    public static int getDefaultHourHeight(View view) {
        return (int) view.getResources().getDimension(R.dimen.hour_height);
    }

    public void update(Time time) {
        this.day = new SafeTime(time);
        updateEvents();
        this.needRelayout = true;
        if (!this.isDayView) {
            if (Utils.isDayEvenInWeek(getContext(), this.day)) {
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.calendar_alternate_day_background));
            } else {
                ContextCompat.getColor(getContext(), android.R.color.transparent);
            }
        }
        requestLayout();
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

    @Override // com.sonymobile.calendar.IDayColumnView
    public void goToCreateEventScreen() {
        this.eventNavigator.goToCreateEvent(Utils.convertDayToMidnight(this.day).toMillis(false) + (((long) getHour(this.latestTouchY)) * 3600000), false);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawTimeOverlay(canvas);
        this.paint.setStrokeWidth(GRID_LINE_WIDTH);
        drawHourLines(canvas);
        if (this.isDayView) {
            drawSideLine(canvas);
        }
        super.onDraw(canvas);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        boolean zDrawChild = super.drawChild(canvas, view, j);
        drawTimeOverlay(canvas);
        return zDrawChild;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            getChildAt(i3).measure(i, i2);
        }
        setMeasuredDimension(this.columnWidth, this.hourHeight * 24);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.needRelayout) {
            CalendarAddEventView calendarAddEventView = this.addEventView;
            if (calendarAddEventView != null) {
                int hourToShowOn = calendarAddEventView.getHourToShowOn();
                int i5 = this.hourHeight;
                int i6 = hourToShowOn * i5;
                this.addEventView.layout(0, i6, i3 - i, i5 + i6);
            }
            int i7 = i3 - i;
            CalendarEventView[] calendarEventViewArr = this.events;
            if (calendarEventViewArr != null) {
                for (CalendarEventView calendarEventView : calendarEventViewArr) {
                    layoutEvent(calendarEventView, i7);
                }
            }
            CalendarEventView calendarEventView2 = this.tempEvent;
            if (calendarEventView2 != null) {
                layoutEvent(calendarEventView2, i7);
            }
            this.needRelayout = false;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.tapDetector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    public boolean prepareForTempEvent(EventInfo eventInfo) {
        this.tempEventId = eventInfo.id;
        hideEvent(eventInfo.id);
        return eventInfo.allDay != 1 && isToday(eventInfo.localBegin);
    }

    public void addTempEvent(EventInfo eventInfo) {
        CalendarEventDayView calendarEventDayView = new CalendarEventDayView(getContext(), eventInfo, this.eventNavigator, this.minHourHeight, this.maxHourHeight);
        this.tempEvent = calendarEventDayView;
        addView(calendarEventDayView);
        this.needRelayout = true;
        CalendarEventView[] calendarEventViewArr = this.events;
        if (calendarEventViewArr != null) {
            computeCluster(this.tempEvent, calendarEventViewArr.length);
        }
        if (this.tempEvent.getClusterIndex() != -1) {
            this.clusterList.get(this.tempEvent.getClusterIndex()).optimize(this.isR2L);
        }
    }

    public void removeTempEvent() {
        CalendarEventView calendarEventView = this.tempEvent;
        if (calendarEventView == null) {
            return;
        }
        if (calendarEventView.getClusterIndex() != -1) {
            this.clusterList.get(this.tempEvent.getClusterIndex()).remove(this.tempEvent);
            this.clusterList.get(this.tempEvent.getClusterIndex()).optimize(this.isR2L);
        }
        removeView(this.tempEvent);
        this.tempEvent = null;
    }

    public boolean isToday(long j) {
        if (this.day == null) {
            return false;
        }
        Time time = new SafeTime(this.day.timezone);
        time.set(j);
        time.normalize(false);
        return Utils.areDatesEqual(this.day, time);
    }

    private void initMeasures() {
        this.hourHeight = (int) (getDefaultHourHeight(this) * Utils.getSystemScale(getContext()));
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setStrokeWidth(GRID_LINE_WIDTH);
        this.paint.setAntiAlias(true);
    }

    private void drawHourLines(Canvas canvas) {
        this.paint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
        int i = this.hourHeight;
        for (int i2 = 0; i2 < 23; i2++) {
            float f = i;
            canvas.drawLine(0.0f, f, canvas.getWidth(), f, this.paint);
            i += this.hourHeight;
        }
    }

    private void drawSideLine(Canvas canvas) {
        this.paint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
        float f = this.isR2L ? this.columnWidth - GRID_LINE_WIDTH : 0.0f;
        canvas.drawLine(f, 0.0f, f, canvas.getHeight(), this.paint);
    }

    private void drawTimeOverlay(Canvas canvas) {
        if (this.day == null) {
            return;
        }
        Time time = new SafeTime(this.day);
        time.set(System.currentTimeMillis());
        long millis = time.toMillis(true) - Utils.convertDayToMidnight(this.day).toMillis(false);
        if (millis > 86400000) {
            drawPastTimeOverlay(time, millis, canvas);
        } else if (millis > 0) {
            drawTodayTimeOverlay(millis, canvas);
        }
    }

    private void drawPastTimeOverlay(Time time, long j, Canvas canvas) {
        if (this.isDayView) {
            return;
        }
        this.paint.setStrokeWidth(getResources().getDimension(R.dimen.time_line_width));
        this.paint.setColor(UiUtils.getAccentColor(getContext()));
        float timeDiffForToday = (getTimeDiffForToday(time) / 8.64E7f) * getHeight();
        canvas.drawLine(0.0f, timeDiffForToday, getWidth(), timeDiffForToday, this.paint);
    }

    private void drawTodayTimeOverlay(long j, Canvas canvas) {
        float height = (j / 8.64E7f) * getHeight();
        this.paint.setStrokeWidth(getResources().getDimension(R.dimen.time_line_width));
        this.paint.setColor(UiUtils.getAccentColor(getContext()));
        canvas.drawLine(0.0f, height, getWidth(), height, this.paint);
    }

    private long getTimeDiffForToday(Time time) {
        Time time2 = new SafeTime(time);
        time2.set(0, 0, 0, time.monthDay, time.month, time.year);
        if (Utils.getWeekNumberOfDay(getContext(), time) != Utils.getWeekNumberOfDay(getContext(), this.day)) {
            return 0L;
        }
        return time.toMillis(true) - time2.toMillis(true);
    }

    private void layoutEvent(CalendarEventView calendarEventView, int i) {
        int bottom;
        if (calendarEventView.getVisibility() == 8) {
            return;
        }
        Time time = new SafeTime(this.day.timezone);
        time.set(calendarEventView.getLocalStartTimeMillis());
        int i2 = (int) (this.hourHeight * ((time.minute / 60.0f) + time.hour));
        time.set(calendarEventView.getEndTimeEventCardInMillis());
        if (Utils.areDatesEqual(time, this.day)) {
            bottom = (int) (this.hourHeight * ((time.minute / 60.0f) + time.hour));
        } else {
            bottom = getBottom();
        }
        int dimension = (int) getResources().getDimension(R.dimen.day_column_event_padding);
        if (this.isDayView) {
            i -= dimension * 2;
        }
        if (calendarEventView.getClusterIndex() != -1) {
            int columnCount = i / this.clusterList.get(calendarEventView.getClusterIndex()).getColumnCount();
            if (this.isDayView) {
                dimension = (calendarEventView.getClusterColumnIndex() * columnCount) + dimension;
            } else {
                dimension = calendarEventView.getClusterColumnIndex() * columnCount;
            }
            i = columnCount + dimension;
        } else if (this.isDayView) {
            i += dimension;
        } else {
            dimension = 0;
        }
        calendarEventView.layout(dimension + 1, i2, i, bottom);
    }

    private void hideEvent(long j) {
        CalendarEventView[] calendarEventViewArr = this.events;
        if (calendarEventViewArr == null || j == -1) {
            return;
        }
        for (CalendarEventView calendarEventView : calendarEventViewArr) {
            if (j == calendarEventView.getEventId()) {
                calendarEventView.setVisibility(8);
            }
        }
    }

    private void computeCluster(CalendarEventView calendarEventView, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (this.events[i2].areOverlapping(calendarEventView.getLocalStartTimeMillis(), calendarEventView.getEndTimeEventCardInMillis())) {
                int clusterIndex = this.events[i2].getClusterIndex();
                if (clusterIndex != -1) {
                    this.clusterList.get(clusterIndex).add(calendarEventView);
                    calendarEventView.setClusterIndex(clusterIndex);
                    return;
                }
                CalendarEventCluster calendarEventCluster = new CalendarEventCluster();
                calendarEventCluster.add(this.events[i2]);
                calendarEventCluster.add(calendarEventView);
                this.clusterList.add(calendarEventCluster);
                int size = this.clusterList.size() - 1;
                this.events[i2].setClusterIndex(size);
                calendarEventView.setClusterIndex(size);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getHour(float f) {
        return (int) (f / this.hourHeight);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r14v1, types: [com.sonymobile.calendar.DayColumnView$2] */
    public void showAddEventView(int i) {
        this.needRelayout = true;
        CalendarAddEventView calendarAddEventView = new CalendarAddEventView(getContext(), i, this.eventNavigator, this.day, false);
        this.addEventView = calendarAddEventView;
        addView(calendarAddEventView);
        this.showAddEventTimer = new CountDownTimer(ADD_EVENT_SHOW_TIME, 1000L) { // from class: com.sonymobile.calendar.DayColumnView.2
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                DayColumnView.this.removeAddEventView();
            }
        }.start();
    }

    private void updateEvents() {
        int i = 0;
        ArrayList<EventInfo> events = EventLoaderService.getInstance().getEvents(Time.getJulianDay(this.day.toMillis(false), this.day.gmtoff));
        if (updateOldAndEvaluate(events)) {
            invalidate();
            return;
        }
        removeAllEventViews();
        if (events.size() > 0) {
            this.events = new CalendarEventView[events.size()];
            if (this.clusterList == null) {
                this.clusterList = new ArrayList<>();
            }
            while (true) {
                CalendarEventView[] calendarEventViewArr = this.events;
                if (i >= calendarEventViewArr.length) {
                    break;
                }
                if (this.extendedLayout) {
                    calendarEventViewArr[i] = new CalendarEventDayView(getContext(), events.get(i), this.eventNavigator, this.minHourHeight, this.maxHourHeight);
                } else {
                    calendarEventViewArr[i] = new CalendarEventWeekView(getContext(), events.get(i), this.eventNavigator, this.minHourHeight, this.maxHourHeight);
                }
                this.events[i].setOnClickListener(this.onEventClickListener);
                addView(this.events[i]);
                computeCluster(this.events[i], i);
                this.events[i].setFocusable(this.isFocusable);
                i++;
            }
            Iterator<CalendarEventCluster> it = this.clusterList.iterator();
            while (it.hasNext()) {
                it.next().optimize(this.isR2L);
            }
            hideEvent(this.tempEventId);
            CalendarEventView calendarEventView = this.tempEvent;
            if (calendarEventView != null && calendarEventView.getClusterIndex() != -1 && !this.clusterList.isEmpty()) {
                computeCluster(this.tempEvent, this.events.length);
                this.clusterList.get(this.tempEvent.getClusterIndex()).optimize(this.isR2L);
            }
            updateEventHeight(this.hourHeight);
        } else {
            invalidate();
        }
        this.needRelayout = true;
    }

    private boolean updateOldAndEvaluate(ArrayList<EventInfo> arrayList) {
        if (this.events == null || arrayList.size() != this.events.length) {
            return false;
        }
        for (EventInfo eventInfo : arrayList) {
            for (CalendarEventView calendarEventView : this.events) {
                if (calendarEventView.update(eventInfo)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeAllEventViews() {
        CalendarEventView[] calendarEventViewArr = this.events;
        if (calendarEventViewArr == null) {
            return;
        }
        for (CalendarEventView calendarEventView : calendarEventViewArr) {
            removeView(calendarEventView);
        }
        this.events = null;
    }

    private void updateEventHeight(int i) {
        CalendarEventView[] calendarEventViewArr = this.events;
        if (calendarEventViewArr != null) {
            for (CalendarEventView calendarEventView : calendarEventViewArr) {
                if (calendarEventView != null) {
                    calendarEventView.setHourHeight(i);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goToEventDetailsScreen(CalendarEventView calendarEventView) {
        Bundle bundleMakeZoomAnimationOnViewBundle = UiUtils.makeZoomAnimationOnViewBundle(calendarEventView);
        EventInfo eventInfo = calendarEventView.getEventInfo();
        if (!eventInfo.isAlarmEvent) {
            this.eventNavigator.goToEventDetails(eventInfo, bundleMakeZoomAnimationOnViewBundle);
        } else {
            EventUtils.startAlarmApp(getContext());
        }
    }

    public void updateFocusability(boolean z) {
        this.isFocusable = z;
        setDescendantFocusability(z ? 262144 : 393216);
        CalendarEventView[] calendarEventViewArr = this.events;
        if (calendarEventViewArr != null) {
            boolean z2 = false;
            for (CalendarEventView calendarEventView : calendarEventViewArr) {
                if (calendarEventView != null) {
                    calendarEventView.setFocusable(z);
                }
            }
            if (z && this.events.length != 0) {
                z2 = true;
            }
            this.isFocusable = z2;
        }
    }

    private class TapDetector extends GestureDetector.SimpleOnGestureListener {
        private TapDetector() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent motionEvent) {
            try {
                DayColumnView.this.latestTouchY = motionEvent.getY();
                return true;
            } catch (IllegalArgumentException unused) {
                return false;
            }
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (DayColumnView.this.day == null) {
                return false;
            }
            if (DayColumnView.this.columnsLayout != null) {
                DayColumnView.this.columnsLayout.onDayClicked(DayColumnView.this);
            }
            DayColumnView dayColumnView = DayColumnView.this;
            dayColumnView.showAddEventView(dayColumnView.getHour(motionEvent.getY()));
            Utils.setDisplayTime(Long.valueOf(DayColumnView.this.day.toMillis(false)));
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            DayColumnView.this.goToCreateEventScreen();
        }
    }
}
