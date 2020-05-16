package database;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
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

        String bloodPressure = "55284-4";
        String hdl = "2085-9";
        String ldl = "18262-6";
        String bmi = "39156-5";
        String bodyWeight = "29463-7";

        ArrayList<String> codes = new ArrayList<>();
        codes.add(bloodPressure);
        codes.add(hdl);
        codes.add(ldl);
        codes.add(bmi);
        codes.add(bodyWeight);

        observationDAO.insertCholesObsByCodes(codes, "1000", 2);

    }
}
