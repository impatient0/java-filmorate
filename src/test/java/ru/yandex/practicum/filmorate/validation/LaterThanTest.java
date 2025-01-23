package ru.yandex.practicum.filmorate.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LaterThanTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Data
    static class TestClass {
        @LaterThan(value = "2011-09-11")
        private LocalDate testDate;
    }

    @Test
    void shouldPassForDateAfterConstraint() {
        TestClass testClass = new TestClass();
        testClass.setTestDate(LocalDate.of(2012, 1, 1));
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailForDateBeforeConstraint() {
        TestClass testClass = new TestClass();
        testClass.setTestDate(LocalDate.of(1998, 6, 19));
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        assertFalse(violations.isEmpty());
        ConstraintViolation<TestClass> violation = violations.iterator().next();
        assertEquals("Date is too old to be valid.", violation.getMessage());
    }

    @Test
    void shouldPassForNullValue() {
        TestClass testClass = new TestClass();
        testClass.setTestDate(null);
        Set<ConstraintViolation<TestClass>> violations = validator.validate(testClass);
        assertTrue(violations.isEmpty());
    }

}
