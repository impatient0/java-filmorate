package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class DirectorValidationException extends ValidationException {

    private final String validationMessage;

    public DirectorValidationException(String message, String validationMessage) {
        super(message);
        this.validationMessage = validationMessage;
    }

    public Class<?> getEntityType() {
        return Film.class;
    }
}
