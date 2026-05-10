package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.configuration.HubEventHandlerFactory;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.service.HubEventHandler;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> consumer;

    private final HubEventHandlerFactory handlerFactory;

    @Value("${analyzer.topic.hub-event-topic}")
    private String hubEventsTopic;

    @Override
    public void run() {
        log.info("Запуск HubEventProcessor. Подписка на топик: {}", hubEventsTopic);
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        Map<String, HubEventHandler> handlerMap = handlerFactory.getHandlerMap();

        try {
            consumer.subscribe(List.of(hubEventsTopic));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    String payloadName = record.value().getPayload().getClass().getSimpleName();
                    if (handlerMap.containsKey(payloadName)) {
                        handlerMap.get(payloadName).handle(record.value());
                    } else {
                        throw new IllegalArgumentException("Не могу найти обработчик события " + record.value());
                    }
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception ex) {
            log.error("Ошибка во время обработки событий от датчиков", ex);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}