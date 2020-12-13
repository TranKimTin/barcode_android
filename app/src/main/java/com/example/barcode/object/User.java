package com.example.barcode.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements Parcelable {
    private String id;
    private String name;
    private Date dateOfBirth;
    private String adress;
    private String phoneNumber;
    private String CMND;
    private List<String> subName;

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        generatorSubName();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getSubName() {
        return subName;
    }

    public void setSubName(List<String> subName) {
        this.subName = subName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCMND() {
        return CMND;
    }

    public void setCMND(String CMND) {
        this.CMND = CMND;
        this.id = MD5(CMND);
    }

    public User(){
        this.dateOfBirth = new Date(0);
        this.subName = new ArrayList<String>();
        subName.add("");
    }

    public User(String id, String name, Date dateOfBirth, String adress, String phoneNumber, String CMND) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        this.CMND = CMND;
    }
    public User(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.dateOfBirth = new Date(in.readLong());
        this.adress = in.readString();
        this.phoneNumber = in.readString();
        this.CMND = in.readString();

        generatorSubName();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.dateOfBirth.getTime());
        dest.writeString(this.adress);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.CMND);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,2));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private void generatorSubName(){
        String lowerName = this.name.toLowerCase();
        this.subName = new ArrayList<String>();
        Set<String> set = new HashSet<String>();
        set.add("");
        for(int i=0; i<lowerName.length()-1; i++){
            if(lowerName.charAt(i) == ' ') continue;
            for(int j=i+1; j<=lowerName.length(); j++){
                set.add(lowerName.substring(i,j).trim());
            }
        }
        this.subName.addAll(set);
    }
}
