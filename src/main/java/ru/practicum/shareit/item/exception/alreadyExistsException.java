package ru.practicum.shareit.item.exception;

public class alreadyExistsException extends RuntimeException{
    public alreadyExistsException(String message) {
        super(message);
    }
}
