package com.sonymobile.calendar.weather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import com.sonyericsson.calendar.util.WeatherService;
import com.sonymobile.accuweather.WeatherInfo;
import com.sonymobile.accuweather.WeatherLocation;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.DialogWeatherInfo;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.PermissionSettingsDialog;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WeatherClickListener;
import com.sonymobile.calendar.WeatherPreferences;
import com.sonymobile.calendar.utils.PermissionUtils;
import java.lang.ref.WeakReference;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherIcon extends FrameLayout {
    private static final int DAYS_IN_FUTURE_SHOWN = 4;
    private static boolean mFirstTimeCheckPermission = true;
    private View.OnClickListener mOnClickListener = new AnonymousClass1();
    private int mHigh;
    private TextView mHighTempView;
    private ImageView mImageView;
    private WeatherInfo mInfo;
    private boolean mIsCelsius;
    private boolean mIsDarkWhiteHeader;
    private boolean mIsR2L;
    private int mLow;
    private TextView mLowTempView;
    private Time mTime;
    private TextView mWeatherLocation;

    /* JADX INFO: renamed from: com.sonymobile.calendar.weather.WeatherIcon$1, reason: invalid class name */
    class AnonymousClass1 implements View.OnClickListener {
        DialogWeatherInfo dialog;

        AnonymousClass1() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            WeatherIcon weatherIcon = (WeatherIcon) view;
            if (!weatherIcon.shouldShowMissingLocationIcon()) {
                if (weatherIcon.mInfo != null && GeneralPreferences.getSharedPreferences(weatherIcon.getContext()).getBoolean(GeneralPreferences.KEY_WEATHER_HIDE_DIALOG, false)) {
                    WeatherClickListener.onClick(weatherIcon.mInfo, weatherIcon.mIsCelsius);
                    return;
                }
                if (this.dialog != null || weatherIcon.mInfo == null) {
                    return;
                }
                DialogWeatherInfo dialogWeatherInfoNewInstance = DialogWeatherInfo.newInstance(weatherIcon.mInfo);
                this.dialog = dialogWeatherInfoNewInstance;
                dialogWeatherInfoNewInstance.setOnDismissListener(new DialogWeatherInfo.OnDismissListener() { // from class: com.sonymobile.calendar.weather.WeatherIcon.1.1
                    @Override // com.sonymobile.calendar.DialogWeatherInfo.OnDismissListener
                    public void onDismiss() {
                        AnonymousClass1.this.dialog = null;
                    }
                });
                this.dialog.show(((AppCompatActivity) weatherIcon.getContext()).getSupportFragmentManager(), "dialog");
                return;
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) weatherIcon.getContext(), "android.permission.ACCESS_FINE_LOCATION") || WeatherIcon.mFirstTimeCheckPermission) {
                boolean unused = WeatherIcon.mFirstTimeCheckPermission = false;
                ActivityCompat.requestPermissions((Activity) weatherIcon.getContext(), new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 0);
            } else {
                weatherIcon.showCouldNotContinueDialog();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCouldNotContinueDialog() {
        PermissionSettingsDialog permissionSettingsDialogNewInstance = PermissionSettingsDialog.newInstance(new String[]{"android.permission.ACCESS_FINE_LOCATION"});
        AppCompatActivity appCompatActivity = getContext() instanceof AppCompatActivity ? (AppCompatActivity) getContext() : null;
        if (appCompatActivity != null) {
            permissionSettingsDialogNewInstance.show(appCompatActivity.getSupportFragmentManager(), PermissionSettingsDialog.TAG);
        }
    }

    public WeatherIcon(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsCelsius = true;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.WeatherIcon);
        this.mIsDarkWhiteHeader = typedArrayObtainStyledAttributes.getBoolean(0, false);
        typedArrayObtainStyledAttributes.recycle();
        initViews(context);
    }

    public WeatherIcon(Context context) {
        super(context);
        this.mIsCelsius = true;
        initViews(context);
    }

    private void initViews(Context context) {
        View viewInflate = View.inflate(context, R.layout.weather_icon, null);
        this.mImageView = (ImageView) viewInflate.findViewById(R.id.weather_icon);
        this.mHighTempView = (TextView) viewInflate.findViewById(R.id.high_temp);
        this.mLowTempView = (TextView) viewInflate.findViewById(R.id.low_temp);
        this.mWeatherLocation = (TextView) viewInflate.findViewById(R.id.weather_location);
        addView(viewInflate);
        setFocusable(true);
        if (this.mIsDarkWhiteHeader) {
            this.mHighTempView.setTextColor(ContextCompat.getColor(context, R.color.white));
            this.mLowTempView.setTextColor(ContextCompat.getColor(context, R.color.white));
            this.mImageView.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
            this.mWeatherLocation.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        setOnClickListener(mOnClickListener);
    }

    private void setTemperature() {
        boolean zEquals = GeneralPreferences.getSharedPreferences(getContext()).getString(WeatherPreferences.KEY_WEATHER_UNIT, getContext().getString(R.string.preferences_default_weather_unit)).equals(WeatherForecast.VALUE_CELSIUS);
        this.mIsCelsius = zEquals;
        String strValueOf = String.valueOf(zEquals ? (char) 8451 : (char) 8457);
        String strValueOf2 = this.mIsCelsius ? String.valueOf(this.mHigh) : String.valueOf(WeatherService.celsius2Fahrenheit(this.mHigh));
        String string = (this.mIsR2L ? new StringBuilder().append(strValueOf).append(strValueOf2) : new StringBuilder().append(strValueOf2).append(strValueOf)).toString();
        String strValueOf3 = this.mIsCelsius ? String.valueOf(this.mLow) : String.valueOf(WeatherService.celsius2Fahrenheit(this.mLow));
        String string2 = (this.mIsR2L ? new StringBuilder().append(strValueOf).append(strValueOf3) : new StringBuilder().append(strValueOf3).append(strValueOf)).toString();
        this.mHighTempView.setText(string);
        this.mHighTempView.requestLayout();
        this.mLowTempView.setText(string2);
        this.mLowTempView.requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldShowMissingLocationIcon() {
        boolean zIsAccessLocationGranted = PermissionUtils.isAccessLocationGranted(getContext());
        boolean z = GeneralPreferences.getSharedPreferences(getContext()).getBoolean(GeneralPreferences.KEY_WEATHER, true);
        boolean zIsCurrentLocationSet = Utils.isCurrentLocationSet(getContext());
        Time time = this.mTime;
        return z && zIsCurrentLocationSet && (time != null && Utils.compareToTodayMidnight(time) >= 0 && (this.mTime.toMillis(true) > (System.currentTimeMillis() + 345600000) ? 1 : (this.mTime.toMillis(true) == (System.currentTimeMillis() + 345600000) ? 0 : -1)) < 0) && !zIsAccessLocationGranted;
    }

    public void setWeatherInfo(Time time, boolean z) {
        this.mTime = time;
        if (shouldShowMissingLocationIcon()) {
            setVisibility(0);
            this.mImageView.setImageResource(R.drawable.no_permission_weather);
            return;
        }
        setVisibility(8);
        if (z && !Utils.isTabletDevice(getContext())) {
            this.mWeatherLocation.setVisibility(8);
        }
        WeatherService.getInstance().requestWeather(getContext(), new WeatherResultHandler(this, null), 0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWeatherInfo(WeatherInfo weatherInfo, boolean z, boolean z2) {
        this.mInfo = weatherInfo;
        if (weatherInfo == null) {
            setVisibility(8);
            this.mImageView.setVisibility(8);
            this.mHighTempView.setText("");
            this.mLowTempView.setText("");
            this.mWeatherLocation.setText(getResources().getString(R.string.current_location_not_set));
            return;
        }
        this.mIsR2L = z2;
        this.mHigh = weatherInfo.highTemperature;
        if (z && weatherInfo.lowTemperature != 9999) {
            this.mLow = weatherInfo.lowTemperature;
            this.mLowTempView.setVisibility(0);
        } else {
            this.mLowTempView.setVisibility(8);
        }
        setTemperature();
        this.mImageView.setImageResource(weatherInfo.iconResource);
        this.mImageView.setVisibility(0);
        this.mWeatherLocation.setText(getSelectedLocation(this.mInfo.mCityName));
        setVisibility(0);
        setupContentDescription(weatherInfo, z);
    }

    private String getSelectedLocation(String str) {
        if (str.isEmpty()) {
            return Utils.getCurrentLocationString(getContext());
        }
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(getContext());
        String string = WeatherLocation.currentLocation(Utils.getCurrentLocationString(getContext())).getJSONObject().toString();
        String string2 = sharedPreferences.getString(WeatherPreferences.KEY_CURRENT_LOCATION, string);
        try {
            if (string2.equals(string)) {
                String string3 = new WeatherLocation(str, "", "").getJSONObject().toString();
                SharedPreferences.Editor editorEdit = sharedPreferences.edit();
                editorEdit.putString(WeatherPreferences.KEY_HOME_LOCATION, string3);
                editorEdit.apply();
                return str;
            }
            WeatherLocation.currentLocation(Utils.getCurrentLocationString(getContext()));
            return WeatherLocation.fromJSON(new JSONObject(string2)).name;
        } catch (JSONException e) {
            e.printStackTrace();
            return Utils.getCurrentLocationString(getContext());
        }
    }

    private void setupContentDescription(WeatherInfo weatherInfo, boolean z) {
        String string;
        StringBuilder sb;
        if (this.mIsCelsius) {
            string = getContext().getString(R.string.weather_celsius);
        } else {
            string = getContext().getString(R.string.weather_fahrenheit);
        }
        String quantityString = getContext().getResources().getQuantityString(R.plurals.accessibility_degree_plural, this.mHigh);
        String str = String.format(Locale.getDefault(), getContext().getString(weatherInfo.isToday ? R.string.accessibility_weather_current : R.string.accessibility_weather_high), Integer.valueOf(this.mIsCelsius ? this.mHigh : WeatherService.celsius2Fahrenheit(this.mHigh)));
        if (weatherInfo.iconStringDescription != -1) {
            sb = new StringBuilder(getContext().getString(weatherInfo.iconStringDescription));
            sb.append(RecurrenceRuleParser.VALUE_SEPARATOR).append(str);
        } else {
            sb = new StringBuilder(str);
        }
        sb.append(quantityString).append(string).append(RecurrenceRuleParser.VALUE_SEPARATOR);
        if (z && weatherInfo.lowTemperature != 9999) {
            sb.append(String.format(Locale.getDefault(), getContext().getString(R.string.accessibility_weather_low), Integer.valueOf(this.mIsCelsius ? this.mLow : WeatherService.celsius2Fahrenheit(this.mLow)))).append(getContext().getResources().getQuantityString(R.plurals.accessibility_degree_plural, this.mLow)).append(string);
        }
        setContentDescription(sb.toString());
    }

    private static class WeatherResultHandler implements IAsyncServiceResultHandler {
        private final WeakReference<WeatherIcon> weatherIconWeekReference;

        /* synthetic */ WeatherResultHandler(WeatherIcon weatherIcon, AnonymousClass1 anonymousClass1) {
            this(weatherIcon);
        }

        private WeatherResultHandler(WeatherIcon weatherIcon) {
            this.weatherIconWeekReference = new WeakReference<>(weatherIcon);
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            WeatherIcon weatherIcon = this.weatherIconWeekReference.get();
            if (weatherIcon == null || !((Boolean) obj).booleanValue() || weatherIcon.mTime == null) {
                return;
            }
            weatherIcon.setWeatherInfo(WeatherService.getInstance().getWeatherInfo(weatherIcon.mTime), true, CalendarApplication.isR2L(weatherIcon.getResources()));
        }
    }
}
