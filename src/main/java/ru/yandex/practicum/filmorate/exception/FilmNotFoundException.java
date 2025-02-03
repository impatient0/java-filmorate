package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class FilmNotFoundException extends RuntimeException {

    private final long id;

    public FilmNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }
}
