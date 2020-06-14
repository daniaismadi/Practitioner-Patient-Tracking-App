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
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;

/***
 * A class to retrieve observation information from the server, insert observation information into the database
 *  * and query information about observations from the database. Implements the ObservationDAO interface.
 *
 */
public class ObservationRepository implements ObservationDAO {

    /**
     * Root URL of the server.
     */
    private String rootUrl = "https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/";

    /**
     * Map of Observation types to their LOINC codes.
     */
    private Map<String, String> observationCodes = new HashMap<String, String>() {{
        put("Cholesterol", "2093-3");
        put("Blood Pressure", "55284-4");
    }};

    /***
     * Instance of local MongoDB database.
     */
    MongoDatabase db;

    /***
     * Class constructor for Observation Repository. Initialises a reference to the local MongoDB database.
     *
     */
    public ObservationRepository() {
        this.db = Mongo.db;
    }

    /***
     * Insert count number of observations, specified by code, of patient with patientId in order of descending
     * date, i.e., the latest observation first.
     *
     * @param patientId     the ID of the patient
     * @param code          the code that specifies the observation type
     * @param count         the number of observations to insert
     */
    @Override
    public void insertPatientObsByCode(String patientId, String code, String count) {
        // URL to get count number of observations as specified by code.
        String obsUrl = rootUrl + "Observation?_count=" + count + "&code=" + code + "&patient=" + patientId +
                "&_sort=-date&_format=json";


        JSONObject observationBundle = null;
        try {
            // Read JSON resource document which contains information about this observation.
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
                // successfully added observations for this patient
                System.out.println("Successfully added observations for " + patientId);
            }
        } catch (JSONException e) {
            // This means this person has no observations yet.
            System.out.println(patientId + " currently has no observations.");
        }
    }

    /***
     * Retrieve cholesterol observations of patient from the server and insert into the database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to insert
     */
    @Override
    public void insertCholesterolObs(String patientId, int count) {
        // Retrieve cholesterol observation code.
        String code = observationCodes.get("Cholesterol");
        // Insert cholesterol observations of patient into the database.
        insertPatientObsByCode(patientId, code, String.valueOf(count));
    }

    /***
     * Retrieve blood pressure observations (systolic and diastolic) of patient from the server and insert into
     * the database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to insert
     */
    @Override
    public void insertBPObs(String patientId, int count) {
        // Retrieve blood pressure observation code.
        String code = observationCodes.get("Blood Pressure");
        // Insert blood pressure observations of patient into the database.
        insertPatientObsByCode(patientId, code, String.valueOf(count));
    }

    /***
     * Retrieve cholesterol observations of patient from the local database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to retrieve
     * @return              a list of an array of objects which is in the format of [Date, Cholesterol Value] sorted in
     *                      descending order of Date. Date is of type Date and Cholesterol Value is of type double.
     * @throws ParseException   Occurs if there is an error in parsing the effectiveDateTIme.
     */
    @Override
    public List<Object[]> getCholesterolObs(String patientId, int count) throws ParseException {
        // Retrieve cholesterol observation code.
        String code = observationCodes.get("Cholesterol");

        // Access Observation collection.
        MongoCollection<Document> collection = db.getCollection("Observation");

        // Find documents for observations with this patient ID.
        Bson filterPatient = eq("subject.reference", "Patient/" + patientId);

        // Find documents for observations that have this code.
        Bson filterType = eq("code.coding.code", code);

        // Return the effectiveDateTime and valueQuantity entries in this document.
        Bson filterProjection = fields(include("effectiveDateTime", "valueQuantity", "id"), excludeId());

        // Order in descending effectiveDateTime so will return count latest measurements.
        Bson filterSort = descending("effectiveDateTime");
        Bson filter = and(filterPatient, filterType);

        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(filterProjection))
                .sort(filterSort)
                .limit(count);

        List<Object[]> observations = new ArrayList<>();

        for (Document doc : result) {
            // get date of cholesterol observation
            Date date = getEffectiveDate(doc.get("id", String.class));

            // get value of cholesterol observation
            Document valueQuantity = doc.get("valueQuantity", Document.class);

            try {
                double value = valueQuantity.get("value", Double.class);
                // add to observations list
                observations.add(new Object[]{date, value});
            } catch (ClassCastException e) {
                Integer val = valueQuantity.get("value", Integer.class);
                double value = (double) val;
                // add to observations list
                observations.add(new Object[]{date, value});
            }
        }

        // return observation list
        return observations;
    }

    /***
     * Retrieve systolic BP observations of patient from the local database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to retrieve
     * @return              a list of an array of objects which is in the format of [Date, Systolic BP Value] sorted
     *                      in descending order of Date. Date is of type Date and Systolic BP Value is of type
     *                      double.
     * @throws ParseException   if a parse exception occurs when retrieving the date
     */
    @Override
    public List<Object[]> getSystolicBPObs(String patientId, int count) throws ParseException {
        return getBPObs(patientId, "Systolic Blood Pressure", count);
    }

    /***
     * Retrieve diastolic BP observations of patient from the local database.
     *
     * @param patientId     the ID of the patient
     * @param count         the number of observations to retrieve
     * @return              a list of an array of objects which is in the format of [Date, Diastolic BP Value] sorted
     *                      in descending order of Date. Date is of type Date and Systolic BP Value is of type double.
     * @throws ParseException   if a parse exception occurs when retrieving the date
     */
    @Override
    public List<Object[]> getDiastolicBPObs(String patientId, int count) throws ParseException {
        return getBPObs(patientId, "Diastolic Blood Pressure", count);
    }

    /***
     * Insert observation into the database.
     *
     * @param obsId     the ID of the observation
     */
    private void insertObs(String obsId) {
        // Observation URL.
        String obsUrl = rootUrl + "Observation/" + obsId + "?_format=json";

        JSONObject json = null;
        try {
            // Read JSON resource document which contains information about this observation.
            json = JsonReader.readJsonFromUrl(obsUrl);
            Document doc = Document.parse(json.toString());

            // Insert or update document with this information ID into the database.
            Bson filter = Filters.eq("id", obsId);
            Bson update = new Document("$set", doc);
            UpdateOptions options = new UpdateOptions().upsert(true);

            db.getCollection("Observation").updateOne(filter, update, options);
        } catch (JSONException | IOException e) {
            // URL is not valid.
            ;
        }
    }

    /***
     * Helper function to retrieve a certain blood pressure values from a blood pressure document in the database.
     *
     * @param patientId     the ID of the patient
     * @param bpType        the type of blood pressure value to obtain, either "Systolic Blood Pressure" or
     *                      "Diastolic Blood Pressure"
     * @param count         the number of observations to retrieve
     * @return              a list of an array of objects which is in the format of [Date, BP Value] sorted in
     *                      descending order of Date. Date is of type Date and BP value is of type double.
     * @throws ParseException   if a parse exception occurs when retrieving the date
     */
    private List<Object[]> getBPObs(String patientId, String bpType, int count) throws ParseException {
        // Retrieve blood pressure observation code.
        String code = observationCodes.get("Blood Pressure");

        // Access the Observation collection.
        MongoCollection<Document> collection = db.getCollection("Observation");
        // Find the observation document of the patient with this patient ID.
        Bson filterPatient = eq("subject.reference", "Patient/" + patientId);
        // Find the observation with this code.
        Bson filterType = eq("code.coding.code", code);
        // Return the id, effectiveDateTime and component of this Observation document.
        Bson filterProjection = fields(include("id", "effectiveDateTime", "component"), excludeId());

        // Sort the documents in descending effectiveDateTime to retrieve the latest measurements.
        Bson filterSort = descending("effectiveDateTime");
        Bson filter = and(filterPatient, filterType);

        // Query the database.
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(filterProjection))
                .sort(filterSort)
                .limit(count);

        // Initialise a list to contain information of the observations.
        List<Object[]> observations = new ArrayList<>();

        for (Document doc : result) {
            // get date of blood pressure observation
            Date date = getEffectiveDate(doc.get("id", String.class));

            // get systolic or diastolic bp measurement stored in component
            ArrayList<Document> component = doc.get("component", ArrayList.class);

            for (Document obs : component) {
                // Distinguish between systolic and diastolic BP.
                Document componentCode = obs.get("code", Document.class);
                String obsType = componentCode.get("text", String.class);

                if (obsType.equalsIgnoreCase(bpType)) {
                    // Value is stored inside valueQuantity.
                    Document valueQuantity = obs.get("valueQuantity", Document.class);
                    try {
                        double value = valueQuantity.get("value", Double.class);
                        // add to observations list
                        observations.add(new Object[]{date, value});
                    } catch (ClassCastException e) {
                        Integer val = valueQuantity.get("value", Integer.class);
                        double value = (double) val;
                        // add to observations list
                        observations.add(new Object[]{date, value});
                    }
                }
            }

        }

        // return observation list
        return observations;
    }

    /***
     * Helper function to retrieve the effective date time of an observation.
     *
     * @param obsId             the ID of the observation
     * @return                  the formatted effective date time of the observation
     * @throws ParseException   if there is an error in parsing the SimpleDateFormat
     */
    private Date getEffectiveDate(String obsId) throws ParseException {
        // Access Observation collection.
        MongoCollection<Document> collection = db.getCollection("Observation");

        // Search for the document with this observation ID.
        Bson filter = eq("id", obsId);
        FindIterable<Document> result = collection.find(filter, Document.class)
                .projection(fields(include("effectiveDateTime"), excludeId()));

        String dateStr = "";

        for (Document doc : result) {
            // Get the effective date time of this observation.
            dateStr = doc.get("effectiveDateTime", String.class);
        }

        // 1999-08-24T15:48:33+10:00
        // Format into Date object.
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return format.parse(dateStr);
    }

