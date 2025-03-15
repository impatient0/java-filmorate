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
    private final EventStorage eventStorage;

    public void likeFilm(long userId, long filmId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Liking film failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when liking film", userId);
        }
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            log.warn("Liking film failed: film with ID {} not found", filmId);
            throw new FilmNotFoundException("Error when liking film", filmId);
        }
        log.debug("User with ID {} likes film with ID {}", userId, filmId);
        likesStorage.addLike(userId, filmId);
        eventStorage.insertUserTapeQuery(userId, 1, 2, filmId);
    }

    public void unlikeFilm(long userId, long filmId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Unliking film failed: user with ID {} not found", userId);
            throw new UserNotFoundException("Error when unliking film", userId);
        }
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            log.warn("Unliking film failed: film with ID {} not found", filmId);
            throw new FilmNotFoundException("Error when unliking film", filmId);
        }
        log.debug("User with ID {} unlikes film with ID {}", userId, filmId);
        likesStorage.removeLike(userId, filmId);
        eventStorage.insertUserTapeQuery(userId, 1, 1, filmId);
    }

    public List<FilmDto> getPopularFilms(int count, Integer genreId, Integer year) {
        log.debug("Getting {} most popular films with genreId={} and year={}", count, genreId, year);
        return likesStorage.getPopularFilms(count, genreId, year).stream()
                .map(filmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}