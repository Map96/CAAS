package org.volante.whitepaper.cache;

import redis.clients.jedis.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;
import static org.volante.whitepaper.cache.LoadDataInCaffeine.*;

public class FetchCacheAndProcess {
    public static final Logger logger = Logger.getLogger(FetchCacheAndProcess.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("ThreadLog.log");
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> getAllKeysFromRedis() {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            return jedis.keys("*");
        }
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
        Set<String> allKeysFromRedis = getAllKeysFromRedis();
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            int threadId = i + 1;
            executorService.execute(() -> {
                String randomKey = getRandomKey(allKeysFromRedis);
                for (int j = 0; j < NUM_QUERIES_PER_THREAD; j++) {
                    Map<String, String> document = caffeineCache.getIfPresent(randomKey);
                    String objectId = document.get(OBJECT_ID);
                    logger.info("Thread: " + threadId + " Key: " + randomKey + " ObjectKey: " + objectId);
                    randomKey = objectId;
                }
            });
        }
        executorService.shutdown();
    }

    public static void fetchDataFromRedis() {
        Set<String> allKeysFromRedis = getAllKeysFromRedis();
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            int threadId = i + 1;
            executorService.execute(() -> {
                String randomKey = getRandomKey(allKeysFromRedis);
                for (int j = 0; j < NUM_QUERIES_PER_THREAD; j++) {
                    try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                        String objectId = jedis.hget(randomKey, OBJECT_ID);
                        logger.info("Thread: " + threadId + " Key: " + randomKey + " ObjectKey: " + objectId);
                        randomKey = objectId;
                    }
                }
            });
        }
        executorService.shutdown();
    }
}
