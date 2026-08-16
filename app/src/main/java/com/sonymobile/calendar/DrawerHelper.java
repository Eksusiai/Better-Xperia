package com.sonymobile.calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.sonyericsson.calendar.util.CalendarColorService;
import com.sonyericsson.calendar.util.CalendarInstanceService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.agendamonth.SplitScreenFragment;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.tablet.TabletAgendaControllerFragment;
import com.sonymobile.calendar.tablet.TabletDayControllerFragment;
import com.sonymobile.calendar.tablet.TabletMonthControllerFragment;
import com.sonymobile.calendar.tablet.TabletTasksControllerFragment;
import com.sonymobile.calendar.tablet.TabletWeekControllerFragment;
import com.sonymobile.calendar.tasks.activity.TasksListFragment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class DrawerHelper implements AdapterView.OnItemClickListener {
    public static final int INDEX_AGENDA = 4;
    public static final int INDEX_DAY = 3;
    public static final int INDEX_MONTH = 1;
    public static final int INDEX_TASKS = 5;
    public static final int INDEX_WEEK = 2;
    public static final int INDEX_YEAR = 0;
    public static final String TASK_PROVIDER_PERMISSION = "com.sonymobile.tasks.provider.permission.WRITE_TASKS";
    private AppCompatActivity mActivity;
    private DrawerAdapter mAdapter;
    private ContentObserver mContentObserver;
    private Time mDate;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mHasTaskProviderPermission;
    private DrawerNavigationListener mListener;
    private boolean mLunarAvailable;
    private boolean mTaskEnabled;

    public DrawerHelper(AppCompatActivity appCompatActivity, DrawerNavigationListener drawerNavigationListener, Toolbar toolbar) {
        this.mTaskEnabled = false;
        this.mHasTaskProviderPermission = false;
        this.mLunarAvailable = false;
        this.mActivity = appCompatActivity;
        this.mListener = drawerNavigationListener;
        this.mLunarAvailable = LunarAvailabilityManager.isLunarAvailable(appCompatActivity);
        this.mTaskEnabled = isTaskEnabled(appCompatActivity);
        this.mHasTaskProviderPermission = appCompatActivity.checkCallingOrSelfPermission(TASK_PROVIDER_PERMISSION) == 0;
        DrawerLayout drawerLayout = (DrawerLayout) appCompatActivity.findViewById(R.id.drawer_layout);
        this.mDrawerLayout = drawerLayout;
        drawerLayout.setFocusable(false);
        this.mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, 8388611);
        this.mDrawerList = (ListView) appCompatActivity.findViewById(R.id.left_drawer_child);
        DrawerAdapter drawerAdapter = new DrawerAdapter(appCompatActivity, populateDrawerItems());
        this.mAdapter = drawerAdapter;
        this.mDrawerList.setAdapter((ListAdapter) drawerAdapter);
        if (this.mDrawerList.getHeaderViewsCount() == 0) {
            this.mDrawerList.addHeaderView(((LayoutInflater) appCompatActivity.getApplicationContext().getSystemService("layout_inflater")).inflate(R.layout.drawer_header_view, (ViewGroup) this.mDrawerList, false));
        }
        this.mDrawerList.setDrawSelectorOnTop(true);
        this.mDrawerList.setOnItemClickListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(appCompatActivity, this.mDrawerLayout, toolbar, R.string.accessibility_drawer_opened, R.string.accessibility_drawer_close) { // from class: com.sonymobile.calendar.DrawerHelper.1
            @Override // androidx.appcompat.app.ActionBarDrawerToggle, androidx.drawerlayout.widget.DrawerLayout.DrawerListener
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                Utils.hideKeyboard(DrawerHelper.this.mActivity, DrawerHelper.this.mActivity.getCurrentFocus());
                DrawerHelper.this.mDrawerList.getChildAt(0).requestFocus();
            }

            @Override // androidx.appcompat.app.ActionBarDrawerToggle, androidx.drawerlayout.widget.DrawerLayout.DrawerListener
            public void onDrawerSlide(View view, float f) {
                super.onDrawerSlide(view, f);
                if (DrawerHelper.this.mListener != null) {
                    DrawerHelper.this.mListener.onDrawerOpened();
                }
            }
        };
        this.mDrawerToggle = actionBarDrawerToggle;
        this.mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        this.mDrawerList.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.sonymobile.calendar.DrawerHelper.2
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (z || view.isInTouchMode()) {
                    return;
                }
                DrawerHelper.this.mDrawerLayout.closeDrawer(DrawerHelper.this.mDrawerList);
            }
        });
    }

    private static boolean isTaskEnabled(Context context) {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        return Utils.isPackageEnabled(TasksListFragment.TASKS_PACKAGE_NAME, context);
    }

    public void registerObserver() {
        if (this.mContentObserver == null) {
            ContentResolver contentResolver = this.mActivity.getContentResolver();
            this.mContentObserver = new ContentObserver(new Handler()) { // from class: com.sonymobile.calendar.DrawerHelper.3
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    DrawerHelper.this.updateDrawerItems();
                    CalendarColorService.getInstance().clearCache();
                }
            };
            contentResolver.registerContentObserver(CalendarContract.Calendars.CONTENT_URI, true, this.mContentObserver);
        }
    }

    private ArrayList<DrawerItem> populateDrawerItems() {
        if (Utils.isTabletDevice(this.mActivity)) {
            return populateDrawerItemsTablet();
        }
        return populateDrawerItemsPhone();
    }

    public void syncDrawer() {
        this.mDrawerToggle.syncState();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return this.mDrawerToggle.onOptionsItemSelected(menuItem);
    }

    public void onConfigChange(Configuration configuration) {
        this.mDrawerToggle.onConfigurationChanged(configuration);
    }

    public void toggleDrawer() {
        if (this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
            this.mDrawerLayout.closeDrawers();
        } else {
            this.mDrawerLayout.openDrawer(this.mDrawerList);
        }
    }

    public void selectItem(int i, boolean z, boolean z2) {
        DrawerItem drawerItem = (DrawerItem) this.mAdapter.getItem(i);
        if (drawerItem == null || drawerItem.getItemType() == DrawerItem.DrawerItemType.DIVIDER) {
            return;
        }
        if (drawerItem.getItemType() != DrawerItem.DrawerItemType.CALENDAR) {
            this.mDrawerLayout.closeDrawers();
            if (this.mAdapter.getSelectedItemPosition() == i) {
                return;
            }
            this.mAdapter.setSelectedItem(i);
            this.mListener.onDrawerNavigated(drawerItem.getClassName(), i, z, z2);
            return;
        }
        DrawerItemCheckable drawerItemCheckable = (DrawerItemCheckable) drawerItem;
        drawerItemCheckable.setChecked(!drawerItemCheckable.isChecked());
        CalendarInstanceService.getInstance().updateVisibility(this.mDrawerList.getContext(), drawerItemCheckable.isChecked(), drawerItemCheckable.getId(), drawerItemCheckable.getAccountType());
    }

    public void selectWithoutNavigating(int i) {
        this.mAdapter.setSelectedItem(i);
        this.mAdapter.notifyDataSetInvalidated();
    }

    public int getSelectedIndex() {
        return this.mAdapter.getSelectedItemPosition();
    }

    public DrawerItem.DrawerItemType getSelectedDrawerItemType() {
        return this.mAdapter.getSelectedDrawerItemType();
    }

    private ArrayList<DrawerItem> populateDrawerItemsPhone() {
        ArrayList<DrawerItem> arrayList = new ArrayList<>();
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.year_view), YearFragment.class.getName(), DrawerItem.DrawerItemType.YEAR));
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.month_view), SplitScreenFragment.class.getName(), DrawerItem.DrawerItemType.MONTH, this.mLunarAvailable));
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.week_view), WeekFragment.class.getName(), DrawerItem.DrawerItemType.WEEK));
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.day_view), DayFragment.class.getName(), DrawerItem.DrawerItemType.DAY, this.mLunarAvailable));
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.agenda_view), SplitScreenFragment.class.getName(), DrawerItem.DrawerItemType.AGENDA));
        if (this.mTaskEnabled && this.mHasTaskProviderPermission) {
            arrayList.add(new DrawerItem(this.mActivity.getString(R.string.task_app_name_txt), TasksListFragment.class.getName(), DrawerItem.DrawerItemType.TASKS));
        }
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.menu_select_calendars), null, DrawerItem.DrawerItemType.DIVIDER));
        return arrayList;
    }

    private ArrayList<DrawerItem> populateDrawerItemsTablet() {
        ArrayList<DrawerItem> arrayList = new ArrayList<>();
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.year_view), YearFragment.class.getName(), DrawerItem.DrawerItemType.YEAR));
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.month_view), TabletMonthControllerFragment.class.getName(), DrawerItem.DrawerItemType.MONTH, this.mLunarAvailable));
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.week_view), TabletWeekControllerFragment.class.getName(), DrawerItem.DrawerItemType.WEEK));
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.day_view), TabletDayControllerFragment.class.getName(), DrawerItem.DrawerItemType.DAY, this.mLunarAvailable));
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.agenda_view), TabletAgendaControllerFragment.class.getName(), DrawerItem.DrawerItemType.AGENDA));
        if (this.mTaskEnabled && this.mHasTaskProviderPermission) {
            arrayList.add(new DrawerItem(this.mActivity.getString(R.string.task_app_name_txt), TabletTasksControllerFragment.class.getName(), DrawerItem.DrawerItemType.TASKS));
        }
        arrayList.add(new DrawerItem(this.mActivity.getString(R.string.menu_select_calendars), null, DrawerItem.DrawerItemType.DIVIDER));
        return arrayList;
    }

    private void requestReload(boolean z) {
        CalendarInstanceService.getInstance().requestLoad(this.mDrawerList.getContext(), new IAsyncServiceResultHandler() { // from class: com.sonymobile.calendar.DrawerHelper.4
            @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
            public void onResult(Object obj, Object obj2) {
                DrawerHelper.this.addCalendars(CalendarInstanceService.getInstance().getCalendars());
            }
        }, false);
        DrawerAdapter drawerAdapter = new DrawerAdapter(this.mActivity, populateDrawerItems());
        this.mAdapter = drawerAdapter;
        this.mDrawerList.setAdapter((ListAdapter) drawerAdapter);
        setNewDate(this.mDate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addCalendars(Map<Long, CalendarItem> map) {
        Iterator<Map.Entry<Long, CalendarItem>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            addCalendar(it.next().getValue());
        }
        this.mAdapter.notifyDataSetChanged();
    }

    private void addCalendar(CalendarItem calendarItem) {
        calendarItem.displayAccountType = Utils.getDisplayNameForAccountType(this.mDrawerList.getContext(), calendarItem.accountType);
        calendarItem.displayName = Utils.getCalendarDisplayName(this.mDrawerList.getContext(), calendarItem.accountType, calendarItem.displayName);
        this.mAdapter.setCalendar(new DrawerItemCheckable(calendarItem.displayAccountType, calendarItem.displayName, DrawerItem.DrawerItemType.CALENDAR, calendarItem.calendarId, calendarItem.color, calendarItem.isVisible, calendarItem.accountType, calendarItem.accountName));
    }

    public void setNewDate(Time time) {
        DrawerAdapter drawerAdapter = this.mAdapter;
        if (drawerAdapter != null) {
            this.mDate = time;
            drawerAdapter.updateDate(time);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void updateDrawerItems() {
        this.mTaskEnabled = Utils.isPackageEnabled(TasksListFragment.TASKS_PACKAGE_NAME, this.mActivity);
        requestReload(true);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (i == 0) {
            return;
        }
        selectItem(i - 1, true, true);
    }

    public boolean isDrawerVisible() {
        return this.mDrawerLayout.isDrawerVisible(this.mDrawerList);
    }

    public void unregisterObserver() {
        if (this.mContentObserver != null) {
            this.mActivity.getContentResolver().unregisterContentObserver(this.mContentObserver);
        }
    }

    public void setAllowDrawerSlide(boolean z) {
        if (z) {
            this.mDrawerLayout.setDrawerLockMode(0);
        } else {
            this.mDrawerLayout.setDrawerLockMode(1);
        }
    }
}
