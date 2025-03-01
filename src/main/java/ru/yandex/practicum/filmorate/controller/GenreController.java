package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

@RestController
@RequestMapping("/genres")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<Collection<Genre>> getAllGenres() {
        log.info("Request to get all genres received.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable long id) {
        log.info("Request to get genre with ID {} received.", id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(genreService.getGenreById(id));
    }
}
