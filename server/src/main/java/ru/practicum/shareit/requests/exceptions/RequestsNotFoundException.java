package ru.practicum.shareit.requests.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RequestsNotFoundException extends RuntimeException {
    public RequestsNotFoundException(String message) {
        super(message);
    }
}
