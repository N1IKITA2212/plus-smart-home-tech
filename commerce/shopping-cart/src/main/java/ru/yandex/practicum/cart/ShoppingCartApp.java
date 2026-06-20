package ru.yandex.practicum.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.cart.config.WarehouseClientConfig;
import ru.yandex.practicum.interaction.feign.WarehouseClient;

@SpringBootApplication
@EnableFeignClients(
        clients = {WarehouseClient.class},
        defaultConfiguration = WarehouseClientConfig.class
)
public class ShoppingCartApp {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApp.class, args);
    }
}
