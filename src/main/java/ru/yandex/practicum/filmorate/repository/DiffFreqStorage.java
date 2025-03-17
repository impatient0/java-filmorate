package ru.yandex.practicum.filmorate.repository;

import java.util.Map;

public interface DiffFreqStorage {

    void saveDiff(Map<Long, Map<Long, Double>> diff);

    void saveFreq(Map<Long, Map<Long, Integer>> freq);

    Map<Long, Map<Long, Double>> loadDiff();

    Map<Long, Map<Long, Integer>> loadFreq();

}
