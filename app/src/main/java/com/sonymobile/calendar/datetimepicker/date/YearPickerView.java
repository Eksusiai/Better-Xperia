package com.sonymobile.calendar.datetimepicker.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.sonymobile.calendar.R;
import com.sonymobile.lunar.lib.LunarUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class YearPickerView extends ListView implements AdapterView.OnItemClickListener, DatePickerDialog.OnDateChangedListener {
    private YearAdapter mAdapter;
    private int mChildSize;
    private final DatePickerController mController;
    private TextViewWithCircularIndicator mSelectedView;
    private int mViewSize;

    public YearPickerView(Context context, DatePickerController datePickerController) {
        super(context);
        this.mController = datePickerController;
        datePickerController.registerOnDateChangedListener(this);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        Resources resources = context.getResources();
        this.mViewSize = resources.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height);
        this.mChildSize = resources.getDimensionPixelOffset(R.dimen.year_label_height);
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(this.mChildSize / 3);
        init(context);
        setOnItemClickListener(this);
        setSelector(new StateListDrawable());
        setDividerHeight(0);
        onDateChanged();
    }

    private void init(Context context) {
        ArrayList arrayList = new ArrayList();
        for (int minYear = this.mController.getMinYear(); minYear <= this.mController.getMaxYear(); minYear++) {
            arrayList.add(String.format("%d", Integer.valueOf(minYear)));
        }
        YearAdapter yearAdapter = new YearAdapter(context, R.layout.year_label_text_view, arrayList);
        this.mAdapter = yearAdapter;
        setAdapter((ListAdapter) yearAdapter);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        this.mController.tryVibrate();
        TextViewWithCircularIndicator textViewWithCircularIndicator = (TextViewWithCircularIndicator) view;
        if (textViewWithCircularIndicator != null) {
            TextViewWithCircularIndicator textViewWithCircularIndicator2 = this.mSelectedView;
            if (textViewWithCircularIndicator != textViewWithCircularIndicator2) {
                if (textViewWithCircularIndicator2 != null) {
                    textViewWithCircularIndicator2.drawIndicator(false);
                    this.mSelectedView.requestLayout();
                }
                textViewWithCircularIndicator.drawIndicator(true);
                textViewWithCircularIndicator.requestLayout();
                this.mSelectedView = textViewWithCircularIndicator;
            }
            this.mController.onYearSelected(getYearFromTextView(textViewWithCircularIndicator));
            this.mAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getYearFromTextView(TextView textView) {
        return Integer.parseInt(textView.getText().toString());
    }

    private class YearAdapter extends ArrayAdapter<String> {
        public YearAdapter(Context context, int i, List<String> list) {
            super(context, i, list);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            int i2;
            TextViewWithCircularIndicator textViewWithCircularIndicator = (TextViewWithCircularIndicator) super.getView(i, view, viewGroup);
            textViewWithCircularIndicator.requestLayout();
            int yearFromTextView = YearPickerView.this.getYearFromTextView(textViewWithCircularIndicator);
            YearPickerView.this.mController.getMinYear();
            if (!YearPickerView.this.mController.isLunarOn()) {
                i2 = YearPickerView.this.mController.getSelectedDay().year;
            } else {
                Calendar calendar = Calendar.getInstance();
                SimpleMonthAdapter.CalendarDay selectedDay = YearPickerView.this.mController.getSelectedDay();
                calendar.set(selectedDay.year, selectedDay.month, selectedDay.day);
                i2 = LunarUtils.convertSolarDateToLunarDate(calendar.getTime()).mYear;
            }
            boolean z = i2 == yearFromTextView;
            textViewWithCircularIndicator.drawIndicator(z);
            if (z) {
                YearPickerView.this.mSelectedView = textViewWithCircularIndicator;
            }
            YearPickerView.this.mController.isLunarOn();
            return textViewWithCircularIndicator;
        }
    }

    public void postSetSelectionCentered(int i) {
        postSetSelectionFromTop(i, (this.mViewSize / 2) - (this.mChildSize / 2));
    }

    public void postSetSelectionFromTop(final int i, final int i2) {
        post(new Runnable() { // from class: com.sonymobile.calendar.datetimepicker.date.YearPickerView.1
            @Override // java.lang.Runnable
            public void run() {
                YearPickerView.this.setSelectionFromTop(i, i2);
                YearPickerView.this.requestLayout();
            }
        });
    }

    public int getFirstPositionOffset() {
        View childAt = getChildAt(0);
        if (childAt == null) {
            return 0;
        }
        return childAt.getTop();
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.OnDateChangedListener
    public void onDateChanged() {
        int i;
        this.mAdapter.notifyDataSetChanged();
        this.mController.getMinYear();
        if (this.mController.isLunarOn()) {
            Calendar calendar = Calendar.getInstance();
            SimpleMonthAdapter.CalendarDay selectedDay = this.mController.getSelectedDay();
            calendar.set(selectedDay.year, selectedDay.month, selectedDay.day);
            i = LunarUtils.convertSolarDateToLunarDate(calendar.getTime()).mYear;
        } else {
            i = this.mController.getSelectedDay().year;
        }
        postSetSelectionCentered(i - this.mController.getMinYear());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (accessibilityEvent.getEventType() == 4096) {
            accessibilityEvent.setFromIndex(0);
            accessibilityEvent.setToIndex(0);
        }
    }

    public void onCheckedChange(boolean z) {
        init(getContext());
        onDateChanged();
    }
}
