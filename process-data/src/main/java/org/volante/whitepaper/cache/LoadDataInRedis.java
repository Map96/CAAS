package org.volante.whitepaper.cache;

import org.bson.*;
import redis.clients.jedis.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;

public class LoadDataInRedis {
    public static void addDataToRedis(Document document) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            //            Pipeline pipeline = jedis.pipelined();
            //            try (MongoClient mongoClient = MongoClients.create(getMongoConnectionSettings())) {
            //                MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
            //                MongoCollection<Document> collection = database.getCollection(CORRELATION);
            //                System.out.println("Loaded documents from collection: " + CORRELATION);
            //                for (Document document : collection.find()) {
            //                    System.out.println("Inserting Record in Pipeline: " + document.get(RECORD_ID).toString());
            //                    pipeline.set(document.get(_ID).toString(), document.get(OBJECT_ID).toString());
            //                }
            //                pipeline.sync();
            //                System.out.println("Inserted documents into Redis hash.");
            //            }
            Pipeline pipeline = jedis.pipelined();
            System.out.println("Inserting Record in Pipeline: " + document.get(RECORD_ID).toString());
            pipeline.set(document.get(_ID).toString(), document.get(OBJECT_ID).toString());
            pipeline.sync();
        }
    }
}
