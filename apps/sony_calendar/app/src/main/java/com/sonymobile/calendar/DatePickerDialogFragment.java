package com.sonymobile.calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;

/* JADX INFO: loaded from: classes2.dex */
public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static final String MONTH = "month";
    public static final String MONTH_DAY = "monthDay";
    public static final String VIEW_ID = "viewId";
    public static final String YEAR = "year";
    private DatePickerDialogListener mListener;
    private int mViewId;

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mViewId = getArguments().getInt(VIEW_ID);
        return new DatePickerDialog(getActivity(), R.style.ActivityDialog, this, getArguments().getInt("year"), getArguments().getInt("month"), getArguments().getInt(MONTH_DAY));
    }

    public void setDateListener(DatePickerDialogListener datePickerDialogListener) {
        this.mListener = datePickerDialogListener;
    }

    public static DatePickerDialogFragment newInstance(int i, int i2, int i3, DatePickerDialogListener datePickerDialogListener, int i4) {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("year", i);
        bundle.putInt("month", i2);
        bundle.putInt(MONTH_DAY, i3);
        bundle.putInt(VIEW_ID, i4);
        datePickerDialogFragment.setArguments(bundle);
        datePickerDialogFragment.setDateListener(datePickerDialogListener);
        return datePickerDialogFragment;
    }

    @Override // android.app.DatePickerDialog.OnDateSetListener
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        DatePickerDialogListener datePickerDialogListener = this.mListener;
        if (datePickerDialogListener != null) {
            datePickerDialogListener.onDateSet(i, i2, i3, this.mViewId);
        }
    }
}
