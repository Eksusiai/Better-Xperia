package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;

/* JADX INFO: loaded from: classes2.dex */
public abstract class MonthEventsBase {
    protected int eventDefaultColor;

    public abstract void drawEvents(Context context, int i, String str, Canvas canvas, Paint paint, int i2, int i3, float f, float f2, int i4, boolean z);

    public abstract void scaleToSize(float f, float f2, boolean z);

    public MonthEventsBase(Context context) {
        initDrawingResources(context);
    }

    protected void initDrawingResources(Context context) {
        this.eventDefaultColor = ContextCompat.getColor(context, R.color.month_default_event_color);
    }

    protected int millisToLocalMinutes(long j, long j2) {
        return (int) ((j - j2) / 60000);
    }
}
