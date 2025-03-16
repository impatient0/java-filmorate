package ru.yandex.practicum.filmorate.mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
@SuppressWarnings("unused")
public class FilmMapperImpl implements FilmMapper {

    @Override
    public FilmDto mapToFilmDto(Film film) {
        return mapToFilmDto(film, Double.NaN);
    }

    @Override
    public FilmDto mapToFilmDto(Film film, double averageRating) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(film.getMpa());
        filmDto.setGenres(film.getGenres() == null ? new ArrayList<>()
            : film.getGenres().stream().sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toList()));
        filmDto.setDirectors(film.getDirectors() == null ? new ArrayList<>()
            : film.getDirectors().stream().sorted(Comparator.comparingLong(Director::getId))
                .collect(Collectors.toList()));
        filmDto.setRate(averageRating);
        return filmDto;
    }

    @Override
    public FilmDto mapToFilmDto(FilmWithRating filmWithRating) {
        return mapToFilmDto(filmWithRating.getFilm(), filmWithRating.getAvgRating());
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
        film.setDirectors(newFilmRequest.getDirectors() == null ? new HashSet<>()
            : newFilmRequest.getDirectors());
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
        } else {
            film.setGenres(new HashSet<>());
        }
        if (updateFilmRequest.getDirectors() != null) {
            film.setDirectors(updateFilmRequest.getDirectors());
        } else {
            film.setDirectors(new HashSet<>());
        }
        return film;
    }

}
