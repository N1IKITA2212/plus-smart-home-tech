package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

public interface SensorEventHandler {

    void handle(SensorEvent event);

    void handle(SensorEventProto eventProto);

    SensorEventType getMessageType();

    SensorEventProto.PayloadCase getMessageProtoType();
}