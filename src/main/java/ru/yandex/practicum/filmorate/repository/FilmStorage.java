package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;

public interface FilmStorage {

    boolean checkFilmExists(long filmId);

    Optional<FilmWithRating> getFilmById(long filmId);

    long addFilm(Film film);

    void updateFilm(Film film);

    List<FilmWithRating> getAllFilms();

    List<FilmWithRating> getDirectorFilmsBylikes(long directorId, String params);

    List<FilmWithRating> getCommonFilms(long userId, long friendId);

    void deleteFilm(long filmId);

    List<FilmWithRating> searchFilms(String query, String by);
}