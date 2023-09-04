package org.whitepaper.cache;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import java.util.*;

import static org.whitepaper.cache.CachePackageConstants.*;
import static org.whitepaper.cache.CachePackageUtils.getMongoConnectionSettings;

public class CreateAndInsertSamples {

    static String objectId = null, _id;
    static int recordId = 1, BATCH_COUNT = 1000, BATCH_SIZE = 10000;
    public static void main(String[] args) {
        //First Record: "fc0bbb04-596c-42ef-8020-f54437bd4043"
        try (MongoClient mongoClient = MongoClients.create(getMongoConnectionSettings())) {
            MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
            MongoCollection<Document> collection = database.getCollection(CORRELATION);

            createIndex(collection);

            for (int i = 0; i < BATCH_COUNT; i++) {
                List<Document> batchDocuments = new ArrayList<>();
                for (int j = 0; j < BATCH_SIZE; j++) {
                    _id = objectId == null ? UUID.randomUUID().toString() : objectId;
                    objectId = UUID.randomUUID().toString();
                    Document document = new Document()
                            .append(_ID, _id)
                            .append(RANDOM_STRING, generateRandomString())
                            .append(RECORD_ID, recordId++)
                            .append(RANDOM_DATE, new Date())
                            .append(RANDOM_INT, new Random().nextInt(1000))
                            .append(OBJECT_ID, objectId);
                    batchDocuments.add(document);
                }
                collection.insertMany(batchDocuments);
                System.out.println("Inserted batch " + i);
            }
        }
    }

    private static void createIndex(MongoCollection<Document> collection) {
        Document index1 = new Document(_ID, 1);
        IndexOptions options1 = new IndexOptions();
        collection.createIndex(index1, options1);
        Document index2 = new Document(RECORD_ID, 1);
        IndexOptions options2 = new IndexOptions().unique(true);
        collection.createIndex(index2, options2);
        System.out.println("Indexes created successfully.");
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
