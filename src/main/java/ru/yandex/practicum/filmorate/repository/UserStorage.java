package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    boolean checkUserExists(long userId);

    Optional<User> getUserById(long userId);

    long addUser(User user);

    void updateUser(User user);

    Collection<User> getAllUsers();
}
