package com.sonymobile.calendar.generativeartwork;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class LetterTileDrawable extends Drawable {
    private static final int ALPHA = 138;
    private static float sLetterToTileRatio;
    private static int sTileFontColor;
    private int mColor;
    private Character mLetter = null;
    private final Paint mPaint;
    private static final Paint sPaint = new Paint();
    private static final Rect sRect = new Rect();
    private static final char[] sFirstChar = new char[1];

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -1;
    }

    public LetterTileDrawable(Context context) {
        int color = context.getResources().getColor(R.color.letter_tile_default_color);
        sTileFontColor = context.getResources().getColor(R.color.letter_tile_font_color);
        sLetterToTileRatio = 0.67f;
        Paint paint = sPaint;
        paint.setTypeface(Typeface.create(context.getString(R.string.roboto_font_medium), 0));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.mPaint = paint2;
        paint2.setFilterBitmap(true);
        paint2.setDither(true);
        this.mColor = color;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (!isVisible() || bounds.isEmpty()) {
            return;
        }
        drawLetterTile(canvas);
    }

    private void drawLetterTile(Canvas canvas) {
        Paint paint = sPaint;
        paint.setColor(this.mColor);
        paint.setAlpha(this.mPaint.getAlpha());
        Rect bounds = getBounds();
        int iMin = Math.min(bounds.width(), bounds.height());
        canvas.drawRect(bounds, paint);
        char[] cArr = sFirstChar;
        cArr[0] = this.mLetter.charValue();
        paint.setTextSize(sLetterToTileRatio * 1.0f * iMin);
        Rect rect = sRect;
        paint.getTextBounds(cArr, 0, 1, rect);
        paint.setTypeface(Typeface.create("sans-serif", 0));
        paint.setColor(sTileFontColor);
        paint.setAlpha(ALPHA);
        canvas.drawText(cArr, 0, 1, bounds.centerX(), (bounds.centerY() + (bounds.height() * 0.0f)) - rect.exactCenterY(), paint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public LetterTileDrawable setLetter(Character ch) {
        this.mLetter = ch;
        return this;
    }

    public LetterTileDrawable setColor(int i) {
        this.mColor = i;
        return this;
    }
}
