package ru.yandex.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.PaymentApi;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService service;

    @Override
    public PaymentDto payment(OrderDto order) {
        return service.payment(order);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        return service.getTotalCost(order);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        return service.productCost(order);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        service.paymentSuccess(paymentId);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        service.paymentFailed(paymentId);
    }
}
