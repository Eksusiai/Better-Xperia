package com.sonymobile.calendar.tablet;
import com.sonymobile.calendar.SafeTime;

import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.DayPicker;
import com.sonymobile.calendar.IDatePicker;
import com.sonymobile.calendar.MonthSwitcher;
import com.sonymobile.calendar.Navigator;
import com.sonymobile.calendar.OnDatePickerSetListener;
import com.sonymobile.calendar.OnDateSwitcherClickedListener;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WeekPicker;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class NavigationMonthGridFragment extends Fragment implements Navigator {
    public static final String TAG = "NavigationMonthGridFragment";
    private IDatePicker mDatePicker;
    private int mStartDay;
    protected Time mTime;
    private View mView;
    private MonthSwitcher monthSwitcher;
    private NavigationMonthGrid navigationMonthGrid;
    private INavigationMonthGridFragmentParent navigationParent;
    private boolean mStarted = true;
    private Time mCurrentDay = new SafeTime();
    private Runnable mUpdateTZ = new Runnable() { // from class: com.sonymobile.calendar.tablet.NavigationMonthGridFragment.1
        @Override // java.lang.Runnable
        public void run() {
            NavigationMonthGridFragment.this.mTime.timezone = Utils.getTimeZone(NavigationMonthGridFragment.this.getActivity(), this);
            NavigationMonthGridFragment.this.mTime.normalize(true);
            NavigationMonthGridFragment.this.monthSwitcher.updateDateLabel(NavigationMonthGridFragment.this.getActivity(), NavigationMonthGridFragment.this.mTime);
            NavigationMonthGridFragment.this.navigationMonthGrid.updateToday(NavigationMonthGridFragment.this.mTime.timezone);
        }
    };

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return "";
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
    }

    public void setNavigationMonthGridFragmentParent(INavigationMonthGridFragmentParent iNavigationMonthGridFragmentParent) {
        this.navigationParent = iNavigationMonthGridFragmentParent;
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        if (this.monthSwitcher == null) {
            return;
        }
        time.normalize(true);
        this.mTime = time;
        this.monthSwitcher.updateDateLabel(getActivity(), this.mTime);
        this.navigationMonthGrid.updateMonth(this.mTime);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.mUpdateTZ));
        time.set(System.currentTimeMillis());
        time.minute = 0;
        time.second = 0;
        time.normalize(false);
        this.mTime = time;
        this.monthSwitcher.updateDateLabel(getActivity(), this.mTime);
        this.navigationMonthGrid.updateMonth(this.mTime);
        Utils.setDisplayTime(Long.valueOf(this.mTime.toMillis(false)));
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return this.mTime.toMillis(false);
    }

    int getStartDay() {
        return this.mStartDay;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mStarted = true;
        FragmentActivity activity = getActivity();
        long jCurrentTimeMillis = bundle != null ? bundle.getLong(LunarContract.EXTRA_EVENT_BEGIN_TIME) : 0L;
        if (jCurrentTimeMillis == 0) {
            jCurrentTimeMillis = Utils.getDisplayTime();
        }
        if (jCurrentTimeMillis == 0) {
            jCurrentTimeMillis = System.currentTimeMillis();
        }
        Time time = new SafeTime(Utils.getTimeZone(activity, this.mUpdateTZ));
        this.mTime = time;
        time.set(jCurrentTimeMillis);
        this.mTime.normalize(true);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Time time = new SafeTime();
        this.mCurrentDay = time;
        time.set(getSelectedTimeInMillis());
        IDatePicker iDatePicker = this.mDatePicker;
        if (iDatePicker != null) {
            iDatePicker.dismiss();
            this.mDatePicker = null;
        }
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
        FragmentActivity activity = getActivity();
        if (Utils.getDisplayTime() > 0) {
            Time time = new SafeTime(Utils.getTimeZone(activity, this.mUpdateTZ));
            time.set(Utils.getDisplayTime());
            goTo(time, false);
        }
        this.mTime.set(Utils.getDisplayTime());
        this.mUpdateTZ.run();
        setStartWeekDay(this.mView);
        this.navigationMonthGrid.updateMonth(this.mTime);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        Time time2 = new SafeTime(Utils.getTimeZone(activity, this.mUpdateTZ));
        time2.set(Utils.getDisplayTime());
        if (this.mCurrentDay.year == time2.year && this.mCurrentDay.month == time2.month) {
            int i = this.mCurrentDay.monthDay;
            int i2 = time2.monthDay;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong(LunarContract.EXTRA_EVENT_BEGIN_TIME, this.mTime.toMillis(true));
    }

    public boolean isR2L() {
        return CalendarApplication.isR2L(getResources());
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.snap_month_view, (ViewGroup) null);
        setStartWeekDay(viewInflate);
        initNavigationMonthGrid(viewInflate);
        initMonthSwitcher(viewInflate);
        this.mView = viewInflate;
        return viewInflate;
    }

    public boolean isStarted() {
        return this.mStarted;
    }

    private void setStartWeekDay(View view) {
        int firstDayOfWeek = Utils.getFirstDayOfWeek(getActivity());
        this.mStartDay = firstDayOfWeek;
        if (firstDayOfWeek == 0) {
            this.mStartDay = 1;
        } else if (firstDayOfWeek == 1) {
            this.mStartDay = 2;
        } else if (firstDayOfWeek == 6) {
            this.mStartDay = 7;
        }
        int i = this.mStartDay - 1;
        int i2 = getResources().getConfiguration().locale.toString().equals("zh_HK") ? 30 : 50;
        int[] iArr = {R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6};
        for (int i3 = 0; i3 < 7; i3++) {
            TextView textView = (TextView) view.findViewById(iArr[i3]);
            if (textView != null) {
                textView.setText(DateUtils.getDayOfWeekString(((i3 + i) % 7) + 1, i2));
            }
        }
    }

    private void initNavigationMonthGrid(View view) {
        NavigationMonthGrid navigationMonthGrid = (NavigationMonthGrid) view.findViewById(R.id.navigation_month_grid);
        this.navigationMonthGrid = navigationMonthGrid;
        if (navigationMonthGrid == null) {
            return;
        }
        navigationMonthGrid.setIsR2L(isR2L());
        INavigationMonthGridFragmentParent iNavigationMonthGridFragmentParent = this.navigationParent;
        if (iNavigationMonthGridFragmentParent != null && (iNavigationMonthGridFragmentParent instanceof TabletWeekControllerFragment)) {
            this.navigationMonthGrid.setIsWeekView(true);
        }
        this.navigationMonthGrid.setNavigationMonthGridListener(new NavigationMonthGridListener() { // from class: com.sonymobile.calendar.tablet.NavigationMonthGridFragment.2
            @Override // com.sonymobile.calendar.tablet.NavigationMonthGridListener
            public void onDateSelected(Time time) {
                NavigationMonthGridFragment.this.navigationParent.goTo(time, true);
            }
        });
    }

    private void initMonthSwitcher(View view) {
        MonthSwitcher monthSwitcher = new MonthSwitcher(view);
        this.monthSwitcher = monthSwitcher;
        monthSwitcher.setOnDateSwitcherClickedListener(new DateSwitcherListener());
    }

    private class DateSwitcherListener implements OnDateSwitcherClickedListener {
        private DateSwitcherListener() {
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onPreviousButtonClicked() {
            Time time = new SafeTime(NavigationMonthGridFragment.this.mTime);
            time.month--;
            time.normalize(true);
            NavigationMonthGridFragment.this.navigationParent.goTo(time, false);
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onNextButtonClicked() {
            Time time = new SafeTime(NavigationMonthGridFragment.this.mTime);
            time.month++;
            time.normalize(true);
            NavigationMonthGridFragment.this.navigationParent.goTo(time, false);
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onDateLabelClicked() {
            if (NavigationMonthGridFragment.this.mDatePicker != null) {
                NavigationMonthGridFragment.this.mDatePicker.dismiss();
            }
            NavigationMonthGridFragment navigationMonthGridFragment = NavigationMonthGridFragment.this;
            navigationMonthGridFragment.mDatePicker = navigationMonthGridFragment.navigationMonthGrid.isWeekView() ? new WeekPicker(NavigationMonthGridFragment.this.getActivity(), NavigationMonthGridFragment.this.mTime) : new DayPicker(NavigationMonthGridFragment.this.getActivity(), NavigationMonthGridFragment.this.mTime);
            NavigationMonthGridFragment.this.mDatePicker.setOnDatePickerSetListener(new OnDatePickerSetListener() { // from class: com.sonymobile.calendar.tablet.NavigationMonthGridFragment.DateSwitcherListener.1
                @Override // com.sonymobile.calendar.OnDatePickerSetListener
                public void onDateSet(Time time) {
                    if ((time.year == NavigationMonthGridFragment.this.mTime.year && time.month == NavigationMonthGridFragment.this.mTime.month && time.monthDay == NavigationMonthGridFragment.this.mTime.monthDay) || NavigationMonthGridFragment.this.navigationParent == null) {
                        return;
                    }
                    NavigationMonthGridFragment.this.navigationParent.goTo(time, true);
                }
            });
            NavigationMonthGridFragment.this.mDatePicker.show(NavigationMonthGridFragment.this.getFragmentManager(), "date_picker");
        }
    }

    public static NavigationMonthGridFragment instantiateSelf(FragmentActivity fragmentActivity) {
        return (NavigationMonthGridFragment) instantiate(fragmentActivity, NavigationMonthGridFragment.class.getName());
    }
}
