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
        patientDAO = new PatientDAO();
        practitionerDAO = new PractitionerDAO();
        observationDAO = new ObservationDAO();


        String practitionerId = "500";
        String patientId = "93991";

        // Populate database (do this once to populate your local database.
         practitionerDAO.insertPractitionerPatients(practitionerId);

        // Get all names of patients of the practitioner with this id.
        System.out.println(practitionerDAO.getPracPatientNames(practitionerId));

        // Get latest cholesterol value and effective date time of the patient with this id.
        // TODO: There's a bug in this.
        String[] values = observationDAO.getLatestCholesValues(patientId);
        System.out.println("Date: " + values[0] + " Cholesterol: " + values[1]);

        // Get patient's birth date.
        System.out.println(patientDAO.getPatientBirthdate(patientId));

        // Get patient's gender.
        System.out.println(patientDAO.getPatientGender(patientId));

        // Get patient's address with city, state and country.
        System.out.println(patientDAO.getPatientAddress(patientId));
    }
}
