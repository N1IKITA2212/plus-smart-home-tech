package ru.yandex.practicum.telemetry.collector.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Component
public class HubEventMapper {

    public DeviceTypeAvro mapDeviceTypeToAvro(DeviceTypeProto deviceType) {
        return switch (deviceType) {
            case DeviceTypeProto.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceTypeProto.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case DeviceTypeProto.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceTypeProto.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceTypeProto.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case UNRECOGNIZED -> null;
        };
    }

    public ActionTypeAvro mapActionTypeToAvro(ActionTypeProto actionType) {
        return switch (actionType) {
            case ActionTypeProto.ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case ActionTypeProto.DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case ActionTypeProto.INVERSE -> ActionTypeAvro.INVERSE;
            case ActionTypeProto.SET_VALUE -> ActionTypeAvro.SET_VALUE;
            case UNRECOGNIZED -> null;
        };
    }

    public ScenarioConditionAvro mapToScenarioConditionAvro(ScenarioConditionProto scenarioCondition) {
        ScenarioConditionAvro.Builder scenarioConditionAvro = ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()));

        switch (scenarioCondition.getValueCase()) {
            case BOOL_VALUE -> scenarioConditionAvro.setValue(scenarioCondition.getBoolValue());
            case INT_VALUE -> scenarioConditionAvro.setValue(scenarioCondition.getIntValue());
            case VALUE_NOT_SET -> scenarioConditionAvro.setValue(null);
        }

        return scenarioConditionAvro.build();
    }

    public DeviceActionAvro mapToDeviceActionAvro(DeviceActionProto deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(mapActionTypeToAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }
}