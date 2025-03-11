package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    boolean checkFilmExists(long filmId);

    Optional<Film> getFilmById(long filmId);

    long addFilm(Film film);

    void updateFilm(Film film);

    Collection<Film> getAllFilms();

    Optional<List<Film>> getDirectorFilmsBylikes(long directorId, Set<String> params);
}