package database;

import org.bson.Document;

import java.util.ArrayList;

public interface MonitorDAO {

    void insertPatient(String hPracId, String hPracIdentifier, String patientId);
    void removePatient(String hPracId, String patientId);
    ArrayList<String> getMonitoredPatients(String hPracId);

}
