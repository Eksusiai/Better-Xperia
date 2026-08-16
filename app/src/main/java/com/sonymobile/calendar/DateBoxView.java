package com.sonymobile.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonyericsson.calendar.util.WeatherService;
import com.sonymobile.calendar.holiday.ChinaHolidaysManager;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.tablet.TextMonthEvents;
import com.sonymobile.calendar.utils.EventUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.weather.WeatherBitmap;
import com.sonymobile.lunar.lib.LunarUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

/* JADX INFO: loaded from: classes2.dex */
public class DateBoxView extends View implements View.OnCreateContextMenuListener {
    public static final double ALPHA_COEF = 1.2E-6d;
    private static final long DOUBLE_PRESS_MILLIS_INTERVAL = 300;
    public static final int FRAMES_PER_SECOND = 60;
    private static final float GRID_LINE_WIDTH = 1.0f;
    private static final float LUNAR_DAY_NUMBER_SIZE = 0.18f;
    private static final int LUNAR_STRING_RESET_GAP = 5000;
    private static final float PADDING = 0.05f;
    private static final String TAG = "DateBoxView";
    private static final float WEATHER_ICON_PADDING = 0.3f;
    private Time day;
    private String dayNumber;
    private GestureDetector doubleTapDetector;
    private CalendarEventNavigator eventNavigator;
    private FocusedViewNavigator focusedViewNavigator;
    private int index;
    private boolean isDisabled;
    private boolean isLunarAvailable;
    private boolean isR2L;
    private boolean isSelected;
    private boolean isToday;
    private int julianDay;
    private float lunarDayNumberSize;
    private float lunarLabelAvailableWidth;
    private Activity mActivity;
    private int mAlpha;
    private boolean mAnimate;
    private float mCircleRadius;
    private float mCircleXPos;
    private float mCircleYPos;
    private float mDayNumberPaddingTop;
    private float mDayNumberSize;
    private long mEnterMillisOne;
    private boolean mIsLandscape;
    private boolean mIsPhonePortrait;
    private boolean mIsTablet;
    private long mLastLunarTextRefreshTime;
    private Handler mMutliLunarStringHandler;
    private int mShowingLunarIndex;
    private long mTimeStep;
    private int mTmpLunarStringListSize;
    private WeatherBitmap mWeatherBitmap;
    private MonthEventsBase monthEvents;
    private int eventDefaultColor;
    private float padding;
    private Paint paint;
    private OnDateBoxSelectedListener selectionListener;
    private TextPaint textPaint;
    private float weatherYPos;

