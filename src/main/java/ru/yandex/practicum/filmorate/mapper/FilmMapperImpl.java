package ru.yandex.practicum.filmorate.mapper;

import java.util.HashSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

@Component
@SuppressWarnings("unused")
public class FilmMapperImpl implements FilmMapper {

    @Override
    public FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(film.getMpa());
        filmDto.setGenres(film.getGenres());
        filmDto.setDirectors(film.getDirector());
        return filmDto;
    }

    @Override
    public Film mapToFilmModel(NewFilmRequest newFilmRequest) {
        Film film = new Film();
        film.setName(newFilmRequest.getName());
        film.setDescription(newFilmRequest.getDescription());
        film.setReleaseDate(newFilmRequest.getReleaseDate());
        film.setDuration(newFilmRequest.getDuration());
        film.setMpa(newFilmRequest.getMpa());
        film.setGenres(
                newFilmRequest.getGenres() == null ? new HashSet<>() : newFilmRequest.getGenres());
        film.setDirector(
                newFilmRequest.getDirectors() == null ? new HashSet<>() : newFilmRequest.getDirectors());
        return film;
    }

    @Override
    public Film updateFilmFields(Film film, UpdateFilmRequest updateFilmRequest) {
        if (updateFilmRequest.getName() != null) {
            film.setName(updateFilmRequest.getName());
        }
        if (updateFilmRequest.getDescription() != null) {
            film.setDescription(updateFilmRequest.getDescription());
        }
        if (updateFilmRequest.getReleaseDate() != null) {
            film.setReleaseDate(updateFilmRequest.getReleaseDate());
        }
        if (updateFilmRequest.getDuration() != null) {
            film.setDuration(updateFilmRequest.getDuration());
        }
        if (updateFilmRequest.getMpa() != null) {
            film.setMpa(updateFilmRequest.getMpa());
        }
        if (updateFilmRequest.getGenres() != null) {
            film.setGenres(updateFilmRequest.getGenres());
        }
        if (updateFilmRequest.getDirectors() != null) {
            film.setDirector(updateFilmRequest.getDirectors());
        }
        return film;
    }

}
