package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DbFilmStorage;
import ru.yandex.practicum.filmorate.repository.DbLikesStorage;
import ru.yandex.practicum.filmorate.repository.DbUserStorage;
import ru.yandex.practicum.filmorate.repository.mappers.FilmWithGenresDataMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbLikesStorage.class, DbUserStorage.class, DbFilmStorage.class, UserRowMapper.class,
        FilmWithGenresDataMapper.class})
public class DbLikesStorageTest {

    private static final String DELETE_LIKES_QUERY = "DELETE FROM likes";
    private static final String DELETE_FILMS_QUERY = "DELETE FROM films";
    private static final String DELETE_USERS_QUERY = "DELETE FROM users";
    private static final String ADD_LIKE_QUERY =
            "INSERT INTO likes (user_id, film_id) VALUES (?," + " ?)";
    private final DbLikesStorage likesStorage;
    private final DbUserStorage userStorage;
    private final DbFilmStorage filmStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute(DELETE_LIKES_QUERY);
        jdbc.execute(DELETE_FILMS_QUERY);
        jdbc.execute(DELETE_USERS_QUERY);
    }

    @AfterEach
    void tearDown() {
        jdbc.execute(DELETE_LIKES_QUERY);
        jdbc.execute(DELETE_FILMS_QUERY);
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

    private Film createFilm(String name, String description, LocalDate releaseDate, int duration,
                            int mpaId, String mpaName) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(mpaId);
        mpaRating.setName(mpaName);
        film.setMpa(mpaRating);
        film.setGenres(new HashSet<>());
        long filmId = filmStorage.addFilm(film);
        film.setId(filmId);
        return film;
    }

    @Test
    void testAddLike() {
        User user = createUser("user1@example.com", "user1login", "User 1",
                LocalDate.of(2000, 1, 1));
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
                "G");

        likesStorage.addLike(user.getId(), film.getId());
        assertThat(jdbc.queryForList("SELECT user_id FROM likes WHERE film_id = ?", Long.class,
                film.getId())).containsExactly(user.getId());
    }

    @Test
    void testRemoveLike() {
        User user = createUser("user1@example.com", "user1login", "User 1",
                LocalDate.of(2000, 1, 1));
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
                "G");
        jdbc.update(ADD_LIKE_QUERY, user.getId(), film.getId());

        likesStorage.removeLike(user.getId(), film.getId());

        assertThat(jdbc.queryForList("SELECT * FROM likes")).isEmpty();
    }

    @Test
    void testGetLikedFilms() {
        User user = createUser("user1@example.com", "user1login", "User 1",
                LocalDate.of(2000, 1, 1));
        Film film1 = createFilm("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120,
                1, "G");
        Film film2 = createFilm("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150,
                2, "PG");
        jdbc.update(ADD_LIKE_QUERY, user.getId(), film1.getId());
        jdbc.update(ADD_LIKE_QUERY, user.getId(), film2.getId());

        Collection<Film> likedFilms = likesStorage.getUserLikedFilms(user.getId());

        assertThat(likedFilms.size()).isEqualTo(2);
        assertThat(likedFilms).contains(film1, film2);
    }

    @Test
    void testGetUsersWhoLikedFilm() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
                LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
                LocalDate.of(2001, 2, 2));
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
                "G");
        jdbc.update(ADD_LIKE_QUERY, user1.getId(), film.getId());
        jdbc.update(ADD_LIKE_QUERY, user2.getId(), film.getId());

        Collection<User> users = likesStorage.getUsersWhoLikedFilm(film.getId());

        assertThat(users.size()).isEqualTo(2);
        assertThat(users).contains(user1, user2);
    }

    @Test
    void testGetPopularFilms() {
        User user1 = createUser("user1@example.com", "user1login", "User 1", LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2", LocalDate.of(2001, 2, 2));

        Film film1 = createFilm("Film 1", "Description 1", LocalDate.of(2000, 1, 1), 120, 1, "G"); // Жанр: Комедия
        Film film2 = createFilm("Film 2", "Description 2", LocalDate.of(2001, 2, 2), 150, 2, "PG"); // Жанр: Драма
        Film film3 = createFilm("Film 3", "Description 3", LocalDate.of(2001, 3, 3), 90, 3, "PG-13"); // Без жанра

        jdbc.update(ADD_LIKE_QUERY, user1.getId(), film1.getId());
        jdbc.update(ADD_LIKE_QUERY, user2.getId(), film1.getId());
        jdbc.update(ADD_LIKE_QUERY, user1.getId(), film2.getId());

        List<Film> popularFilmsNoFilter = (List<Film>) likesStorage.getPopularFilms(3, null, null);
        assertThat(popularFilmsNoFilter).hasSize(3);
        assertThat(popularFilmsNoFilter.get(0).getId()).isEqualTo(film1.getId()); //2 лайка
        assertThat(popularFilmsNoFilter.get(1).getId()).isEqualTo(film2.getId()); //1 лайк
        assertThat(popularFilmsNoFilter.get(2).getId()).isEqualTo(film3.getId()); //0 лайков

        List<Film> popularFilmsByYear = (List<Film>) likesStorage.getPopularFilms(2, null, 2001);
        assertThat(popularFilmsByYear).hasSize(2);
        assertThat(popularFilmsByYear.get(0).getId()).isEqualTo(film2.getId()); //1 лайк
        assertThat(popularFilmsByYear.get(1).getId()).isEqualTo(film3.getId()); //0 лайков

    }
}