package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewDirectorRequest {

    @NotBlank(message = "Director name must not be blank.")
    private String name;
}