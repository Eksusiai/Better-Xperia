package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sonymobile.calendar.ActionBarControllerBase;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tasks.activity.TasksEditActivity;
import com.sonymobile.calendar.tasks.activity.TasksEditFragment;
import com.sonymobile.calendar.tasks.activity.TasksListFragment;

/* JADX INFO: loaded from: classes2.dex */
public class TabletTasksControllerFragment extends ControllerFragment {
    private TasksEditFragment mTasksEditFragment;
    private TasksListFragment mTasksListFragment;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.tablet_layout_tasks, viewGroup, false);
        TasksListFragment tasksListFragment = (TasksListFragment) getChildFragmentManager().findFragmentById(R.id.tasksListFragment);
        this.mTasksListFragment = tasksListFragment;
        if (tasksListFragment == null) {
            Uri data = getActivity().getIntent().getData();
            long j = -3;
            if (data != null) {
                j = Long.parseLong(data.getLastPathSegment());
                getActivity().getIntent().setData(null);
            }
            this.mTasksListFragment = TasksListFragment.newInstance(j);
            getChildFragmentManager().beginTransaction().add(R.id.tasksListFragment, this.mTasksListFragment).commit();
        }
        this.mTasksListFragment.setTabletTasksControllerFragment(this);
        this.mTasksEditFragment = (TasksEditFragment) getChildFragmentManager().findFragmentById(R.id.tasksEditFragment);
        if (Utils.isTabletDevice(getActivity())) {
            if (this.mTasksEditFragment == null) {
                this.mTasksEditFragment = new TasksEditFragment();
                getChildFragmentManager().beginTransaction().add(R.id.tasksEditFragment, this.mTasksEditFragment).commit();
            }
            this.mTasksEditFragment.setTabletTasksControllerFragment(this);
        } else if (this.mTasksEditFragment != null) {
            getChildFragmentManager().beginTransaction().remove(this.mTasksEditFragment).commit();
        }
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ActionBarControllerBase actionBarController = ((LaunchActivity) getActivity()).getActionBarController();
        actionBarController.setToolbar(null);
        actionBarController.onFragmentAttached(getClass().getName());
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().invalidateOptionsMenu();
    }

    public void updateSelectedTaskId(long j) {
        if (j == -2) {
            this.mTasksListFragment.sendMessage(106, Long.valueOf(j));
        }
        this.mTasksEditFragment.updateTask(j);
        getActivity().getIntent().putExtra(TasksEditActivity.TASK_ID, j);
    }

    public void createTaskCompleted(long j) {
        this.mTasksListFragment.sendMessage(105, Long.valueOf(j));
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getExtras() == null || !intent.getExtras().containsKey(TasksEditActivity.TASK_ID)) {
            return;
        }
        intent.removeExtra(TasksEditActivity.TASK_ID);
    }

    public TasksListFragment getTasksListFragment() {
        return this.mTasksListFragment;
    }

    public void setEmptyTaskAreaVisibility(int i) {
        getView().findViewById(R.id.tasksEditFragment).setVisibility(i);
    }

    public void updateTaskSearch(boolean z) {
        this.mTasksListFragment.updateSearchMode(z);
    }

    public void onTaskDeleted(boolean z) {
        this.mTasksListFragment.onTasksDeleted(z);
    }

    public void updateAccounts(Context context) {
        this.mTasksEditFragment.updateAccounts(context);
    }

    public boolean collapseSearchItem() {
        return this.mTasksListFragment.collapseSearchItem();
    }
}
