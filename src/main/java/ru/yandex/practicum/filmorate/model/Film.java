package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.LaterThan;

@Data
@NoArgsConstructor
public class Film {

    long id;
    @LaterThan(value = "1895-12-28", message = "Release date must be after 1895-12-28.")
    LocalDate releaseDate;
    @Positive(message = "Film duration must be positive.")
    int duration;
    @NotBlank(message = "Film name must not be blank.")
    private String name;
    @Size(max = 200, message = "Film description must not exceed 200 characters.")
    private String description;
}
