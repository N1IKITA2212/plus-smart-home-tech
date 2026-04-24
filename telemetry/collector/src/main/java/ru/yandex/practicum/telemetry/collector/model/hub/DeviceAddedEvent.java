package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    private String id;
    @NotBlank
    private DeviceType deviceType;

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.DEVICE_ADDED;
    }
}
