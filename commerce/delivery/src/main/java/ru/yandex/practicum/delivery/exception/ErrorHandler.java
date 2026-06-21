package ru.yandex.practicum.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoDelivery(NoDeliveryFoundException e) {
        return ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND.toString())
                .userMessage(e.getMessage())
                .message(e.getMessage())
                .build();
    }
}
