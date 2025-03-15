package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewDto {
    private Long reviewId;
    private String content;
    @JsonProperty("isPositive")
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private int useful;
}