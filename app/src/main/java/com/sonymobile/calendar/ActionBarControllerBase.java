package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.widget.WhiteHeader;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ActionBarControllerBase implements NavigationListener {
    protected View.OnClickListener clickToggleDrawer = new View.OnClickListener() { // from class: com.sonymobile.calendar.ActionBarControllerBase.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ActionBarControllerBase.this.mDrawerHelper.toggleDrawer();
        }
    };
    protected ActionBar mActionBar;
    protected View mActionBarCustomView;
    protected Context mContext;
    protected DrawerHelper mDrawerHelper;
    protected TextView mSubtitleActionBar;
    protected TextView mTitleActionBar;
    protected Toolbar mToolbar;
    protected WhiteHeader mWhiteHeader;

    public abstract void initActionBar(Toolbar toolbar, WhiteHeader whiteHeader, DrawerHelper drawerHelper, Context context);

    public abstract void onFragmentAttached(String str);

    public abstract void setToolbar(Toolbar toolbar);

    public abstract void updateVisibility(String str);

    public void setTitle(String str) {
        TextView textView = this.mTitleActionBar;
        if (textView != null) {
            textView.setText(str);
        }
    }

    public void setSubtitle(String str) {
        TextView textView = this.mSubtitleActionBar;
        if (textView != null) {
            if (str != null) {
                textView.setVisibility(0);
                this.mSubtitleActionBar.setText(str);
            } else {
                textView.setVisibility(8);
            }
        }
    }

    public int getTitleId() {
        return this.mTitleActionBar.getId();
    }

    public Toolbar getToolbar() {
        return this.mToolbar;
    }

    public TextView getTitle() {
        return this.mTitleActionBar;
    }

    public CharSequence getSubtitleText() {
        return this.mSubtitleActionBar.getText();
    }

    protected void setBaseComponents(ActionBar actionBar, WhiteHeader whiteHeader, DrawerHelper drawerHelper, Context context) {
        this.mActionBar = actionBar;
        this.mDrawerHelper = drawerHelper;
        this.mWhiteHeader = whiteHeader;
        this.mContext = context;
    }

    protected void initActionBarBase() {
        this.mActionBar.setDisplayShowTitleEnabled(false);
        this.mActionBar.setDisplayHomeAsUpEnabled(true);
        this.mActionBar.setDisplayShowCustomEnabled(true);
        this.mActionBar.setCustomView(R.layout.actionbar_title);
        View viewFindViewById = this.mToolbar.findViewById(R.id.actionbar_title_layout);
        this.mActionBarCustomView = viewFindViewById;
        TextView textView = (TextView) viewFindViewById.findViewById(R.id.actionbar_title_text);
        this.mTitleActionBar = textView;
        textView.setOnClickListener(this.clickToggleDrawer);
        this.mSubtitleActionBar = (TextView) this.mActionBarCustomView.findViewById(R.id.actionbar_subtitle_text);
    }

    protected void initActionBarWithWhiteHeader() {
        initActionBarBase();
        WhiteHeader whiteHeader = new WhiteHeader(this.mContext);
        this.mWhiteHeader = whiteHeader;
        whiteHeader.setTitleClickListener(this.clickToggleDrawer);
        this.mWhiteHeader.resetTitleStartMargin();
        ((ViewGroup) this.mActionBarCustomView.getParent()).addView(this.mWhiteHeader);
    }

    /* JADX INFO: renamed from: com.sonymobile.calendar.ActionBarControllerBase$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$calendar$ViewType;

        static {
            int[] iArr = new int[ViewType.values().length];
            $SwitchMap$com$sonymobile$calendar$ViewType = iArr;
            try {
                iArr[ViewType.TASK.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$ViewType[ViewType.YEAR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$ViewType[ViewType.AGENDA.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$ViewType[ViewType.MONTH.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$ViewType[ViewType.WEEK.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$ViewType[ViewType.DAY.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:31:0x00cb  */
    @Override // com.sonymobile.calendar.NavigationListener
    public void onViewNavigated(Time time, String str, ViewType viewType) {
        String dayTitleText;
        this.mActionBar.setTitle("");
        String weekWithWeekNumber = null;
        switch (AnonymousClass2.$SwitchMap$com$sonymobile$calendar$ViewType[viewType.ordinal()]) {
            case 1:
            case 2:
            case 3:
                break;
            case 4:
                if (Utils.isTabletDevice(this.mContext)) {
                    if (UiUtils.isLandscape(this.mContext)) {
                        str = Utils.getActionBarDateTitle(this.mContext, time, 4);
                        this.mActionBar.setElevation(0.0f);
                        this.mToolbar.setTitle(Utils.getActionBarDateTitle(this.mContext, time, 5));
                    } else {
                        str = Utils.getHeaderText(this.mContext, time, 3);
                    }
                } else if (!UiUtils.isPhonePortrait(this.mContext)) {
                    str = null;
                } else {
                    str = Utils.getActionBarDateTitle(this.mContext, time, 3);
                }
                break;
            case 5:
                if (Utils.isTabletDevice(this.mContext)) {
                    if (UiUtils.isLandscape(this.mContext)) {
                        str = Utils.getActionBarDateTitle(this.mContext, time, 4);
                        weekWithWeekNumber = Utils.getWeekForToolbarTitle(this.mContext, time);
                    } else {
                        str = Utils.getWeekWithAppendedWeekNumber(this.mContext, time, 2);
                    }
                } else {
                    str = Utils.getActionBarDateTitle(this.mContext, time, 3);
                    weekWithWeekNumber = Utils.getWeekWithWeekNumber(this.mContext, time);
                }
                break;
            case 6:
                if (Utils.isTabletDevice(this.mContext) && UiUtils.isLandscape(this.mContext)) {
                    str = Utils.getActionBarDateTitle(this.mContext, time, 3);
                    dayTitleText = Utils.getWeekDayWithNumber(this.mContext, time);
                } else {
                    str = Utils.getDayDateString(this.mContext, time, "MMMM d yyyy");
                    dayTitleText = Utils.getDayTitleText(this.mContext, time);
                }
                weekWithWeekNumber = dayTitleText;
                WhiteHeader whiteHeader = this.mWhiteHeader;
                if (whiteHeader != null) {
                    whiteHeader.showHolidays(false);
                }
                break;
            default:
                str = null;
                break;
        }
        setTitle(str);
        setSubtitle(weekWithWeekNumber);
        WhiteHeader whiteHeader2 = this.mWhiteHeader;
        if (whiteHeader2 == null || whiteHeader2.getVisibility() != 0) {
            return;
        }
        this.mWhiteHeader.setDate(time, viewType);
    }
}
