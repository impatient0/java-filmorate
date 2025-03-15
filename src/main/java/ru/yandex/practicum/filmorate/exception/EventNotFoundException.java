package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Event;

@Getter
public class EventNotFoundException extends NotFoundException {

    private final long id;

    public EventNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public Class<?> getEntityType() {
        return Event.class;
    }
}