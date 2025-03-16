package ru.yandex.practicum.filmorate.repository.mappers;

import lombok.Data;

@Data
public class FreqRow {

    private Long filmId1;
    private Long filmId2;
    private Integer freqValue;
}
