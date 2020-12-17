package com.example.barcode.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class DriverLicense implements Parcelable {
    private String name;
    private String number;
    private String adress;
    private String adress2;
    private String contry;
    private Date dateOfBirth;
    private Date startDate;
    private String url;

    public DriverLicense() {
    }

    public DriverLicense(String name, String number, String adress, String adress2, String contry, Date dateOfBirth, Date startDate, String url) {
        this.name = name;
        this.number = number;
        this.adress = adress;
        this.adress2 = adress2;
        this.contry = contry;
        this.dateOfBirth = dateOfBirth;
        this.startDate = startDate;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getAdress2() {
        return adress2;
    }

    public void setAdress2(String adress2) {
        this.adress2 = adress2;
    }

    public String getContry() {
        return contry;
    }

    public void setContry(String contry) {
        this.contry = contry;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DriverLicense(Parcel in) {
        this.number = in.readString();
        this.name = in.readString();
        this.adress = in.readString();
        this.contry = in.readString();
        this.adress2 = in.readString();
        this.dateOfBirth = new Date(in.readLong());
        this.startDate = new Date(in.readLong());
        this.url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(number);
        parcel.writeString(name);
        parcel.writeString(adress);
        parcel.writeString(contry);
        parcel.writeString(adress2);
        parcel.writeLong(this.dateOfBirth.getTime());
        parcel.writeLong(this.startDate.getTime());
        parcel.writeString(url);
    }

    public static final Creator<DriverLicense> CREATOR = new Creator<DriverLicense>() {
        @Override
        public DriverLicense createFromParcel(Parcel in) {
            return new DriverLicense(in);
        }

        @Override
        public DriverLicense[] newArray(int size) {
            return new DriverLicense[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
}
