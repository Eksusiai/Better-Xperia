package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.utils.UiUtils;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarTimeLine extends View {
    private static final float AMPM_TEXT_SIZE = 6.0f;
    private static final String HOUR_PATTERN_12 = "hh";
    private static final String HOUR_PATTERN_24 = "kk";
    private static final float HOUR_TEXT_MARGIN_BOTTOM = 1.0f;
    private static final float HOUR_TEXT_SIZE = 11.0f;
    private float ampmTextSize;
    private boolean circleVisibility;
    private boolean displayForFullWeek;
    private Time displayedDate;
    private float hourTextMarginBottom;
    private float hourTextSize;
    private boolean isR2L;
    private Paint paint;
    private int scrollOffset;
    private int textColor;
    private SimpleDateFormat timeFormat;
    private int viewPortHeight;

    public CalendarTimeLine(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.circleVisibility = true;
        initMeasures();
        initDrawingResources();
        this.isR2L = isR2L();
        if (Utils.isTabletDevice(context)) {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.month_view_background_color));
        }
    }

    public void setViewPortHeight(int i) {
        this.viewPortHeight = i;
    }

    public void setDisplayedDate(Time time) {
        this.circleVisibility = true;
        if (this.displayedDate != time) {
            this.displayedDate = time;
            invalidate();
        }
    }

    public void setVisibilityOfCircle(boolean z) {
        if (this.circleVisibility != z) {
            this.circleVisibility = z;
            invalidate();
        }
    }

    public void setScrollOffset(int i) {
        this.scrollOffset = i;
        invalidate();
    }

    public void setShouldDisplayForFullWeek(boolean z) {
        this.displayForFullWeek = z;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float height = getHeight() / 24.0f;
        drawHours(canvas, height);
        if (!DateFormat.is24HourFormat(getContext())) {
            drawAmPm(canvas, height);
        }
        drawCurrentTimeCircle(canvas);
    }

    private void drawCurrentTimeCircle(Canvas canvas) {
        if (this.circleVisibility && shouldShowForDisplayedWeek()) {
            long timeOfDayFromMidnight = getTimeOfDayFromMidnight();
            float dimension = getResources().getDimension(R.dimen.time_line_circle_radius);
            if (timeOfDayFromMidnight >= 86400000 || timeOfDayFromMidnight <= 0) {
                return;
            }
            this.paint.setColor(UiUtils.getAccentColor(getContext()));
            canvas.drawCircle(this.isR2L ? dimension : getWidth() - dimension, (timeOfDayFromMidnight / 8.64E7f) * getHeight(), dimension, this.paint);
        }
    }

    private void initMeasures() {
        float f = getContext().getResources().getDisplayMetrics().density;
        this.hourTextSize = HOUR_TEXT_SIZE * f;
        this.ampmTextSize = AMPM_TEXT_SIZE * f;
        this.hourTextMarginBottom = f * 1.0f;
    }

    private void initDrawingResources() {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setTextAlign(Paint.Align.CENTER);
        this.paint.setStyle(Paint.Style.FILL);
        this.textColor = ContextCompat.getColor(getContext(), R.color.day_number_color);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        this.timeFormat = simpleDateFormat;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private void drawHours(Canvas canvas, float f) {
        if (DateFormat.is24HourFormat(getContext())) {
            this.timeFormat.applyPattern(HOUR_PATTERN_24);
        } else {
            this.timeFormat.applyPattern(HOUR_PATTERN_12);
        }
        this.paint.setColor(this.textColor);
        this.paint.setTextSize(this.hourTextSize);
        this.paint.setTypeface(Typeface.create(getResources().getString(R.string.roboto_font), 0));
        this.paint.setAntiAlias(true);
        float width = getWidth() / 2.0f;
        float f2 = (this.hourTextSize / 2.8f) + f;
        long j = 3600000;
        for (int i = 1; i < 24; i++) {
            canvas.drawText(this.timeFormat.format(Long.valueOf(j)), width, f2, this.paint);
            f2 += f;
            j += 3600000;
        }
    }

    private void drawAmPm(Canvas canvas, float f) {
        this.timeFormat.applyPattern("a");
        this.paint.setTextSize(this.ampmTextSize);
        float width = getWidth() / 2.0f;
        float f2 = (this.hourTextSize / 2.0f) + this.ampmTextSize + this.hourTextMarginBottom;
        int iCeil = (int) Math.ceil((this.scrollOffset + 1) / f);
        if (iCeil < 12) {
            canvas.drawText(this.timeFormat.format((Object) 0), width, (iCeil * f) + f2, this.paint);
            if (((int) (((this.scrollOffset + 1) + this.viewPortHeight) / f)) >= 12) {
                canvas.drawText(this.timeFormat.format((Object) 43200000), width, (f * 12.0f) + f2, this.paint);
                return;
            }
            return;
        }
        canvas.drawText(this.timeFormat.format((Object) 43200000), width, (iCeil * f) + f2, this.paint);
    }

    private boolean shouldShowForDisplayedWeek() {
        Time time = new SafeTime(this.displayedDate);
        time.setToNow();
        return Utils.getWeekNumberOfDay(getContext(), time) == Utils.getWeekNumberOfDay(getContext(), this.displayedDate);
    }

    private long getTimeOfDayFromMidnight() {
        return System.currentTimeMillis() - getTimeAtMidnight();
    }

    private long getTimeAtMidnight() {
        Time time = new SafeTime(this.displayedDate);
        if (this.displayForFullWeek) {
            time.setToNow();
            time.set(0, 0, 0, time.monthDay, time.month, time.year);
        } else {
            time.set(0, 0, 0, this.displayedDate.monthDay, this.displayedDate.month, this.displayedDate.year);
        }
        return time.toMillis(false);
    }

    private boolean isR2L() {
        return CalendarApplication.isR2L(getResources());
    }
}
