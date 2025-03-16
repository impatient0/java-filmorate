package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationControllerTest {

    private static final String USERS_URL = "/users";
    private static final String RECOMMENDATIONS_URL = "/recommendations";
    private static final String FILMS_URL = "/films";
    private static final String LIKES_URL = "/like";
    private static final String DELETE_LIKES_QUERY = "DELETE FROM ratings";
    private static final String DELETE_FILMS_QUERY = "DELETE FROM films";
    private static final String DELETE_USERS_QUERY = "DELETE FROM users";
    protected final JdbcTemplate jdbc;
    private final TestRestTemplate restTemplate;

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

    private UserDto createUserDto(String email, String login, String name, LocalDate birthday) {
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    private FilmDto createFilmDto(String name, String description, LocalDate releaseDate,
        int duration, int mpaId, String mpaName) {
        FilmDto film = new FilmDto();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(mpaId);
        mpaRating.setName(mpaName);
        film.setMpa(mpaRating);
        film.setGenres(List.of());
        return film;
    }

    private ResponseEntity<UserDto> createUser(UserDto userDto) {
        return restTemplate.postForEntity(USERS_URL, userDto, UserDto.class);
    }

    private ResponseEntity<FilmDto> createFilm(FilmDto filmDto) {
        return restTemplate.postForEntity(FILMS_URL, filmDto, FilmDto.class);
    }

    private ResponseEntity<FilmDto[]> getRecommendations(long userId) {
        return restTemplate.getForEntity(USERS_URL + "/" + userId + RECOMMENDATIONS_URL,
            FilmDto[].class);
    }

    private void rateFilm(long userId, long filmId, int rating) {
        HttpEntity<Void> requestEntity = new HttpEntity<>(null);
        restTemplate.exchange(FILMS_URL + "/" + filmId + LIKES_URL + "/" + userId + "/" + rating,
            HttpMethod.PUT, requestEntity, Void.class);
    }

    @Test
    void testGetRecommendationsForUserWithOneRatedFilm() {
        // Create users
        ResponseEntity<UserDto> user1Entity = createUser(
            createUserDto("user1@example.com", "user1login", "User 1", LocalDate.of(2000, 1, 1)));
        ResponseEntity<UserDto> user2Entity = createUser(
            createUserDto("user2@example.com", "user2login", "User 2", LocalDate.of(2001, 2, 2)));
        UserDto user1 = user1Entity.getBody();
        UserDto user2 = user2Entity.getBody();

        // Create films
        ResponseEntity<FilmDto> film1Entity = createFilm(
            createFilmDto("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120, 1,
                "G"));
        ResponseEntity<FilmDto> film2Entity = createFilm(
            createFilmDto("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150, 2,
                "PG"));
        ResponseEntity<FilmDto> film3Entity = createFilm(
            createFilmDto("Test Film 3", "Test Description 3", LocalDate.of(2002, 3, 3), 180, 3,
                "PG-13"));
        FilmDto film1 = film1Entity.getBody();
        FilmDto film2 = film2Entity.getBody();
        FilmDto film3 = film3Entity.getBody();

        // User 1 rates film 1 and 2
        rateFilm(user1.getId(), film1.getId(), 10);
        rateFilm(user1.getId(), film2.getId(), 9);

        // User 2 rates film 2
        rateFilm(user2.getId(), film2.getId(), 8);

        // Get recommendations for user 2
        ResponseEntity<FilmDto[]> recommendationsEntity = getRecommendations(user2.getId());
        assertThat(recommendationsEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FilmDto[] recommendations = recommendationsEntity.getBody();
        assertThat(recommendations).isNotNull();
        assertThat(recommendations.length).isEqualTo(1);
        assertThat(recommendations[0].getId()).isEqualTo(film1.getId());
    }

    @Test
    void testGetRecommendationsForUserWithNoRatedFilms() {
        // Create a user
        ResponseEntity<UserDto> userEntity = createUser(
            createUserDto("user1@example.com", "user1login", "User 1", LocalDate.of(2000, 1, 1)));
        UserDto user = userEntity.getBody();
        // Get recommendations for the user
        ResponseEntity<FilmDto[]> recommendationsEntity = getRecommendations(user.getId());
        assertThat(recommendationsEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FilmDto[] recommendations = recommendationsEntity.getBody();
        assertThat(recommendations).isNotNull();
        assertThat(recommendations.length).isEqualTo(0);
    }

    @Test
    void testGetRecommendationsForUserWhoRatedAllFilms() {
        // Create users
        ResponseEntity<UserDto> user1Entity = createUser(
            createUserDto("user1@example.com", "user1login", "User 1", LocalDate.of(2000, 1, 1)));
        ResponseEntity<UserDto> user2Entity = createUser(
            createUserDto("user2@example.com", "user2login", "User 2", LocalDate.of(2001, 2, 2)));
        UserDto user1 = user1Entity.getBody();
        UserDto user2 = user2Entity.getBody();

        // Create films
        ResponseEntity<FilmDto> film1Entity = createFilm(
            createFilmDto("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120, 1,
                "G"));
        ResponseEntity<FilmDto> film2Entity = createFilm(
            createFilmDto("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150, 2,
                "PG"));
        ResponseEntity<FilmDto> film3Entity = createFilm(
            createFilmDto("Test Film 3", "Test Description 3", LocalDate.of(2002, 3, 3), 180, 3,
                "PG-13"));
        FilmDto film1 = film1Entity.getBody();
        FilmDto film2 = film2Entity.getBody();
        FilmDto film3 = film3Entity.getBody();

        // User 1 rates all films
        rateFilm(user1.getId(), film1.getId(), 10);
        rateFilm(user1.getId(), film2.getId(), 9);
        rateFilm(user1.getId(), film3.getId(), 8);

        // User 2 rates films 1 and 2
        rateFilm(user2.getId(), film1.getId(), 7);
        rateFilm(user2.getId(), film2.getId(), 6);

        // Get recommendations for user 1
        ResponseEntity<FilmDto[]> recommendationsEntity = getRecommendations(user1.getId());
        assertThat(recommendationsEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FilmDto[] recommendations = recommendationsEntity.getBody();
        assertThat(recommendations).isNotNull();
        assertThat(recommendations.length).isEqualTo(0);
    }

    @Test
    void testGetRecommendationsForMultipleUsers() {
        // Create users
        ResponseEntity<UserDto> user1Entity = createUser(
            createUserDto("user1@example.com", "user1login", "User 1", LocalDate.of(2000, 1, 1)));
        ResponseEntity<UserDto> user2Entity = createUser(
            createUserDto("user2@example.com", "user2login", "User 2", LocalDate.of(2001, 2, 2)));
        ResponseEntity<UserDto> user3Entity = createUser(
            createUserDto("user3@example.com", "user3login", "User 3", LocalDate.of(2002, 3, 3)));
        UserDto user1 = user1Entity.getBody();
        UserDto user2 = user2Entity.getBody();
        UserDto user3 = user3Entity.getBody();

        // Create films
        ResponseEntity<FilmDto> film1Entity = createFilm(
            createFilmDto("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120, 1,
                "G"));
        ResponseEntity<FilmDto> film2Entity = createFilm(
            createFilmDto("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150, 2,
                "PG"));
        ResponseEntity<FilmDto> film3Entity = createFilm(
            createFilmDto("Test Film 3", "Test Description 3", LocalDate.of(2002, 3, 3), 180, 3,
                "PG-13"));
        FilmDto film1 = film1Entity.getBody();
        FilmDto film2 = film2Entity.getBody();
        FilmDto film3 = film3Entity.getBody();

        // User 1 rates film 1
        rateFilm(user1.getId(), film1.getId(), 10);

        // User 2 rates films 1 and 2
        rateFilm(user2.getId(), film1.getId(), 9);
        rateFilm(user2.getId(), film2.getId(), 8);

        // User 3 rates film 2 and 3
        rateFilm(user3.getId(), film2.getId(), 7);
        rateFilm(user3.getId(), film3.getId(), 6);

        // Get recommendations for user 1
        ResponseEntity<FilmDto[]> recommendations1Entity = getRecommendations(user1.getId());
        assertThat(recommendations1Entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FilmDto[] recommendations1 = recommendations1Entity.getBody();
        assertThat(recommendations1).isNotNull();
        assertThat(recommendations1.length).isEqualTo(1);
        assertThat(recommendations1[0].getId()).isEqualTo(film2.getId());

        // Get recommendations for user 2
        ResponseEntity<FilmDto[]> recommendations2Entity = getRecommendations(user2.getId());
        assertThat(recommendations2Entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FilmDto[] recommendations2 = recommendations2Entity.getBody();
        assertThat(recommendations2).isNotNull();
        assertThat(recommendations2.length).isEqualTo(1);
        assertThat(recommendations2[0].getId()).isEqualTo(film3.getId());

        // Get recommendations for user 3
        ResponseEntity<FilmDto[]> recommendations3Entity = getRecommendations(user3.getId());
        assertThat(recommendations3Entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FilmDto[] recommendations3 = recommendations3Entity.getBody();
        assertThat(recommendations3).isNotNull();
        assertThat(recommendations3.length).isEqualTo(1);
        assertThat(recommendations3[0].getId()).isEqualTo(film1.getId());
    }

    @Test
    void testGetRecommendationsWithLowRatedFilms() {
        // Create users
        ResponseEntity<UserDto> user1Entity = createUser(
            createUserDto("user1@example.com", "user1login", "User 1", LocalDate.of(2000, 1, 1)));
        ResponseEntity<UserDto> user2Entity = createUser(
            createUserDto("user2@example.com", "user2login", "User 2", LocalDate.of(2001, 2, 2)));
        UserDto user1 = user1Entity.getBody();
        UserDto user2 = user2Entity.getBody();

        // Create films
        ResponseEntity<FilmDto> film1Entity = createFilm(
            createFilmDto("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120, 1,
                "G"));
        ResponseEntity<FilmDto> film2Entity = createFilm(
            createFilmDto("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150, 2,
                "PG"));
        ResponseEntity<FilmDto> film3Entity = createFilm(
            createFilmDto("Test Film 3", "Test Description 3", LocalDate.of(2002, 3, 3), 180, 3,
                "PG-13"));
        FilmDto film1 = film1Entity.getBody();
        FilmDto film2 = film2Entity.getBody();
        FilmDto film3 = film3Entity.getBody();

        // User 1 rates film 1 and 2
        rateFilm(user1.getId(), film1.getId(), 1);
        rateFilm(user1.getId(), film2.getId(), 2);

        // User 2 rates film 2 and 3
        rateFilm(user2.getId(), film2.getId(), 8);
        rateFilm(user2.getId(), film3.getId(), 3);

        // Get recommendations for user 2
        ResponseEntity<FilmDto[]> recommendationsEntity = getRecommendations(user2.getId());
        assertThat(recommendationsEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        FilmDto[] recommendations = recommendationsEntity.getBody();
        assertThat(recommendations).isNotNull();
        assertThat(recommendations.length).isEqualTo(0);

        // User 1 rates film 3
        rateFilm(user1.getId(), film3.getId(), 10);

        // Get recommendations for user 2
        recommendationsEntity = getRecommendations(user2.getId());
        assertThat(recommendationsEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        recommendations = recommendationsEntity.getBody();
        assertThat(recommendations).isNotNull();
        assertThat(recommendations.length).isEqualTo(0);

        // User 2 rates film 1
        rateFilm(user2.getId(), film1.getId(), 10);

        // Get recommendations for user 1
        recommendationsEntity = getRecommendations(user1.getId());
        assertThat(recommendationsEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        recommendations = recommendationsEntity.getBody();
        assertThat(recommendations).isNotNull();
        assertThat(recommendations.length).isEqualTo(0);
    }
}