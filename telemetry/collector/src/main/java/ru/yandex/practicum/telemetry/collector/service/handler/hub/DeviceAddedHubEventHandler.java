package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.utils.HubEventMapper;

@Component(value = "DEVICE_ADDED")
public class DeviceAddedHubEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedHubEventHandler(KafkaEventProducer producer, HubEventMapper mapper) {
        super(producer, mapper);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventProto eventProto) {
        DeviceAddedEventProto event = eventProto.getDeviceAdded();
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(mapper.mapDeviceTypeToAvro(event.getType()))
                .build();
    }
}