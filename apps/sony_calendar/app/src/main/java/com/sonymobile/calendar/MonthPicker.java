package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;

/* JADX INFO: loaded from: classes2.dex */
public class MonthPicker extends DatePickerDialogBase {
    public MonthPicker(Context context, Time time) {
        super(context, time, R.string.month_picker_select_month);
        adaptPicker(true, true, false);
    }
}
