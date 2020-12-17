package com.example.barcode.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class IdNumber implements Parcelable {
    private String idNUmber;
    private String name;
    private Date dateOfBirth;
    private Date startDate;
    private String adress;
    private String region;
    private String adress2;
    private String gender;
    private String url;

    public IdNumber() {
    }

    public IdNumber(String idNUmber, String name, Date dateOfBirth, Date startDate, String adress, String region, String adress2, String gender, String url) {
        this.idNUmber = idNUmber;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.startDate = startDate;
        this.adress = adress;
        this.region = region;
        this.adress2 = adress2;
        this.gender = gender;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getIdNUmber() {
        return idNUmber;
    }

    public void setIdNUmber(String idNUmber) {
        this.idNUmber = idNUmber;
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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAdress2() {
        return adress2;
    }

    public void setAdress2(String adress2) {
        this.adress2 = adress2;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public IdNumber(Parcel in) {
        this.idNUmber = in.readString();
        this.name = in.readString();
        this.adress = in.readString();
        this.region = in.readString();
        this.adress2 = in.readString();
        this.gender = in.readString();
        this.dateOfBirth = new Date(in.readLong());
        this.startDate = new Date(in.readLong());
        this.url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idNUmber);
        parcel.writeString(name);
        parcel.writeString(adress);
        parcel.writeString(region);
        parcel.writeString(adress2);
        parcel.writeString(gender);
        parcel.writeLong(this.dateOfBirth.getTime());
        parcel.writeLong(this.startDate.getTime());
        parcel.writeString(url);
    }

    public static final Creator<IdNumber> CREATOR = new Creator<IdNumber>() {
        @Override
        public IdNumber createFromParcel(Parcel in) {
            return new IdNumber(in);
        }

        @Override
        public IdNumber[] newArray(int size) {
            return new IdNumber[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
