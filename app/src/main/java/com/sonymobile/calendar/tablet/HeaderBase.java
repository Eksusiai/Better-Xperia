package com.sonymobile.calendar.tablet;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.AppCompatTextView;
import com.sonymobile.calendar.OnDateSwitcherClickedListener;

/* JADX INFO: loaded from: classes2.dex */
public abstract class HeaderBase extends AppCompatTextView implements View.OnClickListener {
    protected Time displayedTime;
    private OnDateSwitcherClickedListener onDateSwitcherClickedListener;

    protected abstract String getText(Context context, Time time);

    public HeaderBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnClickListener(this);
        setFocusable(true);
        setClickable(true);
    }

    public void update(Context context, Time time) {
        if (time == null) {
            return;
        }
        this.displayedTime = new SafeTime(time);
    }

    public void setOnDateSwitcherClickedListener(OnDateSwitcherClickedListener onDateSwitcherClickedListener) {
        this.onDateSwitcherClickedListener = onDateSwitcherClickedListener;
        setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        OnDateSwitcherClickedListener onDateSwitcherClickedListener = this.onDateSwitcherClickedListener;
        if (onDateSwitcherClickedListener != null) {
            onDateSwitcherClickedListener.onDateLabelClicked();
        }
    }
}
