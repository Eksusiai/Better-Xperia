package com.sonymobile.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.Time;
import android.view.View;
import com.sonyericsson.calendar.util.FreeDayService;

/* JADX INFO: loaded from: classes2.dex */
public class DaySwitcher extends DateSwitcher {
    public DaySwitcher(View view) {
        super(view);
    }

    @Override // com.sonymobile.calendar.DateSwitcher
    public void updateDateLabel(Context context, Time time) {
        Resources resources = context.getResources();
        this.dateLabel.setText(Utils.getDayText(context, time, true));
        setDayColor(time);
        this.prevButton.setContentDescription(resources.getString(R.string.accessibility_previous_day));
        this.nextButton.setContentDescription(resources.getString(R.string.accessibility_next_day));
        updateHolidayLabel(time);
    }

    private void setDayColor(Time time) {
        FreeDayService.getInstance().requestIsFreeDay(this.dateLabel.getContext(), time.year, time.month, time.monthDay, new DateSwitcher.FreeDayServiceResultHandler(time), 2);
    }
}
