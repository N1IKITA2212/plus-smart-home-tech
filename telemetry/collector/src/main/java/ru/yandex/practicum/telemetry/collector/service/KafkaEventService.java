package ru.yandex.practicum.telemetry.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.hub.*;
import ru.yandex.practicum.telemetry.collector.model.sensor.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventService implements EventService {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    @Value("${kafka.topics.sensors}")
    private String sensorsTopic;

    @Value("${kafka.topics.hubs}")
    private String hubsTopic;

    // ─────────────────────────────── Sensor events ───────────────────────────────

    @Override
    public void collectSensorEvent(SensorEvent event) {
        SensorEventAvro avro = toAvro(event);
        send(sensorsTopic, avro.getHubId(), avro.getTimestamp(), avro);
        log.debug("Событие датчика отправлено в топик {}: {}", sensorsTopic, avro);
    }

    private SensorEventAvro toAvro(SensorEvent event) {
        Object payload = switch (event.getSensorEventType()) {
            case CLIMATE_SENSOR_EVENT -> toAvro((ClimateSensorEvent) event);
            case LIGHT_SENSOR_EVENT -> toAvro((LightSensorEvent) event);
            case MOTION_SENSOR_EVENT -> toAvro((MotionSensorEvent) event);
            case SWITCH_SENSOR_EVENT -> toAvro((SwitchSensorEvent) event);
            case TEMPERATURE_SENSOR_EVENT -> toAvro((TemperatureSensorEvent) event);
        };

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setPayload(payload)
                .build();
    }

    private ClimateSensorEventAvro toAvro(ClimateSensorEvent e) {
        return ClimateSensorEventAvro.newBuilder()
                .setTemperatureC(e.getTemperatureC())
                .setHumidity(e.getHumidity())
                .setCo2Level(e.getCo2Level())
                .build();
    }

    private LightSensorEventAvro toAvro(LightSensorEvent e) {
        return LightSensorEventAvro.newBuilder()
                .setLinkQuality(e.getLinkQuality())
                .setLuminosity(e.getLuminosity())
                .build();
    }

    private MotionSensorEventAvro toAvro(MotionSensorEvent e) {
        return MotionSensorEventAvro.newBuilder()
                .setLinkQuality(e.getLinkQuality())
                .setMotion(e.isMotion())
                .setVoltage(e.getVoltage())
                .build();
    }

    private SwitchSensorEventAvro toAvro(SwitchSensorEvent e) {
        return SwitchSensorEventAvro.newBuilder()
                .setState(e.isState())
                .build();
    }

    private TemperatureSensorEventAvro toAvro(TemperatureSensorEvent e) {
        return TemperatureSensorEventAvro.newBuilder()
                .setTemperatureC(e.getTemperatureC())
                .setTemperatureF(e.getTemperatureF())
                .build();
    }

    @Override
    public void collectHubEvent(HubEvent event) {
        HubEventAvro avro = toAvro(event);
        send(hubsTopic, avro.getHubId(), avro.getTimestamp(), avro);
        log.debug("Событие хаба отправлено в топик {}: {}", hubsTopic, avro);
    }

    private HubEventAvro toAvro(HubEvent event) {
        Object payload = switch (event.getHubEventType()) {
            case DEVICE_ADDED -> toAvro((DeviceAddedEvent) event);
            case DEVICE_REMOVED -> toAvro((DeviceRemovedEvent) event);
            case SCENARIO_ADDED -> toAvro((ScenarioAddedEvent) event);
            case SCENARIO_REMOVED -> toAvro((ScenarioRemovedEvent) event);
        };

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setPayload(payload)
                .build();
    }

    private DeviceAddedEventAvro toAvro(DeviceAddedEvent e) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(e.getId())
                .setDeviceType(DeviceTypeAvro.valueOf(e.getDeviceType().name()))
                .build();
    }

    private DeviceRemovedEventAvro toAvro(DeviceRemovedEvent e) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(e.getId())
                .build();
    }

    private ScenarioAddedEventAvro toAvro(ScenarioAddedEvent e) {
        List<ScenarioConditionAvro> conditions = e.getConditions().stream()
                .map(c -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(c.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
                        .setValue(c.getValue())
                        .build())
                .toList();

        List<DeviceActionAvro> actions = e.getActions().stream()
                .map(a -> DeviceActionAvro.newBuilder()
                        .setSensorId(a.getSensorId())
                        .setType(ActionTypeAvro.valueOf(a.getType().name()))
                        .setValue(a.getValue())
                        .build())
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(e.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    private ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent e) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(e.getName())
                .build();
    }

    private void send(String topic, String hubId, long timestamp, SpecificRecordBase event) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timestamp,
                hubId,
                event
        );
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.warn("Не удалось записать событие {} в топик {}",
                        event.getClass().getSimpleName(), topic, exception);
            } else {
                log.info("Событие {} было успешно сохранёно в топик {} в партицию {} со смещением {}",
                        event.getClass().getSimpleName(), metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
