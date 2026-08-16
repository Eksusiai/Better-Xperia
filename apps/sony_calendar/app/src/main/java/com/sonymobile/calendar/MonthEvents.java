package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.format.Time;
import com.sonyericsson.calendar.util.EventInfo;
import com.sonymobile.calendar.utils.EventUtils;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class MonthEvents extends MonthEventsBase {
    private static final float ALLDAYEVENT_LINE_WIDTH = 0.05f;
    private static final int EVENT_ALPHA = 102;
    private static final float EVENT_LINE_WIDTH = 0.3f;
    private static final float EVENT_VERTICAL_MARGIN = 0.05f;
    private static final int MINIMUM_EVENT_SIZE = 60;
    private static final int OVERRIDE_EVENT_ALPHA = 230;
    private int allDayColor;
    private float allDayEventLineWidth;
    private float eventLineWidth;
    private int[] eventMinuteColors;
    private float eventVerticalMargin;

    public MonthEvents(Context context) {
        super(context);
        this.allDayColor = this.eventDefaultColor;
    }

    @Override // com.sonymobile.calendar.MonthEventsBase
    public void scaleToSize(float f, float f2, boolean z) {
        this.eventVerticalMargin = f2 * 0.05f;
        this.eventLineWidth = 0.3f * f;
        this.allDayEventLineWidth = f * 0.05f;
    }

    @Override // com.sonymobile.calendar.MonthEventsBase
    public void drawEvents(Context context, int i, String str, Canvas canvas, Paint paint, int i2, int i3, float f, float f2, int i4, boolean z) {
        float f3;
        float f4;
        float f5 = this.eventVerticalMargin;
        float f6 = i3 - (f5 * 2.0f);
        boolean zPrepareAllDayEvent = prepareAllDayEvent(i);
        boolean zPrepareEvents = prepareEvents(i, str);
        float f7 = zPrepareAllDayEvent ? this.allDayEventLineWidth / 2.0f : 0.0f;
        float f8 = this.eventLineWidth / 2.0f;
        if (z) {
            float f9 = i2 - f7;
            f4 = (f9 - f7) - f8;
            f3 = f9;
        } else {
            f3 = f7;
            f4 = f7 + f7 + f8;
        }
        if (zPrepareAllDayEvent) {
            drawAllDayEvent(context, canvas, paint, f3, f5, f6, i4);
        }
        if (zPrepareEvents) {
            drawEvent(context, canvas, paint, f4, f5, f6, i4);
        }
    }

    private boolean prepareAllDayEvent(int i) {
        ArrayList<EventInfo> allDayEventsForDay = EventUtils.getAllDayEventsForDay(i);
        if (allDayEventsForDay.isEmpty()) {
            return false;
        }
        for (EventInfo eventInfo : allDayEventsForDay) {
            if (eventInfo.hasEventColor) {
                this.allDayColor = eventInfo.color;
                return true;
            }
        }
        return true;
    }

    private boolean prepareEvents(int i, String str) {
        ArrayList<EventInfo> eventsForDay = EventUtils.getEventsForDay(i);
        if (eventsForDay.isEmpty()) {
            return false;
        }
        Time time = new SafeTime(str);
        time.setJulianDay(i);
        long millis = time.toMillis(false);
        long j = 86400000 + millis;
        int[] iArr = new int[CalendarViewBase.MINUTES_PER_DAY];
        this.eventMinuteColors = iArr;
        Arrays.fill(iArr, 0);
        int length = this.eventMinuteColors.length - 1;
        for (EventInfo eventInfo : eventsForDay) {
            int iMillisToLocalMinutes = millisToLocalMinutes(eventInfo.localBegin, millis);
            int iMillisToLocalMinutes2 = (eventInfo.endDay > i || eventInfo.end >= j) ? length : millisToLocalMinutes(eventInfo.end, millis);
            if (iMillisToLocalMinutes2 - iMillisToLocalMinutes < 60 && iMillisToLocalMinutes2 != length) {
                iMillisToLocalMinutes2 = iMillisToLocalMinutes + 60;
            }
            if (iMillisToLocalMinutes2 > length) {
                iMillisToLocalMinutes2 = length;
            }
            if (iMillisToLocalMinutes <= 0) {
                iMillisToLocalMinutes = 0;
            }
            while (iMillisToLocalMinutes <= iMillisToLocalMinutes2) {
                int[] iArr2 = this.eventMinuteColors;
                if (iArr2[iMillisToLocalMinutes] == 0 || iArr2[iMillisToLocalMinutes] == this.eventDefaultColor) {
                    this.eventMinuteColors[iMillisToLocalMinutes] = eventInfo.hasEventColor ? eventInfo.color : this.eventDefaultColor;
                }
                iMillisToLocalMinutes++;
            }
        }
        return true;
    }

    private void drawAllDayEvent(Context context, Canvas canvas, Paint paint, float f, float f2, float f3, int i) {
        if (canvas == null) {
            return;
        }
        if (i != 0) {
            paint.setColor(i);
            paint.setAlpha(OVERRIDE_EVENT_ALPHA);
        } else {
            paint.setColor(this.allDayColor);
            paint.setAlpha(102);
        }
        paint.setStrokeWidth(this.allDayEventLineWidth);
        canvas.drawLine(f, f2, f, f2 + f3, paint);
    }

    private void drawEvent(Context context, Canvas canvas, Paint paint, float f, float f2, float f3, int i) {
        if (canvas == null) {
            return;
        }
        boolean z = i != 0;
        paint.setStrokeWidth(this.eventLineWidth);
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (true) {
            int[] iArr = this.eventMinuteColors;
            if (i5 >= iArr.length) {
                return;
            }
            if (i5 == iArr.length - 1 && iArr[i5] != 0) {
                i2++;
                iArr[i5] = 0;
            }
            int i6 = i2;
            if (iArr[i5] == i3) {
                i2 = i6 + 1;
            } else {
                if (i3 != 0) {
                    if (z) {
                        paint.setColor(i);
                        paint.setAlpha(OVERRIDE_EVENT_ALPHA);
                    } else {
                        paint.setColor(i3);
                        paint.setAlpha(102);
                    }
                    drawEventLine(canvas, paint, f, f2, f3, i4, i6);
                }
                i3 = this.eventMinuteColors[i5];
                i2 = i5;
                i4 = i2;
            }
            i5++;
        }
    }

    private void drawEventLine(Canvas canvas, Paint paint, float f, float f2, float f3, int i, int i2) {
        canvas.drawLine(f, (int) (((i / 1440.0f) * f3) + f2), f, (int) (((i2 / 1440.0f) * f3) + f2), paint);
    }
}
