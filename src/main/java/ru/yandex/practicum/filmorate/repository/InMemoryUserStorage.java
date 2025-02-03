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
    private final Map<Long, Set<Long>> userLikedFilms = new HashMap<>();
    private final Map<Long, Set<Long>> userFriends = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(0);

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public long addUser(User user) {
        user.setId(nextId.getAndIncrement());
        users.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Map<Long, User> getAllUsers() {
        return new HashMap<>(users);
    }

    @Override
    public void addLike(long userId, long filmId) {
        userLikedFilms.computeIfAbsent(userId, k -> new HashSet<>()).add(filmId);
    }

    @Override
    public void removeLike(long userId, long filmId) {
        userLikedFilms.get(userId).remove(filmId);
    }

    @Override
    public Set<Long> getUserLikedFilms(long userId) {
        return userLikedFilms.getOrDefault(userId, new HashSet<>());
    }

    @Override
    public void addFriend(long userId, long friendId) {
        userFriends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        userFriends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        userFriends.get(userId).remove(friendId);
        userFriends.get(friendId).remove(userId);
    }

    @Override
    public Set<Long> getUserFriends(long userId) {
        return userFriends.getOrDefault(userId, new HashSet<>());
    }

    @Override
    public Set<Long> getCommonFriends(long userId1, long userId2) {
        Set<Long> friends1 = getUserFriends(userId1);
        Set<Long> friends2 = getUserFriends(userId2);
        Set<Long> commonFriends = new HashSet<>(friends1);
        commonFriends.retainAll(friends2);
        return commonFriends;
    }
}