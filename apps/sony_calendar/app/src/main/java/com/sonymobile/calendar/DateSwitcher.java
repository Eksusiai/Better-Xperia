package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;

/* JADX INFO: loaded from: classes2.dex */
public abstract class DateSwitcher {
    private static final int DATE_TEXT_SIZE = 14;
    private static final int DATE_WHEN_HOLIDAY_TEXT_SIZE = 12;
    protected TextView dateLabel;
    protected TextView holidayLabel;
    protected ImageView nextButton;
    private OnDateSwitcherClickedListener onDateSwitcherClickedListener;
    protected ImageView prevButton;
    protected int textColor;
    protected int todayTextColor;
    private boolean isSwitchEnabled = true;
    private View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.DateSwitcher.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.date_switcher_date_label || id == R.id.date_switcher_holiday_label) {
                if (DateSwitcher.this.isSwitchEnabled) {
                    DateSwitcher.this.onDateSwitcherClickedListener.onDateLabelClicked();
                }
            } else if (id == R.id.date_switcher_next_btn) {
                DateSwitcher.this.onDateSwitcherClickedListener.onNextButtonClicked();
            } else if (id == R.id.date_switcher_prev_btn) {
                DateSwitcher.this.onDateSwitcherClickedListener.onPreviousButtonClicked();
            }
        }
    };

    public abstract void updateDateLabel(Context context, Time time);

    public DateSwitcher(View view) {
        initButtons(view);
        initDateLabel(view);
        initHolidayLabel(view);
    }

    public void setIsSwitchEnabled(boolean z) {
        this.isSwitchEnabled = z;
        if (z) {
            this.prevButton.setVisibility(0);
            this.nextButton.setVisibility(0);
        } else {
            this.prevButton.setVisibility(8);
            this.nextButton.setVisibility(8);
        }
    }

    public CharSequence getDateLabel() {
        return this.dateLabel.getText();
    }

    public void setOnDateSwitcherClickedListener(OnDateSwitcherClickedListener onDateSwitcherClickedListener) {
        this.onDateSwitcherClickedListener = onDateSwitcherClickedListener;
    }

    protected void updateHolidayLabel(Time time) {
        FreeDayService.getInstance().requestHolidayName(this.holidayLabel.getContext(), time.year, time.month, time.monthDay, new FreeDayServiceResultHandler(time), 1);
    }

    private void initButtons(View view) {
        this.prevButton = (ImageView) view.findViewById(R.id.date_switcher_prev_btn);
        this.nextButton = (ImageView) view.findViewById(R.id.date_switcher_next_btn);
        ImageView imageView = this.prevButton;
        if (imageView != null) {
            imageView.setOnClickListener(this.onClickListener);
        }
        ImageView imageView2 = this.nextButton;
        if (imageView2 != null) {
            imageView2.setOnClickListener(this.onClickListener);
        }
    }

    private void initDateLabel(View view) {
        this.dateLabel = (TextView) view.findViewById(R.id.date_switcher_date_label);
        this.todayTextColor = ContextCompat.getColor(view.getContext(), R.color.calendar_week_label);
        this.textColor = ContextCompat.getColor(view.getContext(), R.color.calendar_week_label);
        TextView textView = this.dateLabel;
        if (textView != null) {
            textView.setOnClickListener(this.onClickListener);
        }
    }

    private void initHolidayLabel(View view) {
        TextView textView = (TextView) view.findViewById(R.id.date_switcher_holiday_label);
        this.holidayLabel = textView;
        if (textView == null) {
            return;
        }
        textView.setTextColor(FreeDayService.getInstance().getHolidayColor(view.getContext()));
        this.holidayLabel.setOnClickListener(this.onClickListener);
    }

    protected class FreeDayServiceResultHandler implements IAsyncServiceResultHandler {
        private Time date;

        public FreeDayServiceResultHandler(Time time) {
            this.date = time;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            int i;
            int iIntValue = ((Integer) obj2).intValue();
            if (iIntValue == 1) {
                String str = (String) obj;
                if (!str.equals("")) {
                    DateSwitcher.this.holidayLabel.setText(str);
                    DateSwitcher.this.holidayLabel.setVisibility(0);
                    DateSwitcher.this.dateLabel.setTextSize(1, 12.0f);
                    return;
                } else {
                    DateSwitcher.this.holidayLabel.setVisibility(8);
                    DateSwitcher.this.dateLabel.setTextSize(1, 14.0f);
                    return;
                }
            }
            if (iIntValue != 2) {
                return;
            }
            if (((Boolean) obj).booleanValue()) {
                DateSwitcher.this.dateLabel.setTextColor(FreeDayService.getInstance().getHolidayColor(DateSwitcher.this.dateLabel.getContext()));
                return;
            }
            Time time = new SafeTime(this.date);
            time.set(System.currentTimeMillis());
            if (this.date.year == time.year && this.date.month == time.month && this.date.monthDay == time.monthDay) {
                i = DateSwitcher.this.todayTextColor;
            } else {
                i = DateSwitcher.this.textColor;
            }
            DateSwitcher.this.dateLabel.setTextColor(i);
        }
    }
}
