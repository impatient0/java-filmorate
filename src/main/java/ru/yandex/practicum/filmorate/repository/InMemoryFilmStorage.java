package ru.yandex.practicum.filmorate.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public long addFilm(Film film) {
        film.setId(nextId.getAndIncrement());
        films.put(film.getId(), film);
        return film.getId();
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        return new HashMap<>(films);
    }

    @Override
    public Set<Long> getUsersWhoLikedFilm(long filmId) {
        return userStorage.getAllUsers().keySet().stream()
            .filter(userId -> userStorage.getUserLikedFilms(userId).contains(filmId))
            .collect(java.util.stream.Collectors.toSet());
    }
}
