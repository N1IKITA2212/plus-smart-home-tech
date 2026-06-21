package ru.yandex.practicum.interaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.OrderApi;

@FeignClient(name = "order")
public interface OrderClient extends OrderApi {
}
