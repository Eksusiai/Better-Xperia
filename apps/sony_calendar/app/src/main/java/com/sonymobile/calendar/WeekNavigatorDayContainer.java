package com.sonymobile.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.tablet.TabletWeekNavigatorDayView;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class WeekNavigatorDayContainer extends LinearLayout implements ICalendarColumnContainer {
    private static final String TAG = "WeekNavigatorDayContainer";
    private Activity mActivity;
    protected int mAlpha;
    protected Paint mBorderPaint;
    protected WeekNavigatorDayView[] mDayBoxes;
    private Time[] mDisplayedDates;
    private final IAsyncServiceResultHandler mEventResultHandler;
    protected boolean mIsTablet;
    protected boolean mIsWeekView;
    protected int mSelectedIndex;
    protected long mTimeStep;
    private int mTodayIndex;
    protected FocusedViewNavigator mViewNavigator;
    protected int mWasSelectedIndex;

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void blockRelayout() {
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void reloadEvents() {
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void removeAddEventView() {
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public boolean setHourHeight(int i) {
        return false;
    }

    public WeekNavigatorDayContainer(Context context, int i, boolean z, FocusedViewNavigator focusedViewNavigator, boolean z2) {
        super(context);
        this.mSelectedIndex = -1;
        this.mWasSelectedIndex = -1;
        this.mTodayIndex = -1;
        this.mAlpha = 0;
        this.mEventResultHandler = new IAsyncServiceResultHandler() { // from class: com.sonymobile.calendar.WeekNavigatorDayContainer.1
            @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
            public void onResult(Object obj, Object obj2) {
                int iMin = Math.min(WeekNavigatorDayContainer.this.mDayBoxes.length, WeekNavigatorDayContainer.this.mDisplayedDates.length);
                for (int i2 = 0; i2 < iMin; i2++) {
                    WeekNavigatorDayContainer.this.mDayBoxes[i2].update(WeekNavigatorDayContainer.this.mDisplayedDates[i2], true);
                }
            }
        };
        this.mActivity = (Activity) context;
        initPaint();
        setWillNotDraw(false);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        this.mViewNavigator = focusedViewNavigator;
        this.mIsWeekView = z2;
        initDays(i, z);
        this.mIsTablet = Utils.isTabletDevice(getContext());
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.mBorderPaint = paint;
        paint.setColor(ContextCompat.getColor(getContext(), R.color.focused_border_color));
        this.mBorderPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.focused_border_width));
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override // android.view.View, com.sonymobile.calendar.ICalendarColumnContainer
    public void invalidate() {
        super.invalidate();
        for (WeekNavigatorDayView weekNavigatorDayView : this.mDayBoxes) {
            weekNavigatorDayView.invalidate();
        }
    }

    protected class ViewInvalidator implements Runnable {
        protected ViewInvalidator() {
        }

        @Override // java.lang.Runnable
        public void run() {
            while (WeekNavigatorDayContainer.this.mSelectedIndex != -1 && WeekNavigatorDayContainer.this.mAlpha < 255) {
                try {
                    Thread.sleep(16L);
                } catch (InterruptedException unused) {
                    Log.w(WeekNavigatorDayContainer.TAG, "Thread interrupted");
                }
                long jNanoTime = System.nanoTime();
                long j = jNanoTime - WeekNavigatorDayContainer.this.mTimeStep;
                WeekNavigatorDayContainer.this.mTimeStep = jNanoTime;
                WeekNavigatorDayContainer weekNavigatorDayContainer = WeekNavigatorDayContainer.this;
                weekNavigatorDayContainer.mAlpha = Math.min(weekNavigatorDayContainer.mAlpha + ((int) (j * 1.2E-6d)), 255);
                WeekNavigatorDayContainer.this.mActivity.runOnUiThread(new Runnable() { // from class: com.sonymobile.calendar.WeekNavigatorDayContainer.ViewInvalidator.1
                    @Override // java.lang.Runnable
                    public void run() {
                        WeekNavigatorDayContainer.this.invalidate();
                    }
                });
            }
        }
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void setViewPortSize(int i, int i2, boolean z) {
        WeekNavigatorDayView[] weekNavigatorDayViewArr = this.mDayBoxes;
        float length = i / weekNavigatorDayViewArr.length;
        float f = 0.0f;
        int i3 = 0;
        for (WeekNavigatorDayView weekNavigatorDayView : weekNavigatorDayViewArr) {
            f += length;
            int iRound = Math.round(f) - i3;
            weekNavigatorDayView.setBoxWidth(iRound);
            i3 += iRound;
        }
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void updateView(Time[] timeArr, boolean z) {
        this.mDisplayedDates = (Time[]) timeArr.clone();
        this.mWasSelectedIndex = this.mSelectedIndex;
        this.mTodayIndex = -1;
        this.mSelectedIndex = -1;
        this.mAlpha = 0;
        EventLoaderService eventLoaderService = EventLoaderService.getInstance();
        Context context = getContext();
        Time[] timeArr2 = this.mDisplayedDates;
        eventLoaderService.requestLoad(context, timeArr2[0], timeArr2[timeArr.length - 1], this.mEventResultHandler, z);
    }

    public void updateFocusability(boolean z) {
        setFocusable(z);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.mIsTablet) {
            getWidth();
            int length = this.mDayBoxes.length;
            for (int i = 0; i < this.mDayBoxes.length; i++) {
            }
        }
        int i2 = this.mSelectedIndex;
        if (i2 != -1) {
            UiUtils.drawMarkerWeekDayNavigation(this.mActivity, canvas, this.mDayBoxes[i2], this.mIsTablet, false, this.mAlpha);
        }
        int i3 = this.mWasSelectedIndex;
        if (i3 != -1) {
            UiUtils.drawMarkerWeekDayNavigation(this.mActivity, canvas, this.mDayBoxes[i3], this.mIsTablet, false, Utils.interpolate(255 - this.mAlpha));
        }
        int i4 = this.mTodayIndex;
        if (i4 != -1) {
            UiUtils.drawMarkerWeekDayNavigation(this.mActivity, canvas, this.mDayBoxes[i4], this.mIsTablet, true, this.mAlpha);
        }
        if ((this instanceof DayNavigatorDayContainer) || !hasFocus()) {
            return;
        }
        canvas.drawRect(0.0f, this.mDayBoxes[0].getBoxTop() + 3, getWidth(), this.mDayBoxes[0].getBottom() - 1, this.mBorderPaint);
    }

    protected void initDays(int i, boolean z) {
        WeekNavigatorDayView[] weekNavigatorDayViewArr;
        boolean z2 = this.mActivity.getResources().getBoolean(R.bool.tablet_mode);
        if (z2) {
            weekNavigatorDayViewArr = new TabletWeekNavigatorDayView[i];
        } else {
            weekNavigatorDayViewArr = new WeekNavigatorDayView[i];
        }
        this.mDayBoxes = weekNavigatorDayViewArr;
        for (int i2 = 0; i2 < i; i2++) {
            this.mDayBoxes[i2] = z2 ? new TabletWeekNavigatorDayView(getContext(), z) : new WeekNavigatorDayView(getContext(), z, true);
            addView(this.mDayBoxes[i2]);
        }
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (hasFocus()) {
            if (i == 21) {
                FocusedViewNavigator focusedViewNavigator = this.mViewNavigator;
                if (focusedViewNavigator != null) {
                    focusedViewNavigator.goToPreviousView();
                }
                return false;
            }
            if (i == 22) {
                FocusedViewNavigator focusedViewNavigator2 = this.mViewNavigator;
                if (focusedViewNavigator2 != null) {
                    focusedViewNavigator2.goToNextView();
                }
                return false;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }
}
