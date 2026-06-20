package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "productId")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouse_products")
public class WarehouseProduct {

    @Id
    private UUID productId;

    private boolean fragile;

    @Embedded
    private Dimension dimension;

    private double weight;

    private long quantity;
}
