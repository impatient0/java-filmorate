package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

@RestController
@RequestMapping("/films")
@SuppressWarnings("unused")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public ResponseEntity<?> getAllFilms() {
        log.info("Request to get all films received.");
        return ResponseEntity.ok(films.values());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Film newFilm, BindingResult result) {
        log.info("Request to create new film received: {}", newFilm);
        if (result.hasErrors()) {
            log.warn("Validation error for creating new film: {}", newFilm);
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("New film created with ID {}", newFilm.getId());
        return ResponseEntity.ok(newFilm);
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Film newFilm, BindingResult result) {
        log.info("Request to update film received: {}", newFilm);
        if (result.hasErrors()) {
            log.warn("Validation error for updating film: {}", newFilm);
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        if (films.get(newFilm.getId()) != null) {
            films.put(newFilm.getId(), newFilm);
            log.info("Film with ID {} updated", newFilm.getId());
            return ResponseEntity.ok(newFilm);
        }
        log.warn("Film with ID {} not found for updating", newFilm.getId());
        Map<String, String> message = Map.of("message",
            String.format("Film with ID %d not found for updating", newFilm.getId()));
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
            .stream()
            .mapToLong(id -> id)
            .max()
            .orElse(0);
        log.debug("Generated next ID: {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}
