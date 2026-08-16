package com.sonymobile.calendar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarSwipeView extends HorizontalScrollView {
    public static final long FULL_SWIPE_ANIMATION_DURATION = 300;
    private static final float SWIPE_THRESHOLD = 0.18f;
    private static final String TAG = "CalendarSwipeView";
    private boolean blockScrollBy;
    private boolean canHandleTouch;
    private boolean hasUsedTouch;
    private boolean isInEditEvent;
    private boolean isR2L;
    private boolean isSwipingFast;
    private Navigator navigator;
    private CalendarSwipeListener swipeListener;
    private float swipeThreshold;

    public CalendarSwipeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isR2L = false;
        this.isInEditEvent = false;
        this.canHandleTouch = true;
        this.blockScrollBy = false;
        this.isSwipingFast = false;
        setFocusable(false);
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public void setCalendarSwipeListener(CalendarSwipeListener calendarSwipeListener) {
        this.swipeListener = calendarSwipeListener;
    }

    public void setIsR2L(boolean z) {
        this.isR2L = z;
    }

    public void setIsInEditEvent(boolean z) {
        this.isInEditEvent = z;
    }

    @Override // android.view.View
    protected boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        if (!this.hasUsedTouch && !z) {
            return false;
        }
        this.hasUsedTouch = true;
        return super.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, z);
    }

    public void scrollToCenterView() {
        scrollTo(getWidth(), 0);
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        initMeasures();
    }

    public void animateSwipe(boolean z, final SwipeAnimationDoneHandler swipeAnimationDoneHandler) {
        ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(this, "scrollX", getScrollX(), ((!z || this.isR2L) && (z || !this.isR2L)) ? 0 : getWidth() * 2);
        objectAnimatorOfInt.setDuration(300L);
        objectAnimatorOfInt.setInterpolator(new DecelerateInterpolator());
        objectAnimatorOfInt.setAutoCancel(true);
        objectAnimatorOfInt.addListener(new Animator.AnimatorListener() { // from class: com.sonymobile.calendar.CalendarSwipeView.1
            private boolean isCancelled = false;

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                CalendarSwipeView.this.canHandleTouch = false;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                CalendarSwipeView.this.canHandleTouch = true;
                if (this.isCancelled) {
                    return;
                }
                swipeAnimationDoneHandler.onSwipeAnimationDone(CalendarSwipeView.this.isSwipingFast);
                CalendarSwipeView.this.isSwipingFast = false;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                CalendarSwipeView.this.isSwipingFast = true;
                this.isCancelled = true;
            }
        });
        objectAnimatorOfInt.start();
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.isInEditEvent || super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.canHandleTouch) {
            return true;
        }
        if (event.getPointerCount() > 1) {
            return false;
        }
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return super.onTouchEvent(event);
        }
        float f = (float) (getWidth() - getScrollX());
        if (Math.abs(f) > this.swipeThreshold && this.navigator != null) {
            boolean z;
            if (this.isR2L) {
                z = f > 0.0f;
            } else {
                z = f < 0.0f;
            }
            if (z) {
                this.navigator.goToNext(f);
            } else {
                this.navigator.goToPrevious(f);
            }
        } else if (f != 0.0f) {
            animateBack();
        }
        return true;
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        CalendarSwipeListener calendarSwipeListener;
        CalendarSwipeListener calendarSwipeListener2 = this.swipeListener;
        if (calendarSwipeListener2 != null) {
            calendarSwipeListener2.onSwipe(getScrollX());
        }
        if (getWidth() == i && (calendarSwipeListener = this.swipeListener) != null) {
            calendarSwipeListener.onSwipeCentered();
        }
        super.onScrollChanged(i, i2, i3, i4);
    }

    private void initMeasures() {
        this.swipeThreshold = getWidth() * SWIPE_THRESHOLD;
    }

    public void animateBack() {
        smoothScrollTo(getWidth(), 0);
    }

    @Override // android.widget.HorizontalScrollView
    public boolean arrowScroll(int i) {
        View viewFindFocus = findFocus();
        if (viewFindFocus == null) {
            return false;
        }
        try {
            View viewFindNextFocus = FocusFinder.getInstance().findNextFocus(this, viewFindFocus, i);
            if (viewFindNextFocus != null) {
                return !viewFindNextFocus.isFocusable();
            }
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public void blockScrollBy() {
        this.blockScrollBy = true;
    }

    @Override // android.view.View
    public void scrollBy(int i, int i2) {
        if (this.blockScrollBy) {
            this.blockScrollBy = false;
        } else {
            super.scrollBy(i, i2);
        }
    }
}
