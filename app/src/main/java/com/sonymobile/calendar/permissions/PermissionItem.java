package com.sonymobile.calendar.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.content.ContextCompat;

/* JADX INFO: loaded from: classes2.dex */
public class PermissionItem implements Parcelable, Comparable<PermissionItem> {
    public static final Parcelable.Creator<PermissionItem> CREATOR = new Parcelable.Creator<PermissionItem>() { // from class: com.sonymobile.calendar.permissions.PermissionItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionItem createFromParcel(Parcel parcel) {
            return new PermissionItem(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PermissionItem[] newArray(int i) {
            return new PermissionItem[i];
        }
    };
    private static String TAG = "PermissionItem";
    private String mDescription;
    private final int mIconId;
    private final boolean mIsEssential;
    private String mLabel;
    private final String mPermission;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PermissionItem(String str, String str2, String str3, int i, boolean z) {
        this.mPermission = str;
        this.mLabel = str2;
        this.mDescription = str3;
        this.mIconId = i;
        this.mIsEssential = z;
    }

    protected PermissionItem(Parcel parcel) {
        this.mPermission = parcel.readString();
        this.mLabel = parcel.readString();
        this.mDescription = parcel.readString();
        this.mIconId = parcel.readInt();
        this.mIsEssential = parcel.readInt() == 1;
    }

    public boolean isPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, this.mPermission) == 0;
    }

    public String getPermission() {
        return this.mPermission;
    }

    public String getLabel(Context context) {
        if (TextUtils.isEmpty(this.mLabel)) {
            PackageManager packageManager = context.getPackageManager();
            try {
                this.mLabel = String.valueOf(packageManager.getPermissionInfo(this.mPermission, 128).loadLabel(packageManager));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Permission couldn't be found", e);
            }
        }
        return this.mLabel;
    }

    public int getIconId() {
        return this.mIconId;
    }

    public String getDescription(Context context) {
        if (TextUtils.isEmpty(this.mDescription)) {
            PackageManager packageManager = context.getPackageManager();
            try {
                this.mDescription = String.valueOf(packageManager.getPermissionInfo(this.mPermission, 128).loadDescription(packageManager));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Permission couldn't be found", e);
            }
        }
        return this.mDescription;
    }

    public boolean isEssential() {
        return this.mIsEssential;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPermission);
        parcel.writeString(this.mLabel);
        parcel.writeString(this.mDescription);
        parcel.writeInt(this.mIconId);
        parcel.writeInt(this.mIsEssential ? 1 : 0);
    }

    @Override // java.lang.Comparable
    public int compareTo(PermissionItem permissionItem) {
        boolean z = this.mIsEssential;
        if (z != permissionItem.mIsEssential) {
            return z ? -1 : 1;
        }
        return this.mPermission.compareTo(permissionItem.mPermission);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof PermissionItem) && compareTo((PermissionItem) obj) == 0;
    }

    public int hashCode() {
        return this.mPermission.hashCode();
    }
}
