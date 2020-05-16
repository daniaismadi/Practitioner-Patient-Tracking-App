package database;

import java.util.ArrayList;
import java.util.Date;

public interface ObservationDAO {

    void insertPatientObservationsByCode(String patientId, String code);
    void insertCholesObsByCodes(ArrayList<String> codes, String count, int pageCount);
    void insertLatestCholesObs(String patientId);
    void insertLatestBMIObs(String patientId);
    void insertLatestHRObs(String patientId);
    void insertLatestBodyWeightObs(String patientId);
    void insertLatestRespHRObs(String patientId);
    void insertPatientLatestObsByCode(String patientId, String code);
    void insertObs(String obsId);
    Date getLatestCholesDate(String patientId);
    double getLatestCholesVal(String patientId);
}
