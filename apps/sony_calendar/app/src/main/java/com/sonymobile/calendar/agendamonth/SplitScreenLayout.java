package com.sonymobile.calendar.agendamonth;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.google.common.primitives.Ints;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class SplitScreenLayout extends FrameLayout {
    private static final float DEFAULT_POS_LAND = 0.43f;
    private static final float DEFAULT_POS_PORT = 0.47f;
    private static final int MAX_NBR_OF_CHILDREN = 2;
    public static final int MODE_OFF = 0;
    public static final int MODE_ON = 1;
    public static final boolean SWAP_TOP_BOTTOM = true;
    private boolean SWAP_LEFT_RIGHT;
    private float defaultPositionPortrait;
    private Callback mCallback;
    private float mDefaultPos;
    private int mDividerThickness;
    private boolean mEnabled;
    private int mMode;
    private boolean mPendingRelayout;
    private boolean mRedrawRequested;
    private float mSplitPos;
    private int mState;

    public interface Callback {
        public static final int STATE_BOTH = 0;
        public static final int STATE_LEFT_ONLY = -1;
        public static final int STATE_RIGHT_ONLY = 1;

        void onSplitStateChanged(int i);
    }

    private int calcState(float f) {
        if (f == 1.0f) {
            return -1;
        }
        return f == 0.0f ? 1 : 0;
    }

    private float swapV(float f) {
        return 1.0f - f;
    }

    private int swapV(int i) {
        return 1 - i;
    }

    public SplitScreenLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.SWAP_LEFT_RIGHT = false;
        this.mState = -1;
        this.mSplitPos = 0.0f;
        this.mDividerThickness = 0;
        this.mMode = 0;
        this.mEnabled = true;
        setState(0);
        if (!isHorizontal()) {
            setMode(1);
        }
        initMeasures(attributeSet);
    }

    public boolean isSplitScreenEnabled() {
        return !isHorizontal();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public float getSplitPosition() {
        return this.mSplitPos;
    }

    public float getDefaultSplitPosition() {
        return this.mDefaultPos;
    }

    public void reLayoutSplit() {
        reLayoutSplit(this.mDefaultPos);
    }

    public void reLayoutSplit(float f) {
        this.mSplitPos = f;
        requestLayout();
        relayout();
    }

    private void setState(int i) {
        if (i != this.mState) {
            this.mState = i;
            Callback callback = this.mCallback;
            if (callback != null) {
                callback.onSplitStateChanged(i);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setupVisibility();
    }

    protected void setupVisibility() {
        if (getChildCount() != 2) {
            return;
        }
        if (isSplitScreenEnabled() && this.mEnabled) {
            getChildAt(0).setVisibility(0);
            getChildAt(1).setVisibility(0);
        } else {
            getChildAt(0).setVisibility(8);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int i;
        boolean zDispatchKeyEvent = super.dispatchKeyEvent(keyEvent);
        if (getChildCount() == 2 && !zDispatchKeyEvent && keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            View childAt = null;
            if (keyCode == 21) {
                childAt = getChildAt(0);
                i = 17;
            } else if (keyCode == 22) {
                childAt = getChildAt(1);
                i = 66;
            } else {
                i = -1;
            }
            if (childAt != null && i != -1 && (findFocus() == null || findFocus().focusSearch(i) == null)) {
                childAt.requestFocus(i);
            }
        }
        return zDispatchKeyEvent;
    }

    public boolean isHorizontal() {
        return !UiUtils.isSub320dpScreen(getContext()) && getContext().getResources().getConfiguration().orientation == 2;
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0062 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:18:0x0064  */
    /* JADX WARN: Code duplicated, block: B:19:0x0069  */
    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (!isSplitScreenEnabled() || !this.mEnabled) {
            super.onMeasure(i, i2);
            return;
        }
        int size;
        float fSwapV;
        int dimensionPixelSize;
        boolean zIsHorizontal = isHorizontal();
        if (zIsHorizontal) {
            size = View.MeasureSpec.getSize(i) - this.mDividerThickness;
            fSwapV = swapH(this.mSplitPos);
            dimensionPixelSize = (int) (fSwapV * size);
        } else {
            size = View.MeasureSpec.getSize(i2) - this.mDividerThickness;
            if (UiUtils.isSub320dpScreen(getContext())) {
                dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.month_view_multi_window_split_size);
            } else {
                fSwapV = swapV(this.mSplitPos);
                dimensionPixelSize = (int) (fSwapV * size);
            }
        }
        setState(calcState(this.mSplitPos));
        int iMin = Math.min(getChildCount(), 2);
        int i3 = 0;
        while (i3 < iMin) {
            int iSwapV = zIsHorizontal ? swapH(i3) : swapV(i3);
            View childAt = getChildAt(iSwapV);
            if (childAt != null && childAt.getVisibility() != 8) {
                int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3 == 0 ? dimensionPixelSize : size - dimensionPixelSize, Ints.MAX_POWER_OF_TWO);
                if (zIsHorizontal) {
                    measureChild(childAt, iMakeMeasureSpec, i2);
                } else {
                    measureChild(childAt, i, iMakeMeasureSpec);
                }
            }
            i3++;
        }
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0054 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:18:0x0056  */
    /* JADX WARN: Code duplicated, block: B:19:0x005b  */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float fSwapV;
        int height;
        int i5;
        int dimensionPixelSize;
        if (!isSplitScreenEnabled() || !this.mEnabled) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        boolean zIsHorizontal = isHorizontal();
        if (zIsHorizontal) {
            fSwapV = swapH(this.mSplitPos);
            height = getWidth();
            i5 = this.mDividerThickness;
            dimensionPixelSize = (int) (fSwapV * (height - i5));
        } else {
            if (UiUtils.isSub320dpScreen(getContext())) {
                dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.month_view_multi_window_split_size);
            } else {
                fSwapV = swapV(this.mSplitPos);
                height = getHeight();
                i5 = this.mDividerThickness;
                dimensionPixelSize = (int) (fSwapV * (height - i5));
            }
        }
        int iMin = Math.min(getChildCount(), 2);
        int i8 = 0;
        int i9 = 0;
        for (int i6 = 0; i6 < iMin; i6++) {
            int iSwapV = zIsHorizontal ? swapH(i6) : swapV(i6);
            View childAt = getChildAt(iSwapV);
            if (childAt != null && childAt.getVisibility() != 8) {
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                if (zIsHorizontal) {
                    measuredWidth = Math.min(dimensionPixelSize, measuredWidth);
                } else {
                    measuredHeight = Math.min(dimensionPixelSize, measuredHeight);
                }
                childAt.layout(i8, i9, measuredWidth + i8, measuredHeight + i9);
                if (zIsHorizontal) {
                    i8 += this.mDividerThickness + dimensionPixelSize;
                } else {
                    i9 += this.mDividerThickness + dimensionPixelSize;
                }
            }
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        this.mRedrawRequested = false;
        if (this.mPendingRelayout) {
            this.mRedrawRequested = true;
            requestLayout();
            invalidate();
            this.mPendingRelayout = false;
        }
        super.draw(canvas);
        isSplitScreenEnabled();
    }

    public void doConfigurationChanged(Configuration configuration) {
        this.mDefaultPos = isHorizontal() ? DEFAULT_POS_LAND : this.defaultPositionPortrait;
        setupVisibility();
    }

    private void relayout() {
        if (this.mRedrawRequested) {
            this.mPendingRelayout = true;
            return;
        }
        this.mRedrawRequested = true;
        requestLayout();
        invalidate();
    }

    private void initMeasures(AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.SplitScreenLayout);
        this.defaultPositionPortrait = typedArrayObtainStyledAttributes.getFloat(0, DEFAULT_POS_PORT);
        typedArrayObtainStyledAttributes.recycle();
        float f = isHorizontal() ? DEFAULT_POS_LAND : this.defaultPositionPortrait;
        this.mDefaultPos = f;
        this.mSplitPos = f;
        this.SWAP_LEFT_RIGHT = !needMirror();
    }

    private void setMode(int i) {
        this.mMode = i;
        setupVisibility();
    }

    private float swapH(float f) {
        return this.SWAP_LEFT_RIGHT ? 1.0f - f : f;
    }

    private int swapH(int i) {
        return this.SWAP_LEFT_RIGHT ? 1 - i : i;
    }

    private boolean needMirror() {
        Locale locale = getResources().getConfiguration().locale;
        return locale.toString().equals("fa_IR") || locale.toString().equals("iw_IL") || locale.toString().equals("ar_EG") || locale.toString().equals("ar_IL");
    }
}
