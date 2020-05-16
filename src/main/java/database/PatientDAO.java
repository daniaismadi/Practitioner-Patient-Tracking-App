package database;

import org.bson.Document;

import java.util.ArrayList;

public interface PatientDAO {

    ArrayList<String> getAllPatientIds();
    String getPatientName(String patientId);
    String getPatientLName(String patientId);
    String getPatientFName(String patientId);
    String getPatientGender(String patientId);
    String getPatientBirthdate(String patientId);
    String[] getPatientAddress(String patientId);
    String getPatientAddressCity(String patientId);
    String getPatientAddressState(String patientId);
    String getPatientAddressCountry(String patientId);

    /***
     * Return sorted list of patient names by ascending _id.
     *
     * @param patientIds    Array list of patient IDs.
     * @return
     */
    ArrayList<String> getPatientNamesByIds(ArrayList<String> patientIds);
    ArrayList<Document> getPatientsSorted(ArrayList<String> patientIds);
    String getPatientId(int position, ArrayList<String> patientIds);

    void insertPatient(String patientId);
    void insertPatientsByGender(String patientId);

    ArrayList<String> getPatientIdsSorted(ArrayList<String> patientIds);


}
