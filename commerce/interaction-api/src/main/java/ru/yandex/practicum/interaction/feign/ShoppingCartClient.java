package ru.yandex.practicum.interaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.ShoppingCartApi;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient extends ShoppingCartApi {
}
