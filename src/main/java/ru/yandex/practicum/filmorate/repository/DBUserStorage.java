package ru.yandex.practicum.filmorate.repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

@Repository
@Primary
@SuppressWarnings("unused")
public class DBUserStorage extends DBBaseStorage<User> implements UserStorage {

    private static final String CHECK_EXISTS_QUERY =
        "SELECT EXISTS (SELECT 1 FROM users WHERE " + "user_id = ?)";
    private static final String GET_ALL_QUERY = "SELECT * FROM users";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY =
        "INSERT INTO users (email, login, name, birthday) " + "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
        "UPDATE users SET email = ?, login = ?, name = ?, " + "birthday = ? WHERE user_id = ?";
    private static final String ADD_LIKE_QUERY =
        "INSERT INTO likes (user_id, like_id, liked_at) " + "VALUES (?, ?, CURRENT_TIMESTAMP)";
    private static final String REMOVE_LIKE_QUERY =
        "DELETE FROM likes WHERE user_id = ? AND " + "film_id = ?";
    private static final String GET_LIKED_FILMS_QUERY = "SELECT f.film_id FROM films AS f RIGHT "
        + "JOIN likes AS l ON f.film_id = l.film_id WHERE l.user_id = ?";

    public DBUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public boolean checkUserExists(long userId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, userId));
    }

    @Override
    public Optional<User> getUserById(long id) {
        return super.getSingle(GET_BY_ID_QUERY, id);
    }

    @Override
    public long addUser(User user) {
        return super.insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(),
            user.getBirthday());
    }

    @Override
    public void updateUser(User user) {
        super.update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(),
            user.getBirthday(), user.getId());
    }

    @Override
    public Map<Long, User> getAllUsers() {
        return super.getMultiple(GET_ALL_QUERY).stream()
            .collect(Collectors.toMap(User::getId, u -> u));
    }

    @Override
    public void addLike(long userId, long filmId) {
        jdbc.update(ADD_LIKE_QUERY, userId, filmId);
    }

    @Override
    public void removeLike(long userId, long filmId) {
        jdbc.update(REMOVE_LIKE_QUERY, userId, filmId);
    }

    @Override
    public Set<Long> getUserLikedFilms(long userId) {
        return new HashSet<>(jdbc.queryForList(GET_LIKED_FILMS_QUERY, Long.class));
    }

}
