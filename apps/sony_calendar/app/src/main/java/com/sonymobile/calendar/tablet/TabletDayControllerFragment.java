package com.sonymobile.calendar.tablet;

import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.sonymobile.calendar.ActionBarControllerBase;
import com.sonymobile.calendar.DayFragment;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class TabletDayControllerFragment extends ControllerFragment implements INavigationMonthGridFragmentParent, INavigationMonthGridController {
    private DayFragment mDayFragment;
    private Toolbar mToolbar;
    private NavigationMonthGridFragment navigationMonthFragment;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (initializeChildFragments() || bundle == null) {
            FragmentTransaction fragmentTransactionBeginTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransactionBeginTransaction.replace(R.id.DayGridFragment, this.mDayFragment, DayFragment.TAG);
            fragmentTransactionBeginTransaction.replace(R.id.MonthNavigatorOnDay, this.navigationMonthFragment, NavigationMonthGridFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        ActionBarControllerBase actionBarController = ((LaunchActivity) getActivity()).getActionBarController();
        actionBarController.setToolbar(this.mToolbar);
        actionBarController.onFragmentAttached(getClass().getName());
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.tablet_layout_dayview, (ViewGroup) null);
        Toolbar toolbar = (Toolbar) viewInflate.findViewById(R.id.toolbar);
        this.mToolbar = toolbar;
        if (toolbar != null) {
            toolbar.setBackgroundResource(R.color.toolbar_background_color);
        }
        return viewInflate;
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        this.navigationMonthFragment.goTo(time, false);
        this.mDayFragment.goTo(time, z);
    }

    @Override // com.sonymobile.calendar.tablet.INavigationMonthGridController
    public void updateNavigationMonthGrid(Time time, boolean z) {
        this.navigationMonthFragment.goTo(time, z);
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public void goToToday() {
        DayFragment dayFragment = this.mDayFragment;
        if (dayFragment != null) {
            dayFragment.goToToday();
        }
        NavigationMonthGridFragment navigationMonthGridFragment = this.navigationMonthFragment;
        if (navigationMonthGridFragment != null) {
            navigationMonthGridFragment.goToToday();
        }
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        DayFragment dayFragment = this.mDayFragment;
        if (dayFragment != null) {
            return dayFragment.getSelectedTimeInMillis();
        }
        return 0L;
    }

    private boolean initializeChildFragments() {
        boolean z;
        FragmentManager childFragmentManager = getChildFragmentManager();
        DayFragment dayFragment = (DayFragment) childFragmentManager.findFragmentByTag(DayFragment.TAG);
        this.mDayFragment = dayFragment;
        boolean z2 = true;
        if (dayFragment == null) {
            this.mDayFragment = DayFragment.instantiateSelf(getActivity());
            z = true;
        } else {
            z = false;
        }
        NavigationMonthGridFragment navigationMonthGridFragment = (NavigationMonthGridFragment) childFragmentManager.findFragmentByTag(NavigationMonthGridFragment.TAG);
        this.navigationMonthFragment = navigationMonthGridFragment;
        if (navigationMonthGridFragment == null) {
            this.navigationMonthFragment = NavigationMonthGridFragment.instantiateSelf(getActivity());
        } else {
            z2 = z;
        }
        this.mDayFragment.setNavigationMonthGridController(this);
        this.navigationMonthFragment.setNavigationMonthGridFragmentParent(this);
        return z2;
    }
}
