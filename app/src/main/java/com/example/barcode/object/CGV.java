package com.example.barcode.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class CGV implements Parcelable {
    private String idNUmber;
    private String numberCGV;
    private String name;
    private Date dateOfBirth;
    private String url;

    public CGV() {
    }

    public CGV(String idNUmber, String numberCGV, String name, Date dateOfBirth, String url) {
        this.idNUmber = idNUmber;
        this.numberCGV = numberCGV;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.url = url;
    }

    public String getIdNUmber() {
        return idNUmber;
    }

    public void setIdNUmber(String idNUmber) {
        this.idNUmber = idNUmber;
    }

    public String getNumberCGV() {
        return numberCGV;
    }

    public void setNumberCGV(String numberCGV) {
        this.numberCGV = numberCGV;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CGV(Parcel in) {
        this.idNUmber = in.readString();
        this.numberCGV = in.readString();
        this.name = in.readString();
        this.dateOfBirth = new Date(in.readLong());
        this.url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idNUmber);
        parcel.writeString(numberCGV);
        parcel.writeString(name);
        parcel.writeLong(this.dateOfBirth.getTime());
        parcel.writeString(url);
    }

    public static final Creator<CGV> CREATOR = new Creator<CGV>() {
        @Override
        public CGV createFromParcel(Parcel in) {
            return new CGV(in);
        }

        @Override
        public CGV[] newArray(int size) {
            return new CGV[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
