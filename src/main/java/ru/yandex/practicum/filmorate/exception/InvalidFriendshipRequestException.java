package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class InvalidFriendshipRequestException extends RuntimeException {

    private final long userId;
    private final long friendId;

    public InvalidFriendshipRequestException(String message, long userId, long friendId) {
        super(message);
        this.userId = userId;
        this.friendId = friendId;
    }
}

