package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class SelfFriendshipException extends RuntimeException {

    private final long id;

    public SelfFriendshipException(String message, long id) {
        super(message);
        this.id = id;
    }
}
