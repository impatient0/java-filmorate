package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

@Repository
@Primary
@SuppressWarnings("unused")
public class DbLikesStorage extends DbBaseStorage<Rating> implements LikesStorage {

    private static final String SAVE_RATING_QUERY =
        "INSERT INTO ratings (user_id, film_id, " + "rating_value) VALUES (?, ?, ?)";
    private static final String REMOVE_RATING_QUERY =
        "DELETE FROM ratings WHERE user_id = ? AND " + "film_id = ?";
    private static final String GET_RATED_FILMS_QUERY = "WITH film_ratings AS (SELECT film_id FROM "
        + "ratings WHERE user_id = ?) SELECT f.film_id, f.name AS film_name, f.description, f"
        + ".release_date, f.duration, m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS "
        + "genre_name FROM films f JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id LEFT JOIN "
        + "film_genres fg ON f.film_id = fg.film_id LEFT JOIN genres g ON fg.genre_id = g"
        + ".genre_id JOIN film_ratings ON f.film_id = film_ratings.film_id ORDER BY f.film_id, g"
        + ".genre_id";
    private static final String GET_USERS_WHO_RATED_FILM_QUERY = "SELECT u.* FROM users AS u RIGHT "
        + "JOIN ratings AS r ON u.user_id = r.user_id WHERE r.film_id = ?";
    private static final String GET_USERS_WHO_RATED_BOTH_FILMS_QUERY = "WITH UsersWhoRatedFilm1 "
        + "AS (SELECT user_id FROM ratings WHERE film_id = ?), UsersWhoRatedFilm2 AS (SELECT "
        + "user_id FROM ratings WHERE film_id = ?) SELECT u.* FROM users AS u WHERE u.user_id IN "
        + "(SELECT user_id FROM UsersWhoRatedFilm1 INTERSECT SELECT user_id FROM "
        + "UsersWhoRatedFilm2)";
    private static final String GET_POPULAR_FILMS_QUERY = "WITH film_ratings AS (SELECT film_id, "
        + "COUNT(film_id) AS ratings_count FROM ratings GROUP BY film_id) SELECT f.film_id, f"
        + ".name AS film_name, f.description, f.release_date, f.duration, m.mpa_id, m.name AS "
        + "mpa_name, g.genre_id, g.name AS genre_name, film_ratings.ratings_count FROM films f "
        + "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id LEFT JOIN film_genres fg ON f.film_id"
        + " = fg.film_id LEFT JOIN genres g ON fg.genre_id = g.genre_id LEFT JOIN film_ratings ON f"
        + ".film_id = film_ratings.film_id ORDER BY film_ratings.ratings_count DESC, f.film_id, g"
        + ".genre_id LIMIT ?";
    private static final String GET_ALL_RATINGS_QUERY = "SELECT * FROM ratings";

    private final RowMapper<User> userMapper;
    private final ResultSetExtractor<List<Film>> filmExtractor;

    protected DbLikesStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper,
        RowMapper<User> userMapper, ResultSetExtractor<List<Film>> filmExtractor) {
        super(jdbc, mapper);
        this.userMapper = userMapper;
        this.filmExtractor = filmExtractor;
    }

    @Override
    public void addRating(long userId, long filmId, int rating) {
        jdbc.update(SAVE_RATING_QUERY, userId, filmId, rating);
    }

    @Override
    public void removeRating(long userId, long filmId) {
        jdbc.update(REMOVE_RATING_QUERY, userId, filmId);
    }

    @Override
    public List<Rating> getRatingsOfFilm(long filmId) {
        return getMultiple(GET_ALL_RATINGS_QUERY + " WHERE film_id = ?", filmId);
    }

    @Override
    public List<Rating> getRatingsByUser(long userId) {
        return getMultiple(GET_ALL_RATINGS_QUERY + " WHERE user_id = ?", userId);
    }

    @Override
    public List<Rating> getAllRatings() {
        return getMultiple(GET_ALL_RATINGS_QUERY);
    }

    @Override
    public List<Film> getFilmsRatedByUser(long userId) {
        return jdbc.query(GET_RATED_FILMS_QUERY, filmExtractor, userId);
    }

    @Override
    public List<User> getUsersWhoRatedFilm(long filmId) {
        return jdbc.query(GET_USERS_WHO_RATED_FILM_QUERY, userMapper, filmId);
    }

    @Override
    public List<User> getUsersWhoRatedBothFilms(long filmId1, long filmId2) {
        return jdbc.query(GET_USERS_WHO_RATED_BOTH_FILMS_QUERY, userMapper, filmId1, filmId2);
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        return jdbc.query(GET_POPULAR_FILMS_QUERY, filmExtractor, count);
    }
}
