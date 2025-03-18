package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Film {

    long id;
    LocalDate releaseDate;
    int duration;
    private String name;
    private String description;
    private MpaRating mpa;
    private Set<Genre> genres;
    private Set<Director> directors;
}