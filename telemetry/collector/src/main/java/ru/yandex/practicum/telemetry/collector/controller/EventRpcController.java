package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class EventRpcController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlerMap;
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlerMap;

    public EventRpcController(Set<HubEventHandler> hubHandlers, Set<SensorEventHandler> sensorHandlers) {
        this.hubEventHandlerMap = hubHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.sensorEventHandlerMap = sensorHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> response) {
        try {
            SensorEventHandler handler = sensorEventHandlerMap.get(request.getPayloadCase());

            if (handler == null)
                throw new IllegalArgumentException("Обработчик для события " + request.getPayloadCase() + "не найден.");

            handler.handle(request);

            response.onNext(Empty.getDefaultInstance());
            response.onCompleted();
        } catch (Exception ex) {
            response.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(ex.getLocalizedMessage())
                            .withCause(ex)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> response) {
        try {
            HubEventHandler handler = hubEventHandlerMap.get(request.getPayloadCase());

            if (handler == null)
                throw new IllegalArgumentException("Обработчик для события " + request.getPayloadCase() + "не найден.");

            handler.handle(request);

            response.onNext(Empty.getDefaultInstance());
            response.onCompleted();
        } catch (Exception ex) {
            response.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(ex.getLocalizedMessage())
                            .withCause(ex)
            ));
        }
    }
}