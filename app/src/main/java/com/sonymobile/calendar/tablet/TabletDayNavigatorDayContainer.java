package com.sonymobile.calendar.tablet;

import android.content.Context;
import com.sonymobile.calendar.DayNavigatorDayContainer;
import com.sonymobile.calendar.FocusedViewNavigator;

/* JADX INFO: loaded from: classes2.dex */
public class TabletDayNavigatorDayContainer extends DayNavigatorDayContainer {
    public TabletDayNavigatorDayContainer(Context context, int i, boolean z, FocusedViewNavigator focusedViewNavigator) {
        super(context, i, z, focusedViewNavigator);
    }

    @Override // com.sonymobile.calendar.DayNavigatorDayContainer
    public boolean selectNextDay(boolean z) {
        if (this.mSelectedIndex != -1) {
            ((TabletDayNavigatorDayView) this.mDayBoxes[this.mSelectedIndex]).setIsSelected(false, false);
        } else if (!z) {
            this.mSelectedIndex = this.mDayBoxes.length;
        }
        this.mSelectedIndex += z ? 1 : -1;
        if (this.mSelectedIndex < 0 || this.mSelectedIndex >= this.mDayBoxes.length) {
            this.mSelectedIndex = -1;
            invalidate();
            return false;
        }
        ((TabletDayNavigatorDayView) this.mDayBoxes[this.mSelectedIndex]).setIsSelected(true, false);
        invalidate();
        return true;
    }

    @Override // com.sonymobile.calendar.DayNavigatorDayContainer, com.sonymobile.calendar.WeekNavigatorDayContainer
    protected void initDays(int i, boolean z) {
        this.mDayBoxes = new TabletDayNavigatorDayView[i];
        for (int i2 = 0; i2 < i; i2++) {
            TabletDayNavigatorDayView tabletDayNavigatorDayView = new TabletDayNavigatorDayView(getContext(), z, getViewIndex(i2, i), this.mViewNavigator);
            tabletDayNavigatorDayView.setNavigatorDayClickedListener(this, i2);
            this.mDayBoxes[i2] = tabletDayNavigatorDayView;
            addView(this.mDayBoxes[i2]);
        }
    }
}
