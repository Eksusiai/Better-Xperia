package com.sonymobile.calendar.birthday;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.UiUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class BirthdayIconHeader extends LinearLayout {
    private static final float TAB_TIP_HEIGHT = 0.13f;
    private TextView dayName;
    private View tabView;

    public BirthdayIconHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public void update(Time time) {
        Time time2 = new SafeTime();
        Utils.changeTimeZoneKeepSourceDateTime(time, time2, Time.getCurrentTimezone());
        Date date = new Date(time2.toMillis(false));
        this.dayName.setText(new SimpleDateFormat(Utils.FORMAT_DATE_NORMAL, Locale.getDefault()).format(date));
        updateTabBackground();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateTabBackground();
    }

    private void initViews(Context context) {
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.birthday_icon_header, (ViewGroup) null);
        this.tabView = viewInflate.findViewById(R.id.header_badge);
        this.dayName = (TextView) viewInflate.findViewById(R.id.header_dayText);
        addView(viewInflate);
    }

    private PathShape getTabBackgroundDrawable() {
        float height = this.tabView.getHeight() * TAB_TIP_HEIGHT;
        float height2 = this.tabView.getHeight() - height;
        float width = this.tabView.getWidth();
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(0.0f, height2);
        float f = height + height2;
        path.lineTo(width / 2.0f, f);
        path.lineTo(width, height2);
        path.lineTo(width, 0.0f);
        return new PathShape(path, width, f);
    }

    private void updateTabBackground() {
        ShapeDrawable shapeDrawable = new ShapeDrawable(getTabBackgroundDrawable());
        shapeDrawable.getPaint().setColor(UiUtils.getPrimaryColor(getContext()));
        this.tabView.setBackground(shapeDrawable);
    }
}
