package com.stav.completenotes.firebase;

public class ReadWriteUserDetails {
    private String username, gender, phone, dob;

    public ReadWriteUserDetails(String username, String gender, String phone, String dob) {
        this.username = username;
        this.gender = gender;
        this.phone = phone;
        this.dob = dob;
    }

    public ReadWriteUserDetails(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
