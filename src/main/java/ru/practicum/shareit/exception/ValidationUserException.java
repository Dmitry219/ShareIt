package ru.practicum.shareit.exception;

public class ValidationUserException extends RuntimeException {
    public ValidationUserException(String messege) {
        super(messege);
    }
}
