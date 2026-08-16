package com.sonymobile.calendar.tasks.activity;
import com.sonymobile.calendar.SafeTime;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.sonymobile.calendar.ActionBarControllerBase;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.FABFrameLayout;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.NavigationListener;
import com.sonymobile.calendar.Navigator;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.ViewType;
import com.sonymobile.calendar.tablet.TabletTasksControllerFragment;
import com.sonymobile.calendar.tasks.SearchProvider;
import com.sonymobile.calendar.tasks.SyncManager;
import com.sonymobile.calendar.tasks.TasksDatabase;
import com.sonymobile.calendar.tasks.TasksItemProvider;
import com.sonymobile.calendar.tasks.alerts.TasksAlertReceiver;
import com.sonymobile.calendar.tasks.alerts.TasksAlertWork;
import com.sonymobile.calendar.tasks.model.TaskAccountsData;
import com.sonymobile.calendar.tasks.model.TasksListData;
import com.sonymobile.calendar.tasks.model.TasksListItem;
import com.sonymobile.calendar.tasks.settings.SettingsActivity;
import com.sonymobile.calendar.tasks.settings.TaskAccountManager;
import com.sonymobile.calendar.tasks.utils.TaskConfig;
import com.sonymobile.calendar.tasks.utils.TimeFormatterUtil;
import com.sonymobile.calendar.tasks.widget.TasksListAdapter;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/* JADX INFO: loaded from: classes2.dex */
public class TasksListFragment extends Fragment implements Navigator, CompoundButton.OnCheckedChangeListener {
    private static final String BUNDLE_KEY_EVENT_ID = "key_event_id";
    private static final long DEFAULT_TASK_ID = 0;
    private static final long DELETE_ALL_COMPLETED_TASKS = -1;
    private static final long DOUBLE_CLICK_TRESHOLD = 500;
    private static final int FIRST_CHILD_INDEX = 2;
    private static final long INVALID_TASK_ID = -1;
    private static final int INVALID_TASK_INDEX = -1;
    private static final String IS_CREATING_TASK = "isCreatingTask";
    private static final int LIST_VIEW_LOADER = 0;
    private static final long NEW_TASK_ID = -2;
    private static final int NEW_TASK_INDEX = 0;
    public static final int NOTIFICATION_VIEW_LOADER = 2;
    private static final String SEARCH_QUERY = "searchQuery";
    public static final int SEARCH_VIEW_LOADER = 1;
    private static final String SELECTED_TASK_ID = "selected_task_id";
    private static final String SELECTED_TASK_INDEX = "selectedIndex";
    private static final int SORT_BY_DUE_DATE = 0;
    private static final int SORT_BY_PRIORITY = 1;
    private static final String SORT_VIEW_VISIBILITY = "sort_view_visibility";
    public static final String TASKS_PACKAGE_NAME = "com.sonymobile.tasks";
    public static final int TASK_CREATION_CANCELED = 105;
    public static final int TASK_CREATION_STARTED = 106;
    private static final String TASK_DELETE_DIALOG_ID_KEY = "TASK_DELETE_DIALOG_ID_KEY";
    private static final String TASK_DELETE_DIALOG_TITLE_KEY = "TASK_DELETE_DIALOG_TITLE_KEY";
    public static final int TASK_LIST_UPDATED = 104;
    public static final int TASK_SELECTED = 103;
    public static final int TASK_STATE_CHANGED = 101;
    public static final int TASK_UPDATED = 102;
    public static final int VISIBLE = 1;
    private boolean display;
    private boolean hasOnlyLocalAccount;
    private ActionBarControllerBase mActionBarController;
    private AppCompatActivity mActivity;
    private TasksListAdapter mAdapter;
    private String mDeleteTitle;
    private long mDetailBackTaskId;
    private ExpandableListView mExpandableListView;
    private FABFrameLayout mFabButton;
    private long mLastClickOnTaskItem;
    private String mLocaleDefault;
    private View mNewTaskView;
    private TabletTasksControllerFragment mTabletTasksControllerFragment;
    private View noTaskAlertArea;
    private String orderBy;
    private TasksSearchHelper searchHelper;
    private boolean showSort;
    private int sortType;
    private String[] sqlArgs;
    private String sqlWhere;
    private View tasksListFragmentView;
    private long mDeleteId = -1;
    private int[] childrenCountOfDate = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] childrenCountOfPriority = {0, 0, 0};
    private int[] isExpandOfDate = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private int[] isExpandOfPriority = {1, 1, 1};
    private int activeTasks = 0;
    private long mSelectedTaskId = -1;
    private int mSelectedTaskIndex = 0;
    private boolean needLoadSettings = true;
    private boolean needLoadSortType = true;
    private boolean needRestartLoaderManager = false;
    private boolean needReloadLanguage = false;
    private boolean mSearch = false;
    private boolean mCreatingTask = false;
    private boolean mTabletMode = false;
    private boolean isSearching = false;
    private boolean isSortViewDisplayed = false;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TasksContract.ACTION_TASK_REMINDER)) {
                TasksAlertReceiver.prepareService(context, intent);
            }
        }
    };
    View.OnClickListener fabClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            TasksListFragment.this.launchCreateTaskActivity();
        }
    };
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.10
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (TasksListFragment.this.isAdded()) {
                TasksListFragment.this.getLoaderManager().restartLoader(0, null, TasksListFragment.this.loaderCallback);
            }
        }
    };
    private final LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.18
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            if (TasksListFragment.this.mSearch) {
                return SearchProvider.getInstance().configureLoader(TasksListFragment.this.getExistingActivity());
            }
            return new CursorLoader(TasksListFragment.this.getExistingActivity(), TasksContract.Tasks.CONTENT_URI, null, TasksListFragment.this.sqlWhere, TasksListFragment.this.sqlArgs, TasksListFragment.this.orderBy);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Vector vector;
            Vector vector2;
            Vector vector3;
            Vector vector4;
            Vector vector5;
            Vector vector6;
            Vector vector7;
            Vector vector8;
            Vector vector9;
            int i;
            Vector vector10;
            Vector<Vector<TasksListItem>> vector11;
            int i2;
            int i3;
            Vector vector12;
            Vector vector13;
            Cursor cursor2 = cursor;
            if (TasksListFragment.this.needReloadLanguage) {
                TasksListData.resetStringsAfterLocaleChange();
                TasksListFragment.this.needReloadLanguage = false;
            }
            ArrayList arrayList = new ArrayList();
            Vector<Vector<TasksListItem>> vector14 = new Vector<>();
            vector14.clear();
            Vector vector15 = new Vector();
            Vector vector16 = new Vector();
            Vector vector17 = new Vector();
            Vector vector18 = new Vector();
            Vector vector19 = new Vector();
            Vector vector20 = new Vector();
            Vector vector21 = new Vector();
            Vector vector22 = new Vector();
            Vector vector23 = new Vector();
            Vector vector24 = new Vector();
            Vector vector25 = new Vector();
            Vector vector26 = new Vector();
            Vector vector27 = new Vector();
            Vector vector28 = new Vector();
            if (cursor2 == null || !cursor.moveToFirst()) {
                vector = vector22;
                vector2 = vector23;
                vector3 = vector25;
                vector4 = vector16;
                vector5 = vector21;
                vector6 = vector17;
                vector7 = vector20;
                vector8 = vector18;
                vector9 = vector19;
                i = 0;
            } else {
                int count = cursor.getCount();
                int columnIndexOrThrow = cursor2.getColumnIndexOrThrow("_id");
                Vector vector29 = vector16;
                int columnIndexOrThrow2 = cursor2.getColumnIndexOrThrow(TasksContract.TasksColumns.TASKLIST_ID);
                Vector vector30 = vector17;
                int columnIndexOrThrow3 = cursor2.getColumnIndexOrThrow("subject");
                Vector vector31 = vector18;
                int columnIndexOrThrow4 = cursor2.getColumnIndexOrThrow(TasksContract.TasksColumns.BODY_DATA);
                Vector vector32 = vector25;
                int columnIndexOrThrow5 = cursor2.getColumnIndexOrThrow(TasksContract.TasksColumns.DUE_DATE);
                Vector vector33 = vector19;
                int columnIndexOrThrow6 = cursor2.getColumnIndexOrThrow(TasksContract.TasksColumns.IMPORTANCE);
                Vector vector34 = vector20;
                int columnIndexOrThrow7 = cursor2.getColumnIndexOrThrow("complete");
                Vector vector35 = vector21;
                int columnIndexOrThrow8 = cursor2.getColumnIndexOrThrow("deleted");
                Vector vector36 = vector22;
                int columnIndexOrThrow9 = cursor2.getColumnIndexOrThrow(TasksContract.TaskListsColumns.TASKLIST_COLOR);
                Vector vector37 = vector23;
                while (true) {
                    TasksListItem tasksListItem = new TasksListItem();
                    Vector vector38 = vector27;
                    Vector vector39 = vector26;
                    tasksListItem.taskId = cursor2.getLong(columnIndexOrThrow);
                    tasksListItem.accountId = cursor2.getInt(columnIndexOrThrow2);
                    tasksListItem.accountColor = cursor2.getInt(columnIndexOrThrow9);
                    tasksListItem.taskName = cursor2.getString(columnIndexOrThrow3);
                    tasksListItem.taskDescription = cursor2.getString(columnIndexOrThrow4);
                    tasksListItem.dueDate = cursor2.getLong(columnIndexOrThrow5);
                    tasksListItem.priority = cursor2.getInt(columnIndexOrThrow6);
                    tasksListItem.completed = cursor2.getInt(columnIndexOrThrow7);
                    tasksListItem.deleted = cursor2.getInt(columnIndexOrThrow8);
                    if (TasksListFragment.this.sortType != 0) {
                        vector = vector36;
                        vector26 = vector39;
                        vector27 = vector38;
                        i3 = columnIndexOrThrow;
                        columnIndexOrThrow2 = columnIndexOrThrow2;
                        vector5 = vector35;
                        vector2 = vector37;
                        columnIndexOrThrow3 = columnIndexOrThrow3;
                        vector7 = vector34;
                        columnIndexOrThrow4 = columnIndexOrThrow4;
                        vector9 = vector33;
                        columnIndexOrThrow6 = columnIndexOrThrow6;
                        Vector vector40 = vector32;
                        columnIndexOrThrow7 = columnIndexOrThrow7;
                        vector8 = vector31;
                        columnIndexOrThrow8 = columnIndexOrThrow8;
                        vector6 = vector30;
                        columnIndexOrThrow9 = columnIndexOrThrow9;
                        vector4 = vector29;
                        columnIndexOrThrow5 = columnIndexOrThrow5;
                        vector3 = vector40;
                        if (TasksListFragment.this.sortType == 1) {
                            if (tasksListItem.completed == 1) {
                                vector27.add(tasksListItem);
                            }
                            if (tasksListItem.priority == 2) {
                                vector15.add(tasksListItem);
                            } else if (tasksListItem.priority == 1) {
                                vector6.add(tasksListItem);
                            } else if (tasksListItem.priority == 0) {
                                vector26.add(tasksListItem);
                            }
                        }
                    } else {
                        if (tasksListItem.completed == 1) {
                            vector27 = vector38;
                            vector27.add(tasksListItem);
                            i3 = columnIndexOrThrow;
                            int iCompareWithThisWeek = TimeFormatterUtil.compareWithThisWeek(tasksListItem.dueDate);
                            if (iCompareWithThisWeek == -1 || iCompareWithThisWeek == 2) {
                                vector15.add(tasksListItem);
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                columnIndexOrThrow2 = columnIndexOrThrow2;
                                vector3 = vector32;
                                vector5 = vector35;
                                columnIndexOrThrow3 = columnIndexOrThrow3;
                                columnIndexOrThrow7 = columnIndexOrThrow7;
                                vector8 = vector31;
                                vector7 = vector34;
                                columnIndexOrThrow4 = columnIndexOrThrow4;
                                columnIndexOrThrow8 = columnIndexOrThrow8;
                                vector6 = vector30;
                                vector9 = vector33;
                                columnIndexOrThrow6 = columnIndexOrThrow6;
                                columnIndexOrThrow9 = columnIndexOrThrow9;
                                vector4 = vector29;
                                columnIndexOrThrow5 = columnIndexOrThrow5;
                            }
                        } else {
                            vector27 = vector38;
                            i3 = columnIndexOrThrow;
                        }
                        switch (TimeFormatterUtil.getDayInCurrentWeek(tasksListItem.dueDate)) {
                            case -4:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                columnIndexOrThrow2 = columnIndexOrThrow2;
                                vector5 = vector35;
                                columnIndexOrThrow3 = columnIndexOrThrow3;
                                vector7 = vector34;
                                columnIndexOrThrow4 = columnIndexOrThrow4;
                                vector9 = vector33;
                                columnIndexOrThrow6 = columnIndexOrThrow6;
                                vector12 = vector32;
                                columnIndexOrThrow7 = columnIndexOrThrow7;
                                vector8 = vector31;
                                columnIndexOrThrow8 = columnIndexOrThrow8;
                                vector6 = vector30;
                                columnIndexOrThrow9 = columnIndexOrThrow9;
                                vector4 = vector29;
                                vector13 = vector28;
                                vector13.add(tasksListItem);
                                break;
                            case -3:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                columnIndexOrThrow2 = columnIndexOrThrow2;
                                vector5 = vector35;
                                columnIndexOrThrow3 = columnIndexOrThrow3;
                                vector7 = vector34;
                                columnIndexOrThrow4 = columnIndexOrThrow4;
                                vector9 = vector33;
                                columnIndexOrThrow6 = columnIndexOrThrow6;
                                vector12 = vector32;
                                columnIndexOrThrow7 = columnIndexOrThrow7;
                                vector8 = vector31;
                                columnIndexOrThrow8 = columnIndexOrThrow8;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector4.add(tasksListItem);
                                vector13 = vector28;
                                break;
                            case -2:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                columnIndexOrThrow2 = columnIndexOrThrow2;
                                vector5 = vector35;
                                columnIndexOrThrow3 = columnIndexOrThrow3;
                                vector7 = vector34;
                                columnIndexOrThrow4 = columnIndexOrThrow4;
                                vector9 = vector33;
                                columnIndexOrThrow6 = columnIndexOrThrow6;
                                vector12 = vector32;
                                columnIndexOrThrow7 = columnIndexOrThrow7;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector6.add(tasksListItem);
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case -1:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                columnIndexOrThrow2 = columnIndexOrThrow2;
                                vector5 = vector35;
                                columnIndexOrThrow3 = columnIndexOrThrow3;
                                vector7 = vector34;
                                columnIndexOrThrow4 = columnIndexOrThrow4;
                                vector9 = vector33;
                                columnIndexOrThrow6 = columnIndexOrThrow6;
                                vector12 = vector32;
                                vector8 = vector31;
                                vector8.add(tasksListItem);
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case 0:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                columnIndexOrThrow2 = columnIndexOrThrow2;
                                vector5 = vector35;
                                columnIndexOrThrow3 = columnIndexOrThrow3;
                                vector7 = vector34;
                                columnIndexOrThrow4 = columnIndexOrThrow4;
                                vector9 = vector33;
                                vector12 = vector32;
                                vector12.add(tasksListItem);
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case 1:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                columnIndexOrThrow2 = columnIndexOrThrow2;
                                vector5 = vector35;
                                columnIndexOrThrow3 = columnIndexOrThrow3;
                                vector7 = vector34;
                                vector9 = vector33;
                                vector9.add(tasksListItem);
                                vector12 = vector32;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case 2:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                columnIndexOrThrow2 = columnIndexOrThrow2;
                                vector5 = vector35;
                                vector7 = vector34;
                                vector7.add(tasksListItem);
                                vector9 = vector33;
                                vector12 = vector32;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case 3:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                vector5 = vector35;
                                vector5.add(tasksListItem);
                                vector7 = vector34;
                                vector9 = vector33;
                                vector12 = vector32;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case 4:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                vector.add(tasksListItem);
                                vector5 = vector35;
                                vector7 = vector34;
                                vector9 = vector33;
                                vector12 = vector32;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case 5:
                                vector2 = vector37;
                                vector26 = vector39;
                                vector2.add(tasksListItem);
                                vector = vector36;
                                vector5 = vector35;
                                vector7 = vector34;
                                vector9 = vector33;
                                vector12 = vector32;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case 6:
                                vector26 = vector39;
                                vector24.add(tasksListItem);
                                vector = vector36;
                                vector2 = vector37;
                                vector5 = vector35;
                                vector7 = vector34;
                                vector9 = vector33;
                                vector12 = vector32;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            case 7:
                                vector26 = vector39;
                                vector26.add(tasksListItem);
                                vector = vector36;
                                vector2 = vector37;
                                vector5 = vector35;
                                vector7 = vector34;
                                vector9 = vector33;
                                vector12 = vector32;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                            default:
                                vector = vector36;
                                vector2 = vector37;
                                vector26 = vector39;
                                vector5 = vector35;
                                vector7 = vector34;
                                vector9 = vector33;
                                vector12 = vector32;
                                vector8 = vector31;
                                vector6 = vector30;
                                vector4 = vector29;
                                vector13 = vector28;
                                break;
                        }
                        vector3 = vector12;
                        vector28 = vector13;
                    }
                    if (cursor.moveToNext()) {
                        columnIndexOrThrow5 = columnIndexOrThrow5;
                        columnIndexOrThrow6 = columnIndexOrThrow6;
                        vector33 = vector9;
                        vector29 = vector4;
                        columnIndexOrThrow9 = columnIndexOrThrow9;
                        columnIndexOrThrow4 = columnIndexOrThrow4;
                        vector34 = vector7;
                        vector30 = vector6;
                        columnIndexOrThrow8 = columnIndexOrThrow8;
                        columnIndexOrThrow3 = columnIndexOrThrow3;
                        vector35 = vector5;
                        vector31 = vector8;
                        columnIndexOrThrow7 = columnIndexOrThrow7;
                        columnIndexOrThrow2 = columnIndexOrThrow2;
                        vector32 = vector3;
                        vector36 = vector;
                        vector37 = vector2;
                        columnIndexOrThrow = i3;
                        cursor2 = cursor;
                    } else {
                        i = count;
                    }
                }
            }
            if (TasksListFragment.this.sortType == 0) {
                int[] iArr = new int[13];
                vector10 = vector27;
                vector11 = vector14;
                i2 = i;
                TasksListFragment.this.combineSets(vector11, vector15, iArr, 0);
                TasksListFragment.this.combineSets(vector11, vector4, iArr, 1);
                TasksListFragment.this.combineSets(vector11, vector6, iArr, 2);
                TasksListFragment.this.combineSets(vector11, vector8, iArr, 3);
                TasksListFragment.this.combineSets(vector11, vector9, iArr, 4);
                TasksListFragment.this.combineSets(vector11, vector7, iArr, 5);
                TasksListFragment.this.combineSets(vector11, vector5, iArr, 6);
                TasksListFragment.this.combineSets(vector11, vector, iArr, 7);
                TasksListFragment.this.combineSets(vector11, vector2, iArr, 8);
                TasksListFragment.this.combineSets(vector11, vector24, iArr, 9);
                TasksListFragment.this.combineSets(vector11, vector3, iArr, 10);
                TasksListFragment.this.combineSets(vector11, vector26, iArr, 11);
                TasksListFragment.this.combineSets(vector11, vector28, iArr, 12);
                TasksListFragment.this.loadGroupData(arrayList, iArr);
                TasksListFragment.this.mAdapter.updateItems(vector11, arrayList);
                TasksListFragment.this.mAdapter.notifyDataSetChanged();
                TasksListFragment tasksListFragment = TasksListFragment.this;
                tasksListFragment.expandAndCollapseGroups(tasksListFragment.isExpandOfDate);
            } else {
                vector10 = vector27;
                vector11 = vector14;
                i2 = i;
                if (TasksListFragment.this.sortType == 1) {
                    int[] iArr2 = new int[3];
                    TasksListFragment.this.combineSets(vector11, vector15, iArr2, 0);
                    TasksListFragment.this.combineSets(vector11, vector6, iArr2, 1);
                    TasksListFragment.this.combineSets(vector11, vector26, iArr2, 2);
                    TasksListFragment.this.loadGroupData(arrayList, iArr2);
                    TasksListFragment.this.mAdapter.updateItems(vector11, arrayList);
                    TasksListFragment.this.mAdapter.notifyDataSetChanged();
                    TasksListFragment tasksListFragment2 = TasksListFragment.this;
                    tasksListFragment2.expandAndCollapseGroups(tasksListFragment2.isExpandOfPriority);
                }
            }
            int i4 = i2;
            TasksListFragment.this.updateUI(i4);
            TasksListFragment.this.activeTasks = i4 - vector10.size();
            TasksListFragment.this.storeTaskIds(vector11);
            Time time = new SafeTime(Utils.getTimeZone(TasksListFragment.this.getExistingActivity(), null));
            time.set(Utils.getDisplayTime());
            TasksListFragment.this.updateActionBar(time);
            TasksListFragment.this.mHandler.sendMessage(TasksListFragment.this.mHandler.obtainMessage(104));
        }
    };
    private Handler mHandler = new Handler() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.19
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (TasksListFragment.this.mAdapter == null) {
            }
            int i = message.what;
            long jLongValue = message.obj != null ? ((Long) message.obj).longValue() : 0L;
            switch (i) {
                case 101:
                    if (TasksListFragment.this.mTabletMode && !TasksListFragment.this.mCreatingTask) {
                        TasksListFragment tasksListFragment = TasksListFragment.this;
                        tasksListFragment.mSelectedTaskId = tasksListFragment.mAdapter.getTopMostTaskId();
                        TasksListFragment tasksListFragment2 = TasksListFragment.this;
                        tasksListFragment2.mSelectedTaskIndex = tasksListFragment2.mAdapter.getTopMostTaskIndex();
                        TasksListFragment.this.mExpandableListView.setItemChecked(TasksListFragment.this.mSelectedTaskIndex, true);
                        TasksListFragment tasksListFragment3 = TasksListFragment.this;
                        tasksListFragment3.launchTaskDetailActivity(tasksListFragment3.mSelectedTaskId);
                        break;
                    }
                    break;
                case 102:
                    if (TasksListFragment.this.mTabletMode) {
                        TasksListFragment tasksListFragment4 = TasksListFragment.this;
                        tasksListFragment4.launchTaskDetailActivity(tasksListFragment4.mSelectedTaskId);
                        break;
                    }
                    break;
                case 103:
                    if (TasksListFragment.this.mTabletMode) {
                        TasksListFragment.this.mSelectedTaskId = jLongValue;
                        TasksListFragment tasksListFragment5 = TasksListFragment.this;
                        tasksListFragment5.mSelectedTaskIndex = tasksListFragment5.getIndexForId(jLongValue);
                        TasksListFragment.this.launchTaskDetailActivity(jLongValue);
                    } else {
                        TasksListFragment.this.launchTaskDetailActivity(jLongValue);
                    }
                    break;
                case 104:
                    if (TasksListFragment.this.mTabletMode) {
                        if (!TasksListFragment.this.mAdapter.isTaskPresent(TasksListFragment.this.mSelectedTaskId) && !TasksListFragment.this.mCreatingTask) {
                            TasksListFragment tasksListFragment6 = TasksListFragment.this;
                            tasksListFragment6.mSelectedTaskId = tasksListFragment6.mAdapter.getTopMostTaskId();
                            TasksListFragment tasksListFragment7 = TasksListFragment.this;
                            tasksListFragment7.mSelectedTaskIndex = tasksListFragment7.mAdapter.getTopMostTaskIndex();
                            TasksListFragment.this.mExpandableListView.setItemChecked(TasksListFragment.this.mSelectedTaskIndex, true);
                            TasksListFragment tasksListFragment8 = TasksListFragment.this;
                            tasksListFragment8.launchTaskDetailActivity(tasksListFragment8.mSelectedTaskId);
                        } else if (TasksListFragment.this.mSelectedTaskId >= 0) {
                            TasksListFragment tasksListFragment9 = TasksListFragment.this;
                            tasksListFragment9.mSelectedTaskIndex = tasksListFragment9.getIndexForId(tasksListFragment9.mSelectedTaskId);
                            TasksListFragment.this.mExpandableListView.setItemChecked(TasksListFragment.this.mSelectedTaskIndex, true);
                        }
                        break;
                    }
                    break;
                case 105:
                    if (TasksListFragment.this.mTabletMode) {
                        TasksListFragment.this.mCreatingTask = false;
                        if (!TasksListFragment.this.mAdapter.isTaskPresent(TasksListFragment.this.mSelectedTaskId)) {
                            TasksListFragment tasksListFragment10 = TasksListFragment.this;
                            tasksListFragment10.mSelectedTaskId = tasksListFragment10.mAdapter.getTopMostTaskId();
                            TasksListFragment tasksListFragment11 = TasksListFragment.this;
                            tasksListFragment11.mSelectedTaskIndex = tasksListFragment11.mAdapter.getTopMostTaskIndex();
                        } else {
                            TasksListFragment tasksListFragment12 = TasksListFragment.this;
                            tasksListFragment12.mSelectedTaskIndex = tasksListFragment12.getIndexForId(tasksListFragment12.mSelectedTaskId);
                        }
                        if (TasksListFragment.this.mAdapter.getGroupIsExpandedForId(TasksListFragment.this.mSelectedTaskId)) {
                            TasksListFragment.this.mExpandableListView.setItemChecked(TasksListFragment.this.mSelectedTaskIndex, true);
                            TasksListFragment tasksListFragment13 = TasksListFragment.this;
                            tasksListFragment13.launchTaskDetailActivity(tasksListFragment13.mSelectedTaskId);
                        }
                        TasksListFragment.this.mExpandableListView.setItemChecked(0, false);
                        TasksListFragment.this.setTemporaryTaskVisibility(false);
                        break;
                    }
                    break;
                case 106:
                    if (TasksListFragment.this.mTabletMode) {
                        TasksListFragment.this.mCreatingTask = true;
                        TasksListFragment.this.mExpandableListView.setItemChecked(0, true);
                        TasksListFragment.this.mSelectedTaskId = TasksListFragment.NEW_TASK_ID;
                        TasksListFragment.this.mSelectedTaskIndex = 0;
                        TasksListFragment.this.setTemporaryTaskVisibility(true);
                        break;
                    }
                    break;
            }
        }
    };

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return null;
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return 0L;
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
    }

    static /* synthetic */ int access$412(TasksListFragment tasksListFragment, int i) {
        int i2 = tasksListFragment.mSelectedTaskIndex + i;
        tasksListFragment.mSelectedTaskIndex = i2;
        return i2;
    }

    static /* synthetic */ int access$420(TasksListFragment tasksListFragment, int i) {
        int i2 = tasksListFragment.mSelectedTaskIndex - i;
        tasksListFragment.mSelectedTaskIndex = i2;
        return i2;
    }

    public static TasksListFragment newInstance(long j) {
        TasksListFragment tasksListFragment = new TasksListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_EVENT_ID, j);
        tasksListFragment.setArguments(bundle);
        return tasksListFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (AppCompatActivity) activity;
        setHasOptionsMenu(true);
        TasksListAdapter tasksListAdapter = new TasksListAdapter(getExistingActivity());
        this.mAdapter = tasksListAdapter;
        tasksListAdapter.setOnCheckedChangeListener(this);
        readSettings(this.needLoadSettings);
        if (getLoaderManager().getLoader(0) == null) {
            getLoaderManager().initLoader(0, null, this.loaderCallback);
        } else {
            getLoaderManager().restartLoader(0, null, this.loaderCallback);
        }
        this.mActionBarController = ((LaunchActivity) activity).getActionBarController();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!Utils.isPackageEnabled(TASKS_PACKAGE_NAME, getExistingActivity())) {
            getExistingActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            return;
        }
        String string = null;
        if (bundle != null && bundle.containsKey(SEARCH_QUERY)) {
            string = bundle.getString(SEARCH_QUERY);
        }
        this.searchHelper = new TasksSearchHelper(getExistingActivity(), string);
        this.sortType = Utils.getSharedPreference(getExistingActivity(), GeneralPreferences.KEY_DEFAULT_SORT_TYPE, 0);
        this.mTabletMode = getResources().getBoolean(R.bool.tablet_mode);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TasksContract.ACTION_TASK_REMINDER);
        intentFilter.addDataScheme("content");
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.tasks_list_fragment, viewGroup, false);
        this.tasksListFragmentView = viewInflate;
        this.noTaskAlertArea = viewInflate.findViewById(R.id.no_task_alert_area);
        FABFrameLayout fABFrameLayout = (FABFrameLayout) this.tasksListFragmentView.findViewById(R.id.fab_button);
        this.mFabButton = fABFrameLayout;
        fABFrameLayout.setOnClickListener(this.fabClickListener);
        return this.tasksListFragmentView;
    }

    public void setContentVisibility(int i) {
        this.tasksListFragmentView.setVisibility(i);
    }

    /* JADX WARN: Type inference failed for: r1v13, types: [com.sonymobile.calendar.tasks.activity.TasksListFragment$3] */
    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle == null) {
            new AsyncTask<Void, Void, Void>() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.3
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... voidArr) {
                    if (!TasksDatabase.createTaskListsInDatabaseIfNeeded(TasksListFragment.this.getExistingActivity()) || TasksListFragment.this.mTabletTasksControllerFragment == null) {
                        return null;
                    }
                    TasksListFragment.this.mTabletTasksControllerFragment.updateAccounts(TasksListFragment.this.getExistingActivity());
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
        ExpandableListView expandableListView = (ExpandableListView) getExistingActivity().findViewById(android.R.id.list);
        this.mExpandableListView = expandableListView;
        if (expandableListView == null) {
            return;
        }
        expandableListView.setGroupIndicator(null);
        this.mExpandableListView.setDividerHeight(0);
        restoreState(bundle);
        LayoutInflater layoutInflater = (LayoutInflater) getExistingActivity().getSystemService("layout_inflater");
        if (getResources().getBoolean(R.bool.tablet_mode)) {
            View viewInflate = layoutInflater.inflate(R.layout.tasks_list_childs, (ViewGroup) this.mExpandableListView, false);
            this.mNewTaskView = viewInflate;
            viewInflate.findViewById(R.id.action_icon).setEnabled(false);
            this.mExpandableListView.addHeaderView(this.mNewTaskView);
            if (this.mCreatingTask) {
                Handler handler = this.mHandler;
                handler.dispatchMessage(handler.obtainMessage(106, -1L));
            } else {
                setTemporaryTaskVisibility(false);
            }
        }
        this.mExpandableListView.setAdapter(this.mAdapter);
        this.mExpandableListView.setOnKeyListener(new View.OnKeyListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.4
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() != 0 || i != 62) {
                    return false;
                }
                TasksListFragment.this.mAdapter.onSpacePressed();
                return true;
            }
        });
        this.mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.5
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                if (TasksListFragment.this.mAdapter.getGroupPositionForId(TasksListFragment.this.mSelectedTaskId) <= i) {
                    if (TasksListFragment.this.mAdapter.getGroupPositionForId(TasksListFragment.this.mSelectedTaskId) == i && !TasksListFragment.this.mCreatingTask) {
                        TasksListFragment.this.mExpandableListView.setItemChecked(TasksListFragment.this.mAdapter.getIndexForId(TasksListFragment.this.mSelectedTaskId), true);
                    }
                } else {
                    TasksListFragment tasksListFragment = TasksListFragment.this;
                    TasksListFragment.access$412(tasksListFragment, tasksListFragment.mAdapter.getChildrenCount(i));
                    TasksListFragment.this.mExpandableListView.setItemChecked(TasksListFragment.this.mSelectedTaskIndex, true);
                }
                TasksListFragment.this.mAdapter.setGroupIsExpanded(true, Integer.valueOf(i));
                if (TasksListFragment.this.sortType == 0) {
                    int[] iArr = TasksListFragment.this.isExpandOfDate;
                    TasksListFragment tasksListFragment2 = TasksListFragment.this;
                    iArr[tasksListFragment2.getCategoryPosFromGroupPos(i, tasksListFragment2.isExpandOfDate)] = 1;
                } else if (TasksListFragment.this.sortType == 1) {
                    int[] iArr2 = TasksListFragment.this.isExpandOfPriority;
                    TasksListFragment tasksListFragment3 = TasksListFragment.this;
                    iArr2[tasksListFragment3.getCategoryPosFromGroupPos(i, tasksListFragment3.isExpandOfPriority)] = 1;
                }
            }
        });
        this.mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.6
            @Override // android.widget.ExpandableListView.OnGroupCollapseListener
            public void onGroupCollapse(int i) {
                if (TasksListFragment.this.mAdapter.getGroupPositionForId(TasksListFragment.this.mSelectedTaskId) <= i) {
                    if (TasksListFragment.this.mAdapter.getGroupPositionForId(TasksListFragment.this.mSelectedTaskId) == i && !TasksListFragment.this.mCreatingTask) {
                        TasksListFragment.this.mExpandableListView.setItemChecked(TasksListFragment.this.mSelectedTaskIndex, false);
                    }
                } else {
                    TasksListFragment tasksListFragment = TasksListFragment.this;
                    TasksListFragment.access$420(tasksListFragment, tasksListFragment.mAdapter.getChildrenCount(i));
                    TasksListFragment.this.mExpandableListView.setItemChecked(TasksListFragment.this.mSelectedTaskIndex, true);
                }
                TasksListFragment.this.mAdapter.setGroupIsExpanded(false, Integer.valueOf(i));
                if (TasksListFragment.this.sortType == 0) {
                    int[] iArr = TasksListFragment.this.isExpandOfDate;
                    TasksListFragment tasksListFragment2 = TasksListFragment.this;
                    iArr[tasksListFragment2.getCategoryPosFromGroupPos(i, tasksListFragment2.isExpandOfDate)] = -1;
                } else if (TasksListFragment.this.sortType == 1) {
                    int[] iArr2 = TasksListFragment.this.isExpandOfPriority;
                    TasksListFragment tasksListFragment3 = TasksListFragment.this;
                    iArr2[tasksListFragment3.getCategoryPosFromGroupPos(i, tasksListFragment3.isExpandOfPriority)] = -1;
                }
            }
        });
        this.mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.7
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView2, View view, int i, int i2, long j) {
                if (System.currentTimeMillis() - TasksListFragment.this.mLastClickOnTaskItem < TasksListFragment.DOUBLE_CLICK_TRESHOLD) {
                    return true;
                }
                TasksListFragment.this.mLastClickOnTaskItem = System.currentTimeMillis();
                TasksListFragment.this.mSelectedTaskIndex = expandableListView2.getFlatListPosition(ExpandableListView.getPackedPositionForChild(i, i2));
                TasksListFragment.this.mAdapter.setSelectedTaskId(i, i2);
                if (TasksListFragment.this.mCreatingTask) {
                    TasksListFragment.this.mHandler.sendMessage(TasksListFragment.this.mHandler.obtainMessage(105, Long.valueOf(j)));
                }
                TasksListFragment.this.mHandler.sendMessage(TasksListFragment.this.mHandler.obtainMessage(103, Long.valueOf(j)));
                return true;
            }
        });
        this.mExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.8
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                String string;
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.action_icon);
                if (checkBox != null && checkBox.getTag(R.id.tag_task_id) != null) {
                    long jLongValue = ((Long) checkBox.getTag(R.id.tag_task_id)).longValue();
                    long expandableListPosition = ((ExpandableListView) adapterView).getExpandableListPosition(i);
                    if (ExpandableListView.getPackedPositionType(expandableListPosition) == 1) {
                        TasksListItem tasksListItem = (TasksListItem) TasksListFragment.this.mAdapter.getChild(ExpandableListView.getPackedPositionGroup(expandableListPosition), ExpandableListView.getPackedPositionChild(expandableListPosition));
                        if (TextUtils.isEmpty(tasksListItem.taskName)) {
                            string = TasksListFragment.this.getExistingActivity().getString(R.string.no_title_label);
                        } else {
                            string = tasksListItem.taskName;
                        }
                    } else {
                        string = "";
                    }
                    TasksListFragment.this.doDelete(jLongValue, string);
                }
                return true;
            }
        });
        ActionBarControllerBase actionBarController = ((LaunchActivity) getExistingActivity()).getActionBarController();
        this.mActionBarController = actionBarController;
        actionBarController.onFragmentAttached(getClass().getName());
        getExistingActivity().getContentResolver().registerContentObserver(CalendarContract.Calendars.CONTENT_URI, true, this.mContentObserver);
        Toolbar toolbar = this.mActionBarController.getToolbar();
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.9
                @Override // androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
                public boolean onMenuItemClick(MenuItem menuItem) {
                    return TasksListFragment.this.onOptionsItemSelected(menuItem);
                }
            });
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        getExistingActivity().getContentResolver().unregisterContentObserver(this.mContentObserver);
        super.onDestroy();
    }

    private void restoreState(Bundle bundle) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mSelectedTaskId = arguments.getLong(BUNDLE_KEY_EVENT_ID);
        }
        if (bundle == null) {
            return;
        }
        this.mLocaleDefault = bundle.getString("localeAtStart", Locale.getDefault().getDisplayLanguage());
        this.isExpandOfDate = bundle.getIntArray("isExpandOfDate");
        this.isExpandOfPriority = bundle.getIntArray("isExpandOfPriority");
        this.childrenCountOfDate = bundle.getIntArray("childrenCountOfDate");
        this.childrenCountOfPriority = bundle.getIntArray("childrenCountOfPriority");
        this.mCreatingTask = bundle.getBoolean(IS_CREATING_TASK, false);
        this.mSelectedTaskId = bundle.getLong(SELECTED_TASK_ID);
        int i = bundle.getInt(SELECTED_TASK_INDEX);
        this.mSelectedTaskIndex = i;
        ExpandableListView expandableListView = this.mExpandableListView;
        if (expandableListView != null) {
            expandableListView.setItemChecked(i, true);
        }
        this.mDeleteId = bundle.getLong(TASK_DELETE_DIALOG_ID_KEY);
        this.mDeleteTitle = bundle.getString(TASK_DELETE_DIALOG_TITLE_KEY);
        this.isSortViewDisplayed = bundle.getBoolean(SORT_VIEW_VISIBILITY);
        String str = this.mDeleteTitle;
        if (str != null) {
            doDelete(this.mDeleteId, str);
        }
        if (this.isSortViewDisplayed) {
            popSortMenu();
        }
    }

    /* JADX WARN: Code duplicated, block: B:11:0x002d  */
    /* JADX WARN: Code duplicated, block: B:14:0x0037  */
    /* JADX WARN: Code duplicated, block: B:16:0x003a  */
    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        boolean z = false;
        if (this.mLocaleDefault != null) {
            String displayLanguage = Locale.getDefault().getDisplayLanguage();
            if (!this.mLocaleDefault.equals(displayLanguage)) {
                this.mLocaleDefault = displayLanguage;
                this.needReloadLanguage = true;
                z = true;
            }
        } else {
            this.mLocaleDefault = Locale.getDefault().getDisplayLanguage();
            this.needReloadLanguage = true;
        }
        boolean z2 = false;
        if (this.needLoadSettings) {
            readSettings(this.needLoadSortType);
            z2 = this.needRestartLoaderManager || z;
        }
        if (z2) {
            getLoaderManager().restartLoader(0, null, this.loaderCallback);
        }
        this.hasOnlyLocalAccount = TaskAccountsData.hasOnlyLocalAccount(getExistingActivity());
        PermissionUtils.reportFullyDrawnIfPermitted((AppCompatActivity) getContext());
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (SearchProvider.getInstance().hasActiveQuery()) {
            this.searchHelper.onSearchSubmit(SearchProvider.getInstance().getQueryString());
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        this.needLoadSettings = true;
        this.needLoadSortType = false;
        super.onPause();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
        if (time == null) {
            time = new SafeTime();
            time.setToNow();
            time.normalize(false);
        }
        AppCompatActivity existingActivity = getExistingActivity();
        if (existingActivity instanceof LaunchActivity) {
            ((NavigationListener) existingActivity).onViewNavigated(time, existingActivity.getString(R.string.alert_number_in_action_bar, new Object[]{Integer.valueOf(this.activeTasks)}), ViewType.TASK);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putIntArray("isExpandOfDate", this.isExpandOfDate);
        bundle.putIntArray("isExpandOfPriority", this.isExpandOfPriority);
        bundle.putIntArray("childrenCountOfDate", this.childrenCountOfDate);
        bundle.putIntArray("childrenCountOfPriority", this.childrenCountOfPriority);
        bundle.putInt("sortType", this.sortType);
        Utils.setSharedPreference(getExistingActivity(), GeneralPreferences.KEY_DEFAULT_SORT_TYPE, this.sortType);
        bundle.putString("localeAtStart", this.mLocaleDefault);
        bundle.putLong(SELECTED_TASK_ID, this.mSelectedTaskId);
        bundle.putInt(SELECTED_TASK_INDEX, this.mSelectedTaskIndex);
        bundle.putBoolean(IS_CREATING_TASK, this.mCreatingTask);
        String searchQuery = this.searchHelper.getSearchQuery();
        if (searchQuery != null) {
            bundle.putString(SEARCH_QUERY, searchQuery);
        }
        bundle.putLong(TASK_DELETE_DIALOG_ID_KEY, this.mDeleteId);
        bundle.putString(TASK_DELETE_DIALOG_TITLE_KEY, this.mDeleteTitle);
        bundle.putBoolean(SORT_VIEW_VISIBILITY, this.isSortViewDisplayed);
        super.onSaveInstanceState(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            this.mDetailBackTaskId = intent.getLongExtra(TasksEditActivity.DETAIL_BACK_TASK_ID, 0L);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.tasks_general_actions, menu);
        this.searchHelper.handleSearchItem(menu);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.sort).setVisible(this.showSort && !this.isSearching);
        menu.findItem(R.id.sync).setVisible((this.hasOnlyLocalAccount || this.isSearching) ? false : true);
    }

    public void updateSearchMode(boolean z) {
        this.mSearch = z;
        if (getLoaderManager().getLoader(0) == null) {
            getLoaderManager().initLoader(0, null, this.loaderCallback);
        } else {
            getLoaderManager().restartLoader(0, null, this.loaderCallback);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.clear) {
            clearCompletedTasks();
            return true;
        }
        if (itemId == R.id.settings) {
            launchTaskListSettingsActivity();
            return true;
        }
        if (itemId == R.id.sort) {
            popSortMenu();
            this.isSortViewDisplayed = true;
            return true;
        }
        if (itemId == R.id.sync) {
            syncAllAccounts();
            return true;
        }
        return false;
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        Long l = (Long) compoundButton.getTag(R.id.tag_task_id);
        this.searchHelper.setFlagChangeQuery(false);
        if (!this.display && z && l.longValue() == this.mSelectedTaskId) {
            this.mExpandableListView.setItemChecked(this.mSelectedTaskIndex, false);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(101));
        }
    }

    private void clearCompletedTasks() {
        new DeleteTaskFromDatabase().execute(-1L);
    }

    public void launchCreateTaskActivity() {
        if (this.mTabletMode) {
            this.mCreatingTask = true;
            if (this.mTabletTasksControllerFragment != null) {
                this.noTaskAlertArea.setVisibility(8);
                this.mTabletTasksControllerFragment.setEmptyTaskAreaVisibility(0);
                this.mTabletTasksControllerFragment.updateSelectedTaskId(NEW_TASK_ID);
                return;
            }
            return;
        }
        startActivity(new Intent(getExistingActivity(), (Class<?>) TasksEditActivity.class));
    }

    private void popSortMenu() {
        new AlertDialog.Builder(getExistingActivity(), R.style.AlertDialogTheme).setTitle(R.string.task_list_sort_type_dialog_title_txt).setSingleChoiceItems(getExistingActivity().getResources().getStringArray(R.array.task_default_sorting_option), this.sortType, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                TasksListFragment.this.setSortType(i);
                TasksListFragment.this.isSortViewDisplayed = false;
                dialogInterface.dismiss();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.12
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                TasksListFragment.this.isSortViewDisplayed = false;
                dialogInterface.cancel();
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.11
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                TasksListFragment.this.isSortViewDisplayed = false;
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSortType(int i) {
        this.isExpandOfDate = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        this.isExpandOfPriority = new int[]{1, 1, 1};
        this.sortType = i;
        if (i == 0) {
            this.orderBy = TasksContract.TasksColumns.DUE_DATE;
        }
        Utils.setSharedPreference(getExistingActivity(), GeneralPreferences.KEY_DEFAULT_SORT_TYPE, this.sortType);
        getLoaderManager().destroyLoader(0);
        getLoaderManager().initLoader(0, null, this.loaderCallback);
    }

    /* JADX WARN: Type inference failed for: r2v0, types: [com.sonymobile.calendar.tasks.activity.TasksListFragment$14] */
    private void syncAllAccounts() {
        final View viewFindViewById = getExistingActivity().findViewById(R.id.sync_progress_banner);
        if (viewFindViewById != null) {
            viewFindViewById.setVisibility(0);
        }
        new AsyncTask<Void, Void, Void>() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.14
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... voidArr) {
                SyncManager.getInstance().performSync(TaskAccountManager.getInstance().getAccountLists(TasksListFragment.this.getExistingActivity()));
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void r2) {
                View view = viewFindViewById;
                if (view != null) {
                    view.setVisibility(8);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void launchTaskListSettingsActivity() {
        if (isAdded()) {
            startActivity(new Intent(getExistingActivity(), (Class<?>) SettingsActivity.class));
        } else {
            CalendarApplication.launchSettingsActivity();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchTaskDetailActivity(long j) {
        if (getExistingActivity() == null) {
            return;
        }
        if (this.mTabletMode) {
            if (this.mTabletTasksControllerFragment != null) {
                this.mExpandableListView.setItemChecked(this.mSelectedTaskIndex, true);
                this.mTabletTasksControllerFragment.updateSelectedTaskId(j);
                return;
            }
            return;
        }
        Intent intent = new Intent(getExistingActivity(), (Class<?>) TasksEditActivity.class);
        intent.putExtra(TasksEditActivity.TASK_ID, j);
        startActivityForResult(intent, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDelete(final long j, String str) {
        this.mDeleteId = j;
        this.mDeleteTitle = str;
        new AlertDialog.Builder(getExistingActivity(), R.style.AlertDialogTheme).setTitle(str).setMessage(getString(R.string.task_delete_confirm_dialog_title_txt)).setPositiveButton(getString(R.string.task_delete_confirm_dialog_btn_delete_txt), new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.17
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                new DeleteTaskFromDatabase().execute(Long.valueOf(j));
            }
        }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksListFragment.15
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                TasksListFragment.this.mDeleteTitle = null;
            }
        }).show();
    }

    private void readSettings(boolean z) {
        boolean displayCompletedTaskSettings = TaskConfig.getDisplayCompletedTaskSettings(getExistingActivity());
        this.needRestartLoaderManager = this.display != displayCompletedTaskSettings;
        this.display = displayCompletedTaskSettings;
        if (displayCompletedTaskSettings) {
            this.sqlWhere = "deleted=? and visible=?";
            this.sqlArgs = new String[]{"0", "1"};
        } else {
            this.sqlWhere = "deleted=? and visible=? and  (complete=? or complete is null)";
            this.sqlArgs = new String[]{"0", "1", "0"};
        }
        this.needLoadSettings = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUI(int i) {
        TabletTasksControllerFragment tabletTasksControllerFragment;
        int i2 = 0;
        boolean z = i == 0;
        ActionBar actionBar = getExistingActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(z ? getExistingActivity().getString(R.string.task_app_name_txt) : getExistingActivity().getString(R.string.task_list_actionbar_title_txt, new Object[]{Integer.valueOf(i)}));
        }
        this.noTaskAlertArea.setVisibility((!z || this.mCreatingTask) ? 8 : 0);
        if (this.mTabletMode && (tabletTasksControllerFragment = this.mTabletTasksControllerFragment) != null) {
            if (z && !this.mCreatingTask) {
                i2 = 8;
            }
            tabletTasksControllerFragment.setEmptyTaskAreaVisibility(i2);
        }
        this.showSort = !z;
        if (this.mSearch) {
            return;
        }
        getExistingActivity().invalidateOptionsMenu();
    }

    public void setTabletTasksControllerFragment(TabletTasksControllerFragment tabletTasksControllerFragment) {
        this.mTabletTasksControllerFragment = tabletTasksControllerFragment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void expandAndCollapseGroups(int[] iArr) {
        if (this.mExpandableListView == null) {
            return;
        }
        for (int i = 0; i < iArr.length; i++) {
            if (iArr[i] == 1) {
                this.mExpandableListView.expandGroup(getGroupPosFromCategoryPos(i, iArr));
            } else if (iArr[i] == -1) {
                this.mExpandableListView.collapseGroup(getGroupPosFromCategoryPos(i, iArr));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCategoryPosFromGroupPos(int i, int[] iArr) {
        int i2 = -1;
        while (i2 < iArr.length - 1 && i >= 0) {
            i2++;
            if (iArr[i2] != 0) {
                i--;
            }
        }
        return i2;
    }

    private int getGroupPosFromCategoryPos(int i, int[] iArr) {
        int i2 = -1;
        for (int i3 = 0; i3 <= i; i3++) {
            if (iArr[i3] != 0) {
                i2++;
            }
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadGroupData(List<CharSequence> list, int[] iArr) {
        int i = this.sortType;
        if (i == 0) {
            CharSequence[] groupsDate = TasksListData.getGroupsDate(getExistingActivity());
            int iMin = Math.min(groupsDate.length, this.isExpandOfDate.length);
            for (int i2 = 0; i2 < iMin; i2++) {
                if (iArr[i2] > 0) {
                    list.add(groupsDate[i2]);
                    if (this.childrenCountOfDate[i2] == 0) {
                        this.isExpandOfDate[i2] = 1;
                    }
                } else {
                    this.isExpandOfDate[i2] = 0;
                }
                this.childrenCountOfDate[i2] = iArr[i2];
            }
            return;
        }
        if (i != 1) {
            return;
        }
        CharSequence[] groupsPriority = TasksListData.getGroupsPriority(getExistingActivity());
        for (int i3 = 0; i3 < groupsPriority.length; i3++) {
            if (iArr[i3] > 0) {
                list.add(groupsPriority[i3]);
                if (this.childrenCountOfPriority[i3] == 0) {
                    this.isExpandOfPriority[i3] = 1;
                }
            } else {
                this.isExpandOfPriority[i3] = 0;
            }
            this.childrenCountOfPriority[i3] = iArr[i3];
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void combineSets(Vector<Vector<TasksListItem>> vector, Vector<TasksListItem> vector2, int[] iArr, int i) {
        if (vector2.size() > 0) {
            vector.add(vector2);
            iArr[i] = 1;
        } else {
            iArr[i] = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void storeTaskIds(Vector<Vector<TasksListItem>> vector) {
        ArrayList<Long> arrayList = new ArrayList<>();
        int iIndexOf = 0;
        int iIndexOf2 = 0;
        for (Vector<TasksListItem> vector2 : vector) {
            for (TasksListItem tasksListItem : vector2) {
                arrayList.add(Long.valueOf(tasksListItem.taskId));
                if (this.mDetailBackTaskId == tasksListItem.taskId) {
                    iIndexOf = vector.indexOf(vector2);
                    iIndexOf2 = vector2.indexOf(tasksListItem);
                }
            }
        }
        TasksItemProvider.getInstance().setTaskListIds(arrayList);
        if (this.mDetailBackTaskId > 0) {
            this.mExpandableListView.setSelectedChild(iIndexOf, iIndexOf2, true);
            this.mDetailBackTaskId = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTemporaryTaskVisibility(boolean z) {
        if (z) {
            this.mNewTaskView.findViewById(R.id.task_item).setVisibility(0);
        } else {
            this.mNewTaskView.findViewById(R.id.task_item).setVisibility(8);
        }
        setTaskName();
        if (!z && this.mAdapter.getGroupCount() == 0 && !this.mSearch) {
            this.noTaskAlertArea.setVisibility(0);
            TabletTasksControllerFragment tabletTasksControllerFragment = this.mTabletTasksControllerFragment;
            if (tabletTasksControllerFragment != null) {
                tabletTasksControllerFragment.setEmptyTaskAreaVisibility(8);
            }
        }
        getExistingActivity().invalidateOptionsMenu();
    }

    private void setTaskName() {
        ((TextView) this.mNewTaskView.findViewById(R.id.task_name)).setText(getExistingActivity().getString(R.string.tablet_task_new_task_temporary_name_txt));
        ((TextView) this.mNewTaskView.findViewById(R.id.task_subject)).setVisibility(8);
    }

    public boolean collapseSearchItem() {
        return this.searchHelper.collapseSearchItem();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getIndexForId(long j) {
        int indexForId = this.mAdapter.getIndexForId(j);
        if (indexForId >= 0) {
            return indexForId;
        }
        int topMostTaskIndex = this.mAdapter.getTopMostTaskIndex();
        if (topMostTaskIndex == -1) {
            return 2;
        }
        return topMostTaskIndex;
    }

    public int getCount() {
        TasksListAdapter tasksListAdapter = this.mAdapter;
        if (tasksListAdapter == null) {
            return 0;
        }
        return tasksListAdapter.getGroupCount();
    }

    public void onTasksDeleted(boolean z) {
        if (z) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(101));
        }
    }

    public void setIsSearching(boolean z) {
        AppCompatActivity existingActivity = getExistingActivity();
        if (existingActivity != null && (existingActivity instanceof LaunchActivity)) {
            ((LaunchActivity) existingActivity).allowDrawerOpening(!z);
        }
        this.isSearching = z;
    }

    public boolean isSearching() {
        return this.isSearching;
    }

    public void sendMessage(int i, Object obj) {
        Handler handler = this.mHandler;
        handler.dispatchMessage(handler.obtainMessage(i, obj));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AppCompatActivity getExistingActivity() {
        return getActivity() != null ? (AppCompatActivity) getActivity() : this.mActivity;
    }

    private class DeleteTaskFromDatabase extends AsyncTask<Long, Void, Boolean> {
        private DeleteTaskFromDatabase() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Long... lArr) {
            long jLongValue = lArr[0].longValue();
            if (jLongValue == -1) {
                TasksDatabase.deleteAllCompletedTasks(TasksListFragment.this.getExistingActivity());
            } else {
                TasksDatabase.deleteTask(TasksListFragment.this.getExistingActivity(), jLongValue);
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            TasksAlertWork.updateAlertNotification(TasksListFragment.this.getExistingActivity().getApplicationContext());
            TasksListFragment.this.onTasksDeleted(bool.booleanValue());
        }
    }
}
