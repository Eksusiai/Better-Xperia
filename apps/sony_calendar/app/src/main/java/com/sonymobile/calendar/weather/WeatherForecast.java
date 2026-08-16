package com.sonymobile.calendar.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import com.sonymobile.accuweather.AccuWeatherBroker;
import com.sonymobile.accuweather.AccuWeatherForecast;
import com.sonymobile.accuweather.WeatherFactory;
import com.sonymobile.accuweather.WeatherListener;
import com.sonymobile.accuweather.WeatherLocation;
import com.sonymobile.accuweather.WeatherSet;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WeatherPreferences;
import com.sonymobile.calendar.utils.PermissionUtils;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherForecast {
    public static final String CLASS_NAME = "WeatherForecast";
    private static final boolean DEBUG = false;
    public static final int HALF_HOUR = 1800000;
    public static final String KEY_LAST_UPDATE = "lastUpdate";
    public static final String KEY_STORED_WEATHER_INFO = "key_stored_weather_info";
    private static final int MAX_NUMBER_OF_FORECAST_DAYS = 5;
    public static final String VALUE_CELSIUS = "Celsius";
    private AsyncTask<WeatherLocation, Void, Boolean> mAsyncTaskCity;
    private AsyncTask<Location, Void, Boolean> mAsyncTaskLoc;
    private Context mContext;
    private Queue<WeatherListener> mHandlers = new LinkedList();
    private boolean mIsUpdating;
    private long mLastUpdate;
    private SharedPreferences mPreferences;
    private int mUpdateFrequency;
    private WeatherSet mWeatherSet;

    public WeatherForecast(Context context, WeatherListener weatherListener, boolean z) {
        this.mLastUpdate = 0L;
        addListener(weatherListener);
        this.mContext = context;
        if (this.mIsUpdating) {
            return;
        }
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(context);
        this.mPreferences = sharedPreferences;
        this.mUpdateFrequency = Integer.parseInt(sharedPreferences.getString(WeatherPreferences.KEY_WEATHER_UPDATE, "1800000"));
        this.mLastUpdate = Long.parseLong(this.mPreferences.getString(KEY_LAST_UPDATE, "0"));
        String string = WeatherLocation.currentLocation(Utils.getCurrentLocationString(context)).getJSONObject().toString();
        if (Utils.isCurrentLocationSet(context)) {
            WeatherFactory weatherFactory = CalendarApplication.getWeatherFactory();
            if (PermissionUtils.isAccessLocationGranted(context)) {
                Location currentLocation = weatherFactory.getCurrentLocation();
                if (weatherFactory.isLocationAvailable(currentLocation)) {
                    start(currentLocation, z);
                    return;
                } else {
                    notifyNetworkError();
                    return;
                }
            }
            return;
        }
        start(WeatherLocation.fromString(this.mPreferences.getString(WeatherPreferences.KEY_CURRENT_LOCATION, string)), z);
    }

    private void start(WeatherLocation weatherLocation, boolean z) {
        if ((this.mPreferences.getString(KEY_STORED_WEATHER_INFO, null) != null) && !z) {
            updateIfNeeded(weatherLocation);
        } else {
            getWeatherSetAsync(weatherLocation);
        }
    }

    private void start(Location location, boolean z) {
        if ((this.mPreferences.getString(KEY_STORED_WEATHER_INFO, null) != null) && !z) {
            updateIfNeeded(location);
        } else {
            getWeatherSetAsync(location);
        }
    }

    private void updateIfNeeded(WeatherLocation weatherLocation) {
        if (System.currentTimeMillis() > this.mLastUpdate + ((long) this.mUpdateFrequency) && !this.mIsUpdating) {
            this.mLastUpdate = System.currentTimeMillis();
            getWeatherSetAsync(weatherLocation);
        } else {
            loadCachedWeather(weatherLocation);
        }
    }

    private void updateIfNeeded(Location location) {
        if (System.currentTimeMillis() > this.mLastUpdate + ((long) this.mUpdateFrequency) && !this.mIsUpdating) {
            this.mLastUpdate = System.currentTimeMillis();
            getWeatherSetAsync(location);
        } else {
            loadCachedWeather(location);
        }
    }

    private void loadCachedWeather(Location location) {
        if (loadStoredWeatherConditions()) {
            notifyUpdateDone();
        } else {
            getWeatherSetAsync(location);
        }
    }

    private void loadCachedWeather(WeatherLocation weatherLocation) {
        if (loadStoredWeatherConditions()) {
            notifyUpdateDone();
        } else {
            getWeatherSetAsync(weatherLocation);
        }
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [com.sonymobile.calendar.weather.WeatherForecast$1] */
    private void getWeatherSetAsync(Location location) {
        notifyUpdatingWeather();
        try {
            AsyncTask<Location, Void, Boolean> asyncTask = this.mAsyncTaskLoc;
            if (asyncTask != null) {
                asyncTask.cancel(true);
                this.mAsyncTaskLoc = null;
            }
            this.mAsyncTaskLoc = new AsyncTask<Location, Void, Boolean>() { // from class: com.sonymobile.calendar.weather.WeatherForecast.1
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Boolean bool) {
                    if (bool.booleanValue()) {
                        WeatherForecast.this.notifyUpdateDone();
                        WeatherForecast.this.mLastUpdate = System.currentTimeMillis();
                        WeatherForecast.this.mPreferences.edit().putString(WeatherForecast.KEY_LAST_UPDATE, String.valueOf(WeatherForecast.this.mLastUpdate)).commit();
                    } else {
                        WeatherForecast.this.notifyNetworkError();
                    }
                    WeatherForecast.this.mIsUpdating = false;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Boolean doInBackground(Location... locationArr) {
                    boolean z = false;
                    try {
                        Location location2 = locationArr[0];
                        AccuWeatherBroker accuWeatherBrokerCreateWeatherBroker = CalendarApplication.getWeatherFactory().createWeatherBroker();
                        WeatherForecast weatherForecast = WeatherForecast.this;
                        weatherForecast.mWeatherSet = accuWeatherBrokerCreateWeatherBroker.getWeatherData(weatherForecast.mContext, location2);
                        if (accuWeatherBrokerCreateWeatherBroker.isWeatherDataInitialized(WeatherForecast.this.mWeatherSet)) {
                            WeatherForecast.this.storeWeatherConditions();
                            z = true;
                        } else {
                            WeatherForecast.this.markStoredWeatherConditionsAsDirty();
                        }
                    } catch (Exception unused) {
                    }
                    return Boolean.valueOf(z);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);
        } catch (RejectedExecutionException unused) {
            notifyUpdateFailed();
            this.mIsUpdating = false;
        }
        this.mIsUpdating = true;
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [com.sonymobile.calendar.weather.WeatherForecast$2] */
    private void getWeatherSetAsync(WeatherLocation weatherLocation) {
        notifyUpdatingWeather();
        try {
            AsyncTask<WeatherLocation, Void, Boolean> asyncTask = this.mAsyncTaskCity;
            if (asyncTask != null) {
                asyncTask.cancel(true);
                this.mAsyncTaskCity = null;
            }
            this.mAsyncTaskCity = new AsyncTask<WeatherLocation, Void, Boolean>() { // from class: com.sonymobile.calendar.weather.WeatherForecast.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Boolean bool) {
                    if (!bool.booleanValue()) {
                        WeatherForecast.this.notifyNetworkError();
                    } else {
                        WeatherForecast.this.mLastUpdate = System.currentTimeMillis();
                        WeatherForecast.this.mPreferences.edit().putString(WeatherForecast.KEY_LAST_UPDATE, String.valueOf(WeatherForecast.this.mLastUpdate)).commit();
                        WeatherForecast.this.notifyUpdateDone();
                    }
                    WeatherForecast.this.mIsUpdating = false;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Boolean doInBackground(WeatherLocation... weatherLocationArr) {
                    boolean z = false;
                    try {
                        AccuWeatherBroker accuWeatherBrokerCreateWeatherBroker = CalendarApplication.getWeatherFactory().createWeatherBroker();
                        String str = weatherLocationArr[0].cityId;
                        AccuWeatherForecast.mCityName = weatherLocationArr[0].name;
                        WeatherForecast weatherForecast = WeatherForecast.this;
                        weatherForecast.mWeatherSet = accuWeatherBrokerCreateWeatherBroker.getWeatherData(weatherForecast.mContext, str);
                        if (accuWeatherBrokerCreateWeatherBroker.isWeatherDataInitialized(WeatherForecast.this.mWeatherSet)) {
                            WeatherForecast.this.storeWeatherConditions();
                            z = true;
                        } else {
                            WeatherForecast.this.markStoredWeatherConditionsAsDirty();
                        }
                    } catch (Exception unused) {
                    }
                    return Boolean.valueOf(z);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, weatherLocation);
        } catch (RejectedExecutionException unused) {
            notifyUpdateFailed();
            this.mIsUpdating = false;
        }
        this.mIsUpdating = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markStoredWeatherConditionsAsDirty() {
        SharedPreferences.Editor editorEdit = this.mPreferences.edit();
        editorEdit.putString(KEY_STORED_WEATHER_INFO, null);
        editorEdit.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void storeWeatherConditions() {
        SharedPreferences.Editor editorEdit = this.mPreferences.edit();
        editorEdit.putString(KEY_STORED_WEATHER_INFO, this.mWeatherSet.toJSONObject(5).toString());
        editorEdit.apply();
    }

    private boolean loadStoredWeatherConditions() {
        if (this.mPreferences.getString(KEY_STORED_WEATHER_INFO, null) == null) {
            return false;
        }
        WeatherSet weatherSet = new WeatherSet();
        this.mWeatherSet = weatherSet;
        weatherSet.fromCachedString(this.mPreferences.getString(KEY_STORED_WEATHER_INFO, null), 5);
        return true;
    }

    private void addListener(WeatherListener weatherListener) {
        this.mHandlers.add(weatherListener);
    }

    private void notifyUpdatingWeather() {
        Iterator<WeatherListener> it = this.mHandlers.iterator();
        while (it.hasNext()) {
            it.next().onWeatherUpdating();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUpdateDone() {
        while (!this.mHandlers.isEmpty()) {
            this.mHandlers.remove().onWeatherUpdateFinished(this.mWeatherSet);
        }
    }

    private void notifyUpdateFailed() {
        while (!this.mHandlers.isEmpty()) {
            this.mHandlers.remove().onWeatherUpdateFailed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyNetworkError() {
        while (!this.mHandlers.isEmpty()) {
            this.mHandlers.remove().onWeatherNetworkError();
        }
    }
}
