package ru.yandex.practicum.service.snapshot.evaluator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.ScenarioCondition;

import java.util.Map;

@Component
public class ConditionsEvaluator {

    private static final Map<Class<?>, ValueExtractor> EXTRACTORS = Map.of(
            TemperatureSensorAvro.class, d -> ((TemperatureSensorAvro)d).getTemperatureC(),
            ClimateSensorAvro.class, d -> ((ClimateSensorAvro)d).getTemperatureC(),
            LightSensorAvro.class, d -> ((LightSensorAvro)d).getLuminosity(),
            MotionSensorAvro.class,       d -> ((MotionSensorAvro)d).getMotion() ? 1 : 0,
            SwitchSensorAvro.class,       d -> ((SwitchSensorAvro)d).getState() ? 1 : 0
    );

    public boolean evaluate(ScenarioCondition sc, SensorsSnapshotAvro snapshot) {
        String sensorId = sc.getSensor().getId();
        SensorStateAvro state = snapshot.getSensorsState().get(sensorId);
        if (state == null) {
            return false;
        }

        Object data = state.getData();
        if (data == null) return false;

        ValueExtractor extractor = EXTRACTORS.get(data.getClass());
        if (extractor == null) {
            return false;
        }

        Integer actual = extractor.extract(data);
        Integer expected = sc.getCondition().getValue();
        if (expected == null || actual == null) return false;

        ComparisonOp op = ComparisonOp.from(sc.getCondition().getOperation());
        if (op == null) return false;

        return compare(actual, expected, op);
    }

    private static boolean compare(int actual, int expected, ComparisonOp op) {
        return switch (op) {
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
            case EQUALS -> actual == expected;
        };
    }
}