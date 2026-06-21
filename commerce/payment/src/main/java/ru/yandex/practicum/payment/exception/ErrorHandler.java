package ru.yandex.practicum.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotEnoughInfo(NotEnoughInfoInOrderToCalculateException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoPaymentFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoPayment(NoPaymentFoundException e) {
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    private ErrorResponse build(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .httpStatus(status.toString())
                .userMessage(message)
                .message(message)
                .build();
    }
}
