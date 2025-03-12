package ru.yandex.practicum.filmorate.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class FreqRowMapper implements RowMapper<FreqRow> {

    @Override
    public FreqRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        FreqRow freqRow = new FreqRow();
        freqRow.setFilmId1(rs.getLong("film_id1"));
        freqRow.setFilmId2(rs.getLong("film_id2"));
        freqRow.setFreqValue(rs.getInt("freq_value"));
        return freqRow;
    }
}
