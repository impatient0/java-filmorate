package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.repository.DbDiffFreqStorage;
import ru.yandex.practicum.filmorate.repository.mappers.DiffRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.FreqRowMapper;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbDiffFreqStorage.class, DiffRowMapper.class, FreqRowMapper.class})
public class DbDiffFreqStorageTest {

    private static final String DELETE_ITEM_DIFF_QUERY = "DELETE FROM item_diff";
    private static final String DELETE_ITEM_FREQ_QUERY = "DELETE FROM item_freq";
    private static final String SELECT_ALL_DIFF_QUERY = "SELECT * FROM item_diff";
    private static final String SELECT_ALL_FREQ_QUERY = "SELECT * FROM item_freq";
    private static final String SELECT_DIFF_BY_DI_QUERY = "SELECT * FROM item_diff WHERE film_id1"
        + " = ? AND film_id2 = ?";
    private static final String SELECT_FREQ_BY_ID_QUERY = "SELECT * FROM item_freq WHERE film_id1"
        + " = ? AND film_id2 = ?";

    private final DbDiffFreqStorage diffFreqStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute(DELETE_ITEM_DIFF_QUERY);
        jdbc.execute(DELETE_ITEM_FREQ_QUERY);
    }

    @AfterEach
    void tearDown() {
        jdbc.execute(DELETE_ITEM_DIFF_QUERY);
        jdbc.execute(DELETE_ITEM_FREQ_QUERY);
    }

    @Test
    void testSaveDiff() {
        Map<Long, Map<Long, Double>> diff = new HashMap<>();
        Map<Long, Double> innerMap1 = new HashMap<>();
        innerMap1.put(2L, 0.5);
        innerMap1.put(3L, 1.0);
        Map<Long, Double> innerMap2 = new HashMap<>();
        innerMap2.put(1L, -0.5);
        diff.put(1L, innerMap1);
        diff.put(2L, innerMap2);

        diffFreqStorage.saveDiff(diff);

        assertThat(jdbc.queryForList(SELECT_ALL_DIFF_QUERY)).hasSize(3);
        assertThat(jdbc.queryForMap(SELECT_DIFF_BY_DI_QUERY, 1, 2)).containsEntry("diff_value",
            0.5);
        assertThat(jdbc.queryForMap(SELECT_DIFF_BY_DI_QUERY, 1, 3)).containsEntry("diff_value",
            1.0);
        assertThat(jdbc.queryForMap(SELECT_DIFF_BY_DI_QUERY, 2, 1)).containsEntry("diff_value",
            -0.5);
    }

    @Test
    void testSaveFreq() {
        Map<Long, Map<Long, Integer>> freq = new HashMap<>();
        Map<Long, Integer> innerMap1 = new HashMap<>();
        innerMap1.put(2L, 5);
        innerMap1.put(3L, 10);
        Map<Long, Integer> innerMap2 = new HashMap<>();
        innerMap2.put(1L, 5);
        freq.put(1L, innerMap1);
        freq.put(2L, innerMap2);

        diffFreqStorage.saveFreq(freq);

        assertThat(jdbc.queryForList(SELECT_ALL_FREQ_QUERY)).hasSize(3);
        assertThat(jdbc.queryForMap(SELECT_FREQ_BY_ID_QUERY, 1, 2)).containsEntry("freq_value", 5);
        assertThat(jdbc.queryForMap(SELECT_FREQ_BY_ID_QUERY, 1, 3)).containsEntry("freq_value", 10);
        assertThat(jdbc.queryForMap(SELECT_FREQ_BY_ID_QUERY, 2, 1)).containsEntry("freq_value", 5);
    }

    @Test
    void testLoadDiff() {
        Map<Long, Map<Long, Double>> diff = new HashMap<>();
        Map<Long, Double> innerMap1 = new HashMap<>();
        innerMap1.put(2L, 0.5);
        innerMap1.put(3L, 1.0);
        Map<Long, Double> innerMap2 = new HashMap<>();
        innerMap2.put(1L, -0.5);
        diff.put(1L, innerMap1);
        diff.put(2L, innerMap2);
        diffFreqStorage.saveDiff(diff);

        Map<Long, Map<Long, Double>> loadedDiff = diffFreqStorage.loadDiff();

        assertThat(loadedDiff).isEqualTo(diff);
    }

    @Test
    void testLoadFreq() {
        Map<Long, Map<Long, Integer>> freq = new HashMap<>();
        Map<Long, Integer> innerMap1 = new HashMap<>();
        innerMap1.put(2L, 5);
        innerMap1.put(3L, 10);
        Map<Long, Integer> innerMap2 = new HashMap<>();
        innerMap2.put(1L, 5);
        freq.put(1L, innerMap1);
        freq.put(2L, innerMap2);
        diffFreqStorage.saveFreq(freq);

        Map<Long, Map<Long, Integer>> loadedFreq = diffFreqStorage.loadFreq();

        assertThat(loadedFreq).isEqualTo(freq);
    }
}
