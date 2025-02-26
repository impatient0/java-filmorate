package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public interface GenreStorage {

    boolean checkGenreExists(long genreId);

    Optional<Genre> getGenreDyId(long genreId);

    Collection<Genre> getAllGenres();

}
