package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;

public interface FilmMapper {

    FilmDto mapToFilmDto(Film film);

    FilmDto mapToFilmDto(Film film, double averageRating);

    FilmDto mapToFilmDto(FilmWithRating filmWithRating);

    Film mapToFilmModel(NewFilmRequest filmDto);

    Film updateFilmFields(Film film, UpdateFilmRequest filmDto);
}
