package com.sonymobile.calendar;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.appcompat.app.AppCompatActivity;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.RecycledBitmapCache;
import java.util.Calendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class YearView extends View {
    public static final int CURRENT_YEAR = 1;
    public static final int NEXT_YEAR = 2;
    private static final String PERIOD_SPACE = ". ";
    public static final int PREVIOUS_YEAR = 0;
    private boolean canHandleTouch;
    private float distanceX;
    private GestureDetector gestureDetector;
    private int horizontalFlingThreshold;
    private boolean isR2L;
    private LaunchActivity mActivity;
    private final RecycledBitmapCache mRecycledBitmapCache;
    DrawerStateListener mStateListener;
    private int selectedMonthIndex;
    private int viewWidth;
    private YearFragment yearFragment;
    private YearGrid[] yearGrids;

    static /* synthetic */ float access$116(YearView yearView, float f) {
        float f2 = yearView.distanceX + f;
        yearView.distanceX = f2;
        return f2;
    }

    static /* synthetic */ float access$132(YearView yearView, float f) {
        float f2 = yearView.distanceX * f;
        yearView.distanceX = f2;
        return f2;
    }

    public YearView(YearFragment yearFragment, Navigator navigator) {
        super(yearFragment.getActivity());
        this.horizontalFlingThreshold = 75;
        this.viewWidth = 0;
        this.selectedMonthIndex = -1;
        this.mRecycledBitmapCache = new RecycledBitmapCache();
        this.canHandleTouch = true;
        this.mStateListener = new DrawerStateListener() { // from class: com.sonymobile.calendar.YearView.1
            @Override // com.sonymobile.calendar.DrawerStateListener
            public void onDrawerOpened() {
                YearView.this.animateBack();
            }
        };
        this.yearFragment = yearFragment;
        this.isR2L = CalendarApplication.isR2L(getResources());
        setFocusable(true);
        setClickable(true);
        initScale();
        this.yearGrids = new YearGrid[3];
        initGestureDetector();
        ((LaunchActivity) this.yearFragment.getActivity()).registerDrawerStateListener(this.mStateListener);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        LaunchActivity launchActivity;
        super.onDetachedFromWindow();
        DrawerStateListener drawerStateListener = this.mStateListener;
        if (drawerStateListener != null && (launchActivity = this.mActivity) != null) {
            launchActivity.unregisterDrawerStateListener(drawerStateListener);
        }
        this.mActivity = null;
    }

    public void updateYearGrids(Time time) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0 || time == null) {
            return;
        }
        Time time2 = new SafeTime(time);
        time2.year--;
        for (int i = 0; i <= 2; i++) {
            YearGrid[] yearGridArr = this.yearGrids;
            if (yearGridArr[i] != null) {
                yearGridArr[i].recycle();
            }
            this.yearGrids[i] = new YearGrid(this, width, height, time2, this.yearFragment, this.mRecycledBitmapCache, this.isR2L);
            time2.year++;
        }
        invalidate();
    }

    public void updateYearGrid(Time time, int i) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0 || time == null) {
            return;
        }
        YearGrid[] yearGridArr = this.yearGrids;
        if (yearGridArr[i] != null) {
            yearGridArr[i].recycle();
        }
        this.yearGrids[i] = new YearGrid(this, width, height, time, this.yearFragment, this.mRecycledBitmapCache, this.isR2L);
        invalidate();
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        if (z) {
            YearGrid[] yearGridArr = this.yearGrids;
            if (yearGridArr[1] != null) {
                yearGridArr[1].setSelection(this.selectedMonthIndex, this.isR2L);
                invalidate();
            }
        }
        super.onFocusChanged(z, i, rect);
    }

    public void recycle() {
        for (int i = 0; i <= 2; i++) {
            YearGrid[] yearGridArr = this.yearGrids;
            if (yearGridArr[i] != null) {
                yearGridArr[i].recycle();
            }
        }
    }

    public void sendAccessibilityEvents() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.yearFragment.getActivity().getSystemService("accessibility");
        if (isShown() && accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(32);
            sendAccessibilityEvent(32768);
        }
    }

    public boolean isR2L() {
        return this.isR2L;
    }

    public void setYearGrid(YearGrid yearGrid, int i) {
        if (yearGrid == null) {
            return;
        }
        this.yearGrids[i] = yearGrid;
        if (i == 1) {
            this.selectedMonthIndex = yearGrid.getSelection();
        }
    }

    public YearGrid getYearGrid(int i) {
        return this.yearGrids[i];
    }

    public void setViewWidth(int i) {
        this.viewWidth = i;
    }

    public int getViewWidth() {
        return this.viewWidth;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.viewWidth != i) {
            this.viewWidth = i;
            updateYearGrids(this.yearFragment.getDisplayedDate());
        }
    }

    @Override // android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        int monthAt = this.yearGrids[1].getMonthAt(motionEvent.getX(), motionEvent.getY(), this.isR2L);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2, monthAt);
        setContentDescription(calendar.getDisplayName(2, 2, Locale.getDefault()));
        return super.dispatchHoverEvent(motionEvent);
    }

    public void setCanHandleTouch(boolean z) {
        this.canHandleTouch = z;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.canHandleTouch && !shouldIgnoreForDrawer(motionEvent) && !this.gestureDetector.onTouchEvent(motionEvent)) {
            if (motionEvent.getAction() == 1) {
                boolean z = false;
                if (!this.isR2L ? this.distanceX > 0.0f : this.distanceX < 0.0f) {
                    z = true;
                }
                if (z && this.yearFragment.getDisplayedDate().year >= Utils.EPOCH_YEAR_UPPER_LIMIT) {
                    animateBack();
                } else if (Math.abs(this.distanceX) >= this.horizontalFlingThreshold) {
                    if (z) {
                        this.yearFragment.goToNext(this.distanceX);
                    } else {
                        this.yearFragment.goToPrevious(this.distanceX);
                    }
                    this.distanceX = 0.0f;
                } else if (this.distanceX != 0.0f) {
                    animateBack();
                }
            } else {
                return super.onTouchEvent(motionEvent);
            }
        }
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int i2;
        int i3;
        int i4;
        int i5;
        YearGrid[] yearGridArr = this.yearGrids;
        if (yearGridArr[1] != null) {
            char c = 0;
            if (yearGridArr[0] != null) {
                char c2 = 2;
                if (yearGridArr[2] != null) {
                    if (this.selectedMonthIndex == -1) {
                        this.selectedMonthIndex = 0;
                        yearGridArr[1].setSelection(0, this.isR2L);
                        invalidate();
                        sendAccessibilityEvent(32);
                        return true;
                    }
                    int columnCount = yearGridArr[1].getColumnCount();
                    if (i != 66) {
                        switch (i) {
                            case 19:
                                int i6 = this.selectedMonthIndex - columnCount;
                                if (i6 < 0) {
                                    this.yearGrids[1].setSelection(-1, this.isR2L);
                                    return false;
                                }
                                this.selectedMonthIndex = i6;
                                this.yearGrids[1].setSelection(i6, this.isR2L);
                                invalidate();
                                sendAccessibilityEvent(32);
                                return true;
                            case 20:
                                int i7 = this.selectedMonthIndex + columnCount;
                                if (i7 > 12) {
                                    return false;
                                }
                                this.selectedMonthIndex = i7;
                                this.yearGrids[1].setSelection(i7, this.isR2L);
                                invalidate();
                                sendAccessibilityEvent(32);
                                return true;
                            case 21:
                                boolean z = this.isR2L;
                                if (z) {
                                    int i8 = this.selectedMonthIndex;
                                    i3 = (columnCount - (i8 % columnCount)) - 2;
                                    int i9 = i8 + 1;
                                    this.selectedMonthIndex = i9;
                                    i2 = i9 - columnCount;
                                } else {
                                    int i10 = this.selectedMonthIndex;
                                    int i11 = (i10 % columnCount) - 1;
                                    int i12 = i10 - 1;
                                    this.selectedMonthIndex = i12;
                                    i2 = i12 + columnCount;
                                    c2 = 0;
                                    i3 = i11;
                                }
                                if (i3 < 0) {
                                    this.yearGrids[1].setSelection(-1, z);
                                    this.yearGrids[c2].setSelection(i2, this.isR2L);
                                    if (this.isR2L) {
                                        this.yearFragment.goToNext(0.0f);
                                    } else {
                                        this.yearFragment.goToPrevious(0.0f);
                                    }
                                } else {
                                    this.yearGrids[1].setSelection(this.selectedMonthIndex, z);
                                    invalidate();
                                }
                                sendAccessibilityEvent(32);
                                return true;
                            case 22:
                                boolean z2 = this.isR2L;
                                if (z2) {
                                    int i13 = this.selectedMonthIndex;
                                    i5 = columnCount - (i13 % columnCount);
                                    int i14 = i13 - 1;
                                    this.selectedMonthIndex = i14;
                                    i4 = i14 + columnCount;
                                } else {
                                    int i15 = this.selectedMonthIndex;
                                    int i16 = (i15 % columnCount) + 1;
                                    int i17 = i15 + 1;
                                    this.selectedMonthIndex = i17;
                                    i4 = i17 - columnCount;
                                    i5 = i16;
                                    c = 2;
                                }
                                if (i5 >= columnCount) {
                                    this.yearGrids[1].setSelection(-1, z2);
                                    YearGrid[] yearGridArr2 = this.yearGrids;
                                    if (yearGridArr2[c] != null) {
                                        yearGridArr2[c].setSelection(i4, this.isR2L);
                                    }
                                    if (this.isR2L) {
                                        this.yearFragment.goToPrevious(0.0f);
                                    } else {
                                        this.yearFragment.goToNext(0.0f);
                                    }
                                } else {
                                    this.yearGrids[1].setSelection(this.selectedMonthIndex, z2);
                                    invalidate();
                                }
                                sendAccessibilityEvent(32);
                                return true;
                            case 23:
                                break;
                            default:
                                return super.onKeyDown(i, keyEvent);
                        }
                    }
                    this.yearFragment.goToMonth(this.selectedMonthIndex);
                    sendAccessibilityEvent(32);
                }
            }
        }
        return true;
    }

    private boolean shouldIgnoreForDrawer(MotionEvent motionEvent) {
        boolean z = true;
        byte b = this.distanceX > 0.0f ? (byte) 1 : (byte) -1;
        if (!this.isR2L ? motionEvent.getX() >= ((double) getWidth()) * 0.05d || b == 1 : motionEvent.getX() <= ((double) getWidth()) * 0.95d || b == -1) {
            z = false;
        }
        if (this.distanceX != 0.0f && z) {
            animateBack();
        }
        return z;
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEvent(int i) {
        if (i == 8) {
            return;
        }
        updateContentDescription();
        super.sendAccessibilityEvent(i);
    }

    private void updateContentDescription() {
        String string = getResources().getString(R.string.app_label);
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.yearFragment.getDisplayedDate().year, this.selectedMonthIndex, 1);
        setContentDescription(((Object) string) + PERIOD_SPACE + ((Object) this.yearFragment.getDateString()) + PERIOD_SPACE + calendar.getDisplayName(2, 2, Locale.getDefault()));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.distanceX != 0.0f) {
            drawNextYear(canvas);
        }
        YearGrid[] yearGridArr = this.yearGrids;
        if (yearGridArr[1] != null) {
            yearGridArr[1].draw(canvas);
        }
    }

    private void drawNextYear(Canvas canvas) {
        float f;
        int i;
        int i2;
        float f2;
        char c = 2;
        if (this.isR2L) {
            f = this.distanceX;
            if (f < 0.0f) {
                i2 = -this.viewWidth;
                f2 = i2 - f;
            } else {
                i = this.viewWidth;
                float f3 = i - f;
                c = 0;
                f2 = f3;
            }
        } else {
            f = this.distanceX;
            if (f > 0.0f) {
                i2 = this.viewWidth;
                f2 = i2 - f;
            } else {
                i = -this.viewWidth;
                float f4 = i - f;
                c = 0;
                f2 = f4;
            }
        }
        canvas.save();
        canvas.translate(f2, 0.0f);
        YearGrid[] yearGridArr = this.yearGrids;
        if (yearGridArr[c] != null) {
            yearGridArr[c].draw(canvas);
        }
        canvas.restore();
        canvas.save();
        canvas.translate(-this.distanceX, 0.0f);
    }

    private void initScale() {
        float f = getContext().getResources().getDisplayMetrics().density;
        if (f != 1.0f) {
            this.horizontalFlingThreshold = (int) (this.horizontalFlingThreshold * f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateBack() {
        new CountDownTimer(400L, 10L) { // from class: com.sonymobile.calendar.YearView.2
            @Override // android.os.CountDownTimer
            public void onFinish() {
                YearView.this.distanceX = 0.0f;
                YearView.this.invalidate();
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                YearView.access$132(YearView.this, 0.8f);
                YearView.this.invalidate();
            }
        }.start();
    }

    private void initGestureDetector() {
        this.gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: com.sonymobile.calendar.YearView.3
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                YearView.this.distanceX = 0.0f;
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                YearView.access$116(YearView.this, f);
                YearView yearView = YearView.this;
                yearView.distanceX = Math.max(Math.min(yearView.distanceX, YearView.this.getWidth()), -YearView.this.getWidth());
                YearView.this.invalidate();
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                YearView.this.yearFragment.goToMonth(YearView.this.yearGrids[1].getMonthAt(motionEvent.getX(), motionEvent.getY(), YearView.this.isR2L));
                return true;
            }
        });
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        PermissionUtils.reportFullyDrawnIfPermitted((AppCompatActivity) getContext());
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mActivity = (LaunchActivity) this.yearFragment.getActivity();
    }
}
