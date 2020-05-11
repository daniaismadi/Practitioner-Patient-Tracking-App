package database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Sorts.orderBy;

public class PractitionerRepository implements PractitionerDAO {

    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";
    private MongoDatabase db = Mongo.db;
    // TODO: Get rid of this association.
    private PatientRepository patientRepository = new PatientRepository();

    /***
     * Return all of the practitioner's patient's names.
     *
     * @param identifier    The practitioner identifier.
     * @return              A list of strings all the patient's names.
     */
    @Override
    public List<String> getPracPatientNames(String identifier) {
        MongoCollection<Document> patients = db.getCollection("Patient");
        Bson filter = eq("practitioner", identifier);
        FindIterable<Document> result = patients.find(filter, Document.class)
                .sort(orderBy(ascending("_id")))
                .projection(fields(include("id"), excludeId()));

        List<String> allPatients = new ArrayList<>();

        for (Document doc : result) {
            String patientId = doc.get("id", String.class);
            allPatients.add(patientRepository.getPatientName(patientId));
        }

        return allPatients;
    }

    public String getHPracIdentifier(String hPracId) {
        MongoCollection<Document> hpracs = db.getCollection("Practitioner");

        String identifier = "";

        Bson filter = eq("id", hPracId);
        FindIterable<Document> result = hpracs.find(filter, Document.class);

        for (Document doc : result) {
            ArrayList<Document> arIdentifiers = doc.get("identifier", ArrayList.class);

            for (Document id : arIdentifiers) {
                identifier = id.get("value", String.class);
            }
        }

        return identifier;
    }

    public ArrayList<String> getHPracIds(String hPracIdentifier) {
        ArrayList<String> hPracIds = new ArrayList<>();

        MongoCollection<Document> hPracs = db.getCollection("Practitioner");
        // Get all identifiers
        Bson filter = eq("value", hPracIdentifier);
        Bson projection = Projections.fields(excludeId(), include("id"),
                elemMatch("identifier", filter));

        FindIterable<Document> result = hPracs.find().projection(projection);

        for (Document doc : result) {
            String id = doc.get("id", String.class);
            hPracIds.add("Practitioner/" + id);
        }

        return hPracIds;
    }

    /***
     * Return patient ID based on the position of the documents ordered by ascending order of _id of this practitioner.
     * TODO: IMPLEMENT
     * @param position      Position of the document containing patient ID.
     * @param identifier    Identifier of the practitioner ID.
     * @return              A string of this patient's ID.
     */
    public String getPatientId(int position, String identifier) {
        String patientId = "";

        MongoCollection<Document> patients = db.getCollection("Patient");
        Bson filter = eq("practitioner", identifier);
        FindIterable<Document> result = patients.find(filter, Document.class)
                .sort(orderBy(ascending("_id")))
                .projection(fields(include("id"), excludeId()));

        return patientId;
    }

    public void insertPracById(String pracId) {
        String pracUrl = rootUrl + "Practitioner/" + pracId + "?_format=json";

        JSONObject json = null;
        try {
            json = JsonReader.readJsonFromUrl(pracUrl);
        } catch (FileNotFoundException e) {
            System.out.println("Practitioner not found. Please try again.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (json != null) {
            Document doc = Document.parse(json.toString());

            Bson filter = Filters.eq("id", pracId);
            Bson update =  new Document("$set", doc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            db.getCollection("Practitioner").updateOne(filter, update, options);
        }
    }

    public void insertPracByEncounter(String encounterId) {
        MongoCollection<Document> encounters = db.getCollection("Encounter");
        Bson filter = eq("id", encounterId);
        FindIterable<Document> result = encounters.find(filter, Document.class);

        for (Document doc : result) {
            ArrayList<Document> arParticipants = doc.get("participant", ArrayList.class);

            for (Document individual : arParticipants) {
                String pracId = individual.get("reference", String.class);
                pracId = pracId.replace("Pracitioner/", "");
                insertPracById(pracId);
            }
        }
    }

    /***
     * Insert all of this practitioner's (with practitioner identifier) patients into the database.
     * TODO: Update this method.
     *
     * @param identifier        The practitioner's identifier.
     * @throws IOException
     * @throws JSONException
     */
    @Override
    public void insertPracPatients(String identifier) throws IOException, JSONException {
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
                // patientRepository.insertPatient(patientID, identifier);

                // Get Patient Name.
                String patientName = resource.getJSONObject("subject").getString("display");

                System.out.println(patientID + " " + patientName);
            }
        }

    }
}
