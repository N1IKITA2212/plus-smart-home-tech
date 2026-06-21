package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction.api.DeliveryApi;
import ru.yandex.practicum.interaction.dto.DeliveryDto;
import ru.yandex.practicum.interaction.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliveryService service;

    @Override
    public DeliveryDto planDelivery(DeliveryDto delivery) {
        return service.planDelivery(delivery);
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        service.deliverySuccessful(orderId);
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        service.deliveryPicked(orderId);
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        service.deliveryFailed(orderId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto order) {
        return service.deliveryCost(order);
    }
}
