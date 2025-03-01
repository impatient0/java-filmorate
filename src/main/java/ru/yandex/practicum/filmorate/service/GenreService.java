package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre getGenreById(long id) {
        log.debug("Getting genre with ID {}", id);
        return genreStorage.getGenreById(id).orElseThrow(() -> {
            log.warn("Retrieving genre failed: genre with ID {} not found", id);
            return new GenreNotFoundException("Error when retrieving genre", id);
        });
    }

    public Collection<Genre> getAllGenres() {
        log.debug("Getting all genres");
        return genreStorage.getAllGenres();

    }
}
