package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.google.common.primitives.Ints;

/* JADX INFO: loaded from: classes2.dex */
public class MonthAgendaTransitionView extends View {
    private static final int MONKEY_TEST_ROW_COUNT = 6;
    private Bitmap labelBitmap;
    private int labelHeight;
    private Bitmap monthBitmap;
    private int monthHeight;
    private Rect monthRect;
    private int weekHeight;
    private int weekMonthHeight;
    private int weekY;

    public MonthAgendaTransitionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void prepareTransition(Time time, int i, int i2, int i3, DateBoxGrid dateBoxGrid) {
        dateBoxGrid.setDrawingCacheEnabled(true);
        dateBoxGrid.measure(View.MeasureSpec.makeMeasureSpec(i3, Ints.MAX_POWER_OF_TWO), View.MeasureSpec.makeMeasureSpec(i, Ints.MAX_POWER_OF_TWO));
        dateBoxGrid.layout(0, 0, dateBoxGrid.getMeasuredWidth(), dateBoxGrid.getMeasuredHeight());
        int labelHeight = dateBoxGrid.getLabelHeight();
        this.labelHeight = labelHeight;
        this.monthHeight = i - labelHeight;
        int rowCount = dateBoxGrid.getRowCount();
        int i4 = this.monthHeight;
        if (rowCount == 0) {
            rowCount = 6;
        }
        this.weekHeight = i4 / rowCount;
        this.weekMonthHeight = dateBoxGrid.getRowCount() * this.weekHeight;
        this.weekY = dateBoxGrid.getRowIndex(time) * this.weekHeight;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i3, i, Bitmap.Config.ARGB_8888);
        dateBoxGrid.draw(new Canvas(bitmapCreateBitmap));
        Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(bitmapCreateBitmap, 0, 0, bitmapCreateBitmap.getWidth(), this.labelHeight);
        this.labelBitmap = Bitmap.createBitmap(bitmapCreateBitmap.getWidth(), this.labelHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.labelBitmap);
        canvas.drawColor(ContextCompat.getColor(dateBoxGrid.getContext(), R.color.calendar_grid_area_background));
        canvas.drawBitmap(bitmapCreateBitmap2, 0.0f, 0.0f, (Paint) null);
        this.monthBitmap = Bitmap.createBitmap(bitmapCreateBitmap, 0, this.labelHeight, bitmapCreateBitmap.getWidth(), this.monthHeight);
        bitmapCreateBitmap.recycle();
        bitmapCreateBitmap2.recycle();
        this.monthRect = new Rect(0, this.labelHeight, this.monthBitmap.getWidth(), this.labelHeight + this.monthHeight);
    }

    public void onTransitionCompleted() {
        Bitmap bitmap = this.labelBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        Bitmap bitmap2 = this.monthBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = this.weekHeight;
        int i6 = this.labelHeight;
        int i7 = i5 + i6;
        float f = ((i4 - i2) - i7) / ((this.monthHeight + i6) - i7);
        float f2 = 1.0f - f;
        this.monthRect.top = (int) (i6 - (this.weekY * f2));
        Rect rect = this.monthRect;
        rect.bottom = (int) (rect.top + (this.monthHeight * f) + (this.weekMonthHeight * f2));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(this.monthBitmap, (Rect) null, this.monthRect, (Paint) null);
        canvas.drawBitmap(this.labelBitmap, 0.0f, 0.0f, (Paint) null);
    }
}
