package database;

import java.util.ArrayList;
import java.util.Date;

public interface ObservationDAO {

    void insertCholesObsByCodes(ArrayList<String> codes, String count, int pageCount);
    void insertLatestCholesObs(String patientId);
    void insertPatientLatestObsByCode(String patientId, String code);
    void insertObs(String obsId);
    ArrayList<String> getAllPatientsIdsObs();
    Date getLatestCholesDate(String patientId);
    double getLatestCholesVal(String patientId);
}
