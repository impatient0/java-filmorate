package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

@Getter
public class FilmNotFoundException extends NotFoundException {

    private final long id;

    public FilmNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public Class<?> getEntityType() {
        return Film.class;
    }
}
