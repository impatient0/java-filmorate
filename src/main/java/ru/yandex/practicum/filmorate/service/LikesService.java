package ru.yandex.practicum.filmorate.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikesService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

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
        userStorage.addLike(userId, filmId);
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
        userStorage.removeLike(userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.debug("Getting {} most popular films", count);
        return filmStorage.getAllFilms().keySet().stream()
            .sorted(Comparator.comparing(
                (Long filmId) -> filmStorage.getUsersWhoLikedFilm(filmId).size()).reversed())
            .limit(count).flatMap(id -> filmStorage.getFilmById(id).stream()).toList();
    }
}
