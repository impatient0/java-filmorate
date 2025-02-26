package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Repository
@Primary
@SuppressWarnings("unused")
public class DbMpaRatingStorage extends DbBaseStorage<MpaRating> implements MpaRatingStorage {

    private static final String GET_ALL_QUERY = "SELECT * FROM mpa_ratings";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";

    public DbMpaRatingStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<MpaRating> getRatingById(long ratingId) {
        return getSingle(GET_BY_ID_QUERY, ratingId);
    }

    @Override
    public Collection<MpaRating> getAllRatings() {
        return List.of();
    }
}