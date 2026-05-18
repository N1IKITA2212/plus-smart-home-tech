package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.HubEventHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioAddedHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final SensorRepository sensorRepository;
    private final ActionRepository actionRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();
        String hubId = event.getHubId();
        String name = payload.getName();

        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, name)
                .orElseGet(() -> Scenario.builder()
                        .hubId(hubId)
                        .name(name)
                        .build());

        scenarioRepository.save(scenario);

        scenario.getConditions().clear();
        scenario.getActions().clear();

        payload.getConditions().forEach(condition -> handleCondition(condition, scenario, hubId));
        payload.getActions().forEach(action -> handleAction(action, scenario, hubId));

        scenarioRepository.save(scenario);

        log.info("добавлен сценарий: {}", scenario);
    }

    private void handleCondition(ScenarioConditionAvro conditionAvro, Scenario scenario, String hubId) {

        sensorRepository.findByIdAndHubId(conditionAvro.getSensorId(), hubId).ifPresent(sensor -> {
            Integer value = mapValueToInteger(conditionAvro.getValue());
            Condition condition = Condition.builder()
                    .type(conditionAvro.getType().name())
                    .operation(conditionAvro.getOperation().name())
                    .value(value)
                    .build();
            condition = conditionRepository.save(condition);

            ScenarioCondition sc = ScenarioCondition.builder()
                    .id(new ScenarioConditionId(scenario.getId(), sensor.getId(), condition.getId()))
                    .scenario(scenario)
                    .sensor(sensor)
                    .condition(condition)
                    .build();
            scenario.getConditions().add(sc);
        });
    }

    private void handleAction(DeviceActionAvro actionAvro, Scenario scenario, String hubId) {

        Action action = actionRepository.save(Action.builder()
                .type(actionAvro.getType().name())
                .value(actionAvro.getValue())
                .build());

        sensorRepository.findByIdAndHubId(actionAvro.getSensorId(), hubId).ifPresent(sensor -> {
            ScenarioAction sa = ScenarioAction.builder()
                    .id(new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId()))
                    .scenario(scenario)
                    .sensor(sensor)
                    .action(action)
                    .build();
            scenario.getActions().add(sa);
        });
    }

    private Integer mapValueToInteger(Object value) {
        if (value instanceof Integer i) return i;
        if (value instanceof Boolean b) return b ? 1 : 0;
        return null;
    }

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }
}