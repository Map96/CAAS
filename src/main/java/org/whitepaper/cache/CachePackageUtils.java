package org.whitepaper.cache;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

import static org.whitepaper.cache.CachePackageConstants.*;

public class CachePackageUtils {
    public static MongoClientSettings getMongoConnectionSettings() {
        String uri = MONGODB + "://" + MONGODB_HOST + ":" + MONGODB_PORT;
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();
    }
}
