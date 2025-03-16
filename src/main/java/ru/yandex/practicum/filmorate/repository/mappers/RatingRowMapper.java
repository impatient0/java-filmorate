package ru.yandex.practicum.filmorate.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

@Component
@SuppressWarnings("unused")
public class RatingRowMapper implements RowMapper<Rating> {

    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating rating = new Rating();
        rating.setUserId(rs.getLong("user_id"));
        rating.setFilmId(rs.getLong("film_id"));
        rating.setRatingValue(rs.getInt("rating_value"));
        rating.setRatedAt(rs.getTimestamp("rated_at"));
        return rating;
    }
}
