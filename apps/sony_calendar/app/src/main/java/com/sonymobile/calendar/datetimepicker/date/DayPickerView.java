package com.sonymobile.calendar.datetimepicker.date;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.lunar.LunarHelper;
import com.sonymobile.lunar.lib.LunarUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class DayPickerView extends ListView implements AbsListView.OnScrollListener, DatePickerDialog.OnDateChangedListener {
    protected static final int GOTO_SCROLL_DURATION = 250;
    public static final int LIST_TOP_OFFSET = -1;
    protected static final int SCROLL_CHANGE_DELAY = 40;
    private static final String TAG = "MonthFragment";
    private final SimpleDateFormat YEAR_FORMAT;
    protected SimpleMonthAdapter mAdapter;
    private DatePickerController mController;
    protected int mCurrentScrollState;
    protected float mFriction;
    protected Handler mHandler;
    private boolean mPerformingScroll;
    protected int mPreviousScrollState;
    protected ScrollStateRunnable mScrollStateChangedRunnable;
    protected SimpleMonthAdapter.CalendarDay mSelectedDay;

    public DayPickerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.YEAR_FORMAT = new SimpleDateFormat(Utils.FORMAT_DATE_YEAR, Locale.getDefault());
        this.mFriction = 1.0f;
        this.mSelectedDay = new SimpleMonthAdapter.CalendarDay();
        this.mPreviousScrollState = 0;
        this.mCurrentScrollState = 0;
        this.mScrollStateChangedRunnable = new ScrollStateRunnable();
        init(context);
    }

    public DayPickerView(Context context, DatePickerController datePickerController) {
        super(context);
        this.YEAR_FORMAT = new SimpleDateFormat(Utils.FORMAT_DATE_YEAR, Locale.getDefault());
        this.mFriction = 1.0f;
        this.mSelectedDay = new SimpleMonthAdapter.CalendarDay();
        this.mPreviousScrollState = 0;
        this.mCurrentScrollState = 0;
        this.mScrollStateChangedRunnable = new ScrollStateRunnable();
        init(context);
        setController(datePickerController);
    }

    public void setController(DatePickerController datePickerController) {
        this.mController = datePickerController;
        datePickerController.registerOnDateChangedListener(this);
        setUpAdapter();
        setAdapter((ListAdapter) this.mAdapter);
        onDateChanged();
    }

    public void init(Context context) {
        this.mHandler = new Handler();
        setLayoutParams(new AbsListView.LayoutParams(-1, -1));
        setDrawSelectorOnTop(false);
        setUpListView();
    }

    public void onChange() {
        setUpAdapter();
        setAdapter((ListAdapter) this.mAdapter);
    }

    protected void setUpAdapter() {
        SimpleMonthAdapter simpleMonthAdapter = this.mAdapter;
        if (simpleMonthAdapter == null) {
            this.mAdapter = new SimpleMonthAdapter(getContext(), this.mController);
        } else {
            simpleMonthAdapter.setSelectedDay(this.mSelectedDay);
            this.mAdapter.notifyDataSetChanged();
        }
        this.mAdapter.notifyDataSetChanged();
    }

    protected void setUpListView() {
        setCacheColorHint(0);
        setDivider(null);
        setItemsCanFocus(true);
        setFastScrollEnabled(false);
        setVerticalScrollBarEnabled(false);
        setOnScrollListener(this);
        setFadingEdgeLength(0);
        setFriction(ViewConfiguration.getScrollFriction() * this.mFriction);
    }

    public void goTo(SimpleMonthAdapter.CalendarDay calendarDay, boolean z, boolean z2, boolean z3) {
        View childAt;
        if (z2) {
            this.mSelectedDay.set(calendarDay);
        }
        int minYear = ((calendarDay.year - this.mController.getMinYear()) * 12) + calendarDay.month;
        if (this.mController.isLunarOn()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendarDay.year, calendarDay.month, calendarDay.day);
            LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime());
            minYear = LunarHelper.getPositionFromDate(this.mController.getMinYear(), lunarDateConvertSolarDateToLunarDate.mYear, lunarDateConvertSolarDateToLunarDate.mMonth, lunarDateConvertSolarDateToLunarDate.mIsLeap);
        }
        int i = 0;
        while (true) {
            int i2 = i + 1;
            childAt = getChildAt(i);
            if (childAt == null) {
                break;
            }
            int top = childAt.getTop();
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "child at " + (i2 - 1) + " has top " + top);
            }
            if (top >= 0) {
                break;
            } else {
                i = i2;
            }
        }
        int positionForView = childAt != null ? getPositionForView(childAt) : 0;
        if (z2) {
            this.mAdapter.setSelectedDay(this.mSelectedDay);
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "GoTo position " + minYear);
        }
        if (minYear == positionForView && !z3) {
            if (z2) {
                invalidateViews();
            }
        } else {
            invalidateViews();
            this.mPreviousScrollState = 2;
            if (z) {
                smoothScrollToPositionFromTop(minYear, -1, 250);
            } else {
                postSetSelection(minYear);
            }
        }
    }

    public void postSetSelection(final int i) {
        clearFocus();
        post(new Runnable() { // from class: com.sonymobile.calendar.datetimepicker.date.DayPickerView.1
            @Override // java.lang.Runnable
            public void run() {
                DayPickerView.this.setSelection(i);
            }
        });
        onScrollStateChanged(this, 0);
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (((SimpleMonthView) absListView.getChildAt(0)) == null) {
            return;
        }
        this.mPreviousScrollState = this.mCurrentScrollState;
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView absListView, int i) {
        this.mScrollStateChangedRunnable.doScrollStateChange(absListView, i);
    }

    protected class ScrollStateRunnable implements Runnable {
        private int mNewState;

        protected ScrollStateRunnable() {
        }

        public void doScrollStateChange(AbsListView absListView, int i) {
            DayPickerView.this.mHandler.removeCallbacks(this);
            this.mNewState = i;
            DayPickerView.this.mHandler.postDelayed(this, 40L);
        }

        @Override // java.lang.Runnable
        public void run() {
            DayPickerView.this.mCurrentScrollState = this.mNewState;
            if (Log.isLoggable(DayPickerView.TAG, 3)) {
                Log.d(DayPickerView.TAG, "new scroll state: " + this.mNewState + " old state: " + DayPickerView.this.mPreviousScrollState);
            }
            if (this.mNewState == 0 && DayPickerView.this.mPreviousScrollState != 0) {
                if (DayPickerView.this.mPreviousScrollState != 1) {
                    DayPickerView.this.mPreviousScrollState = this.mNewState;
                    View childAt = DayPickerView.this.getChildAt(0);
                    int i = 0;
                    while (childAt != null && childAt.getBottom() <= 0) {
                        i++;
                        childAt = DayPickerView.this.getChildAt(i);
                    }
                    if (childAt == null) {
                        return;
                    }
                    boolean z = (DayPickerView.this.getFirstVisiblePosition() == 0 || DayPickerView.this.getLastVisiblePosition() == DayPickerView.this.getCount() - 1) ? false : true;
                    int top = childAt.getTop();
                    int bottom = childAt.getBottom();
                    int height = DayPickerView.this.getHeight() / 2;
                    if (!z || top >= -1) {
                        return;
                    }
                    if (bottom > height) {
                        DayPickerView.this.smoothScrollBy(top, 250);
                        return;
                    } else {
                        DayPickerView.this.smoothScrollBy(bottom, 250);
                        return;
                    }
                }
            }
            DayPickerView.this.mPreviousScrollState = this.mNewState;
        }
    }

    public int getMostVisiblePosition() {
        int firstVisiblePosition = getFirstVisiblePosition();
        int height = getHeight();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i < height) {
            View childAt = getChildAt(i2);
            if (childAt == null) {
                break;
            }
            int bottom = childAt.getBottom();
            int iMin = Math.min(bottom, height) - Math.max(0, childAt.getTop());
            if (iMin > i3) {
                i4 = i2;
                i3 = iMin;
            }
            i2++;
            i = bottom;
        }
        return firstVisiblePosition + i4;
    }

    @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.OnDateChangedListener
    public void onDateChanged() {
        goTo(this.mController.getSelectedDay(), false, true, true);
    }

    private SimpleMonthAdapter.CalendarDay findAccessibilityFocus() {
        SimpleMonthView simpleMonthView;
        SimpleMonthAdapter.CalendarDay accessibilityFocus;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if ((childAt instanceof SimpleMonthView) && (accessibilityFocus = (simpleMonthView = (SimpleMonthView) childAt).getAccessibilityFocus()) != null) {
                if (Build.VERSION.SDK_INT == 17) {
                    simpleMonthView.clearAccessibilityFocus();
                }
                return accessibilityFocus;
            }
        }
        return null;
    }

    private boolean restoreAccessibilityFocus(SimpleMonthAdapter.CalendarDay calendarDay) {
        if (calendarDay == null) {
            return false;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if ((childAt instanceof SimpleMonthView) && ((SimpleMonthView) childAt).restoreAccessibilityFocus(calendarDay)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.widget.ListView, android.widget.AbsListView
    protected void layoutChildren() {
        SimpleMonthAdapter.CalendarDay calendarDayFindAccessibilityFocus = findAccessibilityFocus();
        super.layoutChildren();
        if (this.mPerformingScroll) {
            this.mPerformingScroll = false;
        } else {
            restoreAccessibilityFocus(calendarDayFindAccessibilityFocus);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setItemCount(-1);
    }

    private String getMonthAndYearString(SimpleMonthAdapter.CalendarDay calendarDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendarDay.year, calendarDay.month, calendarDay.day);
        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat(Utils.NOMINATIVE_CASE).format(calendar.getTime())).append(" ").append(this.YEAR_FORMAT.format(calendar.getTime()));
        return sb.toString();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(4096);
        accessibilityNodeInfo.addAction(8192);
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        View childAt;
        if (i != 4096 && i != 8192) {
            return super.performAccessibilityAction(i, bundle);
        }
        int firstVisiblePosition = getFirstVisiblePosition();
        SimpleMonthAdapter.CalendarDay calendarDay = new SimpleMonthAdapter.CalendarDay((firstVisiblePosition / 12) + this.mController.getMinYear(), firstVisiblePosition % 12, 1);
        if (i == 4096) {
            calendarDay.month++;
            if (calendarDay.month == 12) {
                calendarDay.month = 0;
                calendarDay.year++;
            }
        } else if (i == 8192 && (childAt = getChildAt(0)) != null && childAt.getTop() >= -1) {
            calendarDay.month--;
            if (calendarDay.month == -1) {
                calendarDay.month = 11;
                calendarDay.year--;
            }
        }
        com.sonymobile.calendar.datetimepicker.Utils.tryAccessibilityAnnounce(this, getMonthAndYearString(calendarDay));
        goTo(calendarDay, true, false, true);
        this.mPerformingScroll = true;
        return true;
    }

    public void onCheckedChange(boolean z) {
        this.mAdapter.notifyDataSetChanged();
        onDateChanged();
    }
}
