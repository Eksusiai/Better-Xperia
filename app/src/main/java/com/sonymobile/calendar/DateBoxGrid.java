package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.common.primitives.Ints;
import com.sonyericsson.calendar.util.EventLoaderService;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class DateBoxGrid extends LinearLayout implements ICalendarColumnContainer, OnDateBoxSelectedListener {
    private static final String DAY_LABEL_AGGREGATED = "c";
    private static final String DAY_LABEL_FULL = "cccc";
    private static final int DAY_LABEL_SIZE = 12;
    private static final float GRID_LINE_WIDTH = 1.0f;
    private static final int PADDING = 2;
    private static final int PADDING_AND_LINE_SCALE = 2;
    private static final int WEEK_NUMBER_SIZE = 10;
    private DateBoxAdapter adapter;
    private boolean allowedToDraw;
    private GridView boxGrid;
    private int[] dayLabelColors;
    private int dayLabelSize;
    private String[] dayLabels;
    private CalendarEventNavigator eventNavigator;
    private FocusedViewNavigator focusedViewNavigator;
    private boolean isR2L;
    private boolean mIsPhoneLandscape;
    private boolean mIsTabletDevice;
    private int padding;
    private Paint paint;
    private DateBoxView selectedBox;
    private OnDateBoxSelectedListener selectionListener;
    private boolean showWeekNumbers;
    private TextPaint textPaint;
    private int weekNumberSize;
    private String[] weekNumbers;

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void blockRelayout() {
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void removeAddEventView() {
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public boolean setHourHeight(int i) {
        return false;
    }

    public DateBoxGrid(Context context, CalendarEventNavigator calendarEventNavigator, boolean z, FocusedViewNavigator focusedViewNavigator) {
        super(context);
        this.eventNavigator = calendarEventNavigator;
        this.focusedViewNavigator = focusedViewNavigator;
        this.isR2L = z;
        setGravity(80);
        this.mIsTabletDevice = Utils.isTabletDevice(getContext());
        this.mIsPhoneLandscape = UiUtils.isPhoneLandscape(getContext()) && !UiUtils.isSub320dpScreen(getContext());
        if (this.mIsTabletDevice) {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.month_view_background_color));
        } else {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_area_background));
        }
        initGrid();
        initPaint(z);
        setFocusable(false);
        setDescendantFocusability(262144);
        setWillNotDraw(false);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int boxHeight = (this.adapter.getBoxHeight() * this.adapter.getRowCount()) + getPaddingTop() + getPaddingBottom();
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(boxHeight, Ints.MAX_POWER_OF_TWO));
        setMeasuredDimension(size, boxHeight);
    }

    public void setOnDateBoxSelectedListener(OnDateBoxSelectedListener onDateBoxSelectedListener) {
        this.selectionListener = onDateBoxSelectedListener;
    }

    @Override // com.sonymobile.calendar.OnDateBoxSelectedListener
    public void onDateSelected(DateBoxView dateBoxView, boolean z) {
        DateBoxView dateBoxView2 = this.selectedBox;
        if (dateBoxView2 == dateBoxView) {
            return;
        }
        if (dateBoxView2 != null && !Utils.areDatesEqual(dateBoxView.getDate(), this.selectedBox.getDate())) {
            this.selectedBox.setIsSelected(false);
        }
        this.selectedBox = dateBoxView.isDisabled() ? null : dateBoxView;
        OnDateBoxSelectedListener onDateBoxSelectedListener = this.selectionListener;
        if (onDateBoxSelectedListener != null) {
            onDateBoxSelectedListener.onDateSelected(dateBoxView, z);
        }
    }

    public void invalidateGrid() {
        this.adapter.notifyDataSetChanged();
    }

    public void clearSelection() {
        DateBoxView dateBoxView = this.selectedBox;
        if (dateBoxView != null) {
            dateBoxView.setIsSelected(false);
            this.selectedBox = null;
        }
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void updateView(Time[] timeArr, boolean z) {
        boolean z2;
        if (timeArr.length > 7) {
            this.adapter.setEnabledMonth(timeArr[7]);
            z2 = false;
        } else {
            z2 = true;
        }
        this.adapter.clear();
        this.adapter.addAll(timeArr);
        updateLabels(timeArr, z2);
        updateWeekNumbers(timeArr[0]);
        updateMeasures();
        reloadEventsAsync(timeArr[0], timeArr[timeArr.length - 1], z);
        this.allowedToDraw = true;
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void reloadEvents() {
        DateBoxView dateBox = this.adapter.getDateBox(0);
        DateBoxAdapter dateBoxAdapter = this.adapter;
        DateBoxView dateBox2 = dateBoxAdapter.getDateBox(dateBoxAdapter.getCount() - 1);
        if (dateBox == null || dateBox2 == null) {
            return;
        }
        reloadEventsAsync(dateBox.getDate(), dateBox2.getDate(), true);
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void setViewPortSize(int i, int i2, boolean z) {
        int rowCount = this.adapter.getRowCount();
        if (rowCount == 0) {
            rowCount = 1;
        }
        setLayoutParams(new LinearLayout.LayoutParams(i, i2));
        int paddingTop = (i2 - getPaddingTop()) - getPaddingBottom();
        int paddingStart = (int) (((double) (((i - getPaddingStart()) - getPaddingEnd()) + 2)) / 7.0d);
        this.adapter.setDateBoxSize(paddingStart, paddingTop / rowCount);
        this.boxGrid.setColumnWidth(paddingStart);
    }

    public int getLabelHeight() {
        return getPaddingTop();
    }

    public int getRowHeight() {
        return this.adapter.getBoxHeight();
    }

    public int getRowCount() {
        return this.adapter.getRowCount();
    }

    public int getCellWidth() {
        return this.adapter.getBoxWidth();
    }

    public int getRowIndex(Time time) {
        return this.adapter.getRowIndex(time);
    }

    @Override // com.sonymobile.calendar.ICalendarColumnContainer
    public void updateFocusability(boolean z) {
        setDescendantFocusability(z ? 262144 : 393216);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.allowedToDraw) {
            if (!this.mIsTabletDevice && this.mIsPhoneLandscape) {
                drawLines(canvas);
            }
            if (!this.mIsPhoneLandscape) {
                drawDayLabels(canvas);
            }
            if (this.showWeekNumbers) {
                drawWeekNumbers(canvas);
            }
            drawEdges(canvas);
            super.onDraw(canvas);
            PermissionUtils.reportFullyDrawnIfPermitted((AppCompatActivity) getContext());
        }
    }

    private void drawLines(Canvas canvas) {
        int paddingTop = getPaddingTop();
        int width = getWidth();
        int rowCount = this.adapter.getRowCount();
        int boxHeight = this.adapter.getBoxHeight();
        for (int i = 0; i <= rowCount; i++) {
            float f = (i * boxHeight) + paddingTop;
            canvas.drawLine(0.0f, f, width, f, this.paint);
        }
    }

    private void updateLabels(Time[] timeArr, boolean z) {
        this.dayLabels = new String[7];
        this.dayLabelColors = new int[7];
        for (int i = 0; i < 7; i++) {
            this.dayLabels[i] = getLabelString(timeArr[i]);
            this.dayLabelColors[i] = getLabelColor(timeArr[i], z);
        }
    }

    private void updateWeekNumbers(Time time) {
        boolean z = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(GeneralPreferences.KEY_SHOW_WEEK_NUMBER, false);
        this.showWeekNumbers = z;
        if (z) {
            int rowCount = this.adapter.getRowCount();
            this.weekNumbers = new String[rowCount];
            Time time2 = new SafeTime(time);
            for (int i = 0; i < rowCount; i++) {
                this.weekNumbers[i] = String.valueOf(Utils.getWeekNumberOfDay(getContext(), time2));
                time2.monthDay += 7;
                time2.normalize(false);
            }
        }
    }

    private String getLabelString(Time time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(!UiUtils.isSub320dpScreen(getContext()) && (this.mIsTabletDevice || UiUtils.isLandscape(getContext())) ? "cccc" : "c", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Utils.getTimeZone(getContext(), null)));
        if (!this.mIsPhoneLandscape && UiUtils.shouldUseOneLetterForDay(getResources())) {
            return simpleDateFormat.format(Long.valueOf(time.toMillis(false))).toUpperCase(Locale.getDefault()).substring(0, 1);
        }
        return simpleDateFormat.format(Long.valueOf(time.toMillis(false))).toUpperCase(Locale.getDefault());
    }

    private void updateMeasures() {
        int i;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float f = displayMetrics.density;
        this.dayLabelSize = (int) TypedValue.applyDimension(2, 12.0f, displayMetrics);
        this.weekNumberSize = (int) getResources().getDimension(R.dimen.day_label_week_number);
        int i2 = (int) (f * 2.0f);
        this.padding = i2;
        if (this.isR2L) {
            if (this.mIsTabletDevice) {
                setTabletPadding();
                return;
            } else {
                setPadding(0, this.dayLabelSize + (i2 * 2), this.showWeekNumbers ? (int) getResources().getDimension(R.dimen.calendar_left_panel_width) : 0, 2);
                return;
            }
        }
        if (this.mIsTabletDevice) {
            setTabletPadding();
            return;
        }
        int dimension = this.showWeekNumbers ? (int) getResources().getDimension(R.dimen.calendar_left_panel_width) : 0;
        if (this.mIsPhoneLandscape) {
            int i3 = this.padding;
            i = (i3 * 2) - i3;
        } else {
            i = (this.padding * 2) + this.dayLabelSize;
        }
        setPadding(dimension, i, 0, 2);
    }

    private void setTabletPadding() {
        int dimension = (int) getResources().getDimension(R.dimen.month_view_grid_padding);
        setPadding(dimension, (int) getResources().getDimension(R.dimen.month_view_top_padding), dimension, 2);
    }

    private void initGrid() {
        GridView gridView = new GridView(getContext());
        this.boxGrid = gridView;
        gridView.setFocusable(true);
        this.boxGrid.setDescendantFocusability(262144);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        this.boxGrid.setNumColumns(7);
        DateBoxAdapter dateBoxAdapter = new DateBoxAdapter(getContext(), this, this.eventNavigator, this.focusedViewNavigator);
        this.adapter = dateBoxAdapter;
        this.boxGrid.setAdapter((ListAdapter) dateBoxAdapter);
        addView(this.boxGrid);
    }

    private void initPaint(boolean z) {
        TextPaint textPaint = new TextPaint();
        this.textPaint = textPaint;
        textPaint.setTextAlign(z ? Paint.Align.LEFT : Paint.Align.RIGHT);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTypeface(Typeface.create(getResources().getString(R.string.roboto_font), 0));
        Paint paint = new Paint();
        this.paint = paint;
        paint.setStrokeWidth(1.0f);
        this.paint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
    }

    private int getLabelColor(Time time, boolean z) {
        if ((z && FreeDayService.getInstance().isFreeDay(time.year, time.month, time.monthDay)) || FreeDayService.getInstance().isWeekend(time.weekDay)) {
            return ContextCompat.getColor(getContext(), R.color.free_day_number);
        }
        return ContextCompat.getColor(getContext(), R.color.day_number_color);
    }

    private void drawEdges(Canvas canvas) {
        float dimension = getResources().getDimension(R.dimen.month_view_grid_padding);
        float paddingLeft = this.isR2L ? getPaddingLeft() - 1.0f : canvas.getWidth() - dimension;
        canvas.drawLine(paddingLeft, getPaddingTop(), paddingLeft, canvas.getHeight(), this.paint);
        if (this.mIsTabletDevice) {
            canvas.drawLine(dimension, canvas.getHeight() - 1.0f, canvas.getWidth() - dimension, canvas.getHeight() - 1.0f, this.paint);
        }
    }

    private void drawDayLabels(Canvas canvas) {
        int width;
        int dimension;
        int boxWidth = this.adapter.getBoxWidth();
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        if (this.mIsTabletDevice) {
            this.textPaint.setTextSize(getResources().getDimension(R.dimen.day_label_week_number));
            width = ((canvas.getWidth() - (boxWidth / 2)) - ((int) getResources().getDimension(R.dimen.month_view_grid_padding))) - 1;
        } else {
            this.textPaint.setTextSize(this.dayLabelSize);
            width = canvas.getWidth() - (boxWidth / 2);
        }
        if (this.isR2L) {
            if (this.mIsTabletDevice) {
                dimension = (boxWidth / 2) + ((int) getResources().getDimension(R.dimen.month_view_grid_padding));
            } else {
                dimension = (int) getResources().getDimension(R.dimen.month_view_grid_padding);
            }
            width = dimension + 1;
            boxWidth = -boxWidth;
        }
        for (int i = 6; i >= 0; i--) {
            drawDayLabel(canvas, this.dayLabels[i], width, this.dayLabelColors[i]);
            width -= boxWidth;
        }
    }

    public boolean isShowWeekNumbers() {
        return this.showWeekNumbers;
    }

    private void drawWeekNumbers(Canvas canvas) {
        int i;
        this.textPaint.setTextAlign(this.isR2L ? Paint.Align.LEFT : Paint.Align.RIGHT);
        this.textPaint.setColor(ContextCompat.getColor(getContext(), R.color.week_number_color));
        this.textPaint.setTextSize(this.weekNumberSize);
        int width = this.isR2L ? canvas.getWidth() - getPaddingStart() : getPaddingStart();
        int paddingTop = this.mIsTabletDevice ? getPaddingTop() : (int) getResources().getDimension(R.dimen.month_view_grid_padding);
        if (this.mIsPhoneLandscape) {
            i = this.padding * 4;
        } else {
            i = (this.padding * 5) + this.weekNumberSize;
        }
        int i2 = paddingTop + i;
        int rowCount = this.adapter.getRowCount();
        int height = (canvas.getHeight() - getPaddingTop()) / rowCount;
        for (int i3 = 0; i3 < rowCount; i3++) {
            drawWeekNumber(canvas, this.weekNumbers[i3], width, i2);
            i2 += height;
        }
    }

    private void drawDayLabel(Canvas canvas, String str, int i, int i2) {
        this.textPaint.setColor(i2);
        canvas.drawText(str, i, this.mIsTabletDevice ? getResources().getDimension(R.dimen.day_label_y_axis) : this.dayLabelSize + this.padding, this.textPaint);
    }

    private void drawWeekNumber(Canvas canvas, String str, int i, int i2) {
        canvas.drawText(str, i, i2, this.textPaint);
    }

    private void reloadEventsAsync(Time time, Time time2, boolean z) {
        EventLoaderService.getInstance().requestLoad(getContext(), time, time2, new EventLoaderResultHandler(), z);
    }

    public int getPositionOfSelection() {
        Time time = new SafeTime();
        time.set(Utils.getDisplayTime());
        DateBoxAdapter dateBoxAdapter = this.adapter;
        if (dateBoxAdapter == null || dateBoxAdapter.getEnabledMonth() == null) {
            return 0;
        }
        int i = this.adapter.getEnabledMonth().month;
        int rowIndex = this.adapter.getRowIndex(time);
        int paddingTop = rowIndex == 0 ? 0 : getPaddingTop();
        if (i == time.month) {
            return (rowIndex * this.adapter.getBoxHeight()) + paddingTop;
        }
        return 0;
    }

    private class EventLoaderResultHandler implements IAsyncServiceResultHandler {
        private EventLoaderResultHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            DateBoxGrid.this.adapter.notifyDataSetChanged();
        }
    }

    public String[] getDayLabels() {
        if (this.dayLabels == null) {
            this.dayLabels = new String[7];
        }
        return (String[]) this.dayLabels.clone();
    }

    public int[] getDayLabelsColor() {
        return (int[]) this.dayLabelColors.clone();
    }
}
