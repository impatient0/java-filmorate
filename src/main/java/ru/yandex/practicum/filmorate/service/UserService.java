package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final Validator validator;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers().values();
    }

    public User getUserById(long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isEmpty()) {
            log.warn("Getting user failed: user with ID {} not found", id);
            throw new UserNotFoundException("Error when getting user", id);
        }
        log.debug("Getting user with ID {}", id);
        return user.get();
    }

    public User addUser(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            log.warn("Adding user failed: {}", violations.iterator().next().getMessage());
            throw new UserValidationException("Error when creating new user",
                violations.iterator().next().getMessage());
        }
        long userId = userStorage.addUser(user);
        user.setId(userId);
        log.debug("Adding new user {}", user);
        return user;
    }

    public void updateUser(User user) {
        if (userStorage.getUserById(user.getId()).isEmpty()) {
            log.warn("Updating user failed: user with ID {} not found", user.getId());
            throw new UserNotFoundException("Error when updating user", user.getId());
        }
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            log.warn("Updating user failed: {}", violations.iterator().next().getMessage());
            throw new UserValidationException("Error when updating user",
                violations.iterator().next().getMessage());
        }
        log.debug("Updating user with ID {}: {}", user.getId(), user);
        userStorage.updateUser(user);
    }

}
