package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class DayOfMonthDrawable extends Drawable {
    boolean isRTL;
    private String mDayOfMonth = "1";
    private final Paint mPaint;
    private final float mTextSize;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public DayOfMonthDrawable(Context context) {
        float dimension = context.getResources().getDimension(R.dimen.today_icon_text_size);
        this.mTextSize = dimension;
        Paint paint = new Paint();
        this.mPaint = paint;
        this.isRTL = CalendarApplication.isR2L(context.getResources());
        paint.setColor(UiUtils.getPrimaryColor(context));
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(dimension);
        paint.setTextAlign(this.isRTL ? Paint.Align.LEFT : Paint.Align.RIGHT);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        float f;
        Rect bounds = getBounds();
        if (this.isRTL) {
            f = bounds.left + (this.mTextSize / 5.0f);
        } else {
            f = bounds.right - (this.mTextSize / 5.0f);
        }
        canvas.drawText(this.mDayOfMonth, f, bounds.top + this.mTextSize, this.mPaint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    public void setDayOfMonth(int i) {
        this.mDayOfMonth = Integer.toString(i);
        invalidateSelf();
    }
}
