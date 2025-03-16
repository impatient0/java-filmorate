package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;

public interface FilmStorage {

    boolean checkFilmExists(long filmId);

    Optional<FilmWithRating> getFilmById(long filmId);

    long addFilm(Film film);

    void updateFilm(Film film);

    List<FilmWithRating> getAllFilms();

    Collection<Film> getDirectorFilmsBylikes(long directorId, Set<String> params);

    Collection<Film> getCommonFilms(long userId, long friendId);

    void deleteFilm(long filmId);

    Collection<Film> searchFilms(String query, String by);
}