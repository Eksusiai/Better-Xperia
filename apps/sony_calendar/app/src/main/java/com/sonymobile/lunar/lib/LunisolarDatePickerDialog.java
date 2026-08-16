package com.sonymobile.lunar.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import com.sonymobile.calendar.R;
import java.util.Calendar;

/* JADX INFO: loaded from: classes2.dex */
public class LunisolarDatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener, LunarDatePicker.OnLunarDateChangedListener, DatePicker.OnDateChangedListener {
    private static final String DAY = "day";
    private static final String LUNAR_TAB = "Lunar";
    public static final int MODE_COMBINED = 0;
    public static final int MODE_LUNAR = 1;
    private static final String MONTH = "month";
    private static final String SOLAR_TAB = "Solar";
    public static final int TAB_LUNAR = 1;
    public static final int TAB_SOLAR = 0;
    private static final String YEAR = "year";
    private final Calendar mCalendar;
    private final OnLuniSolarDateSetListener mCallBack;
    private final LunarDatePicker mLunarDatePicker;
    private int mMode;
    private int mSelectedTab;
    private final DatePicker mSolarDatePicker;
    private final TabHost mTabHost;
    public static final int ID_SOLAR_PICKER = R.id.solar_picker;
    public static final int ID_LUNAR_PICKER = R.id.lunar_picker;
    private static final int ID_TAB_HOST = R.id.tabhost;

    public interface OnLuniSolarDateSetListener {
        void onDateSet(View view, int i, int i2, int i3);
    }

    public LunisolarDatePickerDialog(Context context, OnLuniSolarDateSetListener onLuniSolarDateSetListener, int i, int i2, int i3, int i4, int i5) {
        this(context, 0, onLuniSolarDateSetListener, i, i2, i3, i4, i5);
    }

    public LunisolarDatePickerDialog(Context context, int i, OnLuniSolarDateSetListener onLuniSolarDateSetListener, int i2, int i3, int i4, int i5, int i6) {
        super(context, i);
        this.mCallBack = onLuniSolarDateSetListener;
        this.mMode = i5;
        this.mSelectedTab = i6;
        Calendar calendar = Calendar.getInstance();
        this.mCalendar = calendar;
        calendar.set(i2, i3, i4);
        setButton(-1, "确定", this);
        setButton(-2, "取消", (DialogInterface.OnClickListener) null);
        setIcon(0);
        TabHost tabHostInflateDialogView = inflateDialogView(context);
        setView(tabHostInflateDialogView);
        DatePicker datePicker = (DatePicker) tabHostInflateDialogView.findViewById(ID_SOLAR_PICKER);
        this.mSolarDatePicker = datePicker;
        datePicker.init(i2, i3, i4, this);
        LunarDatePicker lunarDatePicker = (LunarDatePicker) tabHostInflateDialogView.findViewById(ID_LUNAR_PICKER);
        this.mLunarDatePicker = lunarDatePicker;
        lunarDatePicker.init(i2, i3, i4, this);
        this.mTabHost = (TabHost) tabHostInflateDialogView.findViewById(ID_TAB_HOST);
        initPickerView();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        tryNotifyDateSet();
    }

