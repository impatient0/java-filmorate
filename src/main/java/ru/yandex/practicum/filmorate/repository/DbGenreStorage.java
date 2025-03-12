package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

@Repository
public class DbGenreStorage extends DbBaseStorage<Genre> implements GenreStorage {

    private static final String CHECK_EXISTS_QUERY =
        "SELECT EXISTS (SELECT 1 FROM genres WHERE " + "genre_id = ?)";
    private static final String GET_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";

    protected DbGenreStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public boolean checkGenreExists(long genreId) {
        Integer count = jdbc.queryForObject(CHECK_EXISTS_QUERY, Integer.class, genreId);
        return count != null && count > 0;
    }

    @Override
    public Optional<Genre> getGenreById(long genreId) {
        return getSingle(GET_BY_ID_QUERY, genreId);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return getMultiple(GET_ALL_QUERY);
    }

}
