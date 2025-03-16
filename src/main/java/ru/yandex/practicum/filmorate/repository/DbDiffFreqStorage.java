package ru.yandex.practicum.filmorate.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.repository.mappers.DiffRow;
import ru.yandex.practicum.filmorate.repository.mappers.FreqRow;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DbDiffFreqStorage implements DiffFreqStorage {

    private static final String MERGE_DIFF_QUERY =
        "MERGE INTO item_diff (film_id1, film_id2, diff_value) "
            + "KEY (film_id1, film_id2) VALUES (?, ?, ?)";
    private static final String MERGE_FREQ_QUERY =
        "MERGE INTO item_freq (film_id1, film_id2, freq_value) "
            + "KEY (film_id1, film_id2) VALUES (?, ?, ?)";
    private static final String LOAD_DIFF_QUERY =
        "SELECT film_id1, film_id2, diff_value " + "FROM item_diff";
    private static final String LOAD_FREQ_QUERY =
        "SELECT film_id1, film_id2, freq_value " + "FROM item_freq";

    private final JdbcTemplate jdbc;
    private final RowMapper<DiffRow> diffRowRowMapper;
    private final RowMapper<FreqRow> freqRowRowMapper;

    @Override
    public void saveDiff(Map<Long, Map<Long, Double>> diff) {
        List<Object[]> batchArgs = new ArrayList<>();
        diff.forEach((filmId1, innerMap) -> innerMap.forEach(
            (filmId2, diffValue) -> batchArgs.add(new Object[]{filmId1, filmId2, diffValue})));

        try {
            jdbc.batchUpdate(MERGE_DIFF_QUERY, batchArgs);
        } catch (DataAccessException e) {
            throw new InternalServerException("Failed to save diff.", e);
        }
    }

    @Override
    public void saveFreq(Map<Long, Map<Long, Integer>> freq) {
        List<Object[]> batchArgs = new ArrayList<>();
        freq.forEach((filmId1, innerMap) -> innerMap.forEach(
            (filmId2, freqValue) -> batchArgs.add(new Object[]{filmId1, filmId2, freqValue})));

        try {
            jdbc.batchUpdate(MERGE_FREQ_QUERY, batchArgs);
        } catch (DataAccessException e) {
            throw new InternalServerException("Failed to save freq.", e);
        }
    }

    @Override
    public Map<Long, Map<Long, Double>> loadDiff() {
        Map<Long, Map<Long, Double>> diff = new HashMap<>();
        List<DiffRow> rows = jdbc.query(LOAD_DIFF_QUERY, diffRowRowMapper);
        for (DiffRow row : rows) {
            long filmId1 = row.getFilmId1();
            long filmId2 = row.getFilmId2();
            double diffValue = row.getDiffValue();

            diff.computeIfAbsent(filmId1, k -> new HashMap<>()).put(filmId2, diffValue);
        }
        return diff;
    }

    @Override
    public Map<Long, Map<Long, Integer>> loadFreq() {
        Map<Long, Map<Long, Integer>> freq = new HashMap<>();
        List<FreqRow> rows = jdbc.query(LOAD_FREQ_QUERY, freqRowRowMapper);
        for (FreqRow row : rows) {
            long filmId1 = row.getFilmId1();
            long filmId2 = row.getFilmId2();
            int freqValue = row.getFreqValue();

            freq.computeIfAbsent(filmId1, k -> new HashMap<>()).put(filmId2, freqValue);
        }
        return freq;
    }
}
