package database;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public interface EncounterDAO {

    ArrayList<String> getPatientsByHPracId(ArrayList<String> hPracIds);
    void insertEncounter(String encounterId);
    void insertEncountersByPrac(String identifier, PatientDAO patientDAO, PractitionerDAO practitionerDAO,
                                ObservationDAO observationDAO) throws IOException, JSONException;

}
