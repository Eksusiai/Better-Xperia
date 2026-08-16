package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;

/* JADX INFO: loaded from: classes2.dex */
public class DayPicker extends DatePickerDialogBase {
    public DayPicker(Context context, Time time) {
        super(context, time, R.string.day_picker_select_day);
    }
}
