package com.sonymobile.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.Time;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.DayOfMonthCursor;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonymobile.calendar.utils.RecycledBitmapCache;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class YearMonthGrid {
    private static final float MONTHBOX_MARGIN = 0.004f;
    private static final float MONTH_DAY_TEXT_SIZE = 0.11f;
    private static final float MONTH_DAY_TEXT_SIZE_MULTIWINDOW_SCALE = 0.81f;
    private static final float TODAY_MARKER_PHONE_X_MOVE = 2.0f;
    private static final float TODAY_MARKER_TABLET_X_MOVE = 4.0f;
    private static final float YEAR_FRAGMENT_LANDSCAPE_MULTIWINDOW_SCALE = 0.5f;
    private static final float YEAR_FRAGMENT_LANDSCAPE_SCALE = 0.4f;
    private static final float YEAR_FRAGMENT_PORTRAIT_SCALE = 0.45454544f;
    private DayOfMonthCursor cursor;
    private Paint fillPaint;
    private int gridBackgroundColor;
    private Bitmap gridBitmap;
    private int gridForegroundColor;
    private final boolean isInMultiwindow;
    private final RecycledBitmapCache mRecycledBitmapCache;
    private float monthBoxMargin;
    private float monthDayTextSize;
    private final float multiwindowRatio;
    private Paint strokePaint;
    private int numberOfRows = 0;
    private float cellHeight = 0.0f;
    private float cellWidth = 0.0f;

    public void draw(Canvas canvas, int i, int i2) {
        canvas.drawBitmap(this.gridBitmap, i, i2, this.fillPaint);
    }

    public YearMonthGrid(View view, int i, int i2, DayOfMonthCursor dayOfMonthCursor, Time time, Context context, RecycledBitmapCache recycledBitmapCache, boolean z, boolean z2) {
        this.cursor = dayOfMonthCursor;
        this.isInMultiwindow = z2;
        this.multiwindowRatio = (i * 1.0f) / i2;
        this.mRecycledBitmapCache = recycledBitmapCache;
        if (i <= 0 || i2 <= 0) {
            return;
        }
        this.gridBitmap = recycledBitmapCache.getBitmap(i, i2);
        initPaint(view);
        fitGridToSize(i, i2, view);
        drawGrid(this.gridBitmap, time, context, z);
    }

    public void recycle() {
        this.mRecycledBitmapCache.recycleBitmaps(this.gridBitmap);
    }

    private void fitGridToSize(int i, int i2, View view) {
        float f = i2;
        this.monthDayTextSize = MONTH_DAY_TEXT_SIZE * f;
        if (isMultiwindowScale()) {
            this.monthDayTextSize *= MONTH_DAY_TEXT_SIZE_MULTIWINDOW_SCALE;
        }
        float f2 = i;
        this.monthBoxMargin = MONTHBOX_MARGIN * f2;
        int numberOfRowsForMonth = getNumberOfRowsForMonth(this.cursor);
        this.numberOfRows = numberOfRowsForMonth;
        this.cellHeight = f / numberOfRowsForMonth;
        this.cellWidth = f2 / 7.0f;
    }

    private boolean isMultiwindowScale() {
        return this.isInMultiwindow && this.multiwindowRatio < 1.0f;
    }

    private int getNumberOfRowsForMonth(DayOfMonthCursor dayOfMonthCursor) {
        return (dayOfMonthCursor.isWithinCurrentMonth(5, 0) || dayOfMonthCursor.isWithinCurrentMonth(5, 6)) ? 6 : 5;
    }

    private void initPaint(View view) {
        Paint paint = new Paint();
        this.fillPaint = paint;
        paint.setStyle(Paint.Style.FILL);
        this.fillPaint.setAntiAlias(true);
        this.fillPaint.setTypeface(Typeface.create("sans-serif-light", 0));
        Paint paint2 = new Paint();
        this.strokePaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.strokePaint.setAntiAlias(true);
        this.strokePaint.setTypeface(Typeface.create("sans-serif-light", 0));
        TypedArray typedArrayObtainStyledAttributes = view.getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
        this.gridForegroundColor = typedArrayObtainStyledAttributes.getColor(0, R.color.calendar_foreground);
        this.gridBackgroundColor = ContextCompat.getColor(view.getContext(), R.color.calendar_grid_area_background);
        typedArrayObtainStyledAttributes.recycle();
    }

    private void drawGrid(Bitmap bitmap, Time time, Context context, boolean z) {
        int i;
        int i2;
        int i3;
        if (z) {
            i3 = -1;
            i = -1;
            i2 = 6;
        } else {
            i = 1;
            i2 = 0;
            i3 = 7;
        }
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(this.gridBackgroundColor);
        for (int i4 = 0; i4 < this.numberOfRows; i4++) {
            for (int i5 = i2; i5 != i3; i5 += i) {
                drawDateBox(canvas, i4, i5, time, context, z);
            }
        }
    }

    private void drawDateBox(Canvas canvas, int i, int i2, Time time, Context context, boolean z) {
        float f = i2 * this.cellWidth;
        float f2 = i * this.cellHeight;
        float f3 = this.monthBoxMargin;
        RectF rectF = new RectF(f + f3, f2 + f3, (f + this.cellWidth) - f3, (f2 + this.cellHeight) - f3);
        int dayAt = this.cursor.getDayAt(i, i2);
        if (this.cursor.isWithinCurrentMonth(i, i2)) {
            if (dayAt == time.monthDay && this.cursor.getYear() == time.year && this.cursor.getMonth() == time.month) {
                drawTodayMonthBox(canvas, rectF, i, i2, dayAt, context, z);
            } else {
                drawCurrentMonthBox(canvas, rectF, i, i2, dayAt, context, z);
            }
        }
    }

    private void drawCurrentMonthBox(Canvas canvas, RectF rectF, int i, int i2, int i3, Context context, boolean z) {
        FreeDayService freeDayService = FreeDayService.getInstance();
        drawMonthDayNumber(canvas, i3, rectF, freeDayService.isFreeDay(this.cursor.getYear(), this.cursor.getMonth(), i3) ? freeDayService.getHolidayColor(context) : this.gridForegroundColor, z);
    }

    private void drawTodayMonthBox(Canvas canvas, RectF rectF, int i, int i2, int i3, Context context, boolean z) {
        drawTodayDayMarker(canvas, context, rectF);
        drawMonthDayNumber(canvas, i3, rectF, -1, z);
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0039  */
    /* JADX WARN: Code duplicated, block: B:13:0x004b  */
    private void drawTodayDayMarker(Canvas canvas, Context context, RectF rectF) {
        float f2;
        this.strokePaint.setColor(UiUtils.getAccentColor(context));
        this.strokePaint.setStyle(Paint.Style.FILL);
        float fWidth = rectF.width();
        float f3 = 0.0f;
        if (UiUtils.isLandscape(context)) {
            if (isMultiwindowScale()) {
                f2 = fWidth * YEAR_FRAGMENT_LANDSCAPE_MULTIWINDOW_SCALE;
                f3 = (-1.0f) / (this.multiwindowRatio * YEAR_FRAGMENT_LANDSCAPE_MULTIWINDOW_SCALE);
            } else {
                f2 = fWidth * YEAR_FRAGMENT_LANDSCAPE_SCALE;
            }
        } else {
            f2 = fWidth * YEAR_FRAGMENT_PORTRAIT_SCALE;
        }
        if (Utils.isTabletDevice(context)) {
            canvas.drawCircle(rectF.centerX() + TODAY_MARKER_TABLET_X_MOVE + f3, rectF.centerY(), f2, this.strokePaint);
        } else {
            canvas.drawCircle(rectF.centerX() + TODAY_MARKER_PHONE_X_MOVE, rectF.centerY(), f2, this.strokePaint);
        }
    }

    private void drawMonthDayNumber(Canvas canvas, int i, RectF rectF, int i2, boolean z) {
        this.fillPaint.setColor(i2);
        this.fillPaint.setTextSize(this.monthDayTextSize);
        String strValueOf = String.valueOf(i);
        Rect rect = new Rect();
        this.fillPaint.getTextBounds(strValueOf, 0, strValueOf.length(), rect);
        canvas.drawText(strValueOf, rect.left + (rectF.centerX() - rect.centerX()), rect.bottom + (rectF.centerY() - rect.centerY()), this.fillPaint);
    }
}
