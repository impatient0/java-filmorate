package ru.yandex.practicum.filmorate.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DbFriendshipStorage;
import ru.yandex.practicum.filmorate.repository.DbUserStorage;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbFriendshipStorage.class, DbUserStorage.class, UserRowMapper.class})
public class DbFriendshipStorageTest {

    private static final String DELETE_FRIENDSHIPS_QUERY = "DELETE FROM friendships";
    private static final String DELETE_USERS_QUERY = "DELETE FROM users";
    private static final String INSERT_DIRECTIONAL_FRIENDSHIP_QUERY =
        "INSERT INTO friendships " + "(user_id, friend_id, status) VALUES (?, ?, ?)";

    private final DbFriendshipStorage friendshipStorage;
    private final DbUserStorage userStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute(DELETE_FRIENDSHIPS_QUERY);
        jdbc.execute(DELETE_USERS_QUERY);
    }

    @AfterEach
    void tearDown() {
        jdbc.execute(DELETE_FRIENDSHIPS_QUERY);
        jdbc.execute(DELETE_USERS_QUERY);
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
    void testGetDirectionalFriendshipStatus() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));

        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1.getId(), user2.getId(),
            FriendshipStatus.PENDING.toString());

        Optional<FriendshipStatus> friendshipStatus =
            friendshipStorage.getDirectionalFriendshipStatus(user1.getId(), user2.getId());
        assertThat(friendshipStatus).isPresent().contains(FriendshipStatus.PENDING);
    }

    @Test
    void testInsertDirectionalFriendship() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));

        friendshipStorage.insertDirectionalFriendship(user1.getId(), user2.getId(),
            FriendshipStatus.PENDING);

        Optional<FriendshipStatus> friendshipStatus =
            friendshipStorage.getDirectionalFriendshipStatus(user1.getId(), user2.getId());
        assertThat(friendshipStatus).isPresent().contains(FriendshipStatus.PENDING);
    }

    @Test
    void testUpdateFriendshipStatus() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1.getId(), user2.getId(),
            FriendshipStatus.PENDING.toString());
        friendshipStorage.updateFriendshipStatus(user1.getId(), user2.getId(),
            FriendshipStatus.CONFIRMED);

        Optional<FriendshipStatus> friendshipStatus =
            friendshipStorage.getDirectionalFriendshipStatus(user1.getId(), user2.getId());
        assertThat(friendshipStatus).isPresent().contains(FriendshipStatus.CONFIRMED);
    }

    @Test
    void testDeleteDirectionalFriendship() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1.getId(), user2.getId(),
            FriendshipStatus.PENDING.toString());

        friendshipStorage.deleteDirectionalFriendship(user1.getId(), user2.getId());

        Optional<FriendshipStatus> friendshipStatus =
            friendshipStorage.getDirectionalFriendshipStatus(user1.getId(), user2.getId());
        assertThat(friendshipStatus).isEmpty();
    }

    @Test
    void testGetUserFriends() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        User user3 = createUser("user3@example.com", "user3login", "User 3",
            LocalDate.of(2003, 3, 3));

        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1.getId(), user2.getId(),
            FriendshipStatus.CONFIRMED.toString());
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1.getId(), user3.getId(),
            FriendshipStatus.CONFIRMED.toString());

        Set<User> friends = friendshipStorage.getUserFriends(user1.getId());

        assertThat(friends.size()).isEqualTo(2);
        assertThat(friends).contains(user2, user3);
    }

    @Test
    void testGetCommonFriends() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        User user3 = createUser("user3@example.com", "user3login", "User 3",
            LocalDate.of(2003, 3, 3));
        User user4 = createUser("user4@example.com", "user4login", "User 4",
            LocalDate.of(2004, 4, 4));

        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1.getId(), user3.getId(),
            FriendshipStatus.CONFIRMED.toString());
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user2.getId(), user3.getId(),
            FriendshipStatus.CONFIRMED.toString());
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1.getId(), user4.getId(),
            FriendshipStatus.CONFIRMED.toString());

        Set<User> commonFriends = friendshipStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends).contains(user3);
    }
}