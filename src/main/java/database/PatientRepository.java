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
import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.orderBy;

public class PatientRepository implements PatientDAO {

    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";
    MongoDatabase db;

    public PatientRepository() {
        this.db = Mongo.db;
    }

    private ArrayList<String> getAllPatientIds() {
        MongoCollection<Document> collection = db.getCollection("Patient");
        FindIterable<Document> result = collection.find().projection(fields(include("id"), excludeId()));

        ArrayList<String> patientIds = new ArrayList<>();

        for (Document doc : result) {
            String patientId = doc.getString("id");

            if (!patientIds.contains(patientId)) {
                patientIds.add(patientId);
            }
        }

        return patientIds;
    }

    @Override
    public String getPatientName(String patientId) {

        // query database
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("name"), excludeId()));

//        ArrayList<String> names = new ArrayList<>();
//        String fullName = "";

        for (Document doc : result) {
            ArrayList<Document> name = doc.get("name", ArrayList.class);

            for (Document nameDoc : name) {
                // get family name
                String familyName = nameDoc.get("family", String.class).replaceAll("[0-9]", "");

                // get given names
                ArrayList<String> fName = nameDoc.get("given", ArrayList.class);
                StringBuilder givenName = new StringBuilder();

                for (String eachFName : fName) {
                    givenName.append(eachFName.replaceAll("[0-9]", "") + " ");
                }

                String fullName = givenName + familyName;
                return fullName;
            }
        }

        return null;
    }

    @Override
    public String getPatientFName(String patientId) {
        String[] name = getPatientName(patientId).split(" ");
        StringBuilder fname = new StringBuilder();

        for (int i = 0; i < name.length - 1; i++) {
            fname.append(name[i]);
            fname.append(" ");
        }

        return fname.toString().substring(0, fname.length()-1);
    }

    @Override
    public String getPatientLName(String patientId) {
        String[] name = getPatientName(patientId).split(" ");
        return name[name.length - 1];
    }

    @Override
    public String getPatientGender(String patientId) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("gender"), excludeId()));

        String gender = "";

        for (Document doc : result) {
            gender = doc.get("gender").toString();
        }

        // capitalise gender
        gender = gender.substring(0, 1).toUpperCase() + gender.substring(1);

        return gender;
    }

    @Override
    public String getPatientBirthdate(String patientId) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("birthDate"), excludeId()));

        String name = "";

        for (Document doc : result) {
            name = doc.get("birthDate").toString();
        }

        return name;
    }

    private String[] getPatientAddress(String patientId) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("address"), excludeId()));

        String[] addresses = new String[3];

        for (Document doc : result) {
            ArrayList<Document> arAddresses = doc.get("address", ArrayList.class);

            for (Document address : arAddresses) {
                String city = address.get("city", String.class);
                String state = address.get("state", String.class);
                String country = address.get("country", String.class);

                addresses[0] = city;
                addresses[1] = state;
                addresses[2] = country;

                break;
            }
        }

        return addresses;
    }

    @Override
    public String getPatientAddressCity(String patientId) {
        return getPatientAddress(patientId)[0];
    }

    @Override
    public String getPatientAddressState(String patientId) {
        return getPatientAddress(patientId)[1];
    }

    @Override
    public String getPatientAddressCountry(String patientId) {
        return getPatientAddress(patientId)[2];
    }

    private ArrayList<String> getPatientNamesByIds(ArrayList<String> patientIds) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = in("id", patientIds);
        // Sort by _id so position can be found later.
        FindIterable<Document> result = collection.find(filter, Document.class)
                .sort(orderBy(ascending("_id")))
                .projection(fields(include("id"), excludeId()));

        ArrayList<String> names = new ArrayList<>();

        for (Document doc : result) {
            String patientId = doc.get("id", String.class);
            names.add(getPatientName(patientId));
        }

        return names;
    }

    @Override
    public ArrayList<String> getPatientIdsSorted(ArrayList<String> patientIds) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = in("id", patientIds);
        // Sort by _id so position can be found later.
        FindIterable<Document> result = collection.find(filter, Document.class)
                .sort(orderBy(ascending("_id")))
                .projection(fields(include("id"), excludeId()));

        ArrayList<String> ids = new ArrayList<>();

        for (Document doc : result) {
            String patientId = doc.get("id", String.class);
            ids.add(patientId);
        }

        return ids;
    }

    private ArrayList<Document> getPatientsSorted(ArrayList<String> patientIds) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = in("id", patientIds);
        // Sort by _id so position can be found later.
        FindIterable<Document> result = collection.find(filter, Document.class)
                .sort(orderBy(ascending("_id")))
                .projection(fields(include("id"), excludeId()));

        ArrayList<Document> patients = new ArrayList<>();

        for (Document doc : result) {
            patients.add(doc);
        }

        return patients;
    }

    private String getPatientId(int position, ArrayList<String> patientIds) {
        Document doc = getPatientsSorted(patientIds).get(position);
        return doc.get("id", String.class);
    }

    public void insertPatient(String patientId) {
        String patientUrl = rootUrl + "Patient/" + patientId + "?_format=json";
        JSONObject json = null;
        try {
            json = JsonReader.readJsonFromUrl(patientUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (json != null) {
            Document doc = Document.parse(json.toString());

            Bson filter = Filters.eq("id", patientId);
            Bson update =  new Document("$set", doc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            db.getCollection("Patient").updateOne(filter, update, options);
        }
    }

    private void insertPatientsByGender(String gender) {
        // insert the first 50 patients only
        String patientUrl = rootUrl + "Patient?_count=50&gender=" + gender + "&_format=json";

        JSONObject patientsByGender = null;
        try {
            patientsByGender = JsonReader.readJsonFromUrl(patientUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (patientsByGender != null) {
            // loop through all patients
            JSONArray patients = patientsByGender.getJSONArray("entry");
            for (int i = 0; i < patients.length(); i++) {
                // get patient
                JSONObject patient = patients.getJSONObject(i);
                String patientId = patient.getJSONObject("resource").getString("id");
                insertPatient(patientId);
            }
        }
    }

}
