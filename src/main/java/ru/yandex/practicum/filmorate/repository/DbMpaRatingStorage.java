package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Repository
public class DbMpaRatingStorage extends DbBaseStorage<MpaRating> implements MpaRatingStorage {

    private static final String CHECK_EXISTS_QUERY =
        "SELECT EXISTS (SELECT 1 FROM mpa_ratings " + "WHERE mpa_id = ?)";
    private static final String GET_ALL_QUERY = "SELECT * FROM mpa_ratings ORDER BY mpa_id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";

    public DbMpaRatingStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public boolean checkMpaRatingExists(long ratingId) {
        return Boolean.TRUE.equals(
            jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, ratingId));
    }

    @Override
    public Optional<MpaRating> getMpaRatingById(long ratingId) {
        return getSingle(GET_BY_ID_QUERY, ratingId);
    }

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        return getMultiple(GET_ALL_QUERY);
    }
}