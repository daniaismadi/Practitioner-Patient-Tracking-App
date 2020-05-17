package database;

import org.bson.Document;

import java.util.ArrayList;

public interface PatientDAO {

    String getPatientName(String patientId);
    String getPatientLName(String patientId);
    String getPatientFName(String patientId);
    String getPatientGender(String patientId);
    String getPatientBirthdate(String patientId);
    String getPatientAddressCity(String patientId);
    String getPatientAddressState(String patientId);
    String getPatientAddressCountry(String patientId);
    ArrayList<String> getPatientIdsSorted(ArrayList<String> patientIds);

    void insertPatient(String patientId);

}
