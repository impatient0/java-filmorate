package ru.yandex.practicum.filmorate.repository;

import java.util.Optional;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

public interface FriendshipStorage {

    Optional<FriendshipStatus> getDirectionalFriendshipStatus(long userId, long friendId);

    void insertDirectionalFriendship(long userId, long friendId, FriendshipStatus status);

    void updateFriendshipStatus(long userId, long friendId, FriendshipStatus status);

    void deleteDirectionalFriendship(long userId, long friendId);

    Set<User> getUserFriends(long userId);

    Set<User> getCommonFriends(long userId1, long userId2);

}
