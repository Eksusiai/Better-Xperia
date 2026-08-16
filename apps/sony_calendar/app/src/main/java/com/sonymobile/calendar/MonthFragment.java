package com.sonymobile.calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import androidx.fragment.app.Fragment;
import com.google.common.primitives.Ints;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.datetimepicker.date.DatePickerDialog;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.tablet.TabletMonthSwitcher;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class MonthFragment extends Fragment implements ICalendarFragment {
    private MonthView gridView;
    private Activity mActivity;
    private DatePickerDialog mDialog;
    private IMonthFragmentParent mParent;
    public Time mTime;
    private WeekdayLabelsView mWeekdayLabelsView;
    private MonthPicker monthPicker;
    private View rootView;
    private TabletMonthSwitcher tabletMonthSwitcher;
    private CharSequence dateString = "";
    private boolean isWaitingForAllEventChanges = false;
    private boolean mIsTransitionAnimationExecuting = false;
    private boolean mIsInAgendaView = false;
    private boolean mIsTransitionAnimationStartedInWeek = false;
    private boolean firstTime = true;
    private boolean mShouldRefreshViews = true;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.sonymobile.calendar.MonthFragment.1
        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            MonthFragment.this.mShouldRefreshViews = true;
        }
    };
    private final BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.MonthFragment.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                MonthFragment.this.mShouldRefreshViews = true;
            }
        }
    };
    private Runnable mUpdateTZ = new Runnable() { // from class: com.sonymobile.calendar.MonthFragment.3
        @Override // java.lang.Runnable
        public void run() {
            MonthFragment.this.mTime.timezone = Utils.getTimeZone(MonthFragment.this.getActivity(), this);
            MonthFragment.this.mTime.normalize(true);
        }
    };
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.MonthFragment.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.TIME_SET") || action.equals("android.intent.action.DATE_CHANGED") || action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                MonthFragment.this.eventsChanged();
            }
        }
    };
    private final ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.sonymobile.calendar.MonthFragment.6
        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            MonthFragment.this.eventsChanged();
        }
    };

    @Override // com.sonymobile.calendar.CalendarSwipeListener
    public void onSwipe(int i) {
    }

    @Override // com.sonymobile.calendar.CalendarSwipeListener
    public void onSwipeCentered() {
    }

    @Override // com.sonymobile.calendar.ICalendarFragment
    public void drawingCompleted() {
        if (this.firstTime) {
            this.firstTime = false;
            this.gridView.updateAdjacentViews();
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        if (Utils.areDatesEqual(time, this.mTime) && !this.mShouldRefreshViews) {
            eventsChanged();
            return;
        }
        this.mTime = time;
        this.mShouldRefreshViews = false;
        if (this.mIsInAgendaView) {
            Utils.setDisplayTime(Long.valueOf(time.toMillis(false)));
        }
        this.gridView.goToDate(time, z);
        updateSelectedDate(this.mTime);
        updateActionBar(this.mTime);
        this.gridView.sendWindowChangedAccessibilityEvent();
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
        if (isAdded()) {
            Time time = new SafeTime(this.mTime);
            time.set(System.currentTimeMillis());
            if (isDateVisible(time)) {
                if (time.yearDay != this.mTime.yearDay) {
                    updateSelectedDate(time);
                    this.gridView.resetAuxiliaryTime();
                    this.gridView.invalidateGrid(1);
                    this.gridView.clearOldSelections();
                    updateAgendaList(time);
                    updateActionBar(time);
                }
                this.gridView.scrollToSelectedPosition(false);
                return;
            }
            goTo(time, true);
        }
    }

    private void goToNextOrPrevious(boolean z) {
        if (this.mIsInAgendaView) {
            updateSelectedDate(this.gridView.getNextViewTime(this.mTime, z));
            this.gridView.invalidateGrid(z ? 2 : 0);
        }
        this.gridView.clearOldSelections();
        this.mTime = z ? this.gridView.goToNext() : this.gridView.goToPrevious();
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), null));
        time.setToNow();
        if (isDateVisible(time)) {
            this.mTime = time;
        }
        Utils.setDisplayTime(Long.valueOf(this.mTime.toMillis(false)));
        this.gridView.sendWindowChangedAccessibilityEvent();
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
        boolean z = false;
        boolean z2 = !isInAgendaView() && Utils.isMonthClearOfEpochUpperLimit(this.mTime.year, this.mTime.month);
        if (isInAgendaView() && Utils.isWeekClearOfEpochUpperLimit(this.mTime.year, this.mTime.month, this.mTime.monthDay)) {
            z = true;
        }
        if (z2 || z) {
            goToNextOrPrevious(true);
        } else {
            this.gridView.animateBack();
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
        goToNextOrPrevious(false);
    }

    public void onTransitionAnimationStart(int i, int i2) {
        this.gridView.prepareTransition(this.mTime, i, i2);
        this.mIsTransitionAnimationExecuting = true;
    }

    public void onTransitionAnimationCompleted() {
        this.gridView.onTransitionCompleted();
        this.mIsTransitionAnimationExecuting = false;
        this.mIsTransitionAnimationStartedInWeek = false;
    }

    public boolean isTransitionAnimationOngoing() {
        return this.mIsTransitionAnimationExecuting;
    }

    public void showWeek() {
        this.gridView.setVisibility(8);
        MonthWeekView monthWeekView = (MonthWeekView) this.rootView.findViewById(R.id.month_week_view);
        this.gridView = monthWeekView;
        if (monthWeekView != null) {
            monthWeekView.setCalendarFragment(this);
            this.gridView.goToDate(this.mTime, false);
            this.gridView.setVisibility(0);
            this.gridView.updateAdjacentViews();
        }
        this.mIsInAgendaView = true;
    }

    public boolean isGoingFromWeek() {
        return this.mIsTransitionAnimationStartedInWeek;
    }

    public void showMonth() {
        int width = this.gridView.getWidth();
        this.gridView.setVisibility(8);
        MonthView monthView = (MonthView) this.rootView.findViewById(R.id.month_view);
        this.gridView = monthView;
        monthView.setCalendarFragment(this);
        this.gridView.goToDate(this.mTime, false);
        this.gridView.setVisibility(0);
        if (this.gridView.getWidth() == 0) {
            this.gridView.measure(View.MeasureSpec.makeMeasureSpec(width, Ints.MAX_POWER_OF_TWO), View.MeasureSpec.makeMeasureSpec(0, 0));
        }
        this.gridView.updateAdjacentViews();
        if (this.mIsInAgendaView) {
            this.mIsTransitionAnimationStartedInWeek = true;
        }
        this.mIsInAgendaView = false;
    }

    public boolean isInAgendaView() {
        return this.mIsInAgendaView;
    }

    public void startInAgendaMode(boolean z) {
        this.mIsInAgendaView = z;
    }

    @Override // com.sonymobile.calendar.ICalendarFragment
    public void onTransitionComplete() {
        updateDateSwitcher(getActivity(), this.mTime);
        updateAgendaList(this.mTime);
        updateActionBar(this.mTime);
        this.gridView.invalidateGrid(1);
    }

    @Override // com.sonymobile.calendar.ICalendarFragment
    public void onCalendarClicked(Time time, boolean z) {
        int i;
        if (Utils.areDatesEqual(time, this.mTime)) {
            return;
        }
        if (isDateVisible(time)) {
            updateSelectedDate(time);
            this.gridView.clearOldSelections();
            i = 1;
            updateAgendaList(time);
        } else if (Time.compare(time, this.mTime) < 0) {
            goToPrevious(0.0f);
            updateSelectedDate(time);
            i = 0;
        } else {
            goToNext(0.0f);
            updateSelectedDate(time);
            i = 2;
        }
        if (z) {
            this.gridView.forceRefresh(i);
        } else {
            this.gridView.invalidateGrid(i);
        }
        updateActionBar(time);
    }

    @Override // com.sonymobile.calendar.ICalendarFragment
    public int[] getCalendarLeftTop() {
        int[] iArr = new int[2];
        this.gridView.getLocationInWindow(iArr);
        return iArr;
    }

    @Override // com.sonymobile.calendar.ICalendarFragment
    public int[] getCalendarDimensions() {
        return new int[]{this.gridView.getWidth(), this.gridView.getHeight()};
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return this.mTime.toMillis(true);
    }

    public long getDisplayedPreciseTime() {
        return this.gridView.getSelectedTimeInMillis();
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
        ((NavigationListener) this.mActivity).onViewNavigated(time, null, ViewType.MONTH);
        this.dateString = ((LaunchActivity) this.mActivity).getActionBarController().getTitle().getText();
    }

    public void updateSelectedDate(Time time) {
        this.mTime = new SafeTime(time);
        Utils.setDisplayTime(Long.valueOf(time.toMillis(false)));
    }

    public Time getSelectedDate() {
        return this.mTime;
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [com.sonymobile.calendar.MonthFragment$4] */
    public void eventsChanged() {
        if (this.isWaitingForAllEventChanges) {
            return;
        }
        this.isWaitingForAllEventChanges = true;
        new CountDownTimer(200L, 100L) { // from class: com.sonymobile.calendar.MonthFragment.4
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                MonthFragment.this.gridView.reloadEvents();
                MonthFragment.this.isWaitingForAllEventChanges = false;
            }
        }.start();
    }

    private boolean isDateVisible(Time time) {
        if (!(this.mIsInAgendaView && (UiUtils.isPortrait(getActivity()) || UiUtils.isSub320dpScreen(getActivity())))) {
            return this.mTime.year == time.year && this.mTime.month == time.month;
        }
        Time[] days = WeekUtils.getDays(getActivity(), this.mTime, true);
        return (days[0].year < time.year || (days[0].year == time.year && days[0].yearDay <= time.yearDay)) && (days[6].year > time.year || (days[6].year == time.year && days[6].yearDay >= time.yearDay));
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mTime = new SafeTime(Utils.getTimeZone(getActivity(), this.mUpdateTZ));
        GeneralPreferences.getSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this.mPreferenceChangeListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.TIME_SET");
        getActivity().registerReceiver(this.mTimeChangeReceiver, intentFilter, 2);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        GeneralPreferences.getSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this.mPreferenceChangeListener);
        getActivity().unregisterReceiver(this.mTimeChangeReceiver);
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        refreshMonthFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshMonthFragment();
    }

    private void refreshMonthFragment() {
        EventLoaderService.getInstance().setHideDeclinedEvents(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(GeneralPreferences.KEY_HIDE_DECLINED, true));
        FreeDayService.getInstance().loadWeekendDays(getActivity());
        resumeDisplayedDate();
        FreeDayService.getInstance().requestLoad(getActivity(), new FreeDayServiceHandler(), 0, true);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getActivity().registerReceiver(this.mIntentReceiver, intentFilter, 2);
        getActivity().getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, this.mObserver);
        if (LunarAvailabilityManager.isLunarAvailable(getActivity())) {
            getActivity().getContentResolver().registerContentObserver(LunarContract.Events.CONTENT_URI, true, this.mObserver);
        }
        if (UiUtils.isSub320dpScreen(getActivity())) {
            showWeek();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().getContentResolver().unregisterContentObserver(this.mObserver);
        getActivity().unregisterReceiver(this.mIntentReceiver);
        MonthPicker monthPicker = this.monthPicker;
        if (monthPicker != null) {
            monthPicker.dismiss();
            this.monthPicker = null;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        initDateSwitcher(this.rootView);
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong(LunarContract.EXTRA_EVENT_BEGIN_TIME, this.mTime.toMillis(true));
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        MonthView monthView;
        Utils.processBundledBeginEventTime(bundle);
        this.rootView = layoutInflater.inflate(R.layout.month_activity, (ViewGroup) null);
        if (this.mIsInAgendaView && (UiUtils.isPortrait(getActivity()) || UiUtils.isSub320dpScreen(getActivity()))) {
            monthView = (MonthWeekView) this.rootView.findViewById(R.id.month_week_view);
        } else {
            monthView = (MonthView) this.rootView.findViewById(R.id.month_view);
        }
        this.gridView = monthView;
        monthView.setVisibility(0);
        this.gridView.setCalendarFragment(this);
        ActionBarControllerBase actionBarController = ((LaunchActivity) this.mActivity).getActionBarController();
        this.gridView.setNextFocusUpId(actionBarController.getTitleId());
        this.mTime.set(Utils.getDisplayTime());
        actionBarController.onViewNavigated(this.mTime, null, ViewType.MONTH);
        if (!UiUtils.isSub320dpScreen(getActivity()) && UiUtils.isLandscape(getActivity()) && !Utils.isTabletDevice(getActivity())) {
            WeekdayLabelsView weekdayLabelsView = (WeekdayLabelsView) this.rootView.findViewById(R.id.weekdays_label);
            this.mWeekdayLabelsView = weekdayLabelsView;
            weekdayLabelsView.setVisibility(0);
            goTo(this.mTime, false);
            this.gridView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.sonymobile.calendar.MonthFragment.7
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    MonthFragment.this.mWeekdayLabelsView.setDateBoxGridCellWidth(MonthFragment.this.gridView.getCurrentDateBoxGridCellWidth());
                    MonthFragment.this.mWeekdayLabelsView.setDayLabels(MonthFragment.this.gridView.getCurrentDateBoxDayLabels());
                    MonthFragment.this.mWeekdayLabelsView.setDayLabelsColor(MonthFragment.this.gridView.getCurrentDateBoxDayLabelColors());
                    return true;
                }
            });
        }
        return this.rootView;
    }

    public void updateAgendaList(Time time) {
        if (this.mParent != null) {
            if (this.mIsInAgendaView || (time.year == this.mTime.year && time.month == this.mTime.month)) {
                this.mParent.updateAgendaList(getSelectedDate());
            } else {
                this.mParent.hideAgendaList();
            }
        }
    }

    public void setParent(IMonthFragmentParent iMonthFragmentParent) {
        this.mParent = iMonthFragmentParent;
    }

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return this.dateString;
    }

    private void resumeDisplayedDate() {
        long displayTime = Utils.getDisplayTime();
        if (displayTime <= 0) {
            displayTime = System.currentTimeMillis();
        }
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.mUpdateTZ));
        time.set(displayTime);
        goTo(time, false);
    }

    private void initDateSwitcher(View view) {
        if (getResources().getBoolean(R.bool.tablet_mode)) {
            DateSwitcherListener dateSwitcherListener = new DateSwitcherListener();
            TabletMonthSwitcher tabletMonthSwitcher = new TabletMonthSwitcher(this, view);
            this.tabletMonthSwitcher = tabletMonthSwitcher;
            tabletMonthSwitcher.setOnDateSwitcherClickedListener(dateSwitcherListener);
        }
    }

    private void updateDateSwitcher(Context context, Time time) {
        if (isAdded() && getResources().getBoolean(R.bool.tablet_mode)) {
            this.tabletMonthSwitcher.updateMonthSwitcher(time);
        }
    }

    private class DateSwitcherListener implements OnDateSwitcherClickedListener {
        private DateSwitcherListener() {
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onPreviousButtonClicked() {
            if (MonthFragment.this.getResources().getBoolean(R.bool.tablet_mode)) {
                Time time = new SafeTime(MonthFragment.this.mTime);
                time.year--;
                MonthFragment.this.goTo(time, true);
                return;
            }
            MonthFragment.this.goToPrevious(0.0f);
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onNextButtonClicked() {
            if (MonthFragment.this.getResources().getBoolean(R.bool.tablet_mode)) {
                Time time = new SafeTime(MonthFragment.this.mTime);
                time.year++;
                MonthFragment.this.goTo(time, true);
                return;
            }
            MonthFragment.this.goToNext(0.0f);
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onDateLabelClicked() {
            if (MonthFragment.this.mDialog != null) {
                MonthFragment.this.mDialog.dismiss();
            }
            MonthFragment.this.mDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() { // from class: com.sonymobile.calendar.MonthFragment.DateSwitcherListener.1
                @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.OnDateSetListener
                public void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
                    if (i == MonthFragment.this.mTime.year && i2 == MonthFragment.this.mTime.month) {
                        return;
                    }
                    Time time = new SafeTime(MonthFragment.this.mTime);
                    time.year = i;
                    time.month = i2;
                    time.monthDay = i3;
                    time.normalize(false);
                    MonthFragment.this.goTo(time, true);
                }
            }, MonthFragment.this.mTime.year, MonthFragment.this.mTime.month, MonthFragment.this.mTime.monthDay, 2);
            MonthFragment.this.mDialog.show(MonthFragment.this.getFragmentManager(), "datepicker");
        }
    }

    private class FreeDayServiceHandler implements IAsyncServiceResultHandler {
        private FreeDayServiceHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (obj != null && ((Integer) obj2).intValue() == 0 && ((Boolean) obj).booleanValue()) {
                MonthFragment.this.gridView.invalidate();
            }
        }
    }

    public int getCalendarBottom() {
        return getCalendarLeftTop()[1] + getCalendarDimensions()[1];
    }
}
