package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class WeekPicker extends DialogFragment implements IDatePicker {
    private Calendar calendar;
    private Context context;
    private OnDatePickerSetListener onDatePickerSetListener;
    private NumberPicker weekPicker;
    private NumberPicker yearPicker;

    public WeekPicker(Context context, Time time) {
        this.context = context;
        this.calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        int firstDayOfWeek = Utils.getFirstDayOfWeek(context);
        if (firstDayOfWeek == 0) {
            this.calendar.setFirstDayOfWeek(1);
        } else if (firstDayOfWeek == 6) {
            this.calendar.setFirstDayOfWeek(7);
        } else {
            this.calendar.setFirstDayOfWeek(2);
        }
        this.calendar.setTimeInMillis(time.toMillis(false));
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context, R.style.AlertDialogTheme);
        builder.setTitle(R.string.week_picker_select_week);
        builder.setPositiveButton(R.string.set_label, this);
        builder.setNegativeButton(R.string.clr_strings_button_title_cancel_txt, (DialogInterface.OnClickListener) null);
        builder.setView(initPickerView());
        return builder.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        this.weekPicker.clearFocus();
        this.yearPicker.clearFocus();
        this.calendar.set(3, this.weekPicker.getValue());
        this.calendar.set(1, this.yearPicker.getValue());
        Time time = new SafeTime();
        time.set(this.calendar.getTimeInMillis());
        this.onDatePickerSetListener.onDateSet(time);
    }

    @Override // com.sonymobile.calendar.IDatePicker
    public void setOnDatePickerSetListener(OnDatePickerSetListener onDatePickerSetListener) {
        this.onDatePickerSetListener = onDatePickerSetListener;
    }

    private View initPickerView() {
        View viewInflate = LayoutInflater.from(this.context).inflate(R.layout.week_picker, (ViewGroup) null);
        NumberPicker numberPicker = (NumberPicker) viewInflate.findViewById(R.id.week_picker_year_picker);
        this.yearPicker = numberPicker;
        numberPicker.setMinValue(0);
        this.yearPicker.setMaxValue(3000);
        this.yearPicker.setValue(this.calendar.get(1));
        this.yearPicker.setOnValueChangedListener(new YearChangedListener());
        NumberPicker numberPicker2 = (NumberPicker) viewInflate.findViewById(R.id.week_picker_week_picker);
        this.weekPicker = numberPicker2;
        numberPicker2.setMinValue(1);
        this.weekPicker.setMaxValue(getNumberOfWeeksForYear(this.calendar.get(1)));
        this.weekPicker.setValue(Utils.getWeekNumberOfDay(getActivity(), this.calendar));
        return viewInflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getNumberOfWeeksForYear(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(i, 11, 31);
        return Utils.getWeekNumberOfDay(getActivity(), calendar) == 53 ? 53 : 52;
    }

    private class YearChangedListener implements NumberPicker.OnValueChangeListener {
        private YearChangedListener() {
        }

        @Override // android.widget.NumberPicker.OnValueChangeListener
        public void onValueChange(NumberPicker numberPicker, int i, int i2) {
            WeekPicker.this.weekPicker.setMaxValue(WeekPicker.this.getNumberOfWeeksForYear(i2));
        }
    }
}
