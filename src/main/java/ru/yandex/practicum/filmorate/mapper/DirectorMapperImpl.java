package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

@Component
public class DirectorMapperImpl implements DirectorMapper {

    @Override
    public DirectorDto mapToDirectorDto(Director director) {
        DirectorDto directorDto = new DirectorDto();
        directorDto.setId(director.getId());
        directorDto.setName(director.getName());
        return directorDto;
    }

    @Override
    public Director mapToDirectorModel(NewDirectorRequest newDirectorRequest) {
        Director director = new Director();
        director.setName(newDirectorRequest.getName());
        return director;
    }

    @Override
    public Director updateDirectorFields(Director director, UpdateDirectorRequest updateDirectorRequest) {
        if (updateDirectorRequest.getName() != null) {
            director.setName(updateDirectorRequest.getName());
        }
        return director;
    }
}