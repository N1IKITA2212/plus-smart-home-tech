package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = "products")
@EqualsAndHashCode(of = "orderId")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_bookings")
public class OrderBooking {

    @Id
    private UUID orderId;

    private UUID deliveryId;

    @ElementCollection
    @CollectionTable(
            name = "order_booking_products",
            joinColumns = @JoinColumn(name = "order_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    @Builder.Default
    private Map<UUID, Long> products = new HashMap<>();
}
