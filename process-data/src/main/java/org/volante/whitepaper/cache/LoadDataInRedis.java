package org.volante.whitepaper.cache;

import org.bson.*;
import redis.clients.jedis.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;

public class LoadDataInRedis {
    public static void addDataToRedis(Document document) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            Pipeline pipeline = jedis.pipelined();
            System.out.println("Inserting Record in Pipeline: " + document.get(RECORD_ID).toString());
            pipeline.hset(document.get(_ID).toString(), OBJECT_ID, document.get(OBJECT_ID).toString());
            pipeline.hset(document.get(_ID).toString(), PAYLOAD, document.get(PAYLOAD).toString());
            pipeline.sync();
        }
    }
}
