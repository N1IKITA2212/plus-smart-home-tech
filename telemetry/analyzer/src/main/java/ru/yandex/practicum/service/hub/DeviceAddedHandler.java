package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.HubEventHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceAddedHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();
        String sensorId = payload.getId();
        String hubId = event.getHubId();
        try {
            Sensor sensor = Sensor.builder()
                    .id(sensorId)
                    .hubId(hubId)
                    .build();
            sensorRepository.save(sensor);
            log.info("Добавлен новый сенсор {} в хаб {}", sensorId, hubId);
        } catch (DataIntegrityViolationException ex) {
            log.debug("Сенсор {} уже существует в хабе {} (конкурентная вставка)", sensorId, hubId);
        } catch (Exception ex) {
            log.error("Ошибка при добавлении сенсора {} в хаб {}: {}", sensorId, hubId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }
}