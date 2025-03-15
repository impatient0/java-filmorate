package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Director;

@Getter
public class DirectorNotFoundException extends NotFoundException {

    private final long id;

    public DirectorNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public Class<?> getEntityType() {
        return Director.class;
    }
}