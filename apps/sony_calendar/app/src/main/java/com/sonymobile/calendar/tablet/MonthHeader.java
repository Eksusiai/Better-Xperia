package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;

/* JADX INFO: loaded from: classes2.dex */
public class MonthHeader extends HeaderBase {
    public MonthHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initMeasures();
    }

    private void initMeasures() {
        setPaddingRelative((int) getResources().getDimension(R.dimen.calendar_left_panel_width), 0, 0, 0);
    }

    @Override // com.sonymobile.calendar.tablet.HeaderBase
    protected String getText(Context context, Time time) {
        return Utils.getHeaderText(context, time, 3);
    }
}
