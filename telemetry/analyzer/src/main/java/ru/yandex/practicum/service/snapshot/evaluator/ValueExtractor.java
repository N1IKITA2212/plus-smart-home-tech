package ru.yandex.practicum.service.snapshot.evaluator;

@FunctionalInterface
public interface ValueExtractor {

    Integer extract(Object sensorData);
}