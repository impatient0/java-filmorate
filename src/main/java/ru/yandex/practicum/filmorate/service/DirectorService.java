package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;
    private final Validator validator;
    private final DirectorMapper mapper;

    public Director getDirectorById(long id) {
        log.debug("Getting director with ID {}", id);
        return directorStorage.getDirectorById(id).orElseThrow(() -> {
            log.warn("Retrieving director failed: director with ID {} not found", id);
            return new GenreNotFoundException("Error when retrieving director", id);
        });
    }

    public Collection<Director> getAllDirectors() {
        log.debug("Getting all directors");
        return directorStorage.getAllDirectors();
    }

    public DirectorDto addDirector(NewDirectorRequest newDirectorRequest) {
        Set<ConstraintViolation<NewDirectorRequest>> violations = validator.validate(newDirectorRequest);
        if (!violations.isEmpty()) {
            String violationMessage = violations.iterator().next().getMessage();
            log.warn("Adding director failed: {}", violationMessage);
            throw new FilmValidationException("Error when creating new director", violationMessage);
        }
        Director director = mapper.mapToDirectorModel(newDirectorRequest);
        long directorId = directorStorage.addDirector(director);
        director.setId(directorId);
        log.debug("Adding new director {}", newDirectorRequest);
        return mapper.mapToDirectorDto(director);
    }

    public DirectorDto updateDirector(UpdateDirectorRequest updateDirectorRequest) {
        Director director = directorStorage.getDirectorById(updateDirectorRequest.getId()).orElseThrow(() -> {
            log.warn("Updating director failed: director with ID {} not found", updateDirectorRequest.getId());
            return new FilmNotFoundException("Error when updating director", updateDirectorRequest.getId());
        });
        Set<ConstraintViolation<UpdateDirectorRequest>> violations = validator.validate(
                updateDirectorRequest);
        if (!violations.isEmpty()) {
            String violationMessage = violations.iterator().next().getMessage();
            log.warn("Updating film failed: {}", violationMessage);
            throw new FilmValidationException("Error when updating film", violationMessage);
        }
        log.debug("Updating director with ID {}: {}", director.getId(), director);
        director = mapper.updateDirectorFields(director, updateDirectorRequest);
        directorStorage.updateDirector(director);
        return mapper.mapToDirectorDto(director);
    }

    public void delDirector(long directorId) {
        if (directorStorage.getDirectorById(directorId).isEmpty()) {
            log.warn("Deleting director failed: director with ID {} not found", directorId);
            throw new UserNotFoundException("Error when deleting director", directorId);
        }
        log.debug("Director with ID {} was deleted", directorId);
        directorStorage.removeDirector(directorId);
    }
}