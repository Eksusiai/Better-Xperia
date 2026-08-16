package com.sonymobile.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.format.Time;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonyericsson.calendar.util.WeatherService;
import com.sonymobile.accuweather.WeatherInfo;
import com.sonymobile.calendar.holiday.ChinaHolidaysManager;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.weather.WeatherBitmap;
import com.sonymobile.lunar.lib.LunarUtils;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class WeekNavigatorDayView extends LinearLayout implements View.OnClickListener {
    protected static final float DAY_LABEL_SIZE = 0.15f;
    protected static final float DAY_LUNAR_NUMBER_SIZE = 0.13f;
    protected static final float DAY_NUMBER_SIZE_OFFSET_TABLET = 0.818f;
    protected static final float DAY_NUMBER_SIZE_TABLET = 0.175f;
    protected static final float EVENTDOT_Y_OFFSET = 2.2f;
    private static final float GRID_LINE_WIDTH = 1.0f;
    protected static final float PADDING = 0.06f;
    protected static final float PHONE_LANDSCAPE_SCALE = 1.5f;
    protected static final float PHONE_LANDSCAPE_TWO_LETTERS_SCALE = 0.5714286f;
    protected static final float WEEK_NUMBER_Y_OFFSET_TABLET = 3.2f;
    private boolean allowedToDraw;
    private int backgroundColor;
    private Paint borderPaint;
    private int bottom;
    private int boxHeight;
    private Paint boxPaint;
    protected int columnHeight;
    protected int columnWidth;
    protected Time day;
    protected float dayLabelSize;
    private String dayLunarNumber;
    protected float dayLunarNumberSize;
    private String dayNumber;
    protected float dayNumberSize;
    private boolean isLunarAvailable;
    protected boolean isR2L;
    protected boolean isToday;
    protected boolean isWeekView;
    private String label;
    private float mDayNumberPaddingTop;
    protected boolean mIsPhoneCondensedLook;
    protected boolean mIsTablet;
    private int mJulianDay;
    private Paint mPointPaint;
    protected Time mSelectedDate;
    protected WeatherBitmap mWeatherBitmap;
    protected float padding;
    private TextPaint textPaint;
    private float top;
    protected WeatherInfo weatherInfo;
    protected float weatherYPos;

    protected boolean isDaySelected() {
        return false;
    }

    public WeekNavigatorDayView(Context context, boolean z, boolean z2) {
        super(context);
        this.isLunarAvailable = false;
        this.isLunarAvailable = LunarAvailabilityManager.isLunarAvailable(context);
        this.isR2L = z;
        this.isWeekView = z2;
        this.mIsTablet = Utils.isTabletDevice(getContext());
        this.mIsPhoneCondensedLook = UiUtils.isPhoneLandscape(getContext()) || UiUtils.isSub320dpScreen(getContext());
        setOnClickListener(this);
        initMeasures();
        initPaint();
        initWeatherBitmap();
        setFocusable(true);
        this.backgroundColor = 0;
        setBackgroundColor(0);
    }

    private void initWeatherBitmap() {
        this.mWeatherBitmap = new WeatherBitmap();
    }

    public void setBoxWidth(int i) {
        this.columnWidth = i;
        setLayoutParams(new LinearLayout.LayoutParams(this.columnWidth, this.columnHeight));
        float f = this.dayLabelSize + (this.padding * 2.0f);
        this.top = f;
        int i2 = this.columnHeight - 1;
        this.bottom = i2;
        this.boxHeight = (int) (i2 - f);
    }

    public int getBoxHeight() {
        return this.boxHeight;
    }

    public int getBoxTop() {
        return (int) this.top;
    }

    public Time getDay() {
        return this.day;
    }

    public boolean update(Time time, boolean z) {
        this.allowedToDraw = z;
        Time time2 = new SafeTime(time.timezone);
        this.day = time2;
        time2.set(time);
        long millis = this.day.toMillis(false);
        this.mJulianDay = Time.getJulianDay(millis, this.day.gmtoff);
        this.label = getLabelStrings(millis);
        this.dayNumber = String.valueOf(this.day.monthDay);
        this.dayLunarNumber = getLunarString(new Date(millis));
        updateSelectedDate();
        this.mSelectedDate.normalize(false);
        this.backgroundColor = 0;
        WeatherService.getInstance().requestWeather(getContext(), new WeatherResultHandler(this), 0, false);
        return Utils.isDateToday(time);
    }

    public void onClick(View view) {
        ((LaunchActivity) getContext()).switchItem(3, this.day.toMillis(false), false);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.allowedToDraw) {
            updateSelectedDate();
            drawBox(canvas, this.backgroundColor);
            new SafeTime(this.day.timezone).setToNow();
            boolean zAreDatesEqual = Utils.areDatesEqual(this.mSelectedDate, this.day);
            int foregroundColor = getForegroundColor();
            Context context = getContext();
            Time time = this.day;
            int eventDotColor = UiUtils.getEventDotColor(context, time, Utils.isDateToday(time), zAreDatesEqual && !this.isWeekView);
            Context context2 = getContext();
            Time time2 = this.day;
            int dateBoxForegroundColor = UiUtils.getDateBoxForegroundColor(context2, time2, false, false, Utils.isDateToday(time2));
            drawDayLabel(canvas, foregroundColor);
            drawDayNumber(canvas, dateBoxForegroundColor, dateBoxForegroundColor);
            if (!this.mIsTablet) {
                float f = this.dayLabelSize + (this.dayNumberSize * EVENTDOT_Y_OFFSET);
                float width = getWidth() * getResources().getFraction(R.fraction.navigation_number_position_week, 1, 1);
                if (UiUtils.isLandscape(getContext()) && this.isLunarAvailable) {
                    f -= 8.0f;
                }
                boolean z = this.mIsPhoneCondensedLook;
                if (z) {
                    f = this.dayLabelSize + this.dayNumberSize;
                }
                float f2 = f;
                if (z) {
                    return;
                }
                UiUtils.drawEventDot(this.mJulianDay, canvas, this.mPointPaint, width, f2, eventDotColor);
                float dimension = getResources().getDimension(R.dimen.month_view_tablet_grid_padding);
                this.boxPaint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
                canvas.drawLine(0.0f, dimension, getWidth(), dimension, this.boxPaint);
                return;
            }
            drawWeatherIcon(canvas);
        }
    }

    protected void drawBox(Canvas canvas, int i) {
        this.boxPaint.setColor(i);
        canvas.drawRect(0.0f, this.top, getWidth(), this.bottom, this.boxPaint);
        float f = this.isR2L ? this.columnWidth - 1.0f : 0.0f;
        this.boxPaint.setStrokeWidth(1.0f);
        this.boxPaint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
        if (hasFocus()) {
            canvas.drawRect(0.0f, this.top, getWidth(), this.bottom, this.borderPaint);
        }
        if (this.mIsTablet) {
            canvas.drawLine(f, this.top, f, this.bottom, this.boxPaint);
            canvas.drawLine(0.0f, this.top, getWidth(), this.top, this.boxPaint);
            canvas.drawLine(0.0f, this.bottom, getWidth(), this.bottom, this.boxPaint);
            this.boxPaint.setColor(ContextCompat.getColor(getContext(), R.color.month_view_background_color));
            canvas.drawRect(0.0f, 0.0f, getWidth(), this.top - 1.0f, this.boxPaint);
            this.boxPaint.setColor(i);
        }
    }

    protected void drawDayLabel(Canvas canvas, int i) {
        this.textPaint.setColor(i);
        this.textPaint.setTextSize(this.dayLabelSize);
        if (UiUtils.isLandscape(getContext())) {
            if (!UiUtils.shouldUseOneLetterForDay(getResources())) {
                this.textPaint.setTextSize(this.dayLabelSize * PHONE_LANDSCAPE_TWO_LETTERS_SCALE);
            }
            float f = this.dayLabelSize * PHONE_LANDSCAPE_SCALE;
            this.textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(this.label, this.padding * 2.0f, f, this.textPaint);
            return;
        }
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(this.label, getWidth() / 2, this.dayLabelSize + this.padding, this.textPaint);
    }

    protected void drawDayNumber(Canvas canvas, int i, int i2) {
        int width;
        this.textPaint.setTextSize(this.dayNumberSize);
        this.textPaint.setColor(i);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        if (this.mIsTablet) {
            if (UiUtils.isLandscape(getContext())) {
                String str = this.dayNumber;
                if (this.isR2L) {
                    width = ((getWidth() / 2) - (getWidth() / 4)) - (getWidth() / 16);
                } else {
                    width = (getWidth() / 2) + (getWidth() / 4) + (getWidth() / 16);
                }
                canvas.drawText(str, width, this.dayLabelSize + this.dayNumberSize + (this.padding * WEEK_NUMBER_Y_OFFSET_TABLET), this.textPaint);
            } else {
                canvas.drawText(this.dayNumber, this.isR2L ? (getWidth() / 2) - (getWidth() / 4) : (getWidth() / 2) + (getWidth() / 4), this.dayLabelSize + (this.dayNumberSize * DAY_NUMBER_SIZE_OFFSET_TABLET) + (this.padding * WEEK_NUMBER_Y_OFFSET_TABLET), this.textPaint);
            }
        } else {
            float width2 = getWidth() * getResources().getFraction(R.fraction.navigation_number_position_week, 1, 1);
            if (UiUtils.isSub320dpScreen(getContext())) {
                if (UiUtils.isLandscape(getContext())) {
                    canvas.drawText(this.dayNumber, width2, this.dayNumberSize * PHONE_LANDSCAPE_SCALE, this.textPaint);
                } else {
                    canvas.drawText(this.dayNumber, getWidth() / 2, (((getHeight() - this.padding) - this.dayLunarNumberSize) - ((((getHeight() - (this.padding * 2.0f)) - this.dayLunarNumberSize) - this.dayLabelSize) / 2.0f)) + (this.dayNumberSize / 2.0f), this.textPaint);
                }
            } else if (UiUtils.isLandscape(getContext())) {
                canvas.drawText(this.dayNumber, width2, this.dayNumberSize * PHONE_LANDSCAPE_SCALE, this.textPaint);
            } else {
                canvas.drawText(this.dayNumber, width2, this.dayLabelSize + this.dayNumberSize + (this.mDayNumberPaddingTop * 4.0f), this.textPaint);
            }
        }
        if (this.isLunarAvailable) {
            this.textPaint.setColor(i2);
            this.textPaint.setTextSize(this.dayLunarNumberSize);
            canvas.drawText(this.dayLunarNumber, getWidth() / 2, getHeight() - this.padding, this.textPaint);
            drawChinaHolidays(canvas);
        }
    }

    private void drawChinaHolidays(Canvas canvas) {
        int right;
        int boxTop;
        int color;
        String string;
        int width;
        int width2;
        int width3;
        int width4;
        int i;
        float dimension = getResources().getDimension(R.dimen.month_view_phone_lunar_circle_radius);
        float todayMarkerStrokeWidth = UiUtils.getTodayMarkerStrokeWidth(getContext());
        if (this.mIsTablet) {
            if (UiUtils.isLandscape(getContext())) {
                if (CalendarApplication.isR2L(getContext().getResources())) {
                    width3 = (getWidth() / 2) + (getWidth() / 4);
                    width4 = getWidth() / 16;
                    i = width3 + width4;
                } else {
                    width = (getWidth() / 2) - (getWidth() / 4);
                    width2 = getWidth() / 16;
                    i = width - width2;
                }
            } else if (CalendarApplication.isR2L(getContext().getResources())) {
                width3 = getWidth() / 2;
                width4 = getWidth() / 4;
                i = width3 + width4;
            } else {
                width = getWidth() / 2;
                width2 = getWidth() / 4;
                i = width - width2;
            }
            right = getRight() - i;
            boxTop = (int) (getBoxTop() + dimension + (getContext().getResources().getDimension(R.dimen.month_view_tablet_day_number_padding_top) / 2.0f));
            if (this.isToday) {
                dimension -= todayMarkerStrokeWidth / 2.0f;
            }
        } else {
            boolean zIsLunarAvailable = LunarAvailabilityManager.isLunarAvailable(getContext());
            if (UiUtils.isLandscape(getContext())) {
                dimension /= PHONE_LANDSCAPE_SCALE;
                if (this.isToday) {
                    dimension -= todayMarkerStrokeWidth / 2.0f;
                }
                right = ((int) ((getRight() - getLeft()) * getContext().getResources().getFraction(R.fraction.navigation_number_position_week, 1, 1))) + 20;
                int boxTop2 = getBoxTop();
                int boxTop3 = boxTop2 - (boxTop2 / 5);
                if (zIsLunarAvailable) {
                    boxTop3 = getBoxTop() - 5;
                }
                boxTop = boxTop3;
            } else {
                if (this.isToday) {
                    dimension -= todayMarkerStrokeWidth / 2.0f;
                }
                right = (((getRight() - 1) - (getLeft() - 6)) / 2) + 4;
                int boxHeight = (getBoxHeight() / 2) - 12;
                if (UiUtils.isSub320dpScreen(getContext())) {
                    boxHeight -= 5;
                }
                int boxTop4 = getBoxTop() + 3;
                if (zIsLunarAvailable) {
                    boxTop4 = getBoxTop() - 4;
                }
                boxTop = boxTop4 + boxHeight;
            }
        }
        ChinaHolidaysManager chinaHolidaysManager = ChinaHolidaysManager.getInstance(getContext());
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.day.year, this.day.month, this.day.monthDay);
        int holidayType = chinaHolidaysManager.getHolidayType(calendar);
        if (holidayType == 0) {
            color = ContextCompat.getColor(getContext(), R.color.color_china_holiday);
            string = getResources().getString(R.string.str_holiday);
        } else {
            if (holidayType != 1) {
                return;
            }
            color = ContextCompat.getColor(getContext(), R.color.color_china_workday);
            string = getResources().getString(R.string.str_workday);
        }
        float dimension2 = getResources().getDimension(R.dimen.chinaholiday_circle_fill_radius);
        float dimension3 = getResources().getDimension(R.dimen.chinaholiday_circle_stroke_width);
        float dimension4 = getResources().getDimension(R.dimen.chinaholiday_circle_draw_delta_x) - 4.0f;
        float dimension5 = getResources().getDimension(R.dimen.chinaholiday_circle_draw_delta_y) - 4.0f;
        float dimension6 = getResources().getDimension(R.dimen.chinaholiday_circle_text_size);
        float dimension7 = getResources().getDimension(R.dimen.chinaholiday_circle_text_y_delta);
        float f = dimension2 + dimension3;
        float f2 = ((right + dimension) - f) + dimension4;
        float f3 = ((boxTop - dimension) + f) - dimension5;
        this.textPaint.setColor(color);
        this.textPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(f2, f3, dimension2, this.textPaint);
        int color2 = ContextCompat.getColor(getContext(), R.color.color_china_holiday_circle_stroke);
        this.textPaint.setStrokeWidth(dimension3);
        this.textPaint.setColor(color2);
        this.textPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(f2, f3, dimension2 + (dimension3 / 2.0f), this.textPaint);
        this.textPaint.setColor(-1);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTextSize(dimension6);
        this.textPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(string, f2, f3 + dimension7, this.textPaint);
    }

    protected void drawWeatherIcon(Canvas canvas) {
        if (this.mWeatherBitmap.scaledBitmap != null) {
            canvas.drawBitmap(this.mWeatherBitmap.scaledBitmap, this.isR2L ? this.padding * 2.0f : this.columnWidth - this.mWeatherBitmap.scaledBitmap.getWidth(), this.weatherYPos, this.mWeatherBitmap.getPaint(isDaySelected()));
        }
    }

    protected void initPaint() {
        Paint paint = new Paint();
        this.boxPaint = paint;
        paint.setStrokeWidth(1.0f);
        this.boxPaint.setAntiAlias(true);
        this.mPointPaint = new Paint();
        TextPaint textPaint = new TextPaint();
        this.textPaint = textPaint;
        textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTypeface(Typeface.create(getResources().getString(R.string.roboto_font), 0));
        Paint paint2 = new Paint();
        this.borderPaint = paint2;
        paint2.setColor(ContextCompat.getColor(getContext(), R.color.focused_border_color));
        this.borderPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.focused_border_width));
        this.borderPaint.setStyle(Paint.Style.STROKE);
    }

    protected void initMeasures() {
        float dimension;
        this.mIsPhoneCondensedLook = UiUtils.isPhoneLandscape(getContext()) || UiUtils.isSub320dpScreen(getContext());
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.navigation_view_column_height);
        this.columnHeight = dimensionPixelSize;
        if (this.mIsPhoneCondensedLook) {
            float dimension2 = getResources().getDimension(R.dimen.week_navigation_view_text_size);
            this.dayLabelSize = dimension2;
            this.dayNumberSize = dimension2;
        } else {
            this.dayLabelSize = dimensionPixelSize * DAY_LABEL_SIZE;
            if (this.isLunarAvailable) {
                dimension = getResources().getDimension(R.dimen.month_view_digit_size_lunar_available);
            } else {
                dimension = getResources().getDimension(R.dimen.month_view_digit_size);
            }
            this.dayNumberSize = dimension;
        }
        int i = this.columnHeight;
        this.dayLunarNumberSize = i * DAY_LUNAR_NUMBER_SIZE;
        this.padding = i * PADDING;
        this.weatherYPos = this.dayNumberSize + this.dayLabelSize;
        if (this.isLunarAvailable) {
            this.mDayNumberPaddingTop = getResources().getDimension(R.dimen.month_view_lunar_day_number_padding_top);
        } else {
            this.mDayNumberPaddingTop = getResources().getDimension(R.dimen.month_view_day_number_padding_top);
        }
    }

    protected String getLabelStrings(long j) {
        SimpleDateFormat simpleDateFormat;
        if (UiUtils.shouldUseOneLetterForDay(getResources())) {
            simpleDateFormat = new SimpleDateFormat(Utils.FORMAT_WEEK_DAY_ONE_LETTER, Locale.getDefault());
        } else {
            simpleDateFormat = new SimpleDateFormat("c", Locale.getDefault());
        }
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Utils.getTimeZone(getContext(), null)));
        return simpleDateFormat.format(Long.valueOf(j)).toUpperCase(Locale.getDefault());
    }

    private int getForegroundColor() {
        if (FreeDayService.getInstance().isFreeDay(this.day.year, this.day.month, this.day.monthDay)) {
            return FreeDayService.getInstance().getHolidayColor(getContext());
        }
        if (this.isToday) {
            return UiUtils.getPrimaryColor(getContext());
        }
        TypedArray typedArrayObtainStyledAttributes = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
        int color = typedArrayObtainStyledAttributes.getColor(0, ContextCompat.getColor(getContext(), R.color.calendar_foreground));
        typedArrayObtainStyledAttributes.recycle();
        return color;
    }

    private static class WeatherResultHandler implements IAsyncServiceResultHandler {
        private final WeakReference<WeekNavigatorDayView> weekNavigatorDayViewWeakReference;

        private WeatherResultHandler(WeekNavigatorDayView weekNavigatorDayView) {
            this.weekNavigatorDayViewWeakReference = new WeakReference<>(weekNavigatorDayView);
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            WeekNavigatorDayView weekNavigatorDayView = this.weekNavigatorDayViewWeakReference.get();
            if (weekNavigatorDayView == null || !((Boolean) obj).booleanValue()) {
                return;
            }
            weekNavigatorDayView.weatherInfo = WeatherService.getInstance().getWeatherInfo(weekNavigatorDayView.day);
            if (weekNavigatorDayView.mWeatherBitmap.createScaledBitmap(weekNavigatorDayView.getContext(), weekNavigatorDayView.weatherInfo, true)) {
                weekNavigatorDayView.invalidate();
            }
        }
    }

    private String getLunarString(Date date) {
        return LunarUtils.getLunarDayString(LunarUtils.convertSolarDateToLunarDate(date));
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        initMeasures();
    }

    private void updateSelectedDate() {
        Time time = new SafeTime(this.day.timezone);
        this.mSelectedDate = time;
        time.set(Utils.getDisplayTime());
    }
}
