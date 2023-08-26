package org.whitepaper.cache;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import static org.whitepaper.cache.CachePackageConstants.*;

public class LoadDataInMemoryCache {
    public static void main(String[] args) {
        try (MongoClient mongoClient = new MongoClient(MONGODB_HOST, MONGODB_PORT)) {
            MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
            try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                for (String collectionName : database.listCollectionNames()) {
                    MongoCollection<Document> collection = database.getCollection(collectionName);
                    System.out.println("Loaded documents from collection: " + collectionName);
                    for (Document document : collection.find()) {
                        jedis.hset(collectionName, document.get(OBJECT_ID).toString(), document.toJson());
                    }
                    System.out.println("Inserted documents into Redis hash: " + collectionName);
                }
            }
        }
    }
}
