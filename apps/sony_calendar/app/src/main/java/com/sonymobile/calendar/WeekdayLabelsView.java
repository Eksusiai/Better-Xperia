package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public class WeekdayLabelsView extends View {
    private static final int DAY_LABEL_SIZE = 12;
    private static final int PADDING = 2;
    private int mDateBoxGridCellWidth;
    private int[] mDayLabelColors;
    private int mDayLabelSize;
    private String[] mDayLabels;
    private Paint mPaint;
    private int padding;

    public WeekdayLabelsView(Context context) {
        super(context);
        initMeasures();
        initPaint();
    }

    public WeekdayLabelsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initMeasures();
        initPaint();
    }

    public WeekdayLabelsView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initMeasures();
        initPaint();
    }

    public WeekdayLabelsView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initMeasures();
        initPaint();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawDayLabels(canvas);
        super.onDraw(canvas);
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setTextAlign(Paint.Align.CENTER);
        this.mPaint.setTextSize(this.mDayLabelSize);
    }

    private void initMeasures() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this.padding = (int) (displayMetrics.density * 2.0f);
        this.mDayLabelSize = (int) TypedValue.applyDimension(1, 12.0f, displayMetrics);
    }

    public void setDateBoxGridCellWidth(int i) {
        this.mDateBoxGridCellWidth = i;
    }

    public void setDayLabelsColor(int[] iArr) {
        this.mDayLabelColors = (int[]) iArr.clone();
    }

    public void setDayLabels(String[] strArr) {
        this.mDayLabels = (String[]) strArr.clone();
    }

    private void drawDayLabels(Canvas canvas) {
        if (this.mDayLabels == null || this.mDayLabelColors == null) {
            return;
        }
        int width = canvas.getWidth();
        int i = this.mDateBoxGridCellWidth;
        int width2 = width - (i / 2);
        if (CalendarApplication.isR2L(getResources())) {
            width2 = canvas.getWidth() - width2;
            i = -i;
        }
        for (int i2 = 6; i2 >= 0; i2--) {
            drawDayLabel(canvas, this.mDayLabels[i2], width2, this.mDayLabelColors[i2]);
            width2 -= i;
        }
    }

    private void drawDayLabel(Canvas canvas, String str, int i, int i2) {
        this.mPaint.setColor(i2);
        canvas.drawText(str, i, this.mDayLabelSize + this.padding, this.mPaint);
    }
}
