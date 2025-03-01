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
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MpaRatingController {

    private final MpaRatingService mpaRatingService;

    @GetMapping
    public ResponseEntity<Collection<MpaRating>> getAllMpaRatings() {
        log.info("Request to get all MPA ratings received.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(mpaRatingService.getAllMpaRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MpaRating> getMpaRatingById(@PathVariable long id) {
        log.info("Request to get MPA rating with ID {} received.", id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(mpaRatingService.getMpaRatingById(id));
    }

}
