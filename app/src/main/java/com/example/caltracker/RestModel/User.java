package com.example.caltracker.RestModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Date;

public class User implements Parcelable {
    private Integer uid;
    private String name;
    private String surname;
    private String email;
    private Date dob;
    private BigDecimal height;
    private BigDecimal weight;
    private Character gender;
    private String address;
    private String postcode;
    private Character loa;
    private Integer steps;

    public User(String name, String surname, String email, Date dob, BigDecimal height, BigDecimal weight, Character gender, String address, String postcode,Character loa,Integer steps) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.address = address;
        this.postcode = postcode;
        this.loa = loa;
        this.steps = steps;
    }
    public  User()
    {
    }
    public  User(Parcel in)
    {
        this.name = in.readString();
        this.surname = in.readString();
        this.email = in.readString();
        this.dob = new Date(in.readLong());
        this.height = new BigDecimal(in.readString());
        this.weight = new BigDecimal(in.readString());
        this.gender = in.readString().charAt(0);
        this.address = in.readString();
        this.postcode = in.readString();
        this.loa = in.readString().charAt(0);
        this.steps = in.readInt();
        this.uid = in.readInt();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(surname);
        out.writeString(email);
        out.writeLong(dob.getTime());
        out.writeString(height.toString());
        out.writeString(weight.toString());
        out.writeString(String.valueOf(gender));
        out.writeString(address);
        out.writeString(postcode);
        out.writeString(String.valueOf(loa));
        out.writeInt(steps);
        out.writeInt(uid);
    }
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Character getLoa() {
        return loa;
    }

    public void setLoa(Character loa) {
        this.loa = loa;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }


}
