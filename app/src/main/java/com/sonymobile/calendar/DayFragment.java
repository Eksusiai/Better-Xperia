package com.sonymobile.calendar;

import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class DayFragment extends CalendarFragment {
    public static final String TAG = "DayFragment";
    private View fabButton;
    private View mRootView;
    private TextView weekNumberView;

    @Override // com.sonymobile.calendar.ICalendarFragment
    public void drawingCompleted() {
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSelectedDay = this.calendarGridView.getSelectedTime();
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Utils.processBundledBeginEventTime(bundle);
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        refreshDayFragment();
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshDayFragment();
        this.mRootView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.sonymobile.calendar.DayFragment.1
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (DayFragment.this.fabButton != null) {
                    DayFragment.this.fabButton.requestFocus();
                    DayFragment.this.fabButton.requestFocusFromTouch();
                }
            }
        });
    }

    private void refreshDayFragment() {
        UiUtils.showWeekNumberInWeekDayView(getActivity(), this.weekNumberView);
        if (Utils.isTabletDevice(getActivity()) && Utils.isInLandscapeMode(getActivity())) {
            this.mRootView.findViewById(R.id.addEventButton).setVisibility(8);
        }
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong(LunarContract.EXTRA_EVENT_BEGIN_TIME, Utils.getDisplayTime());
    }

    @Override // androidx.fragment.app.Fragment
    public View getView() {
        if (this.mRootView == null) {
            View viewInflate = getActivity().getLayoutInflater().inflate(R.layout.day_activity, (ViewGroup) null);
            this.mRootView = viewInflate;
            this.weekNumberView = (TextView) viewInflate.findViewById(R.id.week_navigator_week_number);
            this.fabButton = this.mRootView.findViewById(R.id.fab);
            if (Utils.isTabletDevice(getActivity())) {
                this.mRootView.findViewById(R.id.week_line).setVisibility(8);
            }
        }
        return this.mRootView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mRootView = null;
        this.weekNumberView = null;
        this.fabButton = null;
    }

    @Override // com.sonymobile.calendar.CalendarFragment, com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return Utils.getNextRoundedTimeMillis(getSelectedTime(), 30);
    }

    @Override // com.sonymobile.calendar.CalendarFragment, com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        super.goTo(time, z);
        this.navigatorView.scrollToSelectedPosition(true);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
        ((NavigationListener) getActivity()).onViewNavigated(time, null, ViewType.DAY);
        ActionBarControllerBase actionBarController = ((LaunchActivity) getActivity()).getActionBarController();
        this.dateString = ((Object) actionBarController.getTitle().getText()) + " " + ((Object) actionBarController.getSubtitleText());
    }

    public static DayFragment instantiateSelf(FragmentActivity fragmentActivity) {
        return (DayFragment) instantiate(fragmentActivity, DayFragment.class.getName());
    }
}
