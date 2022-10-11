package ru.practicum.shareit.requests.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequestsBadRequestException extends RuntimeException {
    public RequestsBadRequestException(String message) {
        System.out.println(message);
    }
}