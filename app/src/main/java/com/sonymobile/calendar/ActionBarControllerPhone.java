package com.sonymobile.calendar;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.sonymobile.calendar.agendamonth.SplitScreenFragment;
import com.sonymobile.calendar.tasks.activity.TasksListFragment;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.widget.WhiteHeader;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarControllerPhone extends ActionBarControllerBase {
    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void setToolbar(Toolbar toolbar) {
    }

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void initActionBar(Toolbar toolbar, WhiteHeader whiteHeader, DrawerHelper drawerHelper, Context context) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        this.mToolbar = toolbar;
        this.mToolbar.setBackgroundResource(R.color.toolbar_background_color);
        appCompatActivity.setSupportActionBar(this.mToolbar);
        setBaseComponents(appCompatActivity.getSupportActionBar(), whiteHeader, drawerHelper, context);
        if (UiUtils.isPortrait(this.mContext)) {
            initActionBarBase();
        } else {
            initActionBarWithWhiteHeader();
        }
    }

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void onFragmentAttached(String str) {
        if (this.mContext == null) {
            return;
        }
        if (str.equals(YearFragment.class.getName()) || str.equals(TasksListFragment.class.getName())) {
            showWhiteHeader(false);
            this.mTitleActionBar.setVisibility(0);
        } else if (str.equals(DayFragment.class.getName()) || str.equals(WeekFragment.class.getName()) || str.equals(SplitScreenFragment.class.getName())) {
            if ((str.equals(SplitScreenFragment.class.getName()) || UiUtils.isPhoneLandscape(this.mContext)) && !UiUtils.isPhonePortrait(this.mContext)) {
                showWhiteHeader(true);
            } else {
                showWhiteHeader(false);
            }
            if (UiUtils.isPortrait(this.mContext)) {
                this.mTitleActionBar.setVisibility(0);
            } else {
                this.mTitleActionBar.setVisibility(8);
                this.mSubtitleActionBar.setVisibility(8);
            }
        }
        if (this.mWhiteHeader != null) {
            this.mWhiteHeader.showWeather((str.equals(WeekFragment.class.getName()) || str.equals(DayFragment.class.getName())) ? false : true);
            this.mWhiteHeader.setWeatherIconInfo();
        }
    }

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void updateVisibility(String str) {
        onFragmentAttached(str);
    }

    private void showWhiteHeader(boolean z) {
        if (this.mWhiteHeader != null) {
            if (z) {
                this.mWhiteHeader.setVisibility(0);
            } else {
                this.mWhiteHeader.setVisibility(8);
            }
        }
    }
}
