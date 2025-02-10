package ru.yandex.practicum.filmorate.repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    Optional<Film> getFilmById(long id);

    long addFilm(Film film);

    void updateFilm(Film film);

    Map<Long, Film> getAllFilms();

    Set<Long> getUsersWhoLikedFilm(long filmId);
}
