package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.LinkedHashSet;

public interface EventStorage {

    void insertUserTapeQuery(long userId, int eventId, int operationId, long entityId);

    LinkedHashSet<Event> getUserEvents(long userId);

}
