package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DbUserStorage;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbUserStorage.class, UserRowMapper.class})
class DbUserStorageTest {

    private static final String DELETE_QUERY = "DELETE FROM users";
    private static final String CHECK_USER_QUERY =
        "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ? AND email = ? AND "
            + "login = ? AND name = ? AND birthday" + " = ?)";

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

    private User createUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        long userId = userStorage.addUser(user);
        user.setId(userId);
        return user;
    }

    @Test
    void testAddUser() {
        User user = createUser("test@example.com", "testlogin", "Test User",
            LocalDate.of(2000, 1, 1));
        assertThat(Boolean.TRUE.equals(
            jdbc.queryForObject(CHECK_USER_QUERY, Boolean.class, user.getId(), user.getEmail(),
                user.getLogin(), user.getName(), user.getBirthday()))).isTrue();
    }

    @Test
    void testGetUserById() {
        User user = createUser("test@example.com", "testlogin", "Test User",
            LocalDate.of(2000, 1, 1));

        Optional<User> userOptional = userStorage.getUserById(user.getId());

        assertThat(userOptional).isPresent().hasValueSatisfying(retrievedUser -> {
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("id", user.getId());
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("email", user.getEmail());
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("login", user.getLogin());
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("name", user.getName());
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("birthday", user.getBirthday());
        });
    }

    @Test
    void testUpdateUser() {
        User user = createUser("test@example.com", "testlogin", "Test User",
            LocalDate.of(2000, 1, 1));

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("new@example.com");
        updatedUser.setLogin("newlogin");
        updatedUser.setName("New User");
        updatedUser.setBirthday(LocalDate.of(1999, 1, 1));
        userStorage.updateUser(updatedUser);

        assertThat(userStorage.getUserById(user.getId())).isPresent()
            .hasValueSatisfying(retrievedUser -> {
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("id", user.getId());
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("email", "new@example.com");
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("login", "newlogin");
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("name", "New User");
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("birthday",
                    LocalDate.of(1999, 1, 1));
            });
    }

    @Test
    void testGetAllUsers() {
        User user1 = createUser("test1@example.com", "testlogin1", "Test User 1",
            LocalDate.of(2001, 1, 1));
        User user2 = createUser("test2@example.com", "testlogin2", "Test User 2",
            LocalDate.of(2002, 2, 2));

        List<User> users = (List<User>) userStorage.getAllUsers();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users).contains(user1, user2);
    }

    @Test
    void testCheckUserExists() {
        User user = createUser("test@example.com", "testlogin", "Test User",
            LocalDate.of(2000, 1, 1));

        assertThat(userStorage.checkUserExists(user.getId())).isTrue();
        assertThat(userStorage.checkUserExists(user.getId() + 42L)).isFalse();
    }

    @Test
    void testDeleteUser() {
        User user = createUser("test@example.com", "testlogin", "Test User",
            LocalDate.of(2000, 1, 1));
        long userId = user.getId();

        userStorage.deleteUser(userId);

        assertThat(userStorage.checkUserExists(userId)).isFalse();
        assertThat(userStorage.getUserById(userId)).isEmpty();
    }
}