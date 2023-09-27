package org.volante.whitepaper.cache;

public class CachePackageConstants {
    public static final String MONGODB_HOST ;
    public static final String REDIS_HOST;
    public static final String KAFKA_HOST ;
    public static final int MONGODB_PORT ;
    public static final int REDIS_PORT ;
    public static final int KAFKA_PORT ;
    public static final int TTL;
    public static final int NUM_THREADS ;
    public static final int NUM_QUERIES_PER_THREAD ;
    public static final boolean isExternalCache ;
    public static final String CACHE_DB = "cacheDb";
    public static final String CHARACTER_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String OBJECT_ID = "objectId";
    public static final String _ID = "_id";
    public static final String RANDOM_INT = "randomInt";
    public static final String RANDOM_DATE = "randomDate";
    public static final String RANDOM_STRING = "randomString";
    public static final String CORRELATION = "Correlation";
    public static final String RECORD_ID = "recordId";
    public static final String MONGODB = "mongodb";
    public static final String DOT = ".";
    public static final String COLON = ":";
    public static final String CONSUMER_GROUP = "my-consumer-group";
    public static final String PAYLOAD = "payload";
    public static final String PUSH_DATA_JOB = "PushDataJob";
    public static final String PUSH_DATA_TRIGGER = "PushDataTrigger";
    public static final String PUSH_DATA_GROUP = "PushDataGroup";
    public static final String CACHE_DATA_JOB = "CacheDataJob";
    public static final String CACHE_DATA_TRIGGER = "CacheDataTrigger";
    public static final String CACHE_DATA_GROUP = "CacheDataGroup";
    public static final int SCHEDULE_TIME ;

    static{
        MONGODB_HOST = System.getenv("MONGODB_HOST");
        REDIS_HOST = System.getenv("REDIS_HOST");
        KAFKA_HOST = System.getenv("KAFKA_HOST");
        MONGODB_PORT = Integer.parseInt(System.getenv("MONGODB_PORT"));
        REDIS_PORT = Integer.parseInt(System.getenv("REDIS_PORT"));
        KAFKA_PORT = Integer.parseInt(System.getenv("KAFKA_PORT"));
        NUM_THREADS = Integer.parseInt(System.getenv("NUM_THREADS"));
        NUM_QUERIES_PER_THREAD = Integer.parseInt(System.getenv("NUM_QUERIES_PER_THREAD"));
        isExternalCache = Boolean.parseBoolean(System.getenv("true"));
        SCHEDULE_TIME = Integer.parseInt(System.getenv("SCHEDULE_TIME"));
        TTL = Integer.parseInt(System.getenv("TTL"));
    }
}
