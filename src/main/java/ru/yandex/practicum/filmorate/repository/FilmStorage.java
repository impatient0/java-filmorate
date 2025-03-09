package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    boolean checkFilmExists(long filmId);

    Optional<Film> getFilmById(long filmId);

    long addFilm(Film film);

    void updateFilm(Film film);

    Collection<Film> getAllFilms();

    void deleteFilm(long filmId);
}
