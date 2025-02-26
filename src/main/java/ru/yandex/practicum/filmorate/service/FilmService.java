package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapperImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final Validator validator;
    private final FilmMapperImpl mapper;

    public Collection<FilmDto> getAllFilms() {
        log.debug("Getting all films");
        return filmStorage.getAllFilms().stream().map(mapper::mapToFilmDto)
            .collect(Collectors.toList());
    }

    public FilmDto getFilmById(long id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        if (film.isEmpty()) {
            log.warn("Getting film failed: film with ID {} not found", id);
            throw new FilmNotFoundException("Error when getting film", id);
        }
        log.debug("Getting film with ID {}", id);
        return mapper.mapToFilmDto(film.get());
    }

    public FilmDto addFilm(NewFilmRequest newFilmRequest) {
        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(newFilmRequest);
        if (!violations.isEmpty()) {
            String violationMessage = violations.iterator().next().getMessage();
            log.warn("Adding film failed: {}", violationMessage);
            throw new FilmValidationException("Error when creating new film", violationMessage);
        }
        Film film = mapper.mapToFilmModel(newFilmRequest);
        long filmId = filmStorage.addFilm(film);
        film.setId(filmId);
        log.debug("Adding new film {}", newFilmRequest);
        return mapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest updateFilmRequest) {
        Film film = filmStorage.getFilmById(updateFilmRequest.getId()).orElseThrow(() -> {
            log.warn("Updating film failed: film with ID {} not found", updateFilmRequest.getId());
            return new FilmNotFoundException("Error when updating film", updateFilmRequest.getId());
        });
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            String violationMessage = violations.iterator().next().getMessage();
            log.warn("Updating film failed: {}", violationMessage);
            throw new FilmValidationException("Error when updating film", violationMessage);
        }
        log.debug("Updating film with ID {}: {}", film.getId(), film);
        film = mapper.updateFilmFields(film, updateFilmRequest);
        filmStorage.updateFilm(film);
        return mapper.mapToFilmDto(film);
    }

}
