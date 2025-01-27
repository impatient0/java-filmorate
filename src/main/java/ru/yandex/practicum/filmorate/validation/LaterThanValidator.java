package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LaterThanValidator implements ConstraintValidator<LaterThan, LocalDate> {

    private LocalDate referenceDate;

    @Override
    public void initialize(LaterThan constraintAnnotation) {
        this.referenceDate = LocalDate.parse(constraintAnnotation.value(),
            DateTimeFormatter.ISO_DATE);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || value.isAfter(referenceDate);
    }
}
