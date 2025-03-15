package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@Repository
@Primary
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Slf4j
public class DbLikesStorage implements LikesStorage {

    private static final String ADD_LIKE_QUERY =
            "INSERT INTO likes (user_id, film_id, liked_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
    private static final String REMOVE_LIKE_QUERY =
        "DELETE FROM likes WHERE user_id = ? AND " + "film_id = ?";
    private static final String GET_LIKED_FILMS_QUERY = "WITH film_likes AS (SELECT film_id FROM "
        + "likes WHERE user_id = ?) SELECT f.film_id, f.name AS film_name, f.description, f"
        + ".release_date, f.duration, m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS "
        + "genre_name, d.director_id, d.name AS director_name FROM films f JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id LEFT JOIN "
        + "film_genres fg ON f.film_id = fg.film_id LEFT JOIN genres g ON fg.genre_id = g"
        + ".genre_id JOIN film_likes ON f.film_id = film_likes.film_id "
        + "LEFT JOIN film_directors fd ON f.film_id = fd.film_id "
        + "LEFT JOIN directors d ON fd.director_id = d.director_id "
        + "ORDER BY f.film_id, g.genre_id";
    private static final String GET_USERS_WHO_LIKED_FILM_QUERY = "SELECT u.* FROM users AS u RIGHT " +
            "JOIN likes AS l ON u.user_id = l.user_id WHERE l.film_id = ?";
    private static final String GET_POPULAR_FILMS_QUERY =
            "WITH film_likes AS (SELECT film_id, COUNT(film_id) AS likes_count FROM likes GROUP BY film_id) " +
                    "SELECT f.film_id, f.name AS film_name, f.description, f.release_date, f.duration, " +
                    "m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS genre_name, d.director_id, d.name AS director_name, " +
                    "COALESCE(film_likes.likes_count, 0) AS likes_count " +
                    "FROM films f " +
                    "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id " +
                    "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN film_likes ON f.film_id = film_likes.film_id " +
                    "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN directors d ON fd.director_id = d.director_id " +
                    "WHERE (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?) " +
                    "AND (? IS NULL OR fg.genre_id = ?) " +
                    "ORDER BY COALESCE(film_likes.likes_count, 0) DESC, f.film_id, g.genre_id " +
                    "LIMIT ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<User> userMapper;
    private final ResultSetExtractor<List<Film>> filmExtractor;

    @Override
    public void addLike(long userId, long filmId) {
        log.info("Adding like from userId={} to filmId={}", userId, filmId);
        int rowsAffected = jdbc.update(ADD_LIKE_QUERY, userId, filmId);
        log.debug("Like added, rows affected: {}", rowsAffected);
    }

    @Override
    public void removeLike(long userId, long filmId) {
        log.info("Removing like from userId={} to filmId={}", userId, filmId);
        int rowsAffected = jdbc.update(REMOVE_LIKE_QUERY, userId, filmId);
        log.debug("Like removed, rows affected: {}", rowsAffected);
    }

    @Override
    public Collection<Film> getUserLikedFilms(long userId) {
        log.info("Fetching liked films for userId={}", userId);
        List<Film> films = jdbc.query(GET_LIKED_FILMS_QUERY, filmExtractor, userId);
        log.debug("Found {} liked films for userId={}", films.size(), userId);
        return films;
    }

    @Override
    public Collection<User> getUsersWhoLikedFilm(long filmId) {
        log.info("Fetching users who liked filmId={}", filmId);
        List<User> users = jdbc.query(GET_USERS_WHO_LIKED_FILM_QUERY, userMapper, filmId);
        log.debug("Found {} users for filmId={}", users.size(), filmId);
        return users;
    }

    @Override
    public Collection<Film> getPopularFilms(long count, Integer genreId, Integer year) {
        log.info("Fetching popular films with count={}, genreId={}, year={}", count, genreId, year);
        List<Film> films = jdbc.query(GET_POPULAR_FILMS_QUERY, filmExtractor, year, year, genreId, genreId, count);
        log.debug("Found {} popular films", films.size());
        return films;
    }
}