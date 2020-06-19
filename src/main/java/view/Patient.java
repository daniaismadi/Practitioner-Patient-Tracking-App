package view;

import java.util.Date;
import java.util.List;

public class Patient {

    /**
     * The ID of this patient.
     */
    private String id;

    /**
     * The family name of this patient.
     */
    private String familyName;

    /**
     * The given name of this patient.
     */
    private String givenName;

    /**
     * The date of birth of this patient.
     */
    private String birthDate;

    /**
     * The gender of this patient.
     */
    private String gender;

    /**
     * The city where this patient is from.
     */
    private String city;

    /**
     * The state where this patient is from.
     */
    private String state;

    /**
     * The country where this patient is from.
     */
    private String country;

    /**
     * The latest total cholesterol measurement of this patient.
     */
    private double totalCholesterol;

    /**
     * The date of the latest total cholesterol measurement of this patient.
     */
    private Date latestCholesterolDate;

    /**
     * The latest systolic blood pressure measurements of this patient (sorted from latest date to oldest date).
     */
    private List<Object[]> systolicBPs;

    /**
     * The latest diastolic blood pressure measurements of this patient (sorted from latest date to oldest date).
     */
    private List<Object[]> diastolicBPs;

    /***
     * Class constructor for Patient.
     */
    public Patient(){
    }

    /***
     * Return the systolic blood pressure measurements of this patient sorted in descending order (latest date first).
     *
     * @return  list of object arrays in the format of [Date, Systolic BP Measurement]
     */
    public List<Object[]> getSystolicBPs() {
        return systolicBPs;
    }

    /***
     * Set the systolic blood pressure measurements of this patient sorted in descending order (latest date first).
     *
     * @param systolicBPs   list of object arrays in the format of [Date, Systolic BP Measurement]
     */
    public void setSystolicBPs(List<Object[]> systolicBPs) {
        this.systolicBPs = systolicBPs;
    }

    /***
     * Return the diastolic blood pressure measurements of this patient sorted in descending order (latest date first).
     *
     * @return  list of object arrays in the format of [Date, Diastolic BP Measurement]
     */
    public List<Object[]> getDiastolicBPs() {
        return diastolicBPs;
    }

    /***
     * Set the systolic blood pressure measurements of this patient sorted in descending order (latest date first).
     *
     * @param diastolicBPs   list of object arrays in the format of [Date, Systolic BP Measurement]
     */
    public void setDiastolicBPs(List<Object[]> diastolicBPs) {
        this.diastolicBPs = diastolicBPs;
    }

    /***
     * Return the family name of this patient.
     *
     * @return  the family name of this patient
     */
    public String getFamilyName() {
        return familyName;
    }

    /***
     * Set the family name of this patient.
     *
     * @param familyName    the family name of this patient
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /***
     * Return the given name of this patient.
     *
     * @return  the given name of this patient
     */
    public String getGivenName() {
        return givenName;
    }

    /***
     * Set the given name of this patient.
     *
     * @param givenName     the given name of this patient
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /***
     * Return the date of birth of this patient.
     *
     * @return      the date of birth of this patient
     */
    public String getBirthDate() {
        return birthDate;
    }

    /***
     * Set the birth date of this patient.
     *
     * @param birthDate     the birth date of this patient
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    /***
     * Return the gender of this patient.
     *
     * @return      the gender of this patient
     */
    public String getGender() {
        return gender;
    }

    /***
     * Set the gender of this patient.
     *
     * @param gender    the gender of this patient
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /***
     * Return the city where this patient is from.
     *
     * @return      the city where this patient is from
     */
    public String getCity() {
        return city;
    }

    /***
     * Set the city where this patient is from.
     *
     * @param city  the city where this patient is from
     */
    public void setCity(String city) {
        this.city = city;
    }

    /***
     * Return the state where this patient is from.
     *
     * @return  the state where this patient is from
     */
    public String getState() {
        return state;
    }

    /***
     * Set the state where this patient is from.
     *
     * @param state     the state where this patient is from
     */
    public void setState(String state) {
        this.state = state;
    }

    /***
     * Return the country where this patient is from.
     *
     * @return      the country where this patient is from
     */
    public String getCountry() {
        return country;
    }

    /***
     * Set the country where this patient is from.
     *
     * @param country       the country where this patient is from
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /***
     * Return the total cholesterol measurement of this patient.
     *
     * @return      the total cholesterol measurement of this patient
     */
    public double getTotalCholesterol() {
        return totalCholesterol;
    }

    /***
     * Set the total cholesterol measurement of this patient.
     *
     * @param totalCholesterol      the total cholesterol measurement of this patient
     */
    public void setTotalCholesterol(double totalCholesterol) {
        this.totalCholesterol = totalCholesterol;
    }

    /***
     * Return the full name of the patient.
     *
     * @return      the full name of the patient
     */
    @Override
    public String toString() {
        return getGivenName()+" "+getFamilyName();
    }

    /***
     * Return true if this patient is the same as the other patient (as compared by the patient ID).
     *
     * @param other     the other object to compare to
     * @return          true, if patient is the same as the other patient, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this.getClass() != other.getClass()) {
            return false;
        }

        Patient otherPatient = (Patient)other;
        String otherId = otherPatient.getId();
        return this.id.equalsIgnoreCase(otherId);
    }

    /***
     * Return the date of the latest cholesterol measurement of this patient.
     *
     * @return      the date of the latest cholesterol measurement of this patient
     */
    public Date getLatestCholesterolDate() {
        return latestCholesterolDate;
    }

    /***
     * Set the date of the latest cholesterol measurement of this patient.
     *
     * @param latestCholesterolDate     the date of the latest cholesterol measurement of this patient
     */
    public void setLatestCholesterolDate(Date latestCholesterolDate) {
        this.latestCholesterolDate = latestCholesterolDate;
    }

    /***
     * Get the ID of this patient.
     *
     * @return  the ID of this patient
     */
    public String getId() {
        return id;
    }

    /***
     * Set the ID of this patient.
     *
     * @param id    the ID of this patient
     */
    public void setId(String id) {
        this.id = id;
    }
}
