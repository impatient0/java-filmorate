package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

@Repository
@SuppressWarnings("unused")
public class DbGenreStorage extends DbBaseStorage<Genre> implements GenreStorage {

    private static final String CHECK_EXISTS_QUERY =
        "SELECT EXISTS (SELECT 1 FROM genres WHERE " + "genre_id = ?)";
    private static final String GET_ALL_QUERY = "SELECT * FROM genres";
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
    public Optional<Genre> getGenreDyId(long genreId) {
        List<Genre> genres = jdbc.query(GET_BY_ID_QUERY, mapper, genreId);
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.getFirst());
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbc.query(GET_ALL_QUERY, mapper);
    }

}
