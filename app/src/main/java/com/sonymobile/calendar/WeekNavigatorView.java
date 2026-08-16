package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class WeekNavigatorView extends CalendarViewBase implements FocusedViewNavigator {
    private TextView weekNumberView;

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void onSwipeCompleted(boolean z) {
    }

    public WeekNavigatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        for (ICalendarColumnContainer iCalendarColumnContainer : this.columnContainers) {
            iCalendarColumnContainer.invalidate();
        }
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void inflateLayout(LayoutInflater layoutInflater) {
        layoutInflater.inflate(R.layout.week_navigator, this);
        this.weekNumberView = (TextView) findViewById(R.id.week_navigator_week_number);
        if (Utils.isTabletDevice(getContext())) {
            findViewById(R.id.week_line).setVisibility(8);
            View viewFindViewById = findViewById(R.id.week_number_layout);
            viewFindViewById.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.month_view_background_color));
            viewFindViewById.setPadding(0, (int) getResources().getDimension(R.dimen.week_number_padding), 0, 0);
        }
        if (UiUtils.isPhoneLandscape(getContext())) {
            findViewById(R.id.week_line).setVisibility(8);
        }
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected ICalendarColumnContainer getNewCalendarColumnContainer() {
        return new WeekNavigatorDayContainer(getContext(), getDayCount(), isR2L(), this, true);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void scrollToSelectedPosition(boolean z) {
        UiUtils.showWeekNumberInWeekDayView(getContext(), this.weekNumberView);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected int getDayCount() {
        return WeekUtils.getDayCount(getContext());
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected Time[] getDays(Time time) {
        return WeekUtils.getDays(getContext(), time, false);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected Time getNextViewTime(Time time, boolean z) {
        return WeekUtils.getNextViewTime(time, z);
    }

    public void goToNextView() {
        this.calendarFragment.goToNext(0.0f);
    }

    public void goToPreviousView() {
        this.calendarFragment.goToPrevious(0.0f);
    }

    @Override // android.widget.ScrollView, android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        PermissionUtils.reportFullyDrawnIfPermitted((AppCompatActivity) getContext());
    }
}
