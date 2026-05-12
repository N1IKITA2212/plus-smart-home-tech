package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;

import java.time.Instant;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    private final String topic;
    protected final KafkaEventProducer eventProducer;

    public BaseSensorEventHandler(@Value("${kafka.topic.telemetry.sensors-topic}") String topic,
                                  KafkaEventProducer eventProducer) {
        this.topic = topic;
        this.eventProducer = eventProducer;
    }

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public void handle(SensorEventProto eventProto) {

        if (eventProto == null)
            throw new IllegalArgumentException("null event");

        T sensorEventPayload = mapToAvro(eventProto);
        Instant timestamp = Instant.ofEpochSecond(eventProto.getTimestamp().getSeconds(),
                eventProto.getTimestamp().getNanos());

        eventProducer.send(topic, eventProto.getHubId(), timestamp.toEpochMilli(), sensorEventPayload);
    }
}