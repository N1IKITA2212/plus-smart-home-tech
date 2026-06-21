package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.interaction.dto.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "paymentId")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    @Column(nullable = false)
    private UUID orderId;

    private BigDecimal productTotal;

    private BigDecimal deliveryTotal;

    private BigDecimal feeTotal;

    private BigDecimal totalPayment;

    @Enumerated(EnumType.STRING)
    private PaymentState state;
}
