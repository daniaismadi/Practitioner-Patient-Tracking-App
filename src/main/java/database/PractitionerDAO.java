package database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Projections.excludeId;

public class PractitionerDAO {

    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";
    private MongoDatabase db;
    // TODO: Get rid of this association.
    private PatientDAO patientDAO;

    public PractitionerDAO(MongoDatabase db) {
        this.db = db;
        patientDAO = new PatientDAO(db);
    }

    /***
     * Return all of the practitioner's patient's names.
     *
     * @param identifier    The practitioner identifier.
     * @return              A list of strings all the patient's names.
     */
    ArrayList<String> getPracPatientNames(String identifier) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = eq("practitioner", identifier);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("id"), excludeId()));

        ArrayList<String> allPatients = new ArrayList<>();

        for (Document doc : result) {
            String patientId = doc.get("id", String.class);
            allPatients.add(patientDAO.getPatientName(patientId));
        }

        return allPatients;
    }

    /***
     * Return a HashMap of all of this practitioner's (with practitioner identifier) patient's names mapped to their
     * IDs.
     *
     * @param identifier    The practitioner identifier.
     * @return              A HashMap with patient's ID as the key and name as the value.
     */
    HashMap<String, String> getPracPatientNamesId(String identifier) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = eq("practitioner", identifier);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("id"), excludeId()));

        HashMap<String, String> allPatients = new HashMap<>();

        for (Document doc : result) {
            String patientId = doc.get("id", String.class);
            allPatients.put(patientId, patientDAO.getPatientName(patientId));
        }

        return allPatients;
    }

    /***
     * Insert all of this practitioner's (with practitioner identifier) patients into the database.
     * TODO: Update this method.
     *
     * @param identifier        The practitioner's identifier.
     * @throws IOException
     * @throws JSONException
     */
    void insertPractitionerPatients(String identifier) throws IOException, JSONException {
        String encountersUrl = rootUrl + "Encounter?_include=Encounter.participant.individual&_include=Encounter." +
                "patient&participant.identifier=http%3A%2F%2Fhl7.org%2Ffhir%2Fsid%2Fus-npi%7C" +
                identifier + "&_format=json";

        // Declare required variables
        boolean nextPage = true;
        String nextUrl = encountersUrl;

        while (nextPage) {
            // Get all encounters on this page
            JSONObject allEncounters = JsonReader.readJsonFromUrl(nextUrl);

            // Check if there is a next page
            nextPage = false;
            // Get all related links
            JSONArray links = allEncounters.getJSONArray("link");
            for (int i = 0; i < links.length(); i++) {
                // Get current link
                JSONObject link = links.getJSONObject(i);
                // Check if relation is next, this means there is a next page
                String relation = link.getString("relation");
                if (relation.equalsIgnoreCase("next")) {
                    // Update variables accordingly
                    nextPage = true;
                    nextUrl = link.getString("url");
                }
            }

            JSONArray encounterData = allEncounters.getJSONArray("entry");
            // Loop through all encounters and retrieve patient ID, patient name, cholesterol level and datetime.
            for (int i = 0; i < encounterData.length(); i++) {
                JSONObject entry = encounterData.getJSONObject(i);
                JSONObject resource = entry.getJSONObject("resource");
                // Get Patient ID and then parse.
                String patientID = resource.getJSONObject("subject").getString("reference");
                patientID = patientID.split("/")[1];
                patientDAO.insertPatient(patientID, identifier);

                // Get Patient Name.
                String patientName = resource.getJSONObject("subject").getString("display");

                System.out.println(patientID + " " + patientName);
            }
        }

    }
}
