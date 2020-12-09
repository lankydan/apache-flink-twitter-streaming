/*
package dev.lankydan.flink.kafka.consumer;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public static FlinkKafkaConsumer<String> createStringConsumerForTopic(
        String topic,
        String kafkaAddress,
        String kafkaGroup
    ) {

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafkaAddress);
        props.setProperty("group.id", kafkaGroup);

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), props);
        logger.info("CREATED CONSUMER");
        return consumer;
    }
}
*/
