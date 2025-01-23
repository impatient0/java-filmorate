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

public class UserTest {

    private static Validator validator;

    private static final String MOCK_USER_EMAIL = "john.doe@example.com";
    private static final String MOCK_USER_LOGIN = "john_doe";
    private static final String MOCK_USER_NAME = "John Doe";
    private static final LocalDate MOCK_USER_BIRTHDAY = LocalDate.of(1990, 5, 15);

    @BeforeAll
    public static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void shouldValidateWithValidParameters() {
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldNotValidateWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid_email");
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("E-mail must be valid.", violation.getMessage());
    }

    @Test
    public void shouldNotValidateWithBlankLogin() {
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(" ");
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Login must not be blank or contain whitespaces.", violation.getMessage());
    }


    @Test
    public void shouldNotValidateWithInvalidLoginWithSpaces() {
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin("test login");
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Login must not be blank or contain whitespaces.", violation.getMessage());
    }

    @Test
    public void shouldNotValidateWithFutureBirthdate() {
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(LocalDate.of(2077, 3, 19));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Birthdate must not be in the future.", violation.getMessage());
    }
}