//    /***
//     * Retrieves cholesterol observations from the server and for each cholesterol observation retrieved, find the
//     * patients associated with this observation and insert their latest measurements for the observations
//     * specified by their LOINC codes in the parameter codes.
//     *
//     * @param codes     the codes for the other types of observations to retrieve
//     * @param count     the number of observations to show per page
//     * @param pageCount the page number on the server to start collecting observations at
//     */
//    @Override
//    public void insertCholesObsByCodes(ArrayList<String> codes, String count, int pageCount) {
//        String obsUrl = rootUrl + "Observation?_count=" + count + "&_sort=-date&code=http%3A%2F%2Floinc.org%7C2093-3" +
//                "&_format=json";
//        JSONObject observationBundle = null;
//
//        // Declare required variables
//        boolean nextPage = true;
//        String nextUrl = obsUrl;
//
//        // go to the desired page
//        for (int i = 0; i < pageCount; i++) {
//            if (!nextPage) {
//                break;
//            }
//
//            nextPage = false;
//            try {
//                observationBundle = JsonReader.readJsonFromUrl(nextUrl);
//            } catch (JSONException | IOException e) {
//                e.printStackTrace();
//            }
//            try{
//                if (observationBundle != null) {
//                    // Check if there is a next page
//                    // Get all related links
//                    JSONArray links = observationBundle.getJSONArray("link");
//                    for (int j = 0; j < links.length(); j++) {
//                        // Get current link
//                        JSONObject link = links.getJSONObject(j);
//                        // Check if relation is next, this means there is a next page
//                        String relation = link.getString("relation");
//                        if (relation.equalsIgnoreCase("next")) {
//                            // Update variables accordingly
//                            System.out.println(i);
//                            nextPage = true;
//                            nextUrl = link.getString("url");
//                        }
//                    }
//                }
//            }
//            catch (JSONException e){
//                e.printStackTrace();
//            }
//
//        }
//
//        // retrieve data from this page
//        try {
//            observationBundle = JsonReader.readJsonFromUrl(nextUrl);
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            JSONArray observations = observationBundle.getJSONArray("entry");
//            for (int i = 0; i < observations.length(); i++) {
//                JSONObject entry = observations.getJSONObject(i);
//                // get resource
//                JSONObject resource = entry.getJSONObject("resource");
//                // get observation id
//                String obsId = resource.getString("id");
//                // insert observation to database
//                insertObs(obsId);
//
//                JSONObject subject = resource.getJSONObject("subject");
//                String patientId = subject.getString("reference");
//                patientId = patientId.replace("Patient/", "");
//
//                // get patient observations of other measures
//                for (String code : codes) {
//                    insertPatientObsByCode(patientId, code, "1");
////                    insertPatientLatestObsByCode(patientId, code);
//                }
//
//                System.out.println("Retrieved observation " + obsId);
//            }
//        } catch (JSONException e) {
//            System.out.println("Failed to retrieve observations.");
//        }
//    }

