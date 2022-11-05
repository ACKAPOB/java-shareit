package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExcHandler {
    @ExceptionHandler(MessageFailedException.class)
    public ResponseEntity handleException(MessageFailedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{\"error\":\"Unknown state: UNSUPPORTED_STATUS\"}");
    }
}
