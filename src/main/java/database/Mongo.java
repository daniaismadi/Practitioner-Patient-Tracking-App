package database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Mongo {
    static MongoDatabase db;
    static final String DATABASE_NAME = "FIT3077JD";

    public static void connect() {
        MongoClient mongoClient = new MongoClient();
        System.out.println("Server connected!");
        db = mongoClient.getDatabase(DATABASE_NAME);
        System.out.println("Connected to database successfully!");
    }
}
