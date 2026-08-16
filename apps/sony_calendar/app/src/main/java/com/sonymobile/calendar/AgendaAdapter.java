package com.sonymobile.calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonyericsson.calendar.util.WeatherService;
import com.sonymobile.calendar.utils.UiUtils;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class AgendaAdapter extends ArrayAdapter<EventInfo> {
    private static final int ALARM_EVENT_ITEM_TYPE = 0;
    public static final int INDICATOR_NEXT = -2;
    public static final int INDICATOR_NONE = -1;
    public static final int INDICATOR_NOW = -3;
    public static final float LINE_ALPHA = 0.3f;
    private static final int STANDARD_EVENT_ITEM_TYPE = 1;
    private int agendaItemLayout;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<EventInfo> eventList;
    private EventPickerFragment eventPicker;
    private boolean isInEventPickerFragment;
    private boolean isListInMonthView;
    private boolean isListInPreview;
    private EventInfo mLastLabeled;
    private long mSelectedEventInstanceId;
    private Time mTime;
    private final String noTitleLabel;
    private boolean shouldAdaptConflictDialog;

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 2;
    }

    static class ViewHolder {
        ImageView alarmIcon;
        TextView beginTime;
        CheckBox checkBox;
        ImageView colorIndicator;
        TextView dateViewBottom;
        TextView dateViewTop;
        View dayHeader;
        ViewGroup mainLayout;
        ImageView recurringIcon;
        TextView startsIn;
        TextView title;
        TextView where;

        ViewHolder() {
        }
    }

    public AgendaAdapter(Context context, int i, ArrayList<EventInfo> arrayList, boolean z) {
        super(context, i, arrayList);
        this.isListInMonthView = false;
        this.isListInPreview = false;
        this.isInEventPickerFragment = false;
        this.agendaItemLayout = i;
        this.eventList = arrayList;
        this.shouldAdaptConflictDialog = z;
        this.noTitleLabel = context.getResources().getString(R.string.no_title_label);
        this.isInEventPickerFragment = i != R.layout.agenda_item;
    }

    public void updateWeather() {
        if (this.isInEventPickerFragment) {
            return;
        }
        WeatherService.getInstance().requestWeather(getContext(), new WeatherResultHandler(this), 0, false);
    }

    public void setTime(Time time) {
        this.mTime = time;
    }

    public void setIsListInMonthView(boolean z) {
        this.isListInMonthView = z;
    }

    public void remove(int i) {
        remove(this.eventList.get(i));
    }

    /* JADX WARN: Code duplicated, block: B:8:0x002c  */
    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewInflate;
        boolean z;
        View view2;
        float dimension;
        String dateRange;
        int i3;
        String string;
        boolean z2 = this.eventList.get(i).isAlarmEvent;
        if (z2) {
            if (view == null) {
                viewInflate = View.inflate(getContext(), R.layout.agenda_alarm_item, null);
            } else {
                viewInflate = view;
            }
        } else if (view == null) {
            viewInflate = View.inflate(getContext(), this.agendaItemLayout, null);
        } else {
            viewInflate = view;
        }
        ViewHolder viewHolder = viewInflate.getTag() instanceof ViewHolder ? (ViewHolder) viewInflate.getTag() : null;
        EventInfo eventInfo = this.eventList.get(i);
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            if (this.isInEventPickerFragment) {
                viewHolder.checkBox = (CheckBox) viewInflate.findViewById(R.id.checkBox);
            }
            if (z2) {
                viewHolder.alarmIcon = (ImageView) viewInflate.findViewById(R.id.alarm_icon);
            }
            viewHolder.recurringIcon = (ImageView) viewInflate.findViewById(R.id.repeat_icon);
            viewHolder.colorIndicator = (ImageView) viewInflate.findViewById(R.id.calendar_indicator);
            viewHolder.dayHeader = viewInflate.findViewById(R.id.dayHeader);
            viewHolder.startsIn = (TextView) viewInflate.findViewById(R.id.startsIn);
            viewHolder.beginTime = (TextView) viewInflate.findViewById(R.id.beginTime);
            viewHolder.title = (TextView) viewInflate.findViewById(R.id.title);
            viewHolder.where = (TextView) viewInflate.findViewById(R.id.location);
            viewHolder.dateViewTop = (TextView) viewInflate.findViewById(R.id.dateTopRow);
            viewHolder.dateViewBottom = (TextView) viewInflate.findViewById(R.id.dateBottomRow);
            viewHolder.mainLayout = (ViewGroup) viewInflate.findViewById(R.id.agenda_item_main_layout);
            viewInflate.setTag(viewHolder);
        }
        if (Utils.isTabletDevice(getContext()) && this.mSelectedEventInstanceId == eventInfo.instanceId && !z2) {
            viewHolder.mainLayout.setBackgroundColor(UiUtils.getPrimaryColor(getContext()));
            z = true;
        } else {
            viewHolder.mainLayout.setBackgroundResource(R.drawable.agenda_task_navigation_selector);
            z = false;
        }
        if (viewHolder.checkBox != null && this.eventPicker != null) {
            viewHolder.checkBox.setChecked(this.eventPicker.isEventSelected(eventInfo));
        }
        if (z2) {
            if (DateFormat.is24HourFormat(getContext())) {
                viewHolder.alarmIcon.setPadding(0, 0, (int) getContext().getResources().getDimension(R.dimen.agenda_alarm_icon_align_padding_end), 0);
            } else {
                viewHolder.alarmIcon.setPadding(0, 0, 0, 0);
            }
            viewHolder.colorIndicator.getDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
        } else {
            viewHolder.colorIndicator.getDrawable().setColorFilter(UiUtils.getDisplayColorFromColor(eventInfo.color), PorterDuff.Mode.MULTIPLY);
        }
        viewHolder.title.setText(!TextUtils.isEmpty(eventInfo.title) ? eventInfo.title : this.noTitleLabel);
        long j = eventInfo.begin;
        int i4 = eventInfo.startDay;
        boolean z3 = eventInfo.allDay != 0;
        Time time = new SafeTime(Utils.getTimeZone(getContext(), null));
        Time time2 = this.mTime;
        if (time2 != null) {
            view2 = viewInflate;
            time.set(time2.toMillis(true));
        } else {
            view2 = viewInflate;
            time.set(Utils.getDisplayTime());
            if (z2) {
                if (this.shouldAdaptConflictDialog) {
                    if (Utils.isInLandscapeMode(getContext())) {
                        dimension = getContext().getResources().getDimension(R.dimen.conflict_view_alarm_padding_start_landscape);
                    } else {
                        dimension = getContext().getResources().getDimension(R.dimen.conflict_view_alarm_padding_start);
                    }
                } else {
                    dimension = getContext().getResources().getDimension(R.dimen.agenda_view_alarm_title_padding_start);
                }
                viewHolder.beginTime.setPadding((int) dimension, 0, 0, 0);
            }
        }
        if (!this.isListInMonthView) {
            time.setJulianDay(i4);
        }
        time.set(0, 0, 0, time.monthDay, time.month, time.year);
        long millis = time.toMillis(true);
        if (millis - j > 0) {
            j = millis;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        long timeInMillis = calendar.getTimeInMillis() + ((long) (calendar.get(15) + calendar.get(16)));
        EventInfo eventInfo2 = i > 0 ? this.eventList.get(i - 1) : null;
        if (eventInfo2 != null) {
            long j2 = eventInfo2.localBegin;
            dateRange = Utils.formatDateRange(getContext(), j2, j2, 8);
        } else {
            dateRange = "";
        }
        String dateRange2 = Utils.formatDateRange(getContext(), j, j, 8);
        if (!dateRange2.equals(dateRange) && !this.isListInMonthView) {
            viewHolder.dayHeader.setVisibility(0);
            String dateRange3 = Utils.formatDateRange(getContext(), j, j, 2);
            String dateRange4 = Utils.formatDateRange(getContext(), timeInMillis, timeInMillis, 8);
            Time time3 = new SafeTime();
            time3.set(j);
            time3.normalize(false);
            int color = ContextCompat.getColor(getContext(), R.color.agenda_date_header_text_color);
            viewHolder.dateViewTop.setTextColor(color);
            viewHolder.dateViewBottom.setTextColor(color);
            if (dateRange2.equals(dateRange4)) {
                string = Utils.formatDateRange(getContext(), j, j, 4).equals(Utils.formatDateRange(getContext(), timeInMillis, timeInMillis, 4)) ? getContext().getString(R.string.agenda_today, Utils.formatDateRange(getContext(), timeInMillis, timeInMillis, 2)) : dateRange3;
                if (!this.isInEventPickerFragment) {
                    setFreeDayColor(viewHolder.dayHeader, 600, i4);
                }
            } else {
                if (!this.isInEventPickerFragment) {
                    setFreeDayColor(viewHolder.dateViewTop, FreeDayServiceResultHandler.REQUEST_IS_FREE_DAY_FOREGROUND, i4);
                    setFreeDayColor(viewHolder.dateViewBottom, FreeDayServiceResultHandler.REQUEST_IS_FREE_DAY_FOREGROUND, i4);
                    viewHolder.dayHeader.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_area_background));
                }
                string = dateRange3;
            }
            Calendar.getInstance().setTimeInMillis(j);
            viewHolder.dateViewTop.setText(string.toUpperCase());
            viewHolder.dateViewBottom.setText(dateRange2.toUpperCase());
            if (!this.isInEventPickerFragment) {
                FreeDayService.getInstance().requestHolidayName(getContext(), i4, new FreeDayServiceResultHandler(viewHolder.dateViewBottom), 1);
            }
        } else {
            viewHolder.dayHeader.setVisibility(8);
        }
        int timeUntilEvent = getTimeUntilEvent(i, eventInfo);
        TextView textView = viewHolder.startsIn;
        if (timeUntilEvent == -1) {
            textView.setVisibility(4);
        } else {
            if (timeUntilEvent == -2) {
                viewHolder.startsIn.setText(getContext().getResources().getString(R.string.agenda_next_indicator_txt));
            } else if (timeUntilEvent == -3) {
                viewHolder.startsIn.setText(getContext().getResources().getString(R.string.agenda_now_indicator_txt));
            } else {
                viewHolder.startsIn.setText(String.format(getContext().getResources().getString(R.string.agenda_minutes_until_indicator_txt), Integer.valueOf(timeUntilEvent)));
            }
            viewHolder.startsIn.setVisibility(0);
        }
        if (z3) {
            viewHolder.beginTime.setText(getContext().getResources().getString(R.string.calendar_agenda_all_day_event_txt));
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            if (!DateFormat.is24HourFormat(getContext())) {
                simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            }
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(time.timezone));
            viewHolder.beginTime.setText(simpleDateFormat.format(new Date(j)));
        }
        int i5 = eventInfo.rrule == null ? 4 : 0;
        if (!z2) {
            viewHolder.recurringIcon.setVisibility(i5);
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (Utils.isTabletDevice(getContext()) && z) {
            viewHolder.startsIn.setTextColor(-1);
            viewHolder.beginTime.setTextColor(-1);
            viewHolder.title.setTextColor(-1);
            if (!z2) {
                viewHolder.where.setTextColor(-1);
            }
        } else if (!isAllDayEventToday(eventInfo, jCurrentTimeMillis) && eventInfo.localEnd < jCurrentTimeMillis && !this.isInEventPickerFragment) {
            int color2 = ContextCompat.getColor(getContext(), R.color.past_events_text_color);
            viewHolder.startsIn.setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_primary_text_color));
            viewHolder.beginTime.setTextColor(color2);
            viewHolder.title.setTextColor(color2);
            if (z2) {
                viewHolder.alarmIcon.setColorFilter(color2);
                viewHolder.colorIndicator.setColorFilter(color2);
            } else {
                viewHolder.where.setTextColor(color2);
            }
        } else {
            int color3 = ContextCompat.getColor(getContext(), R.color.events_text_color);
            viewHolder.startsIn.setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_primary_text_color));
            viewHolder.beginTime.setTextColor(color3);
            if (eventInfo.selfAttendeeStatus == 3) {
                viewHolder.title.setTextColor(UiUtils.getDisplayColorFromColor(eventInfo.color));
            } else {
                viewHolder.title.setTextColor(color3);
            }
            if (!z2) {
                viewHolder.where.setTextColor(color3);
            }
        }
        String str = eventInfo.eventLocation;
        if (z2) {
            i3 = 8;
        } else if (!TextUtils.isEmpty(str)) {
            viewHolder.where.setText(str);
            viewHolder.where.setTextColor(UiUtils.getSecondaryTextColor(getContext()));
            viewHolder.where.setVisibility(0);
            i3 = 8;
        } else {
            i3 = 8;
            viewHolder.where.setVisibility(8);
        }
        if (!Utils.isTabletDevice(getContext()) && !z2) {
            viewHolder.recurringIcon.setVisibility(i3);
        }
        if (z2 && TextUtils.isEmpty(eventInfo.title)) {
            viewHolder.title.setVisibility(i3);
            viewHolder.colorIndicator.setVisibility(i3);
        }
        return view2;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        return this.eventList.get(i).isAlarmEvent ? 0 : 1;
    }

    private boolean isAllDayEventToday(EventInfo eventInfo, long j) {
        return (eventInfo.allDay != 0) && j < eventInfo.localBegin + 86400000;
    }

    private int getTimeUntilEvent(int i, EventInfo eventInfo) {
        EventInfo eventInfo2;
        if (DateUtils.isToday(eventInfo.localBegin) && this.isListInMonthView && !this.isListInPreview && this.agendaItemLayout == R.layout.agenda_item) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            EventInfo eventInfo3 = i > 0 ? this.eventList.get(i - 1) : null;
            if (eventInfo3 == null || jCurrentTimeMillis > eventInfo3.localEnd || (eventInfo3.localBegin == eventInfo.localBegin && ((eventInfo2 = this.mLastLabeled) == null || eventInfo2.equals(eventInfo3)))) {
                if (jCurrentTimeMillis <= eventInfo.localEnd && jCurrentTimeMillis >= eventInfo.localBegin) {
                    this.mLastLabeled = eventInfo;
                    return -3;
                }
                if (jCurrentTimeMillis < eventInfo.localBegin) {
                    this.mLastLabeled = eventInfo;
                    if (3600000 + jCurrentTimeMillis >= eventInfo.localBegin) {
                        return (int) Math.ceil((eventInfo.localBegin - jCurrentTimeMillis) / 60000.0f);
                    }
                    return -2;
                }
            }
        }
        return -1;
    }

    private static class WeatherResultHandler implements IAsyncServiceResultHandler {
        private final WeakReference<ArrayAdapter> arrayAdapterWeakReference;

        private WeatherResultHandler(ArrayAdapter arrayAdapter) {
            this.arrayAdapterWeakReference = new WeakReference<>(arrayAdapter);
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            ArrayAdapter arrayAdapter = this.arrayAdapterWeakReference.get();
            if (arrayAdapter != null && (obj instanceof Boolean) && ((Boolean) obj).booleanValue()) {
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onResume() {
        if (this.isListInMonthView) {
            this.broadcastReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.AgendaAdapter.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("android.intent.action.TIME_TICK")) {
                        AgendaAdapter.this.notifyDataSetChanged();
                    }
                }
            };
            getContext().registerReceiver(this.broadcastReceiver, new IntentFilter("android.intent.action.TIME_TICK"), 2);
        }
    }

    public void onPause() {
        if (this.broadcastReceiver != null) {
            getContext().unregisterReceiver(this.broadcastReceiver);
            this.broadcastReceiver = null;
        }
    }

    public void setListIsInPreview() {
        setIsListInMonthView(true);
        this.isListInPreview = true;
    }

    public void setIsInEventPickerFragment(EventPickerFragment eventPickerFragment) {
        this.eventPicker = eventPickerFragment;
    }

    private void setFreeDayColor(View view, int i, int i2) {
        FreeDayService.getInstance().requestIsFreeDay(getContext(), i2, new FreeDayServiceResultHandler(view), i);
    }

    private class FreeDayServiceResultHandler implements IAsyncServiceResultHandler {
        public static final int REQUEST_IS_FREE_DAY_BACKGROUND = 600;
        public static final int REQUEST_IS_FREE_DAY_FOREGROUND = 601;
        private View view;

        public FreeDayServiceResultHandler(View view) {
            this.view = view;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (obj == null || obj2 == null) {
                return;
            }
            int iIntValue = ((Integer) obj2).intValue();
            if (iIntValue == 1) {
                String str = (String) obj;
                if (str.equals("")) {
                    return;
                }
                ((TextView) this.view).append(", " + str);
                return;
            }
            if (iIntValue == 600) {
                if (((Boolean) obj).booleanValue()) {
                    this.view.setBackgroundColor(FreeDayService.getInstance().getHolidayColor(AgendaAdapter.this.getContext()));
                }
            } else if (iIntValue == 601 && ((Boolean) obj).booleanValue()) {
                ((TextView) this.view).setTextColor(FreeDayService.getInstance().getHolidayColor(AgendaAdapter.this.getContext()));
            }
        }
    }

    public void setSelectedEventInstanceId(long j) {
        this.mSelectedEventInstanceId = j;
    }
}
