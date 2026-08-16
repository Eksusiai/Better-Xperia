package com.sonymobile.calendar.tablet;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonymobile.calendar.ActionBarControllerBase;
import com.sonymobile.calendar.AgendaFragment;
import com.sonymobile.calendar.EventInfoFragment;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class TabletAgendaControllerFragment extends ControllerFragment {
    private AgendaFragment mAgendaFragment;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT < 24 || !getActivity().isInMultiWindowMode()) {
            AgendaFragment agendaFragment = (AgendaFragment) Fragment.instantiate(getActivity(), AgendaFragment.class.getName());
            this.mAgendaFragment = agendaFragment;
            agendaFragment.setTabletAgendaControllerFragment(this);
            FragmentTransaction fragmentTransactionBeginTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransactionBeginTransaction.replace(R.id.agendaGridFragment, this.mAgendaFragment);
            fragmentTransactionBeginTransaction.commit();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.tablet_layout_agenda, viewGroup, false);
    }

    public void updateSelectedEventId(long j, long j2, long j3, long j4, boolean z) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        if (childFragmentManager == null) {
            return;
        }
        EventInfoFragment eventInfoFragment = (EventInfoFragment) childFragmentManager.findFragmentByTag(EventInfoFragment.TAG);
        FragmentTransaction fragmentTransactionBeginTransaction = childFragmentManager.beginTransaction();
        if (j == 0) {
            if (eventInfoFragment != null) {
                fragmentTransactionBeginTransaction.remove(eventInfoFragment).commit();
                return;
            }
            return;
        }
        Uri uriWithAppendedId = ContentUris.withAppendedId(z ? LunarContract.Events.CONTENT_URI : CalendarContract.Events.CONTENT_URI, j);
        if (eventInfoFragment == null) {
            fragmentTransactionBeginTransaction.add(R.id.agenda_eventinfo, EventInfoFragment.newInstance(uriWithAppendedId, j2, j3, j4, false, z), EventInfoFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        } else {
            if (eventInfoFragment.mIsStopped) {
                return;
            }
            fragmentTransactionBeginTransaction.replace(R.id.agenda_eventinfo, EventInfoFragment.newInstance(uriWithAppendedId, j2, j3, j4, false, z), EventInfoFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        FreeDayService.getInstance().loadWeekendDays(getActivity());
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        FreeDayService.getInstance().loadWeekendDays(getActivity());
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        FragmentActivity activity = getActivity();
        if (activity instanceof LaunchActivity) {
            ActionBarControllerBase actionBarController = ((LaunchActivity) activity).getActionBarController();
            actionBarController.setToolbar(null);
            actionBarController.onFragmentAttached(getClass().getName());
        }
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public void goToToday() {
        AgendaFragment agendaFragment = this.mAgendaFragment;
        if (agendaFragment != null) {
            agendaFragment.goToToday();
        }
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        AgendaFragment agendaFragment = this.mAgendaFragment;
        if (agendaFragment != null) {
            return agendaFragment.getSelectedTimeInMillis();
        }
        return 0L;
    }

    @Override // com.sonymobile.calendar.tablet.ControllerFragment, com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        AgendaFragment agendaFragment = this.mAgendaFragment;
        if (agendaFragment != null) {
            agendaFragment.goTo(time, true);
        }
    }
}
