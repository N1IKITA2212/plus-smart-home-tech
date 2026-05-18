package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public void send(String topic, String hubId, long timestamp, SpecificRecordBase event) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timestamp,
                hubId,
                event
        );
        try {
            SendResult<String, SpecificRecordBase> result = kafkaTemplate.send(record).get();
            RecordMetadata metadata = result.getRecordMetadata();
            log.info("Событие {} было успешно сохранёно в топик {} в партицию {} со смещением {}",
                    event.getClass().getSimpleName(), metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Не удалось записать событие {} в топик {} ", event.getClass().getSimpleName(), topic, e);
        }
    }
}