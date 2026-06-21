package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.dto.AddressDto;
import ru.yandex.practicum.interaction.dto.DeliveryDto;
import ru.yandex.practicum.interaction.dto.DeliveryState;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.feign.OrderClient;
import ru.yandex.practicum.interaction.feign.WarehouseClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final double BASE_RATE = 5.0;
    private static final String ADDRESS_2 = "ADDRESS_2";

    private final DeliveryRepository repository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        Delivery delivery = Delivery.builder()
                .orderId(dto.getOrderId())
                .fromAddress(toEntity(dto.getFromAddress()))
                .toAddress(toEntity(dto.getToAddress()))
                .deliveryState(DeliveryState.CREATED)
                .build();
        return toDto(repository.save(delivery));
    }

    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = findByOrder(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        repository.save(delivery);
        warehouseClient.shippedToDelivery(
                ru.yandex.practicum.interaction.dto.ShippedToDeliveryRequest.builder()
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

        double cost = BASE_RATE;
        double warehouseFactor = (from.getCountry() != null && from.getCountry().contains(ADDRESS_2)) ? 2 : 1;
        cost = cost * warehouseFactor + BASE_RATE;

        if (Boolean.TRUE.equals(order.getFragile())) {
            cost += cost * 0.2;
        }

        double weight = order.getDeliveryWeight() != null ? order.getDeliveryWeight() : 0;
        cost += weight * 0.3;

        double volume = order.getDeliveryVolume() != null ? order.getDeliveryVolume() : 0;
        cost += volume * 0.2;

        if (from.getStreet() != null && !from.getStreet().equals(to.getStreet())) {
            cost += cost * 0.2;
        }

        return BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP);
    }

    private Delivery findByOrder(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery not found for order: " + orderId));
    }

    private Address toEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        return Address.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }

    private AddressDto toAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }

    private DeliveryDto toDto(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .fromAddress(toAddressDto(delivery.getFromAddress()))
                .toAddress(toAddressDto(delivery.getToAddress()))
                .deliveryState(delivery.getDeliveryState())
                .build();
    }
}
