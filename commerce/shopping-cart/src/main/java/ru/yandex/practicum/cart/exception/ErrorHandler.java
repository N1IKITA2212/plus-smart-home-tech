package ru.yandex.practicum.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleNotAuthorized(NotAuthorizedUserException e) {
        return Map.of("httpStatus", "401 UNAUTHORIZED", "userMessage", e.getMessage(), "message", e.getMessage());
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoProducts(NoProductsInShoppingCartException e) {
        return Map.of("httpStatus", "400 BAD_REQUEST", "userMessage", e.getMessage(), "message", e.getMessage());
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleLowQuantity(ProductInShoppingCartLowQuantityInWarehouse e) {
        return Map.of("httpStatus", "400 BAD_REQUEST", "userMessage", e.getMessage(), "message", e.getMessage());
    }
}
