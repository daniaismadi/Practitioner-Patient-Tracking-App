package database;

import org.bson.Document;

import java.util.ArrayList;

public interface PatientDAO {

    String getPatientName(String patientId);
    String getPatientGender(String patientId);
    String getPatientBirthdate(String patientId);
    String getPatientAddress(String patientId);

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


}
