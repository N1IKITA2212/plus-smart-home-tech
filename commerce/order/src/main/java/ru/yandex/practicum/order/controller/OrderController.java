package ru.yandex.practicum.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.OrderApi;
import ru.yandex.practicum.interaction.dto.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.ProductReturnRequest;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService service;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        return service.getClientOrders(username);
    }

    @Override
    public OrderDto createNewOrder(String username, CreateNewOrderRequest request) {
        return service.createNewOrder(username, request);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        return service.productReturn(request);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        return service.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return service.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        return service.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return service.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        return service.complete(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        return service.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        return service.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        return service.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        return service.assemblyFailed(orderId);
    }
}
