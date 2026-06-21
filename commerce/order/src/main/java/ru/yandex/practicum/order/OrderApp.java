package ru.yandex.practicum.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.interaction.feign.DeliveryClient;
import ru.yandex.practicum.interaction.feign.PaymentClient;
import ru.yandex.practicum.interaction.feign.WarehouseClient;

@SpringBootApplication
@EnableFeignClients(clients = {DeliveryClient.class, PaymentClient.class, WarehouseClient.class})
public class OrderApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}