    public DateBoxView(Context context, int i, FocusedViewNavigator focusedViewNavigator) {
        super(context);
        float dimension;
        this.isSelected = false;
        this.isLunarAvailable = false;
        this.mAlpha = 0;
        this.mMutliLunarStringHandler = new Handler();
        this.mEnterMillisOne = SystemClock.elapsedRealtime();
        this.mActivity = (Activity) context;
        this.isLunarAvailable = LunarAvailabilityManager.isLunarAvailable(context);
        this.index = i;
        this.focusedViewNavigator = focusedViewNavigator;
        this.isR2L = CalendarApplication.isR2L(getResources());
        this.mIsLandscape = UiUtils.isLandscape(getContext()) && !UiUtils.isSub320dpScreen(getContext());
        this.mIsTablet = Utils.isTabletDevice(getContext());
        this.mIsPhonePortrait = UiUtils.isPhonePortrait(getContext()) || UiUtils.isSub320dpScreen(getContext());
        this.monthEvents = (this.mIsTablet || this.mIsLandscape) ? new TextMonthEvents(context) : new MonthEvents(context);
        setOnCreateContextMenuListener(this);
        this.doubleTapDetector = new GestureDetector(context, new OnDoubleTapListener());
        initPaint();
        setFocusable(true);
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.date_box_view_background));
        if (this.isLunarAvailable) {
            dimension = getResources().getDimension(R.dimen.month_view_digit_size_lunar_available);
        } else {
            dimension = getResources().getDimension(R.dimen.month_view_digit_size);
        }
        this.mDayNumberSize = dimension;
        if (this.isLunarAvailable) {
            this.mDayNumberPaddingTop = getResources().getDimension(R.dimen.month_view_lunar_day_number_padding_top);
        } else {
            this.mDayNumberPaddingTop = getResources().getDimension(R.dimen.month_view_day_number_padding_top);
        }
    }

    public void setOnDateBoxSelectedListener(OnDateBoxSelectedListener onDateBoxSelectedListener) {
        this.selectionListener = onDateBoxSelectedListener;
    }

    public void setEventNavigator(CalendarEventNavigator calendarEventNavigator) {
        this.eventNavigator = calendarEventNavigator;
    }

    public void setIsSelected(boolean z) {
        this.mAnimate = true;
        setIsSelected(z, false);
    }

    @Override // android.view.View
    public boolean isSelected() {
        return this.isSelected;
    }

    public boolean isDisabled() {
        return this.isDisabled;
    }

    public Time getDate() {
        return this.day;
    }

    public void update(Time time, Time time2, boolean z) {
        this.mAnimate = z;
        Time time3 = this.day;
        if (time3 != null && time3.year == time.year && this.day.month == time.month && this.day.monthDay == time.monthDay) {
            updateIsSelected(time);
            return;
        }
        Time time4 = new SafeTime(time.timezone);
        this.day = time4;
        time4.set(time);
        this.julianDay = Time.getJulianDay(this.day.normalize(false), this.day.gmtoff);
        this.dayNumber = String.valueOf(this.day.monthDay);
        if (time2 != null) {
            this.isDisabled = (time2.year == this.day.year && time2.month == this.day.month) ? false : true;
        }
        updateIsSelected(time);
        this.isToday = Utils.isDateToday(time);
        updateContentDescription(time);
        initWeatherBitmap();
        WeatherService.getInstance().requestWeather(getContext(), new WeatherResultHandler(this), 0, false);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.doubleTapDetector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        Time time = new SafeTime(Utils.getTimeZone(getContext(), null));
        time.set(this.day.toMillis(true));
        final long jCorrectSelectedDateTimeWithCurrent = Utils.correctSelectedDateTimeWithCurrent(time);
        contextMenu.setHeaderTitle(Utils.formatDateRange(getContext(), jCorrectSelectedDateTimeWithCurrent, jCorrectSelectedDateTimeWithCurrent, 18));
        CalendarContextMenuHelper.addCreateEventItem(contextMenu, new MenuItem.OnMenuItemClickListener() { // from class: com.sonymobile.calendar.DateBoxView.1
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public boolean onMenuItemClick(MenuItem menuItem) {
                DateBoxView.this.eventNavigator.goToCreateEvent(jCorrectSelectedDateTimeWithCurrent, false);
                return true;
            }
        });
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawBackgroundIfNeeded(canvas);
        drawEdges(canvas);
        if (this.isToday && !this.isSelected) {
            drawTodayMarker(canvas);
        }
        int dateBoxForegroundColor = UiUtils.getDateBoxForegroundColor(getContext(), this.day, this.isDisabled, this.isSelected, this.isToday);
        if (this.isDisabled) {
            drawDayNumber(canvas, dateBoxForegroundColor);
            if (this.isLunarAvailable) {
                drawLunarDayNumber(canvas, dateBoxForegroundColor);
                drawChinaHolidays(canvas);
                return;
            }
            return;
        }
        drawDayNumber(canvas, dateBoxForegroundColor);
        if (this.isLunarAvailable) {
            drawLunarDayNumber(canvas, dateBoxForegroundColor);
            drawChinaHolidays(canvas);
        }
        if (this.mIsTablet) {
            drawWeatherIcon(canvas);
        }
        drawEvents(canvas);
    }

    protected class ViewInvalidator implements Runnable {
        protected ViewInvalidator() {
        }

        @Override // java.lang.Runnable
        public void run() {
            while (true) {
                if ((DateBoxView.this.mAlpha >= 255 || !DateBoxView.this.isSelected) && (DateBoxView.this.mAlpha <= 0 || DateBoxView.this.isSelected)) {
                    return;
                }
                try {
                    Thread.sleep(16L);
                } catch (InterruptedException unused) {
                    Log.w(DateBoxView.TAG, "Thread interrupted");
                }
                long jNanoTime = System.nanoTime();
                long j = jNanoTime - DateBoxView.this.mTimeStep;
                DateBoxView.this.mTimeStep = jNanoTime;
                if (DateBoxView.this.isSelected) {
                    DateBoxView dateBoxView = DateBoxView.this;
                    dateBoxView.mAlpha = Math.min(dateBoxView.mAlpha + ((int) (j * 1.2E-6d)), 255);
                } else {
                    DateBoxView dateBoxView2 = DateBoxView.this;
                    dateBoxView2.mAlpha = Math.max(dateBoxView2.mAlpha - ((int) (j * 1.2E-6d)), 0);
                }
                DateBoxView.this.mActivity.runOnUiThread(new Runnable() { // from class: com.sonymobile.calendar.DateBoxView.ViewInvalidator.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DateBoxView.this.invalidate();
                    }
                });
            }
        }
    }

    private void drawBackgroundIfNeeded(Canvas canvas) {
        int dateBoxBackgroundColor = UiUtils.getDateBoxBackgroundColor(getContext(), this.day);
        if (this.isSelected || this.mAlpha > 0) {
            this.paint.setColor(dateBoxBackgroundColor);
            this.paint.setAlpha(Utils.interpolate(this.mAlpha));
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(this.mCircleXPos, this.mCircleYPos, this.mCircleRadius, this.paint);
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        initMeasures();
    }

    private void initPaint() {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        TextPaint textPaint = new TextPaint();
        this.textPaint = textPaint;
        textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTypeface(Typeface.create(getResources().getString(R.string.roboto_font), 0));
        this.eventDefaultColor = ContextCompat.getColor(getContext(), R.color.month_default_event_color);
    }

    private void initMeasures() {
        float f;
        float f2;
        int width = getWidth();
        float height = getHeight();
        this.lunarDayNumberSize = Math.min(LUNAR_DAY_NUMBER_SIZE * height, getResources().getDimension(R.dimen.month_view_lunar_day_text_size));
        this.padding = PADDING * height;
        initCircleMeasures();
        boolean z = this.mIsTablet;
        if (z) {
            this.weatherYPos = this.padding;
        } else {
            this.weatherYPos = this.mIsLandscape ? this.padding - (this.mDayNumberSize * 0.3f) : this.mDayNumberSize;
        }
        if (this.mIsLandscape) {
            f = width - (this.padding * 2.0f);
            f2 = this.mCircleRadius;
        } else {
            f = width;
            f2 = this.padding;
        }
        this.lunarLabelAvailableWidth = f - (f2 * 2.0f);
        this.monthEvents.scaleToSize(width, height, z);
    }

    private void initCircleMeasures() {
        int width;
        int width2;
        int width3;
        int width4;
        if (this.mIsPhonePortrait) {
            this.mCircleYPos = this.mDayNumberSize + (this.mDayNumberPaddingTop / 3.0f);
            this.mCircleXPos = getWidth() / 2;
            this.mCircleRadius = getResources().getDimension(R.dimen.month_view_portrait_circle_radius);
        } else {
            this.mCircleYPos = this.mDayNumberSize + (this.mDayNumberPaddingTop / 2.0f);
            if (this.isR2L) {
                if (this.mIsLandscape && this.mIsTablet) {
                    width3 = (getWidth() / 2) - (getWidth() / 4);
                    width4 = getWidth() / 16;
                } else {
                    width3 = getWidth() / 2;
                    width4 = getWidth() / 4;
                }
                this.mCircleXPos = width3 - width4;
            } else {
                if (this.mIsLandscape && this.mIsTablet) {
                    width = (getWidth() / 2) + (getWidth() / 4);
                    width2 = getWidth() / 16;
                } else {
                    width = getWidth() / 2;
                    width2 = getWidth() / 4;
                }
                this.mCircleXPos = width + width2;
            }
            if (this.mIsTablet) {
                this.mCircleRadius = getResources().getDimension(R.dimen.month_view_tablet_circle_radius);
            } else {
                this.mCircleRadius = getResources().getDimension(R.dimen.month_view_phone_landscape_circle_radius);
            }
        }
        if (this.isLunarAvailable && this.mIsPhonePortrait) {
            this.mCircleRadius = getResources().getDimension(R.dimen.month_view_phone_lunar_circle_radius);
        }
    }

    private void initWeatherBitmap() {
        this.mWeatherBitmap = new WeatherBitmap();
    }

    private void updateContentDescription(Time time) {
        long millis = time.toMillis(false);
        setContentDescription(Utils.formatDateRange(getContext(), millis, millis, 18));
    }

    private void updateIsSelected(Time time) {
        Time time2 = new SafeTime(time.timezone);
        time2.set(Utils.getDisplayTime());
        if (Utils.areDatesEqual(time, time2)) {
            setIsSelected(true, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIsSelected(boolean z, boolean z2) {
        if (!this.isDisabled) {
            this.isSelected = z;
            this.mTimeStep = System.nanoTime();
            if (this.mAnimate) {
                Thread thread = new Thread(new ViewInvalidator());
                thread.setPriority(1);
                thread.start();
            } else {
                this.mAlpha = z ? 255 : 0;
            }
            if (z) {
                requestFocus();
            }
        }
        OnDateBoxSelectedListener onDateBoxSelectedListener = this.selectionListener;
        if (onDateBoxSelectedListener != null) {
            onDateBoxSelectedListener.onDateSelected(this, z2);
        }
    }

    private void drawEdges(Canvas canvas) {
        this.paint.setStrokeWidth(1.0f);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(ContextCompat.getColor(getContext(), R.color.calendar_grid_line));
        if (this.mIsTablet) {
            float width = this.isR2L ? getWidth() - 1.0f : 1.0f;
            canvas.drawLine(width, 0.0f, width, getHeight(), this.paint);
        }
        if (this.mIsPhonePortrait) {
            return;
        }
        canvas.drawLine(0.0f, 1.0f, getWidth(), 1.0f, this.paint);
    }

    private void drawDayNumber(Canvas canvas, int i) {
        int width;
        int width2;
        float f;
        int width3;
        int width4;
        float f2;
        this.textPaint.setTextSize(this.mDayNumberSize);
        this.textPaint.setColor(i);
        if (this.mIsPhonePortrait) {
            canvas.drawText(this.dayNumber, getWidth() / 2, (this.mDayNumberPaddingTop * 2.0f) + this.mDayNumberSize, this.textPaint);
            return;
        }
        if (this.isR2L) {
            String str = this.dayNumber;
            if (this.mIsLandscape && this.mIsTablet) {
                width3 = (getWidth() / 2) - (getWidth() / 4);
                width4 = getWidth() / 16;
            } else {
                width3 = getWidth() / 2;
                width4 = getWidth() / 4;
            }
            float f3 = width3 - width4;
            if (this.mIsPhonePortrait) {
                f2 = this.mDayNumberPaddingTop + this.mDayNumberSize;
            } else {
                f2 = this.mDayNumberSize + (this.mDayNumberPaddingTop * 2.0f);
            }
            canvas.drawText(str, f3, f2, this.textPaint);
            return;
        }
        String str2 = this.dayNumber;
        if (this.mIsLandscape && this.mIsTablet) {
            width = (getWidth() / 2) + (getWidth() / 4);
            width2 = getWidth() / 16;
        } else {
            width = getWidth() / 2;
            width2 = getWidth() / 4;
        }
        float f4 = width + width2;
        if (this.mIsPhonePortrait) {
            f = this.mDayNumberPaddingTop + this.mDayNumberSize;
        } else {
            f = this.mDayNumberSize + (this.mDayNumberPaddingTop * 2.0f);
        }
        canvas.drawText(str2, f4, f, this.textPaint);
    }

    private void drawEvents(Canvas canvas) {
        if (this.mIsPhonePortrait) {
            UiUtils.drawEventDot(this.julianDay, canvas, this.paint, this.mCircleXPos, this.mCircleYPos + (this.mDayNumberSize / 2.0f) + this.padding, UiUtils.getEventDotColor(getContext(), this.day, this.isToday, this.isSelected));
            drawEventTextsPortrait(canvas);
        } else {
            this.monthEvents.drawEvents(getContext(), this.julianDay, this.day.timezone, canvas, this.paint, getWidth(), getHeight(), this.mDayNumberSize + (this.padding * 3.0f), this.lunarDayNumberSize, getEventColor(), this.isR2L);
        }
    }

    private void drawEventTextsPortrait(Canvas canvas) {
        ArrayList<EventInfo> allDayEventsForDay = EventUtils.getAllDayEventsForDay(this.julianDay);
        ArrayList<EventInfo> eventsForDay = EventUtils.getEventsForDay(this.julianDay);
        if (allDayEventsForDay.isEmpty() && eventsForDay.isEmpty()) {
            return;
        }
        float f = getResources().getDimension(R.dimen.month_view_portrait_event_text_size);
        this.textPaint.setTextSize(f);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        float f2 = this.lunarDayNumberSize;
        float f3 = getHeight() - f2;
        float f4 = this.mCircleYPos + (this.mDayNumberSize / 2.0f) + (this.padding * 2.0f);
        if (f3 - f4 < f) {
            return;
        }
        float f5 = getWidth() - (this.padding * 4.0f);
        int i = (int) ((f3 - f4) / f);
        ArrayList<EventInfo> arrayList = new ArrayList<>();
        if (!allDayEventsForDay.isEmpty()) {
            arrayList.addAll(allDayEventsForDay);
        } else if (!eventsForDay.isEmpty()) {
            arrayList.add(eventsForDay.get(0));
        }
        int i2 = 0;
        int i3 = 0;
        while (i3 < arrayList.size() && i3 < i) {
            EventInfo eventInfo = arrayList.get(i3);
            int i4 = eventInfo.hasEventColor ? eventInfo.color : this.eventDefaultColor;
            this.textPaint.setColor(i4);
            String str = eventInfo.title;
            if (TextUtils.isEmpty(str)) {
                str = getResources().getString(R.string.no_title_label);
            }
            canvas.drawText(TextUtils.ellipsize(str, this.textPaint, f5, TextUtils.TruncateAt.END).toString(), this.mCircleXPos, f4 + (f * (i2 + 1)), this.textPaint);
            i2++;
            i3++;
        }
        this.textPaint.setColor(getEventColor());
        this.textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void drawWeatherIcon(Canvas canvas) {
        float width;
        float f;
        float f2;
        if (this.mWeatherBitmap.scaledBitmap != null) {
            if (this.mIsTablet) {
                if (this.isR2L) {
                    width = getWidth() - this.mWeatherBitmap.scaledBitmap.getWidth();
                    f = this.padding;
                    f2 = width - f;
                } else {
                    f2 = this.padding;
                }
            } else if (this.isR2L) {
                f2 = this.padding;
            } else {
                width = getWidth() - this.mWeatherBitmap.scaledBitmap.getWidth();
                f = this.padding;
                f2 = width - f;
            }
            canvas.drawBitmap(this.mWeatherBitmap.scaledBitmap, f2, this.weatherYPos, this.mWeatherBitmap.getPaint(false));
        }
    }

    private void drawTodayMarker(Canvas canvas) {
        this.paint.setColor(UiUtils.getTodayMarkerColor(getContext(), this.day));
        float todayMarkerStrokeWidth = UiUtils.getTodayMarkerStrokeWidth(getContext());
        this.paint.setStrokeWidth(todayMarkerStrokeWidth);
        canvas.drawCircle(this.mCircleXPos, this.mCircleYPos, this.mCircleRadius - (todayMarkerStrokeWidth / 2.0f), this.paint);
    }

    private int getEventColor() {
        if (this.isToday) {
            return UiUtils.getPrimaryTextColor(getContext());
        }
        return 0;
    }

    private class OnDoubleTapListener extends GestureDetector.SimpleOnGestureListener {
        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        private OnDoubleTapListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            DateBoxView.this.mAnimate = true;
            DateBoxView.this.setIsSelected(true, true);
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent motionEvent) {
            DateBoxView.this.openDayView();
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            DateBoxView.this.setIsSelected(true, true);
            if (DateBoxView.this.getParent() != null) {
                DateBoxView.this.performLongClick();
            }
        }
    }

    private static class WeatherResultHandler implements IAsyncServiceResultHandler {
        private final WeakReference<DateBoxView> dateBoxViewWeekReference;

        private WeatherResultHandler(DateBoxView dateBoxView) {
            this.dateBoxViewWeekReference = new WeakReference<>(dateBoxView);
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            DateBoxView dateBoxView = this.dateBoxViewWeekReference.get();
            if (dateBoxView == null || obj == null || !((Boolean) obj).booleanValue()) {
                return;
            }
            if (dateBoxView.mWeatherBitmap.createScaledBitmap(dateBoxView.getContext(), WeatherService.getInstance().getWeatherInfo(dateBoxView.day), true)) {
                dateBoxView.invalidate();
            }
        }
    }

    private void drawChinaHolidays(Canvas canvas) {
        int color;
        String string;
        ChinaHolidaysManager chinaHolidaysManager = ChinaHolidaysManager.getInstance(getContext());
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.day.year, this.day.month, this.day.monthDay);
        int holidayType = chinaHolidaysManager.getHolidayType(calendar);
        if (holidayType == 0) {
            if (this.isDisabled) {
                color = ContextCompat.getColor(getContext(), R.color.color_china_holiday_disable);
            } else {
                color = ContextCompat.getColor(getContext(), R.color.color_china_holiday);
            }
            string = getResources().getString(R.string.str_holiday);
        } else {
            if (holidayType != 1) {
                return;
            }
            if (this.isDisabled) {
                color = ContextCompat.getColor(getContext(), R.color.color_china_workday_disable);
            } else {
                color = ContextCompat.getColor(getContext(), R.color.color_china_workday);
            }
            string = getResources().getString(R.string.str_workday);
        }
        float dimension = getResources().getDimension(R.dimen.chinaholiday_circle_fill_radius);
        float dimension2 = getResources().getDimension(R.dimen.chinaholiday_circle_stroke_width);
        float dimension3 = getResources().getDimension(R.dimen.chinaholiday_circle_draw_delta_x);
        float dimension4 = getResources().getDimension(R.dimen.chinaholiday_circle_draw_delta_y);
        float dimension5 = getResources().getDimension(R.dimen.chinaholiday_circle_text_size);
        float dimension6 = getResources().getDimension(R.dimen.chinaholiday_circle_text_y_delta);
        float f = this.mCircleXPos;
        float f2 = this.mCircleRadius;
        float f3 = dimension + dimension2;
        float f4 = ((f + f2) - f3) + dimension3;
        float f5 = ((this.mCircleYPos - f2) + f3) - dimension4;
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(f4, f5, dimension, this.paint);
        int color2 = ContextCompat.getColor(getContext(), R.color.color_china_holiday_circle_stroke);
        this.paint.setStrokeWidth(dimension2);
        this.paint.setColor(color2);
        this.paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(f4, f5, dimension + (dimension2 / 2.0f), this.paint);
        this.paint.setColor(-1);
        this.paint.setTextAlign(Paint.Align.CENTER);
        this.paint.setTextSize(dimension5);
        this.paint.setStyle(Paint.Style.FILL);
        canvas.drawText(string, f4, f5 + dimension6, this.paint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        if (this.mLastLunarTextRefreshTime == 0 || System.currentTimeMillis() - this.mLastLunarTextRefreshTime >= 5000) {
            Log.d(TAG, "refresh: ");
            int i = this.mShowingLunarIndex;
            if (i == this.mTmpLunarStringListSize - 1) {
                this.mShowingLunarIndex = 0;
            } else {
                this.mShowingLunarIndex = i + 1;
            }
            invalidate();
            this.mLastLunarTextRefreshTime = System.currentTimeMillis();
        }
    }

    private void drawLunarDayNumber(Canvas canvas, int i) {
        String str;
        float height;
        float f;
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.day.year, this.day.month, this.day.monthDay);
        ArrayList<String> calendarMonthGridString = LunarUtils.getCalendarMonthGridString(calendar.getTime());
        if (calendarMonthGridString.size() == 1) {
            str = calendarMonthGridString.get(0);
        } else if (calendarMonthGridString.size() > 1) {
            this.mTmpLunarStringListSize = calendarMonthGridString.size();
            str = calendarMonthGridString.get(this.mShowingLunarIndex);
            this.mMutliLunarStringHandler.postDelayed(new Runnable() { // from class: com.sonymobile.calendar.DateBoxView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DateBoxView.this.refresh();
                }
            }, 5000L);
        } else {
            str = "";
        }
        String holidaySpecial = ChinaHolidaysManager.getInstance(getContext()).getHolidaySpecial(calendar);
        if (!TextUtils.isEmpty(holidaySpecial)) {
            str = holidaySpecial;
        }
        boolean zIsFreeDay = FreeDayService.getInstance().isFreeDay(this.day.year, this.day.month, this.day.monthDay);
        if (!this.isDisabled) {
            if (zIsFreeDay) {
                i = ContextCompat.getColor(getContext(), R.color.free_day_color);
            } else if (this.isToday) {
                i = UiUtils.getPrimaryTextColor(getContext());
            } else if (this.isSelected) {
                i = ContextCompat.getColor(getContext(), R.color.day_number_color);
            }
        }
        this.textPaint.setColor(i);
        this.textPaint.setTextSize(this.lunarDayNumberSize);
        CharSequence charSequenceEllipsize = TextUtils.ellipsize(str, this.textPaint, this.lunarLabelAvailableWidth, TextUtils.TruncateAt.END);
        if (this.mIsPhonePortrait) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float f2 = displayMetrics.heightPixels / displayMetrics.widthPixels;
            float height2 = getHeight() * PADDING;
            double d = f2;
            if (d > 1.8d && d <= 2.0d) {
                height = getHeight();
                f = 0.065f;
            } else {
                if (d > 2.0d) {
                    height = getHeight();
                    f = 0.2f;
                }
                canvas.drawText((String) charSequenceEllipsize, getWidth() / 2, getHeight() - height2, this.textPaint);
                return;
            }
            height2 = f * height;
            canvas.drawText((String) charSequenceEllipsize, getWidth() / 2, getHeight() - height2, this.textPaint);
            return;
        }
        this.textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText((String) charSequenceEllipsize, (this.mCircleXPos - this.mCircleRadius) - this.padding, this.mCircleYPos + (this.lunarDayNumberSize / 2.0f), this.textPaint);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private boolean doubleKeyPressEnter() {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j = jElapsedRealtime - this.mEnterMillisOne;
        this.mEnterMillisOne = jElapsedRealtime;
        return j < 300;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        keyEvent.startTracking();
        if (hasFocus()) {
            if (i == 21) {
                if (this.isR2L) {
                    if (isEndIndex()) {
                        goToNext();
                    }
                } else if (isStartIndex()) {
                    goToPrevious();
                }
                return false;
            }
            if (i == 22) {
                if (this.isR2L) {
                    if (isStartIndex()) {
                        goToPrevious();
                    }
                } else if (isEndIndex()) {
                    goToNext();
                }
                return false;
            }
            if (i == 62 || i == 66) {
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        if (hasFocus()) {
            if (i == 66) {
                if (getParent() != null) {
                    performLongClick();
                }
                return true;
            }
            if (i == 62) {
                goToNext();
                return true;
            }
        }
        return super.onKeyLongPress(i, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 66) {
            setIsSelected(true, true);
            if (doubleKeyPressEnter()) {
                openDayView();
            }
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openDayView() {
        try {
            ((LaunchActivity) getContext()).switchItem(3, this.day.toMillis(false), false);
        } catch (ClassCastException unused) {
            throw new ClassCastException("Usage of this view is only intended for LaunchActivity");
        }
    }

    private void goToNext() {
        FocusedViewNavigator focusedViewNavigator = this.focusedViewNavigator;
        if (focusedViewNavigator != null) {
            focusedViewNavigator.goToNextView();
        }
    }

    private void goToPrevious() {
        FocusedViewNavigator focusedViewNavigator = this.focusedViewNavigator;
        if (focusedViewNavigator != null) {
            focusedViewNavigator.goToPreviousView();
        }
    }

    private boolean isEndIndex() {
        return this.index % 7 == 6;
    }

    private boolean isStartIndex() {
        return this.index % 7 == 0;
    }
}
