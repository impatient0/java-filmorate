package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

@Getter
public class UserValidationException extends ValidationException {

    private final String validationMessage;

    public UserValidationException(String message, String validationMessage) {
        super(message);
        this.validationMessage = validationMessage;
    }

    public Class<?> getEntityType() {
        return User.class;
    }
}
