package com.sonymobile.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarScrollView extends ScrollView {
    public int name;

    public CalendarScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        BindSVManager.dispatch(motionEvent, this.name);
        return true;
    }

    public boolean callTouch(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent);
    }
}
