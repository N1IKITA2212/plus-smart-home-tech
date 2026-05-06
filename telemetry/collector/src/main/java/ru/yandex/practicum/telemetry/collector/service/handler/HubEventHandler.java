package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;

public interface HubEventHandler {

    void handle(HubEvent event);

    void handle(HubEventProto eventProto);

    HubEventType getMessageType();

    HubEventProto.PayloadCase getMessageProtoType();
}