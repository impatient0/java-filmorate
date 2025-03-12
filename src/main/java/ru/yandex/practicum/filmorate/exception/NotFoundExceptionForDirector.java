package ru.yandex.practicum.filmorate.exception;

public abstract class NotFoundExceptionForDirector extends TypedException {

    public NotFoundExceptionForDirector(String message) {
        super(message);
    }

    public abstract long getId();

    public abstract String getError();
}
