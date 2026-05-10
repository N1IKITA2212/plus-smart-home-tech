package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.HubEventHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceRemovedHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) event.getPayload();
        String sensorId = payload.getId();

        if (sensorRepository.existsById(sensorId)) {
            scenarioConditionRepository.deleteBySensorId(sensorId);
            scenarioActionRepository.deleteBySensorId(sensorId);

            sensorRepository.deleteById(sensorId);
            log.info("Удалён сенсор {}", sensorId);
        } else {
            log.debug("Попытка удалить несуществующий сенсор {}", sensorId);
        }
    }

    @Override
    public String getType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }
}