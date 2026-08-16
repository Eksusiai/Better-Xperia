package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.graphics.Canvas;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import com.sonymobile.calendar.FocusedViewNavigator;
import com.sonymobile.calendar.NavigatorDaySelectedListener;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.ViewIndex;

/* JADX INFO: loaded from: classes2.dex */
public class TabletDayNavigatorDayView extends TabletWeekNavigatorDayView {
    private int columnIndex;
    private ViewIndex index;
    private boolean isR2L;
    private boolean isSelected;
    private NavigatorDaySelectedListener selectedListener;
    private FocusedViewNavigator viewNavigator;

    public TabletDayNavigatorDayView(Context context, boolean z, ViewIndex viewIndex, FocusedViewNavigator focusedViewNavigator) {
        super(context, z);
        this.isSelected = false;
        this.isR2L = z;
        this.index = viewIndex;
        this.viewNavigator = focusedViewNavigator;
        setFocusable(true);
    }

    @Override // com.sonymobile.calendar.tablet.TabletWeekNavigatorDayView
    public void setNavigatorDayClickedListener(NavigatorDaySelectedListener navigatorDaySelectedListener, int i) {
        this.selectedListener = navigatorDaySelectedListener;
        this.columnIndex = i;
    }

    public void setIsSelected(boolean z, boolean z2) {
        NavigatorDaySelectedListener navigatorDaySelectedListener;
        this.isSelected = z;
        if (!z || (navigatorDaySelectedListener = this.selectedListener) == null) {
            return;
        }
        navigatorDaySelectedListener.onDaySelected(this.day, this.columnIndex, z2);
        if (z2) {
            requestFocus();
        }
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView
    public boolean update(Time time, boolean z) {
        boolean zUpdate = super.update(time, z);
        long millis = this.day.toMillis(false) - Utils.getDisplayTime();
        setIsSelected(millis > -86400000 && millis <= 0, false);
        return zUpdate;
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView, android.view.View.OnClickListener
    public void onClick(View view) {
        setIsSelected(true, true);
        requestFocus();
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView
    protected boolean isDaySelected() {
        return this.isSelected;
    }

    @Override // com.sonymobile.calendar.tablet.TabletWeekNavigatorDayView, com.sonymobile.calendar.WeekNavigatorDayView
    protected void drawBox(Canvas canvas, int i) {
        if (this.isSelected) {
            i = 0;
        }
        super.drawBox(canvas, i);
    }

    @Override // com.sonymobile.calendar.tablet.TabletWeekNavigatorDayView, com.sonymobile.calendar.WeekNavigatorDayView
    protected void drawDayNumber(Canvas canvas, int i, int i2) {
        if (this.isSelected) {
            i = -1;
        }
        super.drawDayNumber(canvas, i, i2);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (hasFocus()) {
            if (i == 21) {
                if (this.index == ViewIndex.FIRST) {
                    if (this.isR2L) {
                        goToNextView();
                    } else {
                        goToPreviousView();
                    }
                }
                return false;
            }
            if (i == 22) {
                if (this.index == ViewIndex.LAST) {
                    if (this.isR2L) {
                        goToPreviousView();
                    } else {
                        goToNextView();
                    }
                }
                return false;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    private void goToNextView() {
        FocusedViewNavigator focusedViewNavigator = this.viewNavigator;
        if (focusedViewNavigator != null) {
            focusedViewNavigator.goToNextView();
        }
    }

    private void goToPreviousView() {
        FocusedViewNavigator focusedViewNavigator = this.viewNavigator;
        if (focusedViewNavigator != null) {
            focusedViewNavigator.goToPreviousView();
        }
    }
}
