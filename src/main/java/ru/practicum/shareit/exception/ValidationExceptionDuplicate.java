package ru.practicum.shareit.exception;

public class ValidationExceptionDuplicate extends RuntimeException {
    public ValidationExceptionDuplicate(String messege) {
        super(messege);
    }
}
