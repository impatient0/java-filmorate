package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class InternalServerException extends RuntimeException {

    private final Throwable cause;

    public InternalServerException(String message) {
        super(message);
        cause = null;
    }

    public InternalServerException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

}
