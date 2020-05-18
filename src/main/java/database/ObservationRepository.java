package database;

import com.mongodb.client.DistinctIterable;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

public class ObservationRepository implements ObservationDAO {

    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";
    MongoDatabase db;

    public ObservationRepository() {
        this.db = Mongo.db;
    }

    private void insertPatientObservationsByCode(String patientId, String code) {
        // Adds all patient observations.
        System.out.println("Updating observations for: " + patientId);
        // just get the latest observation
        String obsUrl = rootUrl + "Observation?code=" + code + "&patient=" + patientId + "&_sort=-date&_format=json";
        JSONObject observationBundle = null;
        try {
            observationBundle = JsonReader.readJsonFromUrl(obsUrl);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        try {
            JSONArray observations = observationBundle.getJSONArray("entry");
            for (int i = 0; i < observations.length(); i++) {
                JSONObject entry = observations.getJSONObject(i);
                // get resource
                JSONObject resource = entry.getJSONObject("resource");
                // get observation id
                String obsId = resource.getString("id");
                // insert observation to database
                insertObs(obsId);
                System.out.println("Successfully added observations for " + patientId);
            }
        } catch (JSONException e) {
            // This means this person has no observations yet.
            System.out.println(patientId + " currently has no observations.");
        }
    }

    @Override
    public void insertCholesObsByCodes(ArrayList<String> codes, String count, int pageCount) {
        String obsUrl = rootUrl + "Observation?_count=" + count + "&_sort=-date&code=http%3A%2F%2Floinc.org%7C2093-3" +
                "&_format=json";
        JSONObject observationBundle = null;

        // Declare required variables
        boolean nextPage = true;
        String nextUrl = obsUrl;

        // go to the desired page
        for (int i = 0; i < pageCount; i++) {
            if (!nextPage) {
                break;
            }

            nextPage = false;
            try {
                observationBundle = JsonReader.readJsonFromUrl(nextUrl);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            if (observationBundle != null) {
                // Check if there is a next page
                // Get all related links
                JSONArray links = observationBundle.getJSONArray("link");
                for (int j = 0; j < links.length(); j++) {
                    // Get current link
                    JSONObject link = links.getJSONObject(j);
                    // Check if relation is next, this means there is a next page
                    String relation = link.getString("relation");
                    if (relation.equalsIgnoreCase("next")) {
                        // Update variables accordingly
                        System.out.println(i);
                        nextPage = true;
                        nextUrl = link.getString("url");
                    }
                }
            }
        }

        // retrieve data from this page
        try {
            observationBundle = JsonReader.readJsonFromUrl(nextUrl);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        try {
            JSONArray observations = observationBundle.getJSONArray("entry");
            for (int i = 0; i < observations.length(); i++) {
                JSONObject entry = observations.getJSONObject(i);
                // get resource
                JSONObject resource = entry.getJSONObject("resource");
                // get observation id
                String obsId = resource.getString("id");
                // insert observation to database
                insertObs(obsId);

                JSONObject subject = resource.getJSONObject("subject");
                String patientId = subject.getString("reference");
                patientId = patientId.replace("Patient/", "");

                // get patient observations of other measures
                for (String code : codes) {
                    insertPatientLatestObsByCode(patientId, code);
                }

                System.out.println("Retrieved observation " + obsId);
            }
        } catch (JSONException e) {
            System.out.println("Failed to retrieve observations.");
        }
    }

    @Override
    public void insertLatestCholesObs(String patientId) {
        String cholesCode = "2093-3";
        insertPatientLatestObsByCode(patientId, cholesCode);
    }

    @Override
    public void insertPatientLatestObsByCode(String patientId, String code) {
        System.out.println("Updating observations for: " + patientId);
        // just get the latest observation
        String obsUrl = rootUrl + "Observation?code=" + code + "&patient=" + patientId + "&_sort=-date&_count=1&_format=json";
        JSONObject observationBundle = null;
        try {
            observationBundle = JsonReader.readJsonFromUrl(obsUrl);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        try {
            JSONArray observations = observationBundle.getJSONArray("entry");
            JSONObject entry = observations.getJSONObject(0);
            // get resource
            JSONObject resource = entry.getJSONObject("resource");
            // get observation id
            String obsId = resource.getString("id");
            // insert observation to database
            insertObs(obsId);
            System.out.println("Successfully added observations for " + patientId);
            } catch (JSONException e) {
            // This means this person has no observations yet.
            System.out.println(patientId + " currently has no observations.");
        }
    }

    @Override
    public void insertObs(String obsId) {
        String obsUrl = rootUrl + "Observation/" + obsId + "?_format=json";
        JSONObject json = null;
        try {
            json = JsonReader.readJsonFromUrl(obsUrl);
            Document doc = Document.parse(json.toString());

            Bson filter = Filters.eq("id", obsId);
            Bson update = new Document("$set", doc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            db.getCollection("Observation").updateOne(filter, update, options);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<String> getAllPatientsIdsObs() {
        DistinctIterable<String> patients = Mongo.db.getCollection("Observation")
                .distinct("subject.reference", null, String.class);

        ArrayList<String> patientIds = new ArrayList<>();

        for (String id : patients) {
            id = id.replace("Patient/", "");
            patientIds.add(id);
        }

        return patientIds;
    }

    @Override
    public Date getLatestCholesDate(String patientId) {
        ArrayList<String> allCholesObs = getAllCholesObs(patientId);
        return getLatestObsDate(allCholesObs);
    }

    @Override
    public double getLatestCholesVal(String patientId) {
        ArrayList<String> allCholesObs = getAllCholesObs(patientId);
        return getLatestObsVal(allCholesObs);
    }

    private ArrayList<String> getAllObsByCode(String patientId, String code) {
        MongoCollection<Document> collection = db.getCollection("Observation");
        Bson filterPatient = eq("subject.reference", "Patient/" + patientId);
        Bson filterType = eq("code.coding.code", code);
        Bson filter = and(filterPatient, filterType);

        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("id"), excludeId()));

        ArrayList<String> observations = new ArrayList<>();

        for (Document doc : result) {
            observations.add(doc.get("id", String.class));
        }

        return observations;
    }

    private ArrayList<String> getAllCholesObs(String patientId) {
        String cholesCode = "2093-3";
        return getAllObsByCode(patientId, cholesCode);
    }

    private double getObsValue(String obsId) {
        MongoCollection<Document> collection = db.getCollection("Observation");
        Bson filter = eq("id", obsId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("valueQuantity"), excludeId()));

        double value = 0;

        for (Document doc : result) {
            Document valueQuantity = doc.get("valueQuantity", Document.class);
            Document valueQuantityParsed = Document.parse(valueQuantity.toJson());

            try {
                value = valueQuantityParsed.get("value", Double.class);
            } catch (ClassCastException e) {
                Integer val = valueQuantityParsed.get("value", Integer.class);
                value = (double) val;
            }
        }

        return value;
    }

    private Date getEffectiveDate(String obsId) throws ParseException {
        MongoCollection<Document> collection = db.getCollection("Observation");
        Bson filter = eq("id", obsId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("effectiveDateTime"), excludeId()));

        String dateStr = "";

        for (Document doc : result) {
            dateStr = doc.get("effectiveDateTime", String.class);
        }

        // 1999-08-24T15:48:33+10:00
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return format.parse(dateStr);
    }

    private Date getLatestObsDate(ArrayList<String> obsIds) {
        if (obsIds.size() == 0) {
            // Patient does not have any observations.
            return null;
        }

        Date latestDate = new Date(Long.MIN_VALUE);

        for (String obsId : obsIds) {
            try {
                // this means that getEffectiveDate occurs after latestDate
                if (getEffectiveDate(obsId).compareTo(latestDate) > 0) {
                    // update latestDate
                    latestDate = getEffectiveDate(obsId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return latestDate;
    }

    private double getLatestObsVal(ArrayList<String> obsIds) {
        if (obsIds.size() == 0) {
            // Patient does not have any cholesterol observations.
            return 0;
        }

        Date latestDate = new Date(Long.MIN_VALUE);
        double val = 0;

        for (String obsId : obsIds) {
            try {
                // this means that getEffectiveDate occurs after latestDate
                if (getEffectiveDate(obsId).compareTo(latestDate) > 0) {
                    // update latestDate
                    latestDate = getEffectiveDate(obsId);
                    val = getObsValue(obsId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return val;
    }
}
