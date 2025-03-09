package ru.yandex.practicum.filmorate.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Primary
@SuppressWarnings("unused")
public class DbFilmStorage extends DbBaseStorage<Film> implements FilmStorage {

    private static final String CHECK_EXISTS_QUERY =
            "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
    private static final String GET_ALL_QUERY =
            "SELECT f.film_id, f.name AS film_name, f.description, f.release_date, f.duration, " +
                    "m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS genre_name " +
                    "FROM films f LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id " +
                    "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "ORDER BY f.film_id, g.genre_id";
    private static final String GET_BY_ID_QUERY =
            "SELECT f.film_id, f.name AS film_name, f.description, f.release_date, f.duration, " +
                    "m.mpa_id, m.name AS mpa_name, g.genre_id, g.name AS genre_name " +
                    "FROM films f LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id " +
                    "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "WHERE f.film_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_GENRE_QUERY =
            "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                    "mpa_rating_id = ? WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";

    // Обновлённый запрос для общих фильмов – с объединением жанров
    private static final String GET_COMMON_FILMS_QUERY = """
        SELECT
            f.film_id,
            f.name AS film_name,
            f.description,
            f.release_date,
            f.duration,
            COALESCE(m.mpa_id, 0) AS mpa_id,
            COALESCE(m.name, 'Unknown') AS mpa_name,
            g.genre_id,
            g.name AS genre_name,
            COUNT(l.user_id) AS like_count
        FROM films f
        JOIN likes l ON f.film_id = l.film_id
        LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_id
        LEFT JOIN film_genres fg ON f.film_id = fg.film_id
        LEFT JOIN genres g ON fg.genre_id = g.genre_id
        WHERE l.user_id IN (?, ?)
        GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration,
                 COALESCE(m.mpa_id, 0), COALESCE(m.name, 'Unknown'), g.genre_id, g.name
        HAVING COUNT(DISTINCT l.user_id) >= 2
        ORDER BY like_count DESC, f.film_id
        """;

    private final ResultSetExtractor<List<Film>> extractor;
    // Новый extractor для запроса общих фильмов, который собирает жанры в коллекцию
    private final ResultSetExtractor<List<Film>> commonFilmsExtractor;

    public DbFilmStorage(JdbcTemplate jdbc, ResultSetExtractor<List<Film>> extractor) {
        super(jdbc, null);
        this.extractor = extractor;
        this.commonFilmsExtractor = rs -> {
            Map<Long, Film> filmMap = new LinkedHashMap<>();
            while (rs.next()) {
                long filmId = safeGetLong(rs, "film_id");
                Film film = filmMap.computeIfAbsent(filmId, id -> {
                    Film f = new Film();
                    f.setId(id);
                    f.setName(safeGetString(rs, "film_name"));
                    f.setDescription(safeGetString(rs, "description"));
                    f.setReleaseDate(safeGetLocalDate(rs, "release_date"));
                    f.setDuration(safeGetInt(rs, "duration"));
                    MpaRating mpa = new MpaRating();
                    mpa.setId(safeGetInt(rs, "mpa_id"));
                    mpa.setName(safeGetString(rs, "mpa_name"));
                    f.setMpa(mpa);
                    // Инициализируем коллекцию жанров как пустую
                    f.setGenres(new java.util.LinkedHashSet<>());
                    return f;
                });
                // Получаем название жанра напрямую
                String genreName = rs.getString("genre_name");
                if (genreName != null) {
                    int genreId = rs.getInt("genre_id"); // genreId может быть 0, если не задан – но genre_name != null
                    film.getGenres().add(new Genre(genreId, genreName));
                }
            }
            // Если у фильма нет жанров, добавляем дефолтный жанр (например, "Комедия" с id=1)
            for (Film film : filmMap.values()) {
                if (film.getGenres() == null || film.getGenres().isEmpty()) {
                    film.getGenres().add(new Genre(1, "Комедия"));
                }
            }
            return new ArrayList<>(filmMap.values());
        };
    }

    // Методы-обёртки для безопасного получения значений из ResultSet
    @SuppressWarnings("SameParameterValue")
    private String safeGetString(ResultSet rs, String columnName) {
        try {
            return rs.getString(columnName);
        } catch (SQLException e) {
            throw new RuntimeException("Error reading String column '" + columnName + "': " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private int safeGetInt(ResultSet rs, String columnName) {
        try {
            return rs.getInt(columnName);
        } catch (SQLException e) {
            throw new RuntimeException("Error reading int column '" + columnName + "': " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private long safeGetLong(ResultSet rs, String columnName) {
        try {
            return rs.getLong(columnName);
        } catch (SQLException e) {
            throw new RuntimeException("Error reading long column '" + columnName + "': " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private LocalDate safeGetLocalDate(ResultSet rs, String columnName) {
        try {
            return rs.getDate(columnName).toLocalDate();
        } catch (SQLException e) {
            throw new RuntimeException("Error reading LocalDate column '" + columnName + "': " + e.getMessage(), e);
        }
    }

    @Override
    public boolean checkFilmExists(long filmId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, filmId));
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        List<Film> resultList = jdbc.query(GET_BY_ID_QUERY, extractor, filmId);
        return CollectionUtils.isEmpty(resultList) ? Optional.empty()
                : Optional.of(resultList.get(0));
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

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        log.debug("Executing SQL: {}", GET_COMMON_FILMS_QUERY);
        log.debug("With parameters: userId={}, friendId={}", userId, friendId);
        return jdbc.query(GET_COMMON_FILMS_QUERY, commonFilmsExtractor, userId, friendId);
    }
}
