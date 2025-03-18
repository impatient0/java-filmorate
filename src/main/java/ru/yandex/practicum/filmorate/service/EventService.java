package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EventNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.EventStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public ArrayList<Event> getEventById(long userId) {
        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            log.warn("Getting user failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when getting user", userId);
        } else {
            ArrayList<Event> event = eventStorage.getUserEvents(userId);
            if (event.isEmpty()) {
                log.warn("Event feed for the user with ID {} is empty", userId);
                throw new EventNotFoundException("Error when getting event feed", userId);
            }
            log.debug("Getting all events of the user with ID {}", userId);
            return event;
        }
    }
}
