package com.sonymobile.calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;

/* JADX INFO: loaded from: classes2.dex */
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private TimePickerDialogListener mListener;
    private int mViewId;

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mViewId = getArguments().getInt(DatePickerDialogFragment.VIEW_ID);
        return new TimePickerDialog(getActivity(), R.style.ActivityDialog, this, getArguments().getInt("hour"), getArguments().getInt("min"), DateFormat.is24HourFormat(getActivity()));
    }

    public void setTimeListener(TimePickerDialogListener timePickerDialogListener) {
        this.mListener = timePickerDialogListener;
    }

    public static TimePickerDialogFragment newInstance(int i, int i2, TimePickerDialogListener timePickerDialogListener, int i3) {
        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("hour", i);
        bundle.putInt("min", i2);
        bundle.putInt(DatePickerDialogFragment.VIEW_ID, i3);
        timePickerDialogFragment.setArguments(bundle);
        timePickerDialogFragment.setTimeListener(timePickerDialogListener);
        return timePickerDialogFragment;
    }

    @Override // android.app.TimePickerDialog.OnTimeSetListener
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        TimePickerDialogListener timePickerDialogListener = this.mListener;
        if (timePickerDialogListener != null) {
            timePickerDialogListener.onTimeSet(i, i2, this.mViewId);
        }
    }
}
