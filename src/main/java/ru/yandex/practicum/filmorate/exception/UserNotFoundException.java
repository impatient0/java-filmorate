package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final long id;

    public UserNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }
}
