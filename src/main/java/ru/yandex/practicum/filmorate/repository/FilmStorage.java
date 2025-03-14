package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    boolean checkFilmExists(long filmId);

    Optional<Film> getFilmById(long filmId);

    long addFilm(Film film);

    void updateFilm(Film film);

    Collection<Film> getAllFilms();

    Collection<Film> getDirectorFilmsBylikes(long directorId, Set<String> params);

    Collection<Film> getCommonFilms(long userId, long friendId);

    void deleteFilm(long filmId);
}