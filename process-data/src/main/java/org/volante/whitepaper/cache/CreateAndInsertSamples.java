package org.volante.whitepaper.cache;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.*;
import org.quartz.*;
import org.quartz.impl.*;

import java.util.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;
import static org.volante.whitepaper.cache.CachePackageUtils.*;


public class CreateAndInsertSamples {

    static String objectId = null, _id;
    static int recordId = 1;
    static int PAYLOAD_SIZE = 10240;

    static {
        try (MongoClient mongoClient = MongoClients.create(getMongoConnectionSettings())) {
            MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
            MongoCollection<Document> collection = database.getCollection(CORRELATION);
            createIndex(collection);
        }
    }

    public static void pumpDataToDb() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            JobDetail jobDetail = JobBuilder.newJob(PushData.class).withIdentity(PUSH_DATA_JOB, PUSH_DATA_GROUP).build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(PUSH_DATA_TRIGGER, PUSH_DATA_GROUP).startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(SCHEDULE_TIME).repeatForever()).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createIndex(MongoCollection<Document> collection) {
        Document index1 = new Document(_ID, 1);
        IndexOptions options1 = new IndexOptions();
        collection.createIndex(index1, options1);
        Document index2 = new Document(RECORD_ID, 1);
        IndexOptions options2 = new IndexOptions().unique(true);
        collection.createIndex(index2, options2);
    }

    private static String generateRandomString() {
        String characters = CHARACTER_SET;
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    public static String generateRandomPayload(int sizeInBytes) {
        byte[] payload = new byte[sizeInBytes];
        new Random().nextBytes(payload);
        return Base64.getEncoder().encodeToString(payload);
    }

    public static class PushData implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            try (MongoClient mongoClient = MongoClients.create(getMongoConnectionSettings())) {
                MongoDatabase database = mongoClient.getDatabase(CACHE_DB);
                MongoCollection<Document> collection = database.getCollection(CORRELATION);
                _id = objectId == null ? UUID.randomUUID().toString() : objectId;
                objectId = UUID.randomUUID().toString();
                Document document = new Document().append(_ID, _id).append(RANDOM_STRING, generateRandomString()).append(RECORD_ID, recordId++).append(RANDOM_DATE, new Date()).append(RANDOM_INT, new Random().nextInt(1000)).append(OBJECT_ID, objectId).append(PAYLOAD, generateRandomPayload(PAYLOAD_SIZE));
                collection.insertOne(document);
            }
        }
    }
}
