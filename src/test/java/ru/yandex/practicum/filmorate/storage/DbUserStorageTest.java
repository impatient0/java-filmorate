package ru.yandex.practicum.filmorate.storage;

import static org.assertj.core.api.Assertions.assertThat;

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
        "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ? AND email = 'test@example.com' AND "
            + "login = 'testlogin' AND name = 'Test User' AND birthday" + " = '2000-01-01')";

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
    void testGetUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        long userId = userStorage.addUser(user);

        Optional<User> userOptional = userStorage.getUserById(userId);

        assertThat(userOptional).isPresent().hasValueSatisfying(retrievedUser -> {
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("id", userId);
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("email", "test@example.com");
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("login", "testlogin");
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("name", "Test User");
            assertThat(retrievedUser).hasFieldOrPropertyWithValue("birthday",
                LocalDate.of(2000, 1, 1));
        });
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        long userId = userStorage.addUser(user);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("new@example.com");
        updatedUser.setLogin("newlogin");
        updatedUser.setName("New User");
        updatedUser.setBirthday(LocalDate.of(1999, 1, 1));
        userStorage.updateUser(updatedUser);

        assertThat(userStorage.getUserById(userId)).isPresent()
            .hasValueSatisfying(retrievedUser -> {
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("id", userId);
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("email", "new@example.com");
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("login", "newlogin");
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("name", "New User");
                assertThat(retrievedUser).hasFieldOrPropertyWithValue("birthday",
                    LocalDate.of(1999, 1, 1));
            });
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("testlogin1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        long user1Id = userStorage.addUser(user1);
        user1.setId(user1Id);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("testlogin2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        long user2Id = userStorage.addUser(user2);
        user2.setId(user2Id);

        List<User> users = (List<User>) userStorage.getAllUsers();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users).contains(user1, user2);
    }

    @Test
    void testCheckUserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        long userId = userStorage.addUser(user);

        assertThat(userStorage.checkUserExists(userId)).isTrue();
        assertThat(userStorage.checkUserExists(userId + 42L)).isFalse();
    }

}