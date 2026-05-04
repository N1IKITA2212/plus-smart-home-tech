package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.EventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@RequestBody @Valid SensorEvent sensorEvent) {
        eventService.collectSensorEvent(sensorEvent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@RequestBody @Valid HubEvent hubEvent) {
        eventService.collectHubEvent(hubEvent);
        return ResponseEntity.ok().build();
    }
}
