package com.sonymobile.calendar;

import android.content.DialogInterface;
import androidx.fragment.app.FragmentManager;

/* JADX INFO: loaded from: classes2.dex */
public interface IDatePicker extends DialogInterface.OnClickListener {
    void dismiss();

    void setOnDatePickerSetListener(OnDatePickerSetListener onDatePickerSetListener);

    void show(FragmentManager fragmentManager, String str);
}
