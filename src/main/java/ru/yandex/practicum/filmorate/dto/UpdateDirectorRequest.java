package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateDirectorRequest {

    private long id;
    @NotBlank(message = "Director name must not be blank.")
    private String name;

}