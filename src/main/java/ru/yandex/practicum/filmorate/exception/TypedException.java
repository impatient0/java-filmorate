package ru.yandex.practicum.filmorate.exception;

public abstract class TypedException extends RuntimeException {

    public TypedException(String message) {
        super(message);
    }

    public abstract Class<?> getEntityType();
}
