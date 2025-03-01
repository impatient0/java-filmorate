package ru.yandex.practicum.filmorate.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
@SuppressWarnings("unused")
public class MpaRatingRowMapper implements RowMapper<MpaRating> {

    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        int ratingId = rs.getInt("mpa_id");
        String ratingName = rs.getString("name");
        return new MpaRating(ratingId, ratingName);
    }
}
