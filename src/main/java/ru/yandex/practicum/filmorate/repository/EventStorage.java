package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Set;

public interface EventStorage {

    void insertUserTapeQuery(long userId, int eventId, int operationId, long entityId);

    Set<Event> getUserEvents(long userId);

}
