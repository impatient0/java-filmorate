package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

@Getter
public class UserNotFoundException extends NotFoundException {

    private final long id;

    public UserNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public Class<?> getEntityType() {
        return User.class;
    }
}
