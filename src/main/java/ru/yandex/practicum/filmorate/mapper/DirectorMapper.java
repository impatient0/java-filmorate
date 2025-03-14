package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorMapper {

    DirectorDto mapToDirectorDto(Director director);

    Director mapToDirectorModel(NewDirectorRequest newDirectorRequest);

    Director updateDirectorFields(Director director, UpdateDirectorRequest filmDto);
}
