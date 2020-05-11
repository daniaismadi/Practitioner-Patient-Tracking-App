package database;

import java.util.ArrayList;

public interface EncounterDAO {

    ArrayList<String> getPatientsByHPracId(ArrayList<String> hPracIds);
    void insertEncounter(String encounterId);
    void insertEncountersByPrac(String identifier);
    void insertEncountersByPatient(String patientId);

}
