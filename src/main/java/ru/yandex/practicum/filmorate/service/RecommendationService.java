package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.DiffFreqStorage;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.LikesStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final LikesStorage likesStorage;
    private final DiffFreqStorage diffFreqStorage;
    private final FilmStorage filmStorage;
    private final FilmMapper filmMapper;

    public void calculateDiffAndFreq() {
        log.info("Calculating diff and freq matrices");
        List<Rating> allRatings = likesStorage.getAllRatings();
        Map<Long, Map<Long, Double>> diff = new HashMap<>();
        Map<Long, Map<Long, Integer>> freq = new HashMap<>();

        // Group ratings by user
        Map<Long, Map<Long, Double>> userRatings = new HashMap<>();
        for (Rating rating : allRatings) {
            userRatings.computeIfAbsent(rating.getUserId(), k -> new HashMap<>())
                .put(rating.getFilmId(), (double) rating.getRatingValue());
        }

        // Iterate through all pairs of films
        for (Map<Long, Double> user1Ratings : userRatings.values()) {
            List<Map.Entry<Long, Double>> ratingsList = new ArrayList<>(user1Ratings.entrySet());
            // Ensure each pair is iterated through once
            for (int i = 0; i < ratingsList.size(); i++) {
                Long filmId1 = ratingsList.get(i).getKey();
                Double rating1 = ratingsList.get(i).getValue();
                for (int j = i + 1; j < ratingsList.size(); j++) {
                    Long filmId2 = ratingsList.get(j).getKey();
                    Double rating2 = ratingsList.get(j).getValue();

                    // Update diff and freq for both directions
                    updateDiffAndFreqForPair(diff, freq, filmId1, filmId2, rating1, rating2);
                    updateDiffAndFreqForPair(diff, freq, filmId2, filmId1, rating2, rating1);
                }
            }
        }

        // Divide diff by freq
        diff.forEach((filmId1, innerMap) -> innerMap.replaceAll((filmId2, diffValue) -> {
            Integer frequency = freq.get(filmId1).get(filmId2);
            return diffValue / frequency;
        }));

        // Save matrices to the database
        diffFreqStorage.saveDiff(diff);
        diffFreqStorage.saveFreq(freq);
        log.info("Diff and freq matrices calculated and saved");
    }

    private void updateDiffAndFreqForPair(Map<Long, Map<Long, Double>> diff,
        Map<Long, Map<Long, Integer>> freq, Long filmId1, Long filmId2, Double rating1,
        Double rating2) {
        // Update diff
        diff.computeIfAbsent(filmId1, k -> new HashMap<>()).compute(filmId2, (k, v) -> {
            if (v == null) {
                return rating1 - rating2;
            } else {
                return v + rating1 - rating2;
            }
        });
        // Update freq
        freq.computeIfAbsent(filmId1, k -> new HashMap<>()).compute(filmId2, (k, v) -> {
            if (v == null) {
                return 1;
            } else {
                return v + 1;
            }
        });
    }

    public void updateDiffAndFreq(long userId, long filmId, int ratingValue) {
        log.info("Updating diff and freq matrices for user {} and film {}", userId, filmId);
        // Load existing diff and freq data
        Map<Long, Map<Long, Double>> diff = diffFreqStorage.loadDiff();
        Map<Long, Map<Long, Integer>> freq = diffFreqStorage.loadFreq();

        // Get the list of all films rated by the current user
        List<Rating> userRatings = likesStorage.getRatingsByUser(userId);

        // Update diff and freq for the new rating
        for (Rating userRating : userRatings) {
            long otherFilmId = userRating.getFilmId();
            int otherRating = userRating.getRatingValue();

            // Skip comparing a film to itself
            if (filmId == otherFilmId) {
                continue;
            }
            // Update for both directions
            updateDiffAndFreqForPair(diff, freq, filmId, otherFilmId, (double) ratingValue,
                (double) otherRating);
            updateDiffAndFreqForPair(diff, freq, otherFilmId, filmId, (double) otherRating,
                (double) ratingValue);
        }

        // Save the updated matrices
        diffFreqStorage.saveDiff(diff);
        diffFreqStorage.saveFreq(freq);
        log.info("Diff and freq matrices updated for user {} and film {}", userId, filmId);
    }

    public Collection<FilmDto> getRecommendations(long userId) {
        log.info("Getting recommendations for user {}", userId);
        // Load diff and freq
        Map<Long, Map<Long, Double>> diff = diffFreqStorage.loadDiff();

        // Load user ratings
        List<Rating> userRatings = likesStorage.getRatingsByUser(userId);
        Map<Long, Double> userRatingMap = userRatings.stream()
            .collect(Collectors.toMap(Rating::getFilmId, r -> (double) r.getRatingValue()));

        // Calculate predicted ratings
        Map<Long, Double> predictedRatings = new HashMap<>();
        for (Map.Entry<Long, Double> entry : userRatingMap.entrySet()) {
            long filmId = entry.getKey();
            double rating = entry.getValue();
            Map<Long, Double> filmDiff = diff.get(filmId);
            if (filmDiff != null) {
                int[] predictionsCount = {0};
                for (Map.Entry<Long, Double> diffEntry : filmDiff.entrySet()) {
                    long otherFilmId = diffEntry.getKey();
                    double diffValue = diffEntry.getValue();
                    double otherFilmRating = rating + diffValue;
                    // Skip already rated films
                    if (!userRatingMap.containsKey(otherFilmId)) {
                        predictedRatings.compute(otherFilmId, (k, v) -> {
                            if (v == null) {
                                predictionsCount[0]++;
                                return otherFilmRating;
                            } else {
                                return (v * predictionsCount[0] + otherFilmRating)
                                    / (++predictionsCount[0]);
                            }
                        });
                    }
                }
            }
        }

        // Recommend top films
        List<Film> recommendedFilms = predictedRatings.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed()).map(Map.Entry::getKey)
            .map(filmStorage::getFilmById).flatMap(Optional::stream).limit(10)
            .collect(Collectors.toList());

        log.info("Recommended films: {}", recommendedFilms);
        return recommendedFilms.stream().map(filmMapper::mapToFilmDto).toList();
    }
}
