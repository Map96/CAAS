package org.whitepaper.cache;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import static org.whitepaper.cache.CachePackageConstants.*;
import static org.whitepaper.cache.CachePackageUtils.getMongoConnectionSettings;

public class LoadDataInRedis {
    public static void main(String[] args) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            Pipeline pipeline = jedis.pipelined();
            try (MongoClient mongoClient = MongoClients.create(getMongoConnectionSettings())) {
                MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
                MongoCollection<Document> collection = database.getCollection(CORRELATION);
                System.out.println("Loaded documents from collection: " + CORRELATION);
                for (Document document : collection.find()) {
                    System.out.println("Inserting Record in Pipeline: " + document.get(RECORD_ID).toString());
                    pipeline.set(document.get(_ID).toString(), document.get(OBJECT_ID).toString());
                }
                pipeline.sync();
                System.out.println("Inserted documents into Redis hash.");
            }
        }
    }
}
