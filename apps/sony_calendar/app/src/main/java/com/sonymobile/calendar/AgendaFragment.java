package com.sonymobile.calendar;

import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.KeyEventDispatcher;
import androidx.fragment.app.Fragment;
import com.google.android.gms.actions.SearchIntents;
import com.sonyericsson.calendar.util.CalendarColorService;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.tablet.TabletAgendaControllerFragment;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class AgendaFragment extends Fragment implements Navigator {
    protected static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";
    private static boolean DEBUG = true;
    private static final String TAG = "AgendaFragment";
    private AgendaListView mAgendaListView;
    protected String mQuery;
    private TabletAgendaControllerFragment mTabletAgendaControllerFragment;
    private Time mTime;
    private Runnable mUpdateTZ = new Runnable() { // from class: com.sonymobile.calendar.AgendaFragment.1
        @Override // java.lang.Runnable
        public void run() {
            long millis = AgendaFragment.this.mTime.toMillis(true);
            AgendaFragment.this.mTime = new SafeTime(Utils.getTimeZone(AgendaFragment.this.getActivity(), this));
            AgendaFragment.this.mTime.set(millis);
        }
    };
    private final ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.sonymobile.calendar.AgendaFragment.2
        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            AgendaFragment.this.mAgendaListView.onCalendarsChanged();
        }
    };

    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return "";
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
    }

    public AgendaListView getAgendaList() {
        return this.mAgendaListView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        long jCurrentTimeMillis;
        super.onCreate(bundle);
        this.mTime = new SafeTime();
        if (bundle != null) {
            jCurrentTimeMillis = bundle.getLong(BUNDLE_KEY_RESTORE_TIME);
            if (DEBUG) {
                Log.v(TAG, "Restore value from icicle: " + jCurrentTimeMillis);
            }
        } else {
            jCurrentTimeMillis = 0;
        }
        if (jCurrentTimeMillis == 0) {
            jCurrentTimeMillis = Utils.getDisplayTime();
            if (DEBUG) {
                Time time = new SafeTime();
                time.set(jCurrentTimeMillis);
                Log.v(TAG, "Restore value from intent: " + time.toString());
            }
        }
        if (jCurrentTimeMillis == 0) {
            if (DEBUG) {
                Log.v(TAG, "Restored from current time");
            }
            jCurrentTimeMillis = System.currentTimeMillis();
        }
        this.mTime.set(jCurrentTimeMillis);
        this.mQuery = getActivity().getIntent().getStringExtra(SearchIntents.EXTRA_QUERY);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.agenda_activity, (ViewGroup) null);
        this.mAgendaListView = (AgendaListView) viewInflate.findViewById(R.id.agenda_listview);
        if (this.mQuery != null) {
            ((FABFrameLayout) viewInflate.findViewById(R.id.agenda_fab_button)).setVisibility(8);
            this.mAgendaListView.setIsInSearch();
            if (bundle != null) {
                this.mAgendaListView.setIsInMonthView();
            }
        }
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mAgendaListView.initEventLoader(this);
        refreshAgendaFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshAgendaFragment();
    }

    private void refreshAgendaFragment() {
        this.mAgendaListView.onResume();
        CalendarColorService.getInstance().requestLoad(getActivity(), new ColorServiceResultHandler(), 0, true);
        FreeDayService.getInstance().requestLoad(getActivity(), new FreeDayServiceHandler(), 0, true);
        updateAgendaContent();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getActivity().getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, this.mObserver);
        if (LunarAvailabilityManager.isLunarAvailable(getActivity())) {
            getActivity().getContentResolver().registerContentObserver(LunarContract.Events.CONTENT_URI, true, this.mObserver);
        }
        this.mAgendaListView.setAgendaFragment(this);
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), null));
        time.set(Utils.getDisplayTime());
        updateActionBar(time);
        if (this.mAgendaListView.isEventDeleted()) {
            this.mAgendaListView.callOnCalendarChange();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mAgendaListView.onPause();
        getActivity().getContentResolver().unregisterContentObserver(this.mObserver);
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
        Utils.setAgendaSelectedEventInstanceId(0L);
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), this.mUpdateTZ));
        time.set(System.currentTimeMillis());
        Utils.setDisplayTime(Long.valueOf(time.toMillis(true)));
        goTo(time, false);
    }

    public void setTabletAgendaControllerFragment(TabletAgendaControllerFragment tabletAgendaControllerFragment) {
        this.mTabletAgendaControllerFragment = tabletAgendaControllerFragment;
    }

    protected void updateAgendaContent() {
        this.mTime.set(Utils.getDisplayTime());
        this.mUpdateTZ.run();
        if (DEBUG) {
            Log.v(TAG, "updateAgendaContent to " + this.mTime.toString());
        }
        this.mAgendaListView.setHideDeclinedEvents(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean(GeneralPreferences.KEY_HIDE_DECLINED, true));
        this.mAgendaListView.goTo(this.mTime, this.mQuery, true);
    }

    public void updateSelectedEventId(long j, long j2, long j3, long j4, boolean z) {
        TabletAgendaControllerFragment tabletAgendaControllerFragment = this.mTabletAgendaControllerFragment;
        if (tabletAgendaControllerFragment != null) {
            tabletAgendaControllerFragment.updateSelectedEventId(j, j2, j3, j4, z);
        }
    }

    private class ColorServiceResultHandler implements IAsyncServiceResultHandler {
        private ColorServiceResultHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() == 0) {
                AgendaFragment.this.mAgendaListView.invalidate();
            }
        }
    }

    private class FreeDayServiceHandler implements IAsyncServiceResultHandler {
        private FreeDayServiceHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() == 0) {
                AgendaFragment.this.mAgendaListView.invalidate();
            }
        }
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return System.currentTimeMillis();
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
        if (time == null) {
            time = new SafeTime();
            time.setToNow();
            time.normalize(false);
        }
        KeyEventDispatcher.Component activity = getActivity();
        if (activity instanceof LaunchActivity) {
            ((NavigationListener) activity).onViewNavigated(time, getString(R.string.agenda_view), ViewType.AGENDA);
        }
    }
}
