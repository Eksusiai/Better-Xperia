package com.sonymobile.calendar.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import com.sonymobile.accuweather.WeatherInfo;
import com.sonymobile.calendar.Utils;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherBitmap {
    private static final float WEATHER_ICON_SIZE = 0.7f;
    private WeatherInfo info;
    public Bitmap scaledBitmap;
    private Paint selectedPaint = new Paint();

    public boolean createScaledBitmap(Context context, WeatherInfo weatherInfo, boolean z) {
        if (weatherInfo == null) {
            this.scaledBitmap = null;
            return false;
        }
        if (!weatherInfo.equals(this.info) || this.scaledBitmap == null) {
            Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(context.getResources(), weatherInfo.iconResource);
            if (bitmapDecodeResource == null) {
                this.scaledBitmap = null;
                return false;
            }
            float f = context.getResources().getDisplayMetrics().density;
            float f2 = WEATHER_ICON_SIZE;
            if (z && !Utils.isTabletDevice(context)) {
                f2 = (f * WEATHER_ICON_SIZE) / 3.0f;
            } else if (z) {
                f2 = WEATHER_ICON_SIZE * f;
            }
            double d = f2;
            this.scaledBitmap = Bitmap.createScaledBitmap(bitmapDecodeResource, (int) (((double) bitmapDecodeResource.getWidth()) * d), (int) (((double) bitmapDecodeResource.getHeight()) * d), true);
            this.info = weatherInfo;
            bitmapDecodeResource.recycle();
        }
        return true;
    }

    public Paint getPaint(boolean z) {
        if (z) {
            return this.selectedPaint;
        }
        return null;
    }
}
