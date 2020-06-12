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

/***
 * A class to retrieve practitioner information from the server, insert practitioner information into the database
 * and query information about practitioners from the database. Implements PractitionerDAO interface.
 *
 */
public class PractitionerRepository implements PractitionerDAO {

    /**
     * The root URL for FHIR server.
     */
    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";

    /**
     * The local MongoDB database.
     */
    private MongoDatabase db;

    /***
     * Class constructor for PractitionerRepository. Initialises the local database.
     *
     */
    public PractitionerRepository() {
        this.db = Mongo.db;
    }

    /***
     * Get the identifier of the practitioner with this ID.
     *
     * @param hPracId       The ID of the practitioner.
     * @return              The identifier of this practitioner.
     */
    @Override
    public String getHPracIdentifier(String hPracId) {
        // Access Practitioner collection from the database.
        MongoCollection<Document> hpracs = db.getCollection("Practitioner");

        // Return empty string if this practitioner does not exist in the database.
        String identifier = "";

        // Search for the database for a practitioner with this ID.
        Bson filter = eq("id", hPracId);
        FindIterable<Document> result = hpracs.find(filter, Document.class);

        for (Document doc : result) {
            // Get the identifier of the practitioner.
            ArrayList<Document> arIdentifiers = doc.get("identifier", ArrayList.class);

            for (Document id : arIdentifiers) {
                identifier = id.get("value", String.class);
            }
        }

        return identifier;
    }

    /***
     * Get the list of practitioner IDs that have this identifier.
     *
     * @param hPracIdentifier   The identifier to search for
     * @return                  List of practitioner IDs that have this identifier
     */
    @Override
    public ArrayList<String> getHPracIds(String hPracIdentifier) {
        // Return empty list if identifier does not exist in the database.
        ArrayList<String> hPracIds = new ArrayList<>();

        // Access Practitioner collection from the database.
        MongoCollection<Document> hPracs = db.getCollection("Practitioner");

        // Search for documents which have this identifier.
        Bson filter = eq("identifier.value", hPracIdentifier);
        Bson projection = Projections.fields(excludeId(), include("id"));

        FindIterable<Document> result = hPracs.find(filter, Document.class).projection(projection);

        for (Document doc : result) {
            // Add practitioner IDs to the array list.
            String id = doc.get("id", String.class);
            hPracIds.add("Practitioner/" + id);
        }

        // Return list of practitioner IDs.
        return hPracIds;
    }

    /***
     * Retrieve information of the practitioner with this ID from the FHIR server and insert the document (JSON) of
     * information into this local database.
     *
     * @param pracId            The ID of the practitioner to retrieve information for.
     */
    @Override
    public void insertPracById(String pracId) {
        // The URL for the JSON file.
        String pracUrl = rootUrl + "Practitioner/" + pracId + "?_format=json";

        JSONObject json = null;
        try {
            // Read JSON file which contains information about this practitioner.
            json = JsonReader.readJsonFromUrl(pracUrl);
        } catch (IOException | JSONException e) {
            // URL is not a valid URL.
            ;
        }

        if (json != null) {
            // Parse the document.
            Document doc = Document.parse(json.toString());

            // Insert into local database or update document if the practitioner with this ID already exists in the
            // database.
            Bson filter = Filters.eq("id", pracId);
            Bson update =  new Document("$set", doc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            Mongo.db.getCollection("Practitioner").updateOne(filter, update, options);
        }
    }
}
