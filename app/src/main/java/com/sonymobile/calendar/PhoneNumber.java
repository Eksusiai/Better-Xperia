package com.sonymobile.calendar;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class PhoneNumber implements Parcelable {
    public static final Parcelable.Creator<PhoneNumber> CREATOR = new Parcelable.Creator<PhoneNumber>() { // from class: com.sonymobile.calendar.PhoneNumber.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PhoneNumber createFromParcel(Parcel parcel) {
            return new PhoneNumber(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PhoneNumber[] newArray(int i) {
            return new PhoneNumber[i];
        }
    };
    public String number;
    public String type;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PhoneNumber(String str, String str2) {
        this.type = str;
        this.number = str2;
    }

    protected PhoneNumber(Parcel parcel) {
        this.type = parcel.readString();
        this.number = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.type);
        parcel.writeString(this.number);
    }
}
