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
    private static final String INSERT_DIRECTIONAL_FRIENDSHIP_QUERY = "INSERT INTO friendships "
        + "(user_id, friend_id, status) VALUES (?, ?, ?)";

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

    @Test
    void testGetDirectionalFriendshipStatus() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        long user1Id = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2001, 2, 2));
        long user2Id = userStorage.addUser(user2);

        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1Id, user2Id, "PENDING");

        Optional<FriendshipStatus> friendshipStatus =
            friendshipStorage.getDirectionalFriendshipStatus(
            user1Id, user2Id);
        assertThat(friendshipStatus).isPresent().contains(FriendshipStatus.PENDING);
    }

    @Test
    void testInsertDirectionalFriendship() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        long user1Id = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2001, 2, 2));
        long user2Id = userStorage.addUser(user2);

        friendshipStorage.insertDirectionalFriendship(user1Id, user2Id, FriendshipStatus.PENDING);

        Optional<FriendshipStatus> friendshipStatus =
            friendshipStorage.getDirectionalFriendshipStatus(
            user1Id, user2Id);
        assertThat(friendshipStatus).isPresent().contains(FriendshipStatus.PENDING);
    }

    @Test
    void testUpdateFriendshipStatus() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        long user1Id = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2001, 2, 2));
        long user2Id = userStorage.addUser(user2);
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1Id, user2Id, "REQUESTED");
        friendshipStorage.updateFriendshipStatus(user1Id, user2Id, FriendshipStatus.CONFIRMED);

        Optional<FriendshipStatus> friendshipStatus =
            friendshipStorage.getDirectionalFriendshipStatus(
            user1Id, user2Id);
        assertThat(friendshipStatus).isPresent().contains(FriendshipStatus.CONFIRMED);
    }

    @Test
    void testDeleteDirectionalFriendship() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        long user1Id = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2001, 2, 2));
        long user2Id = userStorage.addUser(user2);
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1Id, user2Id, "REQUESTED");

        friendshipStorage.deleteDirectionalFriendship(user1Id, user2Id);

        Optional<FriendshipStatus> friendshipStatus =
            friendshipStorage.getDirectionalFriendshipStatus(
            user1Id, user2Id);
        assertThat(friendshipStatus).isEmpty();
    }

    @Test
    void testGetUserFriends() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        long user1Id = userStorage.addUser(user1);
        user1.setId(user1Id);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2001, 2, 2));
        long user2Id = userStorage.addUser(user2);
        user2.setId(user2Id);

        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setLogin("user3login");
        user3.setName("User 3");
        user3.setBirthday(LocalDate.of(2003, 3, 3));
        long user3Id = userStorage.addUser(user3);
        user3.setId(user3Id);

        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1Id, user2Id, "CONFIRMED");
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1Id, user3Id, "CONFIRMED");

        Set<User> friends = friendshipStorage.getUserFriends(user1Id);

        assertThat(friends.size()).isEqualTo(2);
        assertThat(friends).contains(user2, user3);
    }

    @Test
    void testGetCommonFriends() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1login");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        long user1Id = userStorage.addUser(user1);
        user1.setId(user1Id);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2login");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(2001, 2, 2));
        long user2Id = userStorage.addUser(user2);
        user2.setId(user2Id);

        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setLogin("user3login");
        user3.setName("User 3");
        user3.setBirthday(LocalDate.of(2003, 3, 3));
        long user3Id = userStorage.addUser(user3);
        user3.setId(user3Id);

        User user4 = new User();
        user4.setEmail("user4@example.com");
        user4.setLogin("user4login");
        user4.setName("User 4");
        user4.setBirthday(LocalDate.of(2004, 4, 4));
        long user4Id = userStorage.addUser(user4);
        user4.setId(user4Id);

        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1Id, user3Id, "CONFIRMED");
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user2Id, user3Id, "CONFIRMED");
        jdbc.update(INSERT_DIRECTIONAL_FRIENDSHIP_QUERY, user1Id, user4Id, "CONFIRMED");

        Set<User> commonFriends = friendshipStorage.getCommonFriends(user1Id, user2Id);

        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends).contains(user3);
    }
}