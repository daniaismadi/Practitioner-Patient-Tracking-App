package database;

import java.util.ArrayList;
import java.util.Date;

public interface ObservationDAO {

    void insertPatientObservations(String patientId);
    void insertObs(String obsId);
    Date getLatestCholesDate(String patientId);
    double getLatestCholesVal(String patientId);
}
