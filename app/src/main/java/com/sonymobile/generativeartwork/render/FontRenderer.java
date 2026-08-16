package com.sonymobile.generativeartwork.render;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

/* JADX INFO: loaded from: classes2.dex */
public class FontRenderer {
    private static final char CHAR_SPACE = ' ';
    private static final int MAX_SEARCH_ITERS = 20;
    public static final float MIN_FONT_SIZE = 10.0f;
    public static final float STROKE_WIDTH = 1.0f;
    private static String TAG = "com.sonymobile.generativeartwork.render.FontRenderer";
    private static final Typeface mTypeface = Typeface.create("Roboto", 1);
    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap mPlane;
    private int mWidth = -1;
    private int mHeight = -1;
    private String mLetter1 = null;
    private String mLetter2 = null;
    private Path mPath1 = new Path();
    private Path mPath2 = new Path();
    private boolean mIsInitialized = false;
    RectF mBoundsPath1 = new RectF();
    RectF mBoundsPath2 = new RectF();
    Matrix m2DTransform = new Matrix();
    DisplayMetrics mDisplayMetrics = Resources.getSystem().getDisplayMetrics();

    public FontRenderer() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-1);
        this.mPaint.setTypeface(mTypeface);
        this.mPaint.setTextSize(120.0f);
        this.mPaint.setStrokeWidth(1.0f);
        this.mPaint.setAntiAlias(false);
        this.mPaint.setDither(false);
        this.mPaint.setHinting(0);
    }

    public void setCanvasSize(int i, int i2) {
        int i3 = (int) (i * 1.5f);
        this.mWidth = i3;
        this.mHeight = i2;
        this.mPlane = Bitmap.createBitmap(i3, i2, Bitmap.Config.ARGB_8888);
        this.mCanvas = new Canvas(this.mPlane);
        this.mPlane.eraseColor(0);
        this.mIsInitialized = true;
    }

    public Bitmap overlapOnBitmap(float f) {
        float f2 = (this.mHeight - 1.0f) - 2.0f;
        if (this.mIsInitialized) {
            this.mPlane.eraseColor(0);
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mPaint.setColor(-16711936);
            float f3 = this.mBoundsPath1.left - 0.5f;
            float f4 = (this.mBoundsPath2.left - 0.5f) - f;
            this.m2DTransform.reset();
            this.m2DTransform.postTranslate(0.5f, f2);
            this.mCanvas.save();
            this.mCanvas.setMatrix(this.m2DTransform);
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mPaint.setColor(-16711936);
            this.mPath1.offset(-f3, 0.0f);
            this.mPath2.offset(-f4, 0.0f);
            this.mCanvas.drawPath(this.mPath1, this.mPaint);
            this.mPaint.setColor(2130706687);
            this.mCanvas.drawPath(this.mPath2, this.mPaint);
            this.mCanvas.restore();
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setColor(-16777216);
            this.mCanvas.drawRect(0.0f, 0.0f, this.mWidth - 1, this.mHeight - 1, this.mPaint);
        }
        return this.mPlane;
    }

    public void prepareShapes(char c, char c2) {
        float fFindBestFontMatch;
        this.mLetter1 = String.valueOf(c);
        String strValueOf = String.valueOf(c2);
        this.mLetter2 = strValueOf;
        if (c == ' ' && c2 != ' ') {
            fFindBestFontMatch = findBestFontMatch(strValueOf);
        } else if (c != ' ' && c2 == ' ') {
            fFindBestFontMatch = findBestFontMatch(this.mLetter1);
        } else if (c == ' ' && c2 == ' ') {
            fFindBestFontMatch = 0.0f;
        } else {
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setTextSize(10.0f);
            this.mPaint.getTextPath(this.mLetter1, 0, 1, 0.0f, 0.0f, this.mPath1);
            this.mPath1.computeBounds(this.mBoundsPath1, true);
            this.mPaint.getTextPath(this.mLetter2, 0, 1, 0.0f, 0.0f, this.mPath2);
            this.mPath2.computeBounds(this.mBoundsPath2, true);
            if (this.mBoundsPath1.bottom - this.mBoundsPath1.top < this.mBoundsPath2.bottom - this.mBoundsPath2.top) {
                fFindBestFontMatch = findBestFontMatch(this.mLetter1);
            } else {
                fFindBestFontMatch = findBestFontMatch(this.mLetter2);
            }
        }
        this.mPaint.setTextSize(fFindBestFontMatch);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.getTextPath(this.mLetter1, 0, 1, 0.0f, 0.0f, this.mPath1);
        this.mPath1.computeBounds(this.mBoundsPath1, true);
        this.mPaint.getTextPath(this.mLetter2, 0, 1, 0.0f, 0.0f, this.mPath2);
        this.mPath2.computeBounds(this.mBoundsPath2, true);
    }

    public RectF getBoundsLetter(int i) {
        return i == 0 ? this.mBoundsPath1 : this.mBoundsPath2;
    }

    private float findBestFontMatch(String str) {
        float f = (this.mHeight - 2.0f) - 2.0f;
        float f2 = (this.mDisplayMetrics.scaledDensity * f) + 300.0f;
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setTextSize(f2);
        this.mPaint.getTextPath(str, 0, 1, 0.0f, 0.0f, this.mPath1);
        this.mPath1.computeBounds(this.mBoundsPath1, true);
        float f3 = this.mBoundsPath1.bottom - this.mBoundsPath1.top;
        this.mPaint.setTextSize(10.0f);
        this.mPaint.getTextPath(str, 0, 1, 0.0f, 0.0f, this.mPath1);
        this.mPath1.computeBounds(this.mBoundsPath1, true);
        float f4 = this.mBoundsPath1.bottom - this.mBoundsPath1.top;
        return (((f - f4) * (f2 - 10.0f)) / (f3 - f4)) + 10.0f;
    }
}
