package database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
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

    /***
     * Return patient name, given patient id.
     *
     * @param patientId     The patient's unique id.
     * @return              The patient's name.
     */
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


    /***
     * Return the patient's gender.
     *
     * @param patientId     The patient id.
     * @return              The gender.
     */
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

    /***
     * Return the patient's birthdate.
     *
     * @param patientId     The patient id.
     * @return              The birth date.
     */
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

    public String getPatientAddress(String patientId) {
        MongoCollection<Document> collection = db.getCollection("Patient");
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("address"), excludeId()));

        ArrayList<String> addresses = new ArrayList<>();

        for (Document doc : result) {
            ArrayList<Document> arAddresses = doc.get("address", ArrayList.class);

            for (Document address : arAddresses) {
                String city = address.get("city", String.class);
                String state = address.get("state", String.class);
                String country = address.get("country", String.class);

                String patientAddress = city + ", " + state + ", " + country;
                addresses.add(patientAddress);
            }
        }

        return addresses.get(0);
    }

    /***
     * Get list of patient names based on patient IDs sorted by ascending order of _id.
     *
     * @param patientIds        ArrayList of patient IDs represented in string.
     * @return                  ArrayList of strings containing patient names.
     */
    @Override
    public ArrayList<String> getPatientNamesByIds(ArrayList<String> patientIds) {
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

    public ArrayList<Document> getPatientsSorted(ArrayList<String> patientIds) {
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

    @Override
    public String getPatientId(int position, ArrayList<String> patientIds) {
        Document doc = getPatientsSorted(patientIds).get(position);
        return doc.get("id", String.class);
    }

    /***
     * Insert patient with patient ID into the database.
     *
     * @param patientId         The patient id.
     * @throws IOException
     * @throws JSONException
     */
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

}
