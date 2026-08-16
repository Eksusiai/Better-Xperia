package com.sonymobile.calendar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.QuickContactBadge;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class RoundedQuickContactBadge extends QuickContactBadge {
    private static float circularImageBorder = 1.0f;
    private final Paint bitmapPaint;
    private final Paint borderPaint;
    private final RectF destination;
    private final Matrix matrix;
    private final RectF source;

    public RoundedQuickContactBadge(Context context) {
        this(context, null, 0);
    }

    public RoundedQuickContactBadge(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RoundedQuickContactBadge(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.matrix = new Matrix();
        this.source = new RectF();
        this.destination = new RectF();
        Paint paint = new Paint();
        this.bitmapPaint = paint;
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        Paint paint2 = new Paint();
        this.borderPaint = paint2;
        paint2.setColor(0);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(circularImageBorder);
        paint2.setAntiAlias(true);
    }

    @Override // android.widget.QuickContactBadge, android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        Bitmap bitmapDrawableToBitmap = UiUtils.drawableToBitmap(getDrawable());
        if (bitmapDrawableToBitmap == null) {
            return;
        }
        this.source.set(0.0f, 0.0f, bitmapDrawableToBitmap.getWidth(), bitmapDrawableToBitmap.getHeight());
        this.destination.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        drawBitmapWithCircleOnCanvas(bitmapDrawableToBitmap, canvas, this.source, this.destination);
    }

    public void drawBitmapWithCircleOnCanvas(Bitmap bitmap, Canvas canvas, RectF rectF, RectF rectF2) {
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.matrix.reset();
        this.matrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.FILL);
        bitmapShader.setLocalMatrix(this.matrix);
        this.bitmapPaint.setShader(bitmapShader);
        canvas.drawCircle(rectF2.centerX(), rectF2.centerY(), rectF2.width() / 2.0f, this.bitmapPaint);
        canvas.drawCircle(rectF2.centerX(), rectF2.centerY(), (rectF2.width() / 2.0f) - (circularImageBorder / 2.0f), this.borderPaint);
    }
}
