package com.sonymobile.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.Time;
import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public class MonthSwitcher extends DateSwitcher {
    public MonthSwitcher(View view) {
        super(view);
    }

    @Override // com.sonymobile.calendar.DateSwitcher
    public void updateDateLabel(Context context, Time time) {
        if (context == null) {
            return;
        }
        Resources resources = context.getResources();
        this.dateLabel.setText(Utils.getMonthText(context, time));
        this.dateLabel.setTextColor(getColor(time));
        this.prevButton.setContentDescription(resources.getString(R.string.accessibility_previous_month));
        this.nextButton.setContentDescription(resources.getString(R.string.accessibility_next_month));
    }

    private int getColor(Time time) {
        Time time2 = new SafeTime(time);
        time2.set(System.currentTimeMillis());
        return (time.year == time2.year && time.month == time2.month) ? this.todayTextColor : this.textColor;
    }
}
