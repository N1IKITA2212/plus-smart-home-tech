package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {

    void handle(SensorEventProto eventProto);

    SensorEventProto.PayloadCase getMessageType();
}