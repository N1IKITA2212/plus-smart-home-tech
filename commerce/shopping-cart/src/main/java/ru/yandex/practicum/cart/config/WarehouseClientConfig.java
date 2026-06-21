package ru.yandex.practicum.cart.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class WarehouseClientConfig {

    @Bean
    public ErrorDecoder warehouseErrorDecoder() {
        return new WarehouseErrorDecoder();
    }
}
