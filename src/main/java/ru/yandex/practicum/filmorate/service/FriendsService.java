package ru.yandex.practicum.filmorate.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.SelfFriendshipException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@AllArgsConstructor
@Slf4j
public class FriendsService {

    private final UserStorage userStorage;

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("Adding friend failed: user can not be friend with himself");
            throw new SelfFriendshipException("Error when adding friend", userId);
        }
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Adding friend failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when adding friend", userId);
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            log.warn("Adding friend failed: friend with ID {} not found", friendId);
            throw new UserNotFoundException("Error when adding friend", friendId);
        }
        log.debug("Adding new friendship between users {} and {}", userId, friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Removing friendship failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when removing friendship", userId);
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            log.warn("Removing friendship failed: friend with ID {} not found", friendId);
            throw new UserNotFoundException("Error when removing friendship", friendId);
        }
        log.debug("Removing friendship between users {} and {}", userId, friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public Set<User> getUserFriends(long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Getting friends failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when getting friends", userId);
        }
        log.debug("Getting friends of user with ID {}", userId);
        return userStorage.getUserFriends(userId).stream()
            .flatMap(id -> userStorage.getUserById(id).stream()).collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(long userId1, long userId2) {
        log.debug("Getting common friends of users {} and {}", userId1, userId2);
        return userStorage.getCommonFriends(userId1, userId2).stream()
            .flatMap(id -> userStorage.getUserById(id).stream()).collect(Collectors.toSet());
    }
}
