package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Repository
@SuppressWarnings("unused")
public class DbMpaRatingStorage extends DbBaseStorage<MpaRating> implements MpaRatingStorage {

    private static final String CHECK_EXISTS_QUERY =
        "SELECT EXISTS (SELECT 1 FROM mpa_ratings " + "WHERE mpa_id = ?)";
    private static final String GET_ALL_QUERY = "SELECT * FROM mpa_ratings";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";

    public DbMpaRatingStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public boolean checkRatingExists(long ratingId) {
        return Boolean.TRUE.equals(
            jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, ratingId));
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