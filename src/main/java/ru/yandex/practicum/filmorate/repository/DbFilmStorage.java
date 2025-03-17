package ru.yandex.practicum.filmorate.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@Repository
@Primary
@SuppressWarnings("unused")
public class DbFilmStorage extends DbBaseStorage<Film> implements FilmStorage {

    private static final String CHECK_EXISTS_QUERY = """
        SELECT EXISTS (
            SELECT 1
            FROM films
            WHERE film_id = ?
        )
        """;

    private static final String GET_ALL_QUERY = """
        SELECT
            f.film_id,
            f.name AS film_name,
            f.description,
            f.release_date,
            f.duration,
            m.mpa_id,
            m.name AS mpa_name,
            g.genre_id,
            g.name AS genre_name,
            d.director_id,
            d.name AS director_name
        FROM films AS f
        LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
        LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
        LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
        LEFT JOIN directors AS d ON fd.director_id = d.director_id
        ORDER BY
            f.film_id,
            g.genre_id
        """;

    private static final String GET_BY_ID_QUERY = """
        SELECT
            f.film_id,
            f.name AS film_name,
            f.description,
            f.release_date,
            f.duration,
            m.mpa_id,
            m.name AS mpa_name,
            g.genre_id,
            g.name AS genre_name,
            d.director_id,
            d.name AS director_name
        FROM films AS f
        LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
        LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
        LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
        LEFT JOIN directors AS d ON fd.director_id = d.director_id
        WHERE f.film_id = ?
        """;

    private static final String INSERT_QUERY = """
        INSERT INTO films (
            name,
            description,
            release_date,
            duration,
            mpa_rating_id
        ) VALUES (
            ?,
            ?,
            ?,
            ?,
            ?
        )
        """;

    private static final String UPDATE_QUERY = """
        UPDATE films
        SET
            name = ?,
            description = ?,
            release_date = ?,
            duration = ?,
            mpa_rating_id = ?
        WHERE film_id = ?
        """;

    private static final String DELETE_QUERY = """
        DELETE FROM films
        WHERE film_id = ?
        """;

    private static final String ADD_GENRE_QUERY = """
        INSERT INTO film_genres (
            film_id,
            genre_id
        ) VALUES (
            ?,
            ?
        )
        """;

    private static final String DELETE_GENRES_QUERY = """
        DELETE FROM film_genres
        WHERE film_id = ?
        """;

    private static final String ADD_DIRECTORS_QUERY = """
        INSERT INTO film_directors (
            film_id,
            director_id
        ) VALUES (
            ?,
            ?
        )
        """;

    private static final String GET_BY_DIRECTOR_ID_LIKES_QUERY = """
        WITH film_likes AS (
            SELECT
                film_id,
                COUNT(film_id) AS likes_count
            FROM likes
            GROUP BY film_id
            )
        SELECT
            f.film_id,
            f.name AS film_name,
            f.description,
            f.release_date,
            f.duration,
            m.mpa_id,
            m.name AS mpa_name,
            g.genre_id,
            g.name AS genre_name,
            d.director_id,
            d.name AS director_name,
            film_likes.likes_count
        FROM films AS f
        JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
        LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
        LEFT JOIN film_likes ON f.film_id = film_likes.film_id
        LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
        LEFT JOIN directors AS d ON fd.director_id = d.director_id
        WHERE d.director_id = ?
        ORDER BY
            film_likes.likes_count DESC,
            f.film_id,
            g.genre_id
        """;

    private static final String GET_BY_DIRECTOR_ID_YEAR_QUERY = """
        SELECT
            f.film_id,
            f.name AS film_name,
            f.description,
            f.release_date,
            f.duration,
            m.mpa_id,
            m.name AS mpa_name,
            g.genre_id,
            g.name AS genre_name,
            d.director_id,
            d.name AS director_name
        FROM films AS f
        LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
        LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
        LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
        LEFT JOIN directors AS d ON fd.director_id = d.director_id
        WHERE d.director_id = ?
        ORDER BY
            f.release_date,
            f.film_id DESC
        """;

