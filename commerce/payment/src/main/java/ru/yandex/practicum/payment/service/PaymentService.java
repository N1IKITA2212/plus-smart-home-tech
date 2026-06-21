package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.PaymentDto;
import ru.yandex.practicum.interaction.dto.PaymentState;
import ru.yandex.practicum.interaction.dto.ProductDto;
import ru.yandex.practicum.interaction.feign.OrderClient;
import ru.yandex.practicum.interaction.feign.ShoppingStoreClient;
import ru.yandex.practicum.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.10");

    private final PaymentRepository repository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto order) {
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Order has no products to calculate cost");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            ProductDto product = shoppingStoreClient.getProduct(entry.getKey());
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto order) {
        if (order.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Delivery price is required to calculate total cost");
        }
        BigDecimal productTotal = productCost(order);
        BigDecimal fee = productTotal.multiply(VAT_RATE);
        return productTotal.add(fee).add(order.getDeliveryPrice()).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public PaymentDto payment(OrderDto order) {
        if (order.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Delivery price is required to form payment");
        }
        BigDecimal productTotal = productCost(order);
        BigDecimal fee = productTotal.multiply(VAT_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal deliveryTotal = order.getDeliveryPrice();
        BigDecimal total = productTotal.add(fee).add(deliveryTotal).setScale(2, RoundingMode.HALF_UP);

        Payment payment = Payment.builder()
                .orderId(order.getOrderId())
                .productTotal(productTotal)
                .deliveryTotal(deliveryTotal)
                .feeTotal(fee)
                .totalPayment(total)
                .state(PaymentState.PENDING)
                .build();
        payment = repository.save(payment);

        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(total)
                .deliveryTotal(deliveryTotal)
                .feeTotal(fee)
                .build();
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = findById(paymentId);
        payment.setState(PaymentState.SUCCESS);
        repository.save(payment);
        orderClient.payment(payment.getOrderId());
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = findById(paymentId);
        payment.setState(PaymentState.FAILED);
        repository.save(payment);
        orderClient.paymentFailed(payment.getOrderId());
    }

    private Payment findById(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Payment not found: " + paymentId));
    }
}
