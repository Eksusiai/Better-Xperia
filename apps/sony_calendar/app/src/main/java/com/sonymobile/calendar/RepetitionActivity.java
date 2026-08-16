package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.sonyericsson.calendar.util.EventRecurrence;
import com.sonymobile.calendar.datetimepicker.date.DatePickerDialog;
import com.sonymobile.calendar.utils.UiUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class RepetitionActivity extends AppCompatActivity {
    private static final int DATE_FORMAT_FLAGS = 524310;
    private static final String DAY_FORMAT_LONG = "cccc";
    private static final String DAY_FORMAT_MEDIUM = "ccc";
    private static final String END_TAG_OLD = "endTagOld";
    private static final String MONTH_FORMAT_LONG = "LLLL";
    private static final int REPEAT_DEFAULT_INTERVAL = 1;
    private static final int REPEAT_MAX_INTERVAL = 99;
    private static final int REPEAT_PATTERN_DAILY = 0;
    private static final int REPEAT_PATTERN_MONTHLY = 2;
    private static final int REPEAT_PATTERN_WEEKLY = 1;
    private static final int REPEAT_PATTERN_YEARLY = 3;
    public static final String REPEAT_RRULE_BYDAY = "rruleByday";
    public static final String REPEAT_RRULE_BYDAYCOUNT = "rruleBydayCount";
    public static final String REPEAT_RRULE_BYDAYNUM = "rruleBydayNum";
    public static final String REPEAT_RRULE_BYMONTH = "rruleByMonth";
    public static final String REPEAT_RRULE_BYMONTHDAY = "rruleByMonthday";
    public static final String REPEAT_RRULE_DTSTART = "rruleDtstart";
    public static final String REPEAT_RRULE_FREQ = "rruleFreq";
    public static final String REPEAT_RRULE_INTERVAL = "rruleInterval";
    public static final String REPEAT_RRULE_ISMONTHDAY = "rruleIsMonthday";
    public static final String REPEAT_RRULE_ISYEARDAY = "rruleIsYearday";
    public static final String REPEAT_RRULE_UNTIL = "rruleUntil";
    private static final String START_TAG_OLD = "startTagOld";
    public static boolean mIsInMultiWindowMode;
    private static SparseIntArray mWeekdayNumberPosition;
    private static SparseIntArray mWeekdayPosition;
    private TextView mActionBarTitle;
    private Button mEndButton;
    private long mEndDate;
    private EditText mFixedNoEditText;
    private TextView mFixedNoLabel;
    private boolean mIsMonthDay;
    private boolean mIsYearDay;
    private EditText mMonthDateEditText;
    private RadioButton mMonthDayRadio;
    private LinearLayout mMonthOptionsContainer;
    private RadioGroup mMonthRadioGroup;
    private RadioButton mMonthWeekDayRadio;
    private Spinner mMonthlyWeekdaysNoSpinner;
    private boolean mMonthlyWeekdaysNoSpinnerEnable;
    private Spinner mMonthlyWeekdaysSpinner;
    private boolean mMonthlyWeekdaysSpinnerEnable;
    private String[] mMonths;
    private Spinner mRepeatsPatternSpinner;
    private ScrollView mRepetitionView;
    private Button mStartButton;
    private long mStartDate;
    private String[] mWeekDays;
    private ListView mWeekdayCheckLV;
    private AlertDialog mWeekdaySelectDialog;
    private LinearLayout mWeeklyOptionsContainer;
    private Button mWeeklydaysButton;
    private EditText mYearDateEditText;
    private RadioButton mYearDayRadio;
    private LinearLayout mYearOptionsContainer;
    private RadioGroup mYearRadioGroup;
    private RadioButton mYearWeekDayRadio;
    private boolean mYearWeekDayRadioChecked;
    private LinearLayout mYearlyMonthContainer;
    private int mYearlyMonthDate;
    private Spinner mYearlyMonthSpinner;
    private Spinner mYearlyWeekdaysNoSpinner;
    private boolean mYearlyWeekdaysNoSpinnerEnable;
    private Spinner mYearlyWeekdaysSpinner;
    private boolean mYearlyWeekdaysSpinnerEnable;
    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private int[] mWeekDayValues = {2, 3, 4, 5, 6, 7, 1};
    private boolean[] mWeekdaysChecked = {false, false, false, false, false, false, false};
    private Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialogListener mStartDateSetOldListener = new DatePickerDialogListener() { // from class: com.sonymobile.calendar.RepetitionActivity.2
        @Override // com.sonymobile.calendar.DatePickerDialogListener
        public void onDateSet(int i, int i2, int i3, int i4) {
            RepetitionActivity.this.performStartDateSet(i, i2, i3);
        }
    };
    DatePickerDialog.OnDateSetListener mStartDateSetListener = new DatePickerDialog.OnDateSetListener() { // from class: com.sonymobile.calendar.RepetitionActivity.3
        @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.OnDateSetListener
        public void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
            RepetitionActivity.this.performStartDateSet(i, i2, i3);
        }
    };
    DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() { // from class: com.sonymobile.calendar.RepetitionActivity.4
        @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.OnDateSetListener
        public void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
            RepetitionActivity.this.performEndDateSet(i, i2, i3);
        }
    };
    private DatePickerDialogListener mEndDateSetOldListener = new DatePickerDialogListener() { // from class: com.sonymobile.calendar.RepetitionActivity.5
        @Override // com.sonymobile.calendar.DatePickerDialogListener
        public void onDateSet(int i, int i2, int i3, int i4) {
            RepetitionActivity.this.performEndDateSet(i, i2, i3);
        }
    };

    private enum DatePickerDialogType {
        START,
        END
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        mWeekdayPosition = sparseIntArray;
        sparseIntArray.put(2, 0);
        mWeekdayPosition.put(3, 1);
        mWeekdayPosition.put(4, 2);
        mWeekdayPosition.put(5, 3);
        mWeekdayPosition.put(6, 4);
        mWeekdayPosition.put(7, 5);
        mWeekdayPosition.put(1, 6);
        SparseIntArray sparseIntArray2 = new SparseIntArray();
        mWeekdayNumberPosition = sparseIntArray2;
        sparseIntArray2.put(1, 0);
        mWeekdayNumberPosition.put(2, 1);
        mWeekdayNumberPosition.put(3, 2);
        mWeekdayNumberPosition.put(4, 3);
        mWeekdayNumberPosition.put(5, 4);
    }

    private void populatePattern(Intent intent) {
        if (intent == null) {
            return;
        }
        int intExtra = intent.getIntExtra(REPEAT_RRULE_FREQ, 4);
        int intExtra2 = intent.getIntExtra(REPEAT_RRULE_INTERVAL, 1);
        String stringExtra = intent.getStringExtra(REPEAT_RRULE_UNTIL);
        int[] intArrayExtra = intent.getIntArrayExtra(REPEAT_RRULE_BYDAY);
        int[] intArrayExtra2 = intent.getIntArrayExtra(REPEAT_RRULE_BYDAYNUM);
        int[] intArrayExtra3 = intent.getIntArrayExtra(REPEAT_RRULE_BYMONTHDAY);
        int[] intArrayExtra4 = intent.getIntArrayExtra(REPEAT_RRULE_BYMONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mEndDate);
        long longExtra = intent.getLongExtra(REPEAT_RRULE_DTSTART, new SafeTime().normalize(true));
        this.mIsMonthDay = intent.getBooleanExtra(REPEAT_RRULE_BYMONTHDAY, false);
        if (intExtra == 4) {
            this.mRepeatsPatternSpinner.setSelection(0);
        } else if (intExtra == 5) {
            this.mRepeatsPatternSpinner.setSelection(1);
            StringBuilder sb = new StringBuilder();
            if (intArrayExtra != null) {
                for (int i : intArrayExtra) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(getDayOfWeekString(EventRecurrence.day2CalendarDay(i), DAY_FORMAT_MEDIUM));
                    this.mWeekdaysChecked[mWeekdayPosition.get(EventRecurrence.day2CalendarDay(i))] = true;
                }
            }
            this.mWeeklydaysButton.setText(sb.toString());
        } else if (intExtra == 6) {
            this.mRepeatsPatternSpinner.setSelection(2);
            this.mWeeklyOptionsContainer.setVisibility(8);
            this.mMonthOptionsContainer.setVisibility(0);
            this.mYearOptionsContainer.setVisibility(8);
            this.mYearlyMonthContainer.setVisibility(8);
            this.mFixedNoLabel.setText(getResources().getString(R.string.clr_strings_fixed_month_no_txt));
            if (intArrayExtra3 != null && intArrayExtra3.length > 0) {
                this.mMonthDayRadio.setChecked(true);
                this.mMonthDateEditText.setEnabled(true);
                this.mMonthlyWeekdaysSpinner.setEnabled(false);
                this.mMonthlyWeekdaysNoSpinner.setEnabled(false);
                this.mMonthDateEditText.setText(String.valueOf(intArrayExtra3[0]));
            } else {
                this.mMonthWeekDayRadio.setChecked(true);
                this.mMonthDateEditText.setEnabled(false);
                this.mMonthlyWeekdaysSpinner.setEnabled(true);
                this.mMonthlyWeekdaysNoSpinner.setEnabled(true);
                this.mMonthlyWeekdaysSpinner.setSelection(mWeekdayPosition.get(EventRecurrence.getFirstByDayOrDefault(intArrayExtra, calendar)));
                this.mMonthlyWeekdaysNoSpinner.setSelection(mWeekdayNumberPosition.get(EventRecurrence.getFirstByDayNumOrDefault(intArrayExtra2, calendar)));
            }
        } else if (intExtra == 7) {
            this.mRepeatsPatternSpinner.setSelection(3);
            this.mYearlyMonthSpinner.setSelection(EventRecurrence.getFirstByMonthOrDefault(intArrayExtra4, calendar));
            this.mWeeklyOptionsContainer.setVisibility(8);
            this.mMonthOptionsContainer.setVisibility(8);
            this.mYearlyMonthContainer.setVisibility(0);
            this.mYearOptionsContainer.setVisibility(0);
            this.mFixedNoLabel.setText(getResources().getString(R.string.clr_strings_fixed_year_no_txt));
            if (intArrayExtra3 != null && intArrayExtra3.length > 0) {
                this.mYearDayRadio.setChecked(true);
                this.mYearDateEditText.setEnabled(true);
                this.mYearlyWeekdaysSpinner.setEnabled(false);
                this.mYearlyWeekdaysNoSpinner.setEnabled(false);
                this.mYearDateEditText.setText(String.valueOf(intArrayExtra3[0]));
            } else {
                this.mYearWeekDayRadio.setChecked(true);
                this.mYearWeekDayRadioChecked = true;
                this.mYearDateEditText.setEnabled(false);
                this.mYearlyWeekdaysSpinner.setEnabled(true);
                this.mYearlyWeekdaysSpinnerEnable = true;
                this.mYearlyWeekdaysNoSpinner.setEnabled(true);
                this.mYearlyWeekdaysNoSpinnerEnable = true;
                this.mYearlyWeekdaysSpinner.setSelection(mWeekdayPosition.get(EventRecurrence.getFirstByDayOrDefault(intArrayExtra, calendar)));
                this.mYearlyWeekdaysNoSpinner.setSelection(mWeekdayNumberPosition.get(EventRecurrence.getFirstByDayNumOrDefault(intArrayExtra2, calendar)));
            }
        }
        this.mFixedNoEditText.setText(String.valueOf(intExtra2));
        String string = this.mFixedNoEditText.getText().toString();
        if (!string.isEmpty()) {
            this.mFixedNoEditText.setSelection(string.length());
        }
        if (longExtra > 0) {
            this.mStartDate = longExtra;
            this.mStartButton.setText(DateUtils.formatDateTime(this, longExtra, DATE_FORMAT_FLAGS));
        }
        if (stringExtra != null && stringExtra.length() >= 8) {
            this.mCalendar.set(Integer.parseInt(stringExtra.substring(0, 4)), Integer.parseInt(stringExtra.substring(4, 6)) - 1, Integer.parseInt(stringExtra.substring(6, 8)));
            long timeInMillis = this.mCalendar.getTimeInMillis();
            this.mEndDate = timeInMillis;
            this.mEndButton.setText(DateUtils.formatDateTime(this, timeInMillis, DATE_FORMAT_FLAGS));
            this.mEventRecurrence.until = DateFormat.format("yyyyMMdd", this.mEndDate).toString();
        }
        if (intArrayExtra4 == null) {
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(this.mStartDate);
            this.mYearlyMonthDate = calendar2.get(2);
            return;
        }
        this.mYearlyMonthDate = intArrayExtra4[0] - 1;
    }

    private void init() {
        setContentView(R.layout.repetition_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setBackgroundResource(R.color.toolbar_background_color);
        setSupportActionBar(toolbar);
        this.mRepetitionView = (ScrollView) findViewById(R.id.repetition_activity);
        getSupportActionBar().setTitle(getResources().getString(R.string.clr_strings_customized_repetition_txt));
        this.mWeeklyOptionsContainer = (LinearLayout) findViewById(R.id.repeats_options_weekly_container);
        this.mMonthOptionsContainer = (LinearLayout) findViewById(R.id.repeats_options_monthly_container);
        this.mYearOptionsContainer = (LinearLayout) findViewById(R.id.repeats_options_yearly_container);
        this.mYearlyMonthContainer = (LinearLayout) findViewById(R.id.yearly_months_container);
        this.mStartButton = (Button) findViewById(R.id.start_date);
        this.mEndButton = (Button) findViewById(R.id.end_date);
        this.mWeeklydaysButton = (Button) findViewById(R.id.weekly_days);
        this.mFixedNoLabel = (TextView) findViewById(R.id.fixed_no_label);
        this.mFixedNoEditText = (EditText) findViewById(R.id.fixed_no);
        this.mMonthDateEditText = (EditText) findViewById(R.id.month_date);
        this.mYearDateEditText = (EditText) findViewById(R.id.year_date);
        this.mMonthRadioGroup = (RadioGroup) findViewById(R.id.monthly_radiogroup);
        this.mMonthDayRadio = (RadioButton) findViewById(R.id.month_day);
        this.mMonthWeekDayRadio = (RadioButton) findViewById(R.id.month_week_day);
        this.mYearRadioGroup = (RadioGroup) findViewById(R.id.yearly_radiogroup);
        this.mYearDayRadio = (RadioButton) findViewById(R.id.year_day);
        this.mYearWeekDayRadio = (RadioButton) findViewById(R.id.year_week_day);
        this.mMonthRadioGroup.setOnCheckedChangeListener(new RGCheckedListener());
        this.mYearRadioGroup.setOnCheckedChangeListener(new RGCheckedListener());
        Spinner spinner = (Spinner) findViewById(R.id.yearly_months);
        this.mYearlyMonthSpinner = spinner;
        populateSpinner(spinner, getMonthStrings());
        Spinner spinner2 = (Spinner) findViewById(R.id.monthly_weekdays);
        this.mMonthlyWeekdaysSpinner = spinner2;
        populateSpinner(spinner2, getDaysOfWeekStrings());
        Spinner spinner3 = (Spinner) findViewById(R.id.monthly_weekdays_no);
        this.mMonthlyWeekdaysNoSpinner = spinner3;
        populateSpinner(spinner3, R.array.repeats_weekdays_no);
        Spinner spinner4 = (Spinner) findViewById(R.id.yearly_weekdays);
        this.mYearlyWeekdaysSpinner = spinner4;
        populateSpinner(spinner4, getDaysOfWeekStrings());
        Spinner spinner5 = (Spinner) findViewById(R.id.yearly_weekdays_no);
        this.mYearlyWeekdaysNoSpinner = spinner5;
        populateSpinner(spinner5, R.array.repeats_weekdays_no);
        Spinner spinner6 = (Spinner) findViewById(R.id.repeats_pattern);
        this.mRepeatsPatternSpinner = spinner6;
        populateSpinner(spinner6, R.array.repeats_pattern);
        this.mRepeatsPatternSpinner.setOnItemSelectedListener(new RepeatsPatternItemSelectedListener());
        this.mRepeatsPatternSpinner.setOnTouchListener(new View.OnTouchListener() { // from class: com.sonymobile.calendar.RepetitionActivity.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                View viewFindFocus = RepetitionActivity.this.mRepetitionView.findFocus();
                if (viewFindFocus == null) {
                    return false;
                }
                viewFindFocus.clearFocus();
                Utils.hideKeyboard(RepetitionActivity.this.getApplicationContext(), viewFindFocus);
                return false;
            }
        });
        this.mStartButton.setOnClickListener(new DateRangeClickListener());
        this.mEndButton.setOnClickListener(new DateRangeClickListener());
        this.mWeeklydaysButton.setOnClickListener(new WeekDaysClickListener());
        populatePattern(getIntent());
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(DatePickerDialogType.START.name());
        if (fragmentFindFragmentByTag != null) {
            Utils.restoreDatePickerDialog(this, (DatePickerDialog) fragmentFindFragmentByTag, this.mStartDateSetListener);
        }
        Fragment fragmentFindFragmentByTag2 = getSupportFragmentManager().findFragmentByTag(DatePickerDialogType.END.name());
        if (fragmentFindFragmentByTag2 != null) {
            Utils.restoreDatePickerDialog(this, (DatePickerDialog) fragmentFindFragmentByTag2, this.mEndDateSetListener);
        }
        Fragment fragmentFindFragmentByTag3 = getSupportFragmentManager().findFragmentByTag(START_TAG_OLD);
        if (fragmentFindFragmentByTag3 != null) {
            ((DatePickerDialogFragment) fragmentFindFragmentByTag3).setDateListener(this.mStartDateSetOldListener);
        }
        Fragment fragmentFindFragmentByTag4 = getSupportFragmentManager().findFragmentByTag(END_TAG_OLD);
        if (fragmentFindFragmentByTag4 != null) {
            ((DatePickerDialogFragment) fragmentFindFragmentByTag4).setDateListener(this.mEndDateSetOldListener);
        }
        this.mFixedNoEditText.clearFocus();
        this.mRepeatsPatternSpinner.requestFocus();
    }

    private String[] getMonthStrings() {
        if (this.mMonths == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLLL", Locale.getDefault());
            int i = 0;
            this.mCalendar.set(2, 0);
            this.mMonths = new String[12];
            while (true) {
                String[] strArr = this.mMonths;
                if (i >= strArr.length) {
                    break;
                }
                strArr[i] = simpleDateFormat.format(this.mCalendar.getTime());
                this.mCalendar.add(2, 1);
                i++;
            }
        }
        return this.mMonths;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String[] getDaysOfWeekStrings() {
        if (this.mWeekDays == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("cccc", Locale.getDefault());
            int i = 0;
            this.mCalendar.set(7, this.mWeekDayValues[0]);
            this.mWeekDays = new String[this.mWeekDayValues.length];
            while (true) {
                String[] strArr = this.mWeekDays;
                if (i >= strArr.length) {
                    break;
                }
                strArr[i] = simpleDateFormat.format(this.mCalendar.getTime());
                this.mCalendar.add(7, 1);
                i++;
            }
        }
        return this.mWeekDays;
    }

    private String getDayOfWeekString(int i, String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str, Locale.getDefault());
        this.mCalendar.set(7, i);
        return simpleDateFormat.format(this.mCalendar.getTime());
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
    }

    private class RGCheckedListener implements RadioGroup.OnCheckedChangeListener {
        private RGCheckedListener() {
        }

        @Override // android.widget.RadioGroup.OnCheckedChangeListener
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if (radioGroup == RepetitionActivity.this.mMonthRadioGroup) {
                if (i == RepetitionActivity.this.mMonthDayRadio.getId()) {
                    RepetitionActivity.this.mMonthDateEditText.setEnabled(true);
                    RepetitionActivity.this.mMonthDateEditText.setFocusable(true);
                    RepetitionActivity.this.mMonthDateEditText.setFocusableInTouchMode(true);
                    RepetitionActivity.this.mMonthlyWeekdaysSpinner.setEnabled(false);
                    RepetitionActivity.this.mMonthlyWeekdaysNoSpinner.setEnabled(false);
                    RepetitionActivity.this.mIsMonthDay = true;
                }
                if (i == RepetitionActivity.this.mMonthWeekDayRadio.getId()) {
                    RepetitionActivity.this.mMonthDateEditText.setFocusableInTouchMode(false);
                    RepetitionActivity.this.mMonthDateEditText.setFocusable(false);
                    RepetitionActivity.this.mFixedNoEditText.requestFocus();
                    RepetitionActivity.this.mMonthDateEditText.setEnabled(false);
                    RepetitionActivity.this.mMonthlyWeekdaysSpinner.setEnabled(true);
                    RepetitionActivity.this.mMonthlyWeekdaysNoSpinner.setEnabled(true);
                    RepetitionActivity.this.mIsMonthDay = false;
                    return;
                }
                return;
            }
            if (radioGroup == RepetitionActivity.this.mYearRadioGroup) {
                if (i == RepetitionActivity.this.mYearDayRadio.getId()) {
                    RepetitionActivity.this.mYearDateEditText.setEnabled(true);
                    RepetitionActivity.this.mYearDateEditText.setFocusable(true);
                    RepetitionActivity.this.mYearDateEditText.setFocusableInTouchMode(true);
                    RepetitionActivity.this.mYearlyWeekdaysSpinner.setEnabled(false);
                    RepetitionActivity.this.mYearlyWeekdaysNoSpinner.setEnabled(false);
                    RepetitionActivity.this.mIsYearDay = true;
                }
                if (i == RepetitionActivity.this.mYearWeekDayRadio.getId()) {
                    RepetitionActivity.this.mYearDateEditText.setFocusableInTouchMode(false);
                    RepetitionActivity.this.mYearDateEditText.setFocusable(false);
                    RepetitionActivity.this.mYearDateEditText.setEnabled(false);
                    RepetitionActivity.this.mYearlyWeekdaysSpinner.setEnabled(true);
                    RepetitionActivity.this.mYearlyWeekdaysNoSpinner.setEnabled(true);
                    RepetitionActivity.this.mIsYearDay = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWeekday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mStartDate);
        int i = calendar.get(7);
        this.mWeekdaysChecked[mWeekdayPosition.get(i)] = true;
        this.mWeeklydaysButton.setText(getDayOfWeekString(i, DAY_FORMAT_MEDIUM));
        this.mEventRecurrence.byday = new int[]{EventRecurrence.calendarDay2Day(i)};
        this.mEventRecurrence.bydayNum = new int[]{0};
        this.mEventRecurrence.bydayCount = 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWeekdaysChecked(boolean[] zArr) {
        for (boolean z : zArr) {
            if (z) {
                return true;
            }
        }
        return false;
    }

    private class RepeatsPatternItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        private RepeatsPatternItemSelectedListener() {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(RepetitionActivity.this.mStartDate);
            RepetitionActivity.this.mFixedNoEditText.setNextFocusDownId(-1);
            if (i == 0) {
                RepetitionActivity.this.mWeeklyOptionsContainer.setVisibility(8);
                RepetitionActivity.this.mMonthOptionsContainer.setVisibility(8);
                RepetitionActivity.this.mYearOptionsContainer.setVisibility(8);
                RepetitionActivity.this.mYearlyMonthContainer.setVisibility(8);
                RepetitionActivity.this.mFixedNoLabel.setText(RepetitionActivity.this.getResources().getString(R.string.clr_strings_fixed_day_no_txt));
                RepetitionActivity.this.mEventRecurrence.freq = 4;
                RepetitionActivity.this.mStartButton.setNextFocusUpId(R.id.fixed_no);
                return;
            }
            if (i == 1) {
                RepetitionActivity.this.mWeeklyOptionsContainer.setVisibility(0);
                RepetitionActivity.this.mMonthOptionsContainer.setVisibility(8);
                RepetitionActivity.this.mYearOptionsContainer.setVisibility(8);
                RepetitionActivity.this.mYearlyMonthContainer.setVisibility(8);
                RepetitionActivity.this.mFixedNoLabel.setText(RepetitionActivity.this.getResources().getString(R.string.clr_strings_fixed_week_no_txt));
                RepetitionActivity.this.mEventRecurrence.freq = 5;
                RepetitionActivity.this.mStartButton.setNextFocusUpId(R.id.weekly_days);
                RepetitionActivity repetitionActivity = RepetitionActivity.this;
                if (repetitionActivity.isWeekdaysChecked(repetitionActivity.mWeekdaysChecked)) {
                    return;
                }
                RepetitionActivity.this.setWeekday();
                return;
            }
            if (i != 2) {
                if (i != 3) {
                    return;
                }
                RepetitionActivity.this.mWeeklyOptionsContainer.setVisibility(8);
                RepetitionActivity.this.mMonthOptionsContainer.setVisibility(8);
                RepetitionActivity.this.mYearlyMonthContainer.setVisibility(0);
                RepetitionActivity.this.mYearOptionsContainer.setVisibility(0);
                RepetitionActivity.this.mFixedNoLabel.setText(RepetitionActivity.this.getResources().getString(R.string.clr_strings_fixed_year_no_txt));
                RepetitionActivity.this.mFixedNoEditText.setNextFocusDownId(R.id.yearly_months);
                RepetitionActivity.this.mStartButton.setNextFocusUpId(R.id.yearly_weekdays);
                if (!RepetitionActivity.this.mYearWeekDayRadioChecked) {
                    RepetitionActivity.this.mYearDayRadio.setChecked(true);
                    RepetitionActivity.this.mYearDateEditText.setEnabled(true);
                }
                if (!RepetitionActivity.this.mYearlyWeekdaysSpinnerEnable && !RepetitionActivity.this.mYearlyWeekdaysNoSpinnerEnable) {
                    RepetitionActivity.this.mYearlyWeekdaysSpinner.setEnabled(false);
                    RepetitionActivity.this.mYearlyWeekdaysNoSpinner.setEnabled(false);
                }
                RepetitionActivity.this.mYearlyMonthSpinner.setSelection(RepetitionActivity.this.mYearlyMonthDate);
                if (RepetitionActivity.this.mYearDateEditText.getText().toString().isEmpty()) {
                    RepetitionActivity.this.mYearDateEditText.setText(String.valueOf(calendar.get(5)));
                }
                RepetitionActivity.this.mEventRecurrence.freq = 7;
                return;
            }
            RepetitionActivity.this.mWeeklyOptionsContainer.setVisibility(8);
            RepetitionActivity.this.mYearOptionsContainer.setVisibility(8);
            RepetitionActivity.this.mYearlyMonthContainer.setVisibility(8);
            RepetitionActivity.this.mMonthOptionsContainer.setVisibility(0);
            RepetitionActivity.this.mFixedNoLabel.setText(RepetitionActivity.this.getResources().getString(R.string.clr_strings_fixed_month_no_txt));
            RepetitionActivity.this.mStartButton.setNextFocusUpId(R.id.monthly_weekdays);
            if (RepetitionActivity.this.mIsMonthDay) {
                RepetitionActivity.this.mMonthDayRadio.setChecked(true);
                RepetitionActivity.this.mMonthDateEditText.setEnabled(true);
            } else {
                RepetitionActivity.this.mMonthWeekDayRadio.setChecked(true);
                RepetitionActivity.this.mMonthDateEditText.setEnabled(false);
                RepetitionActivity.this.mMonthlyWeekdaysSpinner.setEnabled(true);
                RepetitionActivity.this.mMonthlyWeekdaysNoSpinner.setEnabled(true);
                RepetitionActivity.this.mMonthlyWeekdaysNoSpinnerEnable = true;
                RepetitionActivity.this.mMonthlyWeekdaysSpinnerEnable = true;
            }
            if (!RepetitionActivity.this.mMonthlyWeekdaysSpinnerEnable && !RepetitionActivity.this.mMonthlyWeekdaysNoSpinnerEnable) {
                RepetitionActivity.this.mMonthlyWeekdaysSpinner.setEnabled(false);
                RepetitionActivity.this.mMonthlyWeekdaysNoSpinner.setEnabled(false);
            }
            RepetitionActivity.this.mEventRecurrence.freq = 6;
            if (RepetitionActivity.this.mMonthDateEditText.getText().toString().isEmpty()) {
                RepetitionActivity.this.mMonthDateEditText.setText(String.valueOf(calendar.get(5)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDatePickerDialog(DatePickerDialogType datePickerDialogType, View view) {
        boolean z = datePickerDialogType == DatePickerDialogType.START;
        this.mCalendar.setTimeInMillis(z ? this.mStartDate : this.mEndDate);
        int i = this.mCalendar.get(1);
        int i2 = this.mCalendar.get(2);
        int i3 = this.mCalendar.get(5);
        DatePickerDialog.OnDateSetListener onDateSetListener = z ? this.mStartDateSetListener : this.mEndDateSetListener;
        DatePickerDialogListener datePickerDialogListener = z ? this.mStartDateSetOldListener : this.mEndDateSetOldListener;
        if (!UiUtils.isSub320dpScreen(this)) {
            if (isFinishing()) {
                return;
            }
            DatePickerDialog.newInstance(onDateSetListener, i, i2, i3, 2).show(getSupportFragmentManager(), datePickerDialogType.name());
        } else {
            if (isFinishing()) {
                return;
            }
            DatePickerDialogFragment.newInstance(i, i2, i3, datePickerDialogListener, view.getId()).show(getSupportFragmentManager(), datePickerDialogType == DatePickerDialogType.START ? START_TAG_OLD : END_TAG_OLD);
        }
    }

    private class DateRangeClickListener implements View.OnClickListener {
        private DateRangeClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == RepetitionActivity.this.mStartButton) {
                if (RepetitionActivity.this.mStartDate > 0) {
                    RepetitionActivity.this.showDatePickerDialog(DatePickerDialogType.START, view);
                }
            } else if (view == RepetitionActivity.this.mEndButton) {
                if (RepetitionActivity.this.mEndDate <= 0) {
                    RepetitionActivity repetitionActivity = RepetitionActivity.this;
                    repetitionActivity.mEndDate = repetitionActivity.mStartDate;
                }
                RepetitionActivity.this.showDatePickerDialog(DatePickerDialogType.END, view);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performStartDateSet(int i, int i2, int i3) {
        this.mCalendar.set(i, i2, i3);
        long timeInMillis = this.mCalendar.getTimeInMillis();
        this.mStartDate = timeInMillis;
        long j = this.mEndDate;
        if (j == 0 || j >= timeInMillis) {
            this.mStartButton.setText(DateUtils.formatDateTime(this, timeInMillis, DATE_FORMAT_FLAGS));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performEndDateSet(int i, int i2, int i3) {
        this.mCalendar.set(i, i2, i3);
        long timeInMillis = this.mCalendar.getTimeInMillis();
        this.mEndDate = timeInMillis;
        if (timeInMillis >= this.mStartDate) {
            this.mEndButton.setText(DateUtils.formatDateTime(this, timeInMillis, DATE_FORMAT_FLAGS));
            this.mEventRecurrence.until = DateFormat.format("yyyyMMdd", this.mEndDate).toString();
        } else {
            this.mEndButton.setText(getResources().getString(R.string.clr_strings_end_button_title_txt));
            this.mEndDate = 0L;
            this.mEventRecurrence.until = "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void arrayCopy(boolean[] zArr, boolean[] zArr2) {
        System.arraycopy(zArr, 0, zArr2, 0, zArr.length);
    }

    private class WeekDaysClickListener implements View.OnClickListener {
        private WeekDaysClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            final boolean[] zArr = new boolean[RepetitionActivity.this.mWeekdaysChecked.length];
            RepetitionActivity repetitionActivity = RepetitionActivity.this;
            repetitionActivity.arrayCopy(repetitionActivity.mWeekdaysChecked, zArr);
            RepetitionActivity.this.mWeekdaySelectDialog = new AlertDialog.Builder(RepetitionActivity.this, R.style.AlertDialogTheme).setTitle(RepetitionActivity.this.getResources().getString(R.string.clr_strings_prompt_week_day_selection_txt)).setMultiChoiceItems(RepetitionActivity.this.getDaysOfWeekStrings(), zArr, new DialogInterface.OnMultiChoiceClickListener() { // from class: com.sonymobile.calendar.RepetitionActivity.WeekDaysClickListener.2
                @Override // android.content.DialogInterface.OnMultiChoiceClickListener
                public void onClick(DialogInterface dialogInterface, int i, boolean z) {
                    if (z || RepetitionActivity.this.isWeekdaysChecked(zArr)) {
                        return;
                    }
                    zArr[i] = true;
                    RepetitionActivity.this.mWeekdayCheckLV.setItemChecked(i, true);
                }
            }).setPositiveButton(RepetitionActivity.this.getResources().getString(R.string.clr_strings_button_title_ok_txt), new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.RepetitionActivity.WeekDaysClickListener.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    RepetitionActivity.this.arrayCopy(zArr, RepetitionActivity.this.mWeekdaysChecked);
                    RepetitionActivity.this.setWeekDaysInfo();
                    dialogInterface.dismiss();
                }
            }).setNegativeButton(RepetitionActivity.this.getResources().getString(R.string.clr_strings_button_title_cancel_txt), (DialogInterface.OnClickListener) null).create();
            RepetitionActivity repetitionActivity2 = RepetitionActivity.this;
            repetitionActivity2.mWeekdayCheckLV = repetitionActivity2.mWeekdaySelectDialog.getListView();
            RepetitionActivity.this.mWeekdaySelectDialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWeekDaysInfo() {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            boolean[] zArr = this.mWeekdaysChecked;
            if (i >= zArr.length) {
                break;
            }
            if (zArr[i]) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(getDayOfWeekString(this.mWeekDayValues[i], DAY_FORMAT_MEDIUM));
                arrayList.add(Integer.valueOf(this.mWeekDayValues[i]));
            }
            i++;
        }
        this.mWeeklydaysButton.setText(sb.toString());
        int[] iArr = new int[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            iArr[i2] = EventRecurrence.calendarDay2Day(((Integer) arrayList.get(i2)).intValue());
        }
        int[] iArr2 = new int[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            iArr2[i3] = 0;
        }
        this.mEventRecurrence.byday = iArr;
        this.mEventRecurrence.bydayNum = iArr2;
        this.mEventRecurrence.bydayCount = arrayList.size();
    }

    private void populateSpinner(Spinner spinner, int i) {
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource = ArrayAdapter.createFromResource(this, i, R.layout.simple_spinner_item);
        arrayAdapterCreateFromResource.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource);
    }

    private void populateSpinner(Spinner spinner, String[] strArr) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, strArr);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        sendRule();
    }

    private void sendRule() {
        Intent intent = new Intent();
        if (this.mFixedNoEditText.getText() != null && !"".equals(this.mFixedNoEditText.getText().toString())) {
            if (this.mFixedNoEditText.getText().length() < 3) {
                this.mEventRecurrence.interval = Integer.parseInt(this.mFixedNoEditText.getText().toString());
            } else {
                this.mEventRecurrence.interval = 99;
            }
        } else {
            this.mEventRecurrence.interval = 1;
        }
        intent.putExtra(REPEAT_RRULE_DTSTART, this.mStartDate);
        intent.putExtra(REPEAT_RRULE_FREQ, this.mEventRecurrence.freq);
        intent.putExtra(REPEAT_RRULE_INTERVAL, this.mEventRecurrence.interval);
        intent.putExtra(REPEAT_RRULE_UNTIL, this.mEventRecurrence.until);
        int selectedItemPosition = this.mRepeatsPatternSpinner.getSelectedItemPosition();
        if (selectedItemPosition == 0 || selectedItemPosition == 1) {
            if (this.mEventRecurrence.byday == null) {
                setWeekDaysInfo();
            }
            intent.putExtra(REPEAT_RRULE_BYDAY, this.mEventRecurrence.byday);
            intent.putExtra(REPEAT_RRULE_BYDAYNUM, this.mEventRecurrence.bydayNum);
            intent.putExtra(REPEAT_RRULE_BYDAYCOUNT, this.mEventRecurrence.bydayCount);
        } else {
            int i = 31;
            if (selectedItemPosition == 2) {
                if (this.mIsMonthDay) {
                    if (this.mMonthDateEditText.getText() != null && !"".equals(this.mMonthDateEditText.getText().toString()) && this.mMonthDateEditText.getText().length() < 10) {
                        int i2 = Integer.parseInt(this.mMonthDateEditText.getText().toString());
                        if (i2 <= 31) {
                            i = i2 < 1 ? 1 : i2;
                        }
                        this.mEventRecurrence.bymonthday = new int[]{i};
                    } else {
                        this.mEventRecurrence.bymonthday = new int[]{1};
                    }
                    intent.putExtra(REPEAT_RRULE_BYMONTHDAY, this.mEventRecurrence.bymonthday);
                } else {
                    this.mEventRecurrence.byday = new int[]{EventRecurrence.calendarDay2Day(this.mWeekDayValues[this.mMonthlyWeekdaysSpinner.getSelectedItemPosition()])};
                    this.mEventRecurrence.bydayNum = new int[]{this.mMonthlyWeekdaysNoSpinner.getSelectedItemPosition() + 1};
                    this.mEventRecurrence.bydayCount = 0;
                    intent.putExtra(REPEAT_RRULE_BYDAY, this.mEventRecurrence.byday);
                    intent.putExtra(REPEAT_RRULE_BYDAYNUM, this.mEventRecurrence.bydayNum);
                }
                intent.putExtra(REPEAT_RRULE_ISMONTHDAY, this.mIsMonthDay);
            } else if (selectedItemPosition == 3) {
                this.mEventRecurrence.bymonth = new int[]{this.mYearlyMonthSpinner.getSelectedItemPosition() + 1};
                intent.putExtra(REPEAT_RRULE_BYMONTH, this.mEventRecurrence.bymonth);
                if (this.mIsYearDay) {
                    if (this.mYearDateEditText.getText() != null && !"".equals(this.mYearDateEditText.getText().toString()) && this.mYearDateEditText.getText().length() < 10) {
                        int i3 = Integer.parseInt(this.mYearDateEditText.getText().toString());
                        if (i3 <= 31) {
                            i = i3 < 1 ? 1 : i3;
                        }
                        this.mEventRecurrence.bymonthday = new int[]{i};
                    } else {
                        this.mEventRecurrence.bymonthday = new int[]{1};
                    }
                    intent.putExtra(REPEAT_RRULE_BYMONTHDAY, this.mEventRecurrence.bymonthday);
                } else {
                    this.mEventRecurrence.byday = new int[]{EventRecurrence.calendarDay2Day(this.mWeekDayValues[this.mYearlyWeekdaysSpinner.getSelectedItemPosition()])};
                    this.mEventRecurrence.bydayNum = new int[]{this.mYearlyWeekdaysNoSpinner.getSelectedItemPosition() + 1};
                    this.mEventRecurrence.bydayCount = 0;
                    intent.putExtra(REPEAT_RRULE_BYDAYNUM, this.mEventRecurrence.bydayNum);
                    intent.putExtra(REPEAT_RRULE_BYDAY, this.mEventRecurrence.byday);
                }
                intent.putExtra(REPEAT_RRULE_ISYEARDAY, this.mIsYearDay);
            }
        }
        intent.putExtra("mRrule", this.mEventRecurrence.toString());
        setResult(-1, intent);
        finish();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mWeekdaySelectDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            sendRule();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("mYearlyMonthDate", this.mYearlyMonthSpinner.getSelectedItemPosition());
        bundle.putParcelable("mRepeatsPatternSpinner", this.mRepeatsPatternSpinner.onSaveInstanceState());
        bundle.putCharSequence("mWeeklydaysButton", this.mWeeklydaysButton.getText());
        bundle.putParcelable("mMonthlyWeekdaysSpinner", this.mMonthlyWeekdaysSpinner.onSaveInstanceState());
        bundle.putParcelable("mMonthlyWeekdaysNoSpinner", this.mMonthlyWeekdaysNoSpinner.onSaveInstanceState());
        bundle.putParcelable("mYearlyMonthSpinner", this.mYearlyMonthSpinner.onSaveInstanceState());
        bundle.putBoolean("mMonthWeekDayRadioChecked", this.mMonthWeekDayRadio.isChecked());
        bundle.putBoolean("mMonthlyWeekdaysSpinnerEnable", this.mMonthlyWeekdaysSpinner.isEnabled());
        bundle.putBoolean("mMonthlyWeekdaysNoSpinnerEnable", this.mMonthlyWeekdaysNoSpinner.isEnabled());
        bundle.putBoolean("mYearWeekDayRadioChecked", this.mYearWeekDayRadio.isChecked());
        bundle.putBoolean("mYearlyWeekdaysSpinnerEnable", this.mYearlyWeekdaysSpinner.isEnabled());
        bundle.putBoolean("mYearlyWeekdaysNoSpinnerEnable", this.mYearlyWeekdaysNoSpinner.isEnabled());
        bundle.putParcelable("mYearlyWeekdaysSpinner", this.mYearlyWeekdaysSpinner.onSaveInstanceState());
        bundle.putParcelable("mYearlyWeekdaysNoSpinner", this.mYearlyWeekdaysNoSpinner.onSaveInstanceState());
        bundle.putCharSequence("mStartButton", this.mStartButton.getText());
        bundle.putCharSequence("mEndButton", this.mEndButton.getText());
        bundle.putBooleanArray("mWeekdaysChecked", this.mWeekdaysChecked);
        bundle.putString("mRrule", this.mEventRecurrence.toString());
    }

    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mYearlyMonthDate = bundle.getInt("mYearlyMonthDate");
        this.mRepeatsPatternSpinner.onRestoreInstanceState(bundle.getParcelable("mRepeatsPatternSpinner"));
        this.mWeeklydaysButton.setText(bundle.getCharSequence("mWeeklydaysButton"));
        this.mMonthlyWeekdaysSpinner.onRestoreInstanceState(bundle.getParcelable("mMonthlyWeekdaysSpinner"));
        this.mMonthlyWeekdaysNoSpinner.onRestoreInstanceState(bundle.getParcelable("mMonthlyWeekdaysNoSpinner"));
        this.mYearlyMonthSpinner.onRestoreInstanceState(bundle.getParcelable("mYearlyMonthSpinner"));
        this.mMonthlyWeekdaysSpinnerEnable = bundle.getBoolean("mMonthlyWeekdaysSpinnerEnable");
        this.mMonthlyWeekdaysNoSpinnerEnable = bundle.getBoolean("mMonthlyWeekdaysNoSpinnerEnable");
        this.mYearWeekDayRadioChecked = bundle.getBoolean("mYearWeekDayRadioChecked");
        this.mYearlyWeekdaysSpinnerEnable = bundle.getBoolean("mYearlyWeekdaysSpinnerEnable");
        this.mYearlyWeekdaysNoSpinnerEnable = bundle.getBoolean("mYearlyWeekdaysNoSpinnerEnable");
        this.mYearlyWeekdaysSpinner.onRestoreInstanceState(bundle.getParcelable("mYearlyWeekdaysSpinner"));
        this.mYearlyWeekdaysNoSpinner.onRestoreInstanceState(bundle.getParcelable("mYearlyWeekdaysNoSpinner"));
        this.mStartButton.setText(bundle.getCharSequence("mStartButton"));
        this.mEndButton.setText(bundle.getCharSequence("mEndButton"));
        this.mWeekdaysChecked = bundle.getBooleanArray("mWeekdaysChecked");
        this.mEventRecurrence.parse(bundle.getString("mRrule"));
        setWeekDaysInfo();
    }

    public void setRepetitionStartDate(long j) {
        this.mStartDate = j;
    }

    public void setRepetitionEndDate(long j) {
        this.mEndDate = j;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Bundle bundle = new Bundle();
        if (mIsInMultiWindowMode) {
            return;
        }
        onSaveInstanceState(bundle);
        init();
        onRestoreInstanceState(bundle);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onMultiWindowModeChanged(boolean z) {
        mIsInMultiWindowMode = z;
    }
}
