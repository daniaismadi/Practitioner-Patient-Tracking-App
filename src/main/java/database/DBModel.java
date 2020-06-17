package database;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * The model class that connects the views and controllers to the database where the information is stored.
 */
public class DBModel {

    /***
     * To retrieve and access information about patients.
     */
    PatientDAO patientDAO;

    /***
     * To retrieve and access information about encounters.
     */
    EncounterDAO encounterDAO;

//    /***
//     * To retrieve and access information about monitored patients.
//     */
//    MonitorDAO monitorDAO;

    /**
     * To retrieve and access information about observations.
     */
    ObservationDAO observationDAO;

    /**
     * To retrieve and access information about practitioners.
     */
    PractitionerDAO practitionerDAO;

    /***
     * Class constructor for DBModel. Initialises PatientDAO, EncounterDAO, ObservationDAO, PractitionerDAO and
     * MonitorDAO to their corresponding repositories.
     */
    public DBModel() {
        this.patientDAO = new PatientRepository();
        this.encounterDAO = new EncounterRepository();
        this.observationDAO = new ObservationRepository();
        this.practitionerDAO = new PractitionerRepository();
//        this.monitorDAO = new MonitorRepository();
    }

    /***
     * Invoked when the practitioner logs in to the application. Searches for the identifier of the practitioner with
     * this hPracId. Fetches new encounters if fetchEncounters is true. Fetches new observations if fetchObs is true.
     * If practitioner does not exist in this database yet, then will automatically fetch new encounters and
     * fetch new observations.
     *
     * @param hPracId           The ID of the practitioner.
     * @param fetchEncounters   If True, fetch new encounters of this practitioner, otherwise do not.
     * @param fetchObs          If True, fetch new observations of the patients of this practitioner, otherwise do not.
     * @throws IOException      Occurs if there is an error in retrieving information from the server.
     * @throws JSONException    Occurs if there is an error in retrieving the JSON resource document from the server.
     */
    public void onStart(String hPracId, boolean fetchEncounters, boolean fetchObs) throws IOException, JSONException {

        String hPracIdentifier = "";

        // Try querying from the database first.
        hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);

        if (hPracIdentifier.equals("")) {
            // Override both fetchEncounters and fetchObs to true because practitioner does not exist in the database yet.
            fetchEncounters = true;
            fetchObs = true;
        }

        // If fetchEncounters is true, fetch all encounters.
        if (fetchEncounters) {
            // Log in with practitioner ID (NOT IDENTIFIER).
            // 1. Add practitioner to database.
            practitionerDAO.insertPracById(hPracId);
            // 2. Query database for practitioner identifier.
            hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);
            // 3. Populate database with encounters, patients and practitioners based on this identifier.
            System.out.println("Searching for encounters.");
            encounterDAO.insertEncountersByPrac(hPracIdentifier, patientDAO, practitionerDAO);
        }

        // If fetchObs is true, fetch latest observations.
        if (fetchObs) {
            // Get all patients of this practitioner.
            ArrayList<String> patientIds = getPatientList(hPracId);

            // Insert required observations.
            for (String id : patientIds){
                // Insert latest cholesterol observations.
                observationDAO.insertCholesterolObs(id, 1);
                // Insert the latest 5 blood pressure observations.
                observationDAO.insertBPObs(id, 5);
            }
        }
    }

    /***
     * Return the list of patients of this practitioner.
     *
     * @param hPracId   the ID of this practitioner
     * @return          the list of patient IDs of this practitioner
     */
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

    /***
     * Update cholesterol observation of this patient by inserting latest cholesterol observation from the server.
     *
     * @param patientId     The ID of this patient.
     */
    public void updateCholesObs(String patientId) {
        observationDAO.insertCholesterolObs(patientId, 1);
    }

    /***
     * Update blood pressure observations of this patient by inserting the latest 5 observations from the server.
     *
     * @param patientId     The ID of this patient.
     */
    public void updateBPObs(String patientId) {
        observationDAO.insertBPObs(patientId, 5);
    }

    /***
     * Get the given name of this patient (could be more than one word).
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The given name of this patient.
     */
    public String getPatientFName(String patientId) {
        return patientDAO.getPatientFName(patientId);
    }

    /***
     * Get the last name of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The last name of this patient.
     */
    public String getPatientLName(String patientId) {
        return patientDAO.getPatientLName(patientId);
    }

    /***
     * Return the city where this patient is from.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The city where this patient is from.
     */
    public String getPatientAddressCity(String patientId) {
        return patientDAO.getPatientAddressCity(patientId);
    }

    /***
     * Return the state where this patient is from.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The state where this patient is from.
     */
    public String getPatientAddressState(String patientId) {
        return patientDAO.getPatientAddressState(patientId);
    }

    /***
     * Return the country where this patient is from.
     *
     * @param patientId     the ID of the patient to retrieve information from
     * @return              the country where this patient is from
     */
    public String getPatientAddressCountry(String patientId) {
        return patientDAO.getPatientAddressCountry(patientId);
    }

    /***
     * Get the date of birth of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The date of birth of the patient as a string.
     */
    public String getPatientBirthdate(String patientId) {
        return patientDAO.getPatientBirthdate(patientId);
    }

    /***
     * Get the gender of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The gender of the patient.
     */
    public String getPatientGender(String patientId) {
        return patientDAO.getPatientGender(patientId);
    }

    /***
     * Get latest total cholesterol value of this patient.
     *
     * @param patientId     The ID of this patient.
     * @return              The latest total cholesterol value.
     */
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

    /***
     * Get date of when the latest total cholesterol measurement was taken for this patient.
     *
     * @param patientId     The ID of this patient.
     * @return              The latest date.
     */
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

//    public void insertMonitorPatient(String hPracId, String patientId) {
//        String hPracIdentifier = practitionerDAO.getHPracIdentifier(hPracId);
//        monitorDAO.insertPatient(hPracId, hPracIdentifier, patientId);
//    }
//
//    public void removeMonitorPatient(String hPracId, String patientId) {
//        monitorDAO.removePatient(hPracId, patientId);
//    }
//
//    public ArrayList<String> getMonitoredPatients(String hPracId) {
//
//        try {
//            return monitorDAO.getMonitoredPatients(hPracId);
//        } catch (NullPointerException ex) {
//            return new ArrayList<>();
//        }
//    }

}
