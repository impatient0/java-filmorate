package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class InvalidMpaRatingException extends RuntimeException {

    private final long id;

    public InvalidMpaRatingException(String message, long id) {
        super(message);
        this.id = id;
    }
}
