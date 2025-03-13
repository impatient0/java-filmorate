package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmWithRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.DbFilmStorage;
import ru.yandex.practicum.filmorate.repository.mappers.FilmWithAvgRatingDataMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbFilmStorage.class, FilmWithAvgRatingDataMapper.class})
class DbFilmStorageTest {

    private static final String DELETE_FILMS_QUERY = "DELETE FROM films";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres";
    private static final String CHECK_FILM_QUERY =
        "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ? AND name = ? AND description = ? "
            + "AND release_date = ? AND duration = ? AND mpa_rating_id = ?)";
    private static final String ADD_GENRE_QUERY =
        "INSERT INTO film_genres (film_id, genre_id)" + " VALUES (?, ?)";
    private static final String CHECK_FILM_EXISTS_QUERY =
        "SELECT EXISTS (SELECT 1 FROM films " + "WHERE film_id = ?)";

    private final DbFilmStorage filmStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute(DELETE_FILMS_QUERY);
        jdbc.execute(DELETE_FILM_GENRES_QUERY);
    }

    @AfterEach
    void tearDown() {
        jdbc.execute(DELETE_FILMS_QUERY);
        jdbc.execute(DELETE_FILM_GENRES_QUERY);
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
    void testAddFilm() {
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G");

        assertThat(Boolean.TRUE.equals(
            jdbc.queryForObject(CHECK_FILM_QUERY, Boolean.class, film.getId(), film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId()))).isTrue();
    }

    @Test
    void testGetFilmById() {
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G");

        Optional<FilmWithRating> filmOptional = filmStorage.getFilmById(film.getId());

        assertThat(filmOptional).isPresent().hasValueSatisfying(retrievedFilmWithRating -> {
            Film retrievedFilm = retrievedFilmWithRating.getFilm();
            assertThat(retrievedFilm).hasFieldOrPropertyWithValue("id", film.getId());
            assertThat(retrievedFilm).hasFieldOrPropertyWithValue("name", film.getName());
            assertThat(retrievedFilm).hasFieldOrPropertyWithValue("description",
                film.getDescription());
            assertThat(retrievedFilm).hasFieldOrPropertyWithValue("releaseDate",
                film.getReleaseDate());
            assertThat(retrievedFilm).hasFieldOrPropertyWithValue("duration", film.getDuration());
            assertThat(retrievedFilm).hasFieldOrPropertyWithValue("mpa", film.getMpa());
        });
    }

    @Test
    void testUpdateFilm() {
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G");
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        film.getGenres().add(genre);
        jdbc.update(ADD_GENRE_QUERY, film.getId(), genre.getId());

        Film updatedFilm = new Film();
        updatedFilm.setId(film.getId());
        updatedFilm.setName("Updated Film");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(1999, 1, 1));
        updatedFilm.setDuration(150);
        MpaRating updatedMpaRating = new MpaRating();
        updatedMpaRating.setId(2);
        updatedMpaRating.setName("PG");
        updatedFilm.setMpa(updatedMpaRating);
        updatedFilm.setGenres(new HashSet<>());
        updatedFilm.getGenres().add(genre);

        filmStorage.updateFilm(updatedFilm);

        Optional<FilmWithRating> retrievedFilm = filmStorage.getFilmById(film.getId());
        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getFilm()).isEqualTo(updatedFilm);
    }

    @Test
    void testCheckFilmExists() {
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G");

        assertThat(
            jdbc.queryForObject(CHECK_FILM_EXISTS_QUERY, Boolean.class, film.getId())).isTrue();
        assertThat(jdbc.queryForObject(CHECK_FILM_EXISTS_QUERY, Boolean.class,
            film.getId() + 42L)).isFalse();
    }

    @Test
    void testGetAllFilms() {
        Film film1 = createFilm("Test Film 1", "Test Description 1", LocalDate.of(2000, 1, 1), 120,
            1, "G");
        Film film2 = createFilm("Test Film 2", "Test Description 2", LocalDate.of(2001, 2, 2), 150,
            2, "PG");

        Collection<FilmWithRating> films = filmStorage.getAllFilms();
        assertThat(films.size()).isEqualTo(2);
        assertThat(films.stream().map(FilmWithRating::getFilm)).contains(film1, film2);
    }

    @Test
    void testDeleteFilm() {
        Film film = createFilm("Test Film", "Test Description", LocalDate.of(2000, 1, 1), 120, 1,
            "G");
        long filmId = film.getId();

        filmStorage.deleteFilm(filmId);

        assertThat(filmStorage.checkFilmExists(filmId)).isFalse();
        assertThat(filmStorage.getFilmById(filmId)).isEmpty();
    }
}