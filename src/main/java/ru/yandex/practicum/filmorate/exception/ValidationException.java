package ru.yandex.practicum.filmorate.exception;

public abstract class ValidationException extends TypedException {

    public ValidationException(String message) {
        super(message);
    }

    public abstract String getValidationMessage();
}
