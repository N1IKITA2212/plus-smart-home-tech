package ru.yandex.practicum.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotEnoughInfo(NotEnoughInfoInOrderToCalculateException e) {
        return Map.of("httpStatus", "400 BAD_REQUEST", "userMessage", e.getMessage(), "message", e.getMessage());
    }

    @ExceptionHandler(NoPaymentFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoPayment(NoPaymentFoundException e) {
        return Map.of("httpStatus", "404 NOT_FOUND", "userMessage", e.getMessage(), "message", e.getMessage());
    }
}
