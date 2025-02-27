package ru.yandex.practicum.filmorate;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DbUserStorage;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbUserStorage.class, UserRowMapper.class})
class FilmorateApplicationTests {

    private static final String DELETE_QUERY = "DELETE FROM users";
    private static final String ADD_USER_QUERY = "INSERT INTO users (email, login, name, "
        + "birthday) VALUES ('test@example.com', 'testlogin', 'Test User', '2000-01-01')";
    private static final String ADD_ANOTHER_USER_QUERY = "INSERT INTO users (email, login, name, "
        + "birthday) VALUES ('test2@example.com', 'testlogin2', 'Test User 2', '2001-02-02')";
    private static final String CHECK_USER_QUERY =
        "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ? AND email = 'test@example.com' AND "
            + "login = 'testlogin' AND name = 'Test User' AND birthday" + " = '2000-01-01')";
    private static final String CHECK_UPDATED_USER_QUERY =
        "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ? AND email = 'new@example.com' AND "
            + "login = 'newlogin' AND name = 'New User' AND birthday" + " = '1999-01-01')";

    private final DbUserStorage userStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute(DELETE_QUERY);
    }

    @AfterEach
    void tearDown() {
        jdbc.execute(DELETE_QUERY);
    }

    @Test
    void testGetUserById() {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> connection.prepareStatement(ADD_USER_QUERY,
            Statement.RETURN_GENERATED_KEYS), keyHolder);

        Long assignedId = keyHolder.getKeyAs(Long.class);

        // Retrieve the test user that we added
        Optional<User> userOptional = userStorage.getUserById(assignedId);

        assertThat(userOptional).isPresent().hasValueSatisfying(retrievedUser -> {
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("id", assignedId);
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("email", "test@example.com");
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("login", "testlogin");
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("name", "Test User");
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("birthday",
                LocalDate.of(2000, 1, 1));
        });
    }

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        long userId = userStorage.addUser(user);

        assertThat(Boolean.TRUE.equals(
            jdbc.queryForObject(CHECK_USER_QUERY, Boolean.class, userId))).isTrue();
    }

    @Test
    void testUpdateUser() {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> connection.prepareStatement(ADD_USER_QUERY,
            Statement.RETURN_GENERATED_KEYS), keyHolder);

        Long assignedId = keyHolder.getKeyAs(Long.class);

        // Update the test user
        User updatedUser = new User();
        updatedUser.setId(assignedId);
        updatedUser.setEmail("new@example.com");
        updatedUser.setLogin("newlogin");
        updatedUser.setName("New User");
        updatedUser.setBirthday(LocalDate.of(1999, 1, 1));
        userStorage.updateUser(updatedUser);

        assertThat(Boolean.TRUE.equals(
            jdbc.queryForObject(CHECK_UPDATED_USER_QUERY, Boolean.class, assignedId))).isTrue();
    }

    @Test
    void testGetAllUsers() {
        jdbc.execute(ADD_USER_QUERY);
        jdbc.execute(ADD_ANOTHER_USER_QUERY);

        List<User> users = (List<User>) userStorage.getAllUsers();
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    void testCheckUserExists() {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> connection.prepareStatement(ADD_USER_QUERY,
            Statement.RETURN_GENERATED_KEYS), keyHolder);

        Long assignedId = keyHolder.getKeyAs(Long.class);

        assertThat(userStorage.checkUserExists(assignedId)).isTrue();
        assertThat(userStorage.checkUserExists(assignedId + 42L)).isFalse();
    }

}