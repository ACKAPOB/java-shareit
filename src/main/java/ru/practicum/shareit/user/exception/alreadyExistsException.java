package ru.practicum.shareit.user.exception;

public class alreadyExistsException extends RuntimeException{
    public alreadyExistsException(String message) {
        super(message);
    }
}
