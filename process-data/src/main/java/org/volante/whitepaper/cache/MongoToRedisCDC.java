package org.volante.whitepaper.cache;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.*;

import javax.servlet.*;
import java.time.*;
import java.util.*;

import static org.volante.whitepaper.cache.CachePackageConstants.*;

public class MongoToRedisCDC implements ServletContextListener {
    public static void startCDC() {
        Properties consumerProps = getProperties();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singletonList(MONGODB + DOT + CACHE_DB + DOT + CORRELATION));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Key: " + record.key() + ", Value: " + record.value());
                }
            }
        }
    }

    private static Properties getProperties() {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return consumerProps;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        startCDC();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
