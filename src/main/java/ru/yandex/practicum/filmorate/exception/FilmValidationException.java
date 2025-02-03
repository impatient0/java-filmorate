package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class FilmValidationException extends RuntimeException {

    private final String validationMessage;

    public FilmValidationException(String message, String validationMessage) {
        super(message);
        this.validationMessage = validationMessage;
    }
}
