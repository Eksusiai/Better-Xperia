package com.sonymobile.calendar;

import android.content.Context;
import android.content.SharedPreferences;
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
public class WeekFragment extends CalendarFragment {
    private static final String KEY_ROTATION = "previous_rotation";
    public static final String TAG = "WeekFragment";
    private boolean mIsPreferenceChanged;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.sonymobile.calendar.WeekFragment.1
        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            WeekFragment.this.mIsPreferenceChanged = true;
        }
    };
    View mRootView;
    private int mSavedRotation;
    private TextView weekNumberView;

    @Override // com.sonymobile.calendar.ICalendarFragment
    public void drawingCompleted() {
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        this.isWeekFragment = true;
        super.onCreate(bundle);
        GeneralPreferences.getSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this.mPreferenceChangeListener);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        GeneralPreferences.getSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this.mPreferenceChangeListener);
        super.onDestroy();
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Utils.processBundledBeginEventTime(bundle);
        if (bundle != null) {
            this.mSavedRotation = bundle.getInt(KEY_ROTATION);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        refreshWeekFragment();
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshWeekFragment();
    }

    private void refreshWeekFragment() {
        if (this.mSavedRotation == 0 || screenHasRotated(getActivity()) || this.mIsPreferenceChanged) {
            this.calendarGridView.invalidateColumns();
            this.mIsPreferenceChanged = false;
        }
        this.mSavedRotation = getActivity().getResources().getConfiguration().orientation;
        this.allDayView.invalidateColumns();
        if (this.navigatorView != null) {
            this.navigatorView.invalidateColumns();
        }
        if (Utils.isTabletDevice(getActivity()) && Utils.isInLandscapeMode(getActivity())) {
            this.mRootView.findViewById(R.id.addEventButton).setVisibility(8);
        }
        super.onResume();
        UiUtils.showWeekNumberInWeekDayView(getActivity(), this.weekNumberView);
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong(LunarContract.EXTRA_EVENT_BEGIN_TIME, Utils.getDisplayTime());
        bundle.putInt(KEY_ROTATION, this.mSavedRotation);
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSelectedDay = this.calendarGridView.getSelectedTime();
    }

    @Override // androidx.fragment.app.Fragment
    public View getView() {
        if (this.mRootView == null) {
            View viewInflate = getActivity().getLayoutInflater().inflate(R.layout.week_activity, (ViewGroup) null);
            this.mRootView = viewInflate;
            this.weekNumberView = (TextView) viewInflate.findViewById(R.id.week_navigator_week_number);
        }
        return this.mRootView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mRootView = null;
        this.weekNumberView = null;
    }

    @Override // com.sonymobile.calendar.CalendarFragment, com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return Utils.getNextRoundedTimeMillis(getSelectedTime(), 30);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
        ((NavigationListener) getActivity()).onViewNavigated(time, null, ViewType.WEEK);
        ActionBarControllerBase actionBarController = ((LaunchActivity) getActivity()).getActionBarController();
        this.dateString = ((Object) actionBarController.getTitle().getText()) + " " + ((Object) actionBarController.getSubtitleText());
    }

    private boolean screenHasRotated(Context context) {
        return context.getResources().getConfiguration().orientation != this.mSavedRotation;
    }

    public static WeekFragment instantiateSelf(FragmentActivity fragmentActivity) {
        return (WeekFragment) instantiate(fragmentActivity, WeekFragment.class.getName());
    }
}
