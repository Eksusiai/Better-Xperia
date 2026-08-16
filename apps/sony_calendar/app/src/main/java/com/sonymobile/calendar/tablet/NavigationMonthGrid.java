package com.sonymobile.calendar.tablet;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class NavigationMonthGrid extends GridView implements AdapterView.OnItemClickListener {
    private static final float STROKE_WIDTH = 3.0f;
    private static final float WEEK_SELECTION_STROKE_WIDTH = 1.0f;
    private NavigationMonthGridAdapter adapter;
    private Time displayedMonth;
    private NavigationMonthGridListener gridListener;
    private boolean isR2L;
    private Time mCurrentTime;
    private Paint mPaint;

    public NavigationMonthGrid(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initGrid();
        initPaint();
        Time time = new SafeTime();
        this.mCurrentTime = time;
        time.setToNow();
    }

    public void setIsWeekView(boolean z) {
        this.adapter.setIsWeekView(z);
    }

    public boolean isWeekView() {
        return this.adapter.isWeekView();
    }

    public void setIsR2L(boolean z) {
        this.isR2L = z;
    }

    public void updateMonth(Time time) {
        if (isDisplayedMonth(time)) {
            this.adapter.setSelectedDay(time.monthDay);
            this.adapter.notifyDataSetChanged();
            return;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar.setTimeInMillis(time.toMillis(false));
        calendar.set(5, 1);
        int actualMaximum = calendar.getActualMaximum(5);
        int offset = getOffset(calendar.get(7));
        calendar.add(5, -1);
        int actualMaximum2 = calendar.getActualMaximum(5);
        this.displayedMonth = new SafeTime(time);
        updateToday(time.timezone);
        this.adapter.setOffset(offset);
        this.adapter.setDayCountInCurrentMonth(actualMaximum);
        this.adapter.setSelectedDay(time.monthDay);
        addDays(offset, actualMaximum, actualMaximum2);
    }

    public void updateToday(String str) {
        Time time = new SafeTime(str);
        time.set(System.currentTimeMillis());
        this.adapter.setToday(isDisplayedMonth(time) ? time.monthDay : -1);
    }

    public void setNavigationMonthGridListener(NavigationMonthGridListener navigationMonthGridListener) {
        this.gridListener = navigationMonthGridListener;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.adapter.isSelected(i)) {
            return;
        }
        this.adapter.setSelectedPosition(i);
        if (this.gridListener != null) {
            Time time = new SafeTime(this.displayedMonth);
            time.monthDay = this.adapter.getSelectedDay();
            this.gridListener.onDateSelected(time);
        }
        this.adapter.notifyDataSetChanged();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.adapter.isWeekView()) {
            drawSelectedWeekMarker(canvas);
        } else {
            drawSelectedDayMarker(canvas);
        }
        if (this.mCurrentTime.month == this.displayedMonth.month && this.mCurrentTime.year == this.displayedMonth.year) {
            drawTodayMarker(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawSelectedWeekMarker(Canvas canvas) {
        int height = getHeight() / this.adapter.getRowCount();
        int selectedRow = this.adapter.getSelectedRow() * height;
        this.mPaint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRoundRect(new RectF(1.0f, selectedRow + 1, getWidth() - 1, (selectedRow + height) - 1), 3.0f, 3.0f, this.mPaint);
    }

    private void drawSelectedDayMarker(Canvas canvas) {
        drawMarker(canvas, true, this.adapter.getSelectedItem());
    }

    public void drawTodayMarker(Canvas canvas) {
        drawMarker(canvas, false, this.adapter.getTodayItem());
    }

    public void drawMarker(Canvas canvas, boolean z, int i) {
        int i2 = this.isR2L ? 6 - (i % 7) : i % 7;
        int iFloor = (int) Math.floor(((double) i) / 7.0d);
        int width = getWidth() / 7;
        int height = getHeight() / this.adapter.getRowCount();
        int dimension = ((int) getResources().getDimension(R.dimen.mini_calendar_circle_radius)) - 2;
        int i3 = i2 * width;
        int i4 = (iFloor * height) + 1;
        if (z) {
            this.mPaint.setColor(UiUtils.getPrimaryColor(getContext()));
            this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(i3 + (width / 2), (height / 2) + i4, dimension, this.mPaint);
            this.mPaint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
            this.mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(new RectF(1.0f, i4 + 1, getWidth() - 1, (i4 + height) - 1), 3.0f, 3.0f, this.mPaint);
            return;
        }
        this.mPaint.setColor(UiUtils.getPrimaryColor(getContext()));
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(3.0f);
        this.mPaint.setAntiAlias(true);
        canvas.drawCircle(i3 + (width / 2), i4 + (height / 2), dimension, this.mPaint);
    }

    private boolean isDisplayedMonth(Time time) {
        return this.displayedMonth != null && time.year == this.displayedMonth.year && time.month == this.displayedMonth.month;
    }

    private int getOffset(int i) {
        int firstDayOfWeek = Utils.getFirstDayOfWeek(getContext());
        if (firstDayOfWeek == 0) {
            return i - 1;
        }
        if (firstDayOfWeek == 6) {
            return i % 7;
        }
        return (i + 5) % 7;
    }

    private void addDays(int i, int i2, int i3) {
        this.adapter.clear();
        for (int i4 = (i3 + 1) - i; i4 <= i3; i4++) {
            this.adapter.add(String.valueOf(i4));
        }
        for (int i5 = 1; i5 <= i2; i5++) {
            this.adapter.add(String.valueOf(i5));
        }
        int i6 = (i2 + i) % 7;
        if (i6 != 0) {
            int i7 = 7 - i6;
            for (int i8 = 1; i8 <= i7; i8++) {
                this.adapter.add(String.valueOf(i8));
            }
        }
    }

    private void initGrid() {
        setNumColumns(7);
        NavigationMonthGridAdapter navigationMonthGridAdapter = new NavigationMonthGridAdapter(getContext());
        this.adapter = navigationMonthGridAdapter;
        setAdapter((ListAdapter) navigationMonthGridAdapter);
        setOnItemClickListener(this);
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.mPaint.setStrokeWidth(1.0f);
    }
}
