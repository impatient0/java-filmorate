package ru.yandex.practicum.filmorate.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.InvalidFriendshipRequestException;
import ru.yandex.practicum.filmorate.exception.SelfFriendshipException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.repository.EventStorage;
import ru.yandex.practicum.filmorate.repository.FriendshipStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@AllArgsConstructor
@Slf4j
public class FriendsService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final UserMapper mapper;
    private final EventStorage eventStorage;

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("Adding friend failed: user can not be friend with himself");
            throw new SelfFriendshipException("Error when adding friend", userId);
        }
        if (!userStorage.checkUserExists(userId)) {
            log.warn("Adding friend failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when adding friend", userId);
        }
        if (!userStorage.checkUserExists(friendId)) {
            log.warn("Adding friend failed: friend with ID {} not found", friendId);
            throw new UserNotFoundException("Error when adding friend", friendId);
        }
        log.debug("Processing friend request between users {} and {}", userId, friendId);
        Optional<FriendshipStatus> status = friendshipStorage.getDirectionalFriendshipStatus(userId,
            friendId);
        Optional<FriendshipStatus> statusReversed =
            friendshipStorage.getDirectionalFriendshipStatus(
            friendId, userId);
        if (status.isEmpty()) {
            if (statusReversed.isEmpty()) {
                friendshipStorage.insertDirectionalFriendship(userId, friendId,
                    FriendshipStatus.PENDING);
                eventStorage.insertUserTapeQuery(userId, 3, 2, friendId);
                log.debug("Sent friend request from user {} to user {}", userId, friendId);
            } else {
                friendshipStorage.insertDirectionalFriendship(userId, friendId,
                    FriendshipStatus.CONFIRMED);
                friendshipStorage.updateFriendshipStatus(friendId, userId,
                    FriendshipStatus.CONFIRMED);
                eventStorage.insertUserTapeQuery(userId, 3, 3, friendId);
                log.debug("Confirmed friend request from user {} to user {}", friendId, userId);
            }
        } else {
            if (statusReversed.isPresent()) {
                log.warn("Adding friend failed: user {} is already friends with user {}", userId,
                    friendId);
                throw new InvalidFriendshipRequestException("Users are already friends", userId,
                    friendId);
            } else {
                log.warn("Adding friend failed: duplicate friend request from user {} to user {}",
                    userId, friendId);
                throw new InvalidFriendshipRequestException("Friend request already sent", userId,
                    friendId);
            }
        }
    }

    public void removeFriend(long userId, long friendId) {
        if (!userStorage.checkUserExists(userId)) {
            log.warn("Removing friendship failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when removing friendship", userId);
        }
        if (!userStorage.checkUserExists(friendId)) {
            log.warn("Removing friendship failed: friend with ID {} not found", friendId);
            throw new UserNotFoundException("Error when removing friendship", friendId);
        }
        log.debug("Removing friendship between users {} and {}", userId, friendId);
        friendshipStorage.deleteDirectionalFriendship(userId, friendId);
        eventStorage.insertUserTapeQuery(userId, 3, 1, friendId);
        friendshipStorage.updateFriendshipStatus(friendId, userId, FriendshipStatus.PENDING);
        //eventStorage.insertUserTapeQuery(userId, 3, 2, friendId);
    }

    public Set<UserDto> getUserFriends(long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Getting friends failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when getting friends", userId);
        }
        log.debug("Getting friends of user with ID {}", userId);
        return friendshipStorage.getUserFriends(userId).stream().map(mapper::mapToUserDto)
            .collect(Collectors.toSet());
    }

    public Set<UserDto> getCommonFriends(long userId1, long userId2) {
        log.debug("Getting common friends of users {} and {}", userId1, userId2);
        return friendshipStorage.getCommonFriends(userId1, userId2).stream()
            .map(mapper::mapToUserDto).collect(Collectors.toSet());
    }

}
