package database;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseDriver {
    // To test queries.
    private static PatientRepository patientDAO;
    private static PractitionerDAO practitionerDAO;
    private static ObservationDAO observationDAO;
    private static EncounterDAO encounterDAO;
    private static MonitorDAO monitorDAO;

    public static void main(String[] args) throws IOException, JSONException {
        // Establish connection with database.
        Mongo.connect();

        // Create instances.
        patientDAO = new PatientRepository();
        encounterDAO = new EncounterRepository();
        practitionerDAO = new PractitionerRepository();
        observationDAO = new ObservationRepository();
        monitorDAO = new MonitorRepository();

        String patientId = "93991";
        int position = 0;

        // Step 1: Log in with practitioner id.
        String hPracId = "29175";

        // Step 2: Return a list of names of this practitioner's patients.
        ArrayList<String> patientNames = onLogIn(hPracId);
        System.out.println(patientNames);

        // Step 3: Practitioner selects a patient to monitor. Gets position of list view clicked from UI which
        // corresponds to the position of the patient in the array returned in step 2. Add patient to monitor list.
        // monitorNewPatient(position, hPracId);

        // Step 4: Ability to remove patient.
        // removeMonitoredPatient(position, hPracId);

        // Step 5: Get monitored patients.
        ArrayList<String> monitoredPatients = monitorDAO.getMonitoredPatients(hPracId);
        System.out.println(monitoredPatients);

        // Step 6: Get cholesterol level of all monitored patients.
        // HashMap<String, String[]> cholesValues = getCholesMonitoredPatients(hPracId);

        // cholesValues.forEach((k, v) -> System.out.println(k + " : " + (v[1]) + " collected during " + v[0]));

    }

    static ArrayList<String> onLogIn(String hPracId) throws IOException {
        // Log in with practitioner ID (NOT IDENTIFIER).
        // 1. Add practitioner to database.
        practitionerDAO.insertPracById(hPracId);
        // 2. Query database for practitioner identifier.
        String hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);
        // 3. Populate database with encounters, patients and practitioners based on this identifier.
        System.out.println("Searching for encounters.");
        // encounterDAO.insertEncountersByPrac(hPracIdentifier, patientDAO, practitionerDAO, observationDAO);

        // Get all names of patients of the practitioner with this ID.
        // 1. Get Practitioner Identifier.
        hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);
        // System.out.println(hPracIdentifier);

        // 2. Query database for all practitioners with this identifier and return their ids.
        ArrayList<String> hPracIds = practitionerDAO.getHPracIds(hPracIdentifier);
        // System.out.println(hPracIds);

        // 3. Query database for all encounters that match these ids and return list of patient ids.
        ArrayList<String> patientIds = encounterDAO.getPatientsByHPracId(hPracIds);
        // System.out.println(patientIds);

        // 4. Return sorted list of patients.
        ArrayList<String> patientNames = patientDAO.getPatientNamesByIds(patientIds);
        return patientNames;
    }

    static void monitorNewPatient(int position, String hPracId) {
        String hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);
        ArrayList<String> hPracIds = practitionerDAO.getHPracIds(hPracIdentifier);
        ArrayList<String> patientIds = encounterDAO.getPatientsByHPracId(hPracIds);
        String patient = patientDAO.getPatientId(position, patientIds);
        monitorDAO.insertPatient(hPracId, hPracIdentifier, patient);
    }

    static void removeMonitoredPatient(int position, String hPracId) {
        String hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);
        ArrayList<String> hPracIds = practitionerDAO.getHPracIds(hPracIdentifier);
        ArrayList<String> patientIds = encounterDAO.getPatientsByHPracId(hPracIds);
        String patient = patientDAO.getPatientId(position, patientIds);
        monitorDAO.removePatient(hPracId, patient);
    }


//    static HashMap<String, String[]> getCholesMonitoredPatients(String hPracId) {
//        HashMap<String, String[]> cholesValues = new HashMap<>();
//
//        ArrayList<String> patients = monitorDAO.getMonitoredPatients(hPracId);
//        for (String patientId : patients) {
//            String[] choles = observationDAO.getLatestCholesDateVals(patientId);
//            if (choles != null) {
//                cholesValues.put(patientId, choles);
//            }
//        }
//
//        return cholesValues;
//    }
}
