package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class ErrorMessageEspeciallyForDirector {

    private String message;

    private String description;

    private String error;
}