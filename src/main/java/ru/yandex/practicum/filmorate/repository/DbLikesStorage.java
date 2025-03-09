package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
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
public class DbLikesStorage implements LikesStorage {

    private static final String ADD_LIKE_QUERY =
            "INSERT INTO likes (user_id, film_id, liked_at) " + "VALUES (?, ?, CURRENT_TIMESTAMP)";
    private static final String REMOVE_LIKE_QUERY =
            "DELETE FROM likes WHERE user_id = ? AND " + "film_id = ?";
    private static final String GET_LIKED_FILMS_QUERY = "WITH film_likes AS (SELECT film_id FROM "
            + "likes WHERE user_id = ?) SELECT f.film_id, f.name AS film_name, f.description, f"
            + ".release_date, f.duration, m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS "
            + "genre_name FROM films f JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id LEFT JOIN "
            + "film_genres fg ON f.film_id = fg.film_id LEFT JOIN genres g ON fg.genre_id = g"
            + ".genre_id JOIN film_likes ON f.film_id = film_likes.film_id ORDER BY f.film_id, g"
            + ".genre_id";
    private static final String GET_USERS_WHO_LIKED_FILM_QUERY = "SELECT u.* FROM users AS u RIGHT "
            + "JOIN likes AS l ON u.user_id = l.user_id WHERE l.film_id = ?";
    private static final String GET_POPULAR_FILMS_QUERY =
            "WITH film_likes AS (" +
                    "    SELECT film_id, COUNT(film_id) AS likes_count " +
                    "    FROM likes " +
                    "    GROUP BY film_id" +
                    ") " +
                    "SELECT f.film_id, f.name AS film_name, f.description, f.release_date, f.duration, " +
                    "       m.mpa_id, m.name AS mpa_name, " +
                    "       g.genre_id, g.name AS genre_name, " +
                    "       COALESCE(film_likes.likes_count, 0) AS likes_count " +
                    "FROM films f " +
                    "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id " +
                    "LEFT JOIN film_likes ON f.film_id = film_likes.film_id " +
                    "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "WHERE (g.genre_id = ? OR ? = 0) " +
                    "AND EXTRACT(YEAR FROM f.release_date) = ? " +
                    "GROUP BY f.film_id, m.mpa_id, g.genre_id, film_likes.likes_count " +
                    "ORDER BY likes_count DESC, f.film_id " +
                    "LIMIT ?";

    private static final String GET_POPULAR_FILMS_BY_GENRE_AND_YEAR_QUERY =
            """
                    WITH film_likes AS (
                        SELECT film_id, COUNT(film_id) AS likes_count\s
                        FROM likes\s
                        GROUP BY film_id
                    )
                    SELECT\s
                        f.film_id,\s
                        f.name AS film_name,\s
                        f.description,\s
                        f.release_date,\s
                        f.duration,\s
                        m.mpa_id,\s
                        m.name AS mpa_name,\s
                        COALESCE(film_likes.likes_count, 0) AS likes_count\s
                    FROM films f\s
                    JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id\s
                    LEFT JOIN film_likes ON f.film_id = film_likes.film_id\s
                    WHERE EXISTS (
                        SELECT 1\s
                        FROM film_genres fg\s
                        WHERE fg.film_id = f.film_id AND fg.genre_id = ?
                    )\s
                    AND EXTRACT(YEAR FROM f.release_date) = ?
                    ORDER BY likes_count DESC, f.film_id\s
                    LIMIT ?;""";

    private final JdbcTemplate jdbc;
    private final RowMapper<User> userMapper;
    private final ResultSetExtractor<List<Film>> filmExtractor;

    @Override
    public void addLike(long userId, long filmId) {
        jdbc.update(ADD_LIKE_QUERY, userId, filmId);
    }

    @Override
    public void removeLike(long userId, long filmId) {
        jdbc.update(REMOVE_LIKE_QUERY, userId, filmId);
    }

    @Override
    public Collection<Film> getUserLikedFilms(long userId) {
        return jdbc.query(GET_LIKED_FILMS_QUERY, filmExtractor, userId);
    }

    @Override
    public Collection<User> getUsersWhoLikedFilm(long filmId) {
        return jdbc.query(GET_USERS_WHO_LIKED_FILM_QUERY, userMapper, filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(long count) {
        return jdbc.query(GET_POPULAR_FILMS_QUERY, filmExtractor, count);
    }

    @Override
    public Collection<Film> getPopularFilmsByGenreAndYear(long count, int genreId, int year) {
        return jdbc.query(GET_POPULAR_FILMS_BY_GENRE_AND_YEAR_QUERY, filmExtractor, genreId, year, count);
    }
}
