package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public abstract class AllDayEventViewBase extends LinearLayout implements View.OnCreateContextMenuListener {
    private Paint borderPaint;
    protected Time day;
    private ViewIndex index;
    protected TextView mTitleView;
    protected boolean showFullEventText;

    protected abstract Object getNavigationInfo();

    protected abstract String getTitleText();

    protected abstract void initContentDescription();

    protected abstract void initIcon();

    public AllDayEventViewBase(Context context, Time time, boolean z, ViewIndex viewIndex) {
        super(context);
        this.day = time;
        this.showFullEventText = z;
        this.index = viewIndex;
    }

    protected void init(int i) {
        setOnCreateContextMenuListener(this);
        initLayout(i);
        initContentDescription();
        initDrawingResources();
    }

    private void initDrawingResources() {
        Paint paint = new Paint();
        this.borderPaint = paint;
        paint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        long jNormalize = this.day.normalize(false);
        if (contextMenu.size() == 0) {
            CalendarContextMenuHelper.setTitle(getContext(), contextMenu, jNormalize, true);
        }
    }

    protected void initLayout(int i) {
        LayoutInflater.from(getContext()).inflate(R.layout.all_day_event_item, this);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        boolean z = i == -1;
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.day_week_event_background));
        getBackground().setColorFilter(UiUtils.getDisplayColorFromColor(i), PorterDuff.Mode.MULTIPLY);
        setClickable(true);
        initTextView(z);
    }

    protected void initTextView(boolean z) {
        TextView textView = (TextView) findViewById(R.id.event_title);
        this.mTitleView = textView;
        textView.setText(getTitleText());
        if (!z) {
            this.mTitleView.setTextColor(getResources().getColor(R.color.white));
        } else {
            this.mTitleView.setTextColor(getResources().getColor(R.color.all_day_event_text_color));
        }
        this.mTitleView.setEllipsize(null);
    }

    public void setTitleSingleLine(boolean z) {
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setSingleLine(z);
        }
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 21) {
            if (this.index == ViewIndex.FIRST) {
                return true;
            }
        } else if (i == 22 && this.index == ViewIndex.LAST) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }
}
