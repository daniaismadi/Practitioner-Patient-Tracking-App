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

    public PractitionerRepository() {
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
}
