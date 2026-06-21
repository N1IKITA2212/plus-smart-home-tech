package ru.yandex.practicum.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoDelivery(NoDeliveryFoundException e) {
        return Map.of("httpStatus", "404 NOT_FOUND", "userMessage", e.getMessage(), "message", e.getMessage());
    }
}
