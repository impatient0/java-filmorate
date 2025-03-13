package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import ru.yandex.practicum.filmorate.model.FilmWithRating;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

public interface LikesStorage {

    void addRating(long userId, long filmId, int rating);

    void removeRating(long userId, long filmId);

    List<Rating> getRatingsOfFilm(long filmId);

    List<Rating> getRatingsByUser(long userId);

    List<Rating> getAllRatings();

    List<FilmWithRating> getFilmsRatedByUser(long userId);

    List<User> getUsersWhoRatedFilm(long filmId);

    List<User> getUsersWhoRatedBothFilms(long filmId1, long filmId2);

    List<FilmWithRating> getPopularFilms(long count);

}
