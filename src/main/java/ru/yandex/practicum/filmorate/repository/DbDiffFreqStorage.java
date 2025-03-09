package ru.yandex.practicum.filmorate.repository;

import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("usused")
public class DbDiffFreqStorage implements DiffFreqStorage {

    @Override
    public void saveDiff(Map<Long, Map<Long, Double>> diff) {

    }

    @Override
    public void saveFeq(Map<Long, Map<Long, Integer>> freq) {

    }

    @Override
    public Map<Long, Map<Long, Double>> loadDiff() {
        return Map.of();
    }

    @Override
    public Map<Long, Map<Long, Integer>> loadFreq() {
        return Map.of();
    }
}
