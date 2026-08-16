package com.sonymobile.calendar.tablet;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonymobile.calendar.CalendarApplication;
import com.sonymobile.calendar.MonthFragment;
import com.sonymobile.calendar.Navigator;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class MonthsNavigationView extends LinearLayout {
    private static final float FONT_SCALE_DOWN = 0.85f;
    private static final int MONTHS_COUNT = 12;
    private int defaultTextColor;
    private final boolean isR2L;
    private final boolean mIsInMultiWindowMode;
    private final float mLineOffsetY;
    private final float mLineStroke;
    private final int mTextColor;
    private final float mTextSize;
    private final Navigator navigator;
    private final View.OnClickListener onMonthClickListener;
    private final Paint paint;
    private int selectedIndex;
    private int selectionColor;
    private String timezone;

    public MonthsNavigationView(MonthFragment monthFragment) {
        super(monthFragment.getActivity());
        this.selectedIndex = -1;
        this.onMonthClickListener = new View.OnClickListener() { // from class: com.sonymobile.calendar.tablet.MonthsNavigationView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Time time = new SafeTime(MonthsNavigationView.this.timezone);
                time.set(Utils.getDisplayTime());
                int iIntValue = ((Integer) view.getTag(R.id.month_position_tag)).intValue();
                if (iIntValue == time.month) {
                    return;
                }
                time.month = iIntValue;
                time.normalize(false);
                if (time.month != iIntValue) {
                    time.month = iIntValue;
                    time.monthDay = time.getActualMaximum(4);
                }
                if (MonthsNavigationView.this.navigator != null) {
                    MonthsNavigationView.this.navigator.goTo(time, true);
                }
            }
        };
        this.navigator = monthFragment;
        this.isR2L = CalendarApplication.isR2L(getResources());
        this.mIsInMultiWindowMode = CalendarApplication.isIsInMultiWindowMode(monthFragment.getActivity());
        this.mTextSize = getTextSize();
        this.mTextColor = ContextCompat.getColor(getContext(), R.color.day_number_color);
        this.mLineStroke = getResources().getDimension(R.dimen.marker_line_stroke);
        this.mLineOffsetY = getResources().getDimension(R.dimen.marker_line_offset_y);
        this.timezone = Utils.getTimeZone(getContext(), null);
        this.paint = new Paint();
        initLayout();
        initMonthLabels();
    }

    public void updateNavigationView(Time time) {
        this.timezone = time.timezone;
        deselectItem(this.selectedIndex);
        selectItem(time.month);
        invalidate();
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMarker(canvas);
    }

    private void drawMarker(Canvas canvas) {
        float width = getWidth() / 12.0f;
        float height = getChildAt(0).getHeight();
        float f = ((this.isR2L ? 11 - this.selectedIndex : this.selectedIndex) * width) + (width / 2.0f);
        this.paint.setStrokeWidth(this.mLineStroke);
        canvas.drawLine(f, height, f, height - this.mLineOffsetY, this.paint);
    }

    private void initLayout() {
        setLayoutParams(new LinearLayout.LayoutParams(-2, -1));
        setOrientation(0);
        setGravity(17);
        setWillNotDraw(false);
    }

    private void initMonthLabels() {
        String[] monthStrings = getMonthStrings();
        String[] monthContentDescription = getMonthContentDescription();
        initColors(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1, 1.0f);
        Typeface typefaceCreate = Typeface.create(getResources().getString(R.string.roboto_font), 0);
        for (int i = 0; i < 12; i++) {
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(layoutParams);
            textView.setTypeface(typefaceCreate);
            textView.setTextColor(this.mTextColor);
            textView.setTextSize(1, this.mTextSize);
            textView.setText(monthStrings[i]);
            textView.setTag(R.id.month_position_tag, Integer.valueOf(i));
            textView.setContentDescription(monthContentDescription[i]);
            textView.setGravity(17);
            textView.setOnClickListener(this.onMonthClickListener);
            textView.setFocusable(true);
            textView.setBackgroundResource(R.drawable.birthday_item_selector);
            addView(textView);
        }
    }

    private void initColors(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary});
        this.defaultTextColor = typedArrayObtainStyledAttributes.getColor(0, ContextCompat.getColor(context, R.color.calendar_foreground));
        int primaryColor = UiUtils.getPrimaryColor(context);
        this.selectionColor = primaryColor;
        this.paint.setColor(primaryColor);
        this.paint.setAntiAlias(true);
        typedArrayObtainStyledAttributes.recycle();
    }

    private void deselectItem(int i) {
        if (i == -1) {
            return;
        }
        ((TextView) getChildAt(i)).setTextColor(this.defaultTextColor);
    }

    private void selectItem(int i) {
        if (i == -1) {
            return;
        }
        this.selectedIndex = i;
        ((TextView) getChildAt(i)).setTextColor(this.selectionColor);
    }

    private float getTextSize() {
        float dimension = getResources().getDimension(R.dimen.navigation_view_text_size);
        return this.mIsInMultiWindowMode ? dimension * FONT_SCALE_DOWN : dimension;
    }

    private String[] getMonthStrings() {
        return new String[]{DateUtils.getMonthString(0, 20), DateUtils.getMonthString(1, 20), DateUtils.getMonthString(2, 20), DateUtils.getMonthString(3, 20), DateUtils.getMonthString(4, 20), DateUtils.getMonthString(5, 20), DateUtils.getMonthString(6, 20), DateUtils.getMonthString(7, 20), DateUtils.getMonthString(8, 20), DateUtils.getMonthString(9, 20), DateUtils.getMonthString(10, 20), DateUtils.getMonthString(11, 20)};
    }

    private String[] getMonthContentDescription() {
        return new String[]{DateUtils.getMonthString(0, 10), DateUtils.getMonthString(1, 10), DateUtils.getMonthString(2, 10), DateUtils.getMonthString(3, 10), DateUtils.getMonthString(4, 10), DateUtils.getMonthString(5, 10), DateUtils.getMonthString(6, 10), DateUtils.getMonthString(7, 10), DateUtils.getMonthString(8, 10), DateUtils.getMonthString(9, 10), DateUtils.getMonthString(10, 10), DateUtils.getMonthString(11, 10)};
    }
}
