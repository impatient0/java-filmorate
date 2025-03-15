package ru.yandex.practicum.filmorate.controller;

import java.net.URI;
import java.util.Collection;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> addReview(@Valid @RequestBody NewReviewRequest newReviewRequest) {
        ReviewDto created = reviewService.addReview(newReviewRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getReviewId()).toUri();
        return ResponseEntity.created(location)
                .contentType(MediaType.APPLICATION_JSON)
                .body(created);
    }

    @PutMapping
    public ResponseEntity<ReviewDto> updateReview(@RequestBody UpdateReviewRequest updateReviewRequest) {
        ReviewDto updated = reviewService.updateReview(updateReviewRequest);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable("id") long reviewId) {
        ReviewDto review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(review);
    }

    @GetMapping
    public ResponseEntity<Collection<ReviewDto>> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") int count) {
        Collection<ReviewDto> reviews = reviewService.getReviews(filmId, count);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reviews);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.addLike(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Void> addDislike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.addDislike(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.removeLike(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Void> removeDislike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        reviewService.removeDislike(reviewId, userId);
        return ResponseEntity.ok().build();
    }
}