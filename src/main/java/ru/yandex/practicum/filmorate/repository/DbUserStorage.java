package ru.yandex.practicum.filmorate.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

@Repository
@Primary
@SuppressWarnings("unused")
public class DbUserStorage extends DbBaseStorage<User> implements UserStorage {

    private static final String CHECK_EXISTS_QUERY =
        "SELECT EXISTS (SELECT 1 FROM users WHERE " + "user_id = ?)";
    private static final String GET_ALL_QUERY = "SELECT * FROM users ORDER BY user_id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY =
        "INSERT INTO users (email, login, name, birthday) " + "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
        "UPDATE users SET email = ?, login = ?, name = ?, " + "birthday = ? WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = ?";

    public DbUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public boolean checkUserExists(long userId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, userId));
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return getSingle(GET_BY_ID_QUERY, userId);
    }

    @Override
    public long addUser(User user) {
        return insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(),
            user.getBirthday());
    }

    @Override
    public void updateUser(User user) {
        update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(),
            user.getId());
    }

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(getMultiple(GET_ALL_QUERY));
    }

    @Override
    public void deleteUser(long userId) {
        delete(DELETE_QUERY, userId);
    }

}
