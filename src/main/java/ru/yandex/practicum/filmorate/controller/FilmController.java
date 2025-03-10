package ru.yandex.practicum.filmorate.controller;

import java.net.URI;
import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
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
    public ResponseEntity<Collection<FilmDto>> getAllFilms() {
        log.info("Request to get all films received.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> getFilmById(@PathVariable long id) {
        log.info("Request to get film with ID {} received.", id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(filmService.getFilmById(id));
    }

    @PostMapping
    public ResponseEntity<FilmDto> create(@RequestBody NewFilmRequest newFilmRequest) {
        log.info("Request to create new film received: {}", newFilmRequest);
        FilmDto createdFilm = filmService.addFilm(newFilmRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdFilm.getId()).toUri();
        log.info("New film created with ID {}", createdFilm.getId());
        return ResponseEntity.created(location).contentType(MediaType.APPLICATION_JSON)
                .body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<FilmDto> update(@RequestBody UpdateFilmRequest updateFilmRequest) {
        log.info("Request to update film received: {}", updateFilmRequest);
        FilmDto updatedFilm = filmService.updateFilm(updateFilmRequest);
        log.info("Film with ID {} updated", updateFilmRequest.getId());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updatedFilm);
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<FilmDto>> getPopularFilms(
            @RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Request to get {} popular films received.", count);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(likesService.getPopularFilms(count));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable long id) {
        log.info("Request to delete film with ID {} received.", id);
        filmService.deleteFilm(id);
        log.info("Film with ID {} deleted.", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/films/popular")
    public ResponseEntity<Collection<FilmDto>> getPopularFilmsByGenreAndYear(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        log.info("Request to get {} popular films for genre ID {} and year {} received.", count, genreId, year);
        Collection<FilmDto> films;
        if (genreId != null && year != null) {
            films = likesService.getPopularFilmsByGenreAndYear(count, genreId, year);
        } else if (genreId != null) {
            films = likesService.getPopularFilmsByGenre(count, genreId);
        } else if (year != null) {
            films = likesService.getPopularFilmsByYear(count, year);
        } else {
            films = likesService.getPopularFilms(count);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(films);
    }


}
