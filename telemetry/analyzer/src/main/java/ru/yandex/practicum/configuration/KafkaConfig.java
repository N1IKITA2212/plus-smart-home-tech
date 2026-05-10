package ru.yandex.practicum.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String server;

    @Value("${spring.kafka.consumer.hub.key-deserializer}")
    private String hubKeyDeserializer;

    @Value("${spring.kafka.consumer.hub.value-deserializer}")
    private String hubValueDeserializer;

    @Value("${spring.kafka.consumer.hub.group-id}")
    private String hubGroupId;

    @Value("${spring.kafka.consumer.hub.client-id}")
    private String hubClientId;

    @Value("${spring.kafka.consumer.hub.enable-auto-commit}")
    private String hubAutoCommit;

    @Value("${spring.kafka.consumer.snapshots.key-deserializer}")
    private String snapKeyDeserializer;

    @Value("${spring.kafka.consumer.snapshots.value-deserializer}")
    private String snapValueDeserializer;

    @Value("${spring.kafka.consumer.snapshots.group-id}")
    private String snapGroupId;

    @Value("${spring.kafka.consumer.snapshots.client-id}")
    private String snapClientId;

    @Value("${spring.kafka.consumer.snapshots.enable-auto-commit}")
    private String snapAutoCommit;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> getSnapshotConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapKeyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapValueDeserializer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, snapGroupId);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, snapClientId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, snapAutoCommit);
        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> getHubEventConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubKeyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubValueDeserializer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroupId);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, hubClientId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, hubAutoCommit);
        return new KafkaConsumer<>(properties);
    }
}