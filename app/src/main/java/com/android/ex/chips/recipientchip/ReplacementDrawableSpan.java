package com.android.ex.chips.recipientchip;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

/* JADX INFO: loaded from: classes.dex */
public class ReplacementDrawableSpan extends ReplacementSpan {
    protected static final Paint sWorkPaint = new Paint();
    protected Drawable mDrawable;
    private float mExtraMargin;

    public ReplacementDrawableSpan(Drawable drawable) {
        this.mDrawable = drawable;
    }

    public void setExtraMargin(float f) {
        this.mExtraMargin = f;
    }

    private void setupFontMetrics(Paint.FontMetricsInt fontMetricsInt, Paint paint) {
        Paint paint2 = sWorkPaint;
        paint2.set(paint);
        if (fontMetricsInt != null) {
            paint2.getFontMetricsInt(fontMetricsInt);
            Rect bounds = getBounds();
            int i = fontMetricsInt.descent - fontMetricsInt.ascent;
            int i2 = ((int) this.mExtraMargin) / 2;
            fontMetricsInt.ascent = Math.min(fontMetricsInt.top, fontMetricsInt.top + ((i - bounds.bottom) / 2)) - i2;
            fontMetricsInt.descent = Math.max(fontMetricsInt.bottom, fontMetricsInt.bottom + ((bounds.bottom - i) / 2)) + i2;
            fontMetricsInt.top = fontMetricsInt.ascent;
            fontMetricsInt.bottom = fontMetricsInt.descent;
        }
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        setupFontMetrics(fontMetricsInt, paint);
        return getBounds().right;
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        canvas.save();
        canvas.translate(f, ((i5 - this.mDrawable.getBounds().bottom) + i3) / 2);
        this.mDrawable.draw(canvas);
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Rect getBounds() {
        return this.mDrawable.getBounds();
    }
}
