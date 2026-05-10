package ru.yandex.practicum.service.snapshot.executor;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GrpcExecutor {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void execute(String hubId, Scenario scenario) {
        Instant now = Instant.now();

        for (ScenarioAction scenarioAction : scenario.getActions()) {
            Action action = scenarioAction.getAction();
            String sensorId = scenarioAction.getSensor().getId();
            int value = Optional.ofNullable(action.getValue()).orElse(0);


            DeviceActionProto grpcAction = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(ActionTypeProto.valueOf(action.getType()))
                    .setValue(value)
                    .build();

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(grpcAction)
                    .setTimestamp(toTimestamp(now))
                    .build();
            try {
                hubRouterClient.handleDeviceAction(request);
                log.info("grpc выполнил: {}, {}, {}", action.getType(), sensorId, hubId);
            } catch (StatusRuntimeException e) {
                log.error("ошибка grpc: {}", e.getStatus(), e);
            }
        }
    }

    private static Timestamp toTimestamp(Instant i) {
        return Timestamp.newBuilder().setSeconds(i.getEpochSecond()).setNanos(i.getNano()).build();
    }
}