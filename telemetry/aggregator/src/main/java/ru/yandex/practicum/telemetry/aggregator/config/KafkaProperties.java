package ru.yandex.practicum.telemetry.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private String bootstrapServers = "localhost:9092";
    private final Producer producer = new Producer();
    private final Consumer consumer = new Consumer();

    @Getter
    @Setter
    public static class Producer {
        private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String valueSerializer = "ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer";
    }

    @Getter
    @Setter
    public static class Consumer {
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "ru.yandex.practicum.kafka.deserializer.SensorEventDeserializer";
        private String groupId = "sensorEvent.group.id";
        private String clientId = "SensorEventConsumer";
        private String enableAutoCommit = "false";
        private String autoOffsetReset = "earliest";
    }
}
