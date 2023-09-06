package org.volante.whitepaper.cache;

import com.github.benmanes.caffeine.cache.*;
import org.bson.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;

public class LoadDataInCaffeine {
    static Cache<String, String> caffeineCache;

    static {
        caffeineCache = Caffeine.newBuilder().build();
    }

    public static void addDataToCaffeine(Document document) {
        //        try (MongoClient mongoClient = MongoClients.create(getMongoConnectionSettings())) {
        //            MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
        //            MongoCollection<Document> collection = database.getCollection(CORRELATION);
        //            System.out.println("Loaded documents from collection: " + CORRELATION);
        //            for (Document document : collection.find()) {
        //                System.out.println("Inserting Record in Caffeine: " + document.get(RECORD_ID).toString());
        //                caffeineCache.put(document.get(_ID).toString(), document.get(OBJECT_ID).toString());
        //            }
        //            System.out.println("Inserted documents into Caffeine Cache.");
        //            System.out.println(caffeineCache.getIfPresent("fc0bbb04-596c-42ef-8020-f54437bd4043"));
        //        }
        System.out.println("Inserting Record in Caffeine: " + document.get(RECORD_ID).toString());
        caffeineCache.put(document.get(_ID).toString(), document.get(OBJECT_ID).toString());
    }
}
