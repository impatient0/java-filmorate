package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DbFilmStorage;
import ru.yandex.practicum.filmorate.repository.DbLikesStorage;
import ru.yandex.practicum.filmorate.repository.DbUserStorage;
import ru.yandex.practicum.filmorate.repository.mappers.FilmWithAvgRatingDataMapper;
import ru.yandex.practicum.filmorate.repository.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbLikesStorage.class, DbUserStorage.class, DbFilmStorage.class, UserRowMapper.class,
    FilmWithAvgRatingDataMapper.class, RatingRowMapper.class})
public class DbLikesStorageTest {

    private static final String DELETE_LIKES_QUERY = "DELETE FROM ratings";
    private static final String DELETE_FILMS_QUERY = "DELETE FROM films";
    private static final String DELETE_USERS_QUERY = "DELETE FROM users";
    private static final String ADD_LIKE_QUERY = "INSERT INTO ratings (user_id, film_id, "
        + "rating_value) VALUES (?, ?, 1)";
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
        int mpaId, String mpaName, Set<Genre> genres, Set<Director> directors) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(mpaId);
        mpaRating.setName(mpaName);
        film.setMpa(mpaRating);
        film.setGenres(genres);
        film.setDirectors(directors);
        long filmId = filmStorage.addFilm(film);
        film.setId(filmId);
        return film;
    }

    @Test
    void testAddRating() {
        User user = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G", new HashSet<>(), new HashSet<>());

        likesStorage.addRating(user.getId(), film.getId(), 1);
        assertThat(jdbc.queryForList("SELECT user_id FROM ratings WHERE film_id = ?", Long.class,
            film.getId())).containsExactly(user.getId());
    }

    @Test
    void testRemoveRating() {
        User user = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G", new HashSet<>(), new HashSet<>());
        jdbc.update(ADD_LIKE_QUERY, user.getId(), film.getId());

        likesStorage.removeRating(user.getId(), film.getId());

        assertThat(jdbc.queryForList("SELECT * FROM ratings")).isEmpty();
    }

    @Test
    void testGetRatingsOfFilm() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G", new HashSet<>(), new HashSet<>());
        likesStorage.addRating(user1.getId(), film.getId(), 10);
        likesStorage.addRating(user2.getId(), film.getId(), 5);
        List<Rating> ratings = likesStorage.getRatingsOfFilm(film.getId());
        assertThat(ratings).hasSize(2);
        assertThat(ratings.get(0).getRatingValue()).isEqualTo(10);
        assertThat(ratings.get(1).getRatingValue()).isEqualTo(5);
    }

    @Test
    void testGetRatingsByUser() {
        User user = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        Film film1 = createFilm("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120,
            1, "G", new HashSet<>(), new HashSet<>());
        Film film2 = createFilm("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150,
            2, "PG", new HashSet<>(), new HashSet<>());
        likesStorage.addRating(user.getId(), film1.getId(), 10);
        likesStorage.addRating(user.getId(), film2.getId(), 5);
        List<Rating> ratings = likesStorage.getRatingsByUser(user.getId());
        assertThat(ratings).hasSize(2);
        assertThat(ratings.get(0).getRatingValue()).isEqualTo(10);
        assertThat(ratings.get(1).getRatingValue()).isEqualTo(5);
    }

    @Test
    void testGetAllRatings() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        Film film1 = createFilm("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120,
            1, "G", new HashSet<>(), new HashSet<>());
        Film film2 = createFilm("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150,
            2, "PG", new HashSet<>(), new HashSet<>());
        likesStorage.addRating(user1.getId(), film1.getId(), 10);
        likesStorage.addRating(user2.getId(), film2.getId(), 5);
        List<Rating> ratings = likesStorage.getAllRatings();
        assertThat(ratings).hasSize(2);
        assertThat(ratings.get(0).getRatingValue()).isEqualTo(10);
        assertThat(ratings.get(1).getRatingValue()).isEqualTo(5);
    }

    @Test
    void testGetRatedFilms() {
        User user = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        Film film1 = createFilm("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120,
            1, "G", new HashSet<>(), new HashSet<>());
        Film film2 = createFilm("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150,
            2, "PG", new HashSet<>(), new HashSet<>());
        jdbc.update(ADD_LIKE_QUERY, user.getId(), film1.getId());
        jdbc.update(ADD_LIKE_QUERY, user.getId(), film2.getId());

        Collection<FilmWithRating> likedFilms = likesStorage.getFilmsRatedByUser(user.getId());

        assertThat(likedFilms.size()).isEqualTo(2);
        assertThat(likedFilms.stream().map(FilmWithRating::getFilm)).contains(film1, film2);
    }

    @Test
    void testGetUsersWhoRatedFilm() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G", new HashSet<>(), new HashSet<>());
        jdbc.update(ADD_LIKE_QUERY, user1.getId(), film.getId());
        jdbc.update(ADD_LIKE_QUERY, user2.getId(), film.getId());

        Collection<User> users = likesStorage.getUsersWhoRatedFilm(film.getId());

        assertThat(users.size()).isEqualTo(2);
        assertThat(users).contains(user1, user2);
    }

    @Test
    void testGetUsersWhoRatedBothFilms() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        Film film1 = createFilm("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120,
            1, "G", new HashSet<>(), new HashSet<>());
        Film film2 = createFilm("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150,
            2, "PG", new HashSet<>(), new HashSet<>());
        likesStorage.addRating(user1.getId(), film1.getId(), 10);
        likesStorage.addRating(user1.getId(), film2.getId(), 5);
        likesStorage.addRating(user2.getId(), film1.getId(), 5);
        List<User> users = likesStorage.getUsersWhoRatedBothFilms(film1.getId(), film2.getId());
        assertThat(users).hasSize(1);
        assertThat(users.getFirst().getId()).isEqualTo(user1.getId());
    }

    @Test
    void testGetPopularFilms() {
        User user1 = createUser("user1@example.com", "user1login", "User 1",
            LocalDate.of(2000, 1, 1));
        User user2 = createUser("user2@example.com", "user2login", "User 2",
            LocalDate.of(2001, 2, 2));
        Film film1 = createFilm("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120,
            1, "G",new HashSet<>(), new HashSet<>());
        Film film2 = createFilm("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150,
            2, "PG", new HashSet<>(), new HashSet<>());

        jdbc.update(ADD_LIKE_QUERY, user1.getId(), film1.getId());
        jdbc.update(ADD_LIKE_QUERY, user2.getId(), film1.getId());
        jdbc.update(ADD_LIKE_QUERY, user1.getId(), film2.getId());

        List<FilmWithRating> popularFilmsNoFilter = likesStorage.getPopularFilms(3, null, null);
        assertThat(popularFilmsNoFilter).hasSize(2);
        assertThat(popularFilmsNoFilter.get(0).getFilm().getId()).isEqualTo(film1.getId()); //2 лайка
        assertThat(popularFilmsNoFilter.get(1).getFilm().getId()).isEqualTo(film2.getId()); //1 лайк


        List<FilmWithRating> popularFilmsByYear = likesStorage.getPopularFilms(2, null, 2001);
        assertThat(popularFilmsByYear).hasSize(1);
        assertThat(popularFilmsByYear.get(0).getFilm().getId()).isEqualTo(film2.getId()); //1 лайк
    }
}