package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final Validator validator;

    public Collection<Film> getAllFilms() {
        log.debug("Getting all films");
        return filmStorage.getAllFilms().values();
    }

    public Film getFilmById(long id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        if (film.isEmpty()) {
            log.warn("Getting film failed: film with ID {} not found", id);
            throw new FilmNotFoundException("Error when getting film", id);
        }
        log.debug("Getting film with ID {}", id);
        return film.get();
    }

    public Film addFilm(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            String violationMessage = violations.iterator().next().getMessage();
            log.warn("Adding film failed: {}", violationMessage);
            throw new FilmValidationException("Error when creating new film", violationMessage);
        }
        long filmId = filmStorage.addFilm(film);
        film.setId(filmId);
        log.debug("Adding new film {}", film);
        return film;
    }

    public void updateFilm(Film film) {
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            log.warn("Updating film failed: film with ID {} not found", film.getId());
            throw new FilmNotFoundException("Error when updating film", film.getId());
        }
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            String violationMessage = violations.iterator().next().getMessage();
            log.warn("Updating film failed: {}", violationMessage);
            throw new FilmValidationException("Error when updating film", violationMessage);
        }
        log.debug("Updating film with ID {}: {}", film.getId(), film);
        filmStorage.updateFilm(film);
    }

}
