package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.stream.Collectors;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Events;
import ru.yandex.practicum.filmorate.model.Operations;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.EventStorage;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewMapper reviewMapper;
    private final Validator validator;
    private final EventStorage eventStorage;

    public ReviewDto addReview(NewReviewRequest request) {
        if (request.getContent() == null) {
            throw new InternalServerException("Review create Fail: content cannot be null");
        }
        if (request.getIsPositive() == null) {
            throw new InternalServerException("Review create Fail: isPositive cannot be null");
        }

        Long userId = request.getUserId();
        if (userId == null || userId <= 0) {
            throw new UserNotFoundException("User not found", userId != null ? userId : 0L);
        }

        Long filmId = request.getFilmId();
        if (filmId == null || filmId <= 0) {
            throw new FilmNotFoundException("Film not found", filmId != null ? filmId : 0L);
        }

        if (!userStorage.checkUserExists(userId)) {
            throw new UserNotFoundException("User not found", userId);
        }
        if (!filmStorage.checkFilmExists(filmId)) {
            throw new FilmNotFoundException("Film not found", filmId);
        }

        try {
            Review review = reviewMapper.mapToReviewModel(request);
            Review added = reviewStorage.addReview(review);
            log.debug("Added review: {}", added);
            eventStorage.insertUserFeedQuery(added.getUserId(), Events.REVIEW.name(), Operations.ADD.name(), added.getReviewId());
            return reviewMapper.mapToReviewDto(added);
        } catch (DataIntegrityViolationException e) {
            throw new InternalServerException("Review create Fail: database integrity violation", e);
        }
    }


    // Остальные методы остаются без изменений
    public ReviewDto updateReview(UpdateReviewRequest request) {
        Review review = reviewStorage.getReviewById(request.getReviewId())
                .orElseThrow(() -> new ReviewNotFoundException("Review not found", request.getReviewId()));
        review = reviewMapper.updateReviewFields(review, request);
        Review updated = reviewStorage.updateReview(review);
        log.debug("Updated review: {}", updated);
        eventStorage.insertUserFeedQuery(updated.getUserId(), Events.REVIEW.name(), Operations.UPDATE.name(), updated.getReviewId());
        return reviewMapper.mapToReviewDto(updated);
    }

    public void deleteReview(long reviewId) {
        Review review = reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found", reviewId));
        eventStorage.insertUserFeedQuery(review.getUserId(), Events.REVIEW.name(), Operations.REMOVE.name(), reviewId);
        reviewStorage.deleteReview(reviewId);
        log.debug("Deleted review with ID: {}", reviewId);
    }


    public ReviewDto getReviewById(long reviewId) {
        Review review = reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found", reviewId));
        log.debug("Retrieved review: {}", review);
        return reviewMapper.mapToReviewDto(review);
    }

    public Collection<ReviewDto> getReviews(Long filmId, int count) {
        var reviews = reviewStorage.getReviews(filmId, count);
        log.debug("Retrieved {} reviews for filmId: {}", reviews.size(), filmId);
        return reviews.stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public void addLike(long reviewId, long userId) {
        if (reviewStorage.getReviewById(reviewId).isEmpty()) {
            throw new ReviewNotFoundException("Review not found", reviewId);
        }
        if (!userStorage.checkUserExists(userId)) {
            throw new UserNotFoundException("User not found", userId);
        }
        reviewStorage.addLike(reviewId, userId);
        log.debug("Added like to review {} by user {}", reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        reviewStorage.getReviewById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found", reviewId));

        if (!userStorage.checkUserExists(userId)) {
            throw new UserNotFoundException("User not found", userId);
        }

        if (reviewStorage.hasDislike(reviewId, userId)) {
            throw new InternalServerException("User " + userId + " already disliked review " + reviewId);
        }

        reviewStorage.addDislike(reviewId, userId);
        log.debug("Added dislike to review {} by user {}", reviewId, userId);
    }


    public void removeLike(long reviewId, long userId) {
        if (reviewStorage.getReviewById(reviewId).isEmpty()) {
            throw new ReviewNotFoundException("Review not found", reviewId);
        }
        reviewStorage.removeLike(reviewId, userId);
        log.debug("Removed like from review {} by user {}", reviewId, userId);
    }

    public void removeDislike(long reviewId, long userId) {
        if (reviewStorage.getReviewById(reviewId).isEmpty()) {
            throw new ReviewNotFoundException("Review not found", reviewId);
        }
        reviewStorage.removeDislike(reviewId, userId);
        log.debug("Removed dislike from review {} by user {}", reviewId, userId);
    }
}