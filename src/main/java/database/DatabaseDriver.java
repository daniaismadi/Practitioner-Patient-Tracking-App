package database;

import com.mongodb.client.DistinctIterable;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static void main(String[] args) throws Exception {
        // Establish connection with database.
        Mongo.connect();

        // Create instances.
        patientDAO = new PatientRepository();
        encounterDAO = new EncounterRepository();
        practitionerDAO = new PractitionerRepository();
        observationDAO = new ObservationRepository();
        monitorDAO = new MonitorRepository();

        int pageCount = 1;
        System.out.println(predictHighCholes(pageCount));
    }

    static double predictHighCholes(int pageCount) throws Exception {

        String bloodPressure = "55284-4";
        String bmi = "39156-5";
        String bodyWeight = "29463-7";
        String bodyHeight = "8302-2";
        String bloodGlucose = "2339-0";
        String hemoglobin = "718-7";
        String erythrocytes = "789-8";
        String smokingCode = "72166-2";

        ArrayList<String> codes = new ArrayList<>();
        codes.add(bloodPressure);
        codes.add(bmi);
        codes.add(bodyWeight);
        codes.add(bodyHeight);
        codes.add(bloodGlucose);
        codes.add(hemoglobin);
        codes.add(erythrocytes);
        codes.add(smokingCode);

        double accuracy = getModelAccuracy();
        while (accuracy < 0.8) {
            observationDAO.insertCholesObsByCodes(codes, "1000", pageCount);
            accuracy = getModelAccuracy();
            pageCount += 1;
        }

        return accuracy;
    }

    static double getModelAccuracy() throws Exception {
        // changed based on your computer
        String filePath = "/Users/daniaismadi/Documents/university/monash/2020 semester 1/fit3077/assignments/A2/" +
                "FIT3077_A2/project/src/main/resources/predictHighCholes.py";
        ProcessBuilder pb = new ProcessBuilder().command("python", "-u", filePath);
        Process p = pb.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder buffer = new StringBuilder();
        String line = null;
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        int exitCode = p.waitFor();
        String accuracy = buffer.toString();
        System.out.println("Value is: " + accuracy);
        System.out.println("");
        in.close();

        return Double.valueOf(accuracy);
    }

}
