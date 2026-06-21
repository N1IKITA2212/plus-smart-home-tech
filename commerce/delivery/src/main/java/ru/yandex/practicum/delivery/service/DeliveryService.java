package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.config.DeliveryCostProperties;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.dto.DeliveryDto;
import ru.yandex.practicum.interaction.dto.DeliveryState;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.interaction.feign.OrderClient;
import ru.yandex.practicum.interaction.feign.WarehouseClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final String ADDRESS_2 = "ADDRESS_2";

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final DeliveryCostProperties costProperties;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        Delivery delivery = mapper.toEntity(dto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return mapper.toDto(repository.save(delivery));
    }

    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = findByOrder(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        repository.save(delivery);
        warehouseClient.shippedToDelivery(ShippedToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());
    }

    @Transactional
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = findByOrder(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery);
        orderClient.delivery(orderId);
    }

    @Transactional
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = findByOrder(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        repository.save(delivery);
        orderClient.deliveryFailed(orderId);
    }

    @Transactional(readOnly = true)
    public BigDecimal deliveryCost(OrderDto order) {
        Delivery delivery = findByOrder(order.getOrderId());
        Address from = delivery.getFromAddress();
        Address to = delivery.getToAddress();

        double cost = costProperties.getBaseRate();
        double warehouseMultiplier = ADDRESS_2.equals(from.getCountry())
                ? costProperties.getAddress2Multiplier()
                : costProperties.getAddress1Multiplier();
        cost = cost * warehouseMultiplier + costProperties.getBaseRate();

        if (Boolean.TRUE.equals(order.getFragile())) {
            cost += cost * costProperties.getFragileMultiplier();
        }

        double weight = order.getDeliveryWeight() != null ? order.getDeliveryWeight() : 0;
        cost += weight * costProperties.getWeightMultiplier();

        double volume = order.getDeliveryVolume() != null ? order.getDeliveryVolume() : 0;
        cost += volume * costProperties.getVolumeMultiplier();

        if (from.getStreet() != null && !from.getStreet().equals(to.getStreet())) {
            cost += cost * costProperties.getAddressMultiplier();
        }

        return BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP);
    }

    private Delivery findByOrder(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery not found for order: " + orderId));
    }
}
