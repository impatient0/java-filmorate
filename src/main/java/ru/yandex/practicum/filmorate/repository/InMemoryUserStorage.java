package ru.yandex.practicum.filmorate.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
@SuppressWarnings("unused")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> userFriends = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public boolean checkUserExists(long userId) {
        return users.containsKey(userId);
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public long addUser(User user) {
        user.setId(nextId.getAndIncrement());
        users.put(user.getId(), user);
        userFriends.put(user.getId(), new HashSet<>());
        return user.getId();
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Set<User> getAllUsers() {
        return new HashSet<>(users.values());
    }

    public void addFriend(long userId, long friendId) {
        userFriends.get(userId).add(friendId);
        userFriends.get(friendId).add(userId);
    }

    public void deleteFriend(long userId, long friendId) {
        userFriends.get(userId).remove(friendId);
        userFriends.get(friendId).remove(userId);
    }

    public Set<Long> getUserFriends(long userId) {
        return userFriends.getOrDefault(userId, new HashSet<>());
    }

    public Set<Long> getCommonFriends(long userId1, long userId2) {
        Set<Long> friends1 = getUserFriends(userId1);
        Set<Long> friends2 = getUserFriends(userId2);
        Set<Long> commonFriends = new HashSet<>(friends1);
        commonFriends.retainAll(friends2);
        return commonFriends;
    }
}