    private static final String GET_COMMON_FILMS_QUERY = """
        SELECT
            f.film_id,
            f.name AS film_name,
            f.description,
            f.release_date,
            f.duration,
            m.mpa_id,
            m.name AS mpa_name,
            g.genre_id,
            g.name AS genre_name,
            d.director_id,
            d.name AS director_name,
            COUNT(l.user_id) AS like_count
        FROM films AS f
        LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
        LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
        LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
        LEFT JOIN directors AS d ON fd.director_id = d.director_id
        INNER JOIN likes AS l ON f.film_id = l.film_id
        WHERE l.user_id IN (?, ?)
        GROUP BY f.film_id
        HAVING COUNT(DISTINCT l.user_id) = 2
        ORDER BY like_count DESC
        """;

    private static final String SEARCH_QUERY = """
        SELECT
            f.film_id,
            f.name AS film_name,
            f.description,
            f.release_date,
            f.duration,
            m.mpa_id,
            m.name AS mpa_name,
            g.genre_id,
            g.name AS genre_name,
            d.director_id,
            d.name AS director_name,
            COUNT(l.user_id) AS likes_count
        FROM films AS f
        LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
        LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
        LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
        LEFT JOIN directors AS d ON fd.director_id = d.director_id
        LEFT JOIN likes AS l ON f.film_id = l.film_id
        WHERE (%s)
        GROUP BY
            f.film_id,
            f.name,
            f.description,
            f.release_date,
            f.duration,
            m.mpa_id,
            m.name,
            g.genre_id,
            g.name,
            d.director_id,
            d.name
        ORDER BY COUNT(l.user_id) DESC
        """;

    private final ResultSetExtractor<List<Film>> extractor;

    public DbFilmStorage(JdbcTemplate jdbc, ResultSetExtractor<List<Film>> extractor) {
        super(jdbc, null);
        this.extractor = extractor;
    }

    @Override
    public boolean checkFilmExists(long filmId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, filmId));
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        List<Film> resultList = jdbc.query(GET_BY_ID_QUERY, extractor, filmId);
        return CollectionUtils.isEmpty(resultList) ? Optional.empty()
                : Optional.of(resultList.getFirst());
    }

    @Override
    public long addFilm(Film film) {
        long assignedId = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(ADD_GENRE_QUERY, assignedId, genre.getId());
            }
        }
        if (film.getDirector() != null) {
            for (Director director : film.getDirector()) {
                jdbc.update(ADD_DIRECTORS_QUERY, assignedId, director.getId());
            }
        }
        return assignedId;
    }

    @Override
    public void updateFilm(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId());
        jdbc.update(DELETE_GENRES_QUERY, film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(ADD_GENRE_QUERY, film.getId(), genre.getId());
            }
        }
        if (film.getDirector() != null) {
            for (Director director : film.getDirector()) {
                jdbc.update(ADD_DIRECTORS_QUERY, film.getId(), director.getId());
            }
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbc.query(GET_ALL_QUERY, extractor);
    }

    @Override
    public Collection<Film> getDirectorFilmsBylikes(long directorId, Set<String> params) {
        Collection<Film> resultList = List.of();
        if (params.contains("likes"))
            return jdbc.query(GET_BY_DIRECTOR_ID_LIKES_QUERY, extractor, directorId);
        else if (params.contains("year"))
            return jdbc.query(GET_BY_DIRECTOR_ID_YEAR_QUERY, extractor, directorId);
        return resultList;
    }

    @Override
    public void deleteFilm(long filmId) {
        delete(DELETE_QUERY, filmId);
    }

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        return jdbc.query(GET_COMMON_FILMS_QUERY, extractor, userId, friendId);
    }

    ///
    @Override
    public Collection<Film> searchFilms(String query, String by) {
        String[] searchTypes = by.split(",");
        List<String> conditions = new ArrayList<>();

        for (String type : searchTypes) {
            if ("title".equals(type.trim())) {
                conditions.add("LOWER(f.name) LIKE LOWER(?)");
            }
            if ("director".equals(type.trim())) {
                conditions.add("LOWER(d.name) LIKE LOWER(?)");
            }
        }

        String whereClause = String.join(" OR ", conditions);
        String finalQuery = String.format(SEARCH_QUERY, whereClause);

        String searchPattern = "%" + query + "%";
        Object[] params = new Object[conditions.size()];
        Arrays.fill(params, searchPattern);

        return jdbc.query(finalQuery, extractor, params);
    }
}