//    private void insertLatestCholesObs(String patientId) {
//        String cholesCode = "2093-3";
//        insertPatientLatestObsByCode(patientId, cholesCode);
//    }

//    public void insertPatientLatestObsByCode(String patientId, String code) {
//        System.out.println("Updating observations for: " + patientId);
//        // just get the latest observation
//        String obsUrl = rootUrl + "Observation?code=" + code + "&patient=" + patientId + "&_sort=-date&_count=1&_format=json";
//        JSONObject observationBundle = null;
//        try {
//            observationBundle = JsonReader.readJsonFromUrl(obsUrl);
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            JSONArray observations = observationBundle.getJSONArray("entry");
//            JSONObject entry = observations.getJSONObject(0);
//            // get resource
//            JSONObject resource = entry.getJSONObject("resource");
//            // get observation id
//            String obsId = resource.getString("id");
//            // insert observation to database
//            insertObs(obsId);
//            System.out.println("Successfully added observations for " + patientId);
//            } catch (JSONException e) {
//            // This means this person has no observations yet.
//            System.out.println(patientId + " currently has no observations.");
//        }
//    }

//    private ArrayList<String> getAllPatientsIdsObs() {
//        DistinctIterable<String> patients = Mongo.db.getCollection("Observation")
//                .distinct("subject.reference", null, String.class);
//
//        ArrayList<String> patientIds = new ArrayList<>();
//
//        for (String id : patients) {
//            id = id.replace("Patient/", "");
//            patientIds.add(id);
//        }
//
//        return patientIds;
//    }

