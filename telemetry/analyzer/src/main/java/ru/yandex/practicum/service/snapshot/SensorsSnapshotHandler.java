package ru.yandex.practicum.service.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.SnapshotHandler;
import ru.yandex.practicum.service.snapshot.evaluator.ConditionsEvaluator;
import ru.yandex.practicum.service.snapshot.executor.GrpcExecutor;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SensorsSnapshotHandler implements SnapshotHandler {

    private final ScenarioRepository scenarioRepository;
    private final GrpcExecutor executor;
    private final ConditionsEvaluator evaluator;

    @Override
    @Transactional
    public void handle(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        scenarios.stream()
                .filter(this::hasConditions)
                .forEach(s -> {
                    boolean conditionsMet = s.getConditions().stream()
                            .allMatch(sc -> evaluator.evaluate(sc, snapshot));

                    if (conditionsMet) {
                        executor.execute(hubId, s);
                    }
                });
    }

    private boolean hasConditions(Scenario s) {
        return s.getConditions() != null && !s.getConditions().isEmpty();
    }
}