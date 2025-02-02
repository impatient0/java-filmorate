package ru.yandex.practicum.filmorate.repository;

import java.util.Map;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    Film getFilmById(long id);

    long addFilm(Film film);

    Map<Long, Film> getAllFilms();

    Set<Long> getUsersWhoLikedFilm(long filmId);
}
