package org.volante.whitepaper.cache;

import org.apache.logging.log4j.*;
import redis.clients.jedis.*;

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
                    String referenceKey = getRandomKey(allKeysFromCaffeine);
                    logger.info("Thread: " + threadId + " Start Key: " + referenceKey);
                    for (int j = 0; j < NUM_QUERIES_PER_THREAD; j++) {
                        long startTime = System.currentTimeMillis();
                        Map<String, String> document = caffeineCache.getIfPresent(referenceKey);
                        if (document == null) {
                            Set<String> allKeysFromCaffeine1 = getAllKeysFromCaffeine();
                            referenceKey = getRandomKey(allKeysFromCaffeine1);
                            logger.info("Thread: " + threadId + " Start Key: " + referenceKey);
                            continue;
                        }
                        long endTime = System.currentTimeMillis();
                        long latency = endTime - startTime;
                        logger.info("Thread: " + threadId + " Key: " + referenceKey + " Caffeine:Latency: " + latency);
                        String objectId = document.get(OBJECT_ID);
                        logger.info("Thread: " + threadId + " Key: " + referenceKey + " ObjectKey: " + objectId);
                        referenceKey = objectId;
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
                    String referenceKey = getRandomKey(allKeysFromRedis);
                    logger.info("Thread: " + threadId + " Start Key: " + referenceKey);
                    for (int j = 0; j < NUM_QUERIES_PER_THREAD; j++) {
                        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                            long startTime = System.currentTimeMillis();
                            if (!jedis.exists(referenceKey)) {
                                Set<String> allKeysFromRedis1 = getAllKeysFromRedis();
                                referenceKey = getRandomKey(allKeysFromRedis1);
                                logger.info("Thread: " + threadId + " Start Key: " + referenceKey);
                                continue;
                            }
                            String objectId = jedis.hget(referenceKey, OBJECT_ID);
                            long endTime = System.currentTimeMillis();
                            long latency = endTime - startTime;
                            logger.info("Thread: " + threadId + " Key: " + referenceKey + " Redis:Latency: " + latency);
                            logger.info("Thread: " + threadId + " Key: " + referenceKey + " ObjectKey: " + objectId);
                            referenceKey = objectId;
                        }
                    }
                }
            });
        }
        executorService.shutdown();
        System.out.println("Process Finished");
    }
}
