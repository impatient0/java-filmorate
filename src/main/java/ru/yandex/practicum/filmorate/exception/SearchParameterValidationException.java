package ru.yandex.practicum.filmorate.exception;

public class SearchParameterValidationException extends RuntimeException {
    public SearchParameterValidationException(String message, String details) {
        super(message + ": " + details);
    }
}