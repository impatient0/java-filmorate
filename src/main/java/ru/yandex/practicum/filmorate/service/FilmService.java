package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.exception.SearchParameterValidationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.SearchType;
import ru.yandex.practicum.filmorate.repository.DirectorStorage;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.GenreStorage;
import ru.yandex.practicum.filmorate.repository.MpaRatingStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final MpaRatingStorage mpaRatingStorage;
    private final GenreStorage genreStorage;
    private final Validator validator;
    private final FilmMapper mapper;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;

    public Collection<FilmDto> getAllFilms() {
        log.debug("Getting all films");
        return filmStorage.getAllFilms().stream().map(mapper::mapToFilmDto).collect(Collectors.toList());
    }

    public FilmDto getFilmById(long filmId) {
        Optional<FilmWithRating> film = filmStorage.getFilmById(filmId);
        if (film.isEmpty()) {
            log.warn("Getting film failed: film with ID {} not found", filmId);
            throw new FilmNotFoundException("Error when getting film", filmId);
        }
        log.debug("Getting film with ID {}", filmId);
        return mapper.mapToFilmDto(film.get());
    }

    public FilmDto addFilm(NewFilmRequest newFilmRequest) {
        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(newFilmRequest);
        if (!violations.isEmpty()) {
            String violationMessage = violations.iterator().next().getMessage();
            log.warn("Adding film failed: {}", violationMessage);
            throw new FilmValidationException("Error when creating new film", violationMessage);
        }
        if (newFilmRequest.getMpa() != null && !mpaRatingStorage.checkMpaRatingExists(newFilmRequest.getMpa().getId())) {
            throw new MpaRatingNotFoundException("Error when creating new film", newFilmRequest.getMpa().getId());
        }
        if (newFilmRequest.getGenres() != null) {
            for (Genre genre : newFilmRequest.getGenres()) {
                if (!genreStorage.checkGenreExists(genre.getId())) {
                    throw new GenreNotFoundException("Error when creating new film", genre.getId());
                }
            }
        }
        if (newFilmRequest.getDirectors() != null) {
            for (Director director : newFilmRequest.getDirectors()) {
                if (!directorStorage.checkDirectorExists(director.getId())) {
                    throw new DirectorNotFoundException("Error when updating film", director.getId());
                }
            }
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
        }).getFilm();
        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateFilmRequest);
        if (!violations.isEmpty()) {
            String violationMessage = violations.iterator().next().getMessage();
            log.warn("Updating film failed: {}", violationMessage);
            throw new FilmValidationException("Error when updating film", violationMessage);
        }
        if (updateFilmRequest.getMpa() != null && !mpaRatingStorage.checkMpaRatingExists(updateFilmRequest.getMpa().getId())) {
            throw new MpaRatingNotFoundException("Error when updating film", updateFilmRequest.getMpa().getId());
        }
        if (updateFilmRequest.getGenres() != null) {
            for (Genre genre : updateFilmRequest.getGenres()) {
                if (!genreStorage.checkGenreExists(genre.getId())) {
                    throw new GenreNotFoundException("Error when updating film", genre.getId());
                }
            }
        }
        if (updateFilmRequest.getDirectors() != null) {
            for (Director director : updateFilmRequest.getDirectors()) {
                if (!directorStorage.checkDirectorExists(director.getId())) {
                    throw new DirectorNotFoundException("Error when updating film", director.getId());
                }
            }
        }
        log.debug("Updating film with ID {}: {}", film.getId(), film);
        film = mapper.updateFilmFields(film, updateFilmRequest);
        filmStorage.updateFilm(film);
        return mapper.mapToFilmDto(film);
    }

    public List<FilmDto> getFilmsByLikes(long directorId, String params) {
        if (directorStorage.getDirectorById(directorId).isEmpty()) {
            log.warn("Deleting director failed: director with ID {} not found", directorId);
            throw new DirectorNotFoundException("Error when deleting director", directorId);
        } else {
            List<FilmWithRating> film = filmStorage.getDirectorFilmsBylikes(directorId, params);
            if (film.isEmpty()) {
                log.warn("Getting films failed: TOP films with director ID {} not found", directorId);
                throw new FilmNotFoundException("Error when getting films", directorId);
            }
            log.debug("Getting films with directorID {}", directorId);
            return film.stream().map(mapper::mapToFilmDto).collect(Collectors.toList());
        }
    }

    public void deleteFilm(long filmId) {
        if (!filmStorage.checkFilmExists(filmId)) {
            log.warn("Deleting film failed: film with ID {} not found", filmId);
            throw new FilmNotFoundException("Error when deleting film", filmId);
        }
        log.debug("Deleting film with ID {}", filmId);
        filmStorage.deleteFilm(filmId);
    }

    public Collection<FilmDto> searchFilms(String query, Set<SearchType> searchTypes) {
        log.debug("Searching for films with '{}' matching '{}'", searchTypes, query);

        if (query == null || query.trim().isEmpty()) {
            log.warn("Empty search query");
            throw new SearchParameterValidationException("Error when searching for films", "Empty search query");
        }

        if (searchTypes == null || searchTypes.isEmpty()) {
            log.warn("Invalid search parameter: {}", searchTypes);
            throw new SearchParameterValidationException("Error when searching for films", "Search types cannot be empty");
        }

        List<FilmWithRating> films = filmStorage.searchFilms(query.toLowerCase(), searchTypes);
        return films.stream().map(mapper::mapToFilmDto).collect(Collectors.toList());
    }

    public Collection<FilmDto> getCommonFilms(long userId, long friendId) {
        if (!userStorage.checkUserExists(userId)) {
            log.warn("User with ID {} not found", userId);
            throw new UserNotFoundException("User not found", userId);
        }
        if (!userStorage.checkUserExists(friendId)) {
            log.warn("Friend with ID {} not found", friendId);
            throw new UserNotFoundException("Friend not found", friendId);
        }

        log.debug("Fetching common films for users {} and {}", userId, friendId);
        List<FilmWithRating> commonFilms = filmStorage.getCommonFilms(userId, friendId);

        log.debug("Fetched common films: {}", commonFilms);

        return commonFilms.stream().map(mapper::mapToFilmDto).collect(Collectors.toList());
    }
}