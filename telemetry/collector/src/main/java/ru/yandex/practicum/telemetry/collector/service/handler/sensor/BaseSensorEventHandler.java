package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;

import java.time.Instant;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    @Value("${kafka.topic.telemetry.sensors-topic}")
    private String topic;

    protected final KafkaEventProducer eventProducer;

    public BaseSensorEventHandler(KafkaEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    protected abstract T mapToAvro(SensorEvent event);

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public void handle(SensorEvent event) {

        if (event == null)
            throw new IllegalArgumentException("null event");

        T sensorEventPayload = mapToAvro(event);
        eventProducer.send(topic, event.getHubId(), SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(sensorEventPayload)
                .build());
    }

    @Override
    public void handle(SensorEventProto eventProto) {

        if (eventProto == null)
            throw new IllegalArgumentException("null event");

        T sensorEventPayload = mapToAvro(eventProto);
        Instant timestamp = Instant.ofEpochSecond(eventProto.getTimestamp().getSeconds(),
                eventProto.getTimestamp().getNanos());

        eventProducer.send(topic, eventProto.getHubId(), SensorEventAvro.newBuilder()
                .setId(eventProto.getId())
                .setHubId(eventProto.getHubId())
                .setTimestamp(timestamp)
                .setPayload(sensorEventPayload)
                .build());
    }
}