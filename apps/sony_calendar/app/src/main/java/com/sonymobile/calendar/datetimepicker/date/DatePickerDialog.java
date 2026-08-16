package com.sonymobile.calendar.datetimepicker.date;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.datetimepicker.HapticFeedbackController;
import com.sonymobile.lunar.lib.LunarUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class DatePickerDialog extends DialogFragment implements View.OnClickListener, DatePickerController {
    private static final int ANIMATION_DELAY = 500;
    private static final int ANIMATION_DURATION = 300;
    private static final int DEFAULT_END_YEAR = 2037;
    private static final int DEFAULT_START_YEAR = 1970;
    private static final String KEY_CURRENT_VIEW = "current_view";
    private static final String KEY_LIST_POSITION = "list_position";
    private static final String KEY_LIST_POSITION_OFFSET = "list_position_offset";
    private static final String KEY_MODE = "mode";
    private static final String KEY_SELECTED_DAY = "day";
    private static final String KEY_SELECTED_MONTH = "month";
    private static final String KEY_SELECTED_YEAR = "year";
    private static final String KEY_WEEK_START = "week_start";
    private static final String LAST_ON_SAVE_INSTANCE_STATE_TIME = "lastOnSaveInstanceStateTime";
    public static final int MODE_COMBINE = 1;
    public static final int MODE_LUNAR = 3;
    public static final int MODE_SOLAR = 2;
    public static final int MODE_SOLAR_LUNAR_CONVERSION = 0;
    private static final int MONTH_AND_DAY_VIEW = 0;
    private static final String TAG = "DatePickerDialog";
    private static final int UNINITIALIZED = -1;
    private static final int YEAR_VIEW = 1;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;
    private AccessibleDateAnimator mAnimator;
    private final Calendar mCalendar;
    private OnDateSetListener mCallBack;
    private int mCurrentView;
    private TextView mDayOfWeekView;
    private String mDayPickerDescription;
    private DayPickerView mDayPickerView;
    private boolean mDelayAnimation;
    private HapticFeedbackController mHapticFeedbackController;
    private boolean mIsLunarOn;
    private long mLastOnSaveInstanceStateTime;
    private int mListPosition;
    private int mListPositionOffset;
    private HashSet<OnDateChangedListener> mListeners;
    private TextView mLunarSoloarConversionTextView;
    private int mMode;
    private LinearLayout mMonthAndDayView;
    private String mSelectDay;
    private String mSelectYear;
    private TextView mSelectedDayTextView;
    private TextView mSelectedMonthTextView;
    private Switch mSwitch;
    private int mWeekStart;
    private String mYearPickerDescription;
    private YearPickerView mYearPickerView;
    private TextView mYearView;
    private final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat(Utils.FORMAT_DATE_YEAR, Locale.getDefault());
    private final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());

    public interface OnDateChangedListener {
        void onDateChanged();
    }

    public interface OnDateSetListener {
        void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3);
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public int getMaxYear() {
        return 2037;
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public int getMinYear() {
        return DEFAULT_START_YEAR;
    }

    public DatePickerDialog(int i) {
        Calendar calendar = Calendar.getInstance();
        this.mCalendar = calendar;
        this.mListeners = new HashSet<>();
        this.mCurrentView = -1;
        this.mWeekStart = calendar.getFirstDayOfWeek();
        this.mDelayAnimation = true;
        this.mListPosition = -1;
        this.mListPositionOffset = -1;
        this.mIsLunarOn = false;
        this.mLastOnSaveInstanceStateTime = Long.MIN_VALUE;
        this.checkedChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                DatePickerDialog.this.mIsLunarOn = z;
                DatePickerDialog.this.updateDisplay(false);
                DatePickerDialog.this.mDayPickerView.onCheckedChange(z);
                DatePickerDialog.this.mYearPickerView.onCheckedChange(z);
            }
        };
        this.mMode = i;
    }

    public DatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        this.mCalendar = calendar;
        this.mListeners = new HashSet<>();
        this.mCurrentView = -1;
        this.mWeekStart = calendar.getFirstDayOfWeek();
        this.mDelayAnimation = true;
        this.mListPosition = -1;
        this.mListPositionOffset = -1;
        this.mIsLunarOn = false;
        this.mLastOnSaveInstanceStateTime = Long.MIN_VALUE;
        this.checkedChangeListener = new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                DatePickerDialog.this.mIsLunarOn = z;
                DatePickerDialog.this.updateDisplay(false);
                DatePickerDialog.this.mDayPickerView.onCheckedChange(z);
                DatePickerDialog.this.mYearPickerView.onCheckedChange(z);
            }
        };
    }

    public static DatePickerDialog newInstance(OnDateSetListener onDateSetListener, int i, int i2, int i3, int i4) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(i4);
        datePickerDialog.initialize(onDateSetListener, i, i2, i3);
        return datePickerDialog;
    }

    public void initialize(OnDateSetListener onDateSetListener, int i, int i2, int i3) {
        this.mCallBack = onDateSetListener;
        this.mCalendar.set(1, i);
        this.mCalendar.set(2, i2);
        this.mCalendar.set(5, i3);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().getWindow().setSoftInputMode(3);
        if (bundle != null) {
            this.mCalendar.set(1, bundle.getInt("year"));
            this.mCalendar.set(2, bundle.getInt("month"));
            this.mCalendar.set(5, bundle.getInt(KEY_SELECTED_DAY));
            this.mMode = bundle.getInt(KEY_MODE);
            this.mLastOnSaveInstanceStateTime = bundle.getLong(LAST_ON_SAVE_INSTANCE_STATE_TIME);
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("year", this.mCalendar.get(1));
        bundle.putInt("month", this.mCalendar.get(2));
        bundle.putInt(KEY_SELECTED_DAY, this.mCalendar.get(5));
        bundle.putInt("week_start", this.mWeekStart);
        bundle.putInt(KEY_CURRENT_VIEW, this.mCurrentView);
        bundle.putInt(KEY_MODE, this.mMode);
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis - this.mLastOnSaveInstanceStateTime > 1000) {
            int i = this.mCurrentView;
            if (i == 0) {
                this.mListPosition = this.mDayPickerView.getMostVisiblePosition();
            } else if (i == 1) {
                this.mListPosition = this.mYearPickerView.getFirstVisiblePosition();
                int firstPositionOffset = this.mYearPickerView.getFirstPositionOffset();
                this.mListPositionOffset = firstPositionOffset;
                bundle.putInt(KEY_LIST_POSITION_OFFSET, firstPositionOffset);
            }
        }
        bundle.putInt(KEY_LIST_POSITION, this.mListPosition);
        this.mLastOnSaveInstanceStateTime = jCurrentTimeMillis;
        bundle.putLong(LAST_ON_SAVE_INSTANCE_STATE_TIME, jCurrentTimeMillis);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int i;
        Log.d(TAG, "onCreateView: ");
        getDialog().getWindow().requestFeature(1);
        View viewInflate = layoutInflater.inflate(R.layout.date_picker_dialog, (ViewGroup) null);
        int color = ContextCompat.getColor(getContext(), R.color.date_picker_domain_color);
        TextView textView = (TextView) viewInflate.findViewById(R.id.date_picker_header);
        this.mDayOfWeekView = textView;
        textView.setBackgroundColor(color);
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.date_picker_month_and_day);
        this.mMonthAndDayView = linearLayout;
        linearLayout.setOnClickListener(this);
        this.mSelectedMonthTextView = (TextView) viewInflate.findViewById(R.id.date_picker_month);
        this.mSelectedDayTextView = (TextView) viewInflate.findViewById(R.id.date_picker_day);
        this.mSelectedMonthTextView.setTextColor(-1);
        this.mSelectedDayTextView.setTextColor(-1);
        TextView textView2 = (TextView) viewInflate.findViewById(R.id.date_picker_year);
        this.mYearView = textView2;
        textView2.setOnClickListener(this);
        this.mYearView.setTextColor(-1);
        this.mLunarSoloarConversionTextView = (TextView) viewInflate.findViewById(R.id.conversion_label);
        Switch r2 = (Switch) viewInflate.findViewById(R.id.lunar_switch);
        this.mSwitch = r2;
        r2.setOnCheckedChangeListener(this.checkedChangeListener);
        int i2 = this.mMode;
        if (i2 == 0) {
            if (viewInflate.findViewById(R.id.date_picker_header) != null) {
                viewInflate.findViewById(R.id.date_picker_header).setVisibility(8);
            }
            this.mIsLunarOn = false;
        } else if (i2 == 1) {
            this.mLunarSoloarConversionTextView.setVisibility(8);
            viewInflate.findViewById(R.id.date_picker_header).setVisibility(8);
            this.mIsLunarOn = false;
        } else if (i2 == 2) {
            viewInflate.findViewById(R.id.date_picker_lunar_header).setVisibility(8);
            this.mIsLunarOn = false;
        } else if (i2 == 3) {
            viewInflate.findViewById(R.id.date_picker_lunar_header).setVisibility(8);
            this.mIsLunarOn = true;
        }
        viewInflate.findViewById(R.id.date_picker_lunar_header).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.date_picker_header_background));
        viewInflate.findViewById(R.id.day_picker_selected_date_layout).setBackgroundColor(color);
        if (bundle != null) {
            this.mWeekStart = bundle.getInt("week_start");
            i = bundle.getInt(KEY_CURRENT_VIEW);
            this.mListPosition = bundle.getInt(KEY_LIST_POSITION);
            this.mListPositionOffset = bundle.getInt(KEY_LIST_POSITION_OFFSET);
        } else {
            i = 0;
        }
        FragmentActivity activity = getActivity();
        this.mDayPickerView = new DayPickerView(activity, this);
        this.mYearPickerView = new YearPickerView(activity, this);
        Resources resources = getResources();
        this.mDayPickerDescription = resources.getString(R.string.day_picker_description);
        this.mSelectDay = resources.getString(R.string.select_day);
        this.mYearPickerDescription = resources.getString(R.string.year_picker_description);
        this.mSelectYear = resources.getString(R.string.select_year);
        AccessibleDateAnimator accessibleDateAnimator = (AccessibleDateAnimator) viewInflate.findViewById(R.id.animator);
        this.mAnimator = accessibleDateAnimator;
        accessibleDateAnimator.addView(this.mDayPickerView);
        this.mAnimator.addView(this.mYearPickerView);
        this.mAnimator.setDateMillis(this.mCalendar.getTimeInMillis());
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300L);
        this.mAnimator.setInAnimation(alphaAnimation);
        AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation2.setDuration(300L);
        this.mAnimator.setOutAnimation(alphaAnimation2);
        Button button = (Button) viewInflate.findViewById(R.id.done);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DatePickerDialog.this.tryVibrate();
                if (DatePickerDialog.this.mCallBack != null) {
                    OnDateSetListener onDateSetListener = DatePickerDialog.this.mCallBack;
                    DatePickerDialog datePickerDialog = DatePickerDialog.this;
                    onDateSetListener.onDateSet(datePickerDialog, datePickerDialog.mCalendar.get(1), DatePickerDialog.this.mCalendar.get(2), DatePickerDialog.this.mCalendar.get(5));
                }
                DatePickerDialog.this.dismiss();
            }
        });
        button.setTextColor(color);
        updateDisplay(false);
        setCurrentView(i);
        int i3 = this.mListPosition;
        if (i3 != -1) {
            if (i == 0) {
                this.mDayPickerView.postSetSelection(i3);
            } else if (i == 1) {
                this.mYearPickerView.postSetSelectionFromTop(i3, this.mListPositionOffset);
            }
        }
        this.mHapticFeedbackController = new HapticFeedbackController(activity);
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mHapticFeedbackController.start();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mHapticFeedbackController.stop();
        FragmentActivity activity = getActivity();
        if ((activity instanceof LaunchActivity) && ((LaunchActivity) activity).isInMultiWindowMode()) {
            dismiss();
        }
    }

    private void setCurrentView(int i) {
        long timeInMillis = this.mCalendar.getTimeInMillis();
        if (i == 0) {
            ObjectAnimator pulseAnimator = com.sonymobile.calendar.datetimepicker.Utils.getPulseAnimator(this.mMonthAndDayView, 0.9f, 1.05f);
            if (this.mDelayAnimation) {
                pulseAnimator.setStartDelay(500L);
                this.mDelayAnimation = false;
            }
            this.mDayPickerView.onDateChanged();
            if (this.mCurrentView != i) {
                this.mMonthAndDayView.setSelected(true);
                this.mYearView.setSelected(false);
                this.mAnimator.setDisplayedChild(0);
                this.mCurrentView = i;
            }
            pulseAnimator.start();
            this.mAnimator.setContentDescription(this.mDayPickerDescription + ": " + DateUtils.formatDateTime(getActivity(), timeInMillis, 16));
            com.sonymobile.calendar.datetimepicker.Utils.tryAccessibilityAnnounce(this.mAnimator, this.mSelectDay);
            return;
        }
        if (i != 1) {
            return;
        }
        ObjectAnimator pulseAnimator2 = com.sonymobile.calendar.datetimepicker.Utils.getPulseAnimator(this.mYearView, 0.85f, 1.1f);
        if (this.mDelayAnimation) {
            pulseAnimator2.setStartDelay(500L);
            this.mDelayAnimation = false;
        }
        this.mYearPickerView.onDateChanged();
        if (this.mCurrentView != i) {
            this.mMonthAndDayView.setSelected(false);
            this.mYearView.setSelected(true);
            this.mAnimator.setDisplayedChild(1);
            this.mCurrentView = i;
        }
        pulseAnimator2.start();
        this.mAnimator.setContentDescription(this.mYearPickerDescription + ": " + ((Object) this.YEAR_FORMAT.format(Long.valueOf(timeInMillis))));
        com.sonymobile.calendar.datetimepicker.Utils.tryAccessibilityAnnounce(this.mAnimator, this.mSelectYear);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisplay(boolean z) {
        TextView textView = this.mDayOfWeekView;
        if (textView != null) {
            textView.setText(this.mCalendar.getDisplayName(7, 2, Locale.getDefault()).toUpperCase(Locale.getDefault()));
        }
        Resources resources = getResources();
        if (this.mIsLunarOn) {
            LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(this.mCalendar.getTime());
            this.mSelectedMonthTextView.setTextSize(0, resources.getDimensionPixelSize(R.dimen.selected_lunar_date_month_size));
            this.mSelectedMonthTextView.setText(LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate));
            this.mSelectedMonthTextView.setPaddingRelative(0, 28, 0, 18);
            this.mSelectedDayTextView.setTextSize(0, resources.getDimensionPixelSize(R.dimen.selected_lunar_date_day_size));
            this.mSelectedDayTextView.setText(LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate));
            this.mYearView.setTextSize(0, resources.getDimensionPixelSize(R.dimen.selected_lunar_date_year_size));
            this.mYearView.setText(LunarUtils.sLunarYearStrings[lunarDateConvertSolarDateToLunarDate.mYear - 1901]);
        } else {
            this.mSelectedMonthTextView.setTextSize(0, resources.getDimensionPixelSize(R.dimen.selected_date_month_size));
            this.mSelectedMonthTextView.setText(this.mCalendar.getDisplayName(2, 1, Locale.getDefault()).toUpperCase(Locale.getDefault()));
            this.mSelectedMonthTextView.setPaddingRelative(0, 0, 0, 0);
            this.mSelectedDayTextView.setTextSize(0, resources.getDimensionPixelSize(R.dimen.selected_date_day_size));
            this.mSelectedDayTextView.setText(this.DAY_FORMAT.format(this.mCalendar.getTime()));
            this.mYearView.setTextSize(0, resources.getDimensionPixelSize(R.dimen.selected_date_year_size));
            this.mYearView.setText(this.YEAR_FORMAT.format(this.mCalendar.getTime()));
        }
        long timeInMillis = this.mCalendar.getTimeInMillis();
        this.mAnimator.setDateMillis(timeInMillis);
        this.mMonthAndDayView.setContentDescription(DateUtils.formatDateTime(getActivity(), timeInMillis, 24));
        if (z) {
            com.sonymobile.calendar.datetimepicker.Utils.tryAccessibilityAnnounce(this.mAnimator, DateUtils.formatDateTime(getActivity(), timeInMillis, 20));
        }
    }

    public void setFirstDayOfWeek(int i) {
        if (i < 1 || i > 7) {
            throw new IllegalArgumentException("Value must be between Calendar.SUNDAY and Calendar.SATURDAY");
        }
        this.mWeekStart = i;
        DayPickerView dayPickerView = this.mDayPickerView;
        if (dayPickerView != null) {
            dayPickerView.onChange();
        }
    }

    public void setOnDateSetListener(OnDateSetListener onDateSetListener) {
        this.mCallBack = onDateSetListener;
    }

    private void adjustDayInMonthIfNeeded(int i, int i2) {
        int i3 = this.mCalendar.get(5);
        int daysInMonth = com.sonymobile.calendar.datetimepicker.Utils.getDaysInMonth(i, i2);
        if (i3 > daysInMonth) {
            this.mCalendar.set(5, daysInMonth);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        tryVibrate();
        if (view.getId() == R.id.date_picker_year) {
            setCurrentView(1);
        } else if (view.getId() == R.id.date_picker_month_and_day) {
            setCurrentView(0);
        }
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public void onYearSelected(int i) {
        if (this.mIsLunarOn) {
            adjustLunarDayInMonthIfNeeded(i);
        } else {
            adjustDayInMonthIfNeeded(this.mCalendar.get(2), i);
            this.mCalendar.set(1, i);
        }
        updatePickers();
        setCurrentView(0);
        updateDisplay(true);
    }

    private void adjustLunarDayInMonthIfNeeded(int i) {
        int iMin;
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(this.mCalendar.getTime());
        lunarDateConvertSolarDateToLunarDate.mYear = i;
        if (LunarUtils.getLeapMonth(lunarDateConvertSolarDateToLunarDate.mYear) != lunarDateConvertSolarDateToLunarDate.mMonth && lunarDateConvertSolarDateToLunarDate.mIsLeap) {
            lunarDateConvertSolarDateToLunarDate.mIsLeap = false;
        }
        if (lunarDateConvertSolarDateToLunarDate.mIsLeap) {
            iMin = Math.min((int) lunarDateConvertSolarDateToLunarDate.mDay, LunarUtils.getDaysOfLeapMonth(lunarDateConvertSolarDateToLunarDate.mYear));
        } else {
            iMin = Math.min((int) lunarDateConvertSolarDateToLunarDate.mDay, LunarUtils.getDaysOfLunarMonth(lunarDateConvertSolarDateToLunarDate.mYear, lunarDateConvertSolarDateToLunarDate.mMonth));
        }
        lunarDateConvertSolarDateToLunarDate.mDay = (byte) iMin;
        Date dateConvertLunarDateToSolarDate = LunarUtils.convertLunarDateToSolarDate(lunarDateConvertSolarDateToLunarDate);
        if (dateConvertLunarDateToSolarDate == null) {
            dateConvertLunarDateToSolarDate = new Date();
        }
        this.mCalendar.setTime(dateConvertLunarDateToSolarDate);
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public void onDayOfMonthSelected(int i, int i2, int i3) {
        this.mCalendar.set(1, i);
        this.mCalendar.set(2, i2);
        this.mCalendar.set(5, i3);
        updatePickers();
        updateDisplay(true);
    }

    private void updatePickers() {
        Iterator<OnDateChangedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onDateChanged();
        }
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public SimpleMonthAdapter.CalendarDay getSelectedDay() {
        return new SimpleMonthAdapter.CalendarDay(this.mCalendar);
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public int getFirstDayOfWeek() {
        return this.mWeekStart;
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public void registerOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.mListeners.add(onDateChangedListener);
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public void unregisterOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.mListeners.remove(onDateChangedListener);
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public void tryVibrate() {
        this.mHapticFeedbackController.tryVibrate();
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerController
    public boolean isLunarOn() {
        return this.mIsLunarOn;
    }
}
