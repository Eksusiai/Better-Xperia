package com.sonymobile.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.util.Pair;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.os.BuildCompat;
import com.sonyericsson.calendar.util.DayOfMonthCursor;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.utils.RecycledBitmapCache;
import com.sonymobile.calendar.utils.UiUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/* JADX INFO: loaded from: classes2.dex */
public class YearGrid {
    private static final float MONTH_HORIZONTAL_MARGIN = 0.02f;
    private static final float MONTH_LABEL_HEIGHT = 0.04f;
    private static final float MONTH_LABEL_TEXT_SIZE = 0.03f;
    private static final float MONTH_MARGIN_BOTTOM = 0.05f;
    private static final float MONTH_MARGIN_TOP = 0.005f;
    private static final float YEAR_PADDING = 0.015f;
    private boolean animatingFadeIn = false;
    private int columnCount;
    private Time drawTime;
    private int fadeInAlpha;
    private SimpleDateFormat format;
    private Bitmap gridBitmap;
    private Rect labelBounds;
    private int mGridHeight;
    private int mGridWidth;
    private final RecycledBitmapCache mRecycledBitmapCache;
    private int monthHeight;
    private float monthHorizontalMargin;
    private float monthLabelHeight;
    private float monthLabelTextSize;
    private float monthMarginBottom;
    private float monthMarginTop;
    private int monthWidth;
    private Bitmap newGridBitmap;
    private Paint paint;
    private int rowCount;
    private int selectedMonth;
    private Paint selectionPaint;
    private RectF selectionRect;
    private float yearPadding;

    public Time getDrawTime() {
        return this.drawTime;
    }

    public void draw(Canvas canvas) {
        if (!this.gridBitmap.isRecycled()) {
            canvas.drawBitmap(this.gridBitmap, 0.0f, 0.0f, this.paint);
        }
        drawSelectionMarker(canvas);
        if (!this.animatingFadeIn || this.newGridBitmap.isRecycled()) {
            return;
        }
        this.paint.setAlpha(this.fadeInAlpha);
        canvas.drawBitmap(this.newGridBitmap, 0.0f, 0.0f, this.paint);
        this.paint.setAlpha(255);
    }

    public YearGrid(View view, int i, int i2, Time time, YearFragment yearFragment, RecycledBitmapCache recycledBitmapCache, boolean z) {
        Time time2 = new SafeTime();
        this.drawTime = time2;
        time2.set(time.toMillis(false));
        this.mGridHeight = i2;
        this.mGridWidth = i;
        this.mRecycledBitmapCache = recycledBitmapCache;
        this.gridBitmap = recycledBitmapCache.getBitmap(i, i2, this.drawTime.year);
        fitGridToSize(i, i2, view);
        initDrawResources(view, z);
        fitTextToWidth();
        if (!FreeDayService.getInstance().isDataLoaded()) {
            loadFreeDays(view, this.drawTime, yearFragment, z);
        }
        drawMonthGrids(this.gridBitmap, view, this.drawTime, yearFragment, z);
    }

    public int getMonthAt(float f, float f2, boolean z) {
        float f3 = f - this.yearPadding;
        if (f3 < 0.0f) {
            return -1;
        }
        int iFloor = (int) Math.floor(f3 / ((this.monthHorizontalMargin * 2.0f) + this.monthWidth));
        int iFloor2 = (int) Math.floor(f2 / (((this.monthMarginTop + this.monthLabelHeight) + this.monthHeight) + this.monthMarginBottom));
        if (z) {
            iFloor = (this.columnCount - 1) - iFloor;
        }
        return (iFloor2 * this.columnCount) + iFloor;
    }

