package org.volante.whitepaper.cache;

import com.google.gson.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.*;
import org.bson.*;
import org.quartz.*;
import org.quartz.impl.*;

import java.time.*;
import java.util.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;
import static org.volante.whitepaper.cache.LoadDataInCaffeine.*;
import static org.volante.whitepaper.cache.LoadDataInRedis.*;

public class StartCDC {
    static final Duration pollingInterval = Duration.ofSeconds(1);
    static Properties consumerProps = getProperties();

    public static void startCDC() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            JobDetail jobDetail = JobBuilder.newJob(CacheData.class).withIdentity(CACHE_DATA_JOB, CACHE_DATA_GROUP).build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(CACHE_DATA_TRIGGER, CACHE_DATA_GROUP).startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(SCHEDULE_TIME).repeatForever()).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties getProperties() {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST + COLON + KAFKA_PORT);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString());
        return consumerProps;
    }

    public static class CacheData implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            ConsumerRecords<String, String> records;
            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
                consumer.subscribe(Collections.singletonList(CACHE_DB + DOT + CORRELATION));
                records = consumer.poll(pollingInterval);
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        String documentString = JsonParser.parseString(record.value()).getAsJsonObject().get(PAYLOAD).getAsString();
                        Document document = Document.parse(documentString);
                        if (document != null) {
                            if (isExternalCache) {
                                addDataToRedis(document);
                            } else {
                                addDataToCaffeine(document);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    } finally {
                        consumer.close();
                    }
                }
            }
        }
    }
}
