package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class DateBoxAdapter extends ArrayAdapter<Time> {
    private int boxHeight;
    private int boxWidth;
    private DateBoxView[] dateBoxes;
    private Time enabledMonth;
    private CalendarEventNavigator eventNavigator;
    private FocusedViewNavigator focusedViewNavigator;
    private OnDateBoxSelectedListener selectionListener;

    public DateBoxAdapter(Context context, OnDateBoxSelectedListener onDateBoxSelectedListener, CalendarEventNavigator calendarEventNavigator, FocusedViewNavigator focusedViewNavigator) {
        super(context, 0);
        this.boxWidth = 0;
        this.boxHeight = 0;
        this.selectionListener = onDateBoxSelectedListener;
        this.eventNavigator = calendarEventNavigator;
        this.focusedViewNavigator = focusedViewNavigator;
    }

    @Override // android.widget.ArrayAdapter
    public void addAll(Time... timeArr) {
        this.dateBoxes = new DateBoxView[timeArr.length];
        super.addAll(timeArr);
    }

    public DateBoxView getDateBox(int i) {
        DateBoxView[] dateBoxViewArr = this.dateBoxes;
        if (dateBoxViewArr != null) {
            return dateBoxViewArr[i];
        }
        return null;
    }

    public int getRowIndex(Time time) {
        long firstDateMillis;
        int i;
        System.currentTimeMillis();
        DateBoxView[] dateBoxViewArr = this.dateBoxes;
        if (dateBoxViewArr != null && dateBoxViewArr[0] != null) {
            firstDateMillis = dateBoxViewArr[0].getDate().toMillis(false);
        } else {
            firstDateMillis = getFirstDateMillis(time);
        }
        long millis = time.toMillis(false) - firstDateMillis;
        if (millis >= 0 && (i = (int) (millis / 86400000)) < getCount()) {
            return i / 7;
        }
        return -1;
    }

    private long getFirstDateMillis(Time time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(time.year, time.month, 1, time.hour, time.minute, time.second);
        calendar.setTimeZone(TimeZone.getTimeZone(time.timezone));
        return calendar.getTimeInMillis();
    }

    public int getRowCount() {
        return (int) Math.ceil(((double) getCount()) / 7.0d);
    }

    public void setEnabledMonth(Time time) {
        this.enabledMonth = new SafeTime(time);
        notifyDataSetInvalidated();
    }

    public Time getEnabledMonth() {
        return this.enabledMonth;
    }

    public void setDateBoxSize(int i, int i2) {
        float f;
        float f2;
        this.boxWidth = i;
        int i3 = getContext().getResources().getConfiguration().orientation;
        if (!getContext().getResources().getBoolean(R.bool.tablet_mode)) {
            if (i3 != 2 || UiUtils.isSub320dpScreen(getContext())) {
                f = i2;
                f2 = 1.01f;
            } else {
                f = i2;
                f2 = 2.0f;
            }
            i2 = (int) (f * f2);
        }
        this.boxHeight = i2;
    }

    public int getBoxHeight() {
        return this.boxHeight;
    }

    public int getBoxWidth() {
        return this.boxWidth;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        DateBoxView dateBoxView;
        DateBoxView[] dateBoxViewArr = this.dateBoxes;
        if (dateBoxViewArr[i] == null || i == 0) {
            dateBoxView = new DateBoxView(getContext(), i, this.focusedViewNavigator);
            dateBoxView.setLayoutParams(new AbsListView.LayoutParams(this.boxWidth, this.boxHeight));
            dateBoxView.setEventNavigator(this.eventNavigator);
            dateBoxView.setOnDateBoxSelectedListener(this.selectionListener);
            dateBoxView.update(getItem(i), this.enabledMonth, this.dateBoxes[i == 0 ? 1 : i] != null);
            this.dateBoxes[i] = dateBoxView;
        } else {
            dateBoxView = dateBoxViewArr[i];
            if (dateBoxView.getHeight() != this.boxHeight || dateBoxView.getWidth() != this.boxWidth) {
                dateBoxView.setLayoutParams(new AbsListView.LayoutParams(this.boxWidth, this.boxHeight));
            }
            dateBoxView.update(getItem(i), this.enabledMonth, true);
        }
        return dateBoxView;
    }
}
