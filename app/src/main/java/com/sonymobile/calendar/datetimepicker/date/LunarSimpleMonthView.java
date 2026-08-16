package com.sonymobile.calendar.datetimepicker.date;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import com.sonymobile.calendar.R;
import com.sonymobile.lunar.lib.LunarUtils;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class LunarSimpleMonthView extends SimpleMonthView {
    private final Calendar mCalendar;
    private final Calendar mDayLabelCalendar;
    private int mDayOfWeekStart;
    private boolean mIsLeap;
    private int mNumRows;
    private SimpleMonthView.OnDayClickListener mOnDayClickListener;

    public LunarSimpleMonthView(Context context) {
        super(context);
        this.mNumRows = 6;
        this.mIsLeap = false;
        this.mDayOfWeekStart = 0;
        Resources resources = context.getResources();
        this.mDayLabelCalendar = Calendar.getInstance();
        this.mCalendar = Calendar.getInstance();
        this.mMiniDayNumberTextSize = resources.getDimensionPixelSize(R.dimen.lunar_day_number_size);
        initView();
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.SimpleMonthView
    public void setOnDayClickListener(SimpleMonthView.OnDayClickListener onDayClickListener) {
        this.mOnDayClickListener = onDayClickListener;
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.SimpleMonthView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int dayFromLocation;
        if (motionEvent.getAction() == 1 && (dayFromLocation = getDayFromLocation(motionEvent.getX(), motionEvent.getY())) >= 0) {
            onDayClick(dayFromLocation);
        }
        return true;
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.SimpleMonthView, android.view.View
    protected void onDraw(Canvas canvas) {
        drawMonthTitle(canvas);
        drawMonthDayLabels(canvas);
        drawMonthNums(canvas);
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.SimpleMonthView
    public void setMonthParams(HashMap<String, Integer> map) {
        if (!map.containsKey("month") && !map.containsKey("year")) {
            throw new InvalidParameterException("You must specify the month and year for this view");
        }
        setTag(map);
        if (map.containsKey(SimpleMonthView.VIEW_PARAMS_HEIGHT)) {
            this.mRowHeight = map.get(SimpleMonthView.VIEW_PARAMS_HEIGHT).intValue();
            if (this.mRowHeight < 10) {
                this.mRowHeight = 10;
            }
        }
        if (map.containsKey(SimpleMonthView.VIEW_PARAMS_SELECTED_DAY)) {
            this.mSelectedDay = map.get(SimpleMonthView.VIEW_PARAMS_SELECTED_DAY).intValue();
        }
        this.mMonth = map.get("month").intValue();
        this.mYear = map.get("year").intValue();
        int i = 0;
        this.mIsLeap = map.get(SimpleMonthView.VIEW_PARAMS_LEAP).intValue() == 1;
        Time time = new SafeTime(Time.getCurrentTimezone());
        time.setToNow();
        this.mHasToday = false;
        this.mToday = -1;
        LunarUtils.LunarDate lunarDate = new LunarUtils.LunarDate();
        lunarDate.mYear = this.mYear;
        lunarDate.mMonth = (byte) this.mMonth;
        lunarDate.mDay = (byte) 1;
        lunarDate.mIsLeap = this.mIsLeap;
        Date dateConvertLunarDateToSolarDate = LunarUtils.convertLunarDateToSolarDate(lunarDate);
        if (dateConvertLunarDateToSolarDate == null) {
            dateConvertLunarDateToSolarDate = new Date();
        }
        this.mCalendar.setTime(dateConvertLunarDateToSolarDate);
        this.mDayOfWeekStart = this.mCalendar.get(7);
        if (map.containsKey(SimpleMonthView.VIEW_PARAMS_WEEK_START)) {
            this.mWeekStart = map.get(SimpleMonthView.VIEW_PARAMS_WEEK_START).intValue();
        } else {
            this.mWeekStart = this.mCalendar.getFirstDayOfWeek();
        }
        if (this.mIsLeap) {
            this.mNumCells = LunarUtils.getDaysOfLeapMonth(this.mYear);
        } else {
            this.mNumCells = LunarUtils.getDaysOfLunarMonth(this.mYear, this.mMonth);
        }
        while (i < this.mNumCells) {
            i++;
            if (sameDay(i, time)) {
                this.mHasToday = true;
                this.mToday = i;
            }
        }
        this.mNumRows = calculateNumRows();
    }

    private int calculateNumRows() {
        int iFindDayOffset = findDayOffset();
        return ((this.mNumCells + iFindDayOffset) / this.mNumDays) + ((iFindDayOffset + this.mNumCells) % this.mNumDays > 0 ? 1 : 0);
    }

    private boolean sameDay(int i, Time time) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(time.year, time.month, time.monthDay);
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime());
        return this.mYear == lunarDateConvertSolarDateToLunarDate.mYear && this.mMonth == lunarDateConvertSolarDateToLunarDate.mMonth && i == lunarDateConvertSolarDateToLunarDate.mDay && this.mIsLeap == lunarDateConvertSolarDateToLunarDate.mIsLeap;
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.SimpleMonthView, android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), (this.mRowHeight * this.mNumRows) + this.mMonthHeaderSize);
    }

    private String getMonthAndYearString() {
        StringBuilder sb = new StringBuilder(LunarUtils.sLunarYearStrings[this.mYear - 1901].substring(0, 4));
        if (this.mIsLeap) {
            sb.append(LunarUtils.sLunarMonthStrings[0]).append(LunarUtils.sLunarMonthStrings[this.mMonth]);
        } else {
            sb.append(LunarUtils.sLunarMonthStrings[this.mMonth]);
        }
        return sb.toString();
    }

    protected void drawMonthTitle(Canvas canvas) {
        canvas.drawText(getMonthAndYearString(), (this.mWidth + (this.mPadding * 2)) / 2, ((this.mMonthHeaderSize - this.mMonthDayLabelTextSize) / 2) + (this.mMonthLabelTextSize / 3), this.mMonthTitlePaint);
    }

    protected void drawMonthDayLabels(Canvas canvas) {
        int i = this.mMonthHeaderSize - (this.mMonthDayLabelTextSize / 2);
        int i2 = (this.mWidth - (this.mPadding * 2)) / (this.mNumDays * 2);
        for (int i3 = 0; i3 < this.mNumDays; i3++) {
            int i4 = (this.mWeekStart + i3) % this.mNumDays;
            int i5 = (((i3 * 2) + 1) * i2) + this.mPadding;
            this.mDayLabelCalendar.set(7, i4);
            canvas.drawText(this.mDayLabelCalendar.getDisplayName(7, 1, Locale.getDefault()).toUpperCase(Locale.getDefault()), i5, i, this.mMonthDayLabelPaint);
        }
    }

    protected void drawMonthNums(Canvas canvas) {
        int i = (((this.mRowHeight + this.mMiniDayNumberTextSize) / 2) - 1) + this.mMonthHeaderSize;
        int i2 = (this.mWidth - (this.mPadding * 2)) / (this.mNumDays * 2);
        int iFindDayOffset = findDayOffset();
        for (int i3 = 1; i3 <= this.mNumCells; i3++) {
            int i4 = (((iFindDayOffset * 2) + 1) * i2) + this.mPadding;
            if (this.mSelectedDay == i3) {
                canvas.drawCircle(i4, i - (this.mMiniDayNumberTextSize / 3), this.mDaySelectedCircleSize, this.mSelectedCirclePaint);
            }
            if (this.mSelectedDay == i3) {
                this.mMonthNumPaint.setColor(this.mDayTextWhiteColor);
            } else if (this.mHasToday && this.mToday == i3) {
                this.mMonthNumPaint.setColor(this.mTodayNumberColor);
            } else {
                this.mMonthNumPaint.setColor(this.mDayTextColor);
            }
            canvas.drawText(LunarUtils.sLunarDayStrings[i3], i4, i, this.mMonthNumPaint);
            iFindDayOffset++;
            if (iFindDayOffset == this.mNumDays) {
                iFindDayOffset = 0;
                i += this.mRowHeight;
            }
        }
    }

    private int findDayOffset() {
        return (this.mDayOfWeekStart < this.mWeekStart ? this.mDayOfWeekStart + this.mNumDays : this.mDayOfWeekStart) - this.mWeekStart;
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.SimpleMonthView
    public int getDayFromLocation(float f, float f2) {
        int i = this.mPadding;
        float f3 = i;
        if (f >= f3 && f <= this.mWidth - this.mPadding) {
            int iFindDayOffset = (((int) (((f - f3) * this.mNumDays) / ((this.mWidth - i) - this.mPadding))) - findDayOffset()) + 1 + ((((int) (f2 - this.mMonthHeaderSize)) / this.mRowHeight) * this.mNumDays);
            if (iFindDayOffset >= 1 && iFindDayOffset <= this.mNumCells) {
                return iFindDayOffset;
            }
        }
        return -1;
    }

    private void onDayClick(int i) {
        if (this.mOnDayClickListener != null) {
            Calendar calendar = Calendar.getInstance();
            LunarUtils.LunarDate lunarDate = new LunarUtils.LunarDate();
            lunarDate.mYear = this.mYear;
            lunarDate.mMonth = (byte) this.mMonth;
            lunarDate.mDay = (byte) i;
            lunarDate.mIsLeap = this.mIsLeap;
            Date dateConvertLunarDateToSolarDate = LunarUtils.convertLunarDateToSolarDate(lunarDate);
            if (dateConvertLunarDateToSolarDate == null) {
                dateConvertLunarDateToSolarDate = new Date();
            }
            calendar.setTime(dateConvertLunarDateToSolarDate);
            this.mOnDayClickListener.onDayClick(this, new SimpleMonthAdapter.CalendarDay(calendar.get(1), calendar.get(2), calendar.get(5)));
        }
    }
}
