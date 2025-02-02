package ru.yandex.practicum.filmorate.repository;

import java.util.Map;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    User getUserById(long id);

    long addUser(User user);

    Map<Long, User> getAllUsers();

    void addLike(long userId, long filmId);

    void removeLike(long userId, long filmId);

    Set<Long> getUserLikedFilms(long userId);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Set<Long> getUserFriends(long userId);

    Set<Long> getCommonFriends(long userId1, long userId2);
}
