package ru.yandex.practicum.filmorate.repository.mappers;

import lombok.Data;

@Data
public class DiffRow {

    private Long filmId1;
    private Long filmId2;
    private Double diffValue;
}
