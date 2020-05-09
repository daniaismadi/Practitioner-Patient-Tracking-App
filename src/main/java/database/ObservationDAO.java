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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

public class ObservationDAO {

    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";
    MongoDatabase db;

    public ObservationDAO() {
        this.db = Mongo.db;
    }

    /***
     * Insert all observations of the patient with this patient ID into the database.
     *
     * @param patientId     The patient ID.
     * @throws IOException
     */
    void insertObservations(String patientId) throws IOException {
        String obsUrl = rootUrl + "Observation?_count=13&code=2093-3&patient=" + patientId + "&_sort=date&_format=json";
        JSONObject observationBundle = JsonReader.readJsonFromUrl(obsUrl);

        JSONArray observations = observationBundle.getJSONArray("entry");
        for (int i = 0; i < observations.length(); i++) {
            // get current entry
            JSONObject entry = observations.getJSONObject(i);
            // get resource
            JSONObject resource = entry.getJSONObject("resource");
            // get observation id
            String obsId = resource.getString("id");

            // insert observation to database
            insertObs(obsId);
        }
    }

    /***
     * Insert observation with observation ID into database.
     *
     * @param obsId             The observation ID.
     * @throws IOException
     * @throws JSONException
     */
    void insertObs(String obsId) throws IOException, JSONException {
        String obsUrl = rootUrl + "Observation/" + obsId + "?_format=json";
        JSONObject json = JsonReader.readJsonFromUrl(obsUrl);
        Document doc = Document.parse(json.toString());

        Bson filter = Filters.eq("id", obsId);
        Bson update = new Document("$set", doc);
        UpdateOptions options = new UpdateOptions().upsert(true);

        db.getCollection("Observation").updateOne(filter, update, options);
    }

    /***
     * Return the cholesterol value of this observation.
     * TODO: Have to check that this observation is a cholesterol type.
     *
     * @param obsId
     * @return
     */
    double getCholesterol(String obsId) {
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

    /***
     * Return the effective date time of the observation, given observation ID.
     *
     * @param obsId             The observation ID.
     * @return                  The effective date time of this observation.
     * @throws ParseException
     */
    Date getEffectiveDate(String obsId) throws ParseException {
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

    /***
     * Return latest cholesterol value and the effective date time for this patient, if any.
     *
     * @param patientId     The patient id.
     * @return              A list of strings containing {date, cholesterol value}.
     */
    String[] getLatestCholesValues(String patientId) {
        MongoCollection<Document> collection = db.getCollection("Observation");
        Bson filter = eq("subject.reference", "Patient/" + patientId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("id"), excludeId()));

        Date latestDate = new Date(Long.MIN_VALUE);
        double cholesVal = 0;

        for (Document doc: result) {
            String obsId = doc.get("id", String.class);

            try {
                // this means that getEffectiveDate occurs after latestDate
                if (getEffectiveDate(obsId).compareTo(latestDate) > 0) {
                    // update latestDate
                    latestDate = getEffectiveDate(obsId);
                    cholesVal = getCholesterol(obsId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
        String strDate = dateFormat.format(latestDate);

        return new String[]{strDate, String.valueOf(cholesVal)};
    }
}
