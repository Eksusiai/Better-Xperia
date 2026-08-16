package com.sonymobile.calendar;

import android.content.Context;
import android.text.format.Time;

/* JADX INFO: loaded from: classes2.dex */
public class DrawerItem {
    private static final String TAG = "DrawerItem";
    protected String mClassName;
    protected DrawerItemType mItemType;
    protected boolean mLunarAvailable;
    protected String mSubtitle;
    protected String mTitle;

    public enum DrawerItemType {
        DAY,
        WEEK,
        MONTH,
        YEAR,
        AGENDA,
        DIVIDER,
        CALENDAR,
        TASKS
    }

    public DrawerItem(String str, String str2, DrawerItemType drawerItemType, boolean z) {
        this.mTitle = str;
        this.mClassName = str2;
        this.mItemType = drawerItemType;
        this.mLunarAvailable = z;
    }

    public DrawerItem(String str, String str2, DrawerItemType drawerItemType) {
        this(str, str2, drawerItemType, false);
    }

    /* JADX INFO: renamed from: com.sonymobile.calendar.DrawerItem$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$sonymobile$calendar$DrawerItem$DrawerItemType;

        static {
            int[] iArr = new int[DrawerItemType.values().length];
            $SwitchMap$com$sonymobile$calendar$DrawerItem$DrawerItemType = iArr;
            try {
                iArr[DrawerItemType.DAY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$DrawerItem$DrawerItemType[DrawerItemType.WEEK.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$DrawerItem$DrawerItemType[DrawerItemType.MONTH.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$sonymobile$calendar$DrawerItem$DrawerItemType[DrawerItemType.YEAR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public String formatDrawerString(Context context, Time time) {
        if (time == null) {
            return "";
        }
        int i = AnonymousClass1.$SwitchMap$com$sonymobile$calendar$DrawerItem$DrawerItemType[this.mItemType.ordinal()];
        if (i == 1) {
            return Utils.getDayText(context, time, false);
        }
        if (i == 2) {
            return Utils.getWeekText(context, time);
        }
        if (i != 3) {
            return i != 4 ? "" : Utils.getYearText(context, time);
        }
        return Utils.getMonthText(context, time);
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String str) {
        this.mTitle = str;
    }

    public String getSubtitle() {
        return this.mSubtitle;
    }

    public void setSubtitle(String str) {
        this.mSubtitle = str;
    }

    public String getClassName() {
        return this.mClassName;
    }

    public void setClassName(String str) {
        this.mClassName = str;
    }

    public DrawerItemType getItemType() {
        return this.mItemType;
    }

    public void setItemType(DrawerItemType drawerItemType) {
        this.mItemType = drawerItemType;
    }

    public boolean isLunarAvailable() {
        return this.mLunarAvailable;
    }

    public void setLunarAvailable(boolean z) {
        this.mLunarAvailable = z;
    }
}
