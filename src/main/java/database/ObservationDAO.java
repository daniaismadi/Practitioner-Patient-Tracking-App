package database;

import java.util.ArrayList;
import java.util.Date;

public interface ObservationDAO {

    void insertPatientObsByCode(String patientId, String code, String count);
    void insertCholesObsByCodes(ArrayList<String> codes, String count, int pageCount);
    void insertLatestCholesObs(String patientId);
    void insertPatientLatestObsByCode(String patientId, String code);
    void insertObs(String obsId);
    Date getLatestCholesDate(String patientId);
    double getLatestCholesVal(String patientId);
}
