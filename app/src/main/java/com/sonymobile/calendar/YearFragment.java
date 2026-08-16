package com.sonymobile.calendar;

import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ViewSwitcher;
import androidx.fragment.app.Fragment;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.tablet.HeaderBase;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class YearFragment extends Fragment implements ViewSwitcher.ViewFactory, Navigator, Animation.AnimationListener {
    private static final int ANIMATION_DURATION = 400;
    private Time displayedDate;
    private Animation inAnimationLeft;
    private Animation inAnimationRight;
    private ActionBarControllerBase mActionBarController;
    private YearPicker mYearPicker;
    private Animation outAnimationLeft;
    private Animation outAnimationRight;
    private ViewSwitcher viewSwitcher;
    private boolean wentToNext;
    private HeaderBase yearHeader;
    private String dateString = "";
    private Runnable updateTZ = new Runnable() { // from class: com.sonymobile.calendar.YearFragment.1
        @Override // java.lang.Runnable
        public void run() {
            YearFragment.this.displayedDate.timezone = Utils.getTimeZone(YearFragment.this.getActivity(), this);
            YearFragment.this.displayedDate.normalize(true);
        }
    };

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationRepeat(Animation animation) {
    }

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationStart(Animation animation) {
        getNextView().setCanHandleTouch(false);
    }

    @Override // android.view.animation.Animation.AnimationListener
    public void onAnimationEnd(Animation animation) {
        Time time = new SafeTime(this.displayedDate);
        if (this.wentToNext) {
            time.year++;
            getCurrentView().updateYearGrid(time, 2);
        } else {
            time.year--;
            getCurrentView().updateYearGrid(time, 0);
        }
        getCurrentView().setCanHandleTouch(true);
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return Utils.getNextRoundedTimeMillis(this.displayedDate, 30);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        Time time2 = new SafeTime(Utils.getTimeZone(getActivity(), null));
        time2.setToNow();
        if (time.year == time2.year) {
            time = time2;
        }
        if (z) {
            boolean z2 = time.year < this.displayedDate.year;
            boolean zIsR2L = getCurrentView().isR2L();
            if ((z2 && !zIsR2L) || (!z2 && zIsR2L)) {
                this.viewSwitcher.setInAnimation(this.inAnimationLeft);
                this.viewSwitcher.setOutAnimation(this.outAnimationLeft);
            } else {
                this.viewSwitcher.setInAnimation(this.inAnimationRight);
                this.viewSwitcher.setOutAnimation(this.outAnimationRight);
            }
        } else {
            this.viewSwitcher.setInAnimation(null);
            this.viewSwitcher.setOutAnimation(null);
        }
        this.displayedDate = time;
        updateDateSwitcher(getActivity(), this.displayedDate);
        this.viewSwitcher.showNext();
        Utils.setDisplayTime(Long.valueOf(this.displayedDate.toMillis(true)));
        getCurrentView().sendAccessibilityEvent(32);
        getCurrentView().updateYearGrids(this.displayedDate);
        updateActionBar(this.displayedDate);
        getCurrentView().setCanHandleTouch(true);
        if (Utils.isKeyboardAttached(getActivity())) {
            getCurrentView().requestFocus();
            getCurrentView().requestFocusFromTouch();
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.updateTZ));
        time.set(System.currentTimeMillis());
        time.normalize(true);
        this.displayedDate = new SafeTime(time);
        Utils.setDisplayTime(Long.valueOf(time.toMillis(true)));
        goTo(time, false);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
        if (this.displayedDate.year < Utils.EPOCH_YEAR_UPPER_LIMIT) {
            goToNeighbour(true, f);
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
        goToNeighbour(false, f);
    }

    public void goToMonth(int i) {
        Time time = new SafeTime(this.displayedDate.timezone);
        time.set(this.displayedDate.second, this.displayedDate.minute, this.displayedDate.hour, 1, i, this.displayedDate.year);
        ((LaunchActivity) getActivity()).switchItem(1, time.toMillis(true), false);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
        ((NavigationListener) getActivity()).onViewNavigated(time, Utils.getYearText(getActivity(), time), ViewType.YEAR);
    }

    @Override // android.widget.ViewSwitcher.ViewFactory
    public View makeView() {
        YearView yearView = new YearView(this, this);
        yearView.updateYearGrids(this.displayedDate);
        return yearView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ActionBarControllerBase actionBarController = ((LaunchActivity) getActivity()).getActionBarController();
        this.mActionBarController = actionBarController;
        actionBarController.onFragmentAttached(getClass().getName());
        this.mActionBarController.setToolbar(null);
        initDateSwitcher();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initDates(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.year_activity, (ViewGroup) null);
        ViewSwitcher viewSwitcher = (ViewSwitcher) viewInflate.findViewById(R.id.switcher);
        this.viewSwitcher = viewSwitcher;
        viewSwitcher.setFactory(this);
        initAnimations();
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        FreeDayService.getInstance().loadWeekendDays(getActivity());
        FreeDayService.getInstance().requestLoad(getActivity(), new FreeDayServiceHandler(), 0, true);
        resumeDisplayedDate();
        resumeSelectedDate();
        this.updateTZ.run();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        YearPicker yearPicker = this.mYearPicker;
        if (yearPicker != null) {
            yearPicker.dismiss();
            this.mYearPicker = null;
        }
        getCurrentView().recycle();
        getNextView().recycle();
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong(LunarContract.EXTRA_EVENT_BEGIN_TIME, this.displayedDate.toMillis(true));
        Utils.setDisplayTime(Long.valueOf(this.displayedDate.toMillis(true)));
    }

    public Time getDisplayedDate() {
        return this.displayedDate;
    }

    public Time getSelectedDate() {
        return this.displayedDate;
    }

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return this.dateString;
    }

    private void initDates(Bundle bundle) {
        long jCurrentTimeMillis = bundle != null ? bundle.getLong(LunarContract.EXTRA_EVENT_BEGIN_TIME) : 0L;
        if (jCurrentTimeMillis == 0) {
            jCurrentTimeMillis = Utils.getDisplayTime();
        }
        if (jCurrentTimeMillis == 0) {
            jCurrentTimeMillis = System.currentTimeMillis();
        }
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.updateTZ));
        this.displayedDate = time;
        time.set(jCurrentTimeMillis);
        this.displayedDate.normalize(true);
        long displayTime = Utils.getDisplayTime();
        if (displayTime != 0) {
            this.displayedDate.set(displayTime);
        }
    }

    private void initDateSwitcher() {
        if (Utils.isTabletDevice(getActivity())) {
            DateSwitcherListener dateSwitcherListener = new DateSwitcherListener();
            HeaderBase headerBase = (HeaderBase) this.mActionBarController.getTitle();
            this.yearHeader = headerBase;
            headerBase.setOnDateSwitcherClickedListener(dateSwitcherListener);
        }
    }

    private void updateDateSwitcher(Context context, Time time) {
        if (getResources().getBoolean(R.bool.tablet_mode)) {
            this.yearHeader.update(getActivity(), time);
        }
    }

    private void initAnimations() {
        this.inAnimationLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_in);
        this.outAnimationLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_out);
        this.inAnimationRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_in);
        this.outAnimationRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_out);
    }

    private void resumeDisplayedDate() {
        long displayTime = Utils.getDisplayTime();
        if (displayTime > 0) {
            Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.updateTZ));
            time.set(displayTime);
            goTo(time, false);
        }
    }

    private void resumeSelectedDate() {
        long displayTime = Utils.getDisplayTime();
        if (displayTime > 0) {
            Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.updateTZ));
            this.displayedDate = time;
            time.set(displayTime);
        }
    }

    private void goToNeighbour(boolean z, float f) {
        this.wentToNext = z;
        buildSwipeAnimation(z, f);
        this.displayedDate.year += z ? 1 : -1;
        updateDateSwitcher(getActivity(), this.displayedDate);
        recycleYearGrids(z);
        this.viewSwitcher.showNext();
        Utils.setDisplayTime(Long.valueOf(this.displayedDate.toMillis(true)));
        getCurrentView().sendAccessibilityEvents();
        updateActionBar(this.displayedDate);
        if (Utils.isKeyboardAttached(getActivity())) {
            getCurrentView().requestFocus();
            getCurrentView().requestFocusFromTouch();
        }
    }

    private void buildSwipeAnimation(boolean z, float f) {
        boolean zIsR2L = getCurrentView().isR2L();
        float f2 = 1.0f;
        float fMin = Math.min(Math.abs(f) / getCurrentView().getWidth(), 1.0f);
        float f3 = 1.0f - fMin;
        long j = (long) (400.0f * f3);
        if ((!z || zIsR2L) && (z || !zIsR2L)) {
            f3 = fMin - 1.0f;
        } else {
            fMin = -fMin;
            f2 = -1.0f;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(1, f3, 1, 0.0f, 0, 0.0f, 0, 0.0f);
        TranslateAnimation translateAnimation2 = new TranslateAnimation(1, fMin, 1, f2, 0, 0.0f, 0, 0.0f);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        translateAnimation2.setInterpolator(new DecelerateInterpolator());
        translateAnimation.setDuration(j);
        translateAnimation2.setDuration(j);
        translateAnimation.setAnimationListener(this);
        this.viewSwitcher.setInAnimation(translateAnimation);
        this.viewSwitcher.setOutAnimation(translateAnimation2);
    }

    private void recycleYearGrids(boolean z) {
        YearView currentView = getCurrentView();
        YearView nextView = getNextView();
        nextView.setViewWidth(currentView.getViewWidth());
        if (z) {
            nextView.setYearGrid(currentView.getYearGrid(1), 0);
            nextView.setYearGrid(currentView.getYearGrid(2), 1);
        } else {
            nextView.setYearGrid(currentView.getYearGrid(1), 2);
            nextView.setYearGrid(currentView.getYearGrid(0), 1);
        }
    }

    private YearView getNextView() {
        return (YearView) this.viewSwitcher.getNextView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public YearView getCurrentView() {
        return (YearView) this.viewSwitcher.getCurrentView();
    }

    private class DateSwitcherListener implements OnDateSwitcherClickedListener {
        private DateSwitcherListener() {
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onPreviousButtonClicked() {
            YearFragment.this.goToPrevious(0.0f);
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onNextButtonClicked() {
            YearFragment.this.goToNext(0.0f);
        }

        @Override // com.sonymobile.calendar.OnDateSwitcherClickedListener
        public void onDateLabelClicked() {
            if (YearFragment.this.mYearPicker != null) {
                YearFragment.this.mYearPicker.dismiss();
            }
            YearFragment.this.mYearPicker = new YearPicker(YearFragment.this.getActivity(), YearFragment.this.displayedDate);
            YearFragment.this.mYearPicker.setOnDatePickerSetListener(new OnDatePickerSetListener() { // from class: com.sonymobile.calendar.YearFragment.DateSwitcherListener.1
                @Override // com.sonymobile.calendar.OnDatePickerSetListener
                public void onDateSet(Time time) {
                    if (time.year != YearFragment.this.displayedDate.year) {
                        YearFragment.this.goTo(time, true);
                    }
                }
            });
            YearFragment.this.mYearPicker.show(YearFragment.this.getFragmentManager(), "year_picker");
        }
    }

    private class FreeDayServiceHandler implements IAsyncServiceResultHandler {
        private FreeDayServiceHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (obj != null && ((Integer) obj2).intValue() == 0 && ((Boolean) obj).booleanValue()) {
                YearFragment.this.getCurrentView().updateYearGrids(YearFragment.this.displayedDate);
            }
        }
    }
}
