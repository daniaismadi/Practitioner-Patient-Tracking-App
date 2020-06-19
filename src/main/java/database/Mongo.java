package database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/***
 * Class to access the local MongoDB database.
 */
public class Mongo {
    /**
     * The local MongoDB database.
     */
    static MongoDatabase db;

    /**
     * The name of this database.
     */
    static final String DATABASE_NAME = "FIT3077JD";

    /***
     * Establishes a connection with the local MongoDB database.
     */
    public static void connect() {
        // Establish a new client.
        MongoClient mongoClient = new MongoClient();
        System.out.println("Server connected!");
        // Connect to this database.
        db = mongoClient.getDatabase(DATABASE_NAME);
        System.out.println("Connected to database successfully!");
    }
}
