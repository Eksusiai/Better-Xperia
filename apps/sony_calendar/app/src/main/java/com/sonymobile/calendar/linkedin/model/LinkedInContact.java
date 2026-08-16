package com.sonymobile.calendar.linkedin.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInContact implements Parcelable {
    public static final Parcelable.Creator<LinkedInContact> CREATOR = new Parcelable.Creator<LinkedInContact>() { // from class: com.sonymobile.calendar.linkedin.model.LinkedInContact.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkedInContact createFromParcel(Parcel parcel) {
            return new LinkedInContact(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LinkedInContact[] newArray(int i) {
            return new LinkedInContact[i];
        }
    };
    private static final int PHOTO_COMPRESS_QUALITY = 100;
    private int mConnectionLevel;
    private String mEmail;
    private String mFirstName;
    private String mHeadline;
    private String mIndustry;
    private String mLastName;
    private String mLinkedinId;
    private String mLocation;
    private Bitmap mPhoto;
    private String mPhotoUrl;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public LinkedInContact() {
    }

    public LinkedInContact(String str, String str2, String str3, int i, String str4, String str5) {
        this.mLinkedinId = str;
        this.mFirstName = str2;
        this.mConnectionLevel = i;
        this.mLastName = str3;
        this.mEmail = str4;
        this.mPhotoUrl = str5;
    }

    public LinkedInContact(String str, String str2, String str3, int i, String str4, String str5, Bitmap bitmap) {
        this(str, str2, str3, i, str4, str5);
        this.mPhoto = bitmap;
    }

    public LinkedInContact(String str, String str2, String str3, int i, String str4, String str5, byte[] bArr) {
        this(str, str2, str3, i, str4, str5);
        makeBitmapFromByteArray(bArr);
    }

    private void makeBitmapFromByteArray(byte[] bArr) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        this.mPhoto = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
    }

    public String getLinkedinId() {
        return this.mLinkedinId;
    }

    public void setLinkedinId(String str) {
        this.mLinkedinId = str;
    }

    public String getFirstName() {
        return this.mFirstName;
    }

    public void setFirstName(String str) {
        this.mFirstName = str;
    }

    public String getLastName() {
        return this.mLastName;
    }

    public void setLastName(String str) {
        this.mLastName = str;
    }

    public boolean isInConnections() {
        return this.mConnectionLevel == 1;
    }

    public int getConnectionLevel() {
        return this.mConnectionLevel;
    }

    public void setConnectionLevel(int i) {
        this.mConnectionLevel = i;
    }

    public String getEmail() {
        return this.mEmail;
    }

    public void setEmail(String str) {
        this.mEmail = str;
    }

    public String getHeadline() {
        return this.mHeadline;
    }

    public void setmHeadline(String str) {
        this.mHeadline = str;
    }

    public String getIndustry() {
        return this.mIndustry;
    }

    public void setIndustry(String str) {
        this.mIndustry = str;
    }

    public String getLocation() {
        return this.mLocation;
    }

    public void setLocation(String str) {
        this.mLocation = str;
    }

    public String getPhotoUrl() {
        return this.mPhotoUrl;
    }

    public void setPhotoUrl(String str) {
        this.mPhotoUrl = str;
    }

    public Bitmap getPhoto() {
        return this.mPhoto;
    }

    public void setPhoto(Bitmap bitmap) {
        this.mPhoto = bitmap;
    }

    public void setPhoto(byte[] bArr) {
        this.mPhoto = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
    }

    public boolean hasLinkedinId() {
        return !TextUtils.isEmpty(this.mLinkedinId);
    }

    public boolean hasFirstName() {
        return !TextUtils.isEmpty(this.mFirstName);
    }

    public boolean hasLastName() {
        return !TextUtils.isEmpty(this.mLastName);
    }

    public boolean hasEmail() {
        return !TextUtils.isEmpty(this.mEmail);
    }

    public boolean hasHeadline() {
        return !TextUtils.isEmpty(this.mHeadline);
    }

    public boolean hasIndustry() {
        return !TextUtils.isEmpty(this.mIndustry);
    }

    public boolean hasLocation() {
        return !TextUtils.isEmpty(this.mLocation);
    }

    public boolean hasPhotoUrl() {
        return !TextUtils.isEmpty(this.mPhotoUrl);
    }

    public boolean hasPhoto() {
        return this.mPhoto != null;
    }

    public byte[] getPhotoByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.mPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private LinkedInContact(Parcel parcel) {
        this.mLinkedinId = parcel.readString();
        this.mFirstName = parcel.readString();
        this.mLastName = parcel.readString();
        this.mConnectionLevel = parcel.readInt();
        if (parcel.readByte() != 0) {
            this.mEmail = parcel.readString();
        }
        if (parcel.readByte() != 0) {
            this.mHeadline = parcel.readString();
        }
        if (parcel.readByte() != 0) {
            this.mIndustry = parcel.readString();
        }
        if (parcel.readByte() != 0) {
            this.mLocation = parcel.readString();
        }
        if (parcel.readByte() != 0) {
            this.mPhotoUrl = parcel.readString();
        }
        if (parcel.readByte() != 0) {
            byte[] bArr = new byte[parcel.readInt()];
            parcel.readByteArray(bArr);
            makeBitmapFromByteArray(bArr);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mLinkedinId);
        parcel.writeString(this.mFirstName);
        parcel.writeString(this.mLastName);
        parcel.writeInt(this.mConnectionLevel);
        boolean zHasEmail = hasEmail();
        parcel.writeByte(zHasEmail ? (byte) 1 : (byte) 0);
        if (zHasEmail) {
            parcel.writeString(this.mEmail);
        }
        boolean zHasHeadline = hasHeadline();
        parcel.writeByte(zHasHeadline ? (byte) 1 : (byte) 0);
        if (zHasHeadline) {
            parcel.writeString(this.mHeadline);
        }
        boolean zHasIndustry = hasIndustry();
        parcel.writeByte(zHasIndustry ? (byte) 1 : (byte) 0);
        if (zHasIndustry) {
            parcel.writeString(this.mIndustry);
        }
        boolean zHasLocation = hasLocation();
        parcel.writeByte(zHasLocation ? (byte) 1 : (byte) 0);
        if (zHasLocation) {
            parcel.writeString(this.mLocation);
        }
        boolean zHasPhotoUrl = hasPhotoUrl();
        parcel.writeByte(zHasPhotoUrl ? (byte) 1 : (byte) 0);
        if (zHasPhotoUrl) {
            parcel.writeString(this.mPhotoUrl);
        }
        boolean zHasPhoto = hasPhoto();
        parcel.writeByte(zHasPhoto ? (byte) 1 : (byte) 0);
        if (zHasPhoto) {
            byte[] photoByteArray = getPhotoByteArray();
            parcel.writeInt(photoByteArray.length);
            parcel.writeByteArray(photoByteArray);
        }
    }
}
