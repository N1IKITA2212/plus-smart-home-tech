package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.interaction.feign.DeliveryClient;
import ru.yandex.practicum.interaction.feign.PaymentClient;
import ru.yandex.practicum.interaction.feign.WarehouseClient;
import ru.yandex.practicum.order.exception.NoOrderFoundException;
import ru.yandex.practicum.order.exception.NotAuthorizedUserException;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username must not be empty");
        }
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        ShoppingCartDto cart = request.getShoppingCart();

        Order order = Order.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(new HashMap<>(cart.getProducts()))
                .state(OrderState.NEW)
                .build();
        order = repository.save(order);

        BookedProductsDto booked = warehouseClient.checkProductQuantityEnoughForShoppingCart(cart);
        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.isFragile());

        AddressDto from = warehouseClient.getWarehouseAddress();
        DeliveryDto delivery = deliveryClient.planDelivery(DeliveryDto.builder()
                .fromAddress(from)
                .toAddress(request.getDeliveryAddress())
                .orderId(order.getOrderId())
                .deliveryState(DeliveryState.CREATED)
                .build());
        order.setDeliveryId(delivery.getDeliveryId());

        order.setProductPrice(paymentClient.productCost(toDto(order)));

        return toDto(repository.save(order));
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = findById(request.getOrderId());
        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return toDto(repository.save(order));
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = findById(orderId);
        if (order.getPaymentId() == null) {
            PaymentDto payment = paymentClient.payment(toDto(order));
            order.setPaymentId(payment.getPaymentId());
            order.setTotalPrice(payment.getTotalPayment());
            order.setState(OrderState.ON_PAYMENT);
        } else {
            order.setState(OrderState.PAID);
        }
        return toDto(repository.save(order));
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        return updateState(orderId, OrderState.PAYMENT_FAILED);
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        return updateState(orderId, OrderState.DELIVERED);
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        return updateState(orderId, OrderState.DELIVERY_FAILED);
    }

    @Transactional
    public OrderDto complete(UUID orderId) {
        return updateState(orderId, OrderState.COMPLETED);
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findById(orderId);
        order.setTotalPrice(paymentClient.getTotalCost(toDto(order)));
        return toDto(repository.save(order));
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findById(orderId);
        order.setDeliveryPrice(deliveryClient.deliveryCost(toDto(order)));
        return toDto(repository.save(order));
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = findById(orderId);
        BookedProductsDto booked = warehouseClient.assemblyProductsForOrder(
                AssemblyProductsForOrderRequest.builder()
                        .orderId(orderId)
                        .products(order.getProducts())
                        .build());
        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.isFragile());
        order.setState(OrderState.ASSEMBLED);
        return toDto(repository.save(order));
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        return updateState(orderId, OrderState.ASSEMBLY_FAILED);
    }

    private OrderDto updateState(UUID orderId, OrderState state) {
        Order order = findById(orderId);
        order.setState(state);
        return toDto(repository.save(order));
    }

    private Order findById(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found: " + orderId));
    }

    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .shoppingCartId(order.getShoppingCartId())
                .products(order.getProducts())
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .state(order.getState())
                .deliveryWeight(order.getDeliveryWeight())
                .deliveryVolume(order.getDeliveryVolume())
                .fragile(order.getFragile())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .build();
    }
}
