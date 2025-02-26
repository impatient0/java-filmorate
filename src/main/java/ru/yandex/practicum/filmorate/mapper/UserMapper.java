package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@Component
public interface UserMapper {

    UserDto mapToUserDto(User user);

    User mapToUserModel(NewUserRequest newUserRequest);

    User updateUserFields(User user, UpdateUserRequest updateUserRequest);
}