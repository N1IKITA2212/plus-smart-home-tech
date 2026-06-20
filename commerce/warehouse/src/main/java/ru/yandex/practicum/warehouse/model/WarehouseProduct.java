package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
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
