package com.sonymobile.calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.birthday.BirthdayService;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.tablet.HeaderBase;
import com.sonymobile.calendar.tablet.INavigationMonthGridController;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CalendarFragment extends Fragment implements ICalendarFragment {
    private static final String KEY_HOUR_HEIGHT_LANDSCAPE = "hour_height_landscape";
    private static final String KEY_HOUR_HEIGHT_PORTRAIT = "hour_height_portrait";
    private static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    protected AllDayViewBase allDayView;
    protected CalendarGridViewBase calendarGridView;
    private ActionBarControllerBase mActionBarController;
    private IDatePicker mDatePicker;
    private View mRootView;
    protected Time mSelectedDay;
    private INavigationMonthGridController navigationMonthGridController;
    protected WeekNavigatorView navigatorView;
    private PermissionContactsDialog permissionContactsDialog;
    private HeaderBase tabletHeader;
    protected boolean isWeekFragment = false;
    protected String dateString = "";
    private boolean isWaitingForAllEventChanges = false;
    private Runnable mUpdateTZ = new Runnable() { // from class: com.sonymobile.calendar.CalendarFragment.1
        @Override // java.lang.Runnable
        public void run() {
            CalendarFragment.this.mSelectedDay.timezone = Utils.getTimeZone(CalendarFragment.this.getActivity(), this);
            CalendarFragment.this.mSelectedDay.normalize(true);
        }
    };
    private boolean readPermission = false;
    private boolean isShowing = false;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.CalendarFragment.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.TIME_SET") || action.equals("android.intent.action.DATE_CHANGED") || action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                CalendarFragment.this.eventsChanged();
            }
        }
    };
    private ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.sonymobile.calendar.CalendarFragment.3
        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            CalendarFragment.this.eventsChanged();
        }
    };
    private ContentObserver mBirthdayChangedObserver = new ContentObserver(new Handler()) { // from class: com.sonymobile.calendar.CalendarFragment.4
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            BirthdayService.INSTANCE.clearCache();
            CalendarFragment.this.allDayView.goToDate(CalendarFragment.this.mSelectedDay, false);
        }
    };
    private BroadcastReceiver timeTicker = new BroadcastReceiver() { // from class: com.sonymobile.calendar.CalendarFragment.6
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.TIME_TICK")) {
                CalendarFragment.this.invalidateTimeLine();
                CalendarFragment.this.invalidateCurrentView();
            }
        }
    };

    @Override // com.sonymobile.calendar.ICalendarFragment
    public void onTransitionComplete() {
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity instanceof LaunchActivity) {
            ActionBarControllerBase actionBarController = ((LaunchActivity) appCompatActivity).getActionBarController();
            this.mActionBarController = actionBarController;
            actionBarController.onFragmentAttached(getClass().getName());
        }
        initDateSwitcher(this.mRootView);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        FragmentActivity activity = getActivity();
        super.onCreate(bundle);
        Time time = new SafeTime(Utils.getTimeZone(activity, this.mUpdateTZ));
        this.mSelectedDay = time;
        time.switchTimezone(Utils.getTimeZone(activity, this.mUpdateTZ));
    }

    private void checkContactsPermission() {
        boolean z = getActivity().getPackageManager().checkPermission(READ_CONTACTS, getContext().getPackageName()) == 0;
        this.readPermission = z;
        if (z) {
            PermissionContactsDialog permissionContactsDialog = this.permissionContactsDialog;
            if (permissionContactsDialog != null) {
                permissionContactsDialog.dismiss();
            }
            refreshCalendarFragment();
            return;
        }
        showRequestPermissionDialog();
    }

    private void showRequestPermissionDialog() {
        String[] strArr = {READ_CONTACTS};
        if (this.permissionContactsDialog == null) {
            this.permissionContactsDialog = PermissionContactsDialog.newInstance(strArr);
            AppCompatActivity appCompatActivity = getContext() instanceof AppCompatActivity ? (AppCompatActivity) getContext() : null;
            if (appCompatActivity != null) {
                this.permissionContactsDialog.show(appCompatActivity.getSupportFragmentManager(), PermissionContactsDialog.TAG);
            }
            this.isShowing = true;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = getView();
        this.mRootView = view;
        CalendarGridViewBase calendarGridViewBase = (CalendarGridViewBase) view.findViewById(R.id.day_week_view);
        this.calendarGridView = calendarGridViewBase;
        calendarGridViewBase.setCalendarFragment(this);
        AllDayViewBase allDayViewBase = (AllDayViewBase) this.mRootView.findViewById(R.id.all_day_view);
        this.allDayView = allDayViewBase;
        allDayViewBase.setCalendarFragment(this);
        FragmentActivity activity = getActivity();
        if (activity instanceof LaunchActivity) {
            this.mSelectedDay.set(Utils.getDisplayTime());
            ((LaunchActivity) activity).getActionBarController().onViewNavigated(this.mSelectedDay, null, getClass().getName().equals(DayFragment.class.getName()) ? ViewType.DAY : ViewType.WEEK);
        }
        setHourHeight(activity, bundle);
        return this.mRootView;
    }

    private void setHourHeight(Activity activity, Bundle bundle) {
        boolean z = Build.VERSION.SDK_INT >= 24 && getActivity().isInMultiWindowMode();
        if (bundle != null) {
            if ((Utils.isInLandscapeMode(activity) && !z) || (!Utils.isInLandscapeMode(activity) && z)) {
                if (bundle.getInt(KEY_HOUR_HEIGHT_LANDSCAPE) != 0) {
                    this.calendarGridView.setHourHeight(bundle.getInt(KEY_HOUR_HEIGHT_LANDSCAPE));
                }
                if (bundle.getInt(KEY_HOUR_HEIGHT_PORTRAIT) != 0) {
                    this.calendarGridView.setHourHeightPortraitValue(bundle.getInt(KEY_HOUR_HEIGHT_PORTRAIT));
                    return;
                }
                return;
            }
            if (bundle.getInt(KEY_HOUR_HEIGHT_PORTRAIT) != 0) {
                this.calendarGridView.setHourHeight(bundle.getInt(KEY_HOUR_HEIGHT_PORTRAIT));
            }
            if (bundle.getInt(KEY_HOUR_HEIGHT_LANDSCAPE) != 0) {
                this.calendarGridView.setHourHeightLandscapeValue(bundle.getInt(KEY_HOUR_HEIGHT_LANDSCAPE));
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        checkContactsPermission();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        checkContactsPermission();
    }

    private void refreshCalendarFragment() {
        EventLoaderService.getInstance().setHideDeclinedEvents(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(GeneralPreferences.KEY_HIDE_DECLINED, true));
        resumeDisplayTime();
        FreeDayService.getInstance().requestLoad(getActivity(), new FreeDayServiceHandler(), 0, true);
        if (!this.isWeekFragment) {
            updateDateSwitcher(getActivity(), this.mSelectedDay);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getActivity().registerReceiver(this.mIntentReceiver, intentFilter, 2);
        getActivity().getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, this.mObserver);
        getActivity().getContentResolver().registerContentObserver(ContactsContract.Data.CONTENT_URI, true, this.mBirthdayChangedObserver);
        if (LunarAvailabilityManager.isLunarAvailable(getActivity())) {
            getActivity().getContentResolver().registerContentObserver(LunarContract.Events.CONTENT_URI, true, this.mObserver);
        }
        getActivity().registerReceiver(this.timeTicker, new IntentFilter("android.intent.action.TIME_TICK"), 2);
        invalidateTimeLine();
        updateAdjacentViews();
        FreeDayService.getInstance().loadWeekendDays(getActivity());
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        CalendarGridViewBase calendarGridViewBase = this.calendarGridView;
        if (calendarGridViewBase == null || calendarGridViewBase.columnContainers == null) {
            return;
        }
        if (Utils.isInLandscapeMode(getActivity())) {
            bundle.putInt(KEY_HOUR_HEIGHT_PORTRAIT, ((DayColumnContainer) this.calendarGridView.columnContainers[1]).getHourHeight());
            if (this.calendarGridView.getHourHeightLandscapeValue() != 0) {
                bundle.putInt(KEY_HOUR_HEIGHT_LANDSCAPE, this.calendarGridView.getHourHeightLandscapeValue());
                return;
            }
            return;
        }
        bundle.putInt(KEY_HOUR_HEIGHT_LANDSCAPE, ((DayColumnContainer) this.calendarGridView.columnContainers[1]).getHourHeight());
        if (this.calendarGridView.getHourHeightPortraitValue() != 0) {
            bundle.putInt(KEY_HOUR_HEIGHT_PORTRAIT, this.calendarGridView.getHourHeightPortraitValue());
        }
    }

    private void updateAdjacentViews() {
        CalendarGridViewBase calendarGridViewBase = this.calendarGridView;
        if (calendarGridViewBase != null) {
            calendarGridViewBase.updateAdjacentViews();
        }
        WeekNavigatorView weekNavigatorView = this.navigatorView;
        if (weekNavigatorView != null) {
            weekNavigatorView.updateAdjacentViews();
        }
        AllDayViewBase allDayViewBase = this.allDayView;
        if (allDayViewBase != null) {
            allDayViewBase.updateAdjacentViews();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Utils.setDisplayTime(Long.valueOf(Utils.getDisplayTime()));
        if (this.readPermission) {
            if (this.isShowing) {
                this.permissionContactsDialog.dismiss();
            }
            getActivity().getContentResolver().unregisterContentObserver(this.mObserver);
            getActivity().getContentResolver().unregisterContentObserver(this.mBirthdayChangedObserver);
            getActivity().unregisterReceiver(this.mIntentReceiver);
            IDatePicker iDatePicker = this.mDatePicker;
            if (iDatePicker != null) {
                iDatePicker.dismiss();
                this.mDatePicker = null;
            }
            getActivity().unregisterReceiver(this.timeTicker);
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        if (this.isWeekFragment) {
            Time time2 = new SafeTime(Utils.getTimeZone(getActivity(), null));
            time2.setToNow();
            if (time.year == time2.year && time.getWeekNumber() == time2.getWeekNumber()) {
                time = time2;
            }
        }
        this.mSelectedDay = time;
        Utils.setDisplayTime(Long.valueOf(time.toMillis(false)));
        this.calendarGridView.requestFocus();
        updateDateSwitcher(getActivity(), this.mSelectedDay);
        this.calendarGridView.goToDate(this.mSelectedDay, z);
        this.allDayView.goToDate(this.mSelectedDay, z);
        WeekNavigatorView weekNavigatorView = this.navigatorView;
        if (weekNavigatorView != null) {
            weekNavigatorView.goToDate(this.mSelectedDay, z);
        }
        updateActionBar(this.mSelectedDay);
        this.calendarGridView.sendWindowChangedAccessibilityEvent();
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
        Time time = new SafeTime(this.mSelectedDay);
        time.set(System.currentTimeMillis());
        time.normalize(true);
        if (time.monthDay == this.mSelectedDay.monthDay && time.month == this.mSelectedDay.month && time.year == this.mSelectedDay.year) {
            return;
        }
        goTo(time, true);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
        this.mSelectedDay = this.calendarGridView.goToNext();
        this.allDayView.goToNext();
        if (Utils.isWeekClearOfEpochUpperLimit(this.mSelectedDay.year, this.mSelectedDay.month, this.mSelectedDay.monthDay)) {
            WeekNavigatorView weekNavigatorView = this.navigatorView;
            if (weekNavigatorView != null) {
                weekNavigatorView.goToNext();
            }
            updateDateSwitcher(getActivity(), this.mSelectedDay);
            Utils.setDisplayTime(Long.valueOf(this.mSelectedDay.toMillis(false)));
            updateActionBar(this.mSelectedDay);
            this.calendarGridView.sendWindowChangedAccessibilityEvent();
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
        this.mSelectedDay = this.calendarGridView.goToPrevious();
        this.allDayView.goToPrevious();
        WeekNavigatorView weekNavigatorView = this.navigatorView;
        if (weekNavigatorView != null) {
            weekNavigatorView.goToPrevious();
        }
        updateDateSwitcher(getActivity(), this.mSelectedDay);
        Utils.setDisplayTime(Long.valueOf(this.mSelectedDay.toMillis(false)));
        updateActionBar(this.mSelectedDay);
        this.calendarGridView.sendWindowChangedAccessibilityEvent();
    }

    @Override // com.sonymobile.calendar.ICalendarFragment
    public void onCalendarClicked(Time time, boolean z) {
        this.allDayView.removeAddEventView();
        this.calendarGridView.removeAddEventView();
    }

    @Override // com.sonymobile.calendar.ICalendarFragment
    public int[] getCalendarLeftTop() {
        int[] iArr = new int[2];
        this.allDayView.getLocationInWindow(iArr);
        return iArr;
    }

    @Override // com.sonymobile.calendar.ICalendarFragment
    public int[] getCalendarDimensions() {
        return new int[]{this.calendarGridView.getWidth(), this.allDayView.getHeight() + this.calendarGridView.getHeight()};
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return this.calendarGridView.getSelectedTimeInMillis();
    }

    public Time getSelectedTime() {
        return this.calendarGridView.getSelectedTime();
    }

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return this.dateString;
    }

    @Override // com.sonymobile.calendar.CalendarSwipeListener
    public void onSwipe(int i) {
        this.calendarGridView.swipeTo(i);
        this.allDayView.swipeTo(i);
        WeekNavigatorView weekNavigatorView = this.navigatorView;
        if (weekNavigatorView != null) {
            weekNavigatorView.swipeTo(i);
        }
    }

    @Override // com.sonymobile.calendar.CalendarSwipeListener
    public void onSwipeCentered() {
        this.calendarGridView.onSwipeCentered();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v2, types: [com.sonymobile.calendar.CalendarFragment$5] */
    public void eventsChanged() {
        if (this.isWaitingForAllEventChanges) {
            return;
        }
        this.isWaitingForAllEventChanges = true;
        new CountDownTimer(200L, 100L) { // from class: com.sonymobile.calendar.CalendarFragment.5
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                CalendarFragment.this.calendarGridView.reloadEvents();
                CalendarFragment.this.allDayView.reloadEvents();
                CalendarFragment.this.isWaitingForAllEventChanges = false;
            }
        }.start();
    }

    public void setNavigationMonthGridController(INavigationMonthGridController iNavigationMonthGridController) {
        this.navigationMonthGridController = iNavigationMonthGridController;
    }

    protected void initDateSwitcher(View view) {
        ActionBarControllerBase actionBarControllerBase;
        if (Utils.isTabletDevice(getActivity()) && (actionBarControllerBase = this.mActionBarController) != null) {
            HeaderBase headerBase = (HeaderBase) actionBarControllerBase.getTitle();
            this.tabletHeader = headerBase;
            headerBase.setOnDateSwitcherClickedListener(new DateSwitcherListener());
        }
        WeekNavigatorView weekNavigatorView = (WeekNavigatorView) view.findViewById(R.id.day_week_navigator_view);
        this.navigatorView = weekNavigatorView;
        weekNavigatorView.setCalendarFragment(this);
    }

    private void updateDateSwitcher(Context context, Time time) {
        if (getResources().getBoolean(R.bool.tablet_mode)) {
            HeaderBase headerBase = this.tabletHeader;
            if (headerBase != null) {
                headerBase.update(context, time);
            }
            INavigationMonthGridController iNavigationMonthGridController = this.navigationMonthGridController;
            if (iNavigationMonthGridController != null) {
                iNavigationMonthGridController.updateNavigationMonthGrid(time, false);
            }
        }
    }

    private void resumeDisplayTime() {
        long displayTime = Utils.getDisplayTime();
        if (displayTime <= 0) {
            displayTime = System.currentTimeMillis();
        }
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.mUpdateTZ));
        time.set(displayTime);
        goTo(time, false);
    }

    protected class DateSwitcherListener implements OnDateSwitcherClickedListener {
        public DateSwitcherListener() {
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onPreviousButtonClicked() {
            CalendarFragment.this.goToPrevious(0.0f);
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onNextButtonClicked() {
            CalendarFragment.this.goToNext(0.0f);
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onDateLabelClicked() {
            IDatePicker dayPicker;
            if (CalendarFragment.this.mDatePicker != null) {
                CalendarFragment.this.mDatePicker.dismiss();
            }
            CalendarFragment calendarFragment = CalendarFragment.this;
            if (calendarFragment.isWeekFragment) {
                dayPicker = new WeekPicker(CalendarFragment.this.getActivity(), CalendarFragment.this.mSelectedDay);
            } else {
                dayPicker = new DayPicker(CalendarFragment.this.getActivity(), CalendarFragment.this.mSelectedDay);
            }
            calendarFragment.mDatePicker = dayPicker;
            CalendarFragment.this.mDatePicker.setOnDatePickerSetListener(new OnDatePickerSetListener() { // from class: com.sonymobile.calendar.CalendarFragment.DateSwitcherListener.1
                @Override // com.sonymobile.calendar.OnDatePickerSetListener
                public void onDateSet(Time time) {
                    if (time.year == CalendarFragment.this.mSelectedDay.year && time.month == CalendarFragment.this.mSelectedDay.month && time.monthDay == CalendarFragment.this.mSelectedDay.monthDay) {
                        return;
                    }
                    CalendarFragment.this.goTo(time, true);
                }
            });
            CalendarFragment.this.mDatePicker.show(CalendarFragment.this.getFragmentManager(), "month_picker");
        }
    }

    private class FreeDayServiceHandler implements IAsyncServiceResultHandler {
        private FreeDayServiceHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() != 0 || CalendarFragment.this.navigatorView == null) {
                return;
            }
            CalendarFragment.this.navigatorView.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateTimeLine() {
        CalendarGridViewBase calendarGridViewBase = this.calendarGridView;
        if (calendarGridViewBase != null) {
            calendarGridViewBase.invalidateTimeLine();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateCurrentView() {
        CalendarGridViewBase calendarGridViewBase = this.calendarGridView;
        if (calendarGridViewBase != null) {
            calendarGridViewBase.invalidateCurrentView();
        }
    }
}
