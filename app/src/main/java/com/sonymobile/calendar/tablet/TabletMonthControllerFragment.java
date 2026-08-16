package com.sonymobile.calendar.tablet;

import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import com.sonymobile.calendar.ActionBarControllerBase;
import com.sonymobile.calendar.ActionBarControllerTablet;
import com.sonymobile.calendar.IMonthFragmentParent;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.MonthFragment;
import com.sonymobile.calendar.Navigator;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.agendapager.SplitScreenAgendaViewPager;
import com.sonymobile.calendar.widget.WhiteHeader;

/* JADX INFO: loaded from: classes2.dex */
public class TabletMonthControllerFragment extends ControllerFragment implements Navigator, IMonthFragmentParent, SplitScreenAgendaViewPager.AgendaSelectedPageCallback, SplitScreenAgendaViewPager.AgendaSwipePageCallback {
    private ActionBarControllerBase mActionBarController;
    private MonthFragment mMonthFragment;
    private SplitScreenAgendaViewPager mSplitScreenAgendaViewPager;
    private Toolbar mToolbar;
    private WhiteHeader mWhiteHeader;

    @Override // com.sonymobile.calendar.agendapager.SplitScreenAgendaViewPager.AgendaSwipePageCallback
    public void agendaListIsInSwipe(boolean z) {
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return "";
    }

    @Override // com.sonymobile.calendar.IMonthFragmentParent
    public void hideAgendaList() {
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MonthFragment monthFragment = (MonthFragment) instantiate(getActivity(), MonthFragment.class.getName());
        this.mMonthFragment = monthFragment;
        monthFragment.setParent(this);
        SplitScreenAgendaViewPager splitScreenAgendaViewPagerNewInstance = SplitScreenAgendaViewPager.newInstance();
        this.mSplitScreenAgendaViewPager = splitScreenAgendaViewPagerNewInstance;
        splitScreenAgendaViewPagerNewInstance.setParent(this);
        FragmentTransaction fragmentTransactionBeginTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.tablet_agenda_fragment, this.mSplitScreenAgendaViewPager);
        fragmentTransactionBeginTransaction.commit();
        FragmentTransaction fragmentTransactionBeginTransaction2 = getChildFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction2.replace(R.id.tablet_month_fragment, this.mMonthFragment);
        fragmentTransactionBeginTransaction2.commit();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ActionBarControllerBase actionBarController = ((LaunchActivity) getActivity()).getActionBarController();
        this.mActionBarController = actionBarController;
        if (actionBarController instanceof ActionBarControllerTablet) {
            ((ActionBarControllerTablet) actionBarController).setWhiteHeader(this.mWhiteHeader);
        }
        this.mActionBarController.setToolbar(this.mToolbar);
        this.mActionBarController.onFragmentAttached(getClass().getName());
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.tablet_layout_monthview, (ViewGroup) null);
        this.mWhiteHeader = (WhiteHeader) viewInflate.findViewById(R.id.white_header);
        Toolbar toolbar = (Toolbar) viewInflate.findViewById(R.id.toolbar);
        this.mToolbar = toolbar;
        if (toolbar != null) {
            toolbar.setBackgroundResource(R.color.toolbar_background_color);
        }
        return viewInflate;
    }

    @Override // com.sonymobile.calendar.IMonthFragmentParent
    public void updateAgendaList(Time time) {
        SplitScreenAgendaViewPager splitScreenAgendaViewPager;
        FragmentActivity activity = getActivity();
        if (activity == null || activity.isFinishing() || (splitScreenAgendaViewPager = this.mSplitScreenAgendaViewPager) == null) {
            return;
        }
        splitScreenAgendaViewPager.goTo(time);
    }

    @Override // com.sonymobile.calendar.agendapager.SplitScreenAgendaViewPager.AgendaSelectedPageCallback
    public void goToMonthDay(Time time) {
        FragmentActivity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        this.mMonthFragment.onCalendarClicked(time, true);
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        MonthFragment monthFragment;
        FragmentActivity activity = getActivity();
        if (activity == null || activity.isFinishing() || (monthFragment = this.mMonthFragment) == null) {
            return 0L;
        }
        return monthFragment.getSelectedTimeInMillis();
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        MonthFragment monthFragment = this.mMonthFragment;
        if (monthFragment != null) {
            monthFragment.goTo(time, z);
        }
        SplitScreenAgendaViewPager splitScreenAgendaViewPager = this.mSplitScreenAgendaViewPager;
        if (splitScreenAgendaViewPager != null) {
            splitScreenAgendaViewPager.getFragment().goTo(time, z);
        }
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public void goToToday() {
        MonthFragment monthFragment;
        FragmentActivity activity = getActivity();
        if (activity == null || activity.isFinishing() || (monthFragment = this.mMonthFragment) == null) {
            return;
        }
        monthFragment.goToToday();
    }

    public void updateAgendaPager() {
        FragmentActivity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        this.mSplitScreenAgendaViewPager.updatePager();
    }
}
