package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.interaction.feign.OrderClient;
import ru.yandex.practicum.interaction.feign.ShoppingStoreClient;

@SpringBootApplication
@EnableFeignClients(clients = {OrderClient.class, ShoppingStoreClient.class})
public class PaymentApp {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }
}
