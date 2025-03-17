package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.ArrayList;

public interface EventStorage {

    void insertUserFeedQuery(long userId, String eventName, String operationName, long entityId);

    ArrayList<Event> getUserEvents(long userId);

}
