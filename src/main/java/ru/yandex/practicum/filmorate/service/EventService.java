package ru.yandex.practicum.filmorate.service;

import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.EventStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventStorage eventStorage;

    public Set<Event> getEventById(long userId) {
        Optional<Set<Event>> event = eventStorage.getUserEvents(userId);
        if (event.isEmpty()) {
            log.warn("Getting user failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when getting user", userId);
        }
        log.debug("Getting all events of the user with ID {}", userId);
        return event.get();
    }

}
