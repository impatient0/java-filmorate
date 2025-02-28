package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Getter
public class MpaRatingNotFoundException extends NotFoundException {

    private final long id;

    public MpaRatingNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public Class<?> getEntityType() {
        return MpaRating.class;
    }
}
