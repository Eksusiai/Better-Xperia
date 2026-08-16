package com.sonymobile.calendar;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.editevent.EditEventActivity;
import com.sonymobile.calendar.editevent.TabletEditEventActivity;
import com.sonymobile.calendar.tablet.TabletEventInfoActivity;
import com.sonymobile.calendar.utils.EventUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public class AgendaListView extends ListView implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener, AgendaEventLoaderListener {
    public AgendaEventLoader agendaEventLoader;
    private boolean blockLayout;
    private Context context;
    private EventPickerFragment eventPicker;
    private CountDownTimer eventsChangedTimer;
    private boolean hasCancledTouchEvent;
    private boolean isEventsChangedTimerRunning;
    private boolean isInEventPickerFragment;
    private AppCompatActivity mActivity;
    private ListAdapter mAdapter;
    private AgendaFragment mAgendaFragment;
    private DeleteEventHelper mDeleteEventHelper;
    private boolean mIsClicked;
    private boolean mIsListInMonthView;
    private View mItemClicked;
    private KeyboardListener mKeyboardListener;
    private AgendaListNavigatorCallback mListNavigator;
    private LinearLayout mNoEventView;
    private TextView mNoEventsInListText;
    private AdapterView.OnItemClickListener mPreviewItemClickListener;
    private int selectedItemPosition;
    private boolean setSelectionHasBeenCalled;
    private int setSmoothScrollHasBeenCalled;

    public interface AgendaListNavigatorCallback {
        void goToNext();

        void moveToPrevious();
    }

    public AgendaListView(Context context) {
        this(context, null);
    }

    public AgendaListView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AgendaListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsListInMonthView = false;
        this.selectedItemPosition = 0;
        this.setSelectionHasBeenCalled = false;
        this.setSmoothScrollHasBeenCalled = -1;
        this.isEventsChangedTimerRunning = false;
        this.blockLayout = false;
        this.isInEventPickerFragment = false;
        this.hasCancledTouchEvent = false;
        setOnCreateContextMenuListener(this);
        setChoiceMode(1);
        setVerticalScrollBarEnabled(false);
        setOnItemClickListener(this);
        this.mActivity = (AppCompatActivity) context;
        this.context = context;
        this.mDeleteEventHelper = new DeleteEventHelper(this.mActivity, false);
        initNoEventsView();
    }

    public boolean isListAtTop() {
        if (getChildAt(0) != null) {
            return getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0;
        }
        return true;
    }

    @Override // android.widget.AbsListView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        AgendaListNavigatorCallback agendaListNavigatorCallback;
        if (this.mIsListInMonthView && (agendaListNavigatorCallback = this.mListNavigator) != null && ((SplitScreenAgendaFragment) agendaListNavigatorCallback).isMonthGridMoving()) {
            if (this.hasCancledTouchEvent) {
                return true;
            }
            MotionEvent motionEventObtain = MotionEvent.obtain(motionEvent.getDownTime(), motionEvent.getEventTime(), 3, motionEvent.getX(), motionEvent.getY(), motionEvent.getPressure(), motionEvent.getSize(), motionEvent.getMetaState(), motionEvent.getXPrecision(), motionEvent.getYPrecision(), motionEvent.getDeviceId(), motionEvent.getEdgeFlags());
            this.hasCancledTouchEvent = true;
            boolean zOnTouchEvent = super.onTouchEvent(motionEventObtain);
            motionEventObtain.recycle();
            return zOnTouchEvent;
        }
        if (this.hasCancledTouchEvent && motionEvent.getAction() == 1) {
            this.hasCancledTouchEvent = false;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setIsInSearch() {
        this.mNoEventsInListText.setText(R.string.event_search_no_result);
    }

    public void setAgendaListNavigator(AgendaListNavigatorCallback agendaListNavigatorCallback) {
        this.mListNavigator = agendaListNavigatorCallback;
    }

    public void initEventLoader(Fragment fragment) {
        AgendaEventLoader agendaEventLoader = new AgendaEventLoader(this.context, fragment, this.isInEventPickerFragment, Utils.getAgendaSelectedEventInstanceId());
        this.agendaEventLoader = agendaEventLoader;
        if (this.isInEventPickerFragment) {
            agendaEventLoader.setIsInEventPickerFragment(this.eventPicker);
        }
        this.agendaEventLoader.setAgendaEventLoaderListener(this);
        setOnScrollListener(this.agendaEventLoader);
        setAdapter((ListAdapter) this.agendaEventLoader.getAgendaAdapter());
    }

    @Override // android.widget.ListView, android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View view, long j) {
        return super.drawChild(canvas, view, j);
    }

    public void setBlockLayout(boolean z) {
        this.blockLayout = z;
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.blockLayout) {
            return;
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override // com.sonymobile.calendar.AgendaEventLoaderListener
    public void onNoEventsFound() {
        removeHeaderView(this.mNoEventView);
        this.mNoEventView.setVisibility(0);
        addHeaderView(this.mNoEventView);
        AgendaListNavigatorCallback agendaListNavigatorCallback = this.mListNavigator;
        if (agendaListNavigatorCallback != null) {
            ((SplitScreenAgendaFragment) agendaListNavigatorCallback).onEventsDisplayed();
        }
    }

    @Override // com.sonymobile.calendar.AgendaEventLoaderListener
    public void onEventsFound() {
        this.mNoEventView.setVisibility(8);
        removeHeaderView(this.mNoEventView);
        AgendaListNavigatorCallback agendaListNavigatorCallback = this.mListNavigator;
        if (agendaListNavigatorCallback != null) {
            ((SplitScreenAgendaFragment) agendaListNavigatorCallback).onEventsDisplayed();
        }
    }

    @Override // com.sonymobile.calendar.AgendaEventLoaderListener
    public void onEventSelected(EventInfo eventInfo) {
        if (eventInfo == null) {
            updateSelectedEventId(0L, 0L, 0L, 0L, false);
        } else {
            updateSelectedEventId(eventInfo.id, eventInfo.instanceId, eventInfo.begin, eventInfo.end, eventInfo.isLunarEvent);
        }
    }

    @Override // com.sonymobile.calendar.AgendaEventLoaderListener
    public void onPositionSelected(int i, boolean z) {
        View childAt;
        if (z && (childAt = getChildAt(0)) != null) {
            setSelectionFromTop(i, childAt.getTop());
        }
    }

    @Override // android.widget.ListView, android.widget.AdapterView
    public void setSelection(int i) {
        this.setSelectionHasBeenCalled = true;
        this.selectedItemPosition = i;
        if (this.setSmoothScrollHasBeenCalled == 0 && scrollToBirthdayHeader()) {
            return;
        }
        if (this.mIsListInMonthView && getHeaderViewsCount() > 0) {
            i++;
        }
        super.setSelection(i);
    }

    public boolean scrollToBirthdayHeader() {
        if (this.selectedItemPosition <= 1) {
            this.setSmoothScrollHasBeenCalled = 1;
            if (this.setSelectionHasBeenCalled) {
                super.smoothScrollToPosition(0);
                return true;
            }
        }
        return false;
    }

    public void setIsInMonthView() {
        this.mIsListInMonthView = true;
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader != null) {
            agendaEventLoader.setIsListInMonthView(true);
        }
    }

    public void onResume() {
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader != null) {
            agendaEventLoader.onResume();
        }
        this.mIsClicked = false;
        View view = this.mItemClicked;
        if (view != null) {
            view.invalidate();
        }
    }

    private void initNoEventsView() {
        LinearLayout linearLayout = (LinearLayout) ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.agenda_no_events_header, (ViewGroup) null);
        this.mNoEventView = linearLayout;
        this.mNoEventsInListText = (TextView) linearLayout.findViewById(R.id.agenda_no_events_text);
        this.mNoEventView.setVisibility(8);
        addHeaderView(this.mNoEventView);
    }

    @Override // android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        EventInfo item;
        if (this.isInEventPickerFragment || (item = getItem(((AdapterView.AdapterContextMenuInfo) contextMenuInfo).position - getHeaderViewsCount())) == null) {
            return;
        }
        String string = item.title;
        if (item.title == null || item.title.length() < 1) {
            string = getResources().getString(R.string.no_title_label);
        }
        contextMenu.setHeaderTitle(string);
        int i = item.accessLevel;
        if (i == 0) {
            return;
        }
        ContextMenuHandler contextMenuHandler = new ContextMenuHandler(this);
        if (i >= 1) {
            contextMenu.add(0, 7, 0, R.string.event_delete).setOnMenuItemClickListener(contextMenuHandler);
        }
        if (i == 2) {
            contextMenu.add(0, 6, 0, R.string.event_edit).setOnMenuItemClickListener(contextMenuHandler);
        }
    }

    public void onPause() {
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader != null) {
            agendaEventLoader.onPause();
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (!this.mIsClicked || this.isInEventPickerFragment) {
            this.mIsClicked = true;
            int headerViewsCount = i - getHeaderViewsCount();
            this.mItemClicked = view;
            view.setEnabled(false);
            EventInfo item = getItem(headerViewsCount);
            if (this.isInEventPickerFragment) {
                ((CheckBox) view.findViewById(R.id.checkBox)).setChecked(this.eventPicker.eventClicked(item));
                return;
            }
            if (Utils.isTabletDevice(getContext())) {
                if (item == null) {
                    return;
                }
                setItemChecked(headerViewsCount, true);
                if (this.mIsListInMonthView) {
                    if (item.isAlarmEvent) {
                        EventUtils.startAlarmApp(this.context);
                    } else {
                        startEventAction(headerViewsCount, "android.intent.action.VIEW", TabletEventInfoActivity.class.getName());
                    }
                } else {
                    this.mIsClicked = false;
                }
                if (item.isAlarmEvent) {
                    EventUtils.startAlarmApp(this.context);
                }
                Utils.setAgendaSelectedEventInstanceId(item.instanceId);
                Utils.setDisplayTime(Long.valueOf(item.begin));
                Utils.setAgendaSelectedBegin(item.localBegin);
                AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
                if (agendaEventLoader != null) {
                    agendaEventLoader.setSelectedEventInstanceId(item.instanceId);
                }
                updateSelectedEventId(item.id, item.instanceId, item.begin, item.end, item.isLunarEvent);
                Utils.setAgendaSelectedEventInstanceId(item.instanceId);
            } else if (item.isAlarmEvent) {
                EventUtils.startAlarmApp(this.context);
            } else {
                startEventAction(headerViewsCount, "android.intent.action.VIEW", EventInfoActivity.class.getName());
            }
            AdapterView.OnItemClickListener onItemClickListener = this.mPreviewItemClickListener;
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(adapterView, view, i, j);
            }
        }
    }

    public void goTo(Time time, String str, boolean z) {
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader == null) {
            return;
        }
        agendaEventLoader.refresh(Time.getJulianDay(time.toMillis(false), time.gmtoff), str, z);
    }

    public void goToDay(Time time, String str) {
        this.setSmoothScrollHasBeenCalled = -1;
        this.setSelectionHasBeenCalled = false;
        this.selectedItemPosition = 0;
        setItemChecked(getCheckedItemPosition(), false);
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader == null) {
            return;
        }
        agendaEventLoader.goToDay(time, str);
    }

    public void setHideDeclinedEvents(boolean z) {
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader == null) {
            return;
        }
        agendaEventLoader.setHideDeclinedEvents(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startEventAction(int i, String str, String str2) {
        EventInfo item = getItem(i);
        if (item == null) {
            return;
        }
        Intent intent = new Intent(str);
        intent.setData(ContentUris.withAppendedId(item.isLunarEvent ? LunarContract.Events.CONTENT_URI : CalendarContract.Events.CONTENT_URI, item.id));
        intent.setClassName(this.context, str2);
        intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, item.begin);
        intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, item.end);
        if (str.equals("android.intent.action.VIEW")) {
            if (this.mPreviewItemClickListener != null) {
                Bundle bundleMakeZoomAnimationOnViewBundle = UiUtils.makeZoomAnimationOnViewBundle(this.mItemClicked);
                if (bundleMakeZoomAnimationOnViewBundle == null) {
                    startActivitySlideUp(intent);
                    return;
                } else {
                    this.mActivity.startActivity(intent, bundleMakeZoomAnimationOnViewBundle);
                    return;
                }
            }
            startActivitySlideUp(intent);
            return;
        }
        this.mActivity.startActivity(intent);
    }

    private void startActivitySlideUp(Intent intent) {
        intent.putExtra(UiUtils.SLIDE_UP_ANIMATION, true);
        this.mActivity.startActivity(intent);
    }

    public void deleteSelectedEvent(final int i) {
        EventInfo item = getItem(i);
        if (item != null) {
            final long j = item.id;
            final long j2 = item.instanceId;
            this.mDeleteEventHelper.delete(item.begin, item.end, item.id, -1, item.isLunarEvent);
            this.mDeleteEventHelper.setOnDeleteListener(new DeleteEventHelper.onDeleteCallback() { // from class: com.sonymobile.calendar.AgendaListView.1
                @Override // com.sonymobile.calendar.DeleteEventHelper.onDeleteCallback
                public void onSingleDeleteConfirmed() {
                    if (AgendaListView.this.agendaEventLoader != null) {
                        AgendaListView.this.agendaEventLoader.removeItem(i, j, j2);
                    } else {
                        ((AgendaAdapter) AgendaListView.this.getAdapter()).remove(i);
                    }
                }

                @Override // com.sonymobile.calendar.DeleteEventHelper.onDeleteCallback
                public void onRepeatingAllDeleteConfirmed() {
                    if (AgendaListView.this.agendaEventLoader != null) {
                        AgendaListView.this.agendaEventLoader.removeRepeatingItems(j);
                        AgendaListView.this.agendaEventLoader.loadEventFromPosition(AgendaListView.this.getCheckedItemPosition());
                        return;
                    }
                    ((AgendaAdapter) AgendaListView.this.getAdapter()).remove(i);
                }
            });
        }
    }

    public void deleteEvent(long j) {
        int positionForEvent;
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader == null || (positionForEvent = agendaEventLoader.getPositionForEvent(j)) == -1) {
            return;
        }
        deleteSelectedEvent(positionForEvent);
    }

    public void updateEvent(long j, long j2) {
        int positionForEvent;
        EventInfo item;
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader == null || (positionForEvent = agendaEventLoader.getPositionForEvent(j)) == -1 || (item = getItem(positionForEvent)) == null) {
            return;
        }
        item.id = j2;
    }

    private static class ContextMenuHandler implements MenuItem.OnMenuItemClickListener {
        private final WeakReference<AgendaListView> agendaListViewWeakReference;

        public ContextMenuHandler(AgendaListView agendaListView) {
            this.agendaListViewWeakReference = new WeakReference<>(agendaListView);
        }

        @Override // android.view.MenuItem.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem menuItem) {
            String name;
            AgendaListView agendaListView = this.agendaListViewWeakReference.get();
            if (agendaListView == null) {
                return false;
            }
            int itemId = menuItem.getItemId();
            if (itemId != 6) {
                if (itemId != 7) {
                    return false;
                }
                agendaListView.deleteSelectedEvent(((AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo()).position - agendaListView.getHeaderViewsCount());
                return true;
            }
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
            if (Utils.isTabletDevice(agendaListView.getContext())) {
                name = TabletEditEventActivity.class.getName();
            } else {
                name = EditEventActivity.class.getName();
            }
            agendaListView.startEventAction(adapterContextMenuInfo.position - agendaListView.getHeaderViewsCount(), "android.intent.action.EDIT", name);
            return true;
        }
    }

    public void setAgendaFragment(AgendaFragment agendaFragment) {
        this.mAgendaFragment = agendaFragment;
    }

    public void updateSelectedEventId(long j, long j2, long j3, long j4, boolean z) {
        AgendaFragment agendaFragment = this.mAgendaFragment;
        if (agendaFragment != null) {
            agendaFragment.updateSelectedEventId(j, j2, j3, j4, z);
        }
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [com.sonymobile.calendar.AgendaListView$2] */
    public void onCalendarsChanged() {
        CountDownTimer countDownTimer;
        if (this.agendaEventLoader == null) {
            return;
        }
        if (this.isEventsChangedTimerRunning && (countDownTimer = this.eventsChangedTimer) != null) {
            countDownTimer.cancel();
        }
        this.eventsChangedTimer = new CountDownTimer(1000L, 100L) { // from class: com.sonymobile.calendar.AgendaListView.2
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                AgendaListView.this.agendaEventLoader.onCalendarsChanged();
                AgendaListView.this.isEventsChangedTimerRunning = false;
            }
        }.start();
        this.isEventsChangedTimerRunning = true;
    }

    public void setAdapterAndLoader(ListAdapter listAdapter, AgendaEventLoader agendaEventLoader) {
        super.setAdapter(listAdapter);
        if (listAdapter.getCount() > 0) {
            onEventsFound();
        } else {
            onNoEventsFound();
        }
        this.agendaEventLoader = agendaEventLoader;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(ListAdapter listAdapter) {
        super.setAdapter(listAdapter);
        if (this.isInEventPickerFragment) {
            this.mAdapter = listAdapter;
        }
    }

    @Override // android.widget.ListView, android.widget.AdapterView
    public ListAdapter getAdapter() {
        return this.isInEventPickerFragment ? this.mAdapter : super.getAdapter();
    }

    private EventInfo getItem(int i) {
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader != null) {
            return agendaEventLoader.getItem(i);
        }
        return (EventInfo) getAdapter().getItem(i);
    }

    public void setKeyboardListener(KeyboardListener keyboardListener) {
        this.mKeyboardListener = keyboardListener;
    }

    public void setPreviewItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mPreviewItemClickListener = onItemClickListener;
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        View viewFindViewById;
        if (i == 52 || i == 111) {
            KeyboardListener keyboardListener = this.mKeyboardListener;
            if (keyboardListener != null) {
                keyboardListener.onEscapePressed();
            }
        } else {
            switch (i) {
                case 20:
                    if (this.agendaEventLoader != null && getSelectedItemPosition() == this.agendaEventLoader.getCount() && (viewFindViewById = this.mActivity.findViewById(R.id.addEventButton)) != null) {
                        viewFindViewById.requestFocus();
                        viewFindViewById.requestFocusFromTouch();
                    }
                    break;
                case 21:
                    if (this.mIsListInMonthView) {
                        this.mListNavigator.moveToPrevious();
                    }
                    return true;
                case 22:
                    if (this.mIsListInMonthView) {
                        this.mListNavigator.goToNext();
                        return true;
                    }
                    break;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void setIsInEventPickerFragment(EventPickerFragment eventPickerFragment) {
        this.eventPicker = eventPickerFragment;
        this.isInEventPickerFragment = true;
    }

    public void callOnCalendarChange() {
        AgendaEventLoader agendaEventLoader = this.agendaEventLoader;
        if (agendaEventLoader != null) {
            agendaEventLoader.onCalendarsChanged();
            DeleteEventHelper.resetEventDeleted();
        }
    }

    public boolean isEventDeleted() {
        return DeleteEventHelper.isEventDeleted();
    }
}
