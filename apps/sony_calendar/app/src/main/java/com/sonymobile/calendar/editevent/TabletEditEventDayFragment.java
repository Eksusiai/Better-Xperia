package com.sonymobile.calendar.editevent;
import com.sonymobile.calendar.SafeTime;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.CalendarFragment;
import com.sonymobile.calendar.DaySwitcher;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.tablet.OnEditEventTimeChangedListener;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class TabletEditEventDayFragment extends CalendarFragment {
    private static final int EVENT_INDEX_ALL_DAY = 2;
    private static final int EVENT_INDEX_CALENDAR_ID = 3;
    private static final int EVENT_INDEX_TITLE = 1;
    private static final String[] EVENT_PROJECTION = {"_id", LunarContract.EventsColumns.TITLE, "allDay", LunarContract.EventsColumns.CALENDAR_ID};
    private DaySwitcher daySwitcher;
    private boolean isBackFromSleep;
    private Uri mUri;
    private OnEditEventTimeChangedListener onEditEventTimeChangedListener;
    private EventInfo mTempEvent = new EventInfo();
    private boolean canChangeDate = true;
    private View mRootView;

    @Override // com.sonymobile.calendar.ICalendarFragment
    public void drawingCompleted() {
    }

    @Override // com.sonymobile.calendar.CalendarFragment, com.sonymobile.calendar.CalendarSwipeListener
    public void onSwipeCentered() {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSelectedDay = this.calendarGridView.getSelectedTime();
        this.isBackFromSleep = true;
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EditEventActivity editEventActivity = (EditEventActivity) getActivity();
        Intent intent = editEventActivity.getIntent();
        if (!"android.intent.action.INSERT".equals(intent.getAction())) {
            this.mUri = intent.getData();
        }
        long longExtra = intent.getLongExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, 0L);
        long longExtra2 = intent.getLongExtra(LunarContract.EXTRA_EVENT_END_TIME, 3600000 + longExtra);
        Uri uri = this.mUri;
        if (uri != null) {
            long id = ContentUris.parseId(uri);
            Cursor cursorQuery = editEventActivity.getContentResolver().query(this.mUri, EVENT_PROJECTION, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.getCount() != 0) {
                        cursorQuery.moveToFirst();
                        this.mTempEvent.id = id;
                        this.mTempEvent.title = cursorQuery.getString(1);
                        this.mTempEvent.allDay = cursorQuery.getInt(2);
                        this.mTempEvent.calendarId = (int) cursorQuery.getLong(3);
                        if (this.mTempEvent.title == null || this.mTempEvent.title.length() == 0) {
                            this.mTempEvent.title = getResources().getString(R.string.no_title_label);
                        }
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    }
                } catch (Throwable th) {
                    if (cursorQuery != null) {
                        try {
                            cursorQuery.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            Utils.closeCursor(cursorQuery);
            editEventActivity.finish();
            if (cursorQuery != null) {
                cursorQuery.close();
                return;
            }
            return;
        }
        this.mTempEvent.id = Long.MAX_VALUE;
        this.mTempEvent.title = getResources().getString(R.string.event_new_title);
        this.mTempEvent.allDay = 0;
        this.mTempEvent.localBegin = longExtra;
        this.mTempEvent.end = longExtra2;
    }

    @Override // com.sonymobile.calendar.CalendarFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewOnCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        ((TabletEditEventDayView) this.calendarGridView).init(this, this.mTempEvent);
        ((TabletEditEventAllDayView) this.allDayView).init(this, this.mTempEvent);
        return viewOnCreateView;
    }

    @Override // com.sonymobile.calendar.CalendarFragment, com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
        if (this.isBackFromSleep) {
            this.isBackFromSleep = false;
            return;
        }
        super.goTo(time, z);
        ((TabletEditEventDayView) this.calendarGridView).setIsLockedToDate(!this.canChangeDate);
        ((TabletEditEventAllDayView) this.allDayView).setIsLockedToDate(!this.canChangeDate);
        updateDateSwitcher(time);
        updateTempEventViews();
    }

    @Override // com.sonymobile.calendar.CalendarFragment, com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
        super.goToNext(f);
        updateDateSwitcher(this.mSelectedDay);
    }

    @Override // com.sonymobile.calendar.CalendarFragment, com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
        super.goToPrevious(f);
        updateDateSwitcher(this.mSelectedDay);
    }

    public void setOnEditEventTimeChangedListener(OnEditEventTimeChangedListener onEditEventTimeChangedListener) {
        this.onEditEventTimeChangedListener = onEditEventTimeChangedListener;
    }

    public void updateSelectecTime(long j, long j2, long j3, boolean z, boolean z2) {
        this.canChangeDate = z2;
        Time time = new SafeTime(Utils.getTimeZone(getActivity(), null));
        time.set(j);
        this.mTempEvent.allDay = z ? 1 : 0;
        this.mTempEvent.localBegin = j;
        this.mTempEvent.end = j2;
        if (j3 > 0) {
            this.mTempEvent.calendarId = (int) j3;
        }
        goTo(time, false);
    }

    public void updateEditEventView(EventInfo eventInfo) {
        this.mTempEvent = eventInfo;
        updateTempEventViews();
        OnEditEventTimeChangedListener onEditEventTimeChangedListener = this.onEditEventTimeChangedListener;
        if (onEditEventTimeChangedListener != null) {
            onEditEventTimeChangedListener.onTimeChanged(this.mTempEvent.localBegin, this.mTempEvent.end, this.mTempEvent.allDay == 1, this.mTempEvent.calendarId, this.canChangeDate);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View getView() {
        if (this.mRootView == null) {
            this.mRootView = getActivity().getLayoutInflater().inflate(R.layout.edit_event_day_view, (ViewGroup) null);
        }
        return this.mRootView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mRootView = null;
    }

    private void updateTempEventViews() {
        ((TabletEditEventAllDayView) this.allDayView).setTempEvent(this.mTempEvent);
        this.calendarGridView.post(new Runnable() { // from class: com.sonymobile.calendar.editevent.TabletEditEventDayFragment.1
            @Override // java.lang.Runnable
            public void run() {
                ((TabletEditEventDayView) TabletEditEventDayFragment.this.calendarGridView).setTempEvent(TabletEditEventDayFragment.this.mTempEvent);
            }
        });
    }

    private void updateDateSwitcher(Time time) {
        this.daySwitcher.setIsSwitchEnabled(this.canChangeDate);
        this.daySwitcher.updateDateLabel(getActivity(), time);
    }

    @Override // com.sonymobile.calendar.CalendarFragment
    protected void initDateSwitcher(View view) {
        DaySwitcher daySwitcher = new DaySwitcher(view);
        this.daySwitcher = daySwitcher;
        daySwitcher.setOnDateSwitcherClickedListener(new CalendarFragment.DateSwitcherListener());
    }

    public void updateEventColor(int i) {
        this.mTempEvent.color = i;
        updateTempEventViews();
    }
}
