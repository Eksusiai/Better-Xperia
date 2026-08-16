package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarAddEventView extends View implements View.OnClickListener {
    private static final float STROKE_HALF_LENGTH = 0.25f;
    private static final float STROKE_WIDTH = 0.07f;
    private Time day;
    private CalendarEventNavigator eventNavigator;
    private boolean isAllDay;
    private int onHour;
    private Paint paint;
    private float strokeHalfLength;
    private float strokeWidth;

    public CalendarAddEventView(Context context, int i, CalendarEventNavigator calendarEventNavigator, Time time, boolean z) {
        super(context);
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.quick_add_background));
        UiUtils.setViewBackgroundToAccentColor(context, this);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.onHour = i;
        this.eventNavigator = calendarEventNavigator;
        this.day = time;
        this.isAllDay = z;
        initPaint();
        setOnClickListener(this);
    }

    public int getHourToShowOn() {
        return this.onHour;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.eventNavigator == null || this.day == null) {
            return;
        }
        Time time = new SafeTime(this.day.timezone);
        time.set(0, 0, this.onHour, this.day.monthDay, this.day.month, this.day.year);
        this.eventNavigator.goToCreateEvent(time.normalize(false), this.isAllDay);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        float fMin = Math.min(i2, i);
        this.strokeWidth = STROKE_WIDTH * fMin;
        this.strokeHalfLength = fMin * STROKE_HALF_LENGTH;
        super.onSizeChanged(i, i2, i3, i4);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawCross(canvas);
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setAntiAlias(true);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setColor(-1);
    }

    private void drawCross(Canvas canvas) {
        float width = getWidth() / 2.0f;
        float height = getHeight() / 2.0f;
        this.paint.setStrokeWidth(this.strokeWidth);
        float f = this.strokeHalfLength;
        canvas.drawLine(width - f, height, width + f, height, this.paint);
        float f2 = this.strokeHalfLength;
        canvas.drawLine(width, height - f2, width, height + f2, this.paint);
    }
}
