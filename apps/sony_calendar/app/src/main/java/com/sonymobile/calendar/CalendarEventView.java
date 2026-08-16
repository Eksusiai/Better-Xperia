package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.common.base.Strings;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.utils.UiUtils;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CalendarEventView extends LinearLayout implements View.OnCreateContextMenuListener {
    protected static final float ALPHA_SCALING_FACTOR = 0.7f;
    public static final int MIN_EVENT_TIME_FOR_DRAWING = 30;
    public static final String TAG = "CalendarEventView";
    private int clusterColumnIndex;
    private int clusterIndex;
    private long endTimeEventCard;
    protected EventInfo eventInfo;
    private CalendarEventNavigator eventNavigator;
    private float[] lastTouch;
    protected ImageView mAlarmIconImageView;
    private float mHighestCutoff;
    protected float mHourHeight;
    protected float mLargeFontSize;
    protected float mLargestFontSize;
    private float mLowestCutoff;
    private float mMiddleCutoff;
    protected float mSmallFontSize;
    protected float mSmallestFontSize;
    protected TextView mTitleTextView;

    protected abstract int getLayoutResource();

    public CalendarEventView(Context context, EventInfo eventInfo, CalendarEventNavigator calendarEventNavigator, int i, int i2) {
        super(context);
        this.clusterIndex = -1;
        this.clusterColumnIndex = -1;
        this.mHourHeight = DayColumnView.getDefaultHourHeight(this);
        this.eventInfo = eventInfo;
        this.eventNavigator = calendarEventNavigator;
        if (!eventInfo.isAlarmEvent) {
            setOnCreateContextMenuListener(this);
        }
        setupLayout();
        initContentDescription();
        this.mSmallestFontSize = getResources().getDimensionPixelSize(R.dimen.day_week_view_event_smallest_text_size);
        this.mSmallFontSize = getResources().getDimensionPixelSize(R.dimen.day_week_view_event_small_text_size);
        this.mLargeFontSize = getResources().getDimensionPixelSize(R.dimen.day_week_view_event_large_text_size);
        this.mLargestFontSize = getResources().getDimensionPixelSize(R.dimen.day_week_view_event_largest_text_size);
        float f = i;
        float f2 = i2 - i;
        float f3 = f2 / 6.0f;
        this.mHighestCutoff = (5.0f * f3) + f;
        this.mMiddleCutoff = (f2 / 2.0f) + f;
        this.mLowestCutoff = f + f3;
    }

    public long getLocalStartTimeMillis() {
        return this.eventInfo.localBegin;
    }

    private void setupEndTimeEventCard() {
        if (this.eventInfo.end - this.eventInfo.localBegin < TimeUnit.MINUTES.toMillis(30L)) {
            this.endTimeEventCard = this.eventInfo.localBegin + TimeUnit.MINUTES.toMillis(30L);
        } else {
            this.endTimeEventCard = this.eventInfo.end;
        }
    }

    public long getEndTimeEventCardInMillis() {
        return this.endTimeEventCard;
    }

    public long getEventId() {
        return this.eventInfo.id;
    }

    public EventInfo getEventInfo() {
        return this.eventInfo;
    }

    public boolean areOverlapping(long j, long j2) {
        return j < this.endTimeEventCard && j2 > this.eventInfo.localBegin;
    }

    public void setClusterIndex(int i) {
        this.clusterIndex = i;
    }

    public int getClusterIndex() {
        return this.clusterIndex;
    }

    public void setClusterColumnIndex(int i) {
        this.clusterColumnIndex = i;
    }

    public int getClusterColumnIndex() {
        return this.clusterColumnIndex;
    }

    public boolean shouldShowPreview() {
        return this.mTitleTextView.getHeight() > getHeight();
    }

    public boolean update(EventInfo eventInfo) {
        if (!eventInfo.equals(this.eventInfo)) {
            return true;
        }
        this.eventInfo = eventInfo;
        updateLayout();
        return false;
    }

    @Override // android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, final View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        CalendarContextMenuHelper.setTitle(getContext(), contextMenu, this.eventInfo);
        CalendarContextMenuHelper.addViewEvent(contextMenu, this.eventInfo, new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.CalendarEventView.1
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                CalendarEventView.this.eventNavigator.goToEventDetails(CalendarEventView.this.eventInfo, UiUtils.makeZoomAnimationOnViewBundle(view));
                return true;
            }
        });
        CalendarContextMenuHelper.addEditEvent(contextMenu, this.eventInfo, new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.CalendarEventView.2
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                CalendarEventView.this.eventNavigator.goToEditEvent(CalendarEventView.this.eventInfo);
                return true;
            }
        });
        CalendarContextMenuHelper.addDeleteEvent(contextMenu, this.eventInfo, new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.CalendarEventView.3
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                CalendarEventView.this.eventNavigator.deleteEvent(CalendarEventView.this.eventInfo);
                return true;
            }
        });
        CalendarContextMenuHelper.addCreateEventItem(contextMenu, new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.CalendarEventView.4
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                CalendarEventView.this.eventNavigator.goToCreateEvent(CalendarEventView.this.eventInfo.localBegin, false);
                return true;
            }
        });
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.lastTouch = new float[]{motionEvent.getX(), motionEvent.getY()};
        return super.onTouchEvent(motionEvent);
    }

    public float[] getLastTouchLocation() {
        if (this.lastTouch == null) {
            int[] iArr = new int[2];
            getRootView().getLocationInWindow(iArr);
            this.lastTouch = new float[]{iArr[0], iArr[1]};
        }
        return (float[]) this.lastTouch.clone();
    }

    private void setupLayout() {
        setupEndTimeEventCard();
        setOrientation(1);
        LayoutInflater.from(getContext()).inflate(getLayoutResource(), this);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.day_week_event_background);
        adjustBackgroundDrawable(drawable, this.eventInfo);
        setBackground(drawable);
        setClickable(true);
        setupViews();
    }

    private void updateLayout() {
        setupEndTimeEventCard();
        adjustBackgroundDrawable(getBackground(), this.eventInfo);
        setupViews();
    }

    private void adjustBackgroundDrawable(Drawable drawable, EventInfo eventInfo) {
        drawable.setColorFilter(UiUtils.getDisplayColorFromColor(eventInfo.color), PorterDuff.Mode.SRC_ATOP);
        if (3 == eventInfo.selfAttendeeStatus) {
            setAlpha(getAlpha() * ALPHA_SCALING_FACTOR);
        }
    }

    protected void setupViews() {
        if (this.eventInfo.isAlarmEvent) {
            this.mTitleTextView = (TextView) findViewById(R.id.alarm_event_description);
            this.mAlarmIconImageView = (ImageView) findViewById(R.id.alarm_event_icon);
        } else {
            this.mTitleTextView = (TextView) findViewById(R.id.event_title_and_location);
        }
        this.mTitleTextView.setText(determineTitle());
    }

    protected String determineTitle() {
        if (this.eventInfo.isAlarmEvent) {
            if (Strings.isNullOrEmpty(this.eventInfo.description)) {
                return getResources().getString(R.string.no_title_label);
            }
            return this.eventInfo.description;
        }
        if (Strings.isNullOrEmpty(this.eventInfo.title)) {
            return getResources().getString(R.string.no_title_label);
        }
        return this.eventInfo.title;
    }

    private void initContentDescription() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormat.is24HourFormat(getContext()) ? "HH:mm" : "h:mm a", Locale.getDefault());
        setContentDescription(this.eventInfo.title + ". " + String.format(getResources().getString(R.string.accessibility_start_time_to_end_time), simpleDateFormat.format(Long.valueOf(this.eventInfo.localBegin)), simpleDateFormat.format(Long.valueOf(this.eventInfo.end))) + ". " + this.eventInfo.eventLocation);
    }

    public void setHourHeight(int i) {
        this.mHourHeight = i;
    }

    void scaleEventTextSize(TextView textView) {
        float f = this.mHourHeight;
        if (f <= this.mLowestCutoff) {
            resetTextSize(textView, this.mSmallestFontSize);
            return;
        }
        if (f <= this.mMiddleCutoff) {
            resetTextSize(textView, this.mSmallFontSize);
        } else if (f <= this.mHighestCutoff) {
            resetTextSize(textView, this.mLargeFontSize);
        } else {
            resetTextSize(textView, this.mLargestFontSize);
        }
    }

    void resetTextSize(TextView textView, float f) {
        if (textView.getTextSize() != f) {
            textView.setTextSize(0, f);
        }
    }

    void scaleImageView(ImageView imageView, int i, int i2, int i3, int i4) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        if (i2 > i) {
            layoutParams.height = i;
        } else {
            layoutParams.height = i2;
        }
        if (i4 > i3) {
            layoutParams.width = i3;
        } else {
            layoutParams.width = i4;
        }
        imageView.setLayoutParams(layoutParams);
    }

    protected void scaleAlarmImageView(int i, int i2) {
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.alarmIconSize);
        scaleImageView(this.mAlarmIconImageView, i, dimensionPixelSize, i2, dimensionPixelSize);
    }
}
