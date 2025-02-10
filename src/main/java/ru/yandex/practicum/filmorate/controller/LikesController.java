package ru.yandex.practicum.filmorate.controller;

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

    @PutMapping
    public ResponseEntity<Void> addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Request to add like from user {} to film {} received.", userId, id);
        likesService.likeFilm(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Request to remove like from user {} to film {} received.", userId, id);
        likesService.unlikeFilm(userId, id);
        return ResponseEntity.ok().build();
    }

}
