package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {

    boolean checkGenreExists(long genreId);

    Optional<Genre> getGenreById(long genreId);

    Collection<Genre> getAllGenres();

}
