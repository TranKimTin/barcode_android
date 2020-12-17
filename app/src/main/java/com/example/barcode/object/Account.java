package com.example.barcode.object;

public class Account {
    private String username;
    private  String password;
    private  Boolean idnumber_see;
    private  Boolean idnumber_update;
    private  Boolean driver_license_see;
    private  Boolean driver_license_update;
    private  Boolean cgv_see;
    private  Boolean cgv_update;
    private  Boolean student_card_see;
    private  Boolean student_card_update;

    public Account() {
    }

    public Account(String username, String password, Boolean idnumber_see, Boolean idnumber_update, Boolean driver_license_see, Boolean driver_license_update, Boolean cgv_see, Boolean cgv_update, Boolean student_card_see, Boolean student_card_update) {
        this.username = username;
        this.password = password;
        this.idnumber_see = idnumber_see;
        this.idnumber_update = idnumber_update;
        this.driver_license_see = driver_license_see;
        this.driver_license_update = driver_license_update;
        this.cgv_see = cgv_see;
        this.cgv_update = cgv_update;
        this.student_card_see = student_card_see;
        this.student_card_update = student_card_update;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIdnumber_see() {
        return idnumber_see;
    }

    public void setIdnumber_see(Boolean idnumber_see) {
        this.idnumber_see = idnumber_see;
    }

    public Boolean getIdnumber_update() {
        return idnumber_update;
    }

    public void setIdnumber_update(Boolean idnumber_update) {
        this.idnumber_update = idnumber_update;
    }

    public Boolean getDriver_license_see() {
        return driver_license_see;
    }

    public void setDriver_license_see(Boolean driver_license_see) {
        this.driver_license_see = driver_license_see;
    }

    public Boolean getDriver_license_update() {
        return driver_license_update;
    }

    public void setDriver_license_update(Boolean driver_license_update) {
        this.driver_license_update = driver_license_update;
    }

    public Boolean getCgv_see() {
        return cgv_see;
    }

    public void setCgv_see(Boolean cgv_see) {
        this.cgv_see = cgv_see;
    }

    public Boolean getCgv_update() {
        return cgv_update;
    }

    public void setCgv_update(Boolean cgv_update) {
        this.cgv_update = cgv_update;
    }

    public Boolean getStudent_card_see() {
        return student_card_see;
    }

    public void setStudent_card_see(Boolean student_card_see) {
        this.student_card_see = student_card_see;
    }

    public Boolean getStudent_card_update() {
        return student_card_update;
    }

    public void setStudent_card_update(Boolean student_card_update) {
        this.student_card_update = student_card_update;
    }
}
