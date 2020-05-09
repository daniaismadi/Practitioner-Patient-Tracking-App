package database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Mongo {
    static MongoDatabase db;

    public static void connect() {
        MongoClient mongoClient = new MongoClient();
        System.out.println("Server connected!");
        db = mongoClient.getDatabase("test");
    }
}
