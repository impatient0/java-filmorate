package ru.yandex.practicum.filmorate.controller;

import java.net.URI;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikesService;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FilmController {

    private final FilmService filmService;
    private final LikesService likesService;

    @GetMapping
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("Request to get all films received.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable long id) {
        log.info("Request to get film with ID {} received.", id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(filmService.getFilmById(id));
    }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film newFilm) {
        log.info("Request to create new film received: {}", newFilm);
        Film createdFilm = filmService.addFilm(newFilm);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(createdFilm.getId()).toUri();
        log.info("New film created with ID {}", createdFilm.getId());
        return ResponseEntity.created(location).contentType(MediaType.APPLICATION_JSON)
            .body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film newFilm) {
        log.info("Request to update film received: {}", newFilm);
        filmService.updateFilm(newFilm);
        log.info("Film with ID {} updated", newFilm.getId());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(newFilm);
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(
        @RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Request to get {} popular films received.", count);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(likesService.getPopularFilms(count));
    }
}
