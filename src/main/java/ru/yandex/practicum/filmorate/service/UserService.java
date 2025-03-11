package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final Validator validator;
    private final UserMapper mapper;

    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream().map(mapper::mapToUserDto)
            .collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            log.warn("Getting user failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when getting user", userId);
        }
        log.debug("Getting user with ID {}", userId);
        return mapper.mapToUserDto(user.get());
    }

    public UserDto addUser(NewUserRequest newUserRequest) {
        Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(newUserRequest);
        if (!violations.isEmpty()) {
            log.warn("Adding user failed: {}", violations.iterator().next().getMessage());
            throw new UserValidationException("Error when creating new user",
                violations.iterator().next().getMessage());
        }
        if (newUserRequest.getName() == null) {
            log.debug("User had no name - setting it to login ('{}')", newUserRequest.getLogin());
            newUserRequest.setName(newUserRequest.getLogin());
        }
        User user = mapper.mapToUserModel(newUserRequest);
        long userId = userStorage.addUser(user);
        user.setId(userId);
        log.debug("Adding new user {}", newUserRequest);
        return mapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest updateUserRequest) {
        User user = userStorage.getUserById(updateUserRequest.getId()).orElseThrow(() -> {
            log.warn("Updating user failed: user with ID {} not found", updateUserRequest.getId());
            return new UserNotFoundException("Error when updating user", updateUserRequest.getId());
        });
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(
            updateUserRequest);
        if (!violations.isEmpty()) {
            log.warn("Updating user failed: {}", violations.iterator().next().getMessage());
            throw new UserValidationException("Error when updating user",
                violations.iterator().next().getMessage());
        }
        log.debug("Updating user with ID {}: {}", updateUserRequest.getId(), updateUserRequest);
        user = mapper.updateUserFields(user, updateUserRequest);
        userStorage.updateUser(user);
        return mapper.mapToUserDto(user);
    }

    public void deleteUser(long userId) {
        if (!userStorage.checkUserExists(userId)) {
            log.warn("Deleting user failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when deleting user", userId);
        }
        log.debug("Deleting user with ID {}", userId);
        userStorage.deleteUser(userId);
    }

}
