package com.sonymobile.calendar;

import android.content.ActivityNotFoundException;
import android.view.View;
import com.sonymobile.accuweather.WeatherInfo;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherClickListener implements View.OnClickListener {
    private boolean isCelsius;
    private DialogWeatherInfo mDialog;
    private WeatherInfo mInfo;

    public WeatherClickListener(WeatherInfo weatherInfo, boolean z, DialogWeatherInfo dialogWeatherInfo) {
        this.mInfo = weatherInfo;
        this.isCelsius = z;
        this.mDialog = dialogWeatherInfo;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        DialogWeatherInfo dialogWeatherInfo;
        if (!onClick(this.mInfo, this.isCelsius) || (dialogWeatherInfo = this.mDialog) == null) {
            return;
        }
        dialogWeatherInfo.dismiss();
    }

    public static boolean onClick(WeatherInfo weatherInfo, boolean z) {
        try {
            return CalendarApplication.getWeatherFactory().launchThirdPartyActivityForecast(weatherInfo, z);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
