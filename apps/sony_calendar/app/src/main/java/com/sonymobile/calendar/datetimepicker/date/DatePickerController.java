package com.sonymobile.calendar.datetimepicker.date;

/* JADX INFO: loaded from: classes2.dex */
public interface DatePickerController {
    int getFirstDayOfWeek();

    int getMaxYear();

    int getMinYear();

    SimpleMonthAdapter.CalendarDay getSelectedDay();

    boolean isLunarOn();

    void onDayOfMonthSelected(int i, int i2, int i3);

    void onYearSelected(int i);

    void registerOnDateChangedListener(DatePickerDialog.OnDateChangedListener onDateChangedListener);

    void tryVibrate();

    void unregisterOnDateChangedListener(DatePickerDialog.OnDateChangedListener onDateChangedListener);
}
