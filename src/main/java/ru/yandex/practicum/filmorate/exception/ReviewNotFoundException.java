package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Review;

@Getter
public class ReviewNotFoundException extends NotFoundException {

    private final long id;

    public ReviewNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    @Override
    public Class<?> getEntityType() {
        return Review.class;
    }
}