    public void recycle() {
        this.mRecycledBitmapCache.recycleBitmaps(new Pair<>(Integer.valueOf(this.drawTime.year), this.gridBitmap), new Pair<>(Integer.valueOf(this.drawTime.year), this.newGridBitmap));
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public void setSelection(int i, boolean z) {
        this.selectedMonth = i;
        if (i == -1) {
            this.selectionRect = null;
        } else {
            setSelectionRectForMonth(i, z);
        }
    }

    public int getSelection() {
        return this.selectedMonth;
    }

    private void initDrawResources(View view, boolean z) {
        Context context = view.getContext();
        this.labelBounds = new Rect();
        this.paint = new Paint();
        setPaintDefaultColor(context);
        this.paint.setAntiAlias(true);
        this.paint.setTextSize(this.monthLabelTextSize);
        this.paint.setTextAlign(z ? Paint.Align.LEFT : Paint.Align.RIGHT);
        Paint paint = new Paint(this.paint);
        this.selectionPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.selectionPaint.setStrokeWidth(context.getResources().getDimensionPixelSize(R.dimen.focused_border_width));
        this.selectionPaint.setColor(ContextCompat.getColor(context, R.color.focused_border_color));
    }

    private void setPaintDefaultColor(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
        this.paint.setColor(typedArrayObtainStyledAttributes.getColor(0, ContextCompat.getColor(context, R.color.calendar_foreground)));
        typedArrayObtainStyledAttributes.recycle();
    }

    private void fitGridToSize(int i, int i2, View view) {
        float f = i;
        this.yearPadding = YEAR_PADDING * f;
        this.monthHorizontalMargin = MONTH_HORIZONTAL_MARGIN * f;
        float f2 = i2;
        this.monthMarginTop = MONTH_MARGIN_TOP * f2;
        this.monthMarginBottom = MONTH_MARGIN_BOTTOM * f2;
        this.monthLabelHeight = MONTH_LABEL_HEIGHT * f2;
        this.monthLabelTextSize = MONTH_LABEL_TEXT_SIZE * f2;
        if (view.getResources().getConfiguration().orientation == 1) {
            this.rowCount = 4;
            this.columnCount = 3;
        } else {
            this.rowCount = 3;
            this.columnCount = 4;
        }
        float f3 = this.yearPadding;
        int i3 = this.columnCount;
        this.monthWidth = (int) (((f - (f3 * 2.0f)) - ((i3 * 2) * this.monthHorizontalMargin)) / i3);
        int i4 = this.rowCount;
        float f4 = this.monthLabelHeight + this.monthMarginTop;
        float f5 = this.monthMarginBottom;
        this.monthHeight = (int) ((((f2 - (f3 * 2.0f)) - (i4 * (f4 + f5))) + f5) / i4);
    }

    private void fitTextToWidth() {
        this.format = new SimpleDateFormat(Utils.NOMINATIVE_CASE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        for (int i = 0; i < 12; i++) {
            calendar.set(2, i);
            if (this.paint.measureText(this.format.format(calendar.getTime()).toUpperCase()) > this.monthWidth) {
                this.format = new SimpleDateFormat(Utils.NOMINATIVE_CASE_SHORT);
                return;
            }
        }
    }

    public void drawMonthGrids(Bitmap bitmap, View view, Time time, YearFragment yearFragment, boolean z) {
        Canvas canvas = new Canvas(bitmap);
        Time time2 = new SafeTime(time);
        Time time3 = new SafeTime(time2);
        time3.set(System.currentTimeMillis());
        int firstDayOfWeek = getFirstDayOfWeek(yearFragment.getActivity());
        boolean zIsInMultiwindow = isInMultiwindow(yearFragment.getActivity());
        time2.month = 0;
        while (time2.month < 12) {
            drawMonthGrid(view, canvas, this.monthWidth, this.monthHeight, time2, time3, time, yearFragment, firstDayOfWeek, z, zIsInMultiwindow);
            time2.month++;
            time3 = time3;
        }
        view.invalidate();
    }

    private void drawMonthGrid(View view, Canvas canvas, int i, int i2, Time time, Time time2, Time time3, YearFragment yearFragment, int i3, boolean z, boolean z2) {
        YearMonthGrid yearMonthGrid = new YearMonthGrid(view, i, i2, new DayOfMonthCursor(time.year, time.month, time.monthDay, i3, z), time2, yearFragment.getActivity(), this.mRecycledBitmapCache, z, z2);
        int i4 = time.month % this.columnCount;
        int i5 = time.month;
        int i6 = this.columnCount;
        int i7 = i5 / i6;
        if (z) {
            i4 = (i6 - 1) - i4;
        }
        float f = this.yearPadding;
        float f2 = this.monthHorizontalMargin;
        int i8 = (int) (f + f2 + (i4 * (i + (f2 * 2.0f))));
        float f3 = this.monthMarginTop;
        float f4 = this.monthLabelHeight;
        int i9 = (int) ((i7 * (i2 + f3 + f4 + this.monthMarginBottom)) + f);
        yearMonthGrid.draw(canvas, i8, (int) (i9 + f4 + f3));
        drawMonthLabel(yearFragment.getActivity(), time3, canvas, time.month, i8, i9, i, z);
        yearMonthGrid.recycle();
    }

    private void drawMonthLabel(Context context, Time time, Canvas canvas, int i, int i2, int i3, int i4, boolean z) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        calendar.set(2, i);
        String upperCase = this.format.format(calendar.getTime()).toUpperCase();
        this.paint.getTextBounds(upperCase, 0, upperCase.length(), this.labelBounds);
        Time currentTime = Utils.getCurrentTime(context);
        if (i == currentTime.month && time.year == currentTime.year) {
            this.paint.setColor(UiUtils.getAccentColor(context));
        }
        if (!z) {
            i2 += i4;
        }
        canvas.drawText(upperCase, i2, i3 + this.monthLabelHeight, this.paint);
        if (i == currentTime.month && time.year == currentTime.year) {
            setPaintDefaultColor(context);
        }
    }

    private void drawSelectionMarker(Canvas canvas) {
        RectF rectF = this.selectionRect;
        if (rectF == null) {
            return;
        }
        canvas.drawRect(rectF, this.selectionPaint);
    }

    private void setSelectionRectForMonth(int i, boolean z) {
        int i2;
        if (z) {
            int i3 = this.columnCount;
            i2 = (i3 - 1) - (i % i3);
        } else {
            i2 = i % this.columnCount;
        }
        int iFloor = (int) Math.floor(i / this.columnCount);
        float f = this.monthHeight + this.monthMarginTop + this.monthMarginBottom + this.monthLabelHeight;
        float f2 = this.monthWidth + (this.monthHorizontalMargin * 2.0f);
        float f3 = this.yearPadding;
        float f4 = (i2 * f2) + f3;
        float f5 = f3 + (iFloor * f);
        this.selectionRect = new RectF(f4, f5, f2 + f4, ((f + f5) - this.monthMarginBottom) + this.monthMarginTop);
    }

    private int getFirstDayOfWeek(Context context) {
        int firstDayOfWeek = Utils.getFirstDayOfWeek(context);
        if (firstDayOfWeek != 0) {
            return firstDayOfWeek != 6 ? 2 : 7;
        }
        return 1;
    }

    private void loadFreeDays(View view, Time time, YearFragment yearFragment, boolean z) {
        FreeDayService.getInstance().requestLoad(view.getContext(), new FreeDayServiceResultHandler(view, time, yearFragment, z), 0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void redrawWithFadeAnimation(View view, Time time, YearFragment yearFragment, boolean z) {
        this.mRecycledBitmapCache.recycleBitmaps(new Pair<>(Integer.valueOf(this.drawTime.year), this.newGridBitmap));
        Bitmap bitmap = this.mRecycledBitmapCache.getBitmap(this.mGridWidth, this.mGridHeight, this.drawTime.year);
        this.newGridBitmap = bitmap;
        drawMonthGrids(bitmap, view, time, yearFragment, z);
        animateFadeIn(view);
    }

    private void animateFadeIn(final View view) {
        this.animatingFadeIn = true;
        this.fadeInAlpha = 0;
        new CountDownTimer(200L, 10L) { // from class: com.sonymobile.calendar.YearGrid.1
            @Override // android.os.CountDownTimer
            public void onFinish() {
                YearGrid.this.animatingFadeIn = false;
                YearGrid.this.mRecycledBitmapCache.recycleBitmaps(YearGrid.this.gridBitmap);
                YearGrid yearGrid = YearGrid.this;
                yearGrid.gridBitmap = yearGrid.newGridBitmap;
                view.invalidate();
            }

            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                YearGrid.this.fadeInAlpha = (int) (((200 - j) / 200.0f) * 255.0f);
                view.invalidate();
            }
        }.start();
    }

    private boolean isInMultiwindow(Activity activity) {
        return BuildCompat.isAtLeastN() && activity.isInMultiWindowMode();
    }

    private class FreeDayServiceResultHandler implements IAsyncServiceResultHandler {
        Time dateToDisplay;
        boolean isR2L;
        View parentView;
        YearFragment yearFragment;

        public FreeDayServiceResultHandler(View view, Time time, YearFragment yearFragment, boolean z) {
            this.parentView = view;
            this.dateToDisplay = new SafeTime(time);
            this.yearFragment = yearFragment;
            this.isR2L = z;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() == 0) {
                YearGrid.this.redrawWithFadeAnimation(this.parentView, this.dateToDisplay, this.yearFragment, this.isR2L);
            }
        }
    }
}
