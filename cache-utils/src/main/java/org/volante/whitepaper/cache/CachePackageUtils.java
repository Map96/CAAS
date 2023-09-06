package org.volante.whitepaper.cache;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

public class CachePackageUtils {
    public static MongoClientSettings getMongoConnectionSettings() {
        String uri = CachePackageConstants.MONGODB + "://" + CachePackageConstants.MONGODB_HOST + ":" + CachePackageConstants.MONGODB_PORT;
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();
    }
}
