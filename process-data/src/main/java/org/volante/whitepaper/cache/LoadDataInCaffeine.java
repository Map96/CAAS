package org.volante.whitepaper.cache;

import com.github.benmanes.caffeine.cache.*;
import org.bson.*;

import java.time.*;
import java.util.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;

public class LoadDataInCaffeine {
    static Cache<String, Map<String, String>> caffeineCache;

    static {
        caffeineCache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(TTL)).build();
    }

    public static void addDataToCaffeine(Document document) {
        System.out.println("Inserting Record in Caffeine: " + document.get(RECORD_ID).toString());
        HashMap<String, String> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put(OBJECT_ID, document.get(OBJECT_ID).toString());
        stringObjectHashMap.put(PAYLOAD, document.get(PAYLOAD).toString());
        caffeineCache.put(document.get(_ID).toString(), stringObjectHashMap);
    }
}
