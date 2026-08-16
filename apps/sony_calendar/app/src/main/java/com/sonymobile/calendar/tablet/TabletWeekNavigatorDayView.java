package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.sonymobile.calendar.NavigatorDaySelectedListener;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WeekNavigatorDayView;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class TabletWeekNavigatorDayView extends WeekNavigatorDayView {
    protected static final float WEATHER_Y_OFFSET = 2.0f;
    private boolean isSelected;
    private Paint temperaturePaint;
    protected float weatherXPos;

    public void setNavigatorDayClickedListener(NavigatorDaySelectedListener navigatorDaySelectedListener, int i) {
    }

    public TabletWeekNavigatorDayView(Context context, boolean z) {
        super(context, z, false);
        this.isSelected = false;
        this.weatherXPos = 5.0f;
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView
    protected void initPaint() {
        super.initPaint();
        Paint paint = new Paint();
        this.temperaturePaint = paint;
        paint.setAntiAlias(true);
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView
    protected void initMeasures() {
        this.columnHeight = getResources().getDimensionPixelSize(R.dimen.navigation_view_column_height);
        this.dayLabelSize = getResources().getDimension(R.dimen.day_label_week_number);
        this.dayNumberSize = this.columnHeight * 0.175f;
        this.dayLunarNumberSize = this.columnHeight * 0.13f;
        this.padding = this.columnHeight * 0.06f;
        this.weatherYPos = this.dayLabelSize + (this.padding * WEATHER_Y_OFFSET);
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView
    protected void drawWeatherIcon(Canvas canvas) {
        if (this.mWeatherBitmap.scaledBitmap != null) {
            canvas.drawBitmap(this.mWeatherBitmap.scaledBitmap, this.isR2L ? this.padding : this.weatherXPos, this.weatherYPos, this.mWeatherBitmap.getPaint(isDaySelected()));
        }
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView
    protected String getLabelStrings(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("cccc", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Utils.getTimeZone(getContext(), null)));
        return simpleDateFormat.format(Long.valueOf(j)).toUpperCase(Locale.getDefault());
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView
    protected void drawBox(Canvas canvas, int i) {
        if (this.isSelected) {
            i = 0;
        }
        super.drawBox(canvas, i);
    }

    @Override // com.sonymobile.calendar.WeekNavigatorDayView
    protected void drawDayNumber(Canvas canvas, int i, int i2) {
        if (this.isSelected) {
            i = -1;
        }
        super.drawDayNumber(canvas, i, i2);
    }
}
