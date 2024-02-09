package ru.practicum.shareit.exception;

public class AuthorizationFailureException extends RuntimeException {
    public AuthorizationFailureException(String messege) {
        super(messege);
    }
}
