package database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class DBModel {

    PatientDAO patientDAO;
    EncounterDAO encounterDAO;
    MonitorDAO monitorDAO;
    ObservationDAO observationDAO;
    PractitionerDAO practitionerDAO;

    public DBModel() {
        this.patientDAO = new PatientRepository();
        this.encounterDAO = new EncounterRepository();
        this.observationDAO = new ObservationRepository();
        this.practitionerDAO = new PractitionerRepository();
        this.monitorDAO = new MonitorRepository();
    }

    public void onStart(String hPracId) throws IOException {

        String hPracIdentifier = "";

        // Try querying from the database first.
        hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);

        // If practitioner does not exist, then search for encounters.
        if (hPracIdentifier.equals("")) {
            // Log in with practitioner ID (NOT IDENTIFIER).
            // 1. Add practitioner to database.
            practitionerDAO.insertPracById(hPracId);
            // 2. Query database for practitioner identifier.
            hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);
            // 3. Populate database with encounters, patients and practitioners based on this identifier.
            System.out.println("Searching for encounters.");
            encounterDAO.insertEncountersByPrac(hPracIdentifier, patientDAO, practitionerDAO, observationDAO);
        }
    }

    public ArrayList<String> getPatientList(String hPracId) {
        // Get all names of patients of the practitioner with this ID.
        // 1. Get Practitioner Identifier.
        String hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);

        // 2. Query database for all practitioners with this identifier and return their ids.
        ArrayList<String> hPracIds = practitionerDAO.getHPracIds(hPracIdentifier);

        // 3. Query database for all encounters that match these ids and return list of patient ids.
        ArrayList<String> patientIds = encounterDAO.getPatientsByHPracId(hPracIds);

        // 4. Return sorted list of patients.
        ArrayList<String> finalList = patientDAO.getPatientIdsSorted(patientIds);

        return finalList;
    }

    public void updateCholesObs(String patientId) {
        observationDAO.insertLatestCholesObs(patientId);
    }

    public String getPatientFName(String patientId) {
        return patientDAO.getPatientFName(patientId);
    }

    public String getPatientLName(String patientId) {
        return patientDAO.getPatientLName(patientId);
    }

    public String getPatientAddressCity(String patientId) {
        return patientDAO.getPatientAddressCity(patientId);
    }

    public String getPatientAddressState(String patientId) {
        return patientDAO.getPatientAddressState(patientId);
    }

    public String getPatientAddressCountry(String patientId) {
        return patientDAO.getPatientAddressCountry(patientId);
    }

    public String getPatientBirthdate(String patientId) {
        return patientDAO.getPatientBirthdate(patientId);
    }

    public String getPatientGender(String patientId) {
        return patientDAO.getPatientGender(patientId);
    }

    public double getPatientLatestCholes(String patientId) {
        return observationDAO.getLatestCholesVal(patientId);
    }

    public Date getPatientLatestCholesDate(String patientId) {
        return observationDAO.getLatestCholesDate(patientId);
    }

    public void insertMonitorPatient(String hPracId, String patientId) {
        String hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);
        monitorDAO.insertPatient(hPracId, hPracIdentifier, patientId);
    }

    public void removeMonitorPatient(String hPracId, String patientId) {
        monitorDAO.removePatient(hPracId, patientId);
    }

    public ArrayList<String> getMonitoredPatients(String hPracId) {

        try {
            return monitorDAO.getMonitoredPatients(hPracId);
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

}
