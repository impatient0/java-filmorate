package ru.yandex.practicum.filmorate.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LaterThanValidator.class)
public @interface LaterThan {

    String message() default "Дата вне заданного интервала.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value();
}