//    @Override
//    public Date getLatestCholesDate(String patientId) {
//        ArrayList<String> allCholesObs = getAllCholesObs(patientId);
//        return getLatestObsDate(allCholesObs);
//    }
//
//    @Override
//    public double getLatestCholesVal(String patientId) {
//        ArrayList<String> allCholesObs = getAllCholesObs(patientId);
//        return getLatestObsVal(allCholesObs);
//    }

//    private ArrayList<String> getAllObsByCode(String patientId, String code) {
//        MongoCollection<Document> collection = db.getCollection("Observation");
//        Bson filterPatient = eq("subject.reference", "Patient/" + patientId);
//        Bson filterType = eq("code.coding.code", code);
//        Bson filter = and(filterPatient, filterType);
//
//        FindIterable<Document> result = collection.find(filter, Document.class)
//                .projection(fields(include("id"), excludeId()));
//
//        ArrayList<String> observations = new ArrayList<>();
//
//        for (Document doc : result) {
//            observations.add(doc.get("id", String.class));
//        }
//
//        return observations;
//    }

//    private ArrayList<String> getAllCholesObs(String patientId) {
//        String cholesCode = "2093-3";
//        return getAllObsByCode(patientId, cholesCode);
//    }

//    private double getObsValue(String obsId) {
//        MongoCollection<Document> collection = db.getCollection("Observation");
//        Bson filter = eq("id", obsId);
//        FindIterable<Document> result = collection.find(filter, Document.class)
//                .projection(fields(include("valueQuantity"), excludeId()));
//
//        double value = 0;
//
//        for (Document doc : result) {
//            Document valueQuantity = doc.get("valueQuantity", Document.class);
//            Document valueQuantityParsed = Document.parse(valueQuantity.toJson());
//
//            try {
//                value = valueQuantityParsed.get("value", Double.class);
//            } catch (ClassCastException e) {
//                Integer val = valueQuantityParsed.get("value", Integer.class);
//                value = (double) val;
//            }
//        }
//
//        return value;
//    }

//    private Date getLatestObsDate(ArrayList<String> obsIds) {
//        if (obsIds.size() == 0) {
//            // Patient does not have any observations.
//            return null;
//        }
//
//        Date latestDate = new Date(Long.MIN_VALUE);
//
//        for (String obsId : obsIds) {
//            try {
//                // this means that getEffectiveDate occurs after latestDate
//                if (getEffectiveDate(obsId).compareTo(latestDate) > 0) {
//                    // update latestDate
//                    latestDate = getEffectiveDate(obsId);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return latestDate;
//    }
//
//    private double getLatestObsVal(ArrayList<String> obsIds) {
//        if (obsIds.size() == 0) {
//            // Patient does not have any cholesterol observations.
//            return 0;
//        }
//
//        Date latestDate = new Date(Long.MIN_VALUE);
//        double val = 0;
//
//        for (String obsId : obsIds) {
//            try {
//                // this means that getEffectiveDate occurs after latestDate
//                if (getEffectiveDate(obsId).compareTo(latestDate) > 0) {
//                    // update latestDate
//                    latestDate = getEffectiveDate(obsId);
//                    val = getObsValue(obsId);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return val;
//    }
}
