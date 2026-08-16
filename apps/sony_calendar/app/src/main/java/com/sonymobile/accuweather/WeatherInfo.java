package com.sonymobile.accuweather;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class WeatherInfo implements Parcelable {
    public static final Parcelable.Creator<WeatherInfo> CREATOR = new Parcelable.Creator<WeatherInfo>() { // from class: com.sonymobile.accuweather.WeatherInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WeatherInfo createFromParcel(Parcel parcel) {
            return new WeatherInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WeatherInfo[] newArray(int i) {
            return new WeatherInfo[i];
        }
    };
    private static final int INVALID_TEMPERATURE = 9999;
    public String forecastUrl;
    public int highTemperature;
    public int iconResource;
    public int iconStringDescription;
    public boolean isToday;
    public int lowTemperature;
    public String mCityName;
    public String mCountryName;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WeatherInfo() {
        this.lowTemperature = INVALID_TEMPERATURE;
    }

    protected WeatherInfo(Parcel parcel) {
        this.lowTemperature = INVALID_TEMPERATURE;
        this.iconResource = parcel.readInt();
        this.highTemperature = parcel.readInt();
        this.lowTemperature = parcel.readInt();
        this.forecastUrl = parcel.readString();
        this.iconStringDescription = parcel.readInt();
        this.isToday = parcel.readInt() == 1;
        this.mCityName = parcel.readString();
        this.mCountryName = parcel.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.iconResource);
        parcel.writeInt(this.highTemperature);
        parcel.writeInt(this.lowTemperature);
        parcel.writeString(this.forecastUrl);
        parcel.writeInt(this.iconStringDescription);
        parcel.writeInt(this.isToday ? 1 : 0);
        parcel.writeString(this.mCityName);
        parcel.writeString(this.mCountryName);
    }

    public boolean hasLowTemperature() {
        return this.lowTemperature != INVALID_TEMPERATURE;
    }

    public int hashCode() {
        return 31 + this.iconResource;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.iconResource == ((WeatherInfo) obj).iconResource;
    }
}
