package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface LikesStorage {
    void addLike(long userId, long filmId);

    void removeLike(long userId, long filmId);

    Collection<Film> getUserLikedFilms(long userId);

    Collection<User> getUsersWhoLikedFilm(long filmId);

    Collection<Film> getPopularFilms(long count);

    Collection<Film> getPopularFilmsByGenreAndYear(long count, int genreId, int year);

    Collection<Film> getPopularFilmsByGenre(long count, int genreId); // Новый метод

    Collection<Film> getPopularFilmsByYear(long count, int year);     // Новый метод
}
