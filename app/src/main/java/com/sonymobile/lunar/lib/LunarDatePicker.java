package com.sonymobile.lunar.lib;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import com.google.common.base.Ascii;
import com.sonymobile.calendar.R;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class LunarDatePicker extends FrameLayout {
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int DEFAULT_END_YEAR = 2099;
    private static final int DEFAULT_START_YEAR = 1901;
    private static final String LOG_TAG = "LunarDatePicker";
    private Context mContext;
    private Calendar mCurrentDate;
    private Locale mCurrentLocale;
    private final DateFormat mDateFormat;
    private final NumberPicker mDaySpinner;
    private final EditText mDaySpinnerInput;
    private boolean mIsEnabled;
    private String[] mLunarDays;
    private String[] mLunarMonths;
    private String[] mLunarYears;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private final NumberPicker mMonthSpinner;
    private final EditText mMonthSpinnerInput;
    private int mNumberOfMonths;
    private OnLunarDateChangedListener mOnLunarDateChangedListener;
    private final LinearLayout mSpinners;
    private Calendar mTempDate;
    private final NumberPicker mYearSpinner;
    private final EditText mYearSpinnerInput;
    private static final int ID_PICKER = R.id.pickers;
    private static final int ID_YEAR_PICKER = R.id.lunar_year_picker;
    private static final int ID_MONTH_PICKER = R.id.lunar_month_picker;
    private static final int ID_DAY_PICKER = R.id.lunar_day_picker;

    public interface OnLunarDateChangedListener {
        void onLunarDateChanged(LunarDatePicker lunarDatePicker, int i, int i2, int i3);
    }

    public LunarDatePicker(Context context) {
        this(context, null);
    }

    public LunarDatePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, android.R.attr.datePickerStyle);
    }

    public LunarDatePicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        this.mIsEnabled = true;
        this.mContext = context;
        setCurrentLocale(Locale.getDefault());
        this.mMinDate.set(1901, 1, 19);
        this.mMaxDate.set(2099, 11, 31);
        addView(inflatePickerView(context));
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: com.sonymobile.lunar.lib.LunarDatePicker.1
            @Override // android.widget.NumberPicker.OnValueChangeListener
            public void onValueChange(NumberPicker numberPicker, int i2, int i3) {
                LunarDatePicker.this.updateInputState();
                LunarDatePicker.this.mTempDate.setTimeInMillis(LunarDatePicker.this.mCurrentDate.getTimeInMillis());
                LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(LunarDatePicker.this.mTempDate.getTime());
                if (numberPicker == LunarDatePicker.this.mDaySpinner) {
                    LunarDatePicker.this.performDayPickerValueChange(i2, i3, lunarDateConvertSolarDateToLunarDate);
                } else if (numberPicker == LunarDatePicker.this.mMonthSpinner) {
                    LunarDatePicker.this.performMonthPickerValueChange(i2, i3, lunarDateConvertSolarDateToLunarDate);
                } else if (numberPicker == LunarDatePicker.this.mYearSpinner) {
                    LunarDatePicker.this.performYearPickerValueChange(i2, i3, lunarDateConvertSolarDateToLunarDate);
                } else {
                    throw new IllegalArgumentException();
                }
                if (lunarDateConvertSolarDateToLunarDate.isBeforeMin()) {
                    lunarDateConvertSolarDateToLunarDate.setToMin();
                } else if (lunarDateConvertSolarDateToLunarDate.isAfterMax()) {
                    lunarDateConvertSolarDateToLunarDate.setToMax();
                }
                LunarDatePicker.this.mTempDate.setTime(LunarUtils.convertLunarDateToSolarDate(lunarDateConvertSolarDateToLunarDate));
                LunarDatePicker lunarDatePicker = LunarDatePicker.this;
                lunarDatePicker.setDate(lunarDatePicker.mTempDate.get(1), LunarDatePicker.this.mTempDate.get(2), LunarDatePicker.this.mTempDate.get(5));
                LunarDatePicker.this.updateSpinners();
                LunarDatePicker.this.notifyDateChanged();
            }
        };
        this.mSpinners = (LinearLayout) findViewById(ID_PICKER);
        NumberPicker numberPicker = (NumberPicker) findViewById(ID_DAY_PICKER);
        this.mDaySpinner = numberPicker;
        numberPicker.setOnLongPressUpdateInterval(100L);
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        this.mDaySpinnerInput = getEditTextInNumberPicker(numberPicker);
        NumberPicker numberPicker2 = (NumberPicker) findViewById(ID_MONTH_PICKER);
        this.mMonthSpinner = numberPicker2;
        numberPicker2.setMinValue(1);
        numberPicker2.setMaxValue(this.mNumberOfMonths);
        numberPicker2.setOnLongPressUpdateInterval(200L);
        numberPicker2.setOnValueChangedListener(onValueChangeListener);
        this.mMonthSpinnerInput = getEditTextInNumberPicker(numberPicker2);
        NumberPicker numberPicker3 = (NumberPicker) findViewById(ID_YEAR_PICKER);
        this.mYearSpinner = numberPicker3;
        numberPicker3.setOnLongPressUpdateInterval(100L);
        numberPicker3.setOnValueChangedListener(onValueChangeListener);
        this.mYearSpinnerInput = getEditTextInNumberPicker(numberPicker3);
        setSpinnersShown(true);
        this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        init(this.mCurrentDate.get(1), this.mCurrentDate.get(2), this.mCurrentDate.get(5), null);
        reorderSpinners();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
    }

    public void setMinDate(long j) {
        this.mTempDate.setTimeInMillis(j);
        if (this.mTempDate.get(1) != this.mMinDate.get(1) || this.mTempDate.get(6) == this.mMinDate.get(6)) {
            this.mMinDate.setTimeInMillis(j);
            if (this.mCurrentDate.before(this.mMinDate)) {
                this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
            }
            updateSpinners();
        }
    }

    public void setMaxDate(long j) {
        this.mTempDate.setTimeInMillis(j);
        if (this.mTempDate.get(1) != this.mMaxDate.get(1) || this.mTempDate.get(6) == this.mMaxDate.get(6)) {
            this.mMaxDate.setTimeInMillis(j);
            if (this.mCurrentDate.after(this.mMaxDate)) {
                this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
            }
            updateSpinners();
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        if (this.mIsEnabled == z) {
            return;
        }
        super.setEnabled(z);
        this.mDaySpinner.setEnabled(z);
        this.mMonthSpinner.setEnabled(z);
        this.mYearSpinner.setEnabled(z);
        this.mIsEnabled = z;
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.getText().add(DateUtils.formatDateTime(this.mContext, this.mCurrentDate.getTimeInMillis(), 20));
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(LunarDatePicker.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(LunarDatePicker.class.getName());
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setCurrentLocale(configuration.locale);
    }

    public boolean getSpinnersShown() {
        return this.mSpinners.isShown();
    }

    public void setSpinnersShown(boolean z) {
        this.mSpinners.setVisibility(z ? 0 : 8);
    }

    private void setCurrentLocale(Locale locale) {
        if (locale.equals(this.mCurrentLocale)) {
            return;
        }
        this.mCurrentLocale = locale;
        this.mTempDate = getCalendarForLocale(this.mTempDate, locale);
        this.mMinDate = getCalendarForLocale(this.mMinDate, locale);
        this.mMaxDate = getCalendarForLocale(this.mMaxDate, locale);
        this.mCurrentDate = getCalendarForLocale(this.mCurrentDate, locale);
        this.mNumberOfMonths = this.mTempDate.getActualMaximum(2) + 1;
        this.mLunarDays = LunarUtils.sLunarDayStrings;
        this.mLunarMonths = LunarUtils.sLunarMonthStrings;
        this.mLunarYears = LunarUtils.sLunarYearStringsMedium;
    }

    private Calendar getCalendarForLocale(Calendar calendar, Locale locale) {
        if (calendar == null) {
            return Calendar.getInstance(locale);
        }
        long timeInMillis = calendar.getTimeInMillis();
        Calendar calendar2 = Calendar.getInstance(locale);
        calendar2.setTimeInMillis(timeInMillis);
        return calendar2;
    }

    private void reorderSpinners() {
        this.mSpinners.removeAllViews();
        char[] dateFormatOrder = android.text.format.DateFormat.getDateFormatOrder(getContext());
        int length = dateFormatOrder.length;
        for (int i = 0; i < length; i++) {
            char c = dateFormatOrder[i];
            if (c == 'M') {
                this.mSpinners.addView(this.mMonthSpinner);
                setImeOptions(this.mMonthSpinner, length, i);
            } else if (c == 'd') {
                this.mSpinners.addView(this.mDaySpinner);
                setImeOptions(this.mDaySpinner, length, i);
            } else if (c == 'y') {
                this.mSpinners.addView(this.mYearSpinner);
                setImeOptions(this.mYearSpinner, length, i);
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    public void updateDate(int i, int i2, int i3) {
        if (isNewDate(i, i2, i3)) {
            setDate(i, i2, i3);
            updateSpinners();
            notifyDateChanged();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchThawSelfOnly(sparseArray);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), getYear(), getMonth(), getDayOfMonth());
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setDate(savedState.mYear, savedState.mMonth, savedState.mDay);
        updateSpinners();
    }

    public void init(int i, int i2, int i3, OnLunarDateChangedListener onLunarDateChangedListener) {
        setDate(i, i2, i3);
        updateSpinners();
        this.mOnLunarDateChangedListener = onLunarDateChangedListener;
    }

    private boolean parseDate(String str, Calendar calendar) {
        try {
            calendar.setTime(this.mDateFormat.parse(str));
            return true;
        } catch (ParseException unused) {
            Log.w(LOG_TAG, "Date: " + str + " not in format: " + DATE_FORMAT);
            return false;
        }
    }

    private boolean isNewDate(int i, int i2, int i3) {
        return (this.mCurrentDate.get(1) == i && this.mCurrentDate.get(2) == i3 && this.mCurrentDate.get(5) == i2) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDate(int i, int i2, int i3) {
        this.mCurrentDate.set(i, i2, i3);
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
        } else if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSpinners() {
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(this.mCurrentDate.getTime());
        int leapMonth = LunarUtils.getLeapMonth(lunarDateConvertSolarDateToLunarDate.mYear);
        if (this.mCurrentDate.equals(this.mMinDate)) {
            this.mDaySpinner.setMinValue(lunarDateConvertSolarDateToLunarDate.mDay);
            this.mDaySpinner.setMaxValue(LunarUtils.getDaysOfLunarMonth(lunarDateConvertSolarDateToLunarDate.mYear, lunarDateConvertSolarDateToLunarDate.mMonth));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(lunarDateConvertSolarDateToLunarDate.mMonth);
            this.mMonthSpinner.setMaxValue(12);
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else if (this.mCurrentDate.equals(this.mMaxDate)) {
            this.mDaySpinner.setMinValue(1);
            this.mDaySpinner.setMaxValue(lunarDateConvertSolarDateToLunarDate.mDay);
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(1);
            this.mMonthSpinner.setMaxValue((leapMonth <= 0 || leapMonth > lunarDateConvertSolarDateToLunarDate.mMonth) ? lunarDateConvertSolarDateToLunarDate.mMonth : lunarDateConvertSolarDateToLunarDate.mMonth + 1);
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else {
            this.mDaySpinner.setDisplayedValues(null);
            this.mDaySpinner.setMinValue(1);
            this.mDaySpinner.setMaxValue(LunarUtils.getDaysOfLunarMonth(lunarDateConvertSolarDateToLunarDate.mYear, lunarDateConvertSolarDateToLunarDate.mMonth));
            this.mDaySpinner.setWrapSelectorWheel(true);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(1);
            this.mMonthSpinner.setMaxValue(12);
            if (leapMonth != 0) {
                this.mMonthSpinner.setMaxValue(13);
                if (lunarDateConvertSolarDateToLunarDate.mIsLeap) {
                    this.mDaySpinner.setMaxValue(LunarUtils.getDaysOfLeapMonth(lunarDateConvertSolarDateToLunarDate.mYear));
                }
            }
            this.mMonthSpinner.setWrapSelectorWheel(true);
        }
        this.mDaySpinner.setDisplayedValues((String[]) Arrays.copyOfRange(this.mLunarDays, this.mDaySpinner.getMinValue(), this.mDaySpinner.getMaxValue() + 1));
        String[] strArr = (String[]) Arrays.copyOfRange(this.mLunarMonths, this.mMonthSpinner.getMinValue(), this.mMonthSpinner.getMaxValue() + 1);
        if (leapMonth != 0) {
            String str = this.mLunarMonths[0] + this.mLunarMonths[leapMonth].substring(0, 1);
            for (int length = strArr.length - 2; length >= leapMonth; length--) {
                strArr[length + 1] = strArr[length];
            }
            strArr[leapMonth] = str;
        }
        this.mMonthSpinner.setDisplayedValues(strArr);
        this.mYearSpinner.setDisplayedValues(this.mLunarYears);
        this.mYearSpinner.setMinValue(this.mMinDate.get(1));
        this.mYearSpinner.setMaxValue(this.mMaxDate.get(1));
        this.mYearSpinner.setWrapSelectorWheel(false);
        this.mYearSpinner.setValue(lunarDateConvertSolarDateToLunarDate.mYear);
        this.mMonthSpinner.setValue(lunarDateConvertSolarDateToLunarDate.mMonth);
        if ((leapMonth > 0 && lunarDateConvertSolarDateToLunarDate.mMonth > leapMonth) || (lunarDateConvertSolarDateToLunarDate.mMonth == leapMonth && lunarDateConvertSolarDateToLunarDate.mIsLeap)) {
            this.mMonthSpinner.setValue(lunarDateConvertSolarDateToLunarDate.mMonth + 1);
        }
        this.mDaySpinner.setValue(lunarDateConvertSolarDateToLunarDate.mDay);
    }

    public int getYear() {
        return this.mCurrentDate.get(1);
    }

    public int getMonth() {
        return this.mCurrentDate.get(2);
    }

    public int getDayOfMonth() {
        return this.mCurrentDate.get(5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDateChanged() {
        sendAccessibilityEvent(4);
        OnLunarDateChangedListener onLunarDateChangedListener = this.mOnLunarDateChangedListener;
        if (onLunarDateChangedListener != null) {
            onLunarDateChangedListener.onLunarDateChanged(this, getYear(), getMonth(), getDayOfMonth());
        }
    }

    private void setImeOptions(NumberPicker numberPicker, int i, int i2) {
        getEditTextInNumberPicker(numberPicker).setImeOptions(i2 < i + (-1) ? 5 : 6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInputState() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.mContext.getSystemService("input_method");
        if (inputMethodManager != null) {
            if (inputMethodManager.isActive(this.mYearSpinnerInput)) {
                this.mYearSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(this.mMonthSpinnerInput)) {
                this.mMonthSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(this.mDaySpinnerInput)) {
                this.mDaySpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    private static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.sonymobile.lunar.lib.LunarDatePicker.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        private final int mDay;
        private final int mMonth;
        private final int mYear;

        private SavedState(Parcelable parcelable, int i, int i2, int i3) {
            super(parcelable);
            this.mYear = i;
            this.mMonth = i2;
            this.mDay = i3;
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mYear = parcel.readInt();
            this.mMonth = parcel.readInt();
            this.mDay = parcel.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mYear);
            parcel.writeInt(this.mMonth);
            parcel.writeInt(this.mDay);
        }
    }

    private EditText getEditTextInNumberPicker(NumberPicker numberPicker) {
        try {
            Field declaredField = numberPicker.getClass().getDeclaredField("mInputText");
            declaredField.setAccessible(true);
            try {
                return (EditText) declaredField.get(numberPicker);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return null;
            }
        } catch (NoSuchFieldException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    private LinearLayout inflatePickerView(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        NumberPicker numberPicker = new NumberPicker(context);
        layoutParams.setMarginStart(dp2px(context, 8.0f));
        layoutParams.setMarginEnd(dp2px(context, 16.0f));
        numberPicker.setLayoutParams(layoutParams);
        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);
        numberPicker.setId(ID_YEAR_PICKER);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        NumberPicker numberPicker2 = new NumberPicker(context);
        layoutParams2.setMarginStart(dp2px(context, 8.0f));
        layoutParams2.setMarginEnd(dp2px(context, 8.0f));
        numberPicker2.setLayoutParams(layoutParams2);
        numberPicker2.setFocusable(true);
        numberPicker2.setFocusableInTouchMode(true);
        numberPicker2.setId(ID_MONTH_PICKER);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-2, -2);
        NumberPicker numberPicker3 = new NumberPicker(context);
        layoutParams3.setMarginStart(dp2px(context, 8.0f));
        layoutParams3.setMarginEnd(dp2px(context, 8.0f));
        numberPicker3.setLayoutParams(layoutParams3);
        numberPicker3.setFocusable(true);
        numberPicker3.setFocusableInTouchMode(true);
        numberPicker3.setId(ID_DAY_PICKER);
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(-2, -2);
        LinearLayout linearLayout = new LinearLayout(context, null);
        layoutParams4.gravity = 17;
        linearLayout.setLayoutParams(layoutParams4);
        linearLayout.setOrientation(0);
        linearLayout.setId(ID_PICKER);
        linearLayout.addView(numberPicker);
        linearLayout.addView(numberPicker2);
        linearLayout.addView(numberPicker3);
        return linearLayout;
    }

    private int dp2px(Context context, float f) {
        return (int) (f * context.getResources().getDisplayMetrics().density);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDayPickerValueChange(int i, int i2, LunarUtils.LunarDate lunarDate) {
        int daysOfLunarMonth;
        int daysOfLunarMonth2 = LunarUtils.getDaysOfLunarMonth(lunarDate.mYear, lunarDate.mMonth);
        if (lunarDate.mIsLeap) {
            daysOfLunarMonth2 = LunarUtils.getDaysOfLeapMonth(lunarDate.mYear);
        }
        if (i == daysOfLunarMonth2 && i2 == 1) {
            if (LunarUtils.getLeapMonth(lunarDate.mYear) == lunarDate.mMonth && !lunarDate.mIsLeap) {
                lunarDate.mIsLeap = true;
            } else {
                lunarDate.mIsLeap = false;
                lunarDate.mMonth = (byte) (lunarDate.mMonth + 1);
            }
            if (lunarDate.mMonth == 13) {
                lunarDate.mYear++;
                lunarDate.mMonth = (byte) 1;
            }
            if (lunarDate.mYear > 2099) {
                LunarUtils.convertSolarDateToLunarDate(this.mMaxDate.getTime());
                return;
            } else {
                lunarDate.mDay = (byte) 1;
                return;
            }
        }
        if (i == 1 && i2 == daysOfLunarMonth2) {
            if (LunarUtils.getLeapMonth(lunarDate.mYear) > 0 && LunarUtils.getLeapMonth(lunarDate.mYear) == lunarDate.mMonth - 1) {
                lunarDate.mIsLeap = true;
                lunarDate.mMonth = (byte) (lunarDate.mMonth - 1);
            } else {
                lunarDate.mMonth = (byte) (lunarDate.mMonth - (!lunarDate.mIsLeap ? 1 : 0));
                lunarDate.mIsLeap = false;
            }
            if (lunarDate.mMonth == 0) {
                lunarDate.mYear--;
                lunarDate.mMonth = Ascii.FF;
            }
            if (lunarDate.mYear < 1901) {
                LunarUtils.convertSolarDateToLunarDate(this.mMinDate.getTime());
                return;
            }
            if (lunarDate.mIsLeap) {
                daysOfLunarMonth = LunarUtils.getDaysOfLeapMonth(lunarDate.mYear);
            } else {
                daysOfLunarMonth = LunarUtils.getDaysOfLunarMonth(lunarDate.mYear, lunarDate.mMonth);
            }
            lunarDate.mDay = (byte) daysOfLunarMonth;
            return;
        }
        lunarDate.mDay = (byte) (lunarDate.mDay + (i2 - i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performMonthPickerValueChange(int i, int i2, LunarUtils.LunarDate lunarDate) {
        int iMin;
        int leapMonth = LunarUtils.getLeapMonth(lunarDate.mYear);
        int i3 = leapMonth > 0 ? 13 : 12;
        if (i == i3 && i2 == 1) {
            lunarDate.mYear++;
            lunarDate.mMonth = (byte) 1;
        } else if (i == 1 && i2 == i3) {
            lunarDate.mYear--;
            lunarDate.mMonth = Ascii.FF;
        } else if (leapMonth > 0 && i2 == leapMonth + 1) {
            lunarDate.mIsLeap = true;
            if (i == leapMonth + 2) {
                lunarDate.mMonth = (byte) (lunarDate.mMonth - 1);
            }
        } else if (leapMonth > 0 && i == leapMonth + 1) {
            lunarDate.mIsLeap = false;
            if (i2 == leapMonth + 2) {
                lunarDate.mMonth = (byte) (lunarDate.mMonth + 1);
            }
        } else {
            lunarDate.mMonth = (byte) (lunarDate.mMonth + (i2 - i));
            lunarDate.mIsLeap = false;
        }
        if (lunarDate.isBeforeMin()) {
            lunarDate.setToMin();
            return;
        }
        if (lunarDate.isAfterMax()) {
            lunarDate.setToMax();
            return;
        }
        if (lunarDate.mIsLeap) {
            iMin = Math.min((int) lunarDate.mDay, LunarUtils.getDaysOfLeapMonth(lunarDate.mYear));
        } else {
            iMin = Math.min((int) lunarDate.mDay, LunarUtils.getDaysOfLunarMonth(lunarDate.mYear, lunarDate.mMonth));
        }
        lunarDate.mDay = (byte) iMin;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performYearPickerValueChange(int i, int i2, LunarUtils.LunarDate lunarDate) {
        int iMin;
        lunarDate.mYear = i2;
        if (LunarUtils.getLeapMonth(lunarDate.mYear) != lunarDate.mMonth && lunarDate.mIsLeap) {
            lunarDate.mIsLeap = false;
        }
        if (lunarDate.mIsLeap) {
            iMin = Math.min((int) lunarDate.mDay, LunarUtils.getDaysOfLeapMonth(lunarDate.mYear));
        } else {
            iMin = Math.min((int) lunarDate.mDay, LunarUtils.getDaysOfLunarMonth(lunarDate.mYear, lunarDate.mMonth));
        }
        lunarDate.mDay = (byte) iMin;
    }
}
