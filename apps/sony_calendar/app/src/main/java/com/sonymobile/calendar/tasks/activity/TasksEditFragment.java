package com.sonymobile.calendar.tasks.activity;
import com.sonymobile.calendar.SafeTime;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.sonyericsson.calendar.util.CalendarColorService;
import com.sonyericsson.calendar.util.CloseKeyboardTouchListener;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.DatePickerDialogFragment;
import com.sonymobile.calendar.DatePickerDialogListener;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.TimePickerDialogFragment;
import com.sonymobile.calendar.TimePickerDialogListener;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.datetimepicker.date.DatePickerDialog;
import com.sonymobile.calendar.tablet.TabletTasksControllerFragment;
import com.sonymobile.calendar.tasks.TasksDatabase;
import com.sonymobile.calendar.tasks.TasksItemProvider;
import com.sonymobile.calendar.tasks.alerts.TasksAlertWork;
import com.sonymobile.calendar.tasks.model.TaskAccount;
import com.sonymobile.calendar.tasks.model.TasksListItem;
import com.sonymobile.calendar.tasks.settings.TaskAccountManager;
import com.sonymobile.calendar.tasks.utils.TimeFormatterUtil;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class TasksEditFragment extends Fragment {
    private static final String ACCOUNT = "account";
    private static final long CLICK_DELAY = 500;
    private static final String CURRENTLY_EDITING = "currentlyEditing";
    private static final String DESCRIPTION = "description";
    private static final String DUE_DATE = "dueDate";
    private static final String DUE_DATE_CHECKBOX = "dueDateCheckBox";
    private static final String DUE_DATE_PICKER_OLD_TAG = "dueDatePickerOld";
    private static final String DUE_DATE_PICKER_TAG = "dueDateDialog";
    private static final String IS_DELETE_DIALOG_OPEN = "IS_DELETE_DIALOG_OPEN";
    private static final String IS_NEW_TASK = "isNewTask";
    private static final String IS_SAVE_DIALOG_OPEN = "IS_SAVE_DIALOG_OPEN";
    private static final int LOADER_TASKS_DATA = 2;
    public static final int NEW_TASK = -1;
    public static final int NO_TASK = -3;
    private static final String PRIORITY = "priority";
    private static final String REMINDER_CHECKBOX = "reminderCheckBox";
    private static final String REMINDER_DATE = "reminderDate";
    private static final String REMINDER_PICKER_OLD_TAG = "reminderPickerOld";
    private static final String REMINDER_PICKER_TAG = "reminderDialog";
    private static final String SUBJECT = "subject";
    public static final int TABLET_NEW_TASK = -2;
    private static final String TASK_ID = "taskID";
    private static final String TASK_ITEM = "currentTask";
    private static final String TIME_PICKER_TAG = "timePicker";
    TaskEditTextWatcher descriptionWatcher;
    private ArrayList<TaskAccount> mAccountList;
    private Activity mActivity;
    private AlertDialog mCheckDialog;
    private AlertDialog mConfirmDialog;
    private Time mCurrentTime;
    private Time mDueDate;
    private boolean mOriginalDueDateValue;
    private boolean mOriginalReminderValue;
    private Time mReminderDate;
    private Time mSaveCurrentTime;
    private Time mSaveDueDate;
    private View.OnTouchListener mSpinnerTouchListener;
    private Time mStartDate;
    private TabletTasksControllerFragment mTabletTasksControllerFragment;
    private long mTaskId;
    private TasksListItem mTaskItem;
    private Toolbar mToolbar;
    private boolean newTask;
    private int originalAccountId;
    private long reminderMillis;
    TaskEditTextWatcher subjectWatcher;
    private EditText mTasksSubject = null;
    private EditText mTasksDescription = null;
    private Spinner mTasksPriority = null;
    private Spinner mTasksAccount = null;
    private CheckBox mDueDateCheckbox = null;
    private CheckBox mReminderCheckbox = null;
    private Button mDueDateButton = null;
    private Button mReminderDateButton = null;
    private Button mReminderTimeButton = null;
    private int mAccountIndex = 0;
    private boolean isSwitchAccountFromOther = false;
    private boolean updateDatabase = false;
    private boolean currentlyEditingTask = false;
    private boolean mTablet = false;
    private int mDefaultPrioritySelection = -1;
    private boolean isDeleteDialogOpen = false;
    private boolean isSaveTaskDialogOpen = false;
    private TasksDatabase.DirtyType mReminderDirtyType = TasksDatabase.DirtyType.UNKNOWN;
    private int mSavedSoftInputMode = 16;
    private DatePickerDialogListener mOldDialogListener = new DatePickerDialogListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.15
        @Override // com.sonymobile.calendar.DatePickerDialogListener
        public void onDateSet(int i, int i2, int i3, int i4) {
            TasksEditFragment.this.performDateSet(i4, i, i2, i3);
        }
    };
    private TimePickerDialogListener mTimePickerListener = new TimePickerDialogListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.16
        @Override // com.sonymobile.calendar.TimePickerDialogListener
        public void onTimeSet(int i, int i2, int i3) {
            Time time = new SafeTime(TasksEditFragment.this.mReminderDate);
            time.hour = i;
            time.minute = i2;
            time.second = 0;
            if (i3 == TasksEditFragment.this.mReminderTimeButton.getId()) {
                TasksEditFragment.this.mCurrentTime.setToNow();
                if (time.before(TasksEditFragment.this.mCurrentTime)) {
                    if (TasksEditFragment.this.mCheckDialog == null || !TasksEditFragment.this.mCheckDialog.isShowing()) {
                        TasksEditFragment.this.showCheckDialog(false);
                        return;
                    }
                    return;
                }
                TasksEditFragment.this.mSaveCurrentTime.set(TasksEditFragment.this.mReminderDate);
                TasksEditFragment.this.mSaveCurrentTime.hour = i;
                TasksEditFragment.this.mSaveCurrentTime.minute = i2;
                TasksEditFragment.this.mSaveCurrentTime.second = 0;
                TasksEditFragment.this.reminderMillis = time.normalize(true);
                Button button = TasksEditFragment.this.mReminderTimeButton;
                TasksEditFragment tasksEditFragment = TasksEditFragment.this;
                button.setText(tasksEditFragment.getFormattedTime(tasksEditFragment.reminderMillis));
                TasksEditFragment.this.mReminderDate.set(time);
            }
        }
    };
    private final LoaderManager.LoaderCallbacks<Cursor> mTasksMetadataLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.17
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new CursorLoader(TasksEditFragment.this.getActivity(), TasksContract.Tasks.CONTENT_URI, null, "_id=" + TasksEditFragment.this.mTaskId, null, null);
        }

        /* JADX WARN: Type inference failed for: r5v40, types: [com.sonymobile.calendar.tasks.activity.TasksEditFragment$17$1] */
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            String string;
            if (TasksEditFragment.this.getActivity().isFinishing()) {
                return;
            }
            if (cursor != null && cursor.moveToFirst() && TasksEditFragment.this.mTaskId > 0) {
                TasksEditFragment.this.mTaskItem.taskId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                TasksEditFragment tasksEditFragment = TasksEditFragment.this;
                tasksEditFragment.mTaskId = tasksEditFragment.mTaskItem.taskId;
                TasksEditFragment.this.mTaskItem.taskName = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                TasksEditFragment.this.mTaskItem.completed = cursor.getInt(cursor.getColumnIndexOrThrow("complete"));
                TasksEditFragment.this.mTaskItem.priority = cursor.getInt(cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.IMPORTANCE));
                TasksEditFragment.this.mTaskItem.accountId = cursor.getInt(cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.TASKLIST_ID));
                String string2 = cursor.getString(cursor.getColumnIndex("account_type"));
                TasksEditFragment.this.mTaskItem.accountType = string2;
                if ("LOCAL".equalsIgnoreCase(string2)) {
                    string = TasksEditFragment.this.getActivity().getResources().getString(R.string.task_app_name_txt);
                } else {
                    string = cursor.getString(cursor.getColumnIndexOrThrow("account_name"));
                }
                TasksEditFragment.this.mTaskItem.accountName = string;
                TasksEditFragment.this.mTaskItem.dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.DUE_DATE));
                TasksEditFragment.this.mTaskItem.taskDescription = cursor.getString(cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.BODY_DATA));
                TasksEditFragment.this.mTaskItem.hasReminder = cursor.getInt(cursor.getColumnIndexOrThrow(TasksContract.TasksColumns.HAS_REMINDER)) > 0;
                if (!TasksEditFragment.this.mTaskItem.hasReminder || TasksEditFragment.this.getActivity() == null) {
                    TasksEditFragment.this.initiateViewsForExistingTask();
                } else {
                    new AsyncTask<Void, Void, TasksListItem>() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.17.1
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // android.os.AsyncTask
                        public TasksListItem doInBackground(Void... voidArr) {
                            return TasksDatabase.getReminderDataForTask(TasksEditFragment.this.getActivity(), TasksEditFragment.this.mTaskItem);
                        }

                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // android.os.AsyncTask
                        public void onPostExecute(TasksListItem tasksListItem) {
                            TasksEditFragment.this.mTaskItem = tasksListItem;
                            TasksEditFragment.this.initiateViewsForExistingTask();
                        }
                    }.execute(new Void[0]);
                }
            }
            TasksEditFragment.this.getLoaderManager().destroyLoader(2);
        }
    };

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mSpinnerTouchListener = new CloseKeyboardTouchListener(activity.getWindow());
        setHasOptionsMenu(true);
        this.mDueDate = new SafeTime();
        this.mReminderDate = new SafeTime();
        this.mStartDate = new SafeTime();
        this.mCurrentTime = new SafeTime();
        this.mSaveDueDate = new SafeTime();
        this.mSaveCurrentTime = new SafeTime();
        this.mTaskItem = new TasksListItem();
        this.mCurrentTime.setToNow();
        this.mSaveDueDate.set(0, 0, 0, this.mCurrentTime.monthDay, this.mCurrentTime.month, this.mCurrentTime.year);
        this.mSaveCurrentTime.set(this.mCurrentTime);
        this.mSaveCurrentTime.second = 0;
        this.mSavedSoftInputMode = activity.getWindow().getAttributes().softInputMode;
        activity.getWindow().setSoftInputMode(16);
        this.mActivity = activity;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        getActivity().getWindow().setSoftInputMode(this.mSavedSoftInputMode);
        super.onDetach();
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.newTask = getActivity().getIntent().getLongExtra(TasksEditActivity.TASK_ID, -1L) == -1;
        if (this.mTablet) {
            this.newTask = false;
        }
    }

    private void recreateViews(Bundle bundle) {
        TasksListItem taskListItem;
        this.currentlyEditingTask = bundle.getBoolean(CURRENTLY_EDITING);
        this.mTaskId = bundle.getLong(TASK_ID);
        this.mTasksSubject.setText(bundle.getString("subject"));
        this.mTasksDescription.setText(bundle.getString("description"));
        this.subjectWatcher.setOriginalText(bundle.getString("subject"));
        this.descriptionWatcher.setOriginalText(bundle.getString("description"));
        int i = bundle.getInt(PRIORITY);
        this.mDefaultPrioritySelection = i;
        this.mTasksPriority.setSelection(i, true);
        this.mTasksAccount.setSelection(bundle.getInt(ACCOUNT), true);
        this.mDueDate.set(bundle.getLong(DUE_DATE));
        boolean z = bundle.getBoolean(DUE_DATE_CHECKBOX);
        this.mOriginalDueDateValue = z;
        this.mDueDateCheckbox.setChecked(z);
        this.mDueDateButton.setEnabled(this.mOriginalDueDateValue);
        this.mDueDateButton.setText(this.mOriginalDueDateValue ? getFormattedDate(this.mDueDate.normalize(true)) : getResources().getString(R.string.task_edit_reminder_time_btn_none_txt));
        this.mReminderDate.set(bundle.getLong(REMINDER_DATE));
        this.mReminderDateButton.setText(getFormattedDate(this.mReminderDate.normalize(true)));
        this.mReminderTimeButton.setText(getFormattedTime(this.mReminderDate.toMillis(true)));
        this.mSaveCurrentTime.set(this.mReminderDate);
        this.mSaveCurrentTime.second = 0;
        boolean z2 = bundle.getBoolean(REMINDER_CHECKBOX);
        this.mOriginalReminderValue = z2;
        this.mReminderCheckbox.setChecked(z2);
        this.mReminderDateButton.setEnabled(this.mOriginalReminderValue);
        this.mReminderTimeButton.setEnabled(this.mOriginalReminderValue);
        this.newTask = bundle.getBoolean(IS_NEW_TASK);
        TasksListItem tasksListItem = (TasksListItem) bundle.getParcelable(TASK_ITEM);
        if (tasksListItem != null) {
            this.mTaskItem = tasksListItem;
        }
        if (this.mTaskId == -3) {
            getView().setVisibility(8);
        }
        if (bundle.getBoolean(IS_DELETE_DIALOG_OPEN, false)) {
            deleteTask();
        }
        if (bundle.getBoolean(IS_SAVE_DIALOG_OPEN, false) && (taskListItem = getTaskListItem()) != null && this.updateDatabase) {
            showSaveTaskDialog(taskListItem);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        long millis = this.mDueDate.toMillis(false);
        long millis2 = this.mReminderDate.toMillis(false);
        int selectedItemPosition = this.mTasksPriority.getSelectedItemPosition();
        String string = this.mTasksSubject.getText().toString();
        String string2 = this.mTasksDescription.getText().toString();
        int selectedItemPosition2 = this.mTasksAccount.getSelectedItemPosition();
        boolean zIsChecked = this.mDueDateCheckbox.isChecked();
        boolean zIsChecked2 = this.mReminderCheckbox.isChecked();
        bundle.putLong(DUE_DATE, millis);
        bundle.putLong(REMINDER_DATE, millis2);
        bundle.putBoolean(DUE_DATE_CHECKBOX, zIsChecked);
        bundle.putBoolean(REMINDER_CHECKBOX, zIsChecked2);
        bundle.putInt(ACCOUNT, selectedItemPosition2);
        bundle.putInt(PRIORITY, selectedItemPosition);
        bundle.putString("subject", string);
        bundle.putString("description", string2);
        bundle.putBoolean(CURRENTLY_EDITING, this.currentlyEditingTask);
        bundle.putLong(TASK_ID, this.mTaskId);
        bundle.putParcelable(TASK_ITEM, this.mTaskItem);
        bundle.putBoolean(IS_NEW_TASK, this.newTask);
        bundle.putBoolean(IS_DELETE_DIALOG_OPEN, this.isDeleteDialogOpen);
        bundle.putBoolean(IS_SAVE_DIALOG_OPEN, this.isSaveTaskDialogOpen);
        AlertDialog alertDialog = this.mConfirmDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mConfirmDialog = null;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        TabletTasksControllerFragment tabletTasksControllerFragment;
        if (!Utils.isPackageEnabled(TasksListFragment.TASKS_PACKAGE_NAME, getActivity())) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
        View viewInflate = layoutInflater.inflate(R.layout.edit_tasks, viewGroup, false);
        this.mTablet = getResources().getBoolean(R.bool.tablet_mode);
        initDoneButton(viewInflate, layoutInflater);
        Spinner spinner = (Spinner) viewInflate.findViewById(R.id.priority_spinner);
        this.mTasksPriority = spinner;
        spinner.setOnTouchListener(this.mSpinnerTouchListener);
        Spinner spinner2 = (Spinner) viewInflate.findViewById(R.id.account_spinner);
        this.mTasksAccount = spinner2;
        spinner2.setOnTouchListener(this.mSpinnerTouchListener);
        setFocusDown();
        this.mTasksSubject = (EditText) viewInflate.findViewById(R.id.tasks_subject);
        this.mTasksDescription = (EditText) viewInflate.findViewById(R.id.tasks_description);
        this.mDueDateCheckbox = (CheckBox) viewInflate.findViewById(R.id.due_date_checkbox);
        this.mReminderCheckbox = (CheckBox) viewInflate.findViewById(R.id.reminder_checkbox);
        this.mDueDateButton = (Button) viewInflate.findViewById(R.id.select_due_date_button);
        this.mReminderDateButton = (Button) viewInflate.findViewById(R.id.select_reminder_date_button);
        Button button = (Button) viewInflate.findViewById(R.id.select_reminder_time_button);
        this.mReminderTimeButton = button;
        button.setOnTouchListener(this.mSpinnerTouchListener);
        this.subjectWatcher = new TaskEditTextWatcher();
        this.descriptionWatcher = new TaskEditTextWatcher();
        this.mTasksSubject.addTextChangedListener(this.subjectWatcher);
        this.mTasksDescription.addTextChangedListener(this.descriptionWatcher);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, getActivity().getResources().getStringArray(R.array.task_priority_options));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mTasksPriority.setAdapter((SpinnerAdapter) arrayAdapter);
        this.mTasksPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (TasksEditFragment.this.mDefaultPrioritySelection == i || TasksEditFragment.this.mDefaultPrioritySelection == -1) {
                    return;
                }
                TasksEditFragment.this.taskHasBeenEdited();
            }
        });
        if (TasksDatabase.createTaskListsInDatabaseIfNeeded(getActivity()) && (tabletTasksControllerFragment = this.mTabletTasksControllerFragment) != null) {
            tabletTasksControllerFragment.updateAccounts(getActivity());
        }
        updateAccounts(getActivity());
        this.mTasksAccount.setAdapter((SpinnerAdapter) new TasksAccountsAdapter(getActivity(), this.mAccountList));
        this.mTasksAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.2
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (TasksEditFragment.this.mAccountIndex != i && TasksEditFragment.this.mAccountIndex != 0 && i != TasksEditFragment.this.originalAccountId) {
                    TasksEditFragment.this.taskHasBeenEdited();
                }
                TasksEditFragment.this.mAccountIndex = i;
            }
        });
        viewInflate.findViewById(R.id.tasks_account_spinner_group).setVisibility(this.mAccountList.size() <= 1 ? 8 : 0);
        this.mDueDateButton.setOnClickListener(new DateClickListener(this.mDueDate));
        this.mReminderDateButton.setOnClickListener(new DateClickListener(this.mReminderDate));
        this.mReminderDateButton.setOnClickListener(new DateClickListener(this.mReminderDate));
        this.mReminderTimeButton.setOnClickListener(new TimeClickListener(this.mReminderDate));
        this.mDueDateCheckbox.setOnCheckedChangeListener(new CheckBoxStatusChange());
        this.mReminderCheckbox.setOnCheckedChangeListener(new CheckBoxStatusChange());
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag(DUE_DATE_PICKER_TAG);
        if (fragmentFindFragmentByTag != null) {
            Utils.restoreDatePickerDialog(getContext(), (DatePickerDialog) fragmentFindFragmentByTag, new DateSetListener(this.mDueDateButton.getId()));
        }
        Fragment fragmentFindFragmentByTag2 = supportFragmentManager.findFragmentByTag(REMINDER_PICKER_TAG);
        if (fragmentFindFragmentByTag2 != null) {
            Utils.restoreDatePickerDialog(getContext(), (DatePickerDialog) fragmentFindFragmentByTag2, new DateSetListener(this.mReminderDateButton.getId()));
        }
        Fragment fragmentFindFragmentByTag3 = supportFragmentManager.findFragmentByTag(DUE_DATE_PICKER_OLD_TAG);
        if (fragmentFindFragmentByTag3 != null) {
            ((DatePickerDialogFragment) fragmentFindFragmentByTag3).setDateListener(this.mOldDialogListener);
        }
        Fragment fragmentFindFragmentByTag4 = supportFragmentManager.findFragmentByTag(REMINDER_PICKER_OLD_TAG);
        if (fragmentFindFragmentByTag4 != null) {
            ((DatePickerDialogFragment) fragmentFindFragmentByTag4).setDateListener(this.mOldDialogListener);
        }
        Fragment fragmentFindFragmentByTag5 = supportFragmentManager.findFragmentByTag(TIME_PICKER_TAG);
        if (fragmentFindFragmentByTag5 != null) {
            ((TimePickerDialogFragment) fragmentFindFragmentByTag5).setTimeListener(this.mTimePickerListener);
        }
        return viewInflate;
    }

    public void updateAccounts(Context context) {
        this.mAccountList = TaskAccountManager.getInstance().getAccountLists(context);
    }

    private void setFocusDown() {
        if (this.mTablet) {
            return;
        }
        this.mToolbar.findViewById(R.id.action_save).setNextFocusDownId(this.mTasksAccount.getId());
    }

    private static class TasksAccountsAdapter extends ArrayAdapter<TaskAccount> {
        private ArrayList<TaskAccount> accountList;
        private Context context;
        protected AuthenticatorDescription[] mAuthDescs;
        private Map<String, AuthenticatorDescription> mTypeToAuthDescription;

        public TasksAccountsAdapter(Context context, ArrayList<TaskAccount> arrayList) {
            super(context, R.layout.calendars_spinner_item, arrayList);
            this.mTypeToAuthDescription = new HashMap();
            this.accountList = arrayList;
            this.context = context;
            setDropDownViewResource(R.layout.calendars_spinner_dropdown_item);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(this.context, R.layout.calendars_spinner_item, null);
            }
            TaskAccount taskAccount = this.accountList.get(i);
            if (view instanceof LinearLayout) {
                AuthenticatorDescription[] authenticatorTypes = AccountManager.get(this.context).getAuthenticatorTypes();
                this.mAuthDescs = authenticatorTypes;
                for (AuthenticatorDescription authenticatorDescription : authenticatorTypes) {
                    this.mTypeToAuthDescription.put(authenticatorDescription.type, authenticatorDescription);
                }
                LinearLayout linearLayout = (LinearLayout) view;
                TextView textView = (TextView) linearLayout.findViewById(R.id.calendars_spinner_item_name);
                TextView textView2 = (TextView) linearLayout.findViewById(R.id.calendars_spinner_item_type);
                CalendarColorService.getInstance().requestColorByTaskAccountId(this.context, new ColorServiceResultHandler(((ImageView) linearLayout.findViewById(R.id.calendars_spinner_item_icon)).getDrawable()), 1, taskAccount.id, true, false);
                String string = taskAccount.name;
                if (taskAccount.isLocal()) {
                    if (this.context.getResources().getBoolean(R.bool.tablet_mode)) {
                        string = this.context.getResources().getString(R.string.task_default_account_tablet_txt);
                    } else {
                        string = this.context.getResources().getString(R.string.task_default_account_txt);
                    }
                }
                String str = taskAccount.type;
                String displayNameForAccountType = Utils.getDisplayNameForAccountType(this.context, str);
                if (string == null || string.length() == 0 || string.trim().length() == 0) {
                    textView.setText(displayNameForAccountType);
                } else {
                    textView.setText(Utils.getTaskAccountDisplayName(this.context, str, string));
                    textView2.setText(displayNameForAccountType);
                }
            }
            return view;
        }

        @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(this.context, R.layout.calendars_spinner_dropdown_item, null);
            }
            TaskAccount taskAccount = this.accountList.get(i);
            if (view instanceof LinearLayout) {
                AuthenticatorDescription[] authenticatorTypes = AccountManager.get(this.context).getAuthenticatorTypes();
                this.mAuthDescs = authenticatorTypes;
                for (AuthenticatorDescription authenticatorDescription : authenticatorTypes) {
                    this.mTypeToAuthDescription.put(authenticatorDescription.type, authenticatorDescription);
                }
                LinearLayout linearLayout = (LinearLayout) view;
                TextView textView = (TextView) linearLayout.findViewById(R.id.calendars_spinner_item_name);
                TextView textView2 = (TextView) linearLayout.findViewById(R.id.calendars_spinner_item_type);
                CalendarColorService.getInstance().requestColorByTaskAccountId(this.context, new ColorServiceResultHandler(((ImageView) linearLayout.findViewById(R.id.calendars_spinner_item_icon)).getDrawable()), 1, taskAccount.id, true, false);
                String str = taskAccount.name;
                String str2 = taskAccount.type;
                String displayNameForAccountType = Utils.getDisplayNameForAccountType(this.context, str2);
                if (str == null || str.length() == 0 || str.trim().length() == 0) {
                    textView.setText(displayNameForAccountType);
                } else {
                    textView.setText(Utils.getTaskAccountDisplayName(this.context, str2, str));
                    textView2.setText(displayNameForAccountType);
                }
            }
            return view;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle != null) {
            recreateViews(bundle);
            return;
        }
        long longExtra = getActivity().getIntent().getLongExtra(TasksEditActivity.TASK_ID, -1L);
        this.mTaskId = longExtra;
        if (longExtra == -1) {
            if (Time.isEpoch(this.mDueDate) && Time.isEpoch(this.mReminderDate) && Time.isEpoch(this.mStartDate)) {
                this.mDueDate.setToNow();
                this.mReminderDate.setToNow();
                this.mStartDate.setToNow();
                this.mCurrentTime.setToNow();
            }
            if (this.mTablet) {
                return;
            }
            initiateViewsForNewTask();
            return;
        }
        if (Time.isEpoch(this.mCurrentTime)) {
            this.mCurrentTime.setToNow();
        }
        loadTasksData();
    }

    public void updateTask(long j) {
        this.mTaskId = j;
        this.currentlyEditingTask = false;
        if (j == -3) {
            getView().setVisibility(8);
        } else if (j == -2) {
            this.mTaskItem = new TasksListItem();
            getView().setVisibility(0);
            initiateViewsForNewTask();
        } else {
            getView().setVisibility(0);
            loadTasksData();
        }
        getActivity().invalidateOptionsMenu();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TasksListItem getTaskListItem() {
        if (this.newTask || isNewTabletTask()) {
            return getTasksListItemFromUI();
        }
        return updateTasksListItemFromUI();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNewTabletTask() {
        return getActivity().getIntent().getLongExtra(TasksEditActivity.TASK_ID, -1L) == -2;
    }

    public void setTabletTasksControllerFragment(TabletTasksControllerFragment tabletTasksControllerFragment) {
        this.mTabletTasksControllerFragment = tabletTasksControllerFragment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doSaveTasks() {
        TasksListItem taskListItem = getTaskListItem();
        if (this.newTask || isNewTabletTask()) {
            addNewTasks(taskListItem);
            return;
        }
        if (this.updateDatabase) {
            updateExistTasks(taskListItem);
            if (this.mTablet) {
                return;
            }
            Utils.hideKeyboard(getActivity(), getView());
            getActivity().finish();
            return;
        }
        if (!this.mTablet) {
            getActivity().finish();
        } else {
            getActivity().invalidateOptionsMenu();
        }
    }

    public void onBackPressed() {
        TasksListItem taskListItem = getTaskListItem();
        if (taskListItem != null && this.updateDatabase) {
            showSaveTaskDialog(taskListItem);
        } else {
            getActivity().finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSaveTaskDialog(final TasksListItem tasksListItem) {
        this.isSaveTaskDialogOpen = true;
        AlertDialog alertDialogShow = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme).setTitle(R.string.task_save_confirm_dialog_title_txt).setPositiveButton(R.string.task_save_confirm_dialog_btn_save_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                TasksEditFragment.this.mCurrentTime.setToNow();
                Time time = new SafeTime(TasksEditFragment.this.mCurrentTime);
                time.set(0, 0, 0, TasksEditFragment.this.mCurrentTime.monthDay, TasksEditFragment.this.mCurrentTime.month, TasksEditFragment.this.mCurrentTime.year);
                Time time2 = new SafeTime(TasksEditFragment.this.mCurrentTime);
                if (!TasksEditFragment.this.mDueDateCheckbox.isChecked() || !TasksEditFragment.this.mReminderCheckbox.isChecked()) {
                    if (!TasksEditFragment.this.mDueDateCheckbox.isChecked() || TasksEditFragment.this.mReminderCheckbox.isChecked()) {
                        if (TasksEditFragment.this.mDueDateCheckbox.isChecked() || !TasksEditFragment.this.mReminderCheckbox.isChecked()) {
                            if (TasksEditFragment.this.mDueDateCheckbox.isChecked() || TasksEditFragment.this.mReminderCheckbox.isChecked()) {
                                return;
                            }
                            if (TasksEditFragment.this.newTask || TasksEditFragment.this.isNewTabletTask()) {
                                TasksEditFragment.this.addNewTasks(tasksListItem);
                            } else {
                                TasksEditFragment.this.updateExistTasks(tasksListItem);
                            }
                            TasksEditFragment.this.isSaveTaskDialogOpen = false;
                            return;
                        }
                        if (TasksEditFragment.this.mSaveDueDate.before(time)) {
                            TasksEditFragment.this.showCheckDialog(true);
                            return;
                        }
                        if (TasksEditFragment.this.mSaveCurrentTime.before(time2)) {
                            TasksEditFragment.this.showCheckDialog(false);
                            return;
                        }
                        if (TasksEditFragment.this.newTask || TasksEditFragment.this.isNewTabletTask()) {
                            TasksEditFragment.this.addNewTasks(tasksListItem);
                        } else {
                            TasksEditFragment.this.updateExistTasks(tasksListItem);
                        }
                        TasksEditFragment.this.isSaveTaskDialogOpen = false;
                        return;
                    }
                    if (TasksEditFragment.this.mSaveDueDate.before(time)) {
                        TasksEditFragment.this.showCheckDialog(true);
                        return;
                    }
                    if (TasksEditFragment.this.newTask || TasksEditFragment.this.isNewTabletTask()) {
                        TasksEditFragment.this.addNewTasks(tasksListItem);
                    } else {
                        TasksEditFragment.this.updateExistTasks(tasksListItem);
                    }
                    TasksEditFragment.this.isSaveTaskDialogOpen = false;
                    return;
                }
                if (TasksEditFragment.this.mSaveDueDate.before(time)) {
                    TasksEditFragment.this.showCheckDialog(true);
                    return;
                }
                if (TasksEditFragment.this.mSaveCurrentTime.before(time2)) {
                    TasksEditFragment.this.showCheckDialog(false);
                    return;
                }
                if (TasksEditFragment.this.newTask || TasksEditFragment.this.isNewTabletTask()) {
                    TasksEditFragment.this.addNewTasks(tasksListItem);
                } else {
                    TasksEditFragment.this.updateExistTasks(tasksListItem);
                }
                TasksEditFragment.this.isSaveTaskDialogOpen = false;
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                TasksEditFragment.this.isSaveTaskDialogOpen = false;
                TasksEditFragment.this.getActivity().finish();
            }
        }).show();
        this.mConfirmDialog = alertDialogShow;
        alertDialogShow.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.5
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                TasksEditFragment.this.isSaveTaskDialogOpen = false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.sonymobile.calendar.tasks.activity.TasksEditFragment$6] */
    public void addNewTasks(final TasksListItem tasksListItem) {
        new AsyncTask<Void, Void, Long>() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.6
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Long doInBackground(Void... voidArr) {
                return Long.valueOf(TasksDatabase.addNewTask(TasksEditFragment.this.getActivity(), tasksListItem));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Long l) {
                int i = l.longValue() != -1 ? R.string.task_add_success_toast_txt : R.string.task_add_failed_toast_txt;
                FragmentActivity activity = TasksEditFragment.this.getActivity();
                if (activity != null) {
                    Toast.makeText(TasksEditFragment.this.getActivity(), i, 1).show();
                    Utils.setSharedPreference(TasksEditFragment.this.getActivity(), GeneralPreferences.KEY_DEFAULT_TASKS_ACCOUNT, tasksListItem.accountName);
                }
                if (!TasksEditFragment.this.mTablet) {
                    if (activity != null) {
                        activity.finish();
                        return;
                    }
                    return;
                }
                TasksEditFragment.this.newTask = false;
                TasksEditFragment.this.mTaskId = l.longValue();
                TasksEditFragment.this.mTaskItem = tasksListItem;
                TasksEditFragment.this.mTaskItem.taskId = l.longValue();
                if (TasksEditFragment.this.mTabletTasksControllerFragment != null) {
                    TasksEditFragment.this.mTabletTasksControllerFragment.createTaskCompleted(l.longValue());
                }
                TasksEditFragment.this.initiateViewsForExistingTask();
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.sonymobile.calendar.tasks.activity.TasksEditFragment$7] */
    public void updateExistTasks(final TasksListItem tasksListItem) {
        new AsyncTask<Void, Void, Boolean>() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.7
            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                if (TasksEditFragment.this.isSwitchAccountFromOther) {
                    TasksEditFragment tasksEditFragment = TasksEditFragment.this;
                    tasksEditFragment.updateTaskAccount(tasksEditFragment.getTasksListItemFromUI());
                }
                super.onPreExecute();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... voidArr) {
                return Boolean.valueOf(TasksDatabase.updateExistingTask(TasksEditFragment.this.getActivity(), tasksListItem, TasksEditFragment.this.mReminderDirtyType));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean bool) {
                int i = bool.booleanValue() ? R.string.task_update_success_toast_txt : R.string.task_update_failed_toast_txt;
                FragmentActivity activity = TasksEditFragment.this.getActivity();
                if (activity != null) {
                    Toast.makeText(TasksEditFragment.this.getActivity(), i, 1).show();
                }
                if (TasksEditFragment.this.mTablet) {
                    if (TasksEditFragment.this.mTabletTasksControllerFragment != null) {
                        TasksEditFragment.this.mTabletTasksControllerFragment.createTaskCompleted(TasksEditFragment.this.mTaskItem.taskId);
                    }
                } else if (activity != null) {
                    TasksEditFragment.this.getActivity().finish();
                }
            }
        }.execute(new Void[0]);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.tasks_edit_actions, menu);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        TabletTasksControllerFragment tabletTasksControllerFragment;
        boolean zIsNewTabletTask = isNewTabletTask();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item != null) {
                int itemId = item.getItemId();
                boolean z = true;
                if (itemId == R.id.action_cancel) {
                    if (!zIsNewTabletTask && !this.currentlyEditingTask) {
                        z = false;
                    }
                    item.setVisible(z);
                } else if (itemId != R.id.action_delete) {
                    if (itemId == R.id.action_done) {
                        if (!zIsNewTabletTask && !this.currentlyEditingTask) {
                            z = false;
                        }
                        item.setVisible(z);
                    }
                } else if (this.mTablet && (tabletTasksControllerFragment = this.mTabletTasksControllerFragment) != null) {
                    item.setVisible((zIsNewTabletTask || tabletTasksControllerFragment.getTasksListFragment().getCount() <= 0 || this.mTabletTasksControllerFragment.getTasksListFragment().isSearching()) ? false : true);
                }
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        this.currentlyEditingTask = false;
        boolean zIsNewTabletTask = isNewTabletTask();
        Utils.hideKeyboard(getActivity(), getView());
        int itemId = menuItem.getItemId();
        if (itemId != R.id.action_cancel) {
            if (itemId == R.id.action_delete) {
                deleteTask();
                return true;
            }
            if (itemId == R.id.action_done) {
                doSaveTasks();
                return true;
            }
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mTablet) {
            this.mTabletTasksControllerFragment.createTaskCompleted(-1L);
            if (!this.newTask && !zIsNewTabletTask) {
                initiateViewsForExistingTask();
            }
        } else {
            getActivity().finish();
        }
        return true;
    }

    private void deleteTask() {
        this.isDeleteDialogOpen = true;
        TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_body, (ViewGroup) null);
        textView.setText(getActivity().getResources().getString(R.string.task_delete_confirm_dialog_title_txt));
        this.mConfirmDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme).setView(textView).setPositiveButton(getString(R.string.task_delete_confirm_dialog_btn_delete_txt), new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.10
            /* JADX WARN: Type inference failed for: r1v1, types: [com.sonymobile.calendar.tasks.activity.TasksEditFragment$10$1] */
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.10.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Integer doInBackground(Void... voidArr) {
                        return Integer.valueOf(TasksDatabase.deleteTask(TasksEditFragment.this.getActivity(), TasksEditFragment.this.mTaskId));
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public void onPostExecute(Integer num) {
                        TasksAlertWork.updateAlertNotification(TasksEditFragment.this.getActivity().getApplicationContext());
                        if (!TasksEditFragment.this.mTablet) {
                            TasksEditFragment.this.getActivity().finish();
                        } else {
                            TasksEditFragment.this.mTabletTasksControllerFragment.onTaskDeleted(num.intValue() == 1);
                        }
                    }
                }.execute(new Void[0]);
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.8
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                TasksEditFragment.this.isDeleteDialogOpen = false;
            }
        }).show();
    }

    private void initDoneButton(View view, LayoutInflater layoutInflater) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        this.mToolbar = toolbar;
        toolbar.setBackgroundResource(R.color.toolbar_background_color);
        if (this.mTablet) {
            this.mToolbar.setVisibility(8);
            return;
        }
        View viewInflate = layoutInflater.inflate(R.layout.toolbar_save_button, (ViewGroup) null);
        this.mToolbar.setNavigationIcon(R.drawable.ic_delete_reminder);
        this.mToolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                TasksListItem taskListItem = TasksEditFragment.this.getTaskListItem();
                if (taskListItem == null || !TasksEditFragment.this.updateDatabase) {
                    TasksEditFragment.this.mActivity.finish();
                } else {
                    TasksEditFragment.this.showSaveTaskDialog(taskListItem);
                }
            }
        });
        viewInflate.findViewById(R.id.action_save).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                TasksEditFragment.this.mCurrentTime.setToNow();
                Time time = new SafeTime(TasksEditFragment.this.mCurrentTime);
                time.set(0, 0, 0, TasksEditFragment.this.mCurrentTime.monthDay, TasksEditFragment.this.mCurrentTime.month, TasksEditFragment.this.mCurrentTime.year);
                Time time2 = new SafeTime(TasksEditFragment.this.mCurrentTime);
                if (!TasksEditFragment.this.mDueDateCheckbox.isChecked() || !TasksEditFragment.this.mReminderCheckbox.isChecked()) {
                    if (!TasksEditFragment.this.mDueDateCheckbox.isChecked() || TasksEditFragment.this.mReminderCheckbox.isChecked()) {
                        if (TasksEditFragment.this.mDueDateCheckbox.isChecked() || !TasksEditFragment.this.mReminderCheckbox.isChecked()) {
                            if (TasksEditFragment.this.mDueDateCheckbox.isChecked() || TasksEditFragment.this.mReminderCheckbox.isChecked()) {
                                return;
                            }
                            TasksEditFragment.this.doSaveTasks();
                            return;
                        }
                        if (TasksEditFragment.this.mSaveDueDate.before(time)) {
                            TasksEditFragment.this.showCheckDialog(true);
                            return;
                        } else if (TasksEditFragment.this.mSaveCurrentTime.before(time2)) {
                            TasksEditFragment.this.showCheckDialog(false);
                            return;
                        } else {
                            TasksEditFragment.this.doSaveTasks();
                            return;
                        }
                    }
                    if (TasksEditFragment.this.mSaveDueDate.before(time)) {
                        TasksEditFragment.this.showCheckDialog(true);
                        return;
                    } else {
                        TasksEditFragment.this.doSaveTasks();
                        return;
                    }
                }
                if (TasksEditFragment.this.mSaveDueDate.before(time)) {
                    TasksEditFragment.this.showCheckDialog(true);
                } else if (TasksEditFragment.this.mSaveCurrentTime.before(time2)) {
                    TasksEditFragment.this.showCheckDialog(false);
                } else {
                    TasksEditFragment.this.doSaveTasks();
                }
            }
        });
        this.mToolbar.addView(viewInflate);
        this.mToolbar.inflateMenu(R.menu.tasks_edit_actions);
        this.mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.13
            @Override // androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                TasksEditFragment.this.onOptionsItemSelected(menuItem);
                return true;
            }
        });
        if (this.mTablet) {
            return;
        }
        this.newTask = getActivity().getIntent().getLongExtra(TasksEditActivity.TASK_ID, -1L) == -1;
        this.mToolbar.getMenu().findItem(R.id.action_delete).setVisible(true ^ this.newTask);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.sonymobile.calendar.tasks.activity.TasksEditFragment$14] */
    public void updateTaskAccount(final TasksListItem tasksListItem) {
        new AsyncTask<Void, Long, Long>() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditFragment.14
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Long doInBackground(Void... voidArr) {
                return Long.valueOf(TasksDatabase.updateAccountForTask(TasksEditFragment.this.getActivity(), tasksListItem));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Long l) {
                if (l.longValue() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra(TasksEditActivity.TASK_ID_FROM_EDIT, l);
                    TasksEditFragment.this.getActivity().setResult(0, intent);
                    ArrayList<Long> taskIds = TasksItemProvider.getInstance().getTaskIds();
                    for (int i = 0; i < taskIds.size(); i++) {
                        if (taskIds.get(i).longValue() == TasksEditFragment.this.mTaskItem.taskId) {
                            taskIds.add(i, l);
                            break;
                        }
                    }
                    TasksItemProvider.getInstance().setTaskListIds(taskIds);
                }
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void taskHasBeenEdited() {
        if (!this.mTablet || this.mTaskId == -2 || this.currentlyEditingTask) {
            return;
        }
        this.currentlyEditingTask = true;
        getActivity().invalidateOptionsMenu();
    }

    private class DateClickListener implements View.OnClickListener {
        private long mLastClick;
        private Time mTime;

        public DateClickListener(Time time) {
            this.mTime = time;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (this.mLastClick + TasksEditFragment.CLICK_DELAY > jCurrentTimeMillis) {
                return;
            }
            this.mLastClick = jCurrentTimeMillis;
            if (!UiUtils.isSub320dpScreen(TasksEditFragment.this.getContext())) {
                TasksEditFragment.this.taskHasBeenEdited();
                String str = view.getId() == TasksEditFragment.this.mDueDateButton.getId() ? TasksEditFragment.DUE_DATE_PICKER_TAG : TasksEditFragment.REMINDER_PICKER_TAG;
                DatePickerDialog datePickerDialogNewInstance = DatePickerDialog.newInstance(TasksEditFragment.this.new DateSetListener(view.getId()), this.mTime.year, this.mTime.month, this.mTime.monthDay, 2);
                if (TasksEditFragment.this.getActivity().isFinishing()) {
                    return;
                }
                datePickerDialogNewInstance.show(TasksEditFragment.this.getActivity().getSupportFragmentManager(), str);
                return;
            }
            String str2 = view.getId() == TasksEditFragment.this.mDueDateButton.getId() ? TasksEditFragment.DUE_DATE_PICKER_OLD_TAG : TasksEditFragment.REMINDER_PICKER_OLD_TAG;
            DatePickerDialogFragment datePickerDialogFragmentNewInstance = DatePickerDialogFragment.newInstance(this.mTime.year, this.mTime.month, this.mTime.monthDay, TasksEditFragment.this.mOldDialogListener, view.getId());
            if (TasksEditFragment.this.getActivity().isFinishing()) {
                return;
            }
            datePickerDialogFragmentNewInstance.show(TasksEditFragment.this.getActivity().getSupportFragmentManager(), str2);
        }
    }

    private class DateSetListener implements DatePickerDialog.OnDateSetListener {
        private int viewId;

        public DateSetListener(int i) {
            this.viewId = i;
        }

        @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.OnDateSetListener
        public void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
            TasksEditFragment.this.performDateSet(this.viewId, i, i2, i3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDateSet(int i, int i2, int i3, int i4) {
        Time time = new SafeTime();
        time.set(this.mReminderDate.second, this.mReminderDate.minute, this.mReminderDate.hour, i4, i3, i2);
        this.mCurrentTime.setToNow();
        if (i == this.mDueDateButton.getId()) {
            Time time2 = new SafeTime(this.mCurrentTime);
            time2.set(0, 0, 0, this.mCurrentTime.monthDay, this.mCurrentTime.month, this.mCurrentTime.year);
            if (time.before(time2)) {
                AlertDialog alertDialog = this.mCheckDialog;
                if (alertDialog == null || !alertDialog.isShowing()) {
                    showCheckDialog(true);
                    return;
                }
                return;
            }
            long jNormalize = time.normalize(true);
            this.mDueDateButton.setText(getFormattedDate(jNormalize));
            this.mReminderDateButton.setText(getFormattedDate(jNormalize));
            this.mDueDate.set(time);
            this.mReminderDate.set(time);
            this.mSaveDueDate.set(0, 0, 0, i4, i3, i2);
            this.mSaveCurrentTime.set(time);
            this.mSaveCurrentTime.second = 0;
            return;
        }
        if (i == this.mReminderDateButton.getId()) {
            Time time3 = new SafeTime();
            time3.set(0, 0, 0, i4, i3, i2);
            if ((this.mDueDateCheckbox.isChecked() && time3.after(this.mDueDate)) || time.before(this.mCurrentTime)) {
                AlertDialog alertDialog2 = this.mCheckDialog;
                if (alertDialog2 == null || !alertDialog2.isShowing()) {
                    showCheckDialog(false);
                    return;
                }
                return;
            }
            long jNormalize2 = time.normalize(true);
            this.reminderMillis = jNormalize2;
            this.mReminderDateButton.setText(getFormattedDate(jNormalize2));
            this.mReminderDate.set(time);
            this.mSaveDueDate.set(0, 0, 0, i4, i3, i2);
            this.mSaveCurrentTime.set(time);
            this.mSaveCurrentTime.second = 0;
            if (this.mDueDateCheckbox.isChecked()) {
                return;
            }
            this.mDueDate.set(this.mReminderDate);
        }
    }

    private class TimeClickListener implements View.OnClickListener {
        private long mLastClick;
        private Time mTime;

        public TimeClickListener(Time time) {
            this.mTime = time;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (this.mLastClick + TasksEditFragment.CLICK_DELAY > jCurrentTimeMillis) {
                return;
            }
            this.mLastClick = jCurrentTimeMillis;
            TasksEditFragment.this.taskHasBeenEdited();
            TimePickerDialogFragment timePickerDialogFragmentNewInstance = TimePickerDialogFragment.newInstance(this.mTime.hour, this.mTime.minute, TasksEditFragment.this.mTimePickerListener, view.getId());
            if (TasksEditFragment.this.getActivity().isFinishing()) {
                return;
            }
            timePickerDialogFragmentNewInstance.show(TasksEditFragment.this.getActivity().getSupportFragmentManager(), TasksEditFragment.TIME_PICKER_TAG);
        }
    }

    private class CheckBoxStatusChange implements CompoundButton.OnCheckedChangeListener {
        private CheckBoxStatusChange() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (compoundButton.isPressed()) {
                compoundButton.playSoundEffect(0);
            }
            int id = compoundButton.getId();
            if (id == R.id.due_date_checkbox) {
                if (z != TasksEditFragment.this.mOriginalDueDateValue) {
                    TasksEditFragment.this.taskHasBeenEdited();
                }
                if (z) {
                    Button button = TasksEditFragment.this.mDueDateButton;
                    TasksEditFragment tasksEditFragment = TasksEditFragment.this;
                    button.setText(tasksEditFragment.getFormattedDate(tasksEditFragment.mDueDate.normalize(true)));
                    TasksEditFragment.this.mDueDateButton.setEnabled(true);
                    return;
                }
                TasksEditFragment.this.mDueDateButton.setText(R.string.task_edit_reminder_time_btn_none_txt);
                TasksEditFragment.this.mDueDateButton.setEnabled(false);
                return;
            }
            if (id != R.id.reminder_checkbox) {
                return;
            }
            if (z != TasksEditFragment.this.mOriginalReminderValue) {
                TasksEditFragment.this.taskHasBeenEdited();
            }
            if (z) {
                Button button2 = TasksEditFragment.this.mReminderDateButton;
                TasksEditFragment tasksEditFragment2 = TasksEditFragment.this;
                button2.setText(tasksEditFragment2.getFormattedDate(tasksEditFragment2.mReminderDate.normalize(true)));
                Button button3 = TasksEditFragment.this.mReminderTimeButton;
                TasksEditFragment tasksEditFragment3 = TasksEditFragment.this;
                button3.setText(tasksEditFragment3.getFormattedTime(tasksEditFragment3.mReminderDate.toMillis(true)));
                TasksEditFragment.this.mReminderDateButton.setEnabled(true);
                TasksEditFragment.this.mReminderTimeButton.setEnabled(true);
                return;
            }
            TasksEditFragment.this.mReminderDateButton.setText(R.string.task_edit_reminder_time_btn_none_txt);
            TasksEditFragment.this.mReminderTimeButton.setText(R.string.task_edit_reminder_time_btn_none_txt);
            TasksEditFragment.this.mReminderDateButton.setEnabled(false);
            TasksEditFragment.this.mReminderTimeButton.setEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TasksListItem getTasksListItemFromUI() {
        long jTransDateTime2Date = TimeFormatterUtil.transDateTime2Date(this.mDueDate.toMillis(false), TimeZone.getTimeZone("UTC"));
        long millis = this.mReminderDate.toMillis(false);
        TasksListItem tasksListItem = new TasksListItem();
        tasksListItem.taskName = this.mTasksSubject.getText().toString().trim();
        tasksListItem.taskDescription = this.mTasksDescription.getText().toString().trim();
        tasksListItem.completed = this.mTaskItem.completed;
        tasksListItem.priority = this.mTasksPriority.getSelectedItemPosition();
        tasksListItem.accountId = this.mAccountList.get(this.mAccountIndex).id;
        tasksListItem.accountName = this.mAccountList.get(this.mAccountIndex).name;
        if (!this.mDueDateCheckbox.isChecked()) {
            jTransDateTime2Date = 0;
        }
        tasksListItem.dueDate = jTransDateTime2Date;
        tasksListItem.hasReminder = this.mReminderCheckbox.isChecked();
        if (!this.mReminderCheckbox.isChecked()) {
            millis = 0;
        }
        tasksListItem.reminderDate = millis;
        if (this.newTask && isNewTabletTask()) {
            if (this.mReminderCheckbox.isChecked()) {
                this.mReminderDirtyType = TasksDatabase.DirtyType.INSERT;
            }
        } else {
            tasksListItem.taskId = this.mTaskItem.taskId;
        }
        this.updateDatabase = true;
        return tasksListItem;
    }

    private TasksListItem updateTasksListItemFromUI() {
        if (this.mTaskItem.taskName == null || this.mTaskItem.taskDescription == null) {
            return null;
        }
        TasksListItem tasksListItemFromUI = getTasksListItemFromUI();
        this.updateDatabase = false;
        if (!this.mTaskItem.taskName.equals(tasksListItemFromUI.taskName)) {
            this.updateDatabase = true;
        }
        if (!this.mTaskItem.taskDescription.equals(tasksListItemFromUI.taskDescription)) {
            this.updateDatabase = true;
        }
        if (this.mTaskItem.priority != tasksListItemFromUI.priority) {
            this.updateDatabase = true;
        }
        if (this.mTaskItem.accountId != tasksListItemFromUI.accountId) {
            this.updateDatabase = true;
        }
        if (this.mTaskItem.dueDate != tasksListItemFromUI.dueDate) {
            this.updateDatabase = true;
        }
        if (this.mTaskItem.reminderDate != tasksListItemFromUI.reminderDate) {
            this.updateDatabase = true;
            if (tasksListItemFromUI.reminderDate == 0) {
                this.mReminderDirtyType = TasksDatabase.DirtyType.DELETE;
            } else if (this.mTaskItem.reminderDate == 0) {
                this.mReminderDirtyType = TasksDatabase.DirtyType.INSERT;
            } else {
                this.mReminderDirtyType = TasksDatabase.DirtyType.UPDATE;
            }
        }
        return tasksListItemFromUI;
    }

    private void loadTasksData() {
        getLoaderManager().restartLoader(2, null, this.mTasksMetadataLoaderListener);
    }

    private void initiateViewsForNewTask() {
        int i;
        this.mDueDate.setToNow();
        this.mReminderDate.setToNow();
        this.mTasksPriority.setSelection(1, true);
        String sharedPreference = Utils.getSharedPreference(getActivity(), GeneralPreferences.KEY_DEFAULT_TASKS_ACCOUNT, (String) null);
        if (sharedPreference == null) {
            i = 0;
        } else {
            i = -1;
            Iterator<TaskAccount> it = this.mAccountList.iterator();
            while (it.hasNext()) {
                i++;
                if (it.next().name.equals(sharedPreference)) {
                    break;
                }
            }
        }
        this.mTasksAccount.setSelection(i, true);
        this.mDueDateButton.setText(getFormattedDate(this.mDueDate.normalize(true)));
        this.mTasksSubject.setText("");
        this.mTasksDescription.setText("");
        this.mReminderDateButton.setText(getFormattedDate(this.mReminderDate.normalize(true)));
        this.mReminderTimeButton.setText(getFormattedTime(this.mReminderDate.toMillis(true)));
        this.mTaskItem.priority = this.mTasksPriority.getSelectedItemPosition();
        this.mTaskItem.dueDate = this.mDueDate.toMillis(false);
        this.mTaskItem.reminderDate = this.mReminderDate.toMillis(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initiateViewsForExistingTask() {
        String str = this.mTaskItem.taskName;
        String str2 = this.mTaskItem.taskDescription;
        this.subjectWatcher.setOriginalText(str);
        this.mTasksSubject.setText(str);
        this.mTasksSubject.setSelection(str.length());
        this.descriptionWatcher.setOriginalText(str2);
        this.mTasksDescription.setText(this.mTaskItem.taskDescription);
        EditText editText = this.mTasksDescription;
        editText.setSelection(editText.getText().length());
        int i = this.mTaskItem.priority;
        this.mDefaultPrioritySelection = i;
        this.mTasksPriority.setSelection(i, true);
        int accountId = getAccountId(this.mTaskItem.accountId);
        this.originalAccountId = accountId;
        this.mTasksAccount.setSelection(accountId, true);
        if (this.mTaskItem.dueDate != 0) {
            this.mDueDate.set(this.mTaskItem.dueDate);
            this.mOriginalDueDateValue = true;
            this.mDueDateCheckbox.setChecked(true);
            this.mDueDateButton.setText(getFormattedDate(this.mDueDate.normalize(true)));
            this.mDueDateButton.setEnabled(true);
        } else {
            this.mDueDate.set(this.mTaskItem.reminderDate);
            this.mOriginalDueDateValue = false;
            this.mDueDateCheckbox.setChecked(false);
        }
        if (this.mTaskItem.hasReminder) {
            this.mReminderDate.set(this.mTaskItem.reminderDate);
            this.mReminderDateButton.setText(getFormattedDate(this.mReminderDate.normalize(true)));
            this.mReminderTimeButton.setText(getFormattedTime(this.mReminderDate.toMillis(true)));
            this.mSaveDueDate.set(0, 0, 0, this.mReminderDate.monthDay, this.mReminderDate.month, this.mReminderDate.year);
            this.mSaveCurrentTime.set(this.mReminderDate);
            this.mSaveCurrentTime.second = 0;
            this.mOriginalReminderValue = true;
            this.mReminderDateButton.setEnabled(true);
            this.mReminderCheckbox.setChecked(true);
            this.mReminderTimeButton.setEnabled(true);
            return;
        }
        this.mReminderDate.setToNow();
        this.mOriginalReminderValue = false;
        this.mReminderCheckbox.setChecked(false);
    }

    private class TaskEditTextWatcher implements TextWatcher {
        String originalText;

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        private TaskEditTextWatcher() {
        }

        public void setOriginalText(String str) {
            this.originalText = str;
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String str;
            if (TextUtils.isEmpty(charSequence) || (str = this.originalText) == null || str.equals(charSequence.toString())) {
                return;
            }
            TasksEditFragment.this.taskHasBeenEdited();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCheckDialog(boolean z) {
        this.mCheckDialog = new AlertDialog.Builder(this.mActivity, R.style.AlertDialogTheme).setTitle(R.string.task_edit_date_select_confirm_dialog_title_txt).setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) null).setMessage(z ? R.string.task_edit_due_date_select_confirm_dialog_message_txt : R.string.task_edit_reminder_date_select_confirm_dialog_message_txt).show();
    }

    private int getAccountId(int i) {
        for (TaskAccount taskAccount : this.mAccountList) {
            if (taskAccount.id == i) {
                return this.mAccountList.indexOf(taskAccount);
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getFormattedDate(long j) {
        return getActivity() == null ? "" : TimeFormatterUtil.getFormattedDate(getActivity(), j, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getFormattedTime(long j) {
        return getActivity() == null ? "" : TimeFormatterUtil.getFormattedTime(getActivity(), j);
    }

    private static class ColorServiceResultHandler implements IAsyncServiceResultHandler {
        private Drawable drawable;

        public ColorServiceResultHandler(Drawable drawable) {
            this.drawable = drawable;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() == 1) {
                this.drawable.setColorFilter(((Integer) obj).intValue(), PorterDuff.Mode.SRC);
            }
        }
    }
}
