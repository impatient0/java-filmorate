package ru.yandex.practicum.filmorate.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FilmTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void shouldValidateWithValidParameters() {
        Film film = new Film();
        film.setName("Eraserhead");
        film.setReleaseDate(LocalDate.of(1977, 3, 19));
        film.setDescription(
            "Eraserhead is a 1977 American independent surrealist body horror film written, directed, produced, and edited by David Lynch.");
        film.setDuration(89);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotValidateWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setReleaseDate(LocalDate.of(1977, 3, 19));
        film.setDescription(
            "Eraserhead is a 1977 American independent surrealist body horror film written, directed, produced, and edited by David Lynch.");
        film.setDuration(89);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Film name must not be blank.", violation.getMessage());
    }

    @Test
    public void shouldNotValidateWithNullName() {
        Film film = new Film();
        film.setName(null);
        film.setReleaseDate(LocalDate.of(1977, 3, 19));
        film.setDescription(
            "Eraserhead is a 1977 American independent surrealist body horror film written, directed, produced, and edited by David Lynch.");
        film.setDuration(89);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Film name must not be blank.", violation.getMessage());
    }

    @Test
    public void shouldNotValidateWithInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Eraserhead");
        film.setReleaseDate(LocalDate.of(1812, 6, 24));
        film.setDescription(
            "Eraserhead is a 1977 American independent surrealist body horror film written, directed, produced, and edited by David Lynch.");
        film.setDuration(89);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Release date must be after 1895-12-28.", violation.getMessage());
    }

    @Test
    public void shouldNotValidateWithNegativeDuration() {
        Film film = new Film();
        film.setName("Eraserhead");
        film.setReleaseDate(LocalDate.of(1977, 3, 19));
        film.setDescription(
            "Eraserhead is a 1977 American independent surrealist body horror film written, directed, produced, and edited by David Lynch.");
        film.setDuration(-89);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Film duration must be positive.", violation.getMessage());
    }

    @Test
    public void shouldNotValidateWithDescriptionExceedingCharacterLimit() {
        Film film = new Film();
        film.setName("Eraserhead");
        film.setReleaseDate(LocalDate.of(1977, 3, 19));
        film.setDescription(
            "Eraserhead is a 1977 American independent surrealist body horror film written, directed, produced, and edited by David Lynch. ".repeat(
                1000));
        film.setDuration(89);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Film description must not exceed 200 characters.", violation.getMessage());
    }
}
