package com.sonymobile.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.sonymobile.calendar.tablet.TabletAgendaControllerFragment;
import com.sonymobile.calendar.tablet.TabletDayControllerFragment;
import com.sonymobile.calendar.tablet.TabletTasksControllerFragment;
import com.sonymobile.calendar.tablet.TabletWeekControllerFragment;
import com.sonymobile.calendar.tasks.activity.TasksEditFragment;
import com.sonymobile.calendar.tasks.activity.TasksListFragment;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.widget.WhiteHeader;

/* JADX INFO: loaded from: classes2.dex */
public class ActionBarControllerTablet extends ActionBarControllerBase {
    private TextView mTitleToolbar;

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void updateVisibility(String str) {
    }

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void initActionBar(Toolbar toolbar, WhiteHeader whiteHeader, DrawerHelper drawerHelper, Context context) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        this.mToolbar = toolbar;
        this.mToolbar.setBackgroundResource(R.color.toolbar_background_color);
        appCompatActivity.setSupportActionBar(this.mToolbar);
        setBaseComponents(appCompatActivity.getSupportActionBar(), whiteHeader, drawerHelper, context);
        initActionBarBase();
    }

    public void setWhiteHeader(WhiteHeader whiteHeader) {
        this.mWhiteHeader = whiteHeader;
    }

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void setToolbar(Toolbar toolbar) {
        this.mToolbar = toolbar;
        if (toolbar != null) {
            this.mToolbar.setBackgroundResource(R.color.toolbar_background_color);
            this.mTitleToolbar = (TextView) LayoutInflater.from(this.mContext).inflate(R.layout.toolbar_title, this.mToolbar).findViewById(R.id.toolbar_title_text);
        } else {
            this.mTitleToolbar = null;
        }
    }

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void setSubtitle(String str) {
        TextView textView = this.mTitleToolbar;
        if (textView != null) {
            textView.setText(str);
        }
    }

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public TextView getTitle() {
        TextView textView = this.mTitleToolbar;
        return textView != null ? textView : super.getTitle();
    }

    @Override // com.sonymobile.calendar.ActionBarControllerBase
    public void onFragmentAttached(String str) {
        boolean z = false;
        if (str.equals(WeekFragment.class.getName()) || str.equals(TabletWeekControllerFragment.class.getName()) || str.equals(DayFragment.class.getName()) || str.equals(TabletDayControllerFragment.class.getName()) || str.equals(YearFragment.class.getName())) {
            if (UiUtils.isLandscape(this.mContext)) {
                super.getTitle().setOnClickListener(this.clickToggleDrawer);
            }
            TextView textView = this.mTitleToolbar;
            if (textView != null) {
                textView.setClickable(true);
            }
        } else {
            super.getTitle().setOnClickListener(this.clickToggleDrawer);
            TextView textView2 = this.mTitleToolbar;
            if (textView2 != null) {
                textView2.setClickable(false);
            }
        }
        updateActionBarElevation(str);
        if (this.mWhiteHeader != null) {
            WhiteHeader whiteHeader = this.mWhiteHeader;
            if (!str.equals(WeekFragment.class.getName()) && !str.equals(DayFragment.class.getName())) {
                z = true;
            }
            whiteHeader.showWeather(z);
            this.mWhiteHeader.setWeatherIconInfo();
        }
    }

    private void updateActionBarElevation(String str) {
        if (UiUtils.isPortrait(this.mContext) || str.equals(YearFragment.class.getName()) || str.equals(TabletTasksControllerFragment.class.getName()) || str.equals(AgendaFragment.class.getName()) || str.equals(TasksListFragment.class.getName()) || str.equals(TasksEditFragment.class.getName()) || str.equals(TabletAgendaControllerFragment.class.getName())) {
            this.mActionBar.setElevation(this.mContext.getResources().getDimension(R.dimen.toolbar_elevation));
        } else {
            this.mActionBar.setElevation(0.0f);
        }
    }
}
