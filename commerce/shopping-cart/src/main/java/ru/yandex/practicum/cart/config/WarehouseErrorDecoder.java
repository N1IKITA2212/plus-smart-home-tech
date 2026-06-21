package ru.yandex.practicum.cart.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.yandex.practicum.cart.exception.ProductInShoppingCartLowQuantityInWarehouse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class WarehouseErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 400) {
            String message = extractMessage(response);
            return new ProductInShoppingCartLowQuantityInWarehouse(
                    message.isBlank() ? "Недостаточно товара на складе" : message);
        }
        return defaultDecoder.decode(methodKey, response);
    }

    private String extractMessage(Response response) {
        if (response.body() == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        } catch (Exception e) {
            return "";
        }
    }
}
