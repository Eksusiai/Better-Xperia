package com.sonymobile.calendar.datetimepicker.date;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import com.sonymobile.calendar.lunar.LunarHelper;
import com.sonymobile.lunar.lib.LunarUtils;
import java.util.Calendar;
import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
public class SimpleMonthAdapter extends BaseAdapter implements SimpleMonthView.OnDayClickListener {
    protected static final int MONTHS_IN_YEAR = 12;
    private static final String TAG = "SimpleMonthAdapter";
    private final Context mContext;
    private final DatePickerController mController;
    private CalendarDay mSelectedDay;

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        return true;
    }

    public static class CalendarDay {
        private Calendar calendar;
        int day;
        int month;
        private Time time;
        int year;

        public CalendarDay() {
            setTime(System.currentTimeMillis());
        }

        public CalendarDay(long j) {
            setTime(j);
        }

        public CalendarDay(Calendar calendar) {
            this.year = calendar.get(1);
            this.month = calendar.get(2);
            this.day = calendar.get(5);
        }

        public CalendarDay(int i, int i2, int i3) {
            setDay(i, i2, i3);
        }

        public void set(CalendarDay calendarDay) {
            this.year = calendarDay.year;
            this.month = calendarDay.month;
            this.day = calendarDay.day;
        }

        public void setDay(int i, int i2, int i3) {
            this.year = i;
            this.month = i2;
            this.day = i3;
        }

        public void setJulianDay(int i) {
            if (this.time == null) {
                this.time = new SafeTime();
            }
            this.time.setJulianDay(i);
            setTime(this.time.toMillis(false));
        }

        private void setTime(long j) {
            if (this.calendar == null) {
                this.calendar = Calendar.getInstance();
            }
            this.calendar.setTimeInMillis(j);
            this.month = this.calendar.get(2);
            this.year = this.calendar.get(1);
            this.day = this.calendar.get(5);
        }
    }

    public SimpleMonthAdapter(Context context, DatePickerController datePickerController) {
        this.mContext = context;
        this.mController = datePickerController;
        init();
        setSelectedDay(datePickerController.getSelectedDay());
    }

    public void setSelectedDay(CalendarDay calendarDay) {
        this.mSelectedDay = calendarDay;
        notifyDataSetChanged();
    }

    public CalendarDay getSelectedDay() {
        return this.mSelectedDay;
    }

    protected void init() {
        this.mSelectedDay = new CalendarDay(System.currentTimeMillis());
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return ((this.mController.getMaxYear() - this.mController.getMinYear()) + 1) * 12;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0 */
    /* JADX WARN: Type inference failed for: r3v1, types: [int] */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v4 */
    /* JADX WARN: Type inference failed for: r4v0, types: [com.sonymobile.calendar.datetimepicker.date.SimpleMonthAdapter] */
    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        SimpleMonthView simpleMonthView;
        boolean zIsLunarOn = this.mController.isLunarOn();
        int month = i % 12;
        int minYear = (i / 12) + this.mController.getMinYear();
        HashMap<String, Integer> map = null;
        boolean isLeap = false;
        isLeap = false;
        if (zIsLunarOn) {
            if (view != null && (view instanceof LunarSimpleMonthView)) {
                simpleMonthView = (LunarSimpleMonthView) view;
                map = (HashMap) simpleMonthView.getTag();
            } else {
                simpleMonthView = new LunarSimpleMonthView(this.mContext);
                setUpNewView(simpleMonthView);
            }
            LunarHelper.LunarMonthDate lunarMonthFromPosition = LunarHelper.getLunarMonthFromPosition(this.mController.getMinYear(), i);
            minYear = lunarMonthFromPosition.getYear();
            month = lunarMonthFromPosition.getMonth();
            isLeap = lunarMonthFromPosition.getIsLeap();
        } else if (view != null && !(view instanceof LunarSimpleMonthView)) {
            simpleMonthView = (SimpleMonthView) view;
            map = (HashMap) simpleMonthView.getTag();
        } else {
            simpleMonthView = new SimpleMonthView(this.mContext);
            setUpNewView(simpleMonthView);
        }
        if (map == null) {
            map = new HashMap<>();
        }
        map.clear();
        Log.d(TAG, "Year: " + minYear + ", Month: " + month);
        int selectedDayInMonth = getSelectedDayInMonth(minYear, month, isLeap ? 1 : 0);
        simpleMonthView.reuse();
        map.put(SimpleMonthView.VIEW_PARAMS_SELECTED_DAY, Integer.valueOf(selectedDayInMonth));
        map.put("year", Integer.valueOf(minYear));
        map.put("month", Integer.valueOf(month));
            map.put(SimpleMonthView.VIEW_PARAMS_LEAP, Integer.valueOf(isLeap ? 1 : 0));
        map.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, Integer.valueOf(this.mController.getFirstDayOfWeek()));
        simpleMonthView.setMonthParams(map);
        simpleMonthView.invalidate();
        return simpleMonthView;
    }

    private int getSelectedDayInMonth(int i, int i2, int i3) {
        if (this.mController.isLunarOn()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(this.mSelectedDay.year, this.mSelectedDay.month, this.mSelectedDay.day);
            LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime());
            if (lunarDateConvertSolarDateToLunarDate.mYear != i || lunarDateConvertSolarDateToLunarDate.mMonth != i2) {
                return -1;
            }
            if (lunarDateConvertSolarDateToLunarDate.mIsLeap == (i3 == 1)) {
                return lunarDateConvertSolarDateToLunarDate.mDay;
            }
            return -1;
        }
        if (this.mSelectedDay.year == i && this.mSelectedDay.month == i2) {
            return this.mSelectedDay.day;
        }
        return -1;
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.SimpleMonthView.OnDayClickListener
    public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay) {
        if (calendarDay != null) {
            onDayTapped(calendarDay);
        }
    }

    protected void onDayTapped(CalendarDay calendarDay) {
        this.mController.tryVibrate();
        this.mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day);
        setSelectedDay(calendarDay);
    }

    private void setUpNewView(SimpleMonthView simpleMonthView) {
        simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(-1, -1));
        simpleMonthView.setClickable(true);
        simpleMonthView.setOnDayClickListener(this);
    }
}
