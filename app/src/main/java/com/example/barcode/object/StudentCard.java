package com.example.barcode.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class StudentCard implements Parcelable {
    private String studentCode;
    private String name;
    private Date dateOfBirth;
    private Date endtDate;
    private String major;
    private String classes;
    private String gender;
    private String url;

    public StudentCard() {
    }

    public StudentCard(String studentCode, String name, Date dateOfBirth, Date endtDate, String major, String classes, String gender, String url) {
        this.studentCode = studentCode;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.endtDate = endtDate;
        this.major = major;
        this.classes = classes;
        this.gender = gender;
        this.url = url;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
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

    public Date getEndtDate() {
        return endtDate;
    }

    public void setEndtDate(Date endtDate) {
        this.endtDate = endtDate;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public StudentCard(Parcel in) {
        this.studentCode = in.readString();
        this.name = in.readString();
        this.major = in.readString();
        this.classes = in.readString();
        this.gender = in.readString();
        this.dateOfBirth = new Date(in.readLong());
        this.endtDate = new Date(in.readLong());
        this.url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(studentCode);
        parcel.writeString(name);
        parcel.writeString(major);
        parcel.writeString(classes);
        parcel.writeString(gender);
        parcel.writeLong(this.dateOfBirth.getTime());
        parcel.writeLong(this.endtDate.getTime());
        parcel.writeString(url);
    }

    public static final Creator<StudentCard> CREATOR = new Creator<StudentCard>() {
        @Override
        public StudentCard createFromParcel(Parcel in) {
            return new StudentCard(in);
        }

        @Override
        public StudentCard[] newArray(int size) {
            return new StudentCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
