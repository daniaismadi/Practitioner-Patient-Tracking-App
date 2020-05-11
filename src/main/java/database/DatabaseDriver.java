package database;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DatabaseDriver {
    // To test queries.
    private static PatientRepository patientRepository;
    private static PractitionerRepository practitionerRepository;
    private static ObservationRepository observationRepository;
    private static EncounterDAO encounterDAO;

    public static void main(String[] args) throws IOException, JSONException {
        // Establish connection with database.
        Mongo.connect();

        // Create instances.
        patientRepository = new PatientRepository();
        observationRepository = new ObservationRepository();
        encounterDAO = new EncounterRepository();
        practitionerRepository = new PractitionerRepository();

        String patientId = "93991";

        onCreate();

//        // Get latest cholesterol value and effective date time of the patient with this id.
//        // TODO: There's a bug in this.
//        String[] values = observationRepository.getLatestCholesDateVals(patientId);
//        System.out.println("Date: " + values[0] + " Cholesterol: " + values[1]);
//
//        // Get patient's birth date.
//        System.out.println(patientRepository.getPatientBirthdate(patientId));
//
//        // Get patient's gender.
//        System.out.println(patientRepository.getPatientGender(patientId));
//
//        // Get patient's address with city, state and country.
//        System.out.println(patientRepository.getPatientAddress(patientId));
    }

    static void onCreate() {
        String practitionerId = "29175";

        // Log in with practitioner ID (NOT IDENTIFIER).
        // 1. Add practitioner to database.
        practitionerRepository.insertPracById(practitionerId);
        // 2. Query database for practitioner identifier.
        String hPracIdentifier = practitionerRepository.getHPracIdentifier(practitionerId);
        // 3. Populate database with encounters, patients and practitioners based on this identifier.
        System.out.println("Searching for encounters.");
        encounterDAO.insertEncountersByPrac(hPracIdentifier);

        // Get all names of patients of the practitioner with this ID.
        // 1. Get Practitioner Identifier.
        hPracIdentifier = practitionerRepository.getHPracIdentifier(practitionerId);
        // System.out.println(hPracIdentifier);

        // 2. Query database for all practitioners with this identifier and return their ids.
        ArrayList<String> hPracIds = practitionerRepository.getHPracIds(hPracIdentifier);
        // System.out.println(hPracIds);

        // 3. Query database for all encounters that match these ids and return list of patient ids.
        ArrayList<String> patientIds = encounterDAO.getPatientsByHPracId(hPracIds);
        // System.out.println(patientIds);

        // 4. Return sorted list of patients.
        ArrayList<String> patientNames = patientRepository.getPatientNamesByIds(patientIds);
        System.out.println(patientNames);
    }
}
