package com.sonymobile.calendar.tasks.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tablet.TabletTasksControllerFragment;
import com.sonymobile.calendar.tasks.SearchProvider;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TasksSearchHelper {
    public static final String SEARCH_VIEW_TAG = "search_view_tag";
    private String inputQueryString;
    private AppCompatActivity mActivity;
    private MenuItem mSearchItem;
    private boolean mTablet;
    private SearchView searchView;
    private boolean mFlagChangeQuery = true;
    private SearchProvider searchProvider = SearchProvider.getInstance();

    public TasksSearchHelper(AppCompatActivity appCompatActivity, String str) {
        this.mTablet = false;
        this.mActivity = appCompatActivity;
        this.mTablet = appCompatActivity.getResources().getBoolean(R.bool.tablet_mode);
        this.inputQueryString = str;
    }

    public void onSearchSubmit(String str) {
        this.searchProvider.setQueryString(str);
        if (this.mTablet && this.searchProvider.hasActiveQuery()) {
            ((TabletTasksControllerFragment) this.mActivity.getSupportFragmentManager().findFragmentByTag(LaunchActivity.CURRENT_FRAGMENT)).updateTaskSearch(true);
            return;
        }
        updateVisibleFragment();
        TasksSearchViewFragment tasksSearchViewFragment = (TasksSearchViewFragment) this.mActivity.getSupportFragmentManager().findFragmentByTag(SEARCH_VIEW_TAG);
        if (tasksSearchViewFragment != null) {
            this.mActivity.getSupportLoaderManager().restartLoader(1, null, tasksSearchViewFragment);
        }
    }

    private void updateVisibleFragment() {
        FragmentManager supportFragmentManager = this.mActivity.getSupportFragmentManager();
        if (this.searchProvider.hasActiveQuery()) {
            Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag(SEARCH_VIEW_TAG);
            if (fragmentFindFragmentByTag != null && fragmentFindFragmentByTag.getClass().getName().equals(TasksSearchViewFragment.class.getName())) {
                ((TasksListFragment) supportFragmentManager.findFragmentByTag(LaunchActivity.CURRENT_FRAGMENT)).setContentVisibility(4);
                return;
            }
            if (fragmentFindFragmentByTag == null || !fragmentFindFragmentByTag.getClass().getName().equals(TasksSearchViewFragment.class.getName())) {
                fragmentFindFragmentByTag = new TasksSearchViewFragment();
            }
            supportFragmentManager.beginTransaction().add(R.id.content_frame, fragmentFindFragmentByTag, SEARCH_VIEW_TAG).commit();
            ((TasksListFragment) supportFragmentManager.findFragmentByTag(LaunchActivity.CURRENT_FRAGMENT)).setContentVisibility(4);
        }
    }

    public void handleSearchItem(final Menu menu) {
        MenuItem menuItemFindItem = menu.findItem(R.id.search);
        this.mSearchItem = menuItemFindItem;
        this.searchView = (SearchView) MenuItemCompat.getActionView(menuItemFindItem);
        this.mSearchItem.setActionView(initSearchView());
        MenuItemCompat.setOnActionExpandListener(this.mSearchItem, new MenuItemCompat.OnActionExpandListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksSearchHelper.1
            @Override // androidx.core.view.MenuItemCompat.OnActionExpandListener
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                TasksSearchHelper.this.getTaskListFragment().setIsSearching(true);
                for (int i = 0; i < menu.size(); i++) {
                    if (menu.getItem(i).getItemId() == R.id.search) {
                        if (TasksSearchHelper.this.inputQueryString == null) {
                            TasksSearchHelper.this.inputQueryString = "";
                        }
                    } else {
                        menu.getItem(i).setVisible(false);
                    }
                }
                return true;
            }

            @Override // androidx.core.view.MenuItemCompat.OnActionExpandListener
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                TasksListFragment tasksListFragment;
                FragmentManager supportFragmentManager = TasksSearchHelper.this.mActivity.getSupportFragmentManager();
                if (TasksSearchHelper.this.mTablet) {
                    TabletTasksControllerFragment tabletTasksControllerFragment = (TabletTasksControllerFragment) supportFragmentManager.findFragmentByTag(LaunchActivity.CURRENT_FRAGMENT);
                    tasksListFragment = tabletTasksControllerFragment.getTasksListFragment();
                    tabletTasksControllerFragment.updateTaskSearch(false);
                } else {
                    tasksListFragment = (TasksListFragment) supportFragmentManager.findFragmentByTag(LaunchActivity.CURRENT_FRAGMENT);
                }
                tasksListFragment.setContentVisibility(0);
                tasksListFragment.setIsSearching(false);
                Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag(TasksSearchHelper.SEARCH_VIEW_TAG);
                if (fragmentFindFragmentByTag != null) {
                    TasksSearchHelper.this.mActivity.getSupportFragmentManager().beginTransaction().remove(fragmentFindFragmentByTag).commit();
                }
                for (int i = 0; i < menu.size(); i++) {
                    if (menu.getItem(i).getItemId() == R.id.search) {
                        TasksSearchHelper.this.inputQueryString = null;
                    } else {
                        menu.getItem(i).setVisible(true);
                    }
                }
                TasksSearchHelper.this.searchProvider.setQueryString("");
                return true;
            }
        });
        if (this.inputQueryString != null) {
            this.mSearchItem.expandActionView();
        } else if (this.searchProvider.hasActiveQuery()) {
            this.mSearchItem.expandActionView();
            this.searchView.setQuery(this.searchProvider.getQueryString(), false);
        }
    }

    private SearchView initSearchView() {
        if (Utils.isTabletDevice(this.mActivity) || UiUtils.isPortrait(this.mActivity)) {
            ((TextView) this.searchView.findViewById(R.id.search_src_text)).setTextColor(ContextCompat.getColor(this.mActivity, R.color.calendar_primary_text_color));
        }
        this.searchView.setImeOptions(3);
        SearchView searchView = this.searchView;
        searchView.setQueryHint(searchView.getContext().getResources().getString(R.string.clr_strings_search_option_txt));
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksSearchHelper.2
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                if (!TasksSearchHelper.this.searchView.hasFocus() || TasksSearchHelper.this.inputQueryString == null) {
                    return false;
                }
                if (TasksSearchHelper.this.mFlagChangeQuery) {
                    TasksSearchHelper.this.inputQueryString = str;
                    return false;
                }
                TasksSearchHelper.this.mFlagChangeQuery = true;
                return false;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                TasksSearchHelper.this.inputQueryString = str;
                if (TasksSearchHelper.this.searchView != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) TasksSearchHelper.this.mActivity.getSystemService("input_method");
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(TasksSearchHelper.this.searchView.getWindowToken(), 0);
                    }
                    TasksSearchHelper.this.searchView.clearFocus();
                }
                TasksSearchHelper.this.onSearchSubmit(str);
                TasksSearchHelper.this.mFlagChangeQuery = false;
                return true;
            }
        });
        this.searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksSearchHelper.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                TasksSearchHelper.this.searchView.setQuery(TasksSearchHelper.this.inputQueryString, false);
            }
        });
        return this.searchView;
    }

    public String getSearchQuery() {
        return this.inputQueryString;
    }

    public boolean collapseSearchItem() {
        MenuItem menuItem = this.mSearchItem;
        if (menuItem == null || !menuItem.isActionViewExpanded()) {
            return false;
        }
        this.mSearchItem.collapseActionView();
        this.mSearchItem = null;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TasksListFragment getTaskListFragment() {
        FragmentManager supportFragmentManager = this.mActivity.getSupportFragmentManager();
        if (this.mTablet) {
            TasksListFragment tasksListFragment = ((TabletTasksControllerFragment) supportFragmentManager.findFragmentByTag(LaunchActivity.CURRENT_FRAGMENT)).getTasksListFragment();
            tasksListFragment.setIsSearching(true);
            return tasksListFragment;
        }
        return (TasksListFragment) supportFragmentManager.findFragmentByTag(LaunchActivity.CURRENT_FRAGMENT);
    }

    public static void removeTaskSearchFragment(AppCompatActivity appCompatActivity) {
        Fragment fragmentFindFragmentByTag = appCompatActivity.getSupportFragmentManager().findFragmentByTag(SEARCH_VIEW_TAG);
        if (fragmentFindFragmentByTag != null) {
            appCompatActivity.getSupportFragmentManager().beginTransaction().remove(fragmentFindFragmentByTag).commit();
        }
    }

    public boolean getFlagChangeQuery() {
        return this.mFlagChangeQuery;
    }

    public void setFlagChangeQuery(boolean z) {
        this.mFlagChangeQuery = z;
    }
}
