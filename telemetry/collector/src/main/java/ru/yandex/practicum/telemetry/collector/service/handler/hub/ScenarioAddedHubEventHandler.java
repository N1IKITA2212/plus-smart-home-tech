package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.model.hub.*;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.utils.HubEventMapper;

import java.util.List;

@Component(value = "SCENARIO_ADDED")
public class ScenarioAddedHubEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public HubEventProto.PayloadCase getMessageProtoType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedHubEvent _event = (ScenarioAddedHubEvent) event;

        List<ScenarioConditionAvro> conditionAvroList = _event.getConditions().stream()
                .map(HubEventMapper::mapToScenarioConditionAvro)
                .toList();

        List<DeviceActionAvro> deviceActionAvroList = _event.getActions().stream()
                .map(HubEventMapper::mapToDeviceActionAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setActions(deviceActionAvroList)
                .setConditions(conditionAvroList)
                .build();
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto eventProto) {
        ScenarioAddedEventProto _event = eventProto.getScenarioAdded();

        List<ScenarioConditionAvro> conditionAvroList = _event.getConditionList().stream()
                .map(HubEventMapper::mapToScenarioConditionAvro)
                .toList();

        List<DeviceActionAvro> deviceActionAvroList = _event.getActionList().stream()
                .map(HubEventMapper::mapToDeviceActionAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setActions(deviceActionAvroList)
                .setConditions(conditionAvroList)
                .build();
    }
}