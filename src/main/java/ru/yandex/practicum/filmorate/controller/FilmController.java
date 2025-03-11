package ru.yandex.practicum.filmorate.controller;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

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

    @GetMapping(value = "/director/{directorId}", params = {"sortBy"})
    public ResponseEntity<Collection<FilmDto>> getFilmById(@PathVariable long directorId, @RequestParam(value = "sortBy") Set<String> params) {
        log.info("Request to get films with director ID {} received.", directorId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(filmService.getFilmsByLikes(directorId, params));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable long id) {
        log.info("Request to delete film with ID {} received.", id);
        filmService.deleteFilm(id);
        log.info("Film with ID {} deleted.", id);
        return ResponseEntity.noContent().build();
    }

}
