package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Genre;

@Getter
public class GenreNotFoundException extends NotFoundException {

    private final long id;

    public GenreNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public Class<?> getEntityType() {
        return Genre.class;
    }
}
