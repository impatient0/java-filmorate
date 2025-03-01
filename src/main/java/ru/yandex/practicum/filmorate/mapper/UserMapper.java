package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public interface UserMapper {

    UserDto mapToUserDto(User user);

    User mapToUserModel(NewUserRequest newUserRequest);

    User updateUserFields(User user, UpdateUserRequest updateUserRequest);
}