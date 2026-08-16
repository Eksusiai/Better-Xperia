package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes2.dex */
public class DatePickerDialogBase extends DialogFragment implements DatePicker.OnDateChangedListener, IDatePicker {
    private Context context;
    private final DatePicker mDatePicker;
    protected OnDatePickerSetListener onDatePickerSetListener;
    protected Time selectedDate;
    private int titleResId;

    public DatePickerDialogBase(Context context, Time time, int i) {
        this.context = context;
        this.selectedDate = new SafeTime(time);
        this.titleResId = i;
        DatePicker datePicker = (DatePicker) ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.date_picker_dialog_base, (ViewGroup) null);
        this.mDatePicker = datePicker;
        datePicker.init(time.year, time.month, time.monthDay, this);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context, R.style.AlertDialogTheme);
        builder.setTitle(this.titleResId);
        builder.setPositiveButton(R.string.set_label, this);
        builder.setNegativeButton(R.string.clr_strings_button_title_cancel_txt, (DialogInterface.OnClickListener) null);
        builder.setView(this.mDatePicker);
        return builder.create();
    }

    protected void adaptPicker(boolean z, boolean z2, boolean z3) {
        if (!z) {
            filterPicker(new String[]{"mYearPicker", "mYearSpinner"});
        }
        if (!z2) {
            filterPicker(new String[]{"mMonthPicker", "mMonthSpinner"});
        }
        if (z3) {
            return;
        }
        filterPicker(new String[]{"mDayPicker", "mDaySpinner"});
    }

    @Override // com.sonymobile.calendar.IDatePicker
    public void setOnDatePickerSetListener(OnDatePickerSetListener onDatePickerSetListener) {
        this.onDatePickerSetListener = onDatePickerSetListener;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        tryNotifyDateSet();
    }

    @Override // android.widget.DatePicker.OnDateChangedListener
    public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
        this.mDatePicker.init(i, i2, i3, this);
    }

    private void tryNotifyDateSet() {
        if (this.onDatePickerSetListener != null) {
            this.mDatePicker.clearFocus();
            Time time = this.selectedDate;
            time.set(time.second, this.selectedDate.minute, this.selectedDate.hour, this.mDatePicker.getDayOfMonth(), this.mDatePicker.getMonth(), this.mDatePicker.getYear());
            this.onDatePickerSetListener.onDateSet(this.selectedDate);
        }
    }

    private void filterPicker(String[] strArr) {
        try {
            for (Field field : this.mDatePicker.getClass().getDeclaredFields()) {
                for (String str : strArr) {
                    if (str.equals(field.getName()) || str.equals(field.getName())) {
                        field.setAccessible(true);
                        ((View) field.get(this.mDatePicker)).setVisibility(8);
                    }
                }
            }
        } catch (IllegalAccessException unused) {
            Log.e("MonthPicker", "Failed to create date picker");
        } catch (Exception unused2) {
            Log.e("MonthPicker", "Failed to create date picker");
        }
    }
}
