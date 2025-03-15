package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Optional;
import java.util.Set;

public interface EventStorage {

    void insertUserTapeQuery(long userId, int eventId, int operationId, long entityId);

    Optional<Set<Event>> getUserEvents(long userId);

}
