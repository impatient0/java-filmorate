package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateReviewRequest {
    @NotNull(message = "reviewId cannot be null")
    private Long reviewId;
    private String content;
    @JsonProperty("isPositive")
    private Boolean isPositive;
}