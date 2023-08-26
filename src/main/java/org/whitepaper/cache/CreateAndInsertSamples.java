package org.whitepaper.cache;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Date;
import java.util.Random;

import static org.whitepaper.cache.CachePackageConstants.*;

public class CreateAndInsertSamples {
    public static void main(String[] args) {
        try (com.mongodb.MongoClient mongoClient = new MongoClient(MONGODB_HOST, MONGODB_PORT)) {
            MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
            int collectionCount = 1000;
            int recordCount = 30000;
            for (int i = 1; i <= collectionCount; i++) {
                String collectionName = COLLECTION + UNDERSCORE + i;
                MongoCollection<Document> collection = database.getCollection(collectionName);
                for (int j = 1; j <= recordCount; j++) {
                    Document document = new Document()
                            .append(OBJECT_ID, j)
                            .append(RANDOM_INT, new Random().nextInt(100))
                            .append(RANDOM_DATE, new Date())
                            .append(RANDOM_STRING, generateRandomString());
                    collection.insertOne(document);
                }
                System.out.println("Inserted " + recordCount + " documents into collection: " + collectionName);
            }
        }
    }

    private static String generateRandomString() {
        String characters = CHARACTER_SET;
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}
