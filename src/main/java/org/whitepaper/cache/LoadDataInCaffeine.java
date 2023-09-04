package org.whitepaper.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static org.whitepaper.cache.CachePackageConstants.*;
import static org.whitepaper.cache.CachePackageUtils.getMongoConnectionSettings;

public class LoadDataInCaffeine {
    public static void main(String[] args) {
        Cache<String, String> caffeineCache = Caffeine.newBuilder().build();
        try (MongoClient mongoClient = MongoClients.create(getMongoConnectionSettings())) {
            MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
            MongoCollection<Document> collection = database.getCollection(CORRELATION);
            System.out.println("Loaded documents from collection: " + CORRELATION);
            for (Document document : collection.find()) {
                System.out.println("Inserting Record in Caffeine: " + document.get(RECORD_ID).toString());
                caffeineCache.put(document.get(_ID).toString(), document.get(OBJECT_ID).toString());
            }
            System.out.println("Inserted documents into Caffeine Cache.");
            System.out.println(caffeineCache.getIfPresent("fc0bbb04-596c-42ef-8020-f54437bd4043"));
        }
    }
}
