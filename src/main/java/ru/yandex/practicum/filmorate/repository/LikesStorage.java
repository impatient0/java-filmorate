package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

public interface LikesStorage {

    void addRating(long userId, long filmId, int rating);

    void removeRating(long userId, long filmId);

    Collection<Rating> getRatingsOfFilm(long filmId);

    Collection<Rating> getRatingsByUser(long userId);

    Collection<Rating> getAllRatings();

    Collection<Film> getFilmsRatedByUser(long userId);

    Collection<User> getUsersWhoRatedFilm(long filmId);

    Collection<User> getUsersWhoRatedBothFilms(long filmId1, long filmId2);

    Collection<Film> getPopularFilms(long count);


}
