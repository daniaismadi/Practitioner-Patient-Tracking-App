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

/***
 * A class to retrieve patient information from the server, insert patient information into the database
 * and query information about patients from the database. Implements the PatientDAO interface.
 *
 */
public class PatientRepository implements PatientDAO {

    /**
     * The root URL for FHIR server.
     */
    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";

    /**
     * The local MongoDB database.
     */
    MongoDatabase db;

    /***
     * Class constructor for PatientRepository. Initialises the local database.
     *
     */
    public PatientRepository() {
        this.db = Mongo.db;
    }

    /***
     * Get all the IDs of the patients in the local database.
     *
     * @return      The list of all patient IDs in the local database.
     */
    public ArrayList<String> getAllPatientIds() {
        // Access Patient Collection.
        MongoCollection<Document> collection = db.getCollection("Patient");
        FindIterable<Document> result = collection.find().projection(fields(include("id"), excludeId()));

        // Initialise new array list that will contain IDs of patient.
        ArrayList<String> patientIds = new ArrayList<>();

        for (Document doc : result) {
            // Get patient ID and add to patientIds array list.
            String patientId = doc.getString("id");

            if (!patientIds.contains(patientId)) {
                patientIds.add(patientId);
            }
        }

        return patientIds;
    }

    /***
     * Get the full name of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The full name of this patient.
     */
    @Override
    public String getPatientName(String patientId) {

        // Access Patient Collection.
        MongoCollection<Document> collection = db.getCollection("Patient");

        // Search for documents with this patient ID.
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("name"), excludeId()));

        for (Document doc : result) {
            // Get the name.
            ArrayList<Document> name = doc.get("name", ArrayList.class);

            for (Document nameDoc : name) {
                // get family name and parse/clean up name
                String familyName = nameDoc.get("family", String.class).replaceAll("[0-9]", "");

                // get given names
                ArrayList<String> fName = nameDoc.get("given", ArrayList.class);
                StringBuilder givenName = new StringBuilder();

                for (String eachFName : fName) {
                    // parse/clean up given names
                    givenName.append(eachFName.replaceAll("[0-9]", "") + " ");
                }

                // concatenate given names and family name
                String fullName = givenName + familyName;

                // return full name
                return fullName;
            }
        }

        // return null if patient name does not exist
        return null;
    }

    /***
     * Get the given name of this patient (could be more than one word).
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The given name of this patient.
     */
    @Override
    public String getPatientFName(String patientId) {
        // Parse for given name.
        String[] name = getPatientName(patientId).split(" ");
        StringBuilder fname = new StringBuilder();

        for (int i = 0; i < name.length - 1; i++) {
            fname.append(name[i]);
            fname.append(" ");
        }

        return fname.toString().substring(0, fname.length()-1);
    }

    /***
     * Get the last name of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The last name of this patient.
     */
    @Override
    public String getPatientLName(String patientId) {
        // Parse for last name.
        String[] name = getPatientName(patientId).split(" ");
        return name[name.length - 1];
    }

    /***
     * Get the gender of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The gender of the patient.
     */
    @Override
    public String getPatientGender(String patientId) {
        // Access Patient Collection.
        MongoCollection<Document> collection = db.getCollection("Patient");

        // Search for documents with this patient ID.
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("gender"), excludeId()));

        String gender = "";

        for (Document doc : result) {
            // Get the gender of this patient.
            gender = doc.get("gender").toString();
        }

        // capitalise gender
        gender = gender.substring(0, 1).toUpperCase() + gender.substring(1);

        return gender;
    }

    /***
     * Get the date of birth of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The date of birth of the patient as a string.
     */
    @Override
    public String getPatientBirthdate(String patientId) {
        // Access Patient Collection.
        MongoCollection<Document> collection = db.getCollection("Patient");

        // Search for documents with this patient ID.
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("birthDate"), excludeId()));

        String name = "";

        for (Document doc : result) {
            // Get the patient date of birth.
            name = doc.get("birthDate").toString();
        }

        return name;
    }

    /***
     * Get the address of this patient.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The address of this patient as a string array, as specified, [City, State, Country].
     */
    private String[] getPatientAddress(String patientId) {
        // Access Patient information.
        MongoCollection<Document> collection = db.getCollection("Patient");

        // Search for documents with this patient ID.
        Bson filter = eq("id", patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("address"), excludeId()));

        String[] addresses = new String[3];

        for (Document doc : result) {
            // Get the address of this patient.
            ArrayList<Document> arAddresses = doc.get("address", ArrayList.class);

            for (Document address : arAddresses) {
                // Get the city part of this address.
                String city = address.get("city", String.class);

                // Get the state part of this address.
                String state = address.get("state", String.class);

                // Get the country part of this address.
                String country = address.get("country", String.class);

                addresses[0] = city;
                addresses[1] = state;
                addresses[2] = country;

                break;
            }
        }

        // Return the address of this patient [City, State, Country]
        return addresses;
    }

    /***
     * Return the city where this patient is from.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The city where this patient is from.
     */
    @Override
    public String getPatientAddressCity(String patientId) {
        return getPatientAddress(patientId)[0];
    }

    /***
     * Return the state where this patient is from.
     *
     * @param patientId     The ID of the patient to retrieve information from.
     * @return              The state where this patient is from.
     */
    @Override
    public String getPatientAddressState(String patientId) {
        return getPatientAddress(patientId)[1];
    }

    /***
     * Return the country where this patient is from.
     *
     * @param patientId     the ID of the patient to retrieve information from
     * @return              the country where this patient is from
     */
    @Override
    public String getPatientAddressCountry(String patientId) {
        return getPatientAddress(patientId)[2];
    }

    /***
     * Get list of patient names that have IDs in patientIds, sorted by _id.
     *
     * @param patientIds    The list of patient IDs.
     * @return              Sorted list of patient names.
     */
    private ArrayList<String> getPatientNamesByIds(ArrayList<String> patientIds) {
        // Access Patient collection.
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

    /***
     * Return list of patient IDs that are sorted by _id (unique identifier given by MongoDB).
     *
     * @param patientIds        the list of patient IDs to sort
     * @return                  the sorted list of patient IDs
     */
    @Override
    public ArrayList<String> getPatientIdsSorted(ArrayList<String> patientIds) {
        // Access Patient collection.
        MongoCollection<Document> collection = db.getCollection("Patient");

        // Find all documents of patients that are included in patientIds.
        Bson filter = in("id", patientIds);

        // Sort by _id so position can be found later.
        FindIterable<Document> result = collection.find(filter, Document.class)
                .sort(orderBy(ascending("_id")))
                .projection(fields(include("id"), excludeId()));

        // Initialise a new empty list.
        ArrayList<String> ids = new ArrayList<>();

        for (Document doc : result) {
            // Get the ID of the patient and add to ids list.
            String patientId = doc.get("id", String.class);
            ids.add(patientId);
        }

        // Return patient IDs.
        return ids;
    }

//    /***
//     *
//     * @param patientIds
//     * @return
//     */
//    private ArrayList<Document> getPatientsSorted(ArrayList<String> patientIds) {
//        MongoCollection<Document> collection = db.getCollection("Patient");
//        Bson filter = in("id", patientIds);
//        // Sort by _id so position can be found later.
//        FindIterable<Document> result = collection.find(filter, Document.class)
//                .sort(orderBy(ascending("_id")))
//                .projection(fields(include("id"), excludeId()));
//
//        ArrayList<Document> patients = new ArrayList<>();
//
//        for (Document doc : result) {
//            patients.add(doc);
//        }
//
//        return patients;
//    }

//    /***
//     *
//     *
//     * @param position
//     * @param patientIds
//     * @return
//     */
//    private String getPatientId(int position, ArrayList<String> patientIds) {
//        Document doc = getPatientsSorted(patientIds).get(position);
//        return doc.get("id", String.class);
//    }

    /***
     * Retrieve information of the patient with this patientId from the FHIR server and insert the document of
     * this patient into the local database.
     *
     * @param patientId     The ID of the patient to retrieve information for.
     */
    public void insertPatient(String patientId) {
        // The URL of the document of the patient with this patientId.
        String patientUrl = rootUrl + "Patient/" + patientId + "?_format=json";

        JSONObject json = null;
        try {
            // Read JSON file which contains information about this patient.
            json = JsonReader.readJsonFromUrl(patientUrl);
        } catch (JSONException | IOException e) {
            // URL is not a valid URL.
            ;
        }

        if (json != null) {
            // Parse Json into Bson document.
            Document doc = Document.parse(json.toString());

            // Insert/update patient with this ID into the local database.
            Bson filter = Filters.eq("id", patientId);
            Bson update =  new Document("$set", doc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            db.getCollection("Patient").updateOne(filter, update, options);
        }
    }

}
