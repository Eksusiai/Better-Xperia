package com.sonymobile.calendar.tasks.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.tasks.alerts.TasksDismissAlarmsService;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TasksEditActivity extends PermissionHandlerActivity {
    public static final String DETAIL_BACK_TASK_ID = "com.sonymobile.tasks.BackTaskId";
    public static final String TASK_ID = "com.sonymobile.tasks.TaskId";
    public static final String TASK_ID_FROM_EDIT = "com.sonymobile.tasks.EditedTaskId";
    private AlertDialog mConfirmDialog;
    private TasksEditFragment tasksEditFragment;

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent.getIntExtra(LaunchActivity.ARG_FROM_NOTIFICATION, 0) == 2) {
            ComponentName component = intent.getComponent();
            intent.setAction(null);
            intent.setClass(this, TasksDismissAlarmsService.class);
            startService(intent);
            intent.setComponent(component);
        }
        setContentView(R.layout.edit_tasks_fragment);
        if (Utils.getSharedPreference((Context) this, GeneralPreferences.KEY_ACCESS_PACKAGE_LIST, false)) {
            if (bundle == null) {
                this.tasksEditFragment = new TasksEditFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.edit_tasks, this.tasksEditFragment).commit();
            } else {
                this.tasksEditFragment = (TasksEditFragment) getSupportFragmentManager().findFragmentById(R.id.edit_tasks);
            }
            UiUtils.setNavigationBar(this);
            return;
        }
        AlertDialog alertDialogShow = new AlertDialog.Builder(this, R.style.AlertDialogTheme).setMessage(R.string.query_all_packages_permissions_description).setPositiveButton(R.string.clr_strings_button_title_ok_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.setSharedPreference((Context) TasksEditActivity.this, GeneralPreferences.KEY_ACCESS_PACKAGE_LIST, true);
                dialogInterface.dismiss();
                if (bundle == null) {
                    TasksEditActivity.this.tasksEditFragment = new TasksEditFragment();
                    TasksEditActivity.this.getSupportFragmentManager().beginTransaction().add(R.id.edit_tasks, TasksEditActivity.this.tasksEditFragment).commit();
                } else {
                    TasksEditActivity tasksEditActivity = TasksEditActivity.this;
                    tasksEditActivity.tasksEditFragment = (TasksEditFragment) tasksEditActivity.getSupportFragmentManager().findFragmentById(R.id.edit_tasks);
                }
                UiUtils.setNavigationBar(TasksEditActivity.this);
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                TasksEditActivity.this.finish();
            }
        }).show();
        this.mConfirmDialog = alertDialogShow;
        alertDialogShow.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksEditActivity.3
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                TasksEditActivity.this.finish();
            }
        });
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{new PermissionItem("android.permission.READ_CONTACTS", null, null, R.drawable.ic_contact, false), new PermissionItem("android.permission.WRITE_CONTACTS", null, null, R.drawable.ic_contact, false), PermissionUtils.getEssentialCalendarPermissionItem(this), new PermissionItem("android.permission.READ_CALENDAR", getString(R.string.calendar_permision_group_title), getString(R.string.calendar_permision_group_description), R.drawable.ic_calendar_event, true), new PermissionItem("android.permission.POST_NOTIFICATIONS", null, null, 0, true)};
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        TasksEditFragment tasksEditFragment = this.tasksEditFragment;
        if (tasksEditFragment != null) {
            tasksEditFragment.onBackPressed();
        }
    }
}
