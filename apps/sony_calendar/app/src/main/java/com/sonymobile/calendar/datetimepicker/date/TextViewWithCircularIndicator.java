package com.sonymobile.calendar.datetimepicker.date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class TextViewWithCircularIndicator extends AppCompatTextView {
    private final int mCircleColor;
    Paint mCirclePaint;
    private final int mDayTextColor;
    private final int mDayTextWhiteColor;
    private boolean mDrawCircle;
    private final String mItemIsSelectedText;

    public TextViewWithCircularIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCirclePaint = new Paint();
        this.mCircleColor = ContextCompat.getColor(context, R.color.date_picker_domain_color);
        this.mDayTextColor = ContextCompat.getColor(context, R.color.date_picker_text_normal);
        this.mDayTextWhiteColor = ContextCompat.getColor(context, R.color.white);
        this.mItemIsSelectedText = context.getResources().getString(R.string.item_is_selected);
        init();
    }

    private void init() {
        this.mCirclePaint.setFakeBoldText(true);
        this.mCirclePaint.setAntiAlias(true);
        this.mCirclePaint.setColor(this.mCircleColor);
        this.mCirclePaint.setTextAlign(Paint.Align.CENTER);
        this.mCirclePaint.setStyle(Paint.Style.FILL);
    }

    public void drawIndicator(boolean z) {
        this.mDrawCircle = z;
    }

    @Override // android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        if (this.mDrawCircle) {
            int width = getWidth();
            int height = getHeight();
            canvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2, this.mCirclePaint);
            setTextColor(this.mDayTextWhiteColor);
        } else {
            setTextColor(this.mDayTextColor);
        }
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public CharSequence getContentDescription() {
        CharSequence text = getText();
        return this.mDrawCircle ? String.format(this.mItemIsSelectedText, text) : text;
    }
}
