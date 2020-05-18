package database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.orderBy;

public class EncounterRepository implements  EncounterDAO {

    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";
    private MongoDatabase db = Mongo.db;
    private MongoCollection<Document> encounters;

    public EncounterRepository() {
    }

    @Override
    public ArrayList<String> getPatientsByHPracId(ArrayList<String> hPracIds) {
        MongoCollection<Document> encounters = db.getCollection("Encounter");
        ArrayList<String> patientIds = new ArrayList<>();

        Bson filter = in("participant.individual.reference", hPracIds);
        FindIterable<Document> results = encounters.find(filter, Document.class)
                .projection(fields(include("subject.reference"), excludeId()));

        for (Document doc : results) {
            Document subject = doc.get("subject", Document.class);
            String patientId = subject.get("reference", String.class);
            patientId = patientId.replace("Patient/", "");
            if (!patientIds.contains(patientId)) {
                patientIds.add(patientId);
            }
        }
        return patientIds;
    }

    @Override
    public void insertEncounter(String encounterId) {
        MongoCollection<Document> encounters = db.getCollection("Encounter");
        String encounterUrl = rootUrl + "/Encounter/" + encounterId + "?_format=json";
        JSONObject json = null;
        try {
            json = JsonReader.readJsonFromUrl(encounterUrl);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        if (json != null) {
            Document doc = Document.parse(json.toString());

            Bson filter = Filters.eq("id", encounterId);
            Bson update = new Document("$set", doc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            encounters.updateOne(filter, update, options);
        }

    }

    @Override
    public void insertEncountersByPrac(String identifier, PatientDAO patientDAO, PractitionerDAO practitionerDAO,
                                       ObservationDAO observationDAO) throws IOException, JSONException {
        MongoCollection<Document> encounters = db.getCollection("Encounter");

        String encountersUrl = rootUrl + "Encounter?_include=Encounter.participant.individual&_include=Encounter" +
                ".patient&participant.identifier=http%3A%2F%2Fhl7.org%2Ffhir%2Fsid%2Fus-npi%7C" + identifier
                + "&_format=json";

        // Declare required variables
        boolean nextPage = true;
        String nextUrl = encountersUrl;
        ArrayList<String> patientsInserted = new ArrayList<>();
        ArrayList<String> practitionersInserted = new ArrayList<>();

        while (nextPage) {
            // Get all encounters on this page
            JSONObject allEncounters = null;
            try {
                allEncounters = JsonReader.readJsonFromUrl(nextUrl);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            if (allEncounters != null) {
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
            }

            if (allEncounters != null) {
                JSONArray encounterData = allEncounters.getJSONArray("entry");
                // Loop through all encounters and add encounter to database.
                for (int i = 0; i < encounterData.length(); i++) {
                    JSONObject entry = encounterData.getJSONObject(i);
                    JSONObject resource = entry.getJSONObject("resource");
                    // Get Encounter ID and then add to database.
                    String encounterId = resource.getString("id");
                    insertEncounter(encounterId);

                    // Get Patient ID and then parse.
                    String patientID = resource.getJSONObject("subject").getString("reference");
                    patientID = patientID.split("/")[1];

                    if (!patientsInserted.contains(patientID)){
                        // Add patient.
                        patientDAO.insertPatient(patientID);
                        // Insert all observation values of patient.
                        observationDAO.insertLatestCholesObs(patientID);
                        patientsInserted.add(patientID);
                    }

                    // Get Patient Name to print so we know it's working.
                    String patientName = resource.getJSONObject("subject").getString("display");

                    System.out.println(patientID + " " + patientName);

                    // Add involved practitioner.
                    JSONArray arParticipants = resource.getJSONArray("participant");
                    for (int j = 0; j < arParticipants.length(); j ++) {
                        JSONObject participant = arParticipants.getJSONObject(j);
                        String hPracId = participant.getJSONObject("individual").getString("reference");
                        hPracId = hPracId.replace("Practitioner/", "");
                        if (!practitionersInserted.contains(hPracId)) {
                            practitionerDAO.insertPracById(hPracId);
                            practitionersInserted.add(hPracId);
                            System.out.println(hPracId);
                        }
                    }

                }
            }
        }

    }

//    private void insertEncountersByPatient(String patientId) {
//        MongoCollection<Document> encounters = db.getCollection("Encounter");
//
//        String encountersUrl = rootUrl + "Encounter?patient=Patient%2F" + patientId + "&_format=json";
//
//        // Declare required variables
//        boolean nextPage = true;
//        String nextUrl = encountersUrl;
//
//        while (nextPage) {
//            // Get all encounters on this page
//            JSONObject allEncounters = null;
//            try {
//                allEncounters = JsonReader.readJsonFromUrl(nextUrl);
//            } catch (JSONException | IOException e) {
//                e.printStackTrace();
//            }
//
//            if (allEncounters != null) {
//                // Check if there is a next page
//                nextPage = false;
//                // Get all related links
//                JSONArray links = allEncounters.getJSONArray("link");
//                for (int i = 0; i < links.length(); i++) {
//                    // Get current link
//                    JSONObject link = links.getJSONObject(i);
//                    // Check if relation is next, this means there is a next page
//                    String relation = link.getString("relation");
//                    if (relation.equalsIgnoreCase("next")) {
//                        // Update variables accordingly
//                        nextPage = true;
//                        nextUrl = link.getString("url");
//                    }
//                }
//            }
//
//            if (allEncounters != null) {
//                JSONArray encounterData = allEncounters.getJSONArray("entry");
//                // Loop through all encounters and add encounter to database.
//                for (int i = 0; i < encounterData.length(); i++) {
//                    JSONObject entry = encounterData.getJSONObject(i);
//                    JSONObject resource = entry.getJSONObject("resource");
//                    // Get Encounter ID and then add to database.
//                    String encounterId = resource.getString("id");
//                    insertEncounter(encounterId);
//                }
//            }
//        }
//    }
}
