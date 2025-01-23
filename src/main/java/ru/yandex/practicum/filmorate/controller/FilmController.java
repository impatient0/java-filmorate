package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.ErrorMessage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ResponseBody;

@RestController
@RequestMapping("/films")
@Slf4j
@SuppressWarnings("unused")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 1L;

    @GetMapping
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("Request to get all films received.");
        return ResponseEntity.ok(films.values());
    }

    @PostMapping
    public ResponseEntity<ResponseBody> create(@Valid @RequestBody Film newFilm,
        BindingResult result) {
        log.info("Request to create new film received: {}", newFilm);
        if (result.hasErrors()) {
            ErrorMessage errorMessage = new ErrorMessage(
                result.getAllErrors().getFirst().getDefaultMessage());
            log.warn("Validation error for creating new film: {}", errorMessage.getMessage());
            return ResponseEntity.badRequest().body(errorMessage);
        }
        newFilm.setId(currentId++);
        log.debug("Assigned ID: {}", currentId - 1);
        films.put(newFilm.getId(), newFilm);
        log.info("New film created with ID {}", newFilm.getId());
        return new ResponseEntity<>(newFilm, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ResponseBody> update(@Valid @RequestBody Film newFilm,
        BindingResult result) {
        log.info("Request to update film received: {}", newFilm);
        if (films.get(newFilm.getId()) == null) {
            log.warn("Film with ID {} not found for updating", newFilm.getId());
            ErrorMessage errorMessage = new ErrorMessage(
                String.format("Film with ID %d not found for updating", newFilm.getId()));
            return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        }
        if (result.hasErrors()) {
            ErrorMessage errorMessage = new ErrorMessage(
                result.getAllErrors().getFirst().getDefaultMessage());
            log.warn("Validation error for updating film: {}", errorMessage.getMessage());
            return ResponseEntity.badRequest().body(errorMessage);
        }
        films.put(newFilm.getId(), newFilm);
        log.info("Film with ID {} updated", newFilm.getId());
        return ResponseEntity.ok(newFilm);
    }
}
