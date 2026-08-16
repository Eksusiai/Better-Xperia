package com.sonymobile.calendar.tasks.activity;

import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.tasks.TasksDatabase;
import com.sonymobile.calendar.tasks.TasksItemProvider;
import com.sonymobile.calendar.tasks.alerts.TasksAlertUtils;
import com.sonymobile.calendar.tasks.alerts.TasksAlertWork;
import com.sonymobile.calendar.tasks.widget.SearchViewAdapter;
import com.sonymobile.provider.TasksContract;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class TasksNotificationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SearchViewAdapter mAdapter;
    private Button mCompletedButton;
    private long[] mNotifyTasksIdList;

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mNotifyTasksIdList = getIntent().getLongArrayExtra(TasksAlertUtils.TASK_IDS_KEY);
        this.mAdapter = new SearchViewAdapter(this, R.layout.tasks_list_childs, null);
        if (getLoaderManager().getLoader(2) == null) {
            getLoaderManager().initLoader(2, null, this);
        } else {
            getLoaderManager().restartLoader(2, null, this);
        }
        setContentView(R.layout.notification_view_fragment);
        initActionBar();
        ListView listView = (ListView) findViewById(R.id.search_view_list);
        listView.setDividerHeight(0);
        listView.setAdapter((ListAdapter) this.mAdapter);
        ((Button) findViewById(R.id.dismiss_all_alert_tasks)).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksNotificationActivity.1
            /* JADX WARN: Type inference failed for: r3v1, types: [com.sonymobile.calendar.tasks.activity.TasksNotificationActivity$1$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.activity.TasksNotificationActivity.1.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Integer doInBackground(Void... voidArr) {
                        return Integer.valueOf(TasksDatabase.dimissAllFiredAlarms(TasksNotificationActivity.this.getApplicationContext()));
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                TasksNotificationActivity.this.onBackPressed();
            }
        });
        Button button = (Button) findViewById(R.id.complete_all_alert_tasks);
        this.mCompletedButton = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksNotificationActivity.2
            /* JADX WARN: Type inference failed for: r3v1, types: [com.sonymobile.calendar.tasks.activity.TasksNotificationActivity$2$1] */
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                new AsyncTask<Void, Void, Integer>() { // from class: com.sonymobile.calendar.tasks.activity.TasksNotificationActivity.2.1
                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // android.os.AsyncTask
                    public Integer doInBackground(Void... voidArr) {
                        return Integer.valueOf(TasksDatabase.completeAllFiredAlarms(TasksNotificationActivity.this.getApplicationContext()));
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        });
        ((NotificationManager) getSystemService("notification")).cancel(TasksAlertUtils.EXPIRED_GROUP_NOTIFICATION_ID);
        for (long j : this.mNotifyTasksIdList) {
            TasksDatabase.dismissTask(this, j);
        }
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setBackgroundResource(R.color.toolbar_background_color);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        TasksAlertWork.updateAlertNotification(getApplicationContext());
        super.onPause();
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (this.mAdapter.onContextItemSelected(menuItem)) {
            return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext());
        cursorLoader.setUri(TasksContract.TasksAlerts.CONTENT_URI);
        StringBuffer stringBuffer = new StringBuffer("(");
        for (int i2 = 0; i2 < this.mNotifyTasksIdList.length; i2++) {
            stringBuffer.append("'").append(this.mNotifyTasksIdList[i2]).append("',");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        stringBuffer.append(")");
        cursorLoader.setSelection("task_id IN " + ((Object) stringBuffer));
        return cursorLoader;
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        boolean z;
        if (cursor != null) {
            getSupportActionBar().setTitle(getString(R.string.alert_number_in_action_bar, new Object[]{Integer.toString(cursor.getCount())}));
            this.mAdapter.changeCursor(cursor);
            int count = cursor.getCount();
            if (count == 0) {
                z = true;
            } else if (count == 1) {
                this.mAdapter.setHasOnlyOneReminderTask(true);
                z = true;
            } else {
                ArrayList<Long> arrayList = new ArrayList<>();
                cursor.moveToFirst();
                z = true;
                while (!cursor.isAfterLast()) {
                    arrayList.add(Long.valueOf(cursor.getLong(cursor.getColumnIndex("task_id"))));
                    if (cursor.getInt(cursor.getColumnIndex("complete")) == 0) {
                        z = false;
                    }
                    cursor.moveToNext();
                }
                TasksItemProvider.getInstance().setTaskListIds(arrayList);
            }
            this.mCompletedButton.setEnabled(!z);
        }
    }
}
