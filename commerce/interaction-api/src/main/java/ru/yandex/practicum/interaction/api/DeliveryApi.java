package ru.yandex.practicum.interaction.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.DeliveryDto;
import ru.yandex.practicum.interaction.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryApi {

    @PutMapping("/api/v1/delivery")
    DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto delivery);

    @PostMapping("/api/v1/delivery/successful")
    void deliverySuccessful(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/picked")
    void deliveryPicked(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/failed")
    void deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/api/v1/delivery/cost")
    BigDecimal deliveryCost(@Valid @RequestBody OrderDto order);
}
