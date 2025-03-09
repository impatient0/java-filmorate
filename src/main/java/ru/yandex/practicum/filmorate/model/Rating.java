package ru.yandex.practicum.filmorate.model;

import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Rating {

    private long userId;
    private long filmId;
    private int ratingValue;
    private Timestamp ratedAt;
}