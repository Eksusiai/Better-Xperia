package com.sonymobile.calendar.datetimepicker.date;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.datetimepicker.Utils;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class SimpleMonthView extends View {
    protected static final int DAY_SEPARATOR_WIDTH = 1;
    private static final int DEFAULT_HEIGHT = 32;
    private static final int DEFAULT_NUM_DAYS = 7;
    protected static final int DEFAULT_NUM_ROWS = 6;
    private static final int DEFAULT_SELECTED_DAY = -1;
    private static final int DEFAULT_WEEK_START = 1;
    private static final int MAX_NUM_ROWS = 6;
    protected static final int MIN_HEIGHT = 10;
    public static final String VIEW_PARAMS_HEIGHT = "height";
    public static final String VIEW_PARAMS_LEAP = "leap";
    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_SELECTED_DAY = "selected_day";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";
    public static final String VIEW_PARAMS_YEAR = "year";
    private final Calendar mCalendar;
    private final Calendar mDayLabelCalendar;
    private int mDayOfWeekStart;
    private String mDayOfWeekTypeface;
    protected final int mDaySelectedCircleSize;
    protected final int mDayTextColor;
    protected final int mDayTextWhiteColor;
    private final Formatter mFormatter;
    protected boolean mHasToday;
    private boolean mLockAccessibilityDelegate;
    protected int mMiniDayNumberTextSize;
    protected int mMonth;
    protected Paint mMonthDayLabelPaint;
    protected final int mMonthDayLabelTextSize;
    protected final int mMonthHeaderSize;
    protected final int mMonthLabelTextSize;
    protected Paint mMonthNumPaint;
    protected Paint mMonthTitlePaint;
    private String mMonthTitleTypeface;
    protected int mNumCells;
    protected int mNumDays;
    private int mNumRows;
    private OnDayClickListener mOnDayClickListener;
    protected int mPadding;
    protected int mRowHeight;
    protected Paint mSelectedCirclePaint;
    protected int mSelectedDay;
    private final StringBuilder mStringBuilder;
    protected int mToday;
    protected final int mTodayNumberColor;
    protected int mWeekStart;
    protected int mWidth;
    protected int mYear;

    public interface OnDayClickListener {
        void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay);
    }

    public void clearAccessibilityFocus() {
    }

    public SimpleMonthAdapter.CalendarDay getAccessibilityFocus() {
        return null;
    }

    public SimpleMonthView(Context context) {
        super(context);
        this.mPadding = 12;
        this.mRowHeight = 32;
        this.mHasToday = false;
        this.mSelectedDay = -1;
        this.mToday = -1;
        this.mWeekStart = 1;
        this.mNumDays = 7;
        this.mNumCells = 7;
        this.mNumRows = 6;
        this.mDayOfWeekStart = 0;
        this.mDayLabelCalendar = Calendar.getInstance();
        this.mCalendar = Calendar.getInstance();
        Resources resources = context.getResources();
        this.mDayOfWeekTypeface = resources.getString(R.string.day_of_week_label_typeface);
        this.mMonthTitleTypeface = resources.getString(R.string.sans_serif);
        StringBuilder sb = new StringBuilder(50);
        this.mStringBuilder = sb;
        this.mFormatter = new Formatter(sb, Locale.getDefault());
        this.mDayTextColor = ContextCompat.getColor(context, R.color.date_picker_text_normal);
        this.mDayTextWhiteColor = ContextCompat.getColor(context, R.color.white);
        this.mTodayNumberColor = ContextCompat.getColor(context, R.color.date_picker_domain_color);
        this.mMiniDayNumberTextSize = resources.getDimensionPixelSize(R.dimen.day_number_size);
        this.mMonthLabelTextSize = resources.getDimensionPixelSize(R.dimen.month_label_size);
        this.mMonthDayLabelTextSize = resources.getDimensionPixelSize(R.dimen.month_day_label_text_size);
        int dimensionPixelOffset = resources.getDimensionPixelOffset(R.dimen.month_list_item_header_height);
        this.mMonthHeaderSize = dimensionPixelOffset;
        this.mDaySelectedCircleSize = resources.getDimensionPixelSize(R.dimen.day_number_select_circle_radius);
        this.mRowHeight = (resources.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height) - dimensionPixelOffset) / 6;
        ViewCompat.setImportantForAccessibility(this, 1);
        this.mLockAccessibilityDelegate = true;
        initView();
    }

    @Override // android.view.View
    public void setAccessibilityDelegate(View.AccessibilityDelegate accessibilityDelegate) {
        if (this.mLockAccessibilityDelegate) {
            return;
        }
        super.setAccessibilityDelegate(accessibilityDelegate);
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        this.mOnDayClickListener = onDayClickListener;
    }

    @Override // android.view.View
    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        return super.dispatchHoverEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int dayFromLocation;
        if (motionEvent.getAction() == 1 && (dayFromLocation = getDayFromLocation(motionEvent.getX(), motionEvent.getY())) >= 0) {
            onDayClick(dayFromLocation);
        }
        return true;
    }

    protected void initView() {
        Paint paint = new Paint();
        this.mMonthTitlePaint = paint;
        paint.setFakeBoldText(true);
        this.mMonthTitlePaint.setAntiAlias(true);
        this.mMonthTitlePaint.setTextSize(this.mMonthLabelTextSize);
        this.mMonthTitlePaint.setTypeface(Typeface.create(this.mMonthTitleTypeface, 1));
        this.mMonthTitlePaint.setColor(this.mDayTextColor);
        this.mMonthTitlePaint.setTextAlign(Paint.Align.CENTER);
        this.mMonthTitlePaint.setStyle(Paint.Style.FILL);
        Paint paint2 = new Paint();
        this.mSelectedCirclePaint = paint2;
        paint2.setFakeBoldText(true);
        this.mSelectedCirclePaint.setAntiAlias(true);
        this.mSelectedCirclePaint.setColor(this.mTodayNumberColor);
        this.mSelectedCirclePaint.setTextAlign(Paint.Align.CENTER);
        this.mSelectedCirclePaint.setStyle(Paint.Style.FILL);
        Paint paint3 = new Paint();
        this.mMonthDayLabelPaint = paint3;
        paint3.setAntiAlias(true);
        this.mMonthDayLabelPaint.setTextSize(this.mMonthDayLabelTextSize);
        this.mMonthDayLabelPaint.setColor(this.mDayTextColor);
        this.mMonthDayLabelPaint.setTypeface(Typeface.create(this.mDayOfWeekTypeface, 0));
        this.mMonthDayLabelPaint.setStyle(Paint.Style.FILL);
        this.mMonthDayLabelPaint.setTextAlign(Paint.Align.CENTER);
        this.mMonthDayLabelPaint.setFakeBoldText(true);
        Paint paint4 = new Paint();
        this.mMonthNumPaint = paint4;
        paint4.setAntiAlias(true);
        this.mMonthNumPaint.setTextSize(this.mMiniDayNumberTextSize);
        this.mMonthNumPaint.setStyle(Paint.Style.FILL);
        this.mMonthNumPaint.setTextAlign(Paint.Align.CENTER);
        this.mMonthNumPaint.setFakeBoldText(false);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawMonthTitle(canvas);
        drawMonthDayLabels(canvas);
        drawMonthNums(canvas);
    }

    public void setMonthParams(HashMap<String, Integer> map) {
        if (!map.containsKey("month") && !map.containsKey("year")) {
            throw new InvalidParameterException("You must specify the month and year for this view");
        }
        setTag(map);
        if (map.containsKey(VIEW_PARAMS_HEIGHT)) {
            int iIntValue = map.get(VIEW_PARAMS_HEIGHT).intValue();
            this.mRowHeight = iIntValue;
            if (iIntValue < 10) {
                this.mRowHeight = 10;
            }
        }
        if (map.containsKey(VIEW_PARAMS_SELECTED_DAY)) {
            this.mSelectedDay = map.get(VIEW_PARAMS_SELECTED_DAY).intValue();
        }
        this.mMonth = map.get("month").intValue();
        this.mYear = map.get("year").intValue();
        Time time = new SafeTime(Time.getCurrentTimezone());
        time.setToNow();
        int i = 0;
        this.mHasToday = false;
        this.mToday = -1;
        this.mCalendar.set(2, this.mMonth);
        this.mCalendar.set(1, this.mYear);
        this.mCalendar.set(5, 1);
        this.mDayOfWeekStart = this.mCalendar.get(7);
        if (map.containsKey(VIEW_PARAMS_WEEK_START)) {
            this.mWeekStart = map.get(VIEW_PARAMS_WEEK_START).intValue();
        } else {
            this.mWeekStart = this.mCalendar.getFirstDayOfWeek();
        }
        this.mNumCells = Utils.getDaysInMonth(this.mMonth, this.mYear);
        while (i < this.mNumCells) {
            i++;
            if (sameDay(i, time)) {
                this.mHasToday = true;
                this.mToday = i;
            }
        }
        this.mNumRows = calculateNumRows();
    }

    public void reuse() {
        this.mNumRows = 6;
        requestLayout();
    }

    private int calculateNumRows() {
        int iFindDayOffset = findDayOffset();
        int i = this.mNumCells;
        int i2 = this.mNumDays;
        return ((iFindDayOffset + i) / i2) + ((iFindDayOffset + i) % i2 > 0 ? 1 : 0);
    }

    private boolean sameDay(int i, Time time) {
        return this.mYear == time.year && this.mMonth == time.month && i == time.monthDay;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), (this.mRowHeight * this.mNumRows) + this.mMonthHeaderSize);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.mWidth = i;
    }

    private String getMonthAndYearString() {
        this.mStringBuilder.setLength(0);
        long timeInMillis = this.mCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(getContext(), this.mFormatter, timeInMillis, timeInMillis, 52, Time.getCurrentTimezone()).toString();
    }

    private void drawMonthTitle(Canvas canvas) {
        canvas.drawText(getMonthAndYearString(), (this.mWidth + (this.mPadding * 2)) / 2, ((this.mMonthHeaderSize - this.mMonthDayLabelTextSize) / 2) + (this.mMonthLabelTextSize / 3), this.mMonthTitlePaint);
    }

    private void drawMonthDayLabels(Canvas canvas) {
        int i = this.mMonthHeaderSize - (this.mMonthDayLabelTextSize / 2);
        int i2 = (this.mWidth - (this.mPadding * 3)) / (this.mNumDays * 2);
        int i3 = getLayoutDirection() == 0 ? this.mPadding : this.mPadding * 2;
        int i4 = 0;
        while (true) {
            int i5 = this.mNumDays;
            if (i4 >= i5) {
                return;
            }
            this.mDayLabelCalendar.set(7, (this.mWeekStart + i4) % i5);
            canvas.drawText(this.mDayLabelCalendar.getDisplayName(7, 1, Locale.getDefault()).toUpperCase(Locale.getDefault()), (((i4 * 2) + 1) * i2) + i3, i, this.mMonthDayLabelPaint);
            i4++;
        }
    }

    private void drawMonthNums(Canvas canvas) {
        int i = (((this.mRowHeight + this.mMiniDayNumberTextSize) / 2) - 1) + this.mMonthHeaderSize;
        int i2 = (this.mWidth - (this.mPadding * 3)) / (this.mNumDays * 2);
        int i3 = getLayoutDirection() == 0 ? this.mPadding : this.mPadding * 2;
        int iFindDayOffset = findDayOffset();
        for (int i4 = 1; i4 <= this.mNumCells; i4++) {
            int i5 = (((iFindDayOffset * 2) + 1) * i2) + i3;
            if (this.mSelectedDay == i4) {
                canvas.drawCircle(i5, i - (this.mMiniDayNumberTextSize / 3), this.mDaySelectedCircleSize, this.mSelectedCirclePaint);
            }
            if (this.mSelectedDay == i4) {
                this.mMonthNumPaint.setColor(this.mDayTextWhiteColor);
            } else if (this.mHasToday && this.mToday == i4) {
                this.mMonthNumPaint.setColor(this.mTodayNumberColor);
            } else {
                this.mMonthNumPaint.setColor(this.mDayTextColor);
            }
            canvas.drawText(String.format("%d", Integer.valueOf(i4)), i5, i, this.mMonthNumPaint);
            iFindDayOffset++;
            if (iFindDayOffset == this.mNumDays) {
                i += this.mRowHeight;
                iFindDayOffset = 0;
            }
        }
    }

    private int findDayOffset() {
        int i = this.mDayOfWeekStart;
        int i2 = this.mWeekStart;
        if (i < i2) {
            i += this.mNumDays;
        }
        return i - i2;
    }

    public int getDayFromLocation(float f, float f2) {
        int i = this.mPadding;
        float f3 = i;
        if (f >= f3) {
            int i2 = this.mWidth;
            if (f <= i2 - i) {
                int iFindDayOffset = (((int) (((f - f3) * this.mNumDays) / ((i2 - i) - i))) - findDayOffset()) + 1 + ((((int) (f2 - this.mMonthHeaderSize)) / this.mRowHeight) * this.mNumDays);
                if (iFindDayOffset >= 1 && iFindDayOffset <= this.mNumCells) {
                    return iFindDayOffset;
                }
            }
        }
        return -1;
    }

    private void onDayClick(int i) {
        OnDayClickListener onDayClickListener = this.mOnDayClickListener;
        if (onDayClickListener != null) {
            onDayClickListener.onDayClick(this, new SimpleMonthAdapter.CalendarDay(this.mYear, this.mMonth, i));
        }
    }

    public boolean restoreAccessibilityFocus(SimpleMonthAdapter.CalendarDay calendarDay) {
        return calendarDay.year == this.mYear && calendarDay.month == this.mMonth && calendarDay.day <= this.mNumCells;
    }
}
