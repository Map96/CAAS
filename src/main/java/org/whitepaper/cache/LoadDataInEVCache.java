package org.whitepaper.cache;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bson.Document;

import static org.whitepaper.cache.CachePackageConstants.*;

public class LoadDataInEVCache {
    public static void main(String[] args) {
        Cache<String, String> caffeineCache = Caffeine.newBuilder().build();
        try (MongoClient mongoClient = new MongoClient(MONGODB_HOST, MONGODB_PORT)) {
            MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
            MongoCollection<Document> collection = database.getCollection(CORRELATION);
            System.out.println("Loaded documents from collection: " + CORRELATION);
            for (Document document : collection.find()) {
                System.out.println("Inserting Record in EVCache: " + document.get(RECORD_ID).toString());
                caffeineCache.put(document.get(_ID).toString(), document.get(OBJECT_ID).toString());
            }
            System.out.println("Inserted documents into EVCache hash.");
            System.out.println(caffeineCache.getIfPresent("fc0bbb04-596c-42ef-8020-f54437bd4043"));
        }
    }
}
