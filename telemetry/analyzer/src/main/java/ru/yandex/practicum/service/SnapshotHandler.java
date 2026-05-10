package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface SnapshotHandler {

    void handle(SensorsSnapshotAvro snapshotAvro);
}