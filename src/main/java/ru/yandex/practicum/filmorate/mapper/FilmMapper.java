package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public interface FilmMapper {

    FilmDto mapToFilmDto(Film film);

    Film mapToFilmModel(NewFilmRequest filmDto);

    Film updateFilmFields(Film film, UpdateFilmRequest filmDto);
}
