package database;

import org.json.JSONException;

import java.io.IOException;

public class DatabaseDriver {
    // To test queries.
    static PatientDAO patientDAO;
    static PractitionerDAO practitionerDAO;
    static ObservationDAO observationDAO;

    public static void main(String[] args) throws IOException, JSONException {
        // Establish connection with database.
        Mongo.connect();

        // Create instances.
        patientDAO = new PatientDAO(Mongo.db);
        practitionerDAO = new PractitionerDAO(Mongo.db);
        observationDAO = new ObservationDAO(Mongo.db);


        String practitionerId = "500";
        String patientId = "93991";

        // Populate database (do this once to populate your local database.
        // practitionerDAO.insertPractitionerPatients(practitionerId);

        // Get all names of patients of the practitioner with this id.
        practitionerDAO.getPracPatientNames(practitionerId);

        // Get latest cholesterol value and effective date time of the patient with this id.
        observationDAO.getLatestCholesValues(patientId);

        // Get patient's birth date.
        patientDAO.getPatientBirthdate(patientId);

        // Get patient's gender.
        patientDAO.getPatientGender(patientId);

        // Get patient's address with city, state and country.
        patientDAO.getPatientAddress(patientId);
    }
}
