package com.sonymobile.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

/* JADX INFO: loaded from: classes2.dex */
public class AgendaPager extends ViewPager {
    private boolean mIsSwipeEnabled;

    public AgendaPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsSwipeEnabled = true;
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mIsSwipeEnabled) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mIsSwipeEnabled) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    public void setSwipeEnabled(boolean z) {
        this.mIsSwipeEnabled = z;
    }
}
