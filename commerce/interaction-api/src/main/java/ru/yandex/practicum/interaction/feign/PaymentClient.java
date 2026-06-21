package ru.yandex.practicum.interaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.PaymentApi;

@FeignClient(name = "payment")
public interface PaymentClient extends PaymentApi {
}
