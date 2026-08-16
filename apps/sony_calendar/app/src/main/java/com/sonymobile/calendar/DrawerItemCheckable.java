package com.sonymobile.calendar;

/* JADX INFO: loaded from: classes2.dex */
public class DrawerItemCheckable extends DrawerItem {
    private static final String TAG = "DrawerItemCheckable";
    private String mAccountName;
    private String mAccountType;
    private int mColor;
    private long mId;
    private boolean mIsChecked;

    public DrawerItemCheckable(String str, String str2, DrawerItem.DrawerItemType drawerItemType, long j, int i, boolean z, String str3, String str4) {
        super(str, null, drawerItemType);
        this.mIsChecked = z;
        this.mSubtitle = str2;
        this.mId = j;
        this.mColor = i;
        this.mAccountType = str3;
        this.mAccountName = str4;
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    public void setChecked(boolean z) {
        this.mIsChecked = z;
    }

    public int getColor() {
        return this.mColor;
    }

    public void setColor(int i) {
        this.mColor = i;
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long j) {
        this.mId = j;
    }

    public String getAccountType() {
        return this.mAccountType;
    }

    public void setAccountType(String str) {
        this.mAccountType = str;
    }

    public int hashCode() {
        long j = this.mId;
        return 31 + ((int) (j ^ (j >>> 32)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.mId == ((DrawerItemCheckable) obj).mId;
    }

    public String getAccountName() {
        return this.mAccountName;
    }
}
