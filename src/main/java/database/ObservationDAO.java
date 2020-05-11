package database;

import java.util.ArrayList;

public interface ObservationDAO {

    void insertPatientObservations(String patientId);
    void insertObs(String obsId);
    ArrayList<String> getAllCholesObs(String patientId);
    String[] getLatestCholesDateVals(String patientId);
}
