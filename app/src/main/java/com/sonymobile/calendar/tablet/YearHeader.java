package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import com.sonymobile.calendar.Utils;

/* JADX INFO: loaded from: classes2.dex */
public class YearHeader extends HeaderBase {
    public YearHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.sonymobile.calendar.tablet.HeaderBase
    protected String getText(Context context, Time time) {
        return Utils.getHeaderText(context, time, 4);
    }
}
