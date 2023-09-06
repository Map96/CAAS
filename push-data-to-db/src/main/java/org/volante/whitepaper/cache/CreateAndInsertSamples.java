package org.volante.whitepaper.cache;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.*;

import javax.servlet.*;
import java.util.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;
import static org.volante.whitepaper.cache.CachePackageUtils.*;


public class CreateAndInsertSamples implements ServletContextListener {

    static String objectId = null, _id;
    static int recordId = 1, BATCH_COUNT = 1000, BATCH_SIZE = 10000, i = 1;

    public static void pumpDataToDb() {
        try (MongoClient mongoClient = MongoClients.create(getMongoConnectionSettings())) {
            MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
            MongoCollection<Document> collection = database.getCollection(CORRELATION);

            createIndex(collection);

            while (true) {
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

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        pumpDataToDb();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
