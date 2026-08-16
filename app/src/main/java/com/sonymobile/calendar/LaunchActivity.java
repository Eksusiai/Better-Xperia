package com.sonymobile.calendar;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.GaUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.actions.SearchIntents;
import com.google.common.base.Charsets;
import com.sonyericsson.calendar.util.CustomizeConfig;
import com.sonyericsson.calendar.util.DatabaseUtils;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonymobile.accuweather.AccuWeatherBroker;
import com.sonymobile.calendar.agendamonth.SplitScreenFragment;
import com.sonymobile.calendar.alerts.DismissAlarmsService;
import com.sonymobile.calendar.birthday.BirthdayActivity;
import com.sonymobile.calendar.birthday.BirthdayService;
import com.sonymobile.calendar.editevent.EditEventActivity;
import com.sonymobile.calendar.editevent.TabletEditEventActivity;
import com.sonymobile.calendar.holiday.ChinaHolidaysChecker;
import com.sonymobile.calendar.holiday.ChinaHolidaysManager;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.linkedin.model.LinkedInMySelf;
import com.sonymobile.calendar.linkedin.ui.LinkedInRenewalDialog;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.permissions.PermissionAcceptanceActivity;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.tablet.TabletAgendaControllerFragment;
import com.sonymobile.calendar.tablet.TabletEventInfoActivity;
import com.sonymobile.calendar.tablet.TabletSearchActivity;
import com.sonymobile.calendar.tablet.TabletTasksControllerFragment;
import com.sonymobile.calendar.tasks.activity.TasksListFragment;
import com.sonymobile.calendar.tasks.activity.TasksSearchHelper;
import com.sonymobile.calendar.tasks.alerts.TasksDismissAlarmsService;
import com.sonymobile.calendar.utils.NotificationUtils;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.widget.CalendarAppWidgetProvider;
import com.sonymobile.calendar.widget.WhiteHeader;
import com.sonymobile.lunar.lib.LunarContract;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes2.dex */
public class LaunchActivity extends PermissionHandlerActivity implements NavigationListener, DrawerNavigationListener, WelcomeScreenFragment.WelcomeScreenCloseListener {
    public static final String ARG_FROM_NOTIFICATION = "FromNotification";
    private static final String CATEGORY_VIEW_TASKS = "com.sonymobile.calendar.category.TASKS";
    private static final String CATEGORY_VIEW_TASKS_LIST = "com.sonymobile.calendar.action.View_TASKS_LIST";
    public static final String CURRENT_FRAGMENT = "current";
    private static final String FRAGMENT_STACK_LIST = "FragmentStackList";
    private static final String KEY_DISPLAY_SIZE = "keyDisplaySize";
    public static final String KEY_IS_MULTI_WINDOW = "keyIsMultiWindow";
    public static final String KEY_SEARCH_MODE_ON = "keySearchModeOn";
    private static final String LICENSE_FILE_PATH = "license/CalendarLicense.txt";
    public static final int NOTIFICATION_ACTION_CLICK = 1;
    public static final int NOTIFICATION_ACTION_TASKS = 2;
    private static final String REFRESH_ORDER = "account_name,account_type";
    private static final String REFRESH_SELECTION = "sync_events=?";
    private static final int REFRESH_TIMEOUT = 3000;
    private static final int REQUEST_CODE_PERMISSION_ACCEPTANCE = 1001;
    private static final String SEARCH_STRING = "searchString";
    private static final String TAG = "LaunchActivity";
    private static final String URI_PATH_SEGMENT_EVENTS = "events";
    private static final String URI_PATH_SEGMENT_TASKS = "tasks";
    private static final String URI_PATH_SEGMENT_TIME = "time";
    public static final String WELCOME_SREEN_FRAGMENT = "welcomeScreenFragment";
    private boolean clearStack;
    private Stack<Integer> fragmentStackList;
    private ActionBarControllerBase mActionBarController;
    private DrawerHelper mDrawerHelper;
    private boolean mIsInMultiWindowMode;
    private boolean mIsInSearchMode;
    private NotificationUtils mNotificationUtils;
    private RefreshQueryHandler mRefreshHandler;
    private View mRefreshView;
    private Bundle mSavedInstanceState;
    private MenuItem mSearchItem;
    private WhiteHeader mWhiteHeader;
    private static final String[] REFRESH_ARGS = {"1"};
    private static final String[] PERMISSIONS = {"android.permission.INTERNET", "android.permission.READ_CONTACTS", "android.permission.ACCESS_FINE_LOCATION", "android.permission.QUERY_ALL_PACKAGES"};
    private RefreshTimeOut mRefreshTimeOut = new RefreshTimeOut();
    ArrayList<DrawerStateListener> mListeners = new ArrayList<>();
    private boolean mIsTablet = false;
    private CharSequence searchString = null;
    private boolean isLunarAvailable = false;
    private int mScreenLayoutSize = -1;

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!Utils.showWelcomeScreen(this) && !Utils.isSharedPreferenceContain(this, GeneralPreferences.KEY_ACCESS_PACKAGE_LIST)) {
            Utils.setSharedPreference((Context) this, GeneralPreferences.KEY_ACCESS_PACKAGE_LIST, true);
        }
        AccuWeatherBroker.setPartnerString(AccuWeatherBroker.PARTNER_PARAMETER_CALENDAR);
        setDefaultHolidaysIfNeeded();
        setContentView(R.layout.main_content);
        getLoaderManager();
        NotificationUtils notificationUtils = new NotificationUtils(this);
        this.mNotificationUtils = notificationUtils;
        notificationUtils.createChannels();
        FreeDayService.getInstance().requestLoad(this, null, 0, true);
        this.mIsTablet = Utils.isTabletDevice(this);
        this.fragmentStackList = new Stack<>();
        this.mWhiteHeader = (WhiteHeader) findViewById(R.id.white_header);
        this.mActionBarController = ActionBarControllerFactory.getController(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.mDrawerHelper = new DrawerHelper(this, this, toolbar);
        this.mWhiteHeader = (WhiteHeader) findViewById(R.id.white_header);
        initActionBar(toolbar);
        if (isEssentialPermissionsGranted()) {
            continueInitialization(bundle);
        } else {
            this.mSavedInstanceState = bundle;
        }
    }

    private void continueInitialization(Bundle bundle) {
        boolean zHandleIntent = handleIntent(getIntent(), true);
        EventLoaderService.getInstance().start(this);
        this.mDrawerHelper.registerObserver();
        this.mDrawerHelper.updateDrawerItems();
        if (!zHandleIntent) {
            this.mDrawerHelper.selectItem(getStartResolution(bundle, 1), false, false);
        }
        DatabaseUtils.createCalendarInDatabaseIfNeeded(this);
        if (bundle == null) {
            Utils.setDisplayTime(Long.valueOf(Utils.getDisplayTime()));
        } else {
            if (bundle.containsKey(FRAGMENT_STACK_LIST)) {
                this.fragmentStackList.clear();
                this.fragmentStackList.addAll(bundle.getIntegerArrayList(FRAGMENT_STACK_LIST));
            }
            this.searchString = bundle.getCharSequence(SEARCH_STRING, null);
            boolean z = bundle.getBoolean(KEY_SEARCH_MODE_ON, false);
            this.mIsInSearchMode = z;
            this.mDrawerHelper.setAllowDrawerSlide(!z);
            this.mIsInMultiWindowMode = bundle.getBoolean(KEY_IS_MULTI_WINDOW, false);
            this.mScreenLayoutSize = bundle.getInt(KEY_DISPLAY_SIZE, -1);
        }
        this.mRefreshHandler = new RefreshQueryHandler(getContentResolver());
        if (LinkedInUtils.isLinkedInEnabled(this) && Utils.showWelcomeScreen(this)) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            if (supportFragmentManager.findFragmentByTag(WELCOME_SREEN_FRAGMENT) == null) {
                setRequestedOrientation(1);
                WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment();
                FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
                fragmentTransactionBeginTransaction.add(android.R.id.content, welcomeScreenFragment, WELCOME_SREEN_FRAGMENT);
                fragmentTransactionBeginTransaction.addToBackStack("");
                fragmentTransactionBeginTransaction.commitAllowingStateLoss();
            }
        } else if (bundle == null) {
            showPermissionAcceptedDialogIfNeeded();
        }
        if (LinkedInUtils.isLinkedInSyncValid(this) && !LinkedInMySelf.getInstance().hasEmail()) {
            LinkedInUtils.getOwnLinkedInEmail(this);
        }
        this.mDrawerHelper.syncDrawer();
    }

    private void setDefaultHolidaysIfNeeded() {
        if (Utils.shouldAddDefaultHolidays(this)) {
            Utils.setSharedPreference(this, GeneralPreferences.KEY_NATIONAL_HOLIDAYS, getResources().getStringArray(R.array.auto_selected_holiday_lists));
            Utils.defaultHolidaysAdded(this);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        int i = getResources().getConfiguration().screenLayout & 15;
        if (this.mScreenLayoutSize != i) {
            this.mScreenLayoutSize = i;
            if (this.mIsTablet && i == 4) {
                recreate();
            }
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{new PermissionItem("android.permission.READ_CONTACTS", null, null, R.drawable.ic_contact, false), new PermissionItem("android.permission.WRITE_CONTACTS", null, null, R.drawable.ic_contact, false), PermissionUtils.getEssentialCalendarPermissionItem(this), new PermissionItem("android.permission.READ_CALENDAR", getString(R.string.calendar_permision_group_title), getString(R.string.calendar_permision_group_description), R.drawable.ic_calendar_event, true), new PermissionItem("android.permission.POST_NOTIFICATIONS", null, null, 0, true)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        continueInitialization(this.mSavedInstanceState);
        Time time = new SafeTime();
        time.set(Utils.getDisplayTime());
        this.mDrawerHelper.setNewDate(time);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        this.mDrawerHelper.unregisterObserver();
        BirthdayService.INSTANCE.stop();
        EventLoaderService.getInstance().stop();
        ChinaHolidaysChecker.getInstance().stop();
        super.onDestroy();
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1001 && i2 == -1) {
            continueInitialization(new Bundle());
            super.onActivityResult(i, i2, intent);
            return;
        }
        if (LinkedInUtils.handleSyncWithLinkedInResultAndReturnStatus(this, i, i2, intent)) {
            View viewFindViewById = findViewById(R.id.connect_with_linkedin_welcome);
            if (viewFindViewById != null) {
                viewFindViewById.setVisibility(4);
            }
            View viewFindViewById2 = findViewById(R.id.connect_with_linkedin_welcome_label);
            if (viewFindViewById2 != null) {
                viewFindViewById2.setVisibility(4);
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onSearchRequested() {
        this.searchString = "";
        invalidateOptionsMenu();
        return false;
    }

    private void initActionBar(Toolbar toolbar) {
        this.mActionBarController.initActionBar(toolbar, this.mWhiteHeader, this.mDrawerHelper, this);
    }

    @Override // com.sonymobile.calendar.DrawerNavigationListener
    public void onDrawerNavigated(String str, int i, boolean z, boolean z2) {
        this.clearStack = z;
        setFragment(str, i, z2);
    }

    @Override // com.sonymobile.calendar.DrawerNavigationListener
    public void onDrawerOpened() {
        Iterator<DrawerStateListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onDrawerOpened();
        }
    }

    public void registerDrawerStateListener(DrawerStateListener drawerStateListener) {
        this.mListeners.add(drawerStateListener);
    }

    public void unregisterDrawerStateListener(DrawerStateListener drawerStateListener) {
        this.mListeners.remove(drawerStateListener);
    }

    @Override // com.sonymobile.calendar.NavigationListener
    public void onViewNavigated(Time time, String str, ViewType viewType) {
        this.mActionBarController.onViewNavigated(time, str, viewType);
        this.mDrawerHelper.setNewDate(time);
        invalidateOptionsMenu();
    }

    public ActionBarControllerBase getActionBarController() {
        return this.mActionBarController;
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (isEssentialPermissionsGranted()) {
            this.mDrawerHelper.updateDrawerItems();
        }
        this.clearStack = false;
        this.isLunarAvailable = LunarAvailabilityManager.isLunarAvailable(this);
        invalidateOptionsMenu();
        showLinkedInRenewalDialog();
        if (this.isLunarAvailable) {
            ChinaHolidaysChecker.getInstance().check(this);
            ChinaHolidaysManager.getInstance(this).setup();
        }
    }

    private void showLinkedInRenewalDialog() {
        if (LinkedInUtils.isLinkedInEnabled(this) && LinkedInUtils.isTokenExpired(this) && !Utils.showWelcomeScreen(this)) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            if (supportFragmentManager.findFragmentByTag(LinkedInRenewalDialog.TAG) == null) {
                LinkedInRenewalDialog.newInstance().show(supportFragmentManager, LinkedInRenewalDialog.TAG);
            }
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        if (isChangingConfigurations() || !this.clearStack) {
            return;
        }
        this.fragmentStackList.clear();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onMultiWindowModeChanged(boolean z) {
        Log.i(TAG, "onMultiWindowModeChanged");
        super.onMultiWindowModeChanged(z);
        this.mIsInMultiWindowMode = z;
        if (z) {
            return;
        }
        invalidateOptionsMenu();
    }

    @Override // android.app.Activity
    public boolean isInMultiWindowMode() {
        return super.isInMultiWindowMode();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (!isEssentialPermissionsGranted()) {
            return true;
        }
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
        if (fragmentFindFragmentByTag != null && this.mDrawerHelper.getSelectedDrawerItemType() == DrawerItem.DrawerItemType.TASKS) {
            return true;
        }
        Toolbar toolbar = this.mActionBarController.getToolbar();
        String string = null;
        if (toolbar != null) {
            string = TextUtils.isEmpty(this.searchString) ? null : this.searchString.toString();
            toolbar.getMenu().clear();
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.LaunchActivity.1
                @Override // androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
                public boolean onMenuItemClick(MenuItem menuItem) {
                    return LaunchActivity.this.onOptionsItemSelected(menuItem);
                }
            });
        }
        final Menu activeMenu = toolbar != null ? toolbar.getMenu() : menu;
        if (!TextUtils.isEmpty(string)) {
            this.searchString = string;
        }
        getMenuInflater().inflate(R.menu.calendar_title_bar, activeMenu);
        MenuItem menuItemFindItem = activeMenu.findItem(R.id.action_search);
        this.mSearchItem = menuItemFindItem;
        initSearchView(menuItemFindItem);
        MenuItemCompat.setOnActionExpandListener(this.mSearchItem, new MenuItemCompat.OnActionExpandListener() { // from class: com.sonymobile.calendar.LaunchActivity.2
            @Override // androidx.core.view.MenuItemCompat.OnActionExpandListener
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                for (int i = 0; i < activeMenu.size(); i++) {
                    if (activeMenu.getItem(i).getItemId() == R.id.action_search) {
                        activeMenu.findItem(R.id.action_today).setVisible(false);
                        if (LaunchActivity.this.searchString == null) {
                            LaunchActivity.this.searchString = "";
                            LaunchActivity.this.mIsInSearchMode = true;
                            LaunchActivity.this.mDrawerHelper.setAllowDrawerSlide(true);
                        }
                    }
                }
                return true;
            }

            @Override // androidx.core.view.MenuItemCompat.OnActionExpandListener
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                LaunchActivity.this.invalidateOptionsMenu();
                for (int i = 0; i < activeMenu.size(); i++) {
                    MenuItem item = activeMenu.getItem(i);
                    boolean z = item.getItemId() == R.id.action_search;
                    boolean z2 = item.getItemId() == R.id.action_conversion;
                    if (z) {
                        LaunchActivity.this.searchString = null;
                        LaunchActivity.this.mIsInSearchMode = false;
                        LaunchActivity.this.mDrawerHelper.setAllowDrawerSlide(true);
                    } else {
                        if (item.getItemId() == R.id.action_today) {
                            LaunchActivity.this.setTodayIcon(activeMenu);
                        } else {
                            item.setVisible(true);
                        }
                        if (z2 && !LaunchActivity.this.isLunarAvailable) {
                            item.setVisible(false);
                        }
                    }
                }
                return true;
            }
        });
        if (this.isLunarAvailable && !this.mIsInMultiWindowMode) {
            activeMenu.findItem(R.id.action_conversion).setVisible(true);
        }
        if (fragmentFindFragmentByTag instanceof TabletAgendaControllerFragment) {
            activeMenu.findItem(R.id.action_today).setVisible(false);
        }
        if (this.searchString != null) {
            this.mSearchItem.expandActionView();
        }
        return super.onCreateOptionsMenu(activeMenu);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void blockSplitScreenFocusStealing(boolean z) {
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
        if (fragmentFindFragmentByTag == null || fragmentFindFragmentByTag.getClass() != SplitScreenFragment.class) {
            return;
        }
        ((ViewGroup) ((SplitScreenFragment) fragmentFindFragmentByTag).getView().findViewById(R.id.splitter)).setDescendantFocusability(!z ? 262144 : 393216);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 41) {
            if (keyEvent.isCtrlPressed()) {
                this.mDrawerHelper.toggleDrawer();
            }
            return true;
        }
        if (i == 48) {
            if (keyEvent.isCtrlPressed()) {
                Utils.setAgendaSelectedEventInstanceId(0L);
                handleOptionItemSelected().goToToday();
            }
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        Toolbar toolbar = this.mActionBarController.getToolbar();
        if (toolbar != null) {
            menu = toolbar.getMenu();
        }
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
        if (fragmentFindFragmentByTag == null) {
            return super.onPrepareOptionsMenu(menu);
        }
        if (!(fragmentFindFragmentByTag instanceof TabletAgendaControllerFragment) && !this.mIsInSearchMode) {
            setTodayIcon(menu);
        }
        if (!this.mIsTablet && !UiUtils.isPortrait(this) && !this.mIsInSearchMode) {
            this.mActionBarController.updateVisibility(fragmentFindFragmentByTag.getClass().getName());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTodayIcon(Menu menu) {
        boolean zShouldShowTodayLabel = Utils.shouldShowTodayLabel(this, Utils.getTimeZone(this, null));
        final MenuItem menuItemFindItem = menu.findItem(R.id.action_today);
        if (menuItemFindItem != null) {
            menuItemFindItem.setVisible(zShouldShowTodayLabel);
            if (zShouldShowTodayLabel) {
                TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.today_menu_text_view, (ViewGroup) null);
                textView.setText(menuItemFindItem.getTitle().toString().toUpperCase());
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.LaunchActivity.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        Navigator navigatorHandleOptionItemSelected = LaunchActivity.this.handleOptionItemSelected();
                        if (navigatorHandleOptionItemSelected != null) {
                            MenuHelper.onOptionsItemSelected(LaunchActivity.this, menuItemFindItem, navigatorHandleOptionItemSelected);
                        }
                    }
                });
                menuItemFindItem.setActionView(textView);
            }
        }
    }

    private void initSearchView(MenuItem menuItem) {
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: com.sonymobile.calendar.LaunchActivity.4
            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                if (!searchView.hasFocus() || LaunchActivity.this.searchString == null || str == null) {
                    return false;
                }
                LaunchActivity.this.searchString = str;
                return false;
            }

            @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                LaunchActivity.this.search(str);
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.sonymobile.calendar.LaunchActivity.5
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                searchView.setQuery(LaunchActivity.this.searchString, false);
                LaunchActivity.this.blockSplitScreenFocusStealing(z);
            }
        });
        searchView.setQueryHint(getString(R.string.clr_strings_search_calendar_txt));
        ((ImageView) searchView.findViewById(R.id.search_close_btn)).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.LaunchActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                searchView.setQuery("", false);
                LaunchActivity.this.searchString = "";
            }
        });
    }

    private int getStartResolution(Bundle bundle, int i) {
        int sharedPreference = bundle != null ? bundle.getInt(GeneralPreferences.KEY_START_RESOLUTION, -1) : -1;
        if (sharedPreference < 0) {
            sharedPreference = Utils.getSharedPreference(this, GeneralPreferences.KEY_START_RESOLUTION, i);
        }
        if (sharedPreference != 5) {
            return sharedPreference;
        }
        try {
            Class.forName(TasksListFragment.class.getName());
            if (Utils.isPackageEnabled(TasksListFragment.TASKS_PACKAGE_NAME, this)) {
                i = sharedPreference;
            }
            return i;
        } catch (ClassNotFoundException unused) {
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void search(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        if (this.mIsTablet) {
            intent.setClassName(this, TabletSearchActivity.class.getName());
        } else {
            intent.setClassName(this, SearchActivity.class.getName());
        }
        intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, 0);
        intent.putExtra(SearchIntents.EXTRA_QUERY, str);
        intent.setFlags(537001984);
        startActivity(intent);
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (isEssentialPermissionsGranted()) {
            int selectedIndex = this.mDrawerHelper.getSelectedIndex();
            if (selectedIndex >= 0) {
                bundle.putInt(GeneralPreferences.KEY_START_RESOLUTION, selectedIndex);
            }
            CharSequence charSequence = this.searchString;
            if (charSequence != null) {
                bundle.putCharSequence(SEARCH_STRING, charSequence);
            }
            bundle.putIntegerArrayList(FRAGMENT_STACK_LIST, new ArrayList<>(this.fragmentStackList));
            bundle.putBoolean(KEY_SEARCH_MODE_ON, this.mIsInSearchMode);
            bundle.putBoolean(KEY_IS_MULTI_WINDOW, this.mIsInMultiWindowMode);
            bundle.putInt(KEY_DISPLAY_SIZE, this.mScreenLayoutSize);
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isEssentialPermissionsGranted()) {
            handleIntent(intent, intent.hasExtra(CalendarAppWidgetProvider.KEY_APP_WIDGET_ID));
        }
    }

    private boolean handleIntent(Intent intent, boolean z) {
        Bundle extras = intent.getExtras();
        Uri data = intent.getData();
        if (intent.hasExtra(ARG_FROM_NOTIFICATION)) {
            handleNotification(intent.getIntExtra(ARG_FROM_NOTIFICATION, 0), intent);
        }
        if ("android.intent.action.INSERT".equals(intent.getAction()) || "android.intent.action.EDIT".equals(intent.getAction())) {
            if (Utils.isTabletDevice(this)) {
                intent.setClass(this, TabletEditEventActivity.class);
            } else {
                intent.setClass(this, EditEventActivity.class);
            }
            startActivity(intent);
            if (z) {
                finish();
            }
            GaUtils.sendEvent(GaUtils.STARTING_APP_CATEGORY, GaUtils.STARTING_FROM_EMAIL);
            return false;
        }
        if (intent.hasExtra(BirthdayActivity.DISPLAYED_TIME)) {
            intent.setClass(this, BirthdayActivity.class);
            startActivity(intent);
            return false;
        }
        if ("android.intent.action.VIEW".equals(intent.getAction())) {
            GaUtils.sendEvent(GaUtils.STARTING_APP_CATEGORY, GaUtils.STARTING_FROM_CONTACTS);
        } else {
            GaUtils.sendEvent(GaUtils.STARTING_APP_CATEGORY, GaUtils.STARTING_FROM_ICON);
        }
        if (intent.getCategories() != null) {
            if (intent.getCategories().contains(CATEGORY_VIEW_TASKS)) {
                this.mDrawerHelper.selectItem(5, false, true);
                return false;
            }
            if (intent.getCategories().contains(CATEGORY_VIEW_TASKS_LIST)) {
                this.mDrawerHelper.selectItem(5, false, true);
                return false;
            }
        }
        if (extras == null || data == null) {
            if (extras == null && data != null) {
                if (data.getPathSegments().size() == 2 && data.getPathSegments().get(0).equals(URI_PATH_SEGMENT_TIME)) {
                    this.mDrawerHelper.selectItem(1, false, false);
                }
                setDisplayTimeFromIntent(intent);
            }
            return false;
        }
        setDisplayTimeFromIntent(intent);
        List<String> pathSegments = data.getPathSegments();
        if (pathSegments.size() == 2 && this.mIsTablet) {
            if (pathSegments.get(0).equals(URI_PATH_SEGMENT_EVENTS)) {
                intent.setClass(this, TabletEventInfoActivity.class);
                startActivity(intent);
                if (z) {
                    finish();
                }
                return false;
            }
            if (pathSegments.get(0).equals(URI_PATH_SEGMENT_TASKS)) {
                this.mDrawerHelper.selectWithoutNavigating(5);
                setFragment(TabletTasksControllerFragment.class.getName(), 5, false);
                return true;
            }
        }
        return false;
    }

    private void setDisplayTimeFromIntent(Intent intent) {
        Utils.setDisplayTime(Long.valueOf(Utils.getHTZSelectionTimeFromIntent(this, intent, Utils.getTimeZone(getBaseContext(), null))));
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mDrawerHelper.onConfigChange(configuration);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        String str = null;
        if (this.mDrawerHelper.isDrawerVisible()) {
            this.mDrawerHelper.toggleDrawer();
        }
        if (this.mDrawerHelper.onOptionsItemSelected(menuItem)) {
            return true;
        }
        if (menuItem.getItemId() == R.id.action_refresh) {
            View viewFindViewById = findViewById(R.id.refresh_bar);
            this.mRefreshView = viewFindViewById;
            if (viewFindViewById == null) {
                return true;
            }
            refreshCalendars();
            this.mRefreshView.setVisibility(0);
            Handler handler = getWindow().getDecorView().getHandler();
            if (handler != null) {
                handler.postDelayed(this.mRefreshTimeOut, 3000L);
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.action_license) {
            String str2 = null;
            try {
                InputStream inputStreamOpen = getAssets().open(LICENSE_FILE_PATH);
                try {
                    byte[] bArr = new byte[inputStreamOpen.available()];
                    inputStreamOpen.read(bArr);
                    str = new String(bArr, Charsets.UTF_8);
                    if (inputStreamOpen != null) {
                        try {
                            inputStreamOpen.close();
                        } catch (IOException unused) {
                            str2 = str;
                            Log.w(TAG, "Error while reading license file");
                            str = str2;
                        }
                    }
                } catch (Throwable th) {
                    if (inputStreamOpen != null) {
                        try {
                            inputStreamOpen.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException unused2) {
            }
            LicenseDialogFragment.newInstance(getString(R.string.menu_license), str).show(getSupportFragmentManager(), LicenseDialogFragment.TAG);
            return true;
        }
        Navigator navigatorHandleOptionItemSelected = handleOptionItemSelected();
        if ((navigatorHandleOptionItemSelected == null || !MenuHelper.onOptionsItemSelected(this, menuItem, navigatorHandleOptionItemSelected)) && !((Fragment) navigatorHandleOptionItemSelected).onOptionsItemSelected(menuItem)) {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Navigator handleOptionItemSelected() {
        return (Navigator) getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
    }

    private void refreshCalendars() {
        RefreshQueryHandler refreshQueryHandler = this.mRefreshHandler;
        refreshQueryHandler.startQuery(refreshQueryHandler.getNextToken(), null, CalendarContract.Calendars.CONTENT_URI, new String[]{"_id", "account_name", "account_type"}, REFRESH_SELECTION, REFRESH_ARGS, REFRESH_ORDER);
    }

    private static class RefreshQueryHandler extends AsyncQueryHandler {
        private AtomicInteger mUniqueToken;

        public RefreshQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
            this.mUniqueToken = new AtomicInteger(0);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int i, Object obj, Cursor cursor) {
            new RefreshInBackground().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cursor);
        }

        public final int getNextToken() {
            return this.mUniqueToken.getAndIncrement();
        }
    }

    private static class RefreshInBackground extends AsyncTask<Cursor, Integer, Integer> {
        private RefreshInBackground() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(Cursor... cursorArr) {
            Cursor cursor;
            if (cursorArr.length != 1 || (cursor = cursorArr[0]) == null) {
                return null;
            }
            String str = null;
            String str2 = null;
            while (cursor.moveToNext()) {
                try {
                    String string = cursor.getString(1);
                    String string2 = cursor.getString(2);
                    if (!TextUtils.equals(string, str) || !TextUtils.equals(string2, str2)) {
                        Utils.scheduleSync(1, string, string2, false, true);
                        str = string;
                        str2 = string2;
                    }
                } catch (Throwable th) {
                    cursor.close();
                    throw th;
                }
            }
            cursor.close();
            return null;
        }
    }

    class RefreshTimeOut implements Runnable {
        RefreshTimeOut() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (LaunchActivity.this.isFinishing()) {
                return;
            }
            LaunchActivity.this.mRefreshView.setVisibility(8);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        Handler handler = getWindow().getDecorView().getHandler();
        if (handler != null) {
            handler.removeCallbacks(this.mRefreshTimeOut);
            View view = this.mRefreshView;
            if (view != null && view.getVisibility() == 0) {
                this.mRefreshView.setVisibility(8);
            }
        }
        int selectedIndex = this.mDrawerHelper.getSelectedIndex();
        if (selectedIndex >= 0) {
            Utils.setSharedPreference(this, GeneralPreferences.KEY_START_RESOLUTION, selectedIndex);
        }
    }

    private void setFragment(String str, int i, boolean z) {
        if (this.clearStack) {
            this.fragmentStackList.clear();
        }
        TasksSearchHelper.removeTaskSearchFragment(this);
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
        if (str.equals(SplitScreenFragment.class.getName()) || fragmentFindFragmentByTag == null || !str.equals(fragmentFindFragmentByTag.getClass().getName())) {
            final FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragmentInstantiate = Fragment.instantiate(this, str);
            fragmentTransactionBeginTransaction.setTransition(4099);
            fragmentTransactionBeginTransaction.replace(R.id.content_frame, fragmentInstantiate, CURRENT_FRAGMENT);
            if (z) {
                new Handler().postDelayed(new Runnable() { // from class: com.sonymobile.calendar.LaunchActivity.7
                    @Override // java.lang.Runnable
                    public void run() {
                        if (LaunchActivity.this.isFinishing()) {
                            return;
                        }
                        fragmentTransactionBeginTransaction.commit();
                    }
                }, 250L);
            } else {
                fragmentTransactionBeginTransaction.commitAllowingStateLoss();
            }
            if (this.fragmentStackList.contains(Integer.valueOf(i))) {
                this.fragmentStackList.remove(Integer.valueOf(i));
            }
            this.fragmentStackList.push(Integer.valueOf(i));
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.mDrawerHelper.isDrawerVisible()) {
            this.mDrawerHelper.toggleDrawer();
            return;
        }
        if (this.mIsInSearchMode) {
            collapseSearchItem();
            return;
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag(WELCOME_SREEN_FRAGMENT) != null) {
            finish();
            return;
        }
        Fragment fragmentFindFragmentByTag = supportFragmentManager.findFragmentByTag(LinkedInRenewalDialog.TAG);
        if (fragmentFindFragmentByTag != null) {
            ((LinkedInRenewalDialog) fragmentFindFragmentByTag).getNewTokenAndDissmiss();
            return;
        }
        Fragment fragmentFindFragmentByTag2 = supportFragmentManager.findFragmentByTag(CURRENT_FRAGMENT);
        if ((fragmentFindFragmentByTag2 instanceof TasksListFragment) && ((TasksListFragment) fragmentFindFragmentByTag2).collapseSearchItem()) {
            return;
        }
        if ((fragmentFindFragmentByTag2 instanceof TabletTasksControllerFragment) && ((TabletTasksControllerFragment) fragmentFindFragmentByTag2).collapseSearchItem()) {
            return;
        }
        if (this.fragmentStackList.size() > 1) {
            this.fragmentStackList.pop();
            switchItem(this.fragmentStackList.pop().intValue(), Utils.getDisplayTime(), false);
            return;
        }
        try {
            if (supportFragmentManager.popBackStackImmediate()) {
                return;
            }
            if (getCallingPackage() == null && moveTaskToBack(false)) {
                return;
            }
            finish();
        } catch (IllegalStateException unused) {
        }
    }

    public void collapseSearchItem() {
        if (this.mIsInSearchMode) {
            this.mSearchItem.collapseActionView();
        }
    }

    public void switchItem(int i, long j, boolean z) {
        Utils.setDisplayTime(Long.valueOf(j));
        this.mDrawerHelper.selectItem(i, z, false);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.mDrawerHelper.isDrawerVisible()) {
            Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
            if (fragmentFindFragmentByTag instanceof SplitScreenFragment) {
                ((SplitScreenFragment) fragmentFindFragmentByTag).onTouch(fragmentFindFragmentByTag.getView(), motionEvent);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void allowDrawerOpening(boolean z) {
        this.mDrawerHelper.setAllowDrawerSlide(z);
    }

    @Override // com.sonymobile.calendar.WelcomeScreenFragment.WelcomeScreenCloseListener
    public void onWelcomeScreenClose() {
        setRequestedOrientation(-1);
        getSupportActionBar().show();
        getSupportFragmentManager().popBackStack();
        showPermissionAcceptedDialogIfNeeded();
    }

    private void showPermissionAcceptedDialogIfNeeded() {
        if (!CustomizeConfig.isShowingPermissionAcceptanceDialogEnabled(this) || Utils.getSharedPreference((Context) this, PermissionAcceptanceActivity.KEY_REMEMBER_MY_SELECTION, false)) {
            return;
        }
        startActivityForResult(PermissionAcceptanceActivity.getPermissionAcceptanceIntent(this, PERMISSIONS, R.string.permission_description, R.string.calendar_tries_to_title, R.string.dialog_privacy_policy_link), 1001);
    }

    private void handleNotification(int i, Intent intent) {
        intent.setAction(null);
        if (i == 1) {
            intent.setClass(this, DismissAlarmsService.class);
        } else if (i == 2) {
            intent.setClass(this, TasksDismissAlarmsService.class);
        } else {
            Log.d(TAG, "handleNotification: not support notification type");
            return;
        }
        startService(intent);
    }
}
