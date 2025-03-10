package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Review {
    private Long reviewId;
    private String content;
    private boolean isPositive;
    private Long userId;
    private Long filmId;
    private int useful;
}
