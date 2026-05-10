package ru.yandex.practicum.configuration;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.HubEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Getter
public class HubEventHandlerFactory {
    private final Map<String, HubEventHandler> handlerMap;

    public HubEventHandlerFactory(Set<HubEventHandler> hubSet) {
        this.handlerMap = hubSet.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        Function.identity()));
    }
}