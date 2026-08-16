package com.sonymobile.calendar.weather;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import com.sonymobile.accuweather.AccuWeatherBroker;
import com.sonymobile.accuweather.AccuWeatherForecast;
import com.sonymobile.accuweather.WeatherFactory;
import com.sonymobile.accuweather.WeatherInfo;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import java.util.AbstractCollection;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherFactoryAccu extends WeatherFactory {
    public static final int NUMBER_OF_FORECAST_DAYS = 5;

    @Override // com.sonymobile.accuweather.WeatherFactory
    public int getNumberOfForecastDays(AbstractCollection<?> abstractCollection) {
        return 5;
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public boolean isLocationAvailable(Location location) {
        return location != null;
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public boolean isWeatherBrokerAvailable() {
        return true;
    }

    public WeatherFactoryAccu(Context context) {
        super(context);
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public AccuWeatherBroker createWeatherBroker() {
        return new AccuWeatherForecast();
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public AccuWeatherMapper createWeatherMapper() {
        return AccuWeatherMapper.getInstance();
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public boolean launchThirdPartyActivityForecast(WeatherInfo weatherInfo, boolean z) {
        String strReplace;
        String language = Locale.getDefault().getLanguage();
        if (weatherInfo.isToday) {
            strReplace = weatherInfo.forecastUrl.replace("current.aspx", "hourly.aspx").replace(" ", "%20");
        } else {
            strReplace = weatherInfo.forecastUrl.replace(" ", "%20");
        }
        StringBuilder sb = new StringBuilder(strReplace);
        sb.append(z ? "&unit=c" : "&unit=f");
        sb.append("&").append(AccuWeatherBroker.PARTNER_PARAMETER_CALENDAR);
        GeneralPreferences.getSharedPreferences(this.mContext).edit().putString(GeneralPreferences.KEY_CURRENT_LANGUAGE, language).commit();
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(sb.toString()));
        intent.setFlags(268435456);
        try {
            this.mContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public boolean getWeatherLocationEntryEnabled(boolean z) {
        if (Utils.isUsingWeatherBrokerForChina(this.mContext)) {
            return false;
        }
        return z;
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public String getWeatherInformationDialogText() {
        Resources resources = this.mContext.getResources();
        return String.format(Locale.getDefault(), resources.getString(R.string.weather_information_dialog_text), resources.getString(R.string.accuweather_name) + ".com");
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public String getWeatherInformationDialogTitle() {
        return this.mContext.getResources().getString(R.string.accuweather_name);
    }

    @Override // com.sonymobile.accuweather.WeatherFactory
    public Location getCurrentLocation() {
        return Utils.getCurrentLocation(this.mContext);
    }
}
