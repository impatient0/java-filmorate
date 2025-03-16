package ru.yandex.practicum.filmorate.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.LikesService;

@RestController
@RequestMapping("/films/{id}/like/{userId}")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class LikesController {

    private final LikesService likesService;

    @PutMapping(value = {"", "/{ratingValue}"})
    public ResponseEntity<Void> addRating(@PathVariable long id, @PathVariable long userId,
        @PathVariable(required = false) Optional<Integer> ratingValue) {
        int actualRatingValue = ratingValue.orElse(6);

        log.info("Request for user {} to rate film {} as {} received.", userId, id,
            actualRatingValue);
        likesService.rateFilm(userId, id, actualRatingValue);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeRating(@PathVariable long id, @PathVariable long userId) {
        log.info("Request for user {} to unrate film {} received.", userId, id);
        likesService.unrateFilm(userId, id);
        return ResponseEntity.ok().build();
    }

}
