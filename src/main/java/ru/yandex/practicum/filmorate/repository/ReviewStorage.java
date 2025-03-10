package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Review addReview(Review review);
    Review updateReview(Review review);
    void deleteReview(long reviewId);
    Optional<Review> getReviewById(long reviewId);
    Collection<Review> getReviews(Long filmId, int count);

    // Методы для лайков/дизлайков
    void addLike(long reviewId, long userId);
    void addDislike(long reviewId, long userId);
    void removeLike(long reviewId, long userId);
    void removeDislike(long reviewId, long userId);
    boolean hasDislike(long reviewId, long userId);
}