package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@Repository
@Primary
public class DbFilmStorage extends DbBaseStorage<Film> implements FilmStorage {

    private static final String CHECK_EXISTS_QUERY =
        "SELECT EXISTS (SELECT 1 FROM films WHERE " + "film_id = ?)";
    private static final String GET_ALL_QUERY =
        "SELECT f.film_id, " + "f.name AS film_name, f.description, f.release_date, f.duration, "
            + "m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS genre_name "
            + "FROM films f LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id "
            + "LEFT JOIN film_genres fg ON f.film_id = fg.film_id "
            + "LEFT JOIN genres g ON fg.genre_id = g.genre_id ORDER BY f.film_id, g.genre_id";
    private static final String GET_BY_ID_QUERY =
        "SELECT f.film_id, " + "f.name AS film_name, f.description, f.release_date, f.duration, "
            + "m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS genre_name "
            + "FROM films f LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id "
            + "LEFT JOIN film_genres fg ON f.film_id = fg.film_id "
            + "LEFT JOIN genres g ON fg.genre_id = g.genre_id WHERE f.film_id = ?";
    private static final String INSERT_QUERY =
        "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_GENRE_QUERY =
        "INSERT INTO film_genres (film_id, genre_id)" + " VALUES (?, ?)";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String UPDATE_QUERY =
        "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, "
            + "mpa_rating_id = ? " + "WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";

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
        long assignedId = insert(INSERT_QUERY, film.getName(), film.getDescription(),
            film.getReleaseDate(), film.getDuration(),
            film.getMpa() == null ? null : film.getMpa().getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(ADD_GENRE_QUERY, assignedId, genre.getId());
            }
        }
        return assignedId;
    }

    @Override
    public void updateFilm(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
            film.getDuration(), film.getMpa() == null ? null : film.getMpa().getId(), film.getId());
        jdbc.update(DELETE_GENRES_QUERY, film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(ADD_GENRE_QUERY, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbc.query(GET_ALL_QUERY, extractor);
    }

    @Override
    public void deleteFilm(long filmId) {
        delete(DELETE_QUERY, filmId);
    }

}
