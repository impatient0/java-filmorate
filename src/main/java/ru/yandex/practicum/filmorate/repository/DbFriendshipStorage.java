package ru.yandex.practicum.filmorate.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

@Repository
@SuppressWarnings("unused")
public class DbFriendshipStorage extends DbBaseStorage<User> implements FriendshipStorage {

    private static final RowMapper<FriendshipStatus> friendshipStatusRowMapper = (rs, rowNum) -> {
        String statusStr = rs.getString("status");
        try {
            return FriendshipStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    };
    private static final String GET_DIRECTIONAL_FRIENDSHIP_STATUS_QUERY =
        "SELECT status FROM " + "friendships WHERE user_id = ? AND friend_id = ?";
    private static final String INSERT_DIRECTIONAL_FRIENDSHIP_QUERY = "INSERT INTO friendships "
        + "(user_id, friend_id, status, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
    private static final String UPDATE_DIRECTIONAL_FRIENDSHIP_STATUS_QUERY =
        "UPDATE friendships " + "SET status = ? WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_DIRECTIONAL_FRIENDSHIP_QUERY =
        "DELETE FROM friendships " + "WHERE user_id = ? AND friend_id = ?";
    private static final String GET_USER_FRIENDS_QUERY = "SELECT u.* FROM users u JOIN "
        + "friendships f ON u.user_id = f.friend_id WHERE f.user_id = ?";
    private static final String GET_COMMON_FRIENDS_QUERY = "SELECT u.* FROM users u JOIN "
        + "friendships f1 ON u.user_id = f1.friend_id AND f1.user_id = ? JOIN friendships f2 ON"
        + " u.user_id = f2.friend_id AND f2.user_id = ?";

    public DbFriendshipStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<FriendshipStatus> getDirectionalFriendshipStatus(long userId, long friendId) {
        try {
            FriendshipStatus status = jdbc.queryForObject(GET_DIRECTIONAL_FRIENDSHIP_STATUS_QUERY,
                friendshipStatusRowMapper, userId, friendId);
            return Optional.ofNullable(status);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void insertDirectionalFriendship(long userId, long friendId, FriendshipStatus status) {
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, userId, friendId, status.toString());
    }

    @Override
    public void updateFriendshipStatus(long userId, long friendId, FriendshipStatus status) {
        jdbc.update(UPDATE_DIRECTIONAL_FRIENDSHIP_STATUS_QUERY, status.toString(), userId,
            friendId);
    }

    @Override
    public void deleteDirectionalFriendship(long userId, long friendId) {
        jdbc.update(DELETE_DIRECTIONAL_FRIENDSHIP_QUERY, userId, friendId);
    }

    @Override
    public Set<User> getUserFriends(long userId) {
        return new HashSet<>(getMultiple(GET_USER_FRIENDS_QUERY, userId));
    }

    @Override
    public Set<User> getCommonFriends(long userId1, long userId2) {
        return new HashSet<>(getMultiple(GET_COMMON_FRIENDS_QUERY, userId1, userId2));
    }
}