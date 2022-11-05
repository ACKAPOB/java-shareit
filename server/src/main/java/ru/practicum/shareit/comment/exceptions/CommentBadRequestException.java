package ru.practicum.shareit.comment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentBadRequestException extends RuntimeException {
    public CommentBadRequestException(String message) {
        System.out.println(message);
    }
}