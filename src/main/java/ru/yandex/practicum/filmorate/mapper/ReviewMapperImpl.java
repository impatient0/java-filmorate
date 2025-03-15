package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

@Component
@SuppressWarnings("unused")
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public ReviewDto mapToReviewDto(Review review) {
        if (review == null) {
            return null;
        }
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.isPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        return dto;
    }

    @Override
    public Review mapToReviewModel(NewReviewRequest newReviewRequest) {
        if (newReviewRequest == null) {
            return null;
        }
        Review review = new Review();
        review.setContent(newReviewRequest.getContent());
        review.setPositive(newReviewRequest.getIsPositive() != null ? newReviewRequest.getIsPositive() : false); // Защита от null
        review.setUserId(newReviewRequest.getUserId());
        review.setFilmId(newReviewRequest.getFilmId());
        review.setUseful(0); // Начальное значение
        return review;
    }

    @Override
    public Review updateReviewFields(Review review, UpdateReviewRequest updateReviewRequest) {
        if (review == null || updateReviewRequest == null) {
            return review;
        }
        if (updateReviewRequest.getContent() != null) {
            review.setContent(updateReviewRequest.getContent());
        }
        if (updateReviewRequest.getIsPositive() != null) {
            review.setPositive(updateReviewRequest.getIsPositive());
        }
        return review;
    }
}