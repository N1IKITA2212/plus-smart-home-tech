package ru.yandex.practicum.interaction.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    private UUID deliveryId;

    @NotNull
    @Valid
    private AddressDto fromAddress;

    @NotNull
    @Valid
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    private DeliveryState deliveryState;
}
