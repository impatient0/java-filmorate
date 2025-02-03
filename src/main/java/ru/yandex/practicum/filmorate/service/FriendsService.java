package ru.yandex.practicum.filmorate.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@AllArgsConstructor
public class FriendsService {

    private final UserStorage userStorage;

    public void addFriend(long userId, long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public Set<User> getUserFriends(long userId) {
        return userStorage.getUserFriends(userId).stream().map(userStorage::getUserById)
            .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(long userId1, long userId2) {
        return userStorage.getCommonFriends(userId1, userId2).stream().map(userStorage::getUserById)
            .collect(Collectors.toSet());
    }
}
