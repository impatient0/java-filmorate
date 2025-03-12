package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Director;

@Getter
public class DirectorNotFoundException extends NotFoundException {

    private final long id;
    private final String error;

    public DirectorNotFoundException(String message, long id) {
        super(message);
        error = "Wrong Director ID";
        this.id = id;
    }

    public Class<?> getEntityType() {
        return Director.class;
    }
}