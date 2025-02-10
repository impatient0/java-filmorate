package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class UserValidationException extends RuntimeException {

    private final String validationMessage;

    public UserValidationException(String message, String validationMessage) {
        super(message);
        this.validationMessage = validationMessage;
    }
}
