package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface LikesStorage {
    void addLike(long userId, long filmId);

    void removeLike(long userId, long filmId);

    Collection<Film> getUserLikedFilms(long userId);

    Collection<User> getUsersWhoLikedFilm(long filmId);

    Collection<Film> getPopularFilms(long count, Integer genreId, Integer year);
}
