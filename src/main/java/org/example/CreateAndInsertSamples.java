package org.example;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Date;
import java.util.Random;

public class CreateAndInsertSamples {
    public static void main(String[] args) {
        // MongoDB's connection string
        String connectionString = "mongodb://localhost:27017";

        // Connect to MongoDB
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            // Connect to the "cachedb" database
            MongoDatabase database = mongoClient.getDatabase("cachedb");

            // Create N collections and insert M documents into each
            int N = 1000;  // Number of collections
            int M = 30000; // Number of documents per collection

            for (int i = 1; i <= N; i++) {
                String collectionName = "collection_" + i;
                MongoCollection<Document> collection = database.getCollection(collectionName);

                for (int j = 1; j <= M; j++) {
                    Document document = new Document()
                            .append("randomInt", new Random().nextInt(100))
                            .append("randomDate", new Date())
                            .append("randomString", generateRandomString());
                    collection.insertOne(document);
                }

                System.out.println("Inserted " + M + " documents into collection: " + collectionName);
            }
        }
    }

    private static String generateRandomString() {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}
