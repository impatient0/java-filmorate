package ru.yandex.practicum.filmorate.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public void likeFilm(long userId, long filmId) {
        userStorage.addLike(userId, filmId);
    }

    public void unlikeFilm(long userId, long filmId) {
        userStorage.removeLike(userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().keySet().stream()
            .sorted(Comparator.comparing(filmId -> filmStorage.getUsersWhoLikedFilm(filmId).size()))
            .limit(count).flatMap(id -> filmStorage.getFilmById(id).stream()).toList();
    }

}