    private void initPickerView() {
        this.mTabHost.setup();
        int i = this.mMode;
        if (i == 0) {
            TabHost tabHost = this.mTabHost;
            tabHost.addTab(tabHost.newTabSpec(SOLAR_TAB).setIndicator("阳历").setContent(ID_SOLAR_PICKER));
            TabHost tabHost2 = this.mTabHost;
            tabHost2.addTab(tabHost2.newTabSpec(LUNAR_TAB).setIndicator("阴历").setContent(ID_LUNAR_PICKER));
            this.mTabHost.setCurrentTab(this.mSelectedTab);
            updateTitle(this.mSelectedTab != 0);
        } else if (i == 1) {
            this.mSolarDatePicker.setVisibility(8);
            updateTitle(true);
        }
        this.mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() { // from class: com.sonymobile.lunar.lib.LunisolarDatePickerDialog.1
            @Override // android.widget.TabHost.OnTabChangeListener
            public void onTabChanged(String str) {
                if (str.equalsIgnoreCase(LunisolarDatePickerDialog.SOLAR_TAB)) {
                    LunisolarDatePickerDialog.this.updateTitle(false);
                    LunisolarDatePickerDialog.this.mSolarDatePicker.init(LunisolarDatePickerDialog.this.mCalendar.get(1), LunisolarDatePickerDialog.this.mCalendar.get(2), LunisolarDatePickerDialog.this.mCalendar.get(5), LunisolarDatePickerDialog.this);
                } else if (str.equalsIgnoreCase(LunisolarDatePickerDialog.LUNAR_TAB)) {
                    LunisolarDatePickerDialog.this.updateTitle(true);
                    LunisolarDatePickerDialog.this.mLunarDatePicker.init(LunisolarDatePickerDialog.this.mCalendar.get(1), LunisolarDatePickerDialog.this.mCalendar.get(2), LunisolarDatePickerDialog.this.mCalendar.get(5), LunisolarDatePickerDialog.this);
                }
            }
        });
    }

    private void tryNotifyDateSet() {
        if (this.mCallBack != null) {
            this.mSolarDatePicker.clearFocus();
            this.mLunarDatePicker.clearFocus();
            this.mCallBack.onDateSet(this.mTabHost.getCurrentTab() == 0 ? this.mSolarDatePicker : this.mLunarDatePicker, this.mCalendar.get(1), this.mCalendar.get(2), this.mCalendar.get(5));
        }
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        Bundle bundleOnSaveInstanceState = super.onSaveInstanceState();
        bundleOnSaveInstanceState.putInt("year", this.mSolarDatePicker.getYear());
        bundleOnSaveInstanceState.putInt("month", this.mSolarDatePicker.getMonth());
        bundleOnSaveInstanceState.putInt(DAY, this.mSolarDatePicker.getDayOfMonth());
        return bundleOnSaveInstanceState;
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mSolarDatePicker.init(bundle.getInt("year"), bundle.getInt("month"), bundle.getInt(DAY), null);
    }

    @Override // android.widget.DatePicker.OnDateChangedListener
    public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
        this.mCalendar.set(i, i2, i3);
        updateTitle(false);
    }

    @Override // com.sonymobile.lunar.lib.LunarDatePicker.OnLunarDateChangedListener
    public void onLunarDateChanged(LunarDatePicker lunarDatePicker, int i, int i2, int i3) {
        this.mCalendar.set(i, i2, i3);
        updateTitle(true);
    }

    private TabHost inflateDialogView(Context context) {
        DatePicker datePicker = new DatePicker(context);
        datePicker.setId(ID_SOLAR_PICKER);
        datePicker.setSpinnersShown(true);
        datePicker.setCalendarViewShown(false);
        Calendar calendar = Calendar.getInstance();
        calendar.set(LunarUtils.MIN_LUNAR_YEAR, 1, 19);
        datePicker.setMinDate(calendar.getTimeInMillis());
        calendar.set(LunarUtils.MAX_LUNAR_YEAR, 11, 31);
        datePicker.setMaxDate(calendar.getTimeInMillis());
        datePicker.setLayoutParams(new FrameLayout.LayoutParams(dp2px(context, 300.0f), dp2px(context, 210.0f), 17));
        LunarDatePicker lunarDatePicker = new LunarDatePicker(context);
        lunarDatePicker.setId(ID_LUNAR_PICKER);
        FrameLayout frameLayout = new FrameLayout(context, null);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(dp2px(context, 300.0f), dp2px(context, 210.0f), 17));
        frameLayout.addView(lunarDatePicker, new FrameLayout.LayoutParams(-2, -2, 17));
        FrameLayout frameLayout2 = new FrameLayout(context, null);
        frameLayout2.setLayoutParams(new FrameLayout.LayoutParams(-2, -1, 1));
        frameLayout2.setId(android.R.id.tabcontent);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp2px(context, 300.0f), dp2px(context, 210.0f), 17);
        frameLayout2.addView(datePicker, layoutParams);
        frameLayout2.addView(frameLayout, layoutParams);
        TabWidget tabWidget = new TabWidget(context);
        tabWidget.setId(android.R.id.tabs);
        tabWidget.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        LinearLayout linearLayout = new LinearLayout(context, null);
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        linearLayout.addView(tabWidget);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -1);
        layoutParams2.gravity = 1;
        linearLayout.addView(frameLayout2, layoutParams2);
        TabHost tabHost = new TabHost(context, null);
        tabHost.setId(ID_TAB_HOST);
        tabHost.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        tabHost.addView(linearLayout);
        return tabHost;
    }

    private int dp2px(Context context, float f) {
        return (int) (f * context.getResources().getDisplayMetrics().density);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTitle(boolean z) {
        String string;
        if (z) {
            StringBuilder sb = new StringBuilder();
            LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(this.mCalendar.getTime());
            sb.append(LunarUtils.sLunarYearStrings[lunarDateConvertSolarDateToLunarDate.mYear - 1901]).append(LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate).substring(0, 2)).append(LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate));
            string = sb.toString();
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.mCalendar.get(1)).append(" 年 ").append(this.mCalendar.get(2) + 1).append(" 月 ").append(this.mCalendar.get(5)).append(" 日");
            string = sb2.toString();
        }
        setTitle(string);
    }
}
