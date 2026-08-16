package com.sonymobile.calendar.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.ViewType;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.weather.WeatherIcon;

/* JADX INFO: loaded from: classes2.dex */
public class WhiteHeader extends RelativeLayout {
    private boolean mShowHolidays;
    private Time mTime;
    private TextView mTitleTextView;
    private WeatherIcon mWeatherIcon;

    public WhiteHeader(Context context) {
        super(context);
        this.mShowHolidays = true;
        initViews(context);
    }

    public WhiteHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowHolidays = true;
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.white_header, this);
        this.mTitleTextView = (TextView) findViewById(R.id.title);
        this.mWeatherIcon = (WeatherIcon) findViewById(R.id.weatherIcon);
    }

    public void setDate(Time time, ViewType viewType) {
        this.mTime = time;
        if (UiUtils.isPhoneLandscape(getContext())) {
            this.mTitleTextView.setText(Utils.getActionBarDateTitle(getContext(), this.mTime, 3));
            if (this.mShowHolidays) {
                FreeDayService.getInstance().requestHolidayName(getContext(), time.year, time.month, time.monthDay, new FreeDayServiceResultHandler(), 1);
            }
        } else if (viewType == ViewType.MONTH || viewType == ViewType.DAY) {
            this.mTitleTextView.setText(Utils.getDateString(getContext(), this.mTime, viewType));
            FreeDayService.getInstance().requestHolidayName(getContext(), time.year, time.month, time.monthDay, new FreeDayServiceResultHandler(), 1);
        }
        this.mWeatherIcon.setWeatherInfo(this.mTime, false);
    }

    public void showWeather(boolean z) {
        if (z) {
            return;
        }
        this.mWeatherIcon.setVisibility(8);
    }

    public void setWeatherIconInfo() {
        this.mWeatherIcon.setWeatherInfo(this.mTime, true);
    }

    public void setTitleClickListener(View.OnClickListener onClickListener) {
        this.mTitleTextView.setOnClickListener(onClickListener);
    }

    public void resetTitleStartMargin() {
        ((RelativeLayout.LayoutParams) this.mTitleTextView.getLayoutParams()).setMarginStart(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHolidayColor(String str, int i) {
        this.mTitleTextView.setText(str, TextView.BufferType.SPANNABLE);
        Spannable spannable = (Spannable) this.mTitleTextView.getText();
        spannable.setSpan(new ForegroundColorSpan(FreeDayService.getInstance().getHolidayColor(getContext())), i, str.length(), 17);
        this.mTitleTextView.setText(spannable);
        this.mTitleTextView.invalidate();
    }

    private class FreeDayServiceResultHandler implements IAsyncServiceResultHandler {
        private FreeDayServiceResultHandler() {
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() != 1) {
                return;
            }
            String str = (String) obj;
            if (str.equals("") || !WhiteHeader.this.mShowHolidays) {
                return;
            }
            WhiteHeader.this.setHolidayColor(Utils.appendStringsWithDash(str, Utils.getDayDateString(WhiteHeader.this.getContext(), WhiteHeader.this.mTime, "MMMM d yyyy")), 0);
        }
    }

    public void showHolidays(boolean z) {
        this.mShowHolidays = z;
    }
}
