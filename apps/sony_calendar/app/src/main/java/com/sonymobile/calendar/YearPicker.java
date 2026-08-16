package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;

/* JADX INFO: loaded from: classes2.dex */
public class YearPicker extends DatePickerDialogBase {
    public YearPicker(Context context, Time time) {
        super(context, time, R.string.year_picker_select_year);
        adaptPicker(true, false, false);
    }
}
