package com.sonymobile.calendar.birthday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

/* JADX INFO: loaded from: classes2.dex */
public class RoundedImageView extends AppCompatImageView {
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Drawable mDrawable;
    private int mHeight;
    private Paint mPaint;
    private RectF mRectF;
    private boolean mStateChanged;
    private int mWidth;

    public RoundedImageView(Context context) {
        super(context);
        this.mStateChanged = false;
    }

    public RoundedImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mStateChanged = false;
    }

    public RoundedImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mStateChanged = false;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        checkStateChanged();
        setBitmap();
        setBitmapShader();
        setPaint();
        setRect();
        canvas.drawRoundRect(this.mRectF, this.mBitmap.getWidth(), this.mBitmap.getHeight(), this.mPaint);
    }

    private void checkStateChanged() {
        if (getDrawable() != this.mDrawable || getWidth() != this.mWidth || getHeight() != this.mHeight) {
            this.mDrawable = getDrawable();
            this.mWidth = getWidth();
            this.mHeight = getHeight();
            this.mStateChanged = true;
            return;
        }
        this.mStateChanged = false;
    }

    private void setBitmap() {
        if (this.mBitmap == null || this.mStateChanged) {
            this.mBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) this.mDrawable).getBitmap(), this.mWidth, this.mHeight, false);
        }
    }

    private void setBitmapShader() {
        if (this.mBitmapShader == null || this.mStateChanged) {
            this.mBitmapShader = new BitmapShader(this.mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
    }

    private void setPaint() {
        if (this.mPaint == null || this.mStateChanged) {
            Paint paint = new Paint();
            this.mPaint = paint;
            paint.setAntiAlias(true);
            this.mPaint.setShader(this.mBitmapShader);
        }
    }

    private void setRect() {
        if (this.mRectF == null || this.mStateChanged) {
            this.mRectF = new RectF(0.0f, 0.0f, this.mBitmap.getWidth(), this.mBitmap.getHeight());
        }
    }
}
