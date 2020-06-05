package database;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void onStart(String hPracId) throws IOException, JSONException {

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
            encounterDAO.insertEncountersByPrac(hPracIdentifier, patientDAO, practitionerDAO);
        }

        // Get all patients of this practitioner.
        ArrayList<String> patientIds = getPatientList(hPracId);

        // Insert required observations.
//        for (String id : patientIds){
//            // Insert latest cholesterol observations.
//            observationDAO.insertCholesterolObs(id, 1);
//            // Insert the latest 5 blood pressure observations.
//            observationDAO.insertBPObs(id, 5);
//        }
    }

    public ArrayList<String> getPatientList(String hPracId) {
        // Get all IDs of patients of the practitioner with this ID.
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
        observationDAO.insertCholesterolObs(patientId, 1);
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
        try {
            // get latest cholesterol observations
            List<Object[]> cholesterolObs =  observationDAO.getCholesterolObs(patientId, 1);

            if (cholesterolObs.size() == 0) {
                // patient has no cholesterol observations
                return 0;
            }

            // return latest cholesterol observation
            return (double)cholesterolObs.get(0)[1];

        } catch (ParseException e) {
            // patient has no cholesterol observations
            return 0;
        }
    }

    public Date getPatientLatestCholesDate(String patientId) {
        try {
            // get latest cholesterol observations
            List<Object[]> cholesterolObs =  observationDAO.getCholesterolObs(patientId, 1);

            if (cholesterolObs.size() == 0) {
                // patient has no cholesterol observations
                return null;
            }

            // return latest cholesterol observation
            return (Date)cholesterolObs.get(0)[0];

        } catch (ParseException e) {
            // patient has no cholesterol observations
            return null;
        }
    }

    /***
     * Retrieve latest k number of systolic blood pressure measurements of patient, where k is count.
     *
     * @param patientId     The ID of the patient
     * @param count         The number of blood pressure measurements to retrieve
     * @return              A list of an array of objects which is in the format of [Date, Systolic BP Value] sorted
     *                      in descending order of Date. Date is of type Date and Systolic BP Value is of type
     *                      double.
     */
    public List<Object[]> getPatientSystolicBPs(String patientId, int count) {
        try {
            return observationDAO.getSystolicBPObs(patientId, count);
        } catch (ParseException e) {
            // return empty list
            return new ArrayList<Object[]>(){};
        }
    }

    /***
     * Retrieve latest k number of diastolic blood pressure measurements of patient, where k is count.
     *
     * @param patientId     The ID of the patient
     * @param count         The number of blood pressure measurements to retrieve
     * @return              A list of an array of objects which is in the format of [Date, Diastolic BP Value] sorted
     *                      in descending order of Date. Date is of type Date and Diastolic BP Value is of type
     *                      double.
     */
    public List<Object[]> getPatientDiastolicBPs(String patientId, int count) {
        try {
            return observationDAO.getDiastolicBPObs(patientId, count);
        } catch (ParseException e) {
            // return empty list
            return new ArrayList<Object[]>(){};
        }
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
