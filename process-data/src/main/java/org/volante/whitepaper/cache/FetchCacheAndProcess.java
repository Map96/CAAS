package org.volante.whitepaper.cache;

import org.apache.logging.log4j.*;
import redis.clients.jedis.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;
import static org.volante.whitepaper.cache.LoadDataInCaffeine.*;

public class FetchCacheAndProcess {
    public static final Logger logger = LogManager.getLogger(FetchCacheAndProcess.class.getName());

    public static Set<String> getAllKeysFromRedis() {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            return jedis.keys("*");
        }
    }

    public static Set<String> getAllKeysFromCaffeine() {
        return caffeineCache.asMap().keySet();
    }

    private static String getRandomKey(Set<String> keys) {
        int randomIndex = new Random().nextInt(keys.size());
        return keys.stream().skip(randomIndex).findFirst().orElse(null);
    }

    public static void startCacheOps() {
        if (isExternalCache) {
            fetchDataFromRedis();
        } else {
            fetchDataFromCaffeine();
        }
    }

    public static void fetchDataFromCaffeine() {
        Set<String> allKeysFromCaffeine = getAllKeysFromCaffeine();
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            int threadId = i + 1;
            executorService.execute(() -> {
                if (!allKeysFromCaffeine.isEmpty()) {
                    String randomKey = getRandomKey(allKeysFromCaffeine);
                    logger.info("Thread: " + threadId + " Start Key: " + randomKey);
                    for (int j = 0; j < NUM_QUERIES_PER_THREAD; j++) {
                        logger.info("Thread: " + threadId + " Key: " + randomKey + " TIMESTAMP:CAFFEINE:START: " + new Timestamp(System.currentTimeMillis()));
                        Map<String, String> document = caffeineCache.getIfPresent(randomKey);
                        while (document == null) {
                            logger.info("Thread: " + threadId + " Key: " + randomKey + " CACHE MISS");
                            document = caffeineCache.getIfPresent(randomKey);
                        }
                        logger.info("Thread: " + threadId + " Key: " + randomKey + " TIMESTAMP:CAFFEINE:END: " + new Timestamp(System.currentTimeMillis()));
                        String objectId = document.get(OBJECT_ID);
                        logger.info("Thread: " + threadId + " Key: " + randomKey + " ObjectKey: " + objectId);
                        randomKey = objectId;
                    }
                }
            });
        }
        executorService.shutdown();
        System.out.println("Process Finished");
    }

    public static void fetchDataFromRedis() {
        Set<String> allKeysFromRedis = getAllKeysFromRedis();
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            int threadId = i + 1;
            executorService.execute(() -> {
                if (!allKeysFromRedis.isEmpty()) {
                    String randomKey = getRandomKey(allKeysFromRedis);
                    logger.info("Thread: " + threadId + " Start Key: " + randomKey);
                    for (int j = 0; j < NUM_QUERIES_PER_THREAD; j++) {
                        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                            logger.info("Thread: " + threadId + " Key: " + randomKey + " TIMESTAMP:REDIS:START: " + new Timestamp(System.currentTimeMillis()));
                            while (!jedis.exists(randomKey)) {
                                logger.info("Thread: " + threadId + " Key: " + randomKey + " CACHE MISS");
                            }
                            String objectId = jedis.hget(randomKey, OBJECT_ID);
                            logger.info("Thread: " + threadId + " Key: " + randomKey + " TIMESTAMP:REDIS:END: " + new Timestamp(System.currentTimeMillis()));
                            logger.info("Thread: " + threadId + " Key: " + randomKey + " ObjectKey: " + objectId);
                            randomKey = objectId;
                        }
                    }
                }
            });
        }
        executorService.shutdown();
        System.out.println("Process Finished");
    }
}
