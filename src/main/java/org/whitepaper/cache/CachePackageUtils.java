package org.whitepaper.cache;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

import static org.whitepaper.cache.CachePackageConstants.MONGODB;
import static org.whitepaper.cache.CachePackageConstants.MONGODB_PORT;
import static org.whitepaper.cache.CachePackageConstants.MONGODB_HOST;

public class CachePackageUtils {
    public static MongoClientSettings getMongoConnectionSettings() {
        String uri = MONGODB + "://" + MONGODB_HOST + ":" + MONGODB_PORT;
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();
    }
}
