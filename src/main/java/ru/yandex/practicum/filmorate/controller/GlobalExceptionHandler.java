package ru.yandex.practicum.filmorate.controller;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.dto.ErrorMessage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        String errorDetails = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorMessage errorMessage = new ErrorMessage("Validation failed", errorDetails);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMessage);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(final ValidationException e) {
        log.warn("Encountered {}: returning 400 Bad Request. Message: {}",
            e.getClass().getSimpleName(), e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage(),
            String.format("Invalid %s data: %s", e.getEntityType().getSimpleName(),
                e.getValidationMessage()));
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON)
            .body(errorMessage);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundException(final NotFoundException e) {
        log.warn("Encountered {}: returning 404 Not Found. Message: {}",
            e.getClass().getSimpleName(), e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage(),
            String.format("%s with ID %d not found", e.getEntityType().getSimpleName(), e.getId()));
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