package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface LikesStorage {

    void saveRating(long userId, long filmId, int rating);

    void removeRating(long userId, long filmId);

    Collection<Film> getUserRatedFilms(long userId);

    Collection<User> getUsersWhoRatedFilm(long filmId);

    Collection<Film> getPopularFilms(long count);

}
