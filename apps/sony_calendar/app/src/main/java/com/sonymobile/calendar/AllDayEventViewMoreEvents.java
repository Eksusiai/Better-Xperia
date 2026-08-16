package com.sonymobile.calendar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AllDayEventViewMoreEvents extends AllDayEventViewBase {
    private int moreEvents;

    public AllDayEventViewMoreEvents(Context context, Time time, boolean z, ViewIndex viewIndex, int i) {
        super(context, time, z, viewIndex);
        this.moreEvents = i;
        init(-1);
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initContentDescription() {
        StringBuilder sbAppend = new StringBuilder("+").append(this.moreEvents);
        sbAppend.append(getResources().getString(R.string.accessibility_all_day));
        setContentDescription(sbAppend.toString());
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected String getTitleText() {
        return "+" + this.moreEvents;
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected Object getNavigationInfo() {
        return Integer.valueOf(this.moreEvents);
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initIcon() {
        ((ImageView) findViewById(R.id.event_icon)).setVisibility(8);
    }

    @Override // com.sonymobile.calendar.AllDayEventViewBase
    protected void initLayout(int i) {
        LayoutInflater.from(getContext()).inflate(R.layout.all_day_event_item, this);
        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        boolean z = i == -1;
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.day_week_event_background));
        getBackground().setColorFilter(UiUtils.getDisplayColorFromColor(i), PorterDuff.Mode.MULTIPLY);
        setClickable(true);
        initTextView(z);
        initIcon();
    }
}
