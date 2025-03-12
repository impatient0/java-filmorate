package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Director;

@Getter
public class DirectorNotFoundException extends NotFoundExceptionForDirector {

    private final long id;
    private final String error;

    public DirectorNotFoundException(String message, long id, String error) {
        super(message);
        this.error = error;
        this.id = id;
    }

    public Class<?> getEntityType() {
        return Director.class;
    }
}