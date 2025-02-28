package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class FilmValidationException extends ValidationException {

    private final String validationMessage;

    public FilmValidationException(String message, String validationMessage) {
        super(message);
        this.validationMessage = validationMessage;
    }

    public Class<?> getEntityType() {
        return Film.class;
    }
}
