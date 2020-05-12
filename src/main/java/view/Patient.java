package view;

import java.util.Date;

public class Patient {
    private String id;
    private String familyName;
    private String givenName;
    private String birthDate;
    private String gender;
    private String city;
    private String state;
    private String country;
    private double totalCholesterol;
    private Date latestCholesterolDate;

    public Patient(){
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getTotalCholesterol() {
        return totalCholesterol;
    }

    public void setTotalCholesterol(double totalCholesterol) {
        this.totalCholesterol = totalCholesterol;
    }

    @Override
    public String toString() {
        return getGivenName()+" "+getFamilyName();
    }

    public Date getLatestCholesterolDate() {
        return latestCholesterolDate;
    }

    public void setLatestCholesterolDate(Date latestCholesterolDate) {
        this.latestCholesterolDate = latestCholesterolDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
