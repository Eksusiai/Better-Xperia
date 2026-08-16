package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public abstract class AllDayViewBase extends CalendarViewBase {
    private static boolean SHOW_FULL_EVENT_TEXT = true;
    private ImageView iconView;
    private LinearLayout mIconLayout;
    private ImageView showMore;

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected abstract int getDayCount();

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected abstract Time[] getDays(Time time);

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected abstract Time getNextViewTime(Time time, boolean z);

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void scrollToSelectedPosition(boolean z) {
    }

    public AllDayViewBase(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void inflateLayout(LayoutInflater layoutInflater) {
        layoutInflater.inflate(R.layout.all_day_view_base, this);
        this.iconView = (ImageView) findViewById(R.id.all_day_icon);
        this.showMore = (ImageView) findViewById(R.id.show_more_all_days);
        this.mIconLayout = (LinearLayout) findViewById(R.id.icon_layout);
        initIconsLayout();
        if (Utils.isTabletDevice(getContext())) {
            this.iconView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.month_view_background_color));
        }
    }

    private void initIconsLayout() {
        if (this.showMore != null) {
            this.mIconLayout.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.AllDayViewBase.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (AllDayViewBase.this.columnContainers[1] instanceof AllDayColumnContainer) {
                        AllDayColumnContainer allDayColumnContainer = (AllDayColumnContainer) AllDayViewBase.this.columnContainers[1];
                        if (!allDayColumnContainer.isExpanded()) {
                            allDayColumnContainer.expandAllDayColumns();
                            AllDayViewBase.this.showMore.setImageResource(R.drawable.semc_list_expandable_arrow_up);
                        } else {
                            allDayColumnContainer.showLessAllDayEvents();
                            AllDayViewBase.this.showMore.setImageResource(R.drawable.semc_list_expandable_arrow_down);
                        }
                    }
                }
            });
        }
        if (UiUtils.isPhoneLandscape(getContext())) {
            this.mIconLayout.setBackground(null);
        }
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected ICalendarColumnContainer getNewCalendarColumnContainer() {
        return new AllDayColumnContainer(getContext(), getDayCount(), this, this.columnsLayout, isR2L(), showFullEventText(), this);
    }

    @Override // com.sonymobile.calendar.CalendarViewBase
    protected void onSwipeCompleted(boolean z) {
        ICalendarColumnContainer iCalendarColumnContainer = this.columnContainers[1];
        iCalendarColumnContainer.setViewPortSize(getWidth() - this.iconView.getWidth(), getHeight(), true);
        resetExpandedState(iCalendarColumnContainer);
        ICalendarColumnContainer iCalendarColumnContainer2 = this.columnContainers[2];
        iCalendarColumnContainer2.setViewPortSize(getWidth() - this.iconView.getWidth(), getHeight(), true);
        resetExpandedState(iCalendarColumnContainer2);
        ICalendarColumnContainer iCalendarColumnContainer3 = this.columnContainers[0];
        iCalendarColumnContainer3.setViewPortSize(getWidth() - this.iconView.getWidth(), getHeight(), true);
        resetExpandedState(iCalendarColumnContainer3);
    }

    protected boolean showFullEventText() {
        return SHOW_FULL_EVENT_TEXT;
    }

    public void updateMoreIcon() {
        if (this.showMore == null || this.columnContainers == null || !(this.columnContainers[1] instanceof AllDayColumnContainer)) {
            return;
        }
        AllDayColumnContainer allDayColumnContainer = (AllDayColumnContainer) this.columnContainers[1];
        if (allDayColumnContainer.shouldShowMoreIcon()) {
            this.showMore.setVisibility(0);
            this.showMore.setImageResource(allDayColumnContainer.isExpanded() ? R.drawable.semc_list_expandable_arrow_up : R.drawable.semc_list_expandable_arrow_down);
        } else {
            this.showMore.setVisibility(8);
        }
    }

    private void resetExpandedState(ICalendarColumnContainer iCalendarColumnContainer) {
        if (iCalendarColumnContainer instanceof AllDayColumnContainer) {
            ((AllDayColumnContainer) iCalendarColumnContainer).resetExpanded();
        }
    }
}
