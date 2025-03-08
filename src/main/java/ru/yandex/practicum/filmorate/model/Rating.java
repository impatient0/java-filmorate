package ru.yandex.practicum.filmorate.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Rating {

    private long userId;
    private long filmId;
    private int ratingValue;
    private Instant ratedAt;
}