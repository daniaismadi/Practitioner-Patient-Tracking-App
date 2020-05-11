package database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.logging.Filter;

import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.push;

public class MonitorRepository implements MonitorDAO {
    MongoDatabase db = Mongo.db;

    public MonitorRepository() {
    }

    @Override
    public void insertPatient(String hPracId, String hPracIdentifier, String patientId) {
        Bson filter = Filters.eq("PractitionerID", hPracId);
        Bson update = new Document()
                .append("PractitionerID", hPracId)
                .append("PractitionerIdentifier", hPracIdentifier);

        Bson set =  new Document("$set", update);
        Bson add = addToSet("patients", patientId);

        UpdateOptions options = new UpdateOptions().upsert(true);
        db.getCollection("Monitor").updateOne(filter, set, options);
        db.getCollection("Monitor").updateOne(filter, add, options);

    }

    @Override
    public void removePatient(String hPracId, String patientId) {
        Bson filter = Filters.eq("PractitionerID", hPracId);
        Bson pull = Updates.pull("patients", patientId);
        UpdateOptions options = new UpdateOptions().upsert(true);
        db.getCollection("Monitor").updateOne(filter, pull, options);
    }

    @Override
    public ArrayList<String> getMonitoredPatients(String hPracId) {
        Bson filter = Filters.eq("PractitionerID", hPracId);
        MongoCollection<Document> monitor = db.getCollection("Monitor");
        FindIterable<Document> result = monitor.find(filter, Document.class);

        for (Document doc : result) {
            return doc.get("patients", ArrayList.class);
        }

        return new ArrayList<>();

    }
}
