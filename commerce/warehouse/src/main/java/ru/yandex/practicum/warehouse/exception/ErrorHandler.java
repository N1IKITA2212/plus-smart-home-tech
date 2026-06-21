package ru.yandex.practicum.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAlreadyInWarehouse(SpecifiedProductAlreadyInWarehouseException e) {
        return Map.of("httpStatus", "400 BAD_REQUEST", "userMessage", e.getMessage(), "message", e.getMessage());
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNoProductInWarehouse(NoSpecifiedProductInWarehouseException e) {
        return Map.of("httpStatus", "400 BAD_REQUEST", "userMessage", e.getMessage(), "message", e.getMessage());
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleLowQuantity(ProductInShoppingCartLowQuantityInWarehouse e) {
        return Map.of("httpStatus", "400 BAD_REQUEST", "userMessage", e.getMessage(), "message", e.getMessage());
    }
}
