package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class InvalidGenreException extends RuntimeException {

    private final long id;

    public InvalidGenreException(String message, long id) {
        super(message);
        this.id = id;
    }
}
