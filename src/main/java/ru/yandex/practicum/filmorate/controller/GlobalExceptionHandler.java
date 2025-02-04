package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.ErrorMessage;

@RestControllerAdvice
@Slf4j
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleGenericException(final RuntimeException e) {
        log.warn("Encountered {}: returning 500 Internal Server Error. Message: {}",
            e.getClass().getSimpleName(), e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage("Internal server error.", null);
        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
            .body(errorMessage);
    }

    @ExceptionHandler(FilmValidationException.class)
    public ResponseEntity<ErrorMessage> handleFilmValidationException(
        final FilmValidationException e) {
        log.warn("Encountered {}: returning 400 Bad Request. Message: {}",
            e.getClass().getSimpleName(), e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage(),
            "Invalid film data: " + e.getValidationMessage());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
            .body(errorMessage);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ErrorMessage> handleUserValidationException(
        final UserValidationException e) {
        log.warn("Encountered {}: returning 400 Bad Request. Message: {}",
            e.getClass().getSimpleName(), e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage(),
            "Invalid user data: " + e.getValidationMessage());
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
            .body(errorMessage);
    }

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleFilmNotFoundException(final FilmNotFoundException e) {
        log.warn("Encountered {}: returning 404 Not Found. Message: {}",
            e.getClass().getSimpleName(), e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage(),
            String.format("Film with ID %d not found", e.getId()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
            .body(errorMessage);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFoundException(final UserNotFoundException e) {
        log.warn("Encountered {}: returning 404 Not Found. Message: {}",
            e.getClass().getSimpleName(), e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage(),
            String.format("User with ID %d not found", e.getId()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
            .body(errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException e) {
        log.warn("Encountered {}: returning 400 Bad Request. Message: {}",
            e.getClass().getSimpleName(), e.getMessage());
        String parameterName = e.getName();
        String invalidValue = e.getValue() == null ? "null" : e.getValue().toString();
        String requiredType =
            e.getRequiredType() == null ? "null" : e.getRequiredType().getSimpleName();
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage(),
            String.format("Invalid value '%s' for path parameter '%s'. Expected type: %s.",
                parameterName, invalidValue, requiredType));
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
            .body(errorMessage);
    }

}
