package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewReviewRequest {
    private String content;
    @NotNull(message = "isPositive cannot be null")
    @JsonProperty("isPositive")
    private Boolean isPositive;
    @NotNull(message = "userId cannot be null")
    private Long userId;
    @NotNull(message = "filmId cannot be null")
    private Long filmId;
}