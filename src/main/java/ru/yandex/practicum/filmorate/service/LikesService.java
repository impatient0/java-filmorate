package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.repository.EventStorage;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.LikesStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikesService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private final FilmMapper filmMapper;
    private final RecommendationService recommendationService;
    private final EventStorage eventStorage;

    public void rateFilm(long userId, long filmId, double ratingValue) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Rating film failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when rating film", userId);
        }
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            log.warn("Rating film failed: film with ID {} not found", filmId);
            throw new FilmNotFoundException("Error when rating film", filmId);
        }
        log.debug("User with ID {} gives film with ID {} a rating of {}", userId, filmId,
            ratingValue);
        likesStorage.addRating(userId, filmId, ratingValue);
        recommendationService.updateDiffAndFreq(userId, filmId, ratingValue);
        eventStorage.insertUserFeedQuery(userId, 1, 2, filmId);
    }

    public void unrateFilm(long userId, long filmId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Unliking film failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when unrating film", userId);
        }
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            log.warn("Unrating film failed: film with ID {} not found", filmId);
            throw new FilmNotFoundException("Error when unrating film", filmId);
        }
        log.debug("User with ID {} unrates film with ID {}", userId, filmId);
        likesStorage.removeRating(userId, filmId);
        recommendationService.updateDiffAndFreq(userId, filmId, 0);
        eventStorage.insertUserFeedQuery(userId, 1, 1, filmId);
    }

    public List<FilmDto> getPopularFilms(int count, Integer genreId, Integer year) {
        log.debug("Getting {} most popular films with genreId={} and year={}", count, genreId, year);
        return likesStorage.getPopularFilms(count, genreId, year).stream()
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}