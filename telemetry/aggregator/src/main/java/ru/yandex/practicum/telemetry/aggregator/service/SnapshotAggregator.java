package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SnapshotAggregator {

    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        if (!snapshots.containsKey(event.getHubId())) {
            Map<String, SensorStateAvro> stateAvroMap = new HashMap<>();

            stateAvroMap.put(event.getId(), SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build());

            SensorsSnapshotAvro snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(stateAvroMap)
                    .build();

            snapshots.put(snapshot.getHubId(), snapshot);
            return Optional.of(snapshot);
        }
        SensorsSnapshotAvro existingSnapshot = snapshots.get(event.getHubId());

        String sensorId = event.getId();

        if (existingSnapshot.getSensorsState().containsKey(sensorId)) {
            if (existingSnapshot.getSensorsState().get(sensorId).getTimestamp().isAfter(event.getTimestamp())) {
                log.info("skip old event");
                return Optional.empty();
            }
            if (existingSnapshot.getSensorsState().get(sensorId).getData().equals(event.getPayload())) {
                log.info("skip event with same payload");
                return Optional.empty();
            }
        }

        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        existingSnapshot.getSensorsState().put(sensorId, sensorStateAvro);
        existingSnapshot.setTimestamp(event.getTimestamp());

        return Optional.of(existingSnapshot);

    }
}