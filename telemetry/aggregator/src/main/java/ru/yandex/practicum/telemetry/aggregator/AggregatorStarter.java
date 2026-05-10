package ru.yandex.practicum.telemetry.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregator;

import java.time.Duration;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregatorStarter {
    @Value("${topic.telemetry.sensors-topic}")
    private String sensorTopic;

    @Value("${aggregator.topic.telemetry-snapshots}")
    private String snapshotTopic;

    private final Producer<String, SpecificRecordBase> producer;
    private final Consumer<String, SpecificRecordBase> consumer;
    private final SnapshotAggregator aggregator;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(sensorTopic));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro sensorEventAvro = (SensorEventAvro) record.value();
                    Optional<SensorsSnapshotAvro> snapshotAvro = aggregator.updateState(sensorEventAvro);

                    snapshotAvro.ifPresent(sensorSnapshotAvro -> {
                        ProducerRecord<String, SpecificRecordBase> pr = new ProducerRecord<>(snapshotTopic,
                                sensorEventAvro.getHubId(), sensorSnapshotAvro);
                        producer.send(pr);
                        log.info("sent: {}", pr);
                    });
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception ex) {
            log.error("Ошибка во время обработки событий от датчиков", ex);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                consumer.close();
                producer.close();
            }
        }
    }
}