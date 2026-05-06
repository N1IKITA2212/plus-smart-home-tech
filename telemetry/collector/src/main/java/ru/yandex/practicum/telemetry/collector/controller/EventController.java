package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.telemetry.collector.model.hub.*;
import ru.yandex.practicum.telemetry.collector.model.sensor.*;
import ru.yandex.practicum.telemetry.collector.service.EventService;

import java.time.Instant;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final EventService eventService;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.debug("Получено gRPC событие датчика: id={}, hubId={}", request.getId(), request.getHubId());
            eventService.collectSensorEvent(toModel(request));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.debug("Получено gRPC событие хаба: hubId={}", request.getHubId());
            eventService.collectHubEvent(toModel(request));
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка обработки события хаба", e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    // ──────────────────── SensorEventProto → domain ────────────────────

    private SensorEvent toModel(SensorEventProto proto) {
        Instant timestamp = Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        );

        SensorEvent event = switch (proto.getPayloadCase()) {
            case MOTION_SENSOR      -> toModel(proto.getMotionSensor());
            case TEMPERATURE_SENSOR -> toModel(proto.getTemperatureSensor());
            case LIGHT_SENSOR       -> toModel(proto.getLightSensor());
            case CLIMATE_SENSOR     -> toModel(proto.getClimateSensor());
            case SWITCH_SENSOR      -> toModel(proto.getSwitchSensor());
            case PAYLOAD_NOT_SET    -> throw new IllegalArgumentException("Payload не задан в SensorEventProto");
        };

        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(timestamp);
        return event;
    }

    private MotionSensorEvent toModel(MotionSensorProto proto) {
        MotionSensorEvent e = new MotionSensorEvent();
        e.setLinkQuality(proto.getLinkQuality());
        e.setMotion(proto.getMotion());
        e.setVoltage(proto.getVoltage());
        return e;
    }

    private TemperatureSensorEvent toModel(TemperatureSensorProto proto) {
        TemperatureSensorEvent e = new TemperatureSensorEvent();
        e.setTemperatureC(proto.getTemperatureC());
        e.setTemperatureF(proto.getTemperatureF());
        return e;
    }

    private LightSensorEvent toModel(LightSensorProto proto) {
        LightSensorEvent e = new LightSensorEvent();
        e.setLinkQuality(proto.getLinkQuality());
        e.setLuminosity(proto.getLuminosity());
        return e;
    }

    private ClimateSensorEvent toModel(ClimateSensorProto proto) {
        ClimateSensorEvent e = new ClimateSensorEvent();
        e.setTemperatureC(proto.getTemperatureC());
        e.setHumidity(proto.getHumidity());
        e.setCo2Level(proto.getCo2Level());
        return e;
    }

    private SwitchSensorEvent toModel(SwitchSensorProto proto) {
        SwitchSensorEvent e = new SwitchSensorEvent();
        e.setState(proto.getState());
        return e;
    }

    // ──────────────────── HubEventProto → domain ────────────────────

    private HubEvent toModel(HubEventProto proto) {
        Instant timestamp = Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        );

        HubEvent event = switch (proto.getPayloadCase()) {
            case DEVICE_ADDED      -> toModel(proto.getDeviceAdded());
            case DEVICE_REMOVED    -> toModel(proto.getDeviceRemoved());
            case SCENARIO_ADDED    -> toModel(proto.getScenarioAdded());
            case SCENARIO_REMOVED  -> toModel(proto.getScenarioRemoved());
            case PAYLOAD_NOT_SET   -> throw new IllegalArgumentException("Payload не задан в HubEventProto");
        };

        event.setHubId(proto.getHubId());
        event.setTimestamp(timestamp);
        return event;
    }

    private DeviceAddedEvent toModel(DeviceAddedEventProto proto) {
        DeviceAddedEvent e = new DeviceAddedEvent();
        e.setId(proto.getId());
        e.setDeviceType(DeviceType.valueOf(proto.getType().name()));
        return e;
    }

    private DeviceRemovedEvent toModel(DeviceRemovedEventProto proto) {
        DeviceRemovedEvent e = new DeviceRemovedEvent();
        e.setId(proto.getId());
        return e;
    }

    private ScenarioAddedEvent toModel(ScenarioAddedEventProto proto) {
        List<ScenarioCondition> conditions = proto.getConditionList().stream()
                .map(this::toModel)
                .toList();
        List<DeviceAction> actions = proto.getActionList().stream()
                .map(this::toModel)
                .toList();

        ScenarioAddedEvent e = new ScenarioAddedEvent();
        e.setName(proto.getName());
        e.setConditions(conditions);
        e.setActions(actions);
        return e;
    }

    private ScenarioCondition toModel(ScenarioConditionProto proto) {
        Integer value = switch (proto.getValueCase()) {
            case BOOL_VALUE -> proto.getBoolValue() ? 1 : 0;
            case INT_VALUE  -> proto.getIntValue();
            case VALUE_NOT_SET -> null;
        };

        ScenarioCondition c = new ScenarioCondition();
        c.setSensorId(proto.getSensorId());
        c.setType(ConditionType.valueOf(proto.getType().name()));
        c.setOperation(ScenarioOperation.valueOf(proto.getOperation().name()));
        c.setValue(value);
        return c;
    }

    private DeviceAction toModel(DeviceActionProto proto) {
        DeviceAction a = new DeviceAction();
        a.setSensorId(proto.getSensorId());
        a.setType(ActionType.valueOf(proto.getType().name()));
        a.setValue(proto.hasValue() ? proto.getValue() : null);
        return a;
    }

    private ScenarioRemovedEvent toModel(ScenarioRemovedEventProto proto) {
        ScenarioRemovedEvent e = new ScenarioRemovedEvent();
        e.setName(proto.getName());
        return e;
    }
}
