package ru.yandex.practicum.filmorate.service;

import java.util.LinkedHashSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EventNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.EventStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventStorage eventStorage;

    public LinkedHashSet<Event> getEventById(long userId) {
        LinkedHashSet<Event> event = eventStorage.getUserEvents(userId);
        if (event.isEmpty()) {
            log.warn("Event feed for the user with ID {} is empty", userId);
            throw new EventNotFoundException("Error when getting event feed", userId);
        }
        log.debug("Getting all events of the user with ID {}", userId);
        return event;
    }

}
