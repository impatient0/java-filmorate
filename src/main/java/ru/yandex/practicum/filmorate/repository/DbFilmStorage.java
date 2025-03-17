package ru.yandex.practicum.filmorate.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.SearchType;

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
                d.name AS director_name,
                COALESCE(AVG(r.rating_value), 0.0) AS avg_rating
            FROM films AS f
            LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            LEFT JOIN ratings AS r ON f.film_id = r.film_id
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
                d.name AS director_name,
                COALESCE(AVG(r.rating_value), 0.0) AS avg_rating
            FROM films AS f
            LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            LEFT JOIN ratings AS r ON f.film_id = r.film_id
            WHERE f.film_id = ?
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

    private static final String DELETE_DIRECTORS_QUERY = """
            DELETE FROM film_directors
            WHERE film_id = ?
            """;

    private static final String GET_BY_DIRECTOR_ID_RATING_QUERY = """
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
                COALESCE(AVG(r.rating_value), 0.0) AS avg_rating
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id
            JOIN directors AS d ON fd.director_id = d.director_id
            JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            LEFT JOIN ratings AS r ON f.film_id = r.film_id
            WHERE d.director_id = ?
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
            ORDER BY
                avg_rating DESC,
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
                d.name AS director_name,
                COALESCE(AVG(r.rating_value), 0.0) AS avg_rating
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id
            JOIN directors AS d ON fd.director_id = d.director_id
            LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            LEFT JOIN ratings AS r ON f.film_id = r.film_id
            WHERE d.director_id = ?
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
                COALESCE(AVG(r.rating_value), 0.0) AS avg_rating
            FROM films AS f
            LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            INNER JOIN ratings AS r ON f.film_id = r.film_id
            WHERE r.user_id IN (?, ?)
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
            HAVING COUNT(DISTINCT r.user_id) = 2
            ORDER BY avg_rating DESC
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
                COALESCE(AVG(r.rating_value), 0.0) AS avg_rating
            FROM films AS f
            LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_id
            LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            LEFT JOIN ratings AS r ON f.film_id = r.film_id
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
            ORDER BY avg_rating DESC, f.film_id DESC
            """;

    private final ResultSetExtractor<List<FilmWithRating>> extractor;

    public DbFilmStorage(JdbcTemplate jdbc, ResultSetExtractor<List<FilmWithRating>> extractor) {
        super(jdbc, null);
        this.extractor = extractor;
    }

    @Override
    public boolean checkFilmExists(long filmId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, filmId));
    }

    @Override
    public Optional<FilmWithRating> getFilmById(long filmId) {
        List<FilmWithRating> resultList = jdbc.query(GET_BY_ID_QUERY, extractor, filmId);
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
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
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
        jdbc.update(DELETE_DIRECTORS_QUERY, film.getId());
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbc.update(ADD_DIRECTORS_QUERY, film.getId(), director.getId());
            }
        }
    }

    @Override
    public List<FilmWithRating> getAllFilms() {
        return jdbc.query(GET_ALL_QUERY, extractor);
    }

    @Override
    public List<FilmWithRating> getDirectorFilmsBylikes(long directorId, String params) {
        List<FilmWithRating> resultList = List.of();
        if (params.contains("rate") || params.contains("likes"))
            return jdbc.query(GET_BY_DIRECTOR_ID_RATING_QUERY, extractor, directorId);
        else if (params.contains("year"))
            return jdbc.query(GET_BY_DIRECTOR_ID_YEAR_QUERY, extractor, directorId);
        return resultList;
    }

    @Override
    public void deleteFilm(long filmId) {
        delete(DELETE_QUERY, filmId);
    }

    @Override
    public List<FilmWithRating> getCommonFilms(long userId, long friendId) {
        return jdbc.query(GET_COMMON_FILMS_QUERY, extractor, userId, friendId);
    }

    @Override
    public List<FilmWithRating> searchFilms(String query, Set<SearchType> searchTypes) {
        List<String> conditions = new ArrayList<>();

        for (SearchType type : searchTypes) {
            switch (type) {
                case TITLE:
                    conditions.add("LOWER(f.name) LIKE LOWER(?)");
                    break;
                case DIRECTOR:
                    conditions.add("LOWER(d.name) LIKE LOWER(?)");
                    break;
            }
        }

        String whereClause = String.join(" OR ", conditions);
        String finalQuery = String.format(SEARCH_QUERY, whereClause);

        String searchPattern = "%" + query + "%";
        Object[] params = new Object[conditions.size()];
        for (int i = 0; i < params.length; i++) {
            params[i] = searchPattern;
        }

        return jdbc.query(finalQuery, extractor, params);
    }
}