package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class NavigationMonthGridAdapter extends ArrayAdapter<String> {
    private int dayCount;
    private int defaultColor;
    private boolean isWeekView;
    private int offset;
    private int selectedColor;
    private int selectedDay;
    private int selectedRow;
    private int today;

    public NavigationMonthGridAdapter(Context context) {
        super(context, R.layout.navigation_month_grid_item);
        this.selectedRow = -1;
        this.isWeekView = false;
        initColors();
    }

    public void setIsWeekView(boolean z) {
        this.isWeekView = z;
    }

    public void setOffset(int i) {
        this.offset = i;
    }

    public void setDayCountInCurrentMonth(int i) {
        this.dayCount = i;
    }

    public void setSelectedDay(int i) {
        this.selectedDay = i;
        if (this.isWeekView) {
            this.selectedRow = (int) Math.floor(((double) ((i + this.offset) - 1)) / 7.0d);
        }
    }

    public void setSelectedPosition(int i) {
        this.selectedDay = (i - this.offset) + 1;
        if (this.isWeekView) {
            this.selectedRow = (int) Math.floor(((double) i) / 7.0d);
        }
    }

    public void setToday(int i) {
        this.today = i;
    }

    public boolean isWeekView() {
        return this.isWeekView;
    }

    public int getRowCount() {
        return (int) Math.ceil(((double) getCount()) / 7.0d);
    }

    public int getSelectedItem() {
        return (this.selectedDay + this.offset) - 1;
    }

    public int getSelectedDay() {
        return this.selectedDay;
    }

    public int getTodayItem() {
        return (this.today + this.offset) - 1;
    }

    public int getSelectedRow() {
        return this.selectedRow;
    }

    public boolean isSelected(int i) {
        if (this.isWeekView) {
            return this.selectedRow == ((int) Math.floor(((double) i) / 7.0d));
        }
        return i == getSelectedItem();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.navigation_month_grid_item, viewGroup, false);
        }
        view.setMinimumHeight((int) (((double) viewGroup.getHeight()) / ((double) getRowCount())));
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        int i2 = this.offset;
        if (i < i2 || i >= this.dayCount + i2) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.navigation_month_grid_other_month_text));
            textView.setBackgroundColor(0);
            view.setBackgroundColor(0);
        } else if (this.isWeekView) {
            updateWeekItem(i, view, textView);
        } else {
            updateDayItem(i, view, textView);
        }
        textView.setText(getItem(i));
        return view;
    }

    private void initColors() {
        this.defaultColor = ContextCompat.getColor(getContext(), R.color.calendar_week_label);
        this.selectedColor = ContextCompat.getColor(getContext(), R.color.month_selected_text_color);
    }

    private void updateWeekItem(int i, View view, TextView textView) {
        int i2 = (i - this.offset) + 1;
        int iFloor = (int) Math.floor(((double) i) / 7.0d);
        if (i2 == this.today && iFloor == this.selectedRow) {
            textView.setTextColor(this.selectedColor);
            return;
        }
        if (iFloor == this.selectedRow) {
            textView.setTextColor(this.selectedColor);
            textView.setBackgroundColor(0);
            view.setBackgroundColor(0);
        } else {
            textView.setTextColor(this.defaultColor);
            textView.setBackgroundColor(0);
            view.setBackgroundColor(0);
        }
    }

    private void updateDayItem(int i, View view, TextView textView) {
        int i2 = (i - this.offset) + 1;
        if (i2 == this.selectedDay) {
            textView.setTextColor(this.selectedColor);
            textView.setBackgroundColor(0);
            view.setBackgroundColor(0);
        } else {
            if (i2 == this.today) {
                textView.setTextColor(this.defaultColor);
                return;
            }
            textView.setTextColor(this.defaultColor);
            textView.setBackgroundColor(0);
            view.setBackgroundColor(0);
        }
    }
}
