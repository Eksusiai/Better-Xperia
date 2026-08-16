package com.sonymobile.calendar.editevent;
import com.sonymobile.calendar.SafeTime;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.accounts.OnAccountsUpdateListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.AsyncQueryHandler;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.common.collect.Lists;
import com.sonyericsson.calendar.util.CalendarColorService;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonyericsson.calendar.util.CalendarInstanceService;
import com.sonyericsson.calendar.util.CustomizeConfig;
import com.sonyericsson.calendar.util.EventRecurrence;
import com.sonyericsson.calendar.util.EventRecurrenceParser;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import com.sonymobile.calendar.CalendarColorQuery;
import com.sonymobile.calendar.CheckAvailabilityActivity;
import com.sonymobile.calendar.ColorPickerDialogBase;
import com.sonymobile.calendar.DatePickerDialogFragment;
import com.sonymobile.calendar.DatePickerDialogListener;
import com.sonymobile.calendar.EventColorPickerDialog;
import com.sonymobile.calendar.EventInfoFragment;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.GooglePlayUpdateRequiredDialog;
import com.sonymobile.calendar.MapsActivity;
import com.sonymobile.calendar.OnColorPickedListener;
import com.sonymobile.calendar.OnColorSetListener;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.RepetitionActivity;
import com.sonymobile.calendar.TimePickerDialogFragment;
import com.sonymobile.calendar.TimePickerDialogListener;
import com.sonymobile.calendar.TimezoneAdapter;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WeekUtils;
import com.sonymobile.calendar.alerts.AlertReceiver;
import com.sonymobile.calendar.alerts.AlertWork;
import com.sonymobile.calendar.datetimepicker.date.DatePickerDialog;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.tablet.AbstractCheckAvailabilityActivity;
import com.sonymobile.calendar.tablet.OnEditEventTimeChangedListener;
import com.sonymobile.calendar.tablet.TabletCheckAvailabilityActivity;
import com.sonymobile.calendar.transitions.EditCircularRevealTransition;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.widget.MultiSelectSpinner;
import com.sonymobile.common.Rfc822InputFilter;
import com.sonymobile.common.Rfc822Validator;
import com.sonymobile.lunar.lib.LunarContract;
import com.sonymobile.lunar.lib.LunarUtils;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;

/* JADX INFO: loaded from: classes2.dex */
public class EditEventView extends LinearLayout implements View.OnClickListener, DialogInterface.OnCancelListener, DialogInterface.OnClickListener, EditCircularRevealTransition.TransitionListener {
    private static final int COLOR_LOADER_ID = 1;
    private static final String DATE_PICKER_OLD_TAG = "dateDialog";
    private static final int DAYS_IN_WEEK = 7;
    private static final boolean DEBUG = false;
    private static final String DOMAIN_GMAIL = "gmail.com";
    private static final String EDIT_EVENT_VIEW_ID_TAG = "editEventViewId";
    private static final String END_DATE_PICKER_TAG = "endDialog";
    public static final String EVENT_ALL_DAY = "allDay";
    private static final String KEY_SELECTED_CALENDAR_SPINNER_POSITION = "selectedCalendarSpinnerPosition";
    private static final int LAST_WEEK_IN_MONTH = 5;
    private static final int LAST_WEEK_IN_MONTH_SENTINEL_VALUE = -1;
    public static final String NEW_EVENT_ID_EXTRA = "NEW_EVENT_ID_EXTRA";
    private static final String REMINDERS_PICKER_TAG = "reminderPicker";
    private static final String SAVE_EVENT_DIALOG_TAG = "saveEventDialog";
    private static final String START_DATE_PICKER_TAG = "startDialog";
    private static final String TAG = "EditEventView";
    private static final String TIME_PICKER_TAG = "timePicker";
    private static Drawable mTitleTextBackground;
    protected AppCompatActivity activity;
    protected int eventColor;
    protected ImageView eventColorButton;
    private boolean isDirty;
    private boolean isEditing;
    private AccountsUpdateListener mAccountsUpdateListener;
    private TextView mAddReminderView;
    private AlertDialog mAlertDialog;
    private Switch mAllDaySwitch;
    private RecipientEditTextView mAttendeesListOptional;
    private RecipientEditTextView mAttendeesListRequired;
    private Uri mAttendeesUri;
    private Spinner mAvailabilitySpinner;
    protected int mCalendarPosition;
    protected Cursor mCalendarsCursor;
    private boolean mCalendarsQueryComplete;
    protected Spinner mCalendarsSpinner;
    protected Button mCheckAvailability;
    private EventColorPickerDialog mColorPickerDialog;
    private ConflictQueryHandler mConflictQueryHandler;
    private DatePickerDialogListener mDatePickerListener;
    private int mDefaultReminderMinutes;
    private EditText mDescriptionTextView;
    private String mDomain;
    private boolean mEditModeSelectionDialogShowed;
    private Rfc822Validator mEmailValidator;
    protected Button mEndDateButton;
    private TextView mEndDateHome;
    protected Time mEndTime;
    private Button mEndTimeButton;
    private TextView mEndTimeHome;
    private Cursor mEventCursor;
    private long mEventId;
    private EventRecurrence mEventRecurrence;
    protected Uri mEventsUri;
    private int mFirstDayOfWeek;
    private boolean mHasAttendeeData;
    private ContentValues mInitialValues;
    private boolean mIsCanSetDay;
    private boolean mIsCustomizedRepetitionFromDB;
    protected boolean mIsLocalCalendarSelected;
    protected boolean mIsLunarEvent;
    private boolean mIsMonthDay;
    private boolean mIsYearDay;
    private ProgressDialog mLoadingCalendarsDialog;
    protected int mLocalCalendarPosition;
    private TextView mLocationTextView;
    private int mLunarConflictEventCount;
    private int mModification;
    protected int mNewEventIdIndex;
    private AlertDialog mNoCalendarsDialog;
    protected String mOrganizerEmail;
    private String mOriginalAttendeesOptional;
    private String mOriginalAttendeesRequired;
    protected ArrayList<Integer> mOriginalMinutes;
    private String mOwnerAccount;
    private QueryHandler mQueryHandler;
    private TextWatcher mRecipientTextWatcher;
    private ArrayList<Integer> mRecurrenceIndexes;
    private ArrayList<String> mReminderLabels;
    private ArrayList<Integer> mReminderValues;
    private Uri mRemindersUri;
    private int mRepeatsPosition;
    private MultiSelectSpinner mRepeatsSpinner;
    protected String mRrule;
    private int[] mRruleByday;
    private int mRruleBydayCount;
    private int[] mRruleBydayNum;
    private int[] mRruleBymonth;
    private int[] mRruleBymonthday;
    private long mRruleDtstart;
    private int mRruleFreq;
    private int mRruleInterval;
    private String mRruleUntil;
    private boolean mSaveAfterQueryComplete;
    private TextView mSaveSendText;
    private int mSelectedCalendarPosition;
    private String mSelectedEmailAccount;
    private boolean mShowChangeAllSeriesDialog;
    private int mSolarConflictEventCount;
    protected Button mStartDateButton;
    private TextView mStartDateHome;
    protected Time mStartTime;
    private Button mStartTimeButton;
    private TextView mStartTimeHome;
    private String mSyncId;
    private TimePickerDialogListener mTimePickerListener;
    protected String mTimezone;
    private TimezoneAdapter mTimezoneAdapter;
    private Spinner mTimezoneSpinner;
    private TextView mTimezoneTextView;
    private TextView mTitleTextView;
    private boolean mTransitionDone;
    private Uri mUri;
    private Spinner mVisibilitySpinner;
    private TextView mWarningTextView;
    private int mWarningTextViewVisibility;
    private OnColorPickedListener onColorPickedListener;
    private OnColorSetListener onColorSetListener;
    private OnEditEventTimeChangedListener onEditEventTimeChangedListener;
    private ArrayList<Integer> recurrenceIndexes;
    private ArrayList<String> repeatArray;
    public ContentProviderResult[] results;
    private List<DialogFragment> shownDialogs;
    private static final double LOW_MEMORY_LIMIT = Math.pow(2.0d, 20.0d) * 1.0d;
    private static TreeSet<Integer> mSelectedRemindersIndices = new TreeSet<>();
    private static StringBuilder mSB = new StringBuilder(50);
    private static Formatter mF = new Formatter(mSB, Locale.getDefault());
    private static boolean mIsAccountInfoChanged = false;
    private static boolean mTitleAsCompulsory = false;
    private static InputFilter[] sRecipientFilters = {new Rfc822InputFilter()};

