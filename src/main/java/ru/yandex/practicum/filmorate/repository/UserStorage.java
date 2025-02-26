package ru.yandex.practicum.filmorate.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    boolean checkUserExists(long id);

    Optional<User> getUserById(long id);

    long addUser(User user);

    void updateUser(User user);

    Map<Long, User> getAllUsers();

    void addLike(long userId, long filmId);

    void removeLike(long userId, long filmId);

    Set<Long> getUserLikedFilms(long userId);
}
