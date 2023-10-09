package org.volante.whitepaper.cache;

import org.bson.*;
import redis.clients.jedis.*;

import java.time.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;

public class LoadDataInRedis {
    public static void addDataToRedis(Document document) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            Pipeline pipeline = jedis.pipelined();
            String key = document.get(_ID).toString();
            pipeline.hset(key, OBJECT_ID, document.get(OBJECT_ID).toString());
            pipeline.hset(key, PAYLOAD, document.get(PAYLOAD).toString());
            pipeline.expire(key, Duration.ofMinutes(TTL).toSeconds());
            pipeline.sync();
        }
    }
}
