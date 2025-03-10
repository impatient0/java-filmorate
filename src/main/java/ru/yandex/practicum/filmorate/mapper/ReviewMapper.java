package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewMapper {

    ReviewDto mapToReviewDto(Review review);

    Review mapToReviewModel(NewReviewRequest newReviewRequest);

    Review updateReviewFields(Review review, UpdateReviewRequest updateReviewRequest);

}
