package ru.yandex.practicum.filmorate.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class DiffRowMapper implements RowMapper<DiffRow> {

    @Override
    public DiffRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        DiffRow diffRow = new DiffRow();
        diffRow.setFilmId1(rs.getLong("film_id1"));
        diffRow.setFilmId2(rs.getLong("film_id2"));
        diffRow.setDiffValue(rs.getDouble("diff_value"));
        return diffRow;
    }
}