    public EditEventView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.eventColor = 0;
        this.mTransitionDone = true;
        this.mWarningTextViewVisibility = 8;
        this.mOriginalMinutes = new ArrayList<>();
        this.mOriginalAttendeesRequired = "";
        this.mOriginalAttendeesOptional = "";
        this.mHasAttendeeData = true;
        this.mEventRecurrence = new EventRecurrence();
        this.mShowChangeAllSeriesDialog = true;
        this.mRecurrenceIndexes = new ArrayList<>(0);
        this.mNewEventIdIndex = -1;
        this.mModification = 0;
        this.mEditModeSelectionDialogShowed = false;
        this.mCalendarPosition = -1;
        this.mIsCustomizedRepetitionFromDB = true;
        this.mAccountsUpdateListener = null;
        this.mIsCanSetDay = true;
        this.mIsLunarEvent = false;
        this.mIsLocalCalendarSelected = true;
        this.mLocalCalendarPosition = 0;
        this.mSolarConflictEventCount = 0;
        this.mLunarConflictEventCount = 0;
        this.mSelectedCalendarPosition = -1;
        this.results = null;
        this.onColorPickedListener = new OnColorPickedListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.5
            @Override // com.sonymobile.calendar.OnColorPickedListener
            public void onColorPicked(int i, boolean z) {
                EditEventView.this.eventColor = i;
                EditEventView.this.setEventColor(i);
            }
        };
        this.mTimePickerListener = new TimePickerDialogListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.15
            @Override // com.sonymobile.calendar.TimePickerDialogListener
            public void onTimeSet(int i, int i2, int i3) {
                long millis;
                Time time = EditEventView.this.mStartTime;
                Time time2 = EditEventView.this.mEndTime;
                if (EditEventView.this.mStartDateButton == null || EditEventView.this.mEndDateButton == null || EditEventView.this.mStartTimeButton == null || EditEventView.this.mEndTimeButton == null) {
                    return;
                }
                if (i3 == EditEventView.this.mStartTimeButton.getId()) {
                    int i4 = time2.hour - time.hour;
                    int i5 = time2.minute - time.minute;
                    time.hour = i;
                    time.minute = i2;
                    millis = time.normalize(true);
                    time2.hour = i + i4;
                    time2.minute = i2 + i5;
                    EditEventView.this.populateTimezone(millis);
                } else {
                    millis = time.toMillis(true);
                    time2.hour = i;
                    time2.minute = i2;
                    if (time2.before(time)) {
                        time2.monthDay = time.monthDay + 1;
                    }
                }
                long jNormalize = time2.normalize(true);
                if (EditEventView.this.mIsLunarEvent) {
                    EditEventView editEventView = EditEventView.this;
                    editEventView.setLunarDate(editEventView.mEndDateButton, jNormalize, EditEventView.this.mTimezone);
                } else {
                    EditEventView editEventView2 = EditEventView.this;
                    editEventView2.setDate(editEventView2.mEndDateButton, jNormalize);
                }
                EditEventView editEventView3 = EditEventView.this;
                editEventView3.setTime(editEventView3.mStartTimeButton, millis);
                EditEventView editEventView4 = EditEventView.this;
                editEventView4.setTime(editEventView4.mEndTimeButton, jNormalize);
                EditEventView.this.updateWarningText();
                EditEventView.this.updateHomeTime();
                EditEventView.this.updateEditEventDayView();
            }
        };
        this.shownDialogs = Lists.newArrayList();
        this.mDatePickerListener = new DatePickerDialogListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.16
            @Override // com.sonymobile.calendar.DatePickerDialogListener
            public void onDateSet(int i, int i2, int i3, int i4) {
                EditEventView.this.performDateSet(i4, null, i, i2, i3);
            }
        };
        this.activity = (AppCompatActivity) context;
        LayoutInflater.from(context).inflate(R.layout.edit_event, (ViewGroup) this, true);
        updateUris();
    }

    public Time setStartTimeForTest(Time time) {
        this.mStartTime.set(time);
        return this.mStartTime;
    }

    public Time setEndTimeForTest(Time time) {
        this.mEndTime.set(time);
        return this.mEndTime;
    }

    public void setInitValues(String str) {
        this.mInitialValues.put(LunarContract.EventsColumns.TITLE, str);
    }

    public void onResume() {
        this.isEditing = true;
        QueryHandler queryHandler = new QueryHandler(getContext().getContentResolver());
        this.mQueryHandler = queryHandler;
        queryHandler.startQuery(0, null, CalendarContract.Calendars.CONTENT_URI, EditEventHelper.CALENDARS_PROJECTION, "calendar_access_level>=500 AND sync_events=1", null, null);
        showChangeAllSeriesOrSingleEventDialog();
    }

    public void onStop() {
        QueryHandler queryHandler = this.mQueryHandler;
        if (queryHandler != null) {
            queryHandler.cancelOperation(0);
        }
    }

    public void onDestroy() {
        Utils.closeCursor(this.mEventCursor);
        Utils.closeCursor(this.mCalendarsCursor);
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (this.mAccountsUpdateListener != null) {
            AccountManager.get(getContext().getApplicationContext()).removeOnAccountsUpdatedListener(this.mAccountsUpdateListener);
        }
        ImageView imageView = this.eventColorButton;
        if (imageView != null) {
            imageView.getDrawable().setColorFilter(0, PorterDuff.Mode.MULTIPLY);
        }
        this.isEditing = false;
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        long millis = this.mStartTime.toMillis(true);
        long millis2 = this.mEndTime.toMillis(true);
        bundle.putBoolean("keyIsLunarEvent", this.mIsLunarEvent);
        bundle.putLong(LunarContract.EXTRA_EVENT_BEGIN_TIME, millis);
        bundle.putLong(LunarContract.EXTRA_EVENT_END_TIME, millis2);
        bundle.putString("keyTimeZone", this.mTimezone);
        bundle.putInt(KEY_SELECTED_CALENDAR_SPINNER_POSITION, this.mCalendarPosition);
        if (this.mIsLunarEvent) {
            updateLunarRecurrenceRule();
        } else {
            updateRecurrenceRule();
        }
        if (!TextUtils.isEmpty(this.mRrule)) {
            boolean z = this.mEditModeSelectionDialogShowed;
            this.mShowChangeAllSeriesDialog = z;
            bundle.putBoolean("keyShowChangeAllDialog", z);
        }
        bundle.putString("keyRrule", this.mRrule);
        bundle.putInt("keyShowMeAs", this.mAvailabilitySpinner.getSelectedItemPosition());
        bundle.putInt("keyPrivacy", this.mVisibilitySpinner.getSelectedItemPosition());
        bundle.putIntegerArrayList("keyReminderMinutes", reminderItemsToMinutes(this.mReminderValues));
        bundle.putIntegerArrayList("keyReminderIndices", new ArrayList<>(mSelectedRemindersIndices));
        bundle.putInt("keyEventColor", this.eventColor);
        bundle.putInt("keyTimeZonePosition", this.mTimezoneSpinner.getSelectedItemPosition());
        bundle.putInt("keyChangeAllSeries", this.mModification);
        bundle.putCharSequence("keyRequiredAttendees", this.mAttendeesListRequired.getText());
        bundle.putBoolean("keyRequiredAttendeesHasFocus", this.mAttendeesListRequired.hasFocus());
        bundle.putCharSequence("keyOptionalAttendees", this.mAttendeesListOptional.getText());
        bundle.putBoolean("keyOptionalAttendeesHasFocus", this.mAttendeesListOptional.hasFocus());
        bundle.putBoolean("keyTransitionDone", this.mTransitionDone);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String string = bundle.getString("keyTimeZone");
        this.mTimezone = string;
        this.mStartTime.timezone = string;
        this.mEndTime.timezone = this.mTimezone;
        this.mStartTime.set(bundle.getLong(LunarContract.EXTRA_EVENT_BEGIN_TIME));
        this.mEndTime.set(bundle.getLong(LunarContract.EXTRA_EVENT_END_TIME));
        this.mSelectedCalendarPosition = bundle.getInt(KEY_SELECTED_CALENDAR_SPINNER_POSITION, -1);
        this.mTransitionDone = bundle.getBoolean("keyTransitionDone", false);
        ArrayList<Integer> integerArrayList = bundle.getIntegerArrayList("keyReminderMinutes");
        mSelectedRemindersIndices.clear();
        if (integerArrayList != null && integerArrayList.size() > 0) {
            Iterator<Integer> it = integerArrayList.iterator();
            while (it.hasNext()) {
                addReminder(it.next().intValue());
            }
        }
        updateRemindersLabel();
        this.mIsLunarEvent = bundle.getBoolean("keyIsLunarEvent", false);
        clearRecurrence();
        String string2 = bundle.getString("keyRrule", null);
        this.mRrule = string2;
        if (string2 != null && checkRule()) {
            this.mEventRecurrence.parse(this.mRrule);
        }
        if (this.mIsLunarEvent) {
            populateLunarRepeats();
        } else {
            populateRepeats();
        }
        this.mShowChangeAllSeriesDialog = bundle.getBoolean("keyShowChangeAllDialog", true);
        this.eventColor = bundle.getInt("keyEventColor", 0);
        initEventColor();
        int i = bundle.getInt("keyChangeAllSeries");
        this.mModification = i;
        if (i == 2) {
            this.mStartDateButton.setEnabled(false);
            this.mEndDateButton.setEnabled(false);
        } else if (i == 1) {
            this.mRepeatsSpinner.setEnabled(false);
        }
        populateWhen();
        this.mAvailabilitySpinner.setSelection(bundle.getInt("keyShowMeAs"));
        this.mVisibilitySpinner.setSelection(bundle.getInt("keyPrivacy"));
        this.mTimezoneSpinner.setSelection(bundle.getInt("keyTimeZonePosition"));
        this.mAttendeesListRequired.setText(bundle.getCharSequence("keyRequiredAttendees"));
        RecipientEditTextView recipientEditTextView = this.mAttendeesListRequired;
        recipientEditTextView.setSelection(recipientEditTextView.getText().toString().length());
        this.mAttendeesListOptional.setText(bundle.getCharSequence("keyOptionalAttendees"));
        RecipientEditTextView recipientEditTextView2 = this.mAttendeesListOptional;
        recipientEditTextView2.setSelection(recipientEditTextView2.getText().toString().length());
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1 || intent == null) {
            return;
        }
        if (i == 1) {
            this.mShowChangeAllSeriesDialog = false;
            setRepetitionData(intent);
        } else if (i == 2 || i == 3) {
            setContactsData(i, intent);
        } else if (i == 4) {
            setTimeRangeData(intent);
        } else {
            if (i != 5) {
                return;
            }
            setLocationData(intent);
        }
    }

    public void exitOnBackPress() {
        if (!isRequiredInfoEmpty()) {
            SaveEventDialogFragment saveEventDialogFragment = new SaveEventDialogFragment();
            saveEventDialogFragment.setView(this);
            saveEventDialogFragment.show(this.activity.getSupportFragmentManager(), SAVE_EVENT_DIALOG_TAG);
            return;
        }
        this.activity.finish();
    }

    protected void showChangeAllSeriesOrSingleEventDialog() {
        CharSequence[] charSequenceArr;
        if (TextUtils.isEmpty(this.mRrule) || this.mModification != 0 || this.mEditModeSelectionDialogShowed || !this.mShowChangeAllSeriesDialog) {
            return;
        }
        char c = 0;
        if (this.mSyncId == null) {
            charSequenceArr = new CharSequence[1];
        } else {
            charSequenceArr = new CharSequence[2];
            charSequenceArr[0] = getContext().getText(R.string.modify_event);
            c = 1;
        }
        charSequenceArr[c] = getContext().getText(R.string.modify_all);
        this.mAlertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                EditEventView.this.activity.finish();
            }
        }).setTitle(R.string.edit_event_label).setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    EditEventView editEventView = EditEventView.this;
                    editEventView.mModification = editEventView.mSyncId == null ? 2 : 1;
                } else if (i == 1) {
                    EditEventView.this.mModification = 2;
                }
                EditEventView.this.mEditModeSelectionDialogShowed = false;
                if (EditEventView.this.mModification != 2) {
                    if (EditEventView.this.mModification == 1) {
                        EditEventView.this.mRepeatsSpinner.setEnabled(false);
                    }
                } else {
                    EditEventView.this.mStartDateButton.setEnabled(false);
                    EditEventView.this.mEndDateButton.setEnabled(false);
                    EditEventView.this.mIsCanSetDay = false;
                    EditEventView.this.updateEditEventDayView();
                }
            }
        }).show();
        this.mEditModeSelectionDialogShowed = true;
    }

    public void updateTime(long j, long j2, boolean z) {
        String displayName = z ? TimeZone.getTimeZone("GMT").getDisplayName() : Utils.getTimeZone(getContext(), null);
        this.mTimezone = displayName;
        setTimezone(this.mTimezoneAdapter.getRowById(displayName));
        this.mStartTime.set(j);
        this.mEndTime.set(j2);
        setDate(this.mStartDateButton, j);
        setDate(this.mEndDateButton, j2);
        setTime(this.mStartTimeButton, j);
        setTime(this.mEndTimeButton, j2);
        this.mAllDaySwitch.setChecked(z);
        updateWarningText();
    }

    public boolean save() {
        String saveEventText;
        if (Utils.getAvailiableMemory() < LOW_MEMORY_LIMIT) {
            Toast.makeText(getContext(), R.string.memory_low_event, 1).show();
            return false;
        }
        if (!this.isDirty) {
            return true;
        }
        if (isRequiredInfoEmpty()) {
            markTitleAsCompulsory();
            return false;
        }
        if (this.mEventCursor == null) {
            if (!this.mCalendarsQueryComplete) {
                if (this.mLoadingCalendarsDialog == null) {
                    this.mLoadingCalendarsDialog = ProgressDialog.show(getContext(), getContext().getText(R.string.loading_calendars_title), getContext().getText(R.string.loading_calendars_message), true, true, this);
                    this.mSaveAfterQueryComplete = true;
                }
                return false;
            }
            Cursor cursor = this.mCalendarsCursor;
            if (cursor == null || cursor.getCount() == 0 || this.mCalendarsSpinner.getSelectedItemId() == Long.MIN_VALUE) {
                Log.w("Cal", "The calendars table does not contain any calendars or no calendar was selected. New event was not created.");
                return true;
            }
        }
        ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
        ContentValues contentValuesFromUi = getContentValuesFromUi();
        if (!this.mAllDaySwitch.isChecked()) {
            this.mTimezoneAdapter.saveRecentTimezone(this.mTimezone);
        }
        String string = this.mCalendarsCursor.getString(2);
        String string2 = this.mCalendarsCursor.getString(4);
        ArrayList<Integer> arrayListReminderItemsToMinutes = reminderItemsToMinutes(this.mReminderValues);
        contentValuesFromUi.put(LunarContract.EventsColumns.HAS_ALARM, Integer.valueOf(arrayListReminderItemsToMinutes.size() > 0 ? 1 : 0));
        saveRruleAndReminders(arrayList, contentValuesFromUi, this.mUri, arrayListReminderItemsToMinutes, Utils.isGoogleAccountType(string2));
        boolean z = this.mNewEventIdIndex != -1;
        if (this.mHasAttendeeData && z) {
            saveOrganizersResponseAsAttending(arrayList, contentValuesFromUi);
        }
        if (!this.mHasAttendeeData && (!TextUtils.isEmpty(this.mAttendeesListRequired.getText()) || !TextUtils.isEmpty(this.mAttendeesListOptional.getText()))) {
            contentValuesFromUi.put(LunarContract.EventsColumns.HAS_ATTENDEE_DATA, (Integer) 1);
            arrayList.add(ContentProviderOperation.newUpdate(this.mUri).withValues(contentValuesFromUi).build());
            this.mHasAttendeeData = true;
        }
        if (this.mHasAttendeeData && (z || this.mUri != null)) {
            saveAttendies(arrayList, contentValuesFromUi, this.mUri, z);
        }
        commitSave(arrayList);
        updateAlertNotification();
        Utils.scheduleSync(1, string, string2, true, false);
        if (hasRecipients() && !string2.equals("LOCAL") && !z) {
            saveEventText = getResources().getString(R.string.sending_updated_meeting_invitation);
        } else {
            saveEventText = getSaveEventText();
        }
        Toast.makeText(getContext(), saveEventText, 1).show();
        this.isDirty = false;
        return true;
    }

    protected boolean isBasicEventInfoModified() {
        String string = this.mEventCursor.getString(1);
        String string2 = this.mEventCursor.getString(3);
        int selectedItemPosition = this.mRepeatsSpinner.getSelectedItemPosition();
        if (string == null && this.mTitleTextView.getText().toString().trim().length() > 0) {
            return true;
        }
        if (string2 != null || this.mLocationTextView.getText().toString().trim().length() <= 0) {
            return ((string == null || string.equals(this.mTitleTextView.getText().toString().trim())) && (string2 == null || string2.equals(this.mLocationTextView.getText().toString().trim())) && this.mRepeatsPosition == selectedItemPosition) ? false : true;
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:33:0x0104  */
    /* JADX WARN: Code duplicated, block: B:34:0x0111  */
    protected void saveRruleAndReminders(ArrayList<ContentProviderOperation> arrayList, ContentValues contentValues, Uri uri, ArrayList<Integer> arrayList2, boolean z) {
        boolean z2;
        int i;
        if (this.mInitialValues.getAsString("rrule") == null) {
            addRecurrenceRule(contentValues);
            if (this.mRrule != null) {
                contentValues.putNull(LunarContract.EventsColumns.DTEND);
                contentValues.putNull("lastDate");
            } else {
                checkTimeDependentFields(contentValues);
            }
            arrayList.add(ContentProviderOperation.newUpdate(uri).withValues(contentValues).build());
        } else {
            int i2 = this.mModification;
            if (i2 == 1) {
                long jLongValue = this.mInitialValues.getAsLong(LunarContract.EXTRA_EVENT_BEGIN_TIME).longValue();
                contentValues.put("original_sync_id", this.mEventCursor.getString(11));
                contentValues.put("originalInstanceTime", Long.valueOf(jLongValue));
                Integer asInteger = this.mInitialValues.getAsInteger("allDay");
                if (asInteger != null) {
                    contentValues.put("originalAllDay", Integer.valueOf(asInteger.intValue() != 0 ? 1 : 0));
                }
                this.mNewEventIdIndex = arrayList.size();
                contentValues.put(LunarContract.EventsColumns.HAS_ATTENDEE_DATA, (Integer) 1);
                arrayList.add(ContentProviderOperation.newInsert(this.mEventsUri).withValues(contentValues).build());
            } else if (i2 == 2) {
                addRecurrenceRule(contentValues);
                if (this.mRrule == null) {
                    arrayList.add(ContentProviderOperation.newDelete(uri).build());
                    this.mNewEventIdIndex = arrayList.size();
                    contentValues.put(LunarContract.EventsColumns.HAS_ATTENDEE_DATA, (Integer) 1);
                    arrayList.add(ContentProviderOperation.newInsert(this.mEventsUri).withValues(contentValues).build());
                } else {
                    if (!this.mAttendeesListRequired.getText().toString().equals("") || !this.mAttendeesListOptional.getText().toString().equals("")) {
                        contentValues.put(LunarContract.EventsColumns.HAS_ATTENDEE_DATA, (Integer) 1);
                    }
                    checkTimeDependentFields(contentValues);
                    contentValues.remove(LunarContract.EventsColumns.DTEND);
                    arrayList.add(ContentProviderOperation.newUpdate(uri).withValues(contentValues).build());
                }
            }
            z2 = true;
            i = this.mNewEventIdIndex;
            if (i != -1) {
                saveRemindersWithBackRef(arrayList, i, arrayList2, this.mOriginalMinutes, z2, this.mIsLunarEvent, z);
            } else {
                saveReminders(arrayList, ContentUris.parseId(uri), arrayList2, this.mOriginalMinutes, z2, this.mIsLunarEvent);
            }
        }
        z2 = false;
        i = this.mNewEventIdIndex;
        if (i != -1) {
            saveRemindersWithBackRef(arrayList, i, arrayList2, this.mOriginalMinutes, z2, this.mIsLunarEvent, z);
        } else {
            saveReminders(arrayList, ContentUris.parseId(uri), arrayList2, this.mOriginalMinutes, z2, this.mIsLunarEvent);
        }
    }

    private void saveOrganizersResponseAsAttending(ArrayList<ContentProviderOperation> arrayList, ContentValues contentValues) {
        Cursor cursor;
        contentValues.clear();
        int selectedItemPosition = this.mCalendarsSpinner.getSelectedItemPosition();
        Cursor cursor2 = this.mCalendarsCursor;
        if (cursor2 != null && cursor2.moveToPosition(selectedItemPosition)) {
            Utils.setSharedPreference(getContext(), GeneralPreferences.KEY_DEFAULT_CALENDAR, Integer.toString(this.mCalendarsCursor.getInt(0)));
        }
        String string = this.mOwnerAccount;
        if (string == null && (cursor = this.mCalendarsCursor) != null && cursor.moveToPosition(selectedItemPosition)) {
            string = this.mCalendarsCursor.getString(2);
        }
        if (string != null) {
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_EMAIL, string);
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, (Integer) 2);
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_TYPE, (Integer) 1);
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_STATUS, Integer.valueOf(1 ^ (string.endsWith("calendar.google.com") ? 1 : 0)));
            ContentProviderOperation.Builder builderWithValues = ContentProviderOperation.newInsert(this.mAttendeesUri).withValues(contentValues);
            builderWithValues.withValueBackReference("event_id", this.mNewEventIdIndex);
            arrayList.add(builderWithValues.build());
        }
    }

    private void saveAttendies(ArrayList<ContentProviderOperation> arrayList, ContentValues contentValues, Uri uri, boolean z) {
        Editable text = this.mAttendeesListRequired.getText();
        Editable text2 = this.mAttendeesListOptional.getText();
        if (!z && this.mOriginalAttendeesRequired.equals(text.toString()) && this.mOriginalAttendeesOptional.equals(text2.toString())) {
            return;
        }
        LinkedHashSet<Rfc822Token> addressesFromList = getAddressesFromList(this.mAttendeesListRequired);
        LinkedHashSet<Rfc822Token> addressesFromList2 = getAddressesFromList(this.mAttendeesListOptional);
        Iterator<Rfc822Token> it = addressesFromList2.iterator();
        while (it.hasNext()) {
            Rfc822Token next = it.next();
            Iterator<Rfc822Token> it2 = addressesFromList.iterator();
            while (it2.hasNext()) {
                if (next.getAddress().equalsIgnoreCase(it2.next().getAddress())) {
                    it.remove();
                }
            }
        }
        long id = -1;
        if (uri != null) {
            try {
                id = ContentUris.parseId(uri);
            } catch (NumberFormatException unused) {
            }
        }
        long j = id;
        if (!z) {
            HashSet hashSet = new HashSet();
            HashSet hashSet2 = new HashSet();
            HashSet<Rfc822Token> hashSet3 = new HashSet();
            HashSet<Rfc822Token> hashSet4 = new HashSet();
            Rfc822Tokenizer.tokenize(this.mOriginalAttendeesRequired, hashSet3);
            Rfc822Tokenizer.tokenize(this.mOriginalAttendeesOptional, hashSet4);
            for (Rfc822Token rfc822Token : hashSet3) {
                if (addressesFromList.contains(rfc822Token)) {
                    addressesFromList.remove(rfc822Token);
                } else {
                    hashSet.add(rfc822Token);
                }
            }
            for (Rfc822Token rfc822Token2 : hashSet4) {
                if (addressesFromList2.contains(rfc822Token2)) {
                    addressesFromList2.remove(rfc822Token2);
                } else {
                    hashSet2.add(rfc822Token2);
                }
            }
            ContentProviderOperation.Builder builderNewDelete = ContentProviderOperation.newDelete(this.mAttendeesUri);
            ContentProviderOperation.Builder builderNewDelete2 = ContentProviderOperation.newDelete(this.mAttendeesUri);
            String[] strArr = new String[hashSet.size() + 1];
            String[] strArr2 = new String[hashSet2.size() + 1];
            strArr[0] = Long.toString(j);
            strArr2[0] = Long.toString(j);
            StringBuilder sb = new StringBuilder("event_id=? AND attendeeEmail IN (");
            StringBuilder sb2 = new StringBuilder("event_id=? AND attendeeEmail IN (");
            Iterator it3 = hashSet.iterator();
            int i = 1;
            while (it3.hasNext()) {
                Rfc822Token rfc822Token3 = (Rfc822Token) it3.next();
                Iterator it4 = it3;
                if (i > 1) {
                    sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
                }
                sb.append("?");
                strArr[i] = rfc822Token3.getAddress();
                i++;
                it3 = it4;
            }
            sb.append(")");
            Iterator it5 = hashSet2.iterator();
            int i2 = 1;
            while (it5.hasNext()) {
                Rfc822Token rfc822Token4 = (Rfc822Token) it5.next();
                Iterator it6 = it5;
                if (i2 > 1) {
                    sb2.append(RecurrenceRuleParser.VALUE_SEPARATOR);
                }
                sb2.append("?");
                strArr2[i2] = rfc822Token4.getAddress();
                it5 = it6;
                i2++;
            }
            sb2.append(")");
            builderNewDelete.withSelection(sb.toString(), strArr);
            builderNewDelete2.withSelection(sb2.toString(), strArr2);
            arrayList.add(builderNewDelete.build());
            arrayList.add(builderNewDelete2.build());
        }
        if (addressesFromList.size() > 0) {
            addAttendeesToContentOps(arrayList, contentValues, z, addressesFromList, j, 1);
        }
        if (addressesFromList2.size() > 0) {
            addAttendeesToContentOps(arrayList, contentValues, z, addressesFromList2, j, 2);
        }
    }

    private void addAttendeesToContentOps(ArrayList<ContentProviderOperation> arrayList, ContentValues contentValues, boolean z, LinkedHashSet<Rfc822Token> linkedHashSet, long j, int i) {
        ContentProviderOperation.Builder builderWithValues;
        for (Rfc822Token rfc822Token : linkedHashSet) {
            contentValues.clear();
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_NAME, rfc822Token.getName());
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_EMAIL, rfc822Token.getAddress());
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, (Integer) 1);
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_TYPE, Integer.valueOf(i));
            contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_STATUS, (Integer) 0);
            if (z) {
                builderWithValues = ContentProviderOperation.newInsert(this.mAttendeesUri).withValues(contentValues);
                builderWithValues.withValueBackReference("event_id", this.mNewEventIdIndex);
            } else {
                contentValues.put("event_id", Long.valueOf(j));
                builderWithValues = ContentProviderOperation.newInsert(this.mAttendeesUri).withValues(contentValues);
            }
            arrayList.add(builderWithValues.build());
        }
    }

    private void commitSave(ArrayList<ContentProviderOperation> arrayList) {
        try {
            if (this.mIsLunarEvent) {
                this.results = this.activity.getContentResolver().applyBatch(LunarContract.AUTHORITY, arrayList);
            } else {
                this.results = this.activity.getContentResolver().applyBatch("com.android.calendar", arrayList);
            }
            if (this.mNewEventIdIndex != -1) {
                Intent intent = new Intent();
                intent.putExtra(NEW_EVENT_ID_EXTRA, this.results[this.mNewEventIdIndex].uri);
                this.activity.setResult(-1, intent);
            }
        } catch (OperationApplicationException e) {
            Log.w(TAG, "Ignoring unexpected exception", e);
        } catch (RemoteException e2) {
            Log.w(TAG, "Ignoring unexpected remote exception", e2);
        }
    }

    protected String getSaveEventText() {
        return getResources().getString(R.string.saving_event);
    }

    private void updateAlertNotification() {
        if (isBasicEventInfoModified()) {
            Intent intent = new Intent(LunarContract.ACTION_EVENT_REMINDER);
            Intent intent2 = new Intent();
            intent2.setClass(getContext().getApplicationContext(), AlertWork.class);
            intent2.putExtras(intent);
            intent2.putExtra("action", intent.getAction());
            AlertReceiver.beginStartingService(getContext().getApplicationContext(), intent2);
        }
    }

    private void markTitleAsCompulsory() {
        mTitleAsCompulsory = true;
        this.mTitleTextView.setBackgroundResource(R.drawable.title_textview_error_border);
        this.mTitleTextView.requestFocus();
        scrollTo((int) this.mTitleTextView.getX(), (int) this.mTitleTextView.getY());
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        ((LinearLayout) viewGroup.getParent()).removeView(viewGroup);
    }

    public void setOnEditEventTimeChangedListener(OnEditEventTimeChangedListener onEditEventTimeChangedListener) {
        this.onEditEventTimeChangedListener = onEditEventTimeChangedListener;
    }

    @Override // android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        if (dialogInterface == this.mLoadingCalendarsDialog) {
            this.mSaveAfterQueryComplete = false;
        } else if (dialogInterface == this.mNoCalendarsDialog) {
            this.activity.finish();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (dialogInterface == this.mNoCalendarsDialog) {
            this.activity.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String extractDomain(String str) {
        int i;
        int iLastIndexOf = str.lastIndexOf(64);
        if (iLastIndexOf == -1 || (i = iLastIndexOf + 1) >= str.length()) {
            return null;
        }
        return str.substring(i);
    }

    public void init(Intent intent, Bundle bundle, AppCompatActivity appCompatActivity) {
        this.isDirty = true;
        if (initControls(intent)) {
            loadDataFromDatabase(bundle);
        }
        AccountManager accountManager = AccountManager.get(appCompatActivity.getApplicationContext());
        AccountsUpdateListener accountsUpdateListener = new AccountsUpdateListener();
        this.mAccountsUpdateListener = accountsUpdateListener;
        accountManager.addOnAccountsUpdatedListener(accountsUpdateListener, null, false);
        Fragment fragmentFindFragmentByTag = appCompatActivity.getSupportFragmentManager().findFragmentByTag(ColorPickerDialogBase.TAG);
        if (fragmentFindFragmentByTag != null) {
            ((ColorPickerDialogBase) fragmentFindFragmentByTag).setOnColorPickedListener(this.onColorPickedListener);
        }
    }

    private void addReminder(int i) {
        ArrayList<Integer> arrayList = this.mReminderValues;
        if (arrayList == null || this.mReminderLabels == null) {
            return;
        }
        mSelectedRemindersIndices.add(Integer.valueOf(findMinutesInReminderList(arrayList, i)));
    }

    private static void addMinutesToList(Context context, ArrayList<Integer> arrayList, ArrayList<String> arrayList2, int i) {
        if (arrayList.indexOf(Integer.valueOf(i)) != -1) {
            return;
        }
        String strConstructReminderLabelOrCustom = Utils.constructReminderLabelOrCustom(context, i, false);
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (i < arrayList.get(i2).intValue()) {
                arrayList.add(i2, Integer.valueOf(i));
                arrayList2.add(i2, strConstructReminderLabelOrCustom);
                return;
            }
        }
        arrayList.add(Integer.valueOf(i));
        arrayList2.add(size, strConstructReminderLabelOrCustom);
    }

    private boolean initControls(Intent intent) {
        this.mFirstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();
        this.mOrganizerEmail = intent.getStringExtra(EventInfoFragment.ORGANIZER_EMAIL);
        this.mDomain = DOMAIN_GMAIL;
        this.mUri = intent.getData();
        if (!initEventTimes(intent)) {
            return false;
        }
        this.mConflictQueryHandler = new ConflictQueryHandler(this.activity.getContentResolver());
        this.mTitleTextView = (TextView) findViewById(R.id.title);
        this.mDescriptionTextView = (EditText) findViewById(R.id.description);
        this.mWarningTextView = (TextView) findViewById(R.id.warning_label);
        this.mTimezoneTextView = (TextView) findViewById(R.id.timezone_label);
        this.mAvailabilitySpinner = (Spinner) findViewById(R.id.availability);
        this.mVisibilitySpinner = (Spinner) findViewById(R.id.visibility);
        this.mAddReminderView = (TextView) this.activity.findViewById(R.id.add_reminder);
        this.mEmailValidator = new Rfc822Validator(this.mDomain);
        this.mRecipientTextWatcher = new RecipientTextWatcher(this);
        mTitleTextBackground = this.mTitleTextView.getBackground();
        initLocation();
        initTimeControls();
        initCheckAvailabiliyButton();
        initTimezoneSpinner();
        initAllDayEventSwitch(this.mStartTime.allDay);
        initRepetitionSpinner();
        initCalendarSpinner();
        Intent intent2 = new Intent("android.intent.action.PICK");
        intent2.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        intent2.setType("vnd.android.cursor.dir/email_v2");
        boolean zIsIntentRecipientAvailable = Utils.isIntentRecipientAvailable(this.activity, intent2);
        if (!zIsIntentRecipientAvailable) {
            intent2.setAction("com.sonyericsson.android.socialphonebook.PICK");
            zIsIntentRecipientAvailable = Utils.isIntentRecipientAvailable(this.activity, intent2);
        }
        boolean z = zIsIntentRecipientAvailable && Utils.isReadContactsEnabled(this.activity);
        initAddRequiredAttendeesControls(z, intent2);
        initAddOptionalAttendeesControls(z, intent2);
        initReminderControls();
        initEventColor();
        this.mTitleTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z2) {
                if (z2 || !EditEventView.mTitleAsCompulsory) {
                    return;
                }
                boolean unused = EditEventView.mTitleAsCompulsory = false;
                if (EditEventView.mTitleTextBackground != null) {
                    EditEventView.this.mTitleTextView.setBackground(EditEventView.mTitleTextBackground);
                }
            }
        });
        Fragment fragmentFindFragmentByTag = this.activity.getSupportFragmentManager().findFragmentByTag(TIME_PICKER_TAG);
        if (fragmentFindFragmentByTag != null) {
            ((TimePickerDialogFragment) fragmentFindFragmentByTag).setTimeListener(this.mTimePickerListener);
        }
        Fragment fragmentFindFragmentByTag2 = this.activity.getSupportFragmentManager().findFragmentByTag(DATE_PICKER_OLD_TAG);
        if (fragmentFindFragmentByTag2 != null) {
            Utils.restoreDatePickerDialogFragment(getContext(), (DatePickerDialogFragment) fragmentFindFragmentByTag2, this.mDatePickerListener);
        }
        Fragment fragmentFindFragmentByTag3 = this.activity.getSupportFragmentManager().findFragmentByTag(START_DATE_PICKER_TAG);
        if (fragmentFindFragmentByTag3 != null) {
            Utils.restoreDatePickerDialog(getContext(), (DatePickerDialog) fragmentFindFragmentByTag3, new DateSetListener(this.mStartDateButton.getId()));
        }
        Fragment fragmentFindFragmentByTag4 = this.activity.getSupportFragmentManager().findFragmentByTag(END_DATE_PICKER_TAG);
        if (fragmentFindFragmentByTag4 != null) {
            Utils.restoreDatePickerDialog(getContext(), (DatePickerDialog) fragmentFindFragmentByTag4, new DateSetListener(this.mEndDateButton.getId()));
        }
        setAvailButtonVisibility();
        Cursor cursor = this.mEventCursor;
        this.mEventId = cursor == null ? -1L : cursor.getLong(0);
        ContentResolver contentResolver = getContext().getContentResolver();
        if (cursorNotEmpty(this.mEventCursor) && this.mEventCursor.getInt(5) != 0) {
            Cursor cursorQuery = contentResolver.query(this.mRemindersUri, EditEventHelper.REMINDERS_PROJECTION, "event_id=? AND (method=1 OR method=0)", new String[]{Long.toString(this.mEventId)}, null);
            while (cursorQuery.moveToNext()) {
                try {
                    addMinutesToList(getContext(), this.mReminderValues, this.mReminderLabels, cursorQuery.getInt(1));
                } catch (Throwable th) {
                    if (cursorQuery != null) {
                        try {
                            cursorQuery.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            cursorQuery.moveToPosition(-1);
            while (cursorQuery.moveToNext()) {
                int i = cursorQuery.getInt(1);
                this.mOriginalMinutes.add(Integer.valueOf(i));
                addReminder(i);
            }
            updateRemindersLabel();
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
        if (this.mHasAttendeeData) {
            long j = this.mEventId;
            if (j != -1) {
                Cursor cursorQuery2 = contentResolver.query(this.mAttendeesUri, EditEventHelper.ATTENDEES_PROJECTION, "event_id=? AND attendeeRelationship<>2", new String[]{Long.toString(j)}, null);
                try {
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sb2 = new StringBuilder();
                    while (cursorQuery2.moveToNext()) {
                        String string = cursorQuery2.getString(0);
                        String string2 = cursorQuery2.getString(1);
                        int i2 = cursorQuery2.getInt(2);
                        if (string2 != null) {
                            String strTrim = string2.trim();
                            if (strTrim.length() != 0) {
                                if (string != null && string.length() > 0 && !string.equals(strTrim)) {
                                    if (i2 == 1) {
                                        sb.append('\"').append(string).append("\" ");
                                    } else {
                                        sb2.append('\"').append(string).append("\" ");
                                    }
                                }
                                if (i2 == 1) {
                                    sb.append('<').append(strTrim).append(">, ");
                                } else {
                                    sb2.append('<').append(strTrim).append(">, ");
                                }
                            }
                        }
                    }
                    boolean zIsReadContactsEnabled = Utils.isReadContactsEnabled(getContext());
                    if (sb.length() > 0) {
                        String strTrim2 = sb.toString().trim();
                        this.mOriginalAttendeesRequired = strTrim2;
                        if (zIsReadContactsEnabled) {
                            for (String str : strTrim2.split(">,")) {
                                String str2 = str + ">,";
                                this.mAttendeesListRequired.append(str + ">,");
                            }
                        }
                    }
                    if (sb2.length() > 0) {
                        String strTrim3 = sb2.toString().trim();
                        this.mOriginalAttendeesOptional = strTrim3;
                        if (zIsReadContactsEnabled) {
                            for (String str3 : strTrim3.split(">,")) {
                                this.mAttendeesListOptional.append(str3 + ">,");
                            }
                        }
                    }
                    if (!zIsReadContactsEnabled) {
                        this.mAttendeesListOptional.setText(this.mOriginalAttendeesOptional);
                        this.mAttendeesListRequired.setText(this.mOriginalAttendeesRequired);
                    }
                    if (cursorQuery2 != null) {
                        cursorQuery2.close();
                    }
                } catch (Throwable th3) {
                    if (cursorQuery2 != null) {
                        try {
                            cursorQuery2.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                    }
                    throw th3;
                }
            }
        }
        populateAvailability();
        populateVisibility();
        if (this.mEventCursor == null) {
            initFromIntent(intent);
        }
        return true;
    }

    protected void setSaveSendTextView(TextView textView) {
        this.mSaveSendText = textView;
    }

    protected void setAvailButtonVisibility() {
        if (cursorNotEmpty(this.mEventCursor)) {
            Cursor cursorQuery = getContext().getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, EditEventHelper.CALENDARS_PROJECTION, "calendar_access_level>=500 AND sync_events=1 AND _id=" + this.mInitialValues.get(LunarContract.EventsColumns.CALENDAR_ID), null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst() && Utils.isExchangeAccountType(cursorQuery.getString(4))) {
                        this.mCheckAvailability.setVisibility(0);
                    }
                } catch (Throwable th) {
                    if (cursorQuery != null) {
                        try {
                            cursorQuery.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    /* JADX WARN: Type inference failed for: r2v13 */
    /* JADX WARN: Type inference failed for: r2v14, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r2v15 */
    protected boolean initEventTimes(Intent intent) {
        String strExtractDomain;
        Cursor cursorQuery = getContext().getContentResolver().query(this.mUri, EditEventHelper.EVENT_PROJECTION, null, null, null);
        this.mEventCursor = cursorQuery;
        if (!cursorNotEmpty(cursorQuery)) {
            this.activity.finish();
            return false;
        }
        this.mIsLunarEvent = this.mUri.getAuthority().equalsIgnoreCase(LunarContract.AUTHORITY);
        updateUris();
        if (!Utils.isLunar(this.activity) && this.mIsLunarEvent) {
            this.activity.finish();
            return false;
        }
        this.mEventCursor.moveToFirst();
        long longExtra = this.mEventCursor.getLong(7);
        long longExtra2 = this.mEventCursor.getLong(8);
        boolean r2 = this.mEventCursor.getInt(4) != 0;
        this.mTimezone = this.mEventCursor.getString(9);
        String string = this.mEventCursor.getString(10);
        if (string != null) {
            longExtra = intent.getLongExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, 0L);
            longExtra2 = intent.getLongExtra(LunarContract.EXTRA_EVENT_END_TIME, 0L);
        }
        this.mHasAttendeeData = this.mEventCursor.getInt(15) != 0;
        long j = this.mEventCursor.getInt(6);
        String string2 = this.mEventCursor.getString(14);
        this.mOwnerAccount = string2;
        if (!TextUtils.isEmpty(string2) && (strExtractDomain = extractDomain(this.mOwnerAccount)) != null) {
            this.mDomain = strExtractDomain;
        }
        Cursor cursor = this.mEventCursor;
        int i = cursor.getInt(cursor.getColumnIndex(LunarContract.EventsColumns.EVENT_COLOR));
        if (i == 0) {
            i = this.mEventCursor.getInt(19);
        }
        if (i != 0) {
            this.eventColor = i;
        }
        ContentValues contentValues = new ContentValues();
        this.mInitialValues = contentValues;
        contentValues.put(LunarContract.EXTRA_EVENT_BEGIN_TIME, Long.valueOf(longExtra));
        this.mInitialValues.put(LunarContract.EXTRA_EVENT_END_TIME, Long.valueOf(longExtra2));
        this.mInitialValues.put("allDay", Integer.valueOf(r2 ? 1 : 0));
        this.mInitialValues.put("rrule", string);
        this.mInitialValues.put(LunarContract.EventsColumns.EVENT_TIMEZONE, this.mTimezone);
        this.mInitialValues.put(LunarContract.EventsColumns.CALENDAR_ID, Long.valueOf(j));
        this.mStartTime = new SafeTime(this.mTimezone);
        this.mEndTime = new SafeTime(this.mTimezone);
        this.mStartTime.set(longExtra);
        this.mStartTime.second = 0;
        this.mEndTime.set(longExtra2);
        this.mEndTime.second = 0;
        this.mStartTime.allDay = r2;
        if (r2) {
            this.mStartTime.second = 0;
            this.mStartTime.minute = 0;
            this.mStartTime.hour = 0;
        }
        return true;
    }

    protected void initCalendarSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.calendars);
        this.mCalendarsSpinner = spinner;
        spinner.setEnabled(false);
    }

    protected void initLuniSolarPicker() {
        if (this.mIsLunarEvent) {
            initButtonListeners(3);
        } else {
            initButtonListeners(2);
        }
    }

    protected void initButtonListeners(int i) {
        this.mStartDateButton.setOnClickListener(new DateClickListener(this.mStartTime, i));
        this.mEndDateButton.setOnClickListener(new DateClickListener(this.mEndTime, i));
    }

    private void initTimeControls() {
        this.mStartDateButton = (Button) findViewById(R.id.start_date);
        this.mEndDateButton = (Button) findViewById(R.id.end_date);
        this.mStartTimeButton = (Button) findViewById(R.id.start_time);
        this.mEndTimeButton = (Button) findViewById(R.id.end_time);
        this.mStartTimeHome = (TextView) findViewById(R.id.start_time_home);
        this.mStartDateHome = (TextView) findViewById(R.id.start_date_home);
        this.mEndTimeHome = (TextView) findViewById(R.id.end_time_home);
        this.mEndDateHome = (TextView) findViewById(R.id.end_date_home);
        this.mStartTimeHome.setAlpha(0.5f);
        this.mStartDateHome.setAlpha(0.5f);
        this.mEndTimeHome.setAlpha(0.5f);
        this.mEndDateHome.setAlpha(0.5f);
    }

    private void initEventColor() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.event_color_button);
        this.eventColorButton = imageButton;
        if (this.eventColor != 0) {
            imageButton.getDrawable().setColorFilter(UiUtils.getDisplayColorFromColor(this.eventColor), PorterDuff.Mode.MULTIPLY);
        }
        this.eventColorButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.4
            private static final long CLICK_DELAY = 500;
            private long mLastClick;

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (Math.abs(this.mLastClick - jCurrentTimeMillis) < CLICK_DELAY) {
                    return;
                }
                this.mLastClick = jCurrentTimeMillis;
                new CalendarColorQuery((AppCompatActivity) EditEventView.this.getContext(), EditEventView.this.onColorPickedListener, EditEventView.this.eventColor, true).startLoader(EditEventView.this.mCalendarsCursor.getString(2), EditEventView.this.mCalendarsCursor.getString(4), 1);
            }
        });
    }

    public void forceLoad(LoaderManager loaderManager) {
        if (loaderManager.getLoader(1) != null) {
            loaderManager.getLoader(1).forceLoad();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEventColor(int i) {
        if (i == 0) {
            getCalendarColorForButton();
            return;
        }
        OnColorSetListener onColorSetListener = this.onColorSetListener;
        if (onColorSetListener != null) {
            onColorSetListener.onColorSet(i);
        }
        this.eventColorButton.getDrawable().setColorFilter(UiUtils.getDisplayColorFromColor(i), PorterDuff.Mode.MULTIPLY);
    }

    protected void getCalendarColorForButton() {
        Cursor cursor = this.mCalendarsCursor;
        if (cursor != null && cursor.moveToPosition(this.mCalendarsSpinner.getSelectedItemPosition()) && this.eventColor == 0) {
            CalendarColorService.getInstance().requestColor(getContext(), new ColorServiceResultHandler(this.eventColorButton.getDrawable(), this.onColorSetListener), 1, this.mCalendarsCursor.getInt(0), true, this.mIsLunarEvent);
        }
    }

    public void setOnColorSetListener(OnColorSetListener onColorSetListener) {
        this.onColorSetListener = onColorSetListener;
    }

    private void initLocation() {
        this.mLocationTextView = (TextView) findViewById(R.id.location);
        ImageButton imageButton = (ImageButton) findViewById(R.id.maps_button);
        if (CustomizeConfig.getInstance().getShowDataUsage(getContext())) {
            imageButton.setVisibility(8);
            return;
        }
        int iIsGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext().getApplicationContext());
        if (iIsGooglePlayServicesAvailable != 0 && iIsGooglePlayServicesAvailable != 2) {
            imageButton.setVisibility(8);
        } else {
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.6
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    int iIsGooglePlayServicesAvailable2 = GooglePlayServicesUtil.isGooglePlayServicesAvailable(EditEventView.this.getContext().getApplicationContext());
                    if (iIsGooglePlayServicesAvailable2 == 2) {
                        GooglePlayUpdateRequiredDialog.newInstance(iIsGooglePlayServicesAvailable2).show(EditEventView.this.activity.getSupportFragmentManager(), GooglePlayUpdateRequiredDialog.TAG);
                    } else if (iIsGooglePlayServicesAvailable2 != 0) {
                        view.setVisibility(8);
                    } else {
                        Utils.hideKeyboard(EditEventView.this.getContext(), EditEventView.this.activity.getCurrentFocus());
                        EditEventView.this.launchMapsActivity();
                    }
                }
            });
            imageButton.setVisibility(Utils.isDataTrafficEnabled(getContext()) ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchMapsActivity() {
        Intent intent = new Intent(this.activity, (Class<?>) MapsActivity.class);
        intent.putExtra(MapsActivity.ADDRESS_FIELD, this.mLocationTextView.getText().toString());
        this.activity.startActivityForResult(intent, 5);
    }

    private void initAllDayEventSwitch(boolean z) {
        Switch r0 = (Switch) findViewById(R.id.is_all_day);
        this.mAllDaySwitch = r0;
        r0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.7
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                if (z2) {
                    EditEventView.this.onAllDayEventChecked();
                } else {
                    EditEventView.this.onAllDayEventUnchecked();
                }
                compoundButton.playSoundEffect(0);
                EditEventView.this.updateWarningText();
                EditEventView.this.updateHomeTime();
                EditEventView.this.updateEditEventDayView();
            }
        });
        this.mAllDaySwitch.setChecked(z);
    }

    private void initTimezoneSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.timezone);
        this.mTimezoneSpinner = spinner;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.8
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i < 0 || i >= EditEventView.this.mTimezoneAdapter.getCount()) {
                    return;
                }
                EditEventView.this.setTimezone(i);
                EditEventView.this.updateHomeTime();
                EditEventView.this.updateEditEventDayView();
            }
        });
    }

    protected void initReminderControls() {
        mSelectedRemindersIndices.clear();
        Resources resources = getResources();
        String[] stringArray = resources.getStringArray(R.array.reminder_minutes_values);
        this.mReminderValues = new ArrayList<>(stringArray.length);
        for (String str : stringArray) {
            this.mReminderValues.add(Integer.valueOf(Integer.parseInt(str)));
        }
        this.mReminderLabels = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.reminder_minutes_labels)));
        try {
            this.mDefaultReminderMinutes = Integer.parseInt(GeneralPreferences.getSharedPreferences(getContext()).getString(GeneralPreferences.KEY_DEFAULT_REMINDER, "10"));
        } catch (NumberFormatException unused) {
            this.mDefaultReminderMinutes = 10;
        }
        this.mAddReminderView.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                RemindersDialogFragment.newInstance(EditEventView.mSelectedRemindersIndices, EditEventView.this.mReminderLabels).show(EditEventView.this.activity.getSupportFragmentManager(), EditEventView.REMINDERS_PICKER_TAG);
            }
        });
    }

    protected boolean hasRecipients() {
        return this.mAttendeesListRequired.getSelectedRecipients().size() > 0 || this.mAttendeesListOptional.getSelectedRecipients().size() > 0 || Patterns.EMAIL_ADDRESS.matcher(this.mAttendeesListRequired.getText()).matches() || Patterns.EMAIL_ADDRESS.matcher(this.mAttendeesListOptional.getText()).matches();
    }

    private void initAddRequiredAttendeesControls(boolean z, final Intent intent) {
        RecipientEditTextView recipientEditTextViewInitMultiAutoCompleteTextView = initMultiAutoCompleteTextView(R.id.attendees_required);
        this.mAttendeesListRequired = recipientEditTextViewInitMultiAutoCompleteTextView;
        recipientEditTextViewInitMultiAutoCompleteTextView.addTextChangedListener(this.mRecipientTextWatcher);
        ImageButton imageButton = (ImageButton) findViewById(R.id.to_plus_required);
        if (!z) {
            imageButton.setVisibility(8);
        } else {
            imageButton.setVisibility(0);
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.10
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    try {
                        EditEventView.this.activity.startActivityForResult(intent, 2);
                        EditEventView.this.mAttendeesListRequired.performValidation();
                    } catch (ActivityNotFoundException unused) {
                    }
                }
            });
        }
    }

    private void initAddOptionalAttendeesControls(boolean z, final Intent intent) {
        RecipientEditTextView recipientEditTextViewInitMultiAutoCompleteTextView = initMultiAutoCompleteTextView(R.id.attendees_optional);
        this.mAttendeesListOptional = recipientEditTextViewInitMultiAutoCompleteTextView;
        recipientEditTextViewInitMultiAutoCompleteTextView.addTextChangedListener(this.mRecipientTextWatcher);
        ImageButton imageButton = (ImageButton) findViewById(R.id.to_plus_optional);
        if (!z) {
            imageButton.setVisibility(8);
        } else {
            imageButton.setVisibility(0);
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.11
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    try {
                        EditEventView.this.activity.startActivityForResult(intent, 3);
                        EditEventView.this.mAttendeesListOptional.performValidation();
                    } catch (ActivityNotFoundException unused) {
                    }
                }
            });
        }
    }

    private void initCheckAvailabiliyButton() {
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EditEventView.this.startAvailability();
            }
        };
        Button button = (Button) findViewById(R.id.btn_availability);
        this.mCheckAvailability = button;
        button.setOnClickListener(onClickListener);
        if (Utils.isDataTrafficEnabled(getContext())) {
            return;
        }
        this.mCheckAvailability.setVisibility(8);
    }

    private void initRepetitionSpinner() {
        MultiSelectSpinner multiSelectSpinner = (MultiSelectSpinner) findViewById(R.id.repeats);
        this.mRepeatsSpinner = multiSelectSpinner;
        multiSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.13
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (view == null || ((Integer) EditEventView.this.mRecurrenceIndexes.get(i)).intValue() != 9 || !EditEventView.this.mRepeatsSpinner.isSpinnerInitialised || EditEventView.this.mIsLunarEvent) {
                    return;
                }
                EditEventView.this.setCustomizedRepetition();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAllDayEventUnchecked() {
        if (this.mStartTime.allDay) {
            String timeZone = Utils.getTimeZone(getContext(), null);
            this.mTimezone = timeZone;
            this.mStartTime.timezone = timeZone;
            this.mStartTime.allDay = false;
            Utils.updateAndRoundHour(this.mStartTime);
            long millis = this.mStartTime.toMillis(true);
            this.mEndTime.timezone = this.mTimezone;
            this.mEndTime.set(millis);
            this.mEndTime.hour++;
            long jNormalize = this.mEndTime.normalize(true);
            if (this.mIsLunarEvent) {
                setLunarDate(this.mStartDateButton, millis, this.mTimezone);
                setLunarDate(this.mEndDateButton, jNormalize, this.mTimezone);
            } else {
                setDate(this.mStartDateButton, millis);
                setDate(this.mEndDateButton, jNormalize);
            }
            setTime(this.mStartTimeButton, millis);
            setTime(this.mEndTimeButton, jNormalize);
            setTimezone(this.mTimezoneAdapter.getRowById(this.mTimezone));
        }
        this.mStartTimeButton.setVisibility(0);
        this.mEndTimeButton.setVisibility(0);
        this.mTimezoneSpinner.setVisibility(0);
        this.mTimezoneTextView.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAllDayEventChecked() {
        if (this.mStartTime.allDay) {
            this.mEndTime.hour--;
            long jNormalize = this.mEndTime.normalize(false);
            if (this.mIsLunarEvent) {
                setLunarDate(this.mEndDateButton, jNormalize, this.mTimezone);
            } else {
                setDate(this.mEndDateButton, jNormalize);
            }
            setTime(this.mEndTimeButton, jNormalize);
        }
        this.mStartTimeButton.setVisibility(8);
        this.mEndTimeButton.setVisibility(8);
        this.mTimezoneSpinner.setVisibility(8);
        this.mTimezoneTextView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAvailability() {
        Intent intent;
        boolean zIsChecked;
        String strTrim;
        if (getResources().getBoolean(R.bool.tablet_mode)) {
            intent = new Intent(this.activity, (Class<?>) TabletCheckAvailabilityActivity.class);
        } else {
            intent = new Intent(this.activity, (Class<?>) CheckAvailabilityActivity.class);
        }
        int i = 0;
        if (cursorNotEmpty(this.mEventCursor)) {
            zIsChecked = this.mEventCursor.getInt(4) != 0;
        } else {
            zIsChecked = this.mAllDaySwitch.isChecked();
        }
        intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ALL_DAY, zIsChecked);
        intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_START_TIME, this.mStartTime.toMillis(true));
        if (zIsChecked) {
            intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_TIME_ZONE, "UTC");
        } else {
            intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_TIME_ZONE, this.mStartTime.timezone);
        }
        int selectedItemPosition = this.mCalendarsSpinner.getSelectedItemPosition();
        Cursor cursor = this.mCalendarsCursor;
        if (cursor != null && cursor.moveToPosition(selectedItemPosition)) {
            strTrim = this.mCalendarsCursor.getString(2).trim();
        } else {
            Cursor cursor2 = this.mEventCursor;
            strTrim = cursor2 != null ? cursor2.getString(14).trim() : "";
        }
        if ("".equals(strTrim)) {
            return;
        }
        intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ACCOUNT_NAME, strTrim);
        LinkedHashSet<Rfc822Token> addressesFromList = getAddressesFromList(this.mAttendeesListRequired);
        String[] strArr = new String[addressesFromList.size() + 1];
        String[] strArr2 = new String[addressesFromList.size() + 1];
        strArr[0] = getResources().getString(R.string.organizer_availability_label);
        strArr2[0] = strTrim;
        int i2 = 1;
        for (Rfc822Token rfc822Token : addressesFromList) {
            strArr[i2] = rfc822Token.getName();
            strArr2[i2] = rfc822Token.getAddress();
            i2++;
        }
        LinkedHashSet<Rfc822Token> addressesFromList2 = getAddressesFromList(this.mAttendeesListOptional);
        String[] strArr3 = new String[addressesFromList2.size()];
        String[] strArr4 = new String[addressesFromList2.size()];
        CheckAvailabilityActivity.setLog("Optional attendees is below:", this.activity);
        for (Rfc822Token rfc822Token2 : addressesFromList2) {
            CheckAvailabilityActivity.setLog("Optional attendees is in~ ", this.activity);
            CheckAvailabilityActivity.setLog("Optional attendees name is " + rfc822Token2.getName(), this.activity);
            CheckAvailabilityActivity.setLog("Optional attendees email is " + rfc822Token2.getAddress(), this.activity);
            strArr3[i] = rfc822Token2.getName();
            strArr4[i] = rfc822Token2.getAddress();
            i++;
        }
        intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ATTENDEES_NAME, strArr);
        intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ATTENDEES_EMAIL, strArr2);
        if (addressesFromList2 != null && addressesFromList2.size() > 0) {
            intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ATTENDEES_OPTIONAL_NAME, strArr3);
            intent.putExtra(AbstractCheckAvailabilityActivity.EVENT_AVAILABILITY_ATTENDEES_OPTIONAL_EMAIL, strArr4);
        }
        this.activity.startActivityForResult(intent, 4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCustomizedRepetition() {
        Intent intent = new Intent(this.activity, (Class<?>) RepetitionActivity.class);
        if (this.mRrule != null) {
            if (isCustomRecurrence() && this.mIsCustomizedRepetitionFromDB) {
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_FREQ, this.mEventRecurrence.freq);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_INTERVAL, this.mEventRecurrence.interval);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_UNTIL, this.mEventRecurrence.until);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYDAY, this.mEventRecurrence.byday);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYDAYNUM, this.mEventRecurrence.bydayNum);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYDAYCOUNT, this.mEventRecurrence.bydayCount);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYMONTHDAY, this.mEventRecurrence.bymonthday);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYMONTH, this.mEventRecurrence.bymonth);
            } else {
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_FREQ, this.mRruleFreq);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_INTERVAL, this.mRruleInterval);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_UNTIL, this.mRruleUntil);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYDAY, this.mRruleByday);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYDAYNUM, this.mRruleBydayNum);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYDAYCOUNT, this.mRruleBydayCount);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYMONTHDAY, this.mRruleBymonthday);
                intent.putExtra(RepetitionActivity.REPEAT_RRULE_BYMONTH, this.mRruleBymonth);
            }
        }
        intent.putExtra(RepetitionActivity.REPEAT_RRULE_DTSTART, this.mStartTime.toMillis(false));
        this.activity.startActivityForResult(intent, 1);
    }

    private LinkedHashSet<Rfc822Token> getAddressesFromList(RecipientEditTextView recipientEditTextView) {
        recipientEditTextView.clearComposingText();
        LinkedHashSet<Rfc822Token> linkedHashSet = new LinkedHashSet<>();
        Rfc822Tokenizer.tokenize(recipientEditTextView.getText(), linkedHashSet);
        Iterator<Rfc822Token> it = linkedHashSet.iterator();
        while (it.hasNext()) {
            Rfc822Token next = it.next();
            if (!this.mEmailValidator.isValid(next.getAddress()) || addressIsDuplicate(next, linkedHashSet)) {
                Log.w(TAG, "Dropping invalid attendee email address: " + next);
                it.remove();
            }
        }
        return linkedHashSet;
    }

    private boolean addressIsDuplicate(Rfc822Token rfc822Token, LinkedHashSet<Rfc822Token> linkedHashSet) {
        Iterator<Rfc822Token> it = linkedHashSet.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().getAddress().equalsIgnoreCase(rfc822Token.getAddress())) {
                i++;
            }
        }
        return i > 1;
    }

    private RecipientEditTextView initMultiAutoCompleteTextView(int i) {
        RecipientEditTextView recipientEditTextView = (RecipientEditTextView) findViewById(i);
        if (Utils.isReadContactsEnabled(getContext())) {
            recipientEditTextView.setAdapter(new BaseRecipientAdapter(getContext()));
        }
        recipientEditTextView.setTokenizer(new Rfc822Tokenizer());
        recipientEditTextView.setValidator(this.mEmailValidator);
        recipientEditTextView.setFilters(sRecipientFilters);
        return recipientEditTextView;
    }

    private void initFromIntent(Intent intent) {
        String stringExtra = intent.getStringExtra("name");
        if (!TextUtils.isEmpty(stringExtra)) {
            this.mSelectedEmailAccount = stringExtra;
        }
        String stringExtra2 = intent.getStringExtra(LunarContract.EventsColumns.TITLE);
        if (stringExtra2 != null) {
            this.mTitleTextView.setText(stringExtra2);
        }
        String stringExtra3 = intent.getStringExtra(LunarContract.EventsColumns.EVENT_LOCATION);
        if (stringExtra3 != null) {
            this.mLocationTextView.setText(stringExtra3);
        }
        String stringExtra4 = intent.getStringExtra("description");
        if (stringExtra4 != null) {
            this.mDescriptionTextView.setText(stringExtra4);
        }
        int intExtra = intent.getIntExtra(LunarContract.EventsColumns.AVAILABILITY, -1);
        if (intExtra != -1) {
            this.mAvailabilitySpinner.setSelection(intExtra);
        }
        int intExtra2 = intent.getIntExtra(LunarContract.EventsColumns.ACCESS_LEVEL, -1);
        if (intExtra2 != -1) {
            if (intExtra2 > 0) {
                intExtra2--;
            }
            this.mVisibilitySpinner.setSelection(intExtra2);
        }
        String stringExtra5 = intent.getStringExtra("rrule");
        if (!TextUtils.isEmpty(stringExtra5)) {
            this.mRrule = stringExtra5;
            this.mEventRecurrence.parse(stringExtra5);
        }
        populateAttendeesList(intent.getStringExtra("android.intent.extra.EMAIL"), this.mAttendeesListRequired);
        populateAttendeesList(intent.getStringExtra("android.intent.extra.CC"), this.mAttendeesListOptional);
    }

    private void populateAttendeesList(String str, TextView textView) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        String[] strArrSplit = str.split("[ ,;]");
        StringBuilder sb = new StringBuilder();
        boolean zIsReadContactsEnabled = Utils.isReadContactsEnabled(getContext());
        for (String str2 : strArrSplit) {
            if (!TextUtils.isEmpty(str2) && str2.contains("@")) {
                String str3 = str2.trim() + RecurrenceRuleParser.VALUE_SEPARATOR;
                if (zIsReadContactsEnabled) {
                    textView.append(str3);
                } else {
                    sb.append(str3);
                }
            }
        }
        if (zIsReadContactsEnabled) {
            return;
        }
        textView.setText(sb);
    }

    private void isNewEventEpoch() {
        if (this.mStartTime.allDay) {
            this.mStartTime = Utils.convertDayToMidnight(this.mStartTime);
        }
        if (this.mEndTime.allDay) {
            this.mEndTime = Utils.convertDayToMidnight(this.mEndTime);
        }
        if (Time.isEpoch(this.mStartTime) && Time.isEpoch(this.mEndTime)) {
            this.mStartTime.setToNow();
            this.mStartTime.second = 0;
            int i = this.mStartTime.minute;
            if (i != 0) {
                if (i > 0 && i <= 30) {
                    this.mStartTime.minute = 30;
                } else {
                    this.mStartTime.minute = 0;
                    this.mStartTime.hour++;
                }
            }
            this.mEndTime.set(this.mStartTime.normalize(true) + 3600000);
        }
    }

    private void loadDataFromDatabase(Bundle bundle) {
        if (cursorNotEmpty(this.mEventCursor)) {
            Cursor cursor = this.mEventCursor;
            cursor.moveToFirst();
            this.mRrule = cursor.getString(10);
            String string = cursor.getString(1);
            String string2 = cursor.getString(2);
            String string3 = cursor.getString(3);
            int i = cursor.getInt(12);
            int i2 = cursor.getInt(13);
            if (i2 > 0) {
                i2--;
            }
            if (bundle != null) {
                this.mModification = bundle.getInt("mModification", 0);
            }
            if (!TextUtils.isEmpty(this.mRrule) && this.mModification == 0 && !this.mEditModeSelectionDialogShowed) {
                this.mSyncId = cursor.getString(11);
                this.mEventRecurrence.parse(this.mRrule);
            }
            this.mTitleTextView.setText(string);
            this.mLocationTextView.setText(string3);
            this.mDescriptionTextView.setText(string2);
            this.mAvailabilitySpinner.setSelection(i);
            this.mVisibilitySpinner.setSelection(i2);
        } else {
            isNewEventEpoch();
        }
        populateWhen();
        populateTimezone(this.mStartTime.normalize(true));
        updateHomeTime();
        if (this.mIsLunarEvent) {
            populateLunarRepeats();
        } else {
            populateRepeats();
        }
    }

    private boolean checkRule() {
        for (String str : this.mRrule.split(";")) {
            int iIndexOf = str.indexOf(61);
            if (iIndexOf <= 0 || str.substring(iIndexOf + 1).length() <= 0) {
                return false;
            }
        }
        return true;
    }

    protected void addReminder() {
        int i = this.mDefaultReminderMinutes;
        if (i != 0) {
            addReminder(i);
            updateRemindersLabel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void populateWhen() {
        long millis = this.mStartTime.toMillis(false);
        long millis2 = this.mEndTime.toMillis(false);
        if (this.mIsLunarEvent) {
            setLunarDate(this.mStartDateButton, millis, this.mTimezone);
            setLunarDate(this.mEndDateButton, millis2, this.mTimezone);
        } else {
            setDate(this.mStartDateButton, millis);
            setDate(this.mEndDateButton, millis2);
        }
        setTime(this.mStartTimeButton, millis);
        setTime(this.mEndTimeButton, millis2);
        initButtonListeners(2);
        if (LunarAvailabilityManager.isLunarAvailable(this.activity) && this.mIsLocalCalendarSelected) {
            initLuniSolarPicker();
        }
        this.mStartTimeButton.setOnClickListener(new TimeClickListener(this.mStartTime));
        this.mEndTimeButton.setOnClickListener(new TimeClickListener(this.mEndTime));
        updateWarningText();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void populateTimezone(long j) {
        TimezoneAdapter timezoneAdapter = new TimezoneAdapter(getContext(), this.mTimezone, j);
        this.mTimezoneAdapter = timezoneAdapter;
        timezoneAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mTimezoneSpinner.setAdapter((SpinnerAdapter) this.mTimezoneAdapter);
        setTimezone(this.mTimezoneAdapter.getRowById(this.mTimezone));
    }

    protected void updateHomeTime() {
        int i;
        int i2;
        String timeZone = Utils.getTimeZone(getContext(), null);
        TimeZone timeZone2 = TimeZone.getTimeZone(timeZone);
        TimeZone timeZone3 = TimeZone.getTimeZone(this.mTimezone);
        if (!this.mAllDaySwitch.isChecked() && !timeZone2.equals(timeZone3)) {
            boolean zIs24HourFormat = DateFormat.is24HourFormat(getContext());
            int i3 = zIs24HourFormat ? 129 : 1;
            long millis = this.mStartTime.toMillis(false);
            long millis2 = this.mEndTime.toMillis(false);
            String timezoneDisplayName = Utils.getTimezoneDisplayName(TimeZone.getTimeZone(timeZone));
            StringBuilder sb = new StringBuilder();
            mSB.setLength(0);
            sb.append(DateUtils.formatDateRange(getContext(), mF, millis, millis, i3, timeZone)).append(" ").append(timezoneDisplayName);
            this.mStartTimeHome.setText(sb.toString());
            mSB.setLength(0);
            if (this.mIsLunarEvent) {
                setLunarDate(this.mStartDateHome, millis, timeZone);
            } else {
                this.mStartDateHome.setText(DateUtils.formatDateRange(this.activity, mF, millis, millis, 524310, timeZone).toString());
            }
            if (zIs24HourFormat) {
                i = 0;
                i2 = 129;
            } else {
                i = 0;
                i2 = 1;
            }
            sb.setLength(i);
            mSB.setLength(i);
            sb.append(DateUtils.formatDateRange(getContext(), mF, millis2, millis2, i2, timeZone)).append(" ").append(timezoneDisplayName);
            this.mEndTimeHome.setText(sb.toString());
            mSB.setLength(0);
            if (this.mIsLunarEvent) {
                setLunarDate(this.mEndDateHome, millis2, timeZone);
            } else {
                this.mEndDateHome.setText(DateUtils.formatDateRange(this.activity, mF, millis2, millis2, 524310, timeZone).toString());
            }
            this.mStartTimeHome.setVisibility(0);
            this.mStartDateHome.setVisibility(0);
            this.mEndTimeHome.setVisibility(0);
            this.mEndDateHome.setVisibility(0);
            return;
        }
        this.mStartTimeHome.setVisibility(8);
        this.mStartDateHome.setVisibility(8);
        this.mEndTimeHome.setVisibility(8);
        this.mEndDateHome.setVisibility(8);
    }

    private boolean isPastTime(Time time, Time time2, boolean z) {
        if (z) {
            return Utils.compareToTodayMidnight(time2) <= 0;
        }
        Time time3 = new SafeTime(Utils.getTimeZone(getContext(), null));
        time3.setToNow();
        return time3.after(time);
    }

    public void updateWarningText() {
        boolean zIsChecked = this.mAllDaySwitch.isChecked();
        if (isPastTime(this.mStartTime, this.mEndTime, zIsChecked)) {
            this.mWarningTextView.setText(getResources().getString(R.string.edit_event_past_time));
            this.mWarningTextViewVisibility = 0;
            handleWarningTextViewVisibility();
        } else {
            if (!zIsChecked) {
                String[] strArr = {Long.toString(this.mEventId), Long.toString(this.mStartTime.toMillis(true)), Long.toString(this.mEndTime.toMillis(true))};
                this.mSolarConflictEventCount = 0;
                this.mConflictQueryHandler.startQuery(1, null, CalendarContract.Events.CONTENT_URI, EditEventHelper.CONFLICTING_EVENT_PROJECTION, "_id!=? AND allDay=0 AND dtend>? AND dtstart<? AND selfAttendeeStatus!=2 AND visible=1", strArr, null);
                if (LunarAvailabilityManager.isLunarAvailable(this.activity)) {
                    this.mLunarConflictEventCount = 0;
                    this.mConflictQueryHandler.startQuery(2, null, LunarContract.Events.CONTENT_URI, EditEventHelper.CONFLICTING_EVENT_PROJECTION, "_id!=? AND allDay=0 AND dtend>? AND dtstart<? AND selfAttendeeStatus!=2 AND visible=1", strArr, null);
                    return;
                }
                return;
            }
            this.mWarningTextView.setVisibility(8);
        }
    }

    protected void populateCalendars(int i) {
        CalendarsAdapter calendarsAdapter = new CalendarsAdapter(getContext(), this.mCalendarsCursor);
        this.mCalendarsSpinner.setAdapter((SpinnerAdapter) calendarsAdapter);
        getCalendarColorForButton();
        String str = this.mSelectedEmailAccount;
        if (str != null && this.mSelectedCalendarPosition == -1) {
            this.mSelectedCalendarPosition = findCalendarPositionByEmailAccount(calendarsAdapter, str);
        }
        int i2 = this.mSelectedCalendarPosition;
        if (i2 != -1) {
            this.mCalendarsSpinner.setSelection(i2);
            this.mCalendarPosition = this.mSelectedCalendarPosition;
            this.mSelectedCalendarPosition = -1;
            this.mSelectedEmailAccount = null;
        } else {
            int i3 = this.mCalendarPosition;
            if (i3 != -1) {
                this.mCalendarsSpinner.setSelection(i3);
            } else {
                this.mCalendarsSpinner.setSelection(i);
                this.mCalendarPosition = i;
            }
        }
        this.mCalendarsQueryComplete = true;
        if (this.mSaveAfterQueryComplete) {
            this.mLoadingCalendarsDialog.cancel();
            save();
            this.activity.finish();
        }
        if (Utils.isKeyboardAttached(getContext())) {
            if (this.mCalendarsSpinner.isEnabled()) {
                this.mCalendarsSpinner.requestFocus();
                this.mCalendarsSpinner.setNextFocusDownId(R.id.title);
            } else {
                this.mTitleTextView.requestFocus();
            }
        }
    }

    private int findCalendarPositionByEmailAccount(CalendarsAdapter calendarsAdapter, String str) {
        int i = this.mLocalCalendarPosition;
        int count = calendarsAdapter.getCount();
        for (int i2 = 0; i2 < count; i2++) {
            Cursor cursor = (Cursor) calendarsAdapter.getItem(i2);
            if (cursor != null && cursor.getString(1).equalsIgnoreCase(str)) {
                return i2;
            }
        }
        return i;
    }

    private void populateRepeats() {
        int i;
        Time time = this.mStartTime;
        Resources resources = getResources();
        String[] strArr = {DateUtils.getDayOfWeekString(1, 20), DateUtils.getDayOfWeekString(2, 20), DateUtils.getDayOfWeekString(3, 20), DateUtils.getDayOfWeekString(4, 20), DateUtils.getDayOfWeekString(5, 20), DateUtils.getDayOfWeekString(6, 20), DateUtils.getDayOfWeekString(7, 20)};
        String[] stringArray = resources.getStringArray(R.array.ordinal_labels);
        ArrayList<String> arrayList = this.repeatArray;
        if (arrayList != null) {
            arrayList.clear();
        }
        this.repeatArray = new ArrayList<>(0);
        ArrayList<Integer> arrayList2 = this.recurrenceIndexes;
        if (arrayList2 != null) {
            arrayList2.clear();
        }
        this.recurrenceIndexes = new ArrayList<>(0);
        this.repeatArray.add(resources.getString(R.string.does_not_repeat));
        this.recurrenceIndexes.add(0);
        this.repeatArray.add(resources.getString(R.string.daily));
        this.recurrenceIndexes.add(1);
        Calendar calendar = Calendar.getInstance();
        String[] weekendIndices = getWeekendIndices();
        if (!isWeekendEvent(weekendIndices)) {
            int[] dayIndices = WeekUtils.getDayIndices(getContext());
            calendar.set(7, dayIndices[0] + 1);
            i = 1;
            String displayName = calendar.getDisplayName(7, 1, Locale.getDefault());
            calendar.set(7, dayIndices[dayIndices.length - 1] + 1);
            this.repeatArray.add(String.format(resources.getString(R.string.every_weekday), displayName, calendar.getDisplayName(7, 1, Locale.getDefault())));
            this.recurrenceIndexes.add(2);
        } else {
            i = 1;
            calendar.set(7, Integer.parseInt(weekendIndices[0]) + 1);
            String displayName2 = calendar.getDisplayName(7, 1, Locale.getDefault());
            calendar.set(7, Integer.parseInt(weekendIndices[weekendIndices.length - 1]) + 1);
            this.repeatArray.add(String.format(resources.getString(R.string.clr_strings_every_weekend_txt), displayName2, calendar.getDisplayName(7, 1, Locale.getDefault())));
            this.recurrenceIndexes.add(3);
        }
        String string = resources.getString(R.string.weekly);
        ArrayList<String> arrayList3 = this.repeatArray;
        Object[] objArr = new Object[i];
        objArr[0] = time.format("%a");
        arrayList3.add(String.format(string, objArr));
        this.recurrenceIndexes.add(4);
        String string2 = resources.getString(R.string.biweekly);
        ArrayList<String> arrayList4 = this.repeatArray;
        Object[] objArr2 = new Object[i];
        objArr2[0] = time.format("%a");
        arrayList4.add(String.format(string2, objArr2));
        this.recurrenceIndexes.add(5);
        int i2 = (time.monthDay - i) / 7;
        String string3 = resources.getString(R.string.monthly_on_day_count);
        ArrayList<String> arrayList5 = this.repeatArray;
        Object[] objArr3 = new Object[2];
        objArr3[0] = stringArray[i2];
        objArr3[i] = strArr[time.weekDay];
        arrayList5.add(String.format(string3, objArr3));
        this.recurrenceIndexes.add(6);
        String string4 = resources.getString(R.string.monthly_on_day);
        ArrayList<String> arrayList6 = this.repeatArray;
        Object[] objArr4 = new Object[i];
        objArr4[0] = Integer.valueOf(time.monthDay);
        arrayList6.add(String.format(string4, objArr4));
        this.recurrenceIndexes.add(7);
        this.repeatArray.add(String.format(resources.getString(R.string.yearly), DateUtils.formatDateTime(getContext(), time.toMillis(false), DateFormat.is24HourFormat(getContext()) ? 128 : 0)));
        this.recurrenceIndexes.add(8);
        this.repeatArray.add(resources.getString(R.string.clr_strings_customized_txt));
        this.recurrenceIndexes.add(9);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.simple_spinner_item, this.repeatArray);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mRepeatsSpinner.setAdapter((SpinnerAdapter) arrayAdapter);
        ArrayList<Integer> arrayList7 = this.recurrenceIndexes;
        this.mRecurrenceIndexes = arrayList7;
        int iIndexOf = arrayList7.indexOf(0);
        if (this.mRrule != null) {
            if (isCustomRecurrence()) {
                iIndexOf = this.recurrenceIndexes.indexOf(9);
            } else {
                int i3 = this.mEventRecurrence.freq;
                if (i3 == 4) {
                    iIndexOf = this.recurrenceIndexes.indexOf(1);
                } else if (i3 != 5) {
                    if (i3 != 6) {
                        if (i3 == 7) {
                            iIndexOf = this.recurrenceIndexes.indexOf(8);
                        } else {
                            iIndexOf = this.recurrenceIndexes.indexOf(9);
                        }
                    } else if (this.mEventRecurrence.repeatsMonthlyOnDayCount()) {
                        iIndexOf = this.recurrenceIndexes.indexOf(6);
                    } else {
                        iIndexOf = this.recurrenceIndexes.indexOf(7);
                    }
                } else if (this.mEventRecurrence.repeatsOnEveryWeekDay(WeekUtils.getDayIndices(getContext()))) {
                    iIndexOf = this.recurrenceIndexes.indexOf(2);
                } else if (this.mEventRecurrence.repeatsOnEveryWeekEnd(weekendIndices)) {
                    iIndexOf = this.recurrenceIndexes.indexOf(3);
                } else if (!this.mEventRecurrence.repeatsBiweekly()) {
                    iIndexOf = this.recurrenceIndexes.indexOf(4);
                } else {
                    iIndexOf = this.recurrenceIndexes.indexOf(5);
                }
            }
        }
        this.mRepeatsSpinner.isSpinnerInitialised = false;
        this.mRepeatsSpinner.setSelectionFirstTime(iIndexOf);
        this.mRepeatsPosition = iIndexOf;
    }

    private void populateAvailability() {
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource = ArrayAdapter.createFromResource(getContext(), R.array.availability, R.layout.simple_spinner_item);
        arrayAdapterCreateFromResource.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mAvailabilitySpinner.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource);
    }

    private void populateVisibility() {
        ArrayAdapter<CharSequence> arrayAdapterCreateFromResource = ArrayAdapter.createFromResource(getContext(), R.array.visibility, R.layout.simple_spinner_item);
        arrayAdapterCreateFromResource.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mVisibilitySpinner.setAdapter((SpinnerAdapter) arrayAdapterCreateFromResource);
    }

    private static int findMinutesInReminderList(ArrayList<Integer> arrayList, int i) {
        int iIndexOf = arrayList.indexOf(Integer.valueOf(i));
        if (iIndexOf != -1) {
            return iIndexOf;
        }
        Log.e("Cal", "Cannot find minutes (" + i + ") in list");
        return 0;
    }

    protected void setDate(TextView textView, long j) {
        mSB.setLength(0);
        textView.setText(DateUtils.formatDateRange(getContext(), mF, j, j, 524310, this.mTimezone).toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTime(TextView textView, long j) {
        int i = DateFormat.is24HourFormat(getContext()) ? 129 : 1;
        mSB.setLength(0);
        String string = DateUtils.formatDateRange(getContext(), mF, j, j, i, this.mTimezone).toString();
        if (Locale.getDefault().getLanguage().equals("bn")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm");
            Calendar calendar = Calendar.getInstance();
            int iIndexOf = string.indexOf(CalendarConstants.COLON);
            if (iIndexOf > 0) {
                int i2 = Integer.parseInt(string.substring(0, iIndexOf));
                int i3 = Integer.parseInt(string.substring(iIndexOf + 1, iIndexOf + 3));
                calendar.set(10, i2);
                calendar.set(12, i3);
                if (!DateFormat.is24HourFormat(getContext())) {
                    string = simpleDateFormat.format(calendar.getTime()) + string.substring(string.length() - 2, string.length());
                } else {
                    string = simpleDateFormat.format(calendar.getTime());
                }
            }
        }
        textView.setText(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimezone(int i) {
        if (i < 0 || i > this.mTimezoneAdapter.getCount()) {
            return;
        }
        TimezoneAdapter.TimezoneRow item = this.mTimezoneAdapter.getItem(i);
        this.mTimezoneSpinner.setSelection(i);
        String str = item.mId;
        this.mTimezone = str;
        this.mTimezoneAdapter.setCurrentTimezone(str);
        this.mStartTime.timezone = this.mTimezone;
        this.mStartTime.normalize(true);
        this.mEndTime.timezone = this.mTimezone;
        this.mEndTime.normalize(true);
    }

    protected Uri asSyncAdapter(Uri uri, String str, String str2) {
        return uri.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", str).appendQueryParameter("account_type", str2).build();
    }

    protected void addUid(ContentValues contentValues) {
        Cursor cursorQuery = getContext().getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, EditEventHelper.CALENDARS_PROJECTION, "calendar_access_level>=500 AND sync_events=1 AND _id=" + contentValues.getAsLong(LunarContract.EventsColumns.CALENDAR_ID).longValue(), null, null);
        if (cursorQuery != null) {
            try {
                if (cursorQuery.getCount() != 0) {
                    cursorQuery.moveToFirst();
                    String string = cursorQuery.getString(4);
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    if (this.mHasAttendeeData && Utils.isExchangeAccountType(string)) {
                        contentValues.put("sync_data2", UUID.randomUUID().toString());
                        return;
                    }
                    return;
                }
            } catch (Throwable th) {
                if (cursorQuery != null) {
                    try {
                        cursorQuery.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (cursorQuery != null) {
            cursorQuery.close();
        }
    }

    private void checkTimeDependentFields(ContentValues contentValues) {
        boolean z;
        long jLongValue = this.mInitialValues.getAsLong(LunarContract.EXTRA_EVENT_BEGIN_TIME).longValue();
        long jLongValue2 = this.mInitialValues.getAsLong(LunarContract.EXTRA_EVENT_END_TIME).longValue();
        Integer asInteger = this.mInitialValues.getAsInteger("allDay");
        boolean z2 = (asInteger == null || asInteger.intValue() == 0) ? false : true;
        String asString = this.mInitialValues.getAsString("rrule");
        String asString2 = this.mInitialValues.getAsString(LunarContract.EventsColumns.EVENT_TIMEZONE);
        long jLongValue3 = contentValues.getAsLong(LunarContract.EventsColumns.DTSTART).longValue();
        long jLongValue4 = contentValues.getAsLong(LunarContract.EventsColumns.DTEND).longValue();
        Integer asInteger2 = contentValues.getAsInteger("allDay");
        if (asInteger2 != null) {
            z = asInteger2.intValue() != 0;
        } else {
            z = false;
        }
        String asString3 = contentValues.getAsString("rrule");
        String asString4 = contentValues.getAsString(LunarContract.EventsColumns.EVENT_TIMEZONE);
        if (jLongValue == jLongValue3 && jLongValue2 == jLongValue4 && z2 == z && TextUtils.equals(asString, asString3) && TextUtils.equals(asString2, asString4)) {
            contentValues.remove(LunarContract.EventsColumns.DTSTART);
            contentValues.remove(LunarContract.EventsColumns.DTEND);
            contentValues.remove(LunarContract.EventsColumns.DURATION);
            contentValues.remove("allDay");
            contentValues.remove("rrule");
            contentValues.remove(LunarContract.EventsColumns.EVENT_TIMEZONE);
            return;
        }
        if (asString == null || asString3 == null) {
            return;
        }
        if (this.mModification == 2) {
            this.mEventCursor.moveToFirst();
            long jFindNewStartTimeInChangedRule = this.mEventCursor.getLong(7);
            if (jLongValue != jLongValue3) {
                jFindNewStartTimeInChangedRule += jLongValue3 - jLongValue;
            }
            if (!TextUtils.equals(asString, asString3)) {
                jFindNewStartTimeInChangedRule = EventRecurrenceParser.findNewStartTimeInChangedRule(asString3, jFindNewStartTimeInChangedRule, jLongValue3);
            }
            contentValues.put(LunarContract.EventsColumns.DTSTART, Long.valueOf(jFindNewStartTimeInChangedRule));
        }
    }

    private static ArrayList<Integer> reminderItemsToMinutes(ArrayList<Integer> arrayList) {
        ArrayList<Integer> arrayList2 = new ArrayList<>(mSelectedRemindersIndices.size());
        Iterator<Integer> it = mSelectedRemindersIndices.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            if (arrayList.contains(arrayList.get(iIntValue))) {
                arrayList2.add(arrayList.get(iIntValue));
            }
        }
        return arrayList2;
    }

    private static void saveReminders(ArrayList<ContentProviderOperation> arrayList, long j, ArrayList<Integer> arrayList2, ArrayList<Integer> arrayList3, boolean z, boolean z2) {
        Uri uri;
        if (!arrayList2.equals(arrayList3) || z) {
            if (z2) {
                uri = LunarContract.Reminders.CONTENT_URI;
            } else {
                uri = CalendarContract.Reminders.CONTENT_URI;
            }
            String[] strArr = {Long.toString(j)};
            ContentProviderOperation.Builder builderNewDelete = ContentProviderOperation.newDelete(uri);
            builderNewDelete.withSelection("event_id=?", strArr);
            arrayList.add(builderNewDelete.build());
            ContentValues contentValues = new ContentValues();
            int size = arrayList2.size();
            for (int i = 0; i < size; i++) {
                int iIntValue = arrayList2.get(i).intValue();
                contentValues.clear();
                contentValues.put("minutes", Integer.valueOf(iIntValue));
                contentValues.put("method", (Integer) 1);
                contentValues.put("event_id", Long.valueOf(j));
                arrayList.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
            }
        }
    }

    static void saveRemindersWithBackRef(ArrayList<ContentProviderOperation> arrayList, int i, ArrayList<Integer> arrayList2, ArrayList<Integer> arrayList3, boolean z, boolean z2, boolean z3) {
        Uri uri;
        if (!arrayList2.equals(arrayList3) || z) {
            if (z2) {
                uri = LunarContract.Reminders.CONTENT_URI;
            } else {
                uri = CalendarContract.Reminders.CONTENT_URI;
            }
            ContentProviderOperation.Builder builderNewDelete = ContentProviderOperation.newDelete(uri);
            builderNewDelete.withSelection("event_id=?", new String[1]);
            builderNewDelete.withSelectionBackReference(0, i);
            arrayList.add(builderNewDelete.build());
            ContentValues contentValues = new ContentValues();
            int size = arrayList2.size();
            if (z3 && size == 0) {
                contentValues.clear();
                contentValues.put("minutes", (Integer) 0);
                contentValues.put("method", (Integer) 1);
                ContentProviderOperation.Builder builderWithValues = ContentProviderOperation.newInsert(uri).withValues(contentValues);
                builderWithValues.withValueBackReference("event_id", i);
                arrayList.add(builderWithValues.build());
                return;
            }
            for (int i2 = 0; i2 < size; i2++) {
                int iIntValue = arrayList2.get(i2).intValue();
                contentValues.clear();
                contentValues.put("minutes", Integer.valueOf(iIntValue));
                contentValues.put("method", (Integer) 1);
                ContentProviderOperation.Builder builderWithValues2 = ContentProviderOperation.newInsert(uri).withValues(contentValues);
                builderWithValues2.withValueBackReference("event_id", i);
                arrayList.add(builderWithValues2.build());
            }
        }
    }

    private void setTimeRangeData(Intent intent) {
        if (intent.getBooleanExtra(TabletCheckAvailabilityActivity.AVAILABILITY_NEED_CHANGED_TIME_DATE, false)) {
            long longExtra = intent.getLongExtra(TabletCheckAvailabilityActivity.AVAILABILITY_CHANGED_TIME_DATE, 0L);
            if (longExtra != 0) {
                updateStartAndEndDate(longExtra);
            }
        }
        if (intent.getBooleanExtra(AbstractCheckAvailabilityActivity.AVAILABILITY_NEED_CHANGED_HOUR, false)) {
            this.mStartTime.hour = intent.getIntExtra(AbstractCheckAvailabilityActivity.AVAILABILITY_CHANGED_TIME_HOUR, 0);
            this.mStartTime.minute = 0;
            this.mStartTime.second = 0;
            this.mEndTime.hour = this.mStartTime.hour + 1;
            this.mEndTime.minute = 0;
            this.mEndTime.second = 0;
            setTime(this.mStartTimeButton, this.mStartTime.toMillis(true));
            setTime(this.mEndTimeButton, this.mEndTime.toMillis(true));
        }
    }

    private void updateStartAndEndDate(long j) {
        int i = this.mStartTime.hour;
        int i2 = this.mStartTime.minute;
        int i3 = this.mStartTime.second;
        int i4 = this.mEndTime.hour;
        int i5 = this.mEndTime.minute;
        int i6 = this.mEndTime.second;
        this.mStartTime.set(j);
        this.mEndTime.set(j);
        this.mStartTime.hour = i;
        this.mStartTime.minute = i2;
        this.mStartTime.second = i3;
        this.mEndTime.hour = i4;
        this.mEndTime.minute = i5;
        this.mEndTime.second = i6;
        setDate(this.mStartDateButton, j);
        setDate(this.mEndDateButton, j);
    }

    private void setLocationData(Intent intent) {
        this.mLocationTextView.setText(intent.getStringExtra(MapsActivity.ADDRESS_FIELD));
    }

    private void setRepetitionData(Intent intent) {
        this.mIsCustomizedRepetitionFromDB = false;
        this.mRruleFreq = intent.getIntExtra(RepetitionActivity.REPEAT_RRULE_FREQ, 0);
        this.mRruleInterval = intent.getIntExtra(RepetitionActivity.REPEAT_RRULE_INTERVAL, 1);
        this.mRruleUntil = intent.getStringExtra(RepetitionActivity.REPEAT_RRULE_UNTIL);
        this.mRruleByday = intent.getIntArrayExtra(RepetitionActivity.REPEAT_RRULE_BYDAY);
        this.mRruleBydayNum = intent.getIntArrayExtra(RepetitionActivity.REPEAT_RRULE_BYDAYNUM);
        this.mRruleBydayCount = intent.getIntExtra(RepetitionActivity.REPEAT_RRULE_BYDAYCOUNT, 0);
        this.mRruleBymonthday = intent.getIntArrayExtra(RepetitionActivity.REPEAT_RRULE_BYMONTHDAY);
        this.mRruleBymonth = intent.getIntArrayExtra(RepetitionActivity.REPEAT_RRULE_BYMONTH);
        this.mRruleDtstart = intent.getLongExtra(RepetitionActivity.REPEAT_RRULE_DTSTART, new SafeTime().normalize(true));
        this.mIsMonthDay = intent.getBooleanExtra(RepetitionActivity.REPEAT_RRULE_ISMONTHDAY, true);
        this.mIsYearDay = intent.getBooleanExtra(RepetitionActivity.REPEAT_RRULE_ISYEARDAY, true);
        Calendar calendar = Calendar.getInstance();
        if (this.mRruleDtstart > 0) {
            int i = this.mEndTime.monthDay - this.mStartTime.monthDay;
            int i2 = this.mEndTime.month - this.mStartTime.month;
            int i3 = this.mEndTime.year - this.mStartTime.year;
            calendar.setTimeInMillis(this.mRruleDtstart);
            this.mStartTime.monthDay = calendar.get(5);
            this.mStartTime.month = calendar.get(2);
            this.mStartTime.year = calendar.get(1);
            this.mEndTime.monthDay = calendar.get(5);
            this.mEndTime.month = calendar.get(2);
            this.mEndTime.year = calendar.get(1);
            this.mEndTime.monthDay += i;
            this.mEndTime.month += i2;
            this.mEndTime.year += i3;
            setDate(this.mStartDateButton, this.mRruleDtstart);
            setDate(this.mEndDateButton, this.mEndTime.normalize(true));
            updateWarningText();
        }
        String str = this.mRruleUntil;
        if (str != null && !"".equals(str)) {
            calendar.set(Integer.parseInt(this.mRruleUntil.substring(0, 4)), Integer.parseInt(this.mRruleUntil.substring(4, 6)) - 1, Integer.parseInt(this.mRruleUntil.substring(6, 8)));
        }
        this.mRrule = intent.getStringExtra("mRrule");
    }

    private void setContactsData(int i, Intent intent) {
        RecipientEditTextView recipientEditTextView;
        if (Utils.isReadContactsEnabled(getContext())) {
            if (i == 2) {
                recipientEditTextView = this.mAttendeesListRequired;
            } else {
                recipientEditTextView = i == 3 ? this.mAttendeesListOptional : null;
            }
            if (recipientEditTextView != null) {
                new PickContactTask(intent, recipientEditTextView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
    }

    protected void addRecurrenceRule(ContentValues contentValues) {
        if (this.mIsLunarEvent) {
            updateLunarRecurrenceRule();
        } else {
            updateRecurrenceRule();
        }
        String str = this.mRrule;
        if (str == null) {
            return;
        }
        contentValues.put("rrule", str);
        contentValues.put(LunarContract.EventsColumns.DURATION, getEventDuration(this.mStartTime.toMillis(true), this.mEndTime.toMillis(true), this.mAllDaySwitch.isChecked()));
    }

    public static String getEventDuration(long j, long j2, boolean z) {
        if (z) {
            return "P" + ((((j2 - j) + 86400000) - 1) / 86400000) + RecurrenceRuleParser.REPEAT_DAILY;
        }
        return "P" + ((j2 - j) / 1000) + CalendarConstants.SECONDS;
    }

    private void clearRecurrence() {
        this.mEventRecurrence.byday = null;
        this.mEventRecurrence.bydayNum = null;
        this.mEventRecurrence.bydayCount = 0;
        this.mEventRecurrence.bymonth = null;
        this.mEventRecurrence.bymonthCount = 0;
        this.mEventRecurrence.bymonthday = null;
        this.mEventRecurrence.bymonthdayCount = 0;
        this.mEventRecurrence.bysetpos = null;
        this.mEventRecurrence.bysetposCount = 0;
        this.mEventRecurrence.interval = 1;
    }

    private void updateRecurrenceRule() {
        int iIntValue = this.mRecurrenceIndexes.get(this.mRepeatsSpinner.getSelectedItemPosition()).intValue();
        if (iIntValue != 9 || !this.mIsCustomizedRepetitionFromDB) {
            clearRecurrence();
        }
        if (iIntValue == 0) {
            this.mRrule = null;
            return;
        }
        if (iIntValue == 1) {
            this.mEventRecurrence.freq = 4;
            this.mEventRecurrence.bymonthdayCount = 0;
            this.mEventRecurrence.bydayCount = 0;
        } else if (iIntValue == 2) {
            this.mEventRecurrence.freq = 5;
            int[] weekDays = getWeekDays();
            if (weekDays.length > 0) {
                int[] iArr = new int[weekDays.length];
                for (int i = 0; i < weekDays.length; i++) {
                    iArr[i] = 0;
                }
                this.mEventRecurrence.byday = weekDays;
                this.mEventRecurrence.bydayNum = iArr;
                this.mEventRecurrence.bydayCount = weekDays.length;
            } else {
                this.mEventRecurrence = null;
            }
        } else if (iIntValue == 3) {
            String[] weekendIndices = getWeekendIndices();
            Arrays.sort(weekendIndices, new Comparator<String>() { // from class: com.sonymobile.calendar.editevent.EditEventView.14
                @Override // java.util.Comparator
                public int compare(String str, String str2) {
                    return Integer.parseInt(str) - Integer.parseInt(str2);
                }
            });
            this.mEventRecurrence.freq = 5;
            int length = weekendIndices.length;
            int[] iArr2 = new int[length];
            int[] iArr3 = new int[length];
            for (int i2 = 0; i2 < length; i2++) {
                iArr2[i2] = EventRecurrence.timeDay2Day(Integer.parseInt(weekendIndices[i2]));
                iArr3[i2] = 0;
            }
            this.mEventRecurrence.byday = iArr2;
            this.mEventRecurrence.bydayNum = iArr3;
            this.mEventRecurrence.bydayCount = length;
        } else if (iIntValue == 4) {
            this.mEventRecurrence.freq = 5;
            this.mEventRecurrence.byday = new int[]{EventRecurrence.timeDay2Day(this.mStartTime.weekDay)};
            this.mEventRecurrence.bydayNum = new int[]{0};
            this.mEventRecurrence.bydayCount = 1;
        } else if (iIntValue == 5) {
            this.mEventRecurrence.freq = 5;
            this.mEventRecurrence.interval = 2;
            this.mEventRecurrence.byday = new int[]{EventRecurrence.timeDay2Day(this.mStartTime.weekDay)};
            this.mEventRecurrence.bydayNum = new int[]{0};
            this.mEventRecurrence.bydayCount = 1;
        } else if (iIntValue == 7) {
            this.mEventRecurrence.freq = 6;
            this.mEventRecurrence.bydayCount = 0;
            this.mEventRecurrence.bymonthdayCount = 1;
            this.mEventRecurrence.bymonthday = new int[]{this.mStartTime.monthDay};
        } else if (iIntValue == 6) {
            this.mEventRecurrence.freq = 6;
            this.mEventRecurrence.bydayCount = 1;
            this.mEventRecurrence.bymonthdayCount = 0;
            int[] iArr4 = {getWeekCount(this.mStartTime)};
            this.mEventRecurrence.byday = new int[]{EventRecurrence.timeDay2Day(this.mStartTime.weekDay)};
            this.mEventRecurrence.bydayNum = iArr4;
        } else if (iIntValue == 8) {
            this.mEventRecurrence.freq = 7;
        } else if (iIntValue == 9 && !this.mIsCustomizedRepetitionFromDB) {
            this.mEventRecurrence.freq = this.mRruleFreq;
            this.mEventRecurrence.interval = this.mRruleInterval;
            String str = this.mRruleUntil;
            if (str != null && !"".equals(str)) {
                Time time = new SafeTime();
                time.set(59, 59, 23, Integer.parseInt(this.mRruleUntil.substring(6, 8)), Integer.parseInt(this.mRruleUntil.substring(4, 6)) - 1, Integer.parseInt(this.mRruleUntil.substring(0, 4)));
                time.switchTimezone("UTC");
                time.normalize(false);
                this.mEventRecurrence.until = time.format2445();
            }
            int i3 = this.mEventRecurrence.freq;
            if (i3 == 5) {
                int[] iArr5 = this.mRruleByday;
                if (iArr5 != null) {
                    this.mEventRecurrence.byday = iArr5;
                }
                int[] iArr6 = this.mRruleBydayNum;
                if (iArr6 != null) {
                    this.mEventRecurrence.bydayNum = iArr6;
                }
                int i4 = this.mRruleBydayCount;
                if (i4 != 0) {
                    this.mEventRecurrence.bydayCount = i4;
                }
            } else if (i3 != 6) {
                if (i3 == 7) {
                    if (this.mIsYearDay) {
                        this.mEventRecurrence.bymonthCount = 1;
                        this.mEventRecurrence.bymonth = this.mRruleBymonth;
                        this.mEventRecurrence.bymonthdayCount = 1;
                        this.mEventRecurrence.bymonthday = this.mRruleBymonthday;
                    } else {
                        this.mEventRecurrence.bydayCount = 1;
                        this.mEventRecurrence.bymonthCount = 1;
                        this.mEventRecurrence.bymonth = this.mRruleBymonth;
                        this.mEventRecurrence.byday = this.mRruleByday;
                        this.mEventRecurrence.bydayNum = this.mRruleBydayNum;
                    }
                }
            } else if (this.mIsMonthDay) {
                this.mEventRecurrence.bydayCount = 0;
                this.mEventRecurrence.bymonthdayCount = 1;
                this.mEventRecurrence.bymonthday = this.mRruleBymonthday;
            } else {
                this.mEventRecurrence.bydayCount = 1;
                this.mEventRecurrence.bymonthdayCount = 0;
                this.mEventRecurrence.byday = this.mRruleByday;
                this.mEventRecurrence.bydayNum = this.mRruleBydayNum;
            }
        }
        EventRecurrence eventRecurrence = this.mEventRecurrence;
        if (eventRecurrence != null) {
            eventRecurrence.wkst = EventRecurrence.calendarDay2Day(this.mFirstDayOfWeek);
            this.mRrule = this.mEventRecurrence.toString();
        }
    }

    private static int getWeekCount(Time time) {
        int i = ((time.monthDay - 1) / 7) + 1;
        if (i == 5) {
            return -1;
        }
        return i;
    }

    private int[] getWeekDays() {
        String[] weekendIndices = getWeekendIndices();
        int[] dayIndices = WeekUtils.getDayIndices(getContext());
        if (weekendIndices.length >= 7) {
            return new int[0];
        }
        for (int i = 0; i < dayIndices.length; i++) {
            dayIndices[i] = EventRecurrence.timeDay2Day(dayIndices[i]);
        }
        return dayIndices;
    }

    private String[] getWeekendIndices() {
        return Utils.getSharedPreference(getContext(), GeneralPreferences.KEY_WEEKEND_DAYS, getContext().getResources().getStringArray(R.array.preferences_weekend_default));
    }

    private ContentValues getContentValuesFromUi() {
        long millis;
        long millis2;
        String strTrim = this.mTitleTextView.getText().toString().trim();
        boolean zIsChecked = this.mAllDaySwitch.isChecked();
        String strTrim2 = this.mLocationTextView.getText().toString().trim();
        String strTrim3 = this.mDescriptionTextView.getText().toString().trim();
        String string = this.mCalendarsCursor.getString(4);
        ContentValues contentValues = new ContentValues();
        long selectedItemId = 1;
        if (zIsChecked) {
            this.mTimezone = "UTC";
            this.mStartTime.hour = 0;
            this.mStartTime.minute = 0;
            this.mStartTime.second = 0;
            this.mStartTime.allDay = true;
            this.mStartTime.timezone = this.mTimezone;
            millis = this.mStartTime.normalize(true);
            this.mEndTime.hour = 0;
            this.mEndTime.minute = 0;
            this.mEndTime.second = 0;
            this.mEndTime.timezone = this.mTimezone;
            this.mEndTime.monthDay++;
            millis2 = this.mEndTime.normalize(true);
            if (this.mEventCursor == null) {
                if (!this.mIsLunarEvent) {
                    selectedItemId = this.mCalendarsSpinner.getSelectedItemId();
                }
            } else {
                selectedItemId = this.mInitialValues.getAsLong(LunarContract.EventsColumns.CALENDAR_ID).longValue();
            }
        } else {
            if (cursorNotEmpty(this.mEventCursor)) {
                selectedItemId = this.mInitialValues.getAsLong(LunarContract.EventsColumns.CALENDAR_ID).longValue();
            } else if (!this.mIsLunarEvent) {
                selectedItemId = this.mCalendarsSpinner.getSelectedItemId();
            }
            this.mStartTime.timezone = this.mTimezone;
            this.mEndTime.timezone = this.mTimezone;
            millis = this.mStartTime.toMillis(true);
            millis2 = this.mEndTime.toMillis(true);
        }
        contentValues.put(LunarContract.EventsColumns.CALENDAR_ID, Long.valueOf(selectedItemId));
        contentValues.put(LunarContract.EventsColumns.EVENT_TIMEZONE, this.mTimezone);
        contentValues.put(LunarContract.EventsColumns.TITLE, strTrim);
        contentValues.put(LunarContract.EventsColumns.EVENT_COLOR, Integer.valueOf(this.eventColor));
        int colorKey = CalendarInstanceService.getInstance().getColorKey(this.eventColor);
        if (colorKey != -1 && Utils.isGoogleAccountType(string)) {
            contentValues.put(LunarContract.EventsColumns.EVENT_COLOR_KEY, Integer.valueOf(colorKey));
        }
        contentValues.put("allDay", Integer.valueOf(zIsChecked ? 1 : 0));
        contentValues.put(LunarContract.EventsColumns.DTSTART, Long.valueOf(millis));
        contentValues.put(LunarContract.EventsColumns.DTEND, Long.valueOf(millis2));
        contentValues.put("description", strTrim3);
        contentValues.put(LunarContract.EventsColumns.EVENT_LOCATION, strTrim2);
        contentValues.put(LunarContract.EventsColumns.AVAILABILITY, Integer.valueOf(this.mAvailabilitySpinner.getSelectedItemPosition()));
        int selectedItemPosition = this.mVisibilitySpinner.getSelectedItemPosition();
        if (selectedItemPosition > 0) {
            selectedItemPosition++;
        }
        contentValues.put(LunarContract.EventsColumns.ACCESS_LEVEL, Integer.valueOf(selectedItemPosition));
        return contentValues;
    }

    private boolean isRequiredInfoEmpty() {
        return this.mTitleTextView.getText().toString().trim().length() <= 0 && this.mLocationTextView.getText().toString().trim().length() <= 0 && this.mDescriptionTextView.getText().toString().trim().length() <= 0;
    }

    private boolean isCustomRecurrence() {
        int i;
        if (this.mEventRecurrence.until != null || this.mEventRecurrence.count != 0 || (this.mEventRecurrence.interval > 1 && !this.mEventRecurrence.repeatsBiweekly())) {
            return true;
        }
        int weekCount = getWeekCount(this.mStartTime);
        if (this.mEventRecurrence.repeatsMonthlyOnDayCount() && (weekCount != this.mEventRecurrence.bydayNum[0] || EventRecurrence.timeDay2Day(this.mStartTime.weekDay) != this.mEventRecurrence.byday[0])) {
            return true;
        }
        if (this.mEventRecurrence.freq != 0 && (i = this.mEventRecurrence.freq) != 4) {
            if (i != 5) {
                if (i != 6) {
                    return i != 7 || this.mEventRecurrence.bydayCount > 0;
                }
                if (this.mEventRecurrence.repeatsMonthlyOnDayCount()) {
                    return false;
                }
                if (this.mEventRecurrence.bydayCount == 0 && this.mEventRecurrence.bymonthdayCount == 1) {
                    return false;
                }
            } else if (this.mEventRecurrence.repeatsOnEveryWeekEnd(getWeekendIndices()) || this.mEventRecurrence.repeatsOnEveryWeekDay(WeekUtils.getDayIndices(getContext())) || this.mEventRecurrence.repeatsBiweekly() || this.mEventRecurrence.bydayCount == 1) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isWeekendEvent(String[] strArr) {
        for (String str : strArr) {
            if (Integer.parseInt(str) == this.mStartTime.weekDay) {
                return true;
            }
        }
        return false;
    }

    private boolean cursorNotEmpty(Cursor cursor) {
        return (cursor == null || cursor.getCount() == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDateSet(int i, View view, int i2, int i3, int i4) {
        long millis;
        long jNormalize;
        Time time = this.mStartTime;
        Time time2 = this.mEndTime;
        Button button = this.mStartDateButton;
        if (button == null || this.mEndDateButton == null || this.mStartTimeButton == null || this.mEndTimeButton == null) {
            return;
        }
        if (i == button.getId()) {
            int i5 = time2.year - time.year;
            int i6 = time2.month - time.month;
            int i7 = time2.monthDay - time.monthDay;
            time.year = i2;
            time.month = i3;
            time.monthDay = i4;
            millis = time.normalize(true);
            time2.year = i2 + i5;
            time2.month = i3 + i6;
            time2.monthDay = i4 + i7;
            jNormalize = time2.normalize(true);
            populateRepeats();
            populateTimezone(millis);
        } else {
            millis = time.toMillis(true);
            time2.year = i2;
            time2.month = i3;
            time2.monthDay = i4;
            long jNormalize2 = time2.normalize(true);
            if (time2.before(time)) {
                time2.set(time);
                jNormalize = millis;
            } else {
                jNormalize = jNormalize2;
            }
        }
        if (this.mIsLunarEvent) {
            setLunarDate(this.mStartDateButton, millis, this.mTimezone);
            setLunarDate(this.mEndDateButton, jNormalize, this.mTimezone);
            populateLunarRepeats();
        } else {
            setDate(this.mStartDateButton, millis);
            setDate(this.mEndDateButton, jNormalize);
            populateRepeats();
        }
        setTime(this.mEndTimeButton, jNormalize);
        updateWarningText();
        updateHomeTime();
        updateEditEventDayView();
    }

    protected void updateEditEventDayView() {
        OnEditEventTimeChangedListener onEditEventTimeChangedListener = this.onEditEventTimeChangedListener;
        if (onEditEventTimeChangedListener != null) {
            onEditEventTimeChangedListener.onTimeChanged(this.mStartTime.normalize(true), this.mEndTime.normalize(true), this.mAllDaySwitch.isChecked(), this.mCalendarsSpinner.getSelectedItemId(), this.mIsCanSetDay);
        }
    }

    @Override // com.sonymobile.calendar.transitions.EditCircularRevealTransition.TransitionListener
    public void onTransitionStart() {
        this.mTransitionDone = false;
    }

    @Override // com.sonymobile.calendar.transitions.EditCircularRevealTransition.TransitionListener
    public void onTransitionEnd() {
        this.mTransitionDone = true;
        handleWarningTextViewVisibility();
    }

    public void setTransitionDone(boolean z) {
        this.mTransitionDone = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWarningTextViewVisibility() {
        int i;
        if (!this.mTransitionDone || (i = this.mWarningTextViewVisibility) == 8) {
            return;
        }
        this.mWarningTextView.setVisibility(i);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(250L);
        this.mWarningTextView.setAnimation(alphaAnimation);
    }

    private static class AccountsUpdateListener implements OnAccountsUpdateListener {
        private AccountsUpdateListener() {
        }

        @Override // android.accounts.OnAccountsUpdateListener
        public void onAccountsUpdated(Account[] accountArr) {
            boolean unused = EditEventView.mIsAccountInfoChanged = true;
        }
    }

    private class TimeClickListener implements View.OnClickListener {
        private Time mTime;

        public TimeClickListener(Time time) {
            this.mTime = time;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            TimePickerDialogFragment timePickerDialogFragmentNewInstance = TimePickerDialogFragment.newInstance(this.mTime.hour, this.mTime.minute, EditEventView.this.mTimePickerListener, view.getId());
            if (EditEventView.this.activity.isFinishing()) {
                return;
            }
            EditEventView editEventView = EditEventView.this;
            editEventView.showDialog(timePickerDialogFragmentNewInstance, editEventView.activity.getSupportFragmentManager(), EditEventView.TIME_PICKER_TAG);
        }
    }

    private class DateClickListener implements View.OnClickListener {
        private int mDialogMode;
        private Time mTime;

        public DateClickListener(Time time, int i) {
            this.mTime = time;
            this.mDialogMode = i;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (!UiUtils.isSub320dpScreen(EditEventView.this.getContext())) {
                String str = view.getId() == EditEventView.this.mStartDateButton.getId() ? EditEventView.START_DATE_PICKER_TAG : EditEventView.END_DATE_PICKER_TAG;
                DatePickerDialog datePickerDialogNewInstance = DatePickerDialog.newInstance(EditEventView.this.new DateSetListener(view.getId()), this.mTime.year, this.mTime.month, this.mTime.monthDay, this.mDialogMode);
                if (EditEventView.this.activity.isFinishing()) {
                    return;
                }
                int firstDayOfWeek = Utils.getFirstDayOfWeek(EditEventView.this.getContext());
                int i = 1;
                if (firstDayOfWeek == 6) {
                    i = 7;
                } else if (firstDayOfWeek == 1) {
                    i = 2;
                }
                datePickerDialogNewInstance.setFirstDayOfWeek(i);
                EditEventView editEventView = EditEventView.this;
                editEventView.showDialog(datePickerDialogNewInstance, editEventView.activity.getSupportFragmentManager(), str);
                return;
            }
            DatePickerDialogFragment datePickerDialogFragmentNewInstance = DatePickerDialogFragment.newInstance(this.mTime.year, this.mTime.month, this.mTime.monthDay, EditEventView.this.mDatePickerListener, view.getId());
            if (EditEventView.this.activity.isFinishing()) {
                return;
            }
            EditEventView editEventView2 = EditEventView.this;
            editEventView2.showDialog(datePickerDialogFragmentNewInstance, editEventView2.activity.getSupportFragmentManager(), EditEventView.DATE_PICKER_OLD_TAG);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDialog(DialogFragment dialogFragment, FragmentManager fragmentManager, String str) {
        Iterator<DialogFragment> it = this.shownDialogs.iterator();
        while (it.hasNext()) {
            it.next().dismiss();
        }
        this.shownDialogs.clear();
        this.shownDialogs.add(dialogFragment);
        dialogFragment.show(fragmentManager, str);
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {
        private int viewId;

        public DateSetListener(int i) {
            this.viewId = i;
        }

        @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.OnDateSetListener
        public void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
            EditEventView.this.mIsLunarEvent = datePickerDialog.isLunarOn();
            EditEventView.this.updateUris();
            EditEventView.this.performDateSet(this.viewId, null, i, i2, i3);
        }
    }

    private static class CalendarsAdapter extends ResourceCursorAdapter {
        protected AuthenticatorDescription[] mAuthDescs;
        private Map<String, AuthenticatorDescription> mTypeToAuthDescription;

        public CalendarsAdapter(Context context, Cursor cursor) {
            super(context, R.layout.calendars_spinner_item, cursor);
            this.mTypeToAuthDescription = new HashMap();
            setDropDownViewResource(R.layout.calendars_spinner_dropdown_item);
        }

        @Override // android.widget.CursorAdapter
        public void bindView(View view, Context context, Cursor cursor) {
            if (view instanceof LinearLayout) {
                AuthenticatorDescription[] authenticatorTypes = AccountManager.get(context).getAuthenticatorTypes();
                this.mAuthDescs = authenticatorTypes;
                for (AuthenticatorDescription authenticatorDescription : authenticatorTypes) {
                    this.mTypeToAuthDescription.put(authenticatorDescription.type, authenticatorDescription);
                }
                LinearLayout linearLayout = (LinearLayout) view;
                TextView textView = (TextView) linearLayout.findViewById(R.id.calendars_spinner_item_name);
                TextView textView2 = (TextView) linearLayout.findViewById(R.id.calendars_spinner_item_type);
                final Drawable drawable = ((ImageView) linearLayout.findViewById(R.id.calendars_spinner_item_icon)).getDrawable();
                CalendarColorService.getInstance().requestColor(context, new IAsyncServiceResultHandler() { // from class: com.sonymobile.calendar.editevent.EditEventView.CalendarsAdapter.1
                    @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
                    public void onResult(Object obj, Object obj2) {
                        drawable.setColorFilter(UiUtils.getDisplayColorFromColor(((Integer) obj).intValue()), PorterDuff.Mode.SRC);
                    }
                }, 1, cursor.getInt(0), true, false);
                String string = cursor.getString(1);
                String string2 = cursor.getString(4);
                String displayNameForAccountType = Utils.getDisplayNameForAccountType(context, string2);
                if (string == null || string.length() == 0 || string.trim().length() == 0) {
                    textView.setText(cursor.getString(2));
                } else {
                    textView.setText(Utils.getCalendarDisplayName(context, string2, string));
                    textView2.setText(displayNameForAccountType);
                }
                if (EditEventView.mIsAccountInfoChanged) {
                    boolean unused = EditEventView.mIsAccountInfoChanged = false;
                }
            }
        }
    }

    private class ConflictQueryHandler extends AsyncQueryHandler {
        public ConflictQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int i, Object obj, Cursor cursor) {
            if (cursor == null) {
                EditEventView.this.mWarningTextView.setVisibility(8);
                return;
            }
            try {
                if (i == 1) {
                    EditEventView.this.mSolarConflictEventCount = cursor.getCount();
                } else if (i == 2) {
                    EditEventView.this.mLunarConflictEventCount = cursor.getCount();
                }
                if (EditEventView.this.mAllDaySwitch.isChecked() || EditEventView.this.mSolarConflictEventCount + EditEventView.this.mLunarConflictEventCount <= 0) {
                    EditEventView.this.mWarningTextView.setVisibility(8);
                } else {
                    EditEventView.this.mWarningTextView.setText(EditEventView.this.getResources().getString(R.string.edit_event_time_conflict));
                    EditEventView.this.mWarningTextViewVisibility = 0;
                    EditEventView.this.handleWarningTextViewVisibility();
                }
                Utils.closeCursor(cursor);
            } catch (Throwable th) {
                Utils.closeCursor(cursor);
                throw th;
            }
        }
    }

    private class QueryHandler extends AsyncQueryHandler {
        private static final String DEFAULT_CALENDAR_NOT_SET = "0";

        public QueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int i, Object obj, Cursor cursor) {
            String string;
            String strExtractDomain;
            if (cursor == null || cursor.isClosed()) {
                return;
            }
            if (EditEventView.this.activity.isFinishing()) {
                EditEventView.this.activity.stopManagingCursor(cursor);
                Utils.closeCursor(cursor);
                return;
            }
            if (EditEventView.this.mCalendarsCursor != null) {
                EditEventView.this.activity.stopManagingCursor(EditEventView.this.mCalendarsCursor);
                Utils.closeCursor(EditEventView.this.mCalendarsCursor);
            }
            EditEventView.this.mCalendarsCursor = cursor;
            EditEventView.this.activity.startManagingCursor(cursor);
            if (cursor.getCount() == 0) {
                if (EditEventView.this.mSaveAfterQueryComplete) {
                    EditEventView.this.mLoadingCalendarsDialog.cancel();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(EditEventView.this.getContext(), R.style.AlertDialogTheme);
                builder.setTitle(R.string.no_syncable_calendars).setIcon(R.drawable.ic_dialog_alert_holo_light).setMessage(R.string.no_calendars_found).setPositiveButton(android.R.string.ok, EditEventView.this).setOnCancelListener(EditEventView.this);
                EditEventView.this.mNoCalendarsDialog = builder.show();
                return;
            }
            EditEventView editEventView = EditEventView.this;
            editEventView.mLocalCalendarPosition = findLocalCalendarPostition(editEventView.mCalendarsCursor);
            int iFindDefaultCalendarPosition = findDefaultCalendarPosition(EditEventView.this.mCalendarsCursor, EditEventView.this.mLocalCalendarPosition);
            if (EditEventView.this.mLocalCalendarPosition < 0) {
                EditEventView.this.mIsLocalCalendarSelected = false;
                EditEventView.this.populateWhen();
            }
            if (EditEventView.this.mEventCursor != null) {
                EditEventView.this.mCalendarsCursor.moveToPosition(-1);
                int i2 = 0;
                while (EditEventView.this.mCalendarsCursor.moveToNext()) {
                    if (!EditEventView.this.mCalendarsCursor.isClosed() && !EditEventView.this.mEventCursor.isClosed() && EditEventView.this.mCalendarsCursor.getInt(0) == EditEventView.this.mEventCursor.getInt(6)) {
                        iFindDefaultCalendarPosition = i2;
                        break;
                    }
                    i2++;
                }
                if (EditEventView.this.mIsLunarEvent) {
                    iFindDefaultCalendarPosition = EditEventView.this.mLocalCalendarPosition;
                }
            }
            EditEventView.this.populateCalendars(iFindDefaultCalendarPosition);
            if (!EditEventView.this.mHasAttendeeData || !cursor.moveToPosition(iFindDefaultCalendarPosition) || (string = cursor.getString(2)) == null || (strExtractDomain = EditEventView.extractDomain(string)) == null) {
                return;
            }
            EditEventView.this.mEmailValidator = new Rfc822Validator(strExtractDomain);
            EditEventView.this.mAttendeesListRequired.setValidator(EditEventView.this.mEmailValidator);
            EditEventView.this.mAttendeesListOptional.setValidator(EditEventView.this.mEmailValidator);
        }

        private int findDefaultCalendarPosition(Cursor cursor, int i) {
            if (cursor.getCount() <= 0) {
                return -1;
            }
            String sharedPreference = Utils.getSharedPreference(EditEventView.this.getContext(), GeneralPreferences.KEY_DEFAULT_CALENDAR, DEFAULT_CALENDAR_NOT_SET);
            if (sharedPreference.equals(DEFAULT_CALENDAR_NOT_SET)) {
                return getDefaultCalendarPosition(cursor, i);
            }
            cursor.moveToPosition(-1);
            int i2 = 0;
            while (cursor.moveToNext()) {
                if (sharedPreference.equals(Integer.toString(cursor.getInt(0)))) {
                    return i2;
                }
                i2++;
            }
            return getDefaultCalendarPosition(cursor, i);
        }

        private int getDefaultCalendarPosition(Cursor cursor, int i) {
            return (cursor.getCount() < 2 || i != 0) ? 0 : 1;
        }

        private int findLocalCalendarPostition(Cursor cursor) {
            if (cursor.getCount() == 0) {
                return -1;
            }
            int i = 0;
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                if ("LOCAL".equals(cursor.getString(4))) {
                    return i;
                }
                i++;
            }
            return -1;
        }
    }

    private class PickContactTask extends AsyncTask<Void, Void, ArrayList<String>> {
        private static final int MIN_DELIMITED_EMAIL_LENGTH = 5;
        private Context mContext;
        private Intent mData;
        private MultiAutoCompleteTextView mTextView;
        private HashSet<String> mUniqueEntries;

        public PickContactTask(Intent intent, MultiAutoCompleteTextView multiAutoCompleteTextView) {
            this.mData = intent;
            this.mTextView = multiAutoCompleteTextView;
            this.mContext = EditEventView.this.getContext().getApplicationContext();
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            super.onPreExecute();
            this.mUniqueEntries = new HashSet<>();
            String strTrim = this.mTextView.getText().toString().trim();
            int iLastIndexOf = strTrim.lastIndexOf(RecurrenceRuleParser.VALUE_SEPARATOR);
            if (iLastIndexOf == -1) {
                iLastIndexOf = strTrim.lastIndexOf(";");
            }
            if (iLastIndexOf > -1) {
                strTrim = strTrim.substring(0, iLastIndexOf);
            }
            String[] strArrSplit = strTrim.split("[,;]");
            if (strTrim.length() > 5) {
                for (String str : strArrSplit) {
                    int iIndexOf = str.indexOf("<") + 1;
                    int iIndexOf2 = str.indexOf(">");
                    if (iIndexOf > 0 && iIndexOf2 > iIndexOf + 5) {
                        String strSubstring = str.substring(iIndexOf, iIndexOf2);
                        if (Patterns.EMAIL_ADDRESS.matcher(strSubstring).matches()) {
                            this.mUniqueEntries.add(strSubstring);
                        }
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public ArrayList<String> doInBackground(Void... voidArr) {
            ArrayList parcelableArrayListExtra = this.mData.getParcelableArrayListExtra("email_uris");
            ArrayList<String> arrayList = new ArrayList<>();
            if (parcelableArrayListExtra != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("_id");
                sb.append(" IN (");
                StringBuilder sb2 = new StringBuilder();
                for (int i = 0; i < parcelableArrayListExtra.size(); i++) {
                    if (parcelableArrayListExtra.get(i) instanceof Uri) {
                        sb2.append(((Uri) parcelableArrayListExtra.get(i)).getLastPathSegment());
                        sb2.append(RecurrenceRuleParser.VALUE_SEPARATOR);
                    } else if (parcelableArrayListExtra.get(i) instanceof CharSequence) {
                        CharSequence charSequence = (CharSequence) parcelableArrayListExtra.get(i);
                        if (!TextUtils.isEmpty(charSequence)) {
                            arrayList.add(charSequence.toString());
                        }
                    }
                }
                if (sb2.length() == 0) {
                    return arrayList;
                }
                sb.append((CharSequence) sb2);
                sb.deleteCharAt(sb.length() - 1);
                sb.append(")");
                arrayList.addAll(getContactResults(ContactsContract.Data.CONTENT_URI, sb.toString()));
            } else {
                arrayList.addAll(getContactResults(this.mData.getData(), null));
            }
            return arrayList;
        }

        private List<String> getContactResults(Uri uri, String str) {
            ArrayList arrayList = new ArrayList();
            if (uri == null) {
                return arrayList;
            }
            Cursor cursorQuery = this.mContext.getContentResolver().query(uri, new String[]{"data1"}, str, null, null);
            if (cursorQuery != null) {
                try {
                    int columnIndex = cursorQuery.getColumnIndex("data1");
                    while (cursorQuery.moveToNext()) {
                        arrayList.add(cursorQuery.getString(columnIndex).trim());
                    }
                } catch (Throwable th) {
                    if (cursorQuery != null) {
                        try {
                            cursorQuery.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onCancelled(ArrayList<String> arrayList) {
            super.onCancelled(arrayList);
            this.mTextView = null;
            this.mData = null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(ArrayList<String> arrayList) {
            if (arrayList == null) {
                return;
            }
            this.mTextView.requestFocus();
            String string = this.mTextView.getText().toString();
            if (!TextUtils.isEmpty(string)) {
                String strTrim = string.trim();
                if (strTrim.charAt(strTrim.length() - 1) != ',') {
                    this.mTextView.append(", ");
                }
            }
            arrayList.removeAll(this.mUniqueEntries);
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                this.mTextView.append(it.next());
            }
        }
    }

    private static class ColorServiceResultHandler implements IAsyncServiceResultHandler {
        private Drawable drawable;
        private OnColorSetListener onColorSetListener;

        public ColorServiceResultHandler(Drawable drawable, OnColorSetListener onColorSetListener) {
            this.drawable = drawable;
            this.onColorSetListener = onColorSetListener;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() == 1) {
                int iIntValue = ((Integer) obj).intValue();
                OnColorSetListener onColorSetListener = this.onColorSetListener;
                if (onColorSetListener != null) {
                    onColorSetListener.onColorSet(iIntValue);
                }
                this.drawable.setColorFilter(UiUtils.getDisplayColorFromColor(iIntValue), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLunarDate(TextView textView, long j, String str) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(str));
        calendar.setTimeInMillis(j);
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime(), str);
        StringBuilder sb = new StringBuilder();
        mSB.setLength(0);
        sb.append(DateUtils.formatDateRange(this.activity, mF, j, j, 524290, str).toString()).append(RecurrenceRuleParser.VALUE_SEPARATOR).append(String.valueOf(lunarDateConvertSolarDateToLunarDate.mYear)).append(this.activity.getString(R.string.year_label)).append(LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate).subSequence(0, 2)).append(LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate));
        textView.setText(sb.toString());
    }

    private void populateLunarRepeats() {
        ArrayList<String> arrayList = this.repeatArray;
        if (arrayList != null) {
            arrayList.clear();
        }
        this.repeatArray = new ArrayList<>(0);
        ArrayList<Integer> arrayList2 = this.recurrenceIndexes;
        if (arrayList2 != null) {
            arrayList2.clear();
        }
        this.recurrenceIndexes = new ArrayList<>(0);
        Resources resources = getResources();
        this.repeatArray.add(resources.getString(R.string.does_not_repeat));
        this.recurrenceIndexes.add(0);
        String string = resources.getString(R.string.yearly);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.mStartTime.toMillis(true));
        LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime());
        this.repeatArray.add(String.format(string, LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate) + LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate)));
        this.recurrenceIndexes.add(1);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this.activity, R.layout.simple_spinner_item, this.repeatArray);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.mRepeatsSpinner.setAdapter((SpinnerAdapter) arrayAdapter);
        ArrayList<Integer> arrayList3 = this.recurrenceIndexes;
        this.mRecurrenceIndexes = arrayList3;
        int iIndexOf = arrayList3.indexOf(0);
        if (this.mRrule != null && this.mEventRecurrence.freq == 7) {
            iIndexOf = this.recurrenceIndexes.indexOf(1);
        }
        this.mRepeatsSpinner.setSelectionFirstTime(iIndexOf);
    }

    private void updateLunarRecurrenceRule() {
        int iIntValue = this.mRecurrenceIndexes.get(this.mRepeatsSpinner.getSelectedItemPosition()).intValue();
        if (iIntValue == 0) {
            this.mRrule = null;
            return;
        }
        if (iIntValue == 1) {
            this.mEventRecurrence.freq = 7;
        }
        this.mEventRecurrence.wkst = EventRecurrence.calendarDay2Day(this.mFirstDayOfWeek);
        this.mRrule = this.mEventRecurrence.toString();
    }

    protected void updateUris() {
        if (this.mIsLunarEvent) {
            this.mAttendeesUri = LunarContract.Attendees.CONTENT_URI;
            this.mRemindersUri = LunarContract.Reminders.CONTENT_URI;
            this.mEventsUri = LunarContract.Events.CONTENT_URI;
        } else {
            this.mAttendeesUri = CalendarContract.Attendees.CONTENT_URI;
            this.mRemindersUri = CalendarContract.Reminders.CONTENT_URI;
            this.mEventsUri = CalendarContract.Events.CONTENT_URI;
        }
    }

    public static void setSelectedReminders(ArrayList arrayList) {
        mSelectedRemindersIndices.clear();
        mSelectedRemindersIndices.addAll(arrayList);
    }

    public void updateRemindersLabel() {
        TextView textView = (TextView) findViewById(R.id.add_reminder);
        if (mSelectedRemindersIndices.isEmpty()) {
            textView.setText(getResources().getString(R.string.add_new_reminder));
            return;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Integer> it = mSelectedRemindersIndices.iterator();
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            if (iIntValue < 7) {
                sb.append(Utils.constructReminderLabelOrCustom(getContext(), this.mReminderValues.get(iIntValue).intValue(), true));
            } else {
                ArrayList<Integer> arrayList = this.mReminderValues;
                sb.append(this.mReminderLabels.get(findMinutesInReminderList(arrayList, arrayList.get(iIntValue).intValue())));
            }
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        textView.setText(sb.toString());
    }

    public static class SaveEventDialogFragment extends DialogFragment {
        public EditEventView mEditEventView;

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_body, (ViewGroup) null);
            textView.setText(getResources().getString(R.string.popup_title_save_event));
            if (bundle != null) {
                this.mEditEventView = (EditEventView) getActivity().findViewById(bundle.getInt(EditEventView.EDIT_EVENT_VIEW_ID_TAG));
            }
            return new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme).setView(textView).setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.SaveEventDialogFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (SaveEventDialogFragment.this.mEditEventView == null || SaveEventDialogFragment.this.mEditEventView.save()) {
                        SaveEventDialogFragment.this.getActivity().finish();
                    }
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.editevent.EditEventView.SaveEventDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    SaveEventDialogFragment.this.getActivity().finish();
                }
            }).create();
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            bundle.putInt(EditEventView.EDIT_EVENT_VIEW_ID_TAG, this.mEditEventView.getId());
            super.onSaveInstanceState(bundle);
        }

        public void setView(EditEventView editEventView) {
            this.mEditEventView = editEventView;
        }
    }

    protected static class RecipientTextWatcher implements TextWatcher {
        private final WeakReference<EditEventView> mEditEventViewReference;

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public RecipientTextWatcher(EditEventView editEventView) {
            this.mEditEventViewReference = new WeakReference<>(editEventView);
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            EditEventView editEventView = this.mEditEventViewReference.get();
            if (editEventView == null || editEventView.mSaveSendText == null) {
                return;
            }
            editEventView.mSaveSendText.setText(editEventView.hasRecipients() ? R.string.button_send : R.string.save_label);
        }
    }

    public boolean isEditing() {
        return this.isEditing;
    }
}
