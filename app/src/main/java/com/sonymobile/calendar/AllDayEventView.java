package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AllDayEventView extends AllDayEventViewBase {
    private final EventInfo[] eventInfo;
    private CalendarEventNavigator mEventNavigator;
    private boolean mShowIcon;

    public AllDayEventView(Context context, EventInfo[] eventInfoArr, CalendarEventNavigator calendarEventNavigator, Time time, boolean z, ViewIndex viewIndex) {
        super(context, time, z, viewIndex);
        this.eventInfo = (EventInfo[]) eventInfoArr.clone();
        init(eventInfoArr.length == 1 ? eventInfoArr[0].color : -1);
        this.mEventNavigator = calendarEventNavigator;
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initContentDescription() {
        StringBuilder sbAppend = new StringBuilder("").append(this.eventInfo.length);
        sbAppend.append(getResources().getString(R.string.accessibility_all_day)).append(getContext().getResources().getQuantityString(R.plurals.plural_events, this.eventInfo.length));
        setContentDescription(sbAppend.toString());
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected String getTitleText() {
        this.mShowIcon = false;
        if (TextUtils.isEmpty(this.eventInfo[0].title)) {
            return getContext().getResources().getString(R.string.no_title_label);
        }
        return this.eventInfo[0].title;
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected Object getNavigationInfo() {
        return this.eventInfo;
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initIcon() {
        ImageView imageView = (ImageView) findViewById(R.id.event_icon);
        if (this.mShowIcon) {
            imageView.setBackgroundResource(R.drawable.ic_all_day);
            imageView.setVisibility(0);
        } else {
            imageView.setVisibility(8);
        }
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initLayout(int i) {
        LayoutInflater.from(getContext()).inflate(R.layout.all_day_event_item, this);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        boolean z = i == -1;
        setBackground(getResources().getDrawable(R.drawable.day_week_event_background));
        getBackground().setColorFilter(UiUtils.getDisplayColorFromColor(i), PorterDuff.Mode.MULTIPLY);
        setClickable(true);
        initTextView(z);
        initIcon();
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, final View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        CalendarContextMenuHelper.setTitle(getContext(), contextMenu, this.eventInfo[0]);
        CalendarContextMenuHelper.addViewEvent(contextMenu, this.eventInfo[0], new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.AllDayEventView.1
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                AllDayEventView.this.mEventNavigator.goToEventDetails(AllDayEventView.this.eventInfo[0], UiUtils.makeZoomAnimationOnViewBundle(view));
                return true;
            }
        });
        CalendarContextMenuHelper.addEditEvent(contextMenu, this.eventInfo[0], new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.AllDayEventView.2
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                AllDayEventView.this.mEventNavigator.goToEditEvent(AllDayEventView.this.eventInfo[0]);
                return true;
            }
        });
        CalendarContextMenuHelper.addDeleteEvent(contextMenu, this.eventInfo[0], new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.AllDayEventView.3
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                AllDayEventView.this.mEventNavigator.deleteEvent(AllDayEventView.this.eventInfo[0]);
                return true;
            }
        });
        CalendarContextMenuHelper.addCreateEventItem(contextMenu, new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.AllDayEventView.4
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                AllDayEventView.this.mEventNavigator.goToCreateEvent(AllDayEventView.this.eventInfo[0].localBegin, false);
                return true;
            }
        });
    }
}
