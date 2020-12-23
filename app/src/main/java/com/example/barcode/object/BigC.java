package com.example.barcode.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class BigC implements Parcelable {
    private String numberBigC;
    private String name;
    private String url;

    public BigC() {
    }

    public BigC(String numberBigC, String name, String url) {
        this.numberBigC = numberBigC;
        this.name = name;
        this.url = url;
    }

    public String getNumberBigC() {
        return numberBigC;
    }

    public void setNumberBigC(String numberBigC) {
        this.numberBigC = numberBigC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BigC(Parcel in) {
        this.numberBigC = in.readString();
        this.name = in.readString();
        this.url = in.readString();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(numberBigC);
        parcel.writeString(name);
        parcel.writeString(url);
    }

    public static final Creator<BigC> CREATOR = new Creator<BigC>() {
        @Override
        public BigC createFromParcel(Parcel in) {
            return new BigC(in);
        }

        @Override
        public BigC[] newArray(int size) {
            return new BigC[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
