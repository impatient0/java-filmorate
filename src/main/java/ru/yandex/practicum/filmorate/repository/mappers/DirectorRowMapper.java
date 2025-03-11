package ru.yandex.practicum.filmorate.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component
@SuppressWarnings("unused")
public class DirectorRowMapper implements RowMapper<Director> {

    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}