package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.TypedValue;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.MonthEventsBase;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.utils.EventUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class TextMonthEvents extends MonthEventsBase {
    private static final int ALARM_ICON_INCREMENT = 10;
    private static final int ALARM_ICON_PADDING = 5;
    private static final float BOX_PADDING = 0.05f;
    private static final float EVENT_MARGIN_TOP = 0.07f;
    private static final int EVENT_TEXT_SIZE_DP = 12;
    private static final float EVENT_TIME_MARGIN_END = 0.05f;
    private static final float TIME_WIDTH_THRESHOLD = 0.25f;
    private static final float TIME_WIDTH_THRESHOLD_TABLET = 0.4f;
    private Drawable alarmIcon;
    private int alarmIconSize;
    private int alarmIconYPosition;
    private String allDayString;
    private float allDayStringWidth;
    private float boxPadding;
    private float eventHeight;
    private float eventMarginTop;
    private float eventTimeMarginEnd;
    private float eventTimeWidth;
    private String eventsRestString;
    private boolean isLunarAvailable;
    private TextPaint textPaint;
    private SimpleDateFormat timeFormat;
    private float timeThreshold;

    public TextMonthEvents(Context context) {
        super(context);
        this.isLunarAvailable = false;
        this.isLunarAvailable = LunarAvailabilityManager.isLunarAvailable(context);
    }

    @Override // com.sonymobile.calendar.MonthEventsBase
    public void scaleToSize(float f, float f2, boolean z) {
        float f3 = f * 0.05f;
        this.boxPadding = f3;
        this.eventTimeMarginEnd = f3;
        this.eventMarginTop = f2 * EVENT_MARGIN_TOP;
        this.timeThreshold = z ? TIME_WIDTH_THRESHOLD_TABLET : TIME_WIDTH_THRESHOLD;
    }

    @Override // com.sonymobile.calendar.MonthEventsBase
    protected void initDrawingResources(Context context) {
        super.initDrawingResources(context);
        TextPaint textPaint = new TextPaint();
        this.textPaint = textPaint;
        textPaint.setAntiAlias(true);
        int iApplyDimension = (int) TypedValue.applyDimension(2, 12.0f, context.getResources().getDisplayMetrics());
        this.alarmIconSize = iApplyDimension + 10;
        this.textPaint.setTextSize(iApplyDimension);
        this.eventsRestString = context.getResources().getString(R.string.month_subject_more_txt);
        this.allDayString = context.getResources().getString(R.string.calendar_agenda_all_day_event_txt);
        if (DateFormat.is24HourFormat(context)) {
            this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            this.timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        }
        this.timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String str = this.timeFormat.format(new Date(82800000L));
        Rect rect = new Rect();
        this.textPaint.getTextBounds(str, 0, str.length(), rect);
        this.eventTimeWidth = rect.width();
        float fHeight = rect.height();
        this.eventHeight = fHeight;
        this.alarmIconYPosition = (int) (fHeight + ((this.alarmIconSize - fHeight) / 2.0f));
        TextPaint textPaint2 = this.textPaint;
        String str2 = this.allDayString;
        textPaint2.getTextBounds(str2, 0, str2.length(), rect);
        this.allDayStringWidth = rect.width();
        this.alarmIcon = VectorDrawableCompat.create(context.getResources(), R.drawable.alarm_icon, context.getTheme());
    }

    @Override // com.sonymobile.calendar.MonthEventsBase
    public void drawEvents(Context context, int i, String str, Canvas canvas, Paint paint, int i2, int i3, float f, float f2, int i4, boolean z) {
        int size;
        float f3;
        float f4;
        float f5;
        boolean z2;
        float f6;
        this.timeFormat.setTimeZone(TimeZone.getTimeZone(str));
        float f7 = this.eventHeight;
        float f8 = this.eventMarginTop;
        float f9 = f7 + f8;
        float f10 = i3 - (f + (f8 * 2.0f));
        if (this.isLunarAvailable) {
            f10 -= f2;
        }
        float f11 = i2;
        float f12 = f11 - (this.boxPadding * 2.0f);
        int iFloor = (int) Math.floor(f10 / f9);
        ArrayList<EventInfo> allDayEventsForDay = EventUtils.getAllDayEventsForDay(i);
        ArrayList<EventInfo> eventsForDay = EventUtils.getEventsForDay(i);
        int size2 = (allDayEventsForDay.size() + eventsForDay.size()) - iFloor;
        if (size2 > 0) {
            size = iFloor - 1;
            size2++;
        } else {
            size = allDayEventsForDay.size() + eventsForDay.size();
        }
        int i5 = size;
        int i6 = size2;
        if (z) {
            this.textPaint.setTextAlign(Paint.Align.RIGHT);
            f3 = f11 - this.boxPadding;
        } else {
            f3 = this.boxPadding;
        }
        float f13 = f3;
        int i7 = i4 != 0 ? i4 : this.eventDefaultColor;
        this.textPaint.setColor(i7);
        float f14 = f + f9 + this.eventMarginTop;
        if (this.isLunarAvailable) {
            f14 += f2;
        }
        String string = context.getResources().getString(R.string.no_title_label);
        int i8 = 0;
        float f15 = f14;
        int i9 = 0;
        while (!allDayEventsForDay.isEmpty() && i9 < i5) {
            String str2 = allDayEventsForDay.remove(i8).title;
            drawTextEvent(canvas, i, f12, f13, f15, TextUtils.isEmpty(str2) ? string : str2, this.allDayString, this.allDayStringWidth, z, false);
            f15 += f9;
            i9++;
            i6 = i6;
            i5 = i5;
            i7 = i7;
            i8 = 0;
            f12 = f12;
            allDayEventsForDay = allDayEventsForDay;
        }
        int i10 = i7;
        int i11 = i5;
        float f16 = f12;
        int i12 = i6;
        float f17 = f16;
        float f18 = f13;
        float f19 = f15;
        int i13 = i9;
        while (!eventsForDay.isEmpty() && i13 < i11) {
            EventInfo eventInfoRemove = eventsForDay.remove(0);
            String str3 = eventInfoRemove.isAlarmEvent ? eventInfoRemove.description : eventInfoRemove.title;
            String str4 = TextUtils.isEmpty(str3) ? string : str3;
            if (eventInfoRemove.isAlarmEvent) {
                drawAlarmIcon(canvas, i10, f18, f19, z);
                if (z) {
                    f6 = f18 - (this.alarmIconSize + 5);
                } else {
                    f6 = f18 + this.alarmIconSize + 5;
                }
                f4 = f17 - this.alarmIconSize;
                f5 = f6;
                z2 = true;
            } else {
                f4 = f17;
                f5 = f18;
                z2 = false;
            }
            int i14 = i11;
            float f20 = f19;
            int i15 = i13;
            drawTextEvent(canvas, i, f4, f5, f19, str4, millisToTimeString(eventInfoRemove.localBegin), this.eventTimeWidth, z, z2);
            if (z2) {
                if (z) {
                    f5 += this.alarmIconSize + 5;
                } else {
                    f5 -= this.alarmIconSize + 5;
                }
                f4 += this.alarmIconSize;
            }
            f17 = f4;
            f18 = f5;
            f19 = f20 + f9;
            i13 = i15 + 1;
            i11 = i14;
        }
        float f21 = f19;
        if (i12 > 0) {
            drawMore(canvas, i12, f18, f21);
        }
    }

    private void drawTextEvent(Canvas canvas, int i, float f, float f2, float f3, CharSequence charSequence, String str, float f4, boolean z, boolean z2) {
        float f5 = this.eventTimeMarginEnd;
        if (f4 / f >= this.timeThreshold || z2) {
            f5 = 0.0f;
            f4 = 0.0f;
        } else {
            this.textPaint.setFakeBoldText(true);
            canvas.drawText(str, f2, f3, this.textPaint);
            this.textPaint.setFakeBoldText(false);
        }
        float f6 = f4 + f5;
        canvas.drawText(TextUtils.ellipsize(charSequence, this.textPaint, f - f6, TextUtils.TruncateAt.END).toString(), z ? f2 - f6 : f2 + f6, f3, this.textPaint);
    }

    private void drawAlarmIcon(Canvas canvas, int i, float f, float f2, boolean z) {
        Drawable drawable = this.alarmIcon;
        int i2 = this.alarmIconSize;
        drawable.setBounds(0, 0, i2, i2);
        this.alarmIcon.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_ATOP));
        if (z) {
            f -= this.alarmIconSize;
        }
        canvas.save();
        canvas.translate(f, f2 - this.alarmIconYPosition);
        this.alarmIcon.draw(canvas);
        canvas.restore();
    }

    private void drawMore(Canvas canvas, int i, float f, float f2) {
        canvas.drawText(String.format(this.eventsRestString, Integer.valueOf(i)), f, f2, this.textPaint);
    }

    private String millisToTimeString(long j) {
        return this.timeFormat.format(new Date(j));
    }
}
