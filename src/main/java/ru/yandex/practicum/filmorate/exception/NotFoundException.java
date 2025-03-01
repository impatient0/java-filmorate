package ru.yandex.practicum.filmorate.exception;

public abstract class NotFoundException extends TypedException {

    public NotFoundException(String message) {
        super(message);
    }

    public abstract long getId();
}
