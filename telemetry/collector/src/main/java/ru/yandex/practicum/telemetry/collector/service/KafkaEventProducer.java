package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public void send(String topic, String key, SpecificRecordBase value) {
        kafkaTemplate.send(topic, key, value);
    }
}