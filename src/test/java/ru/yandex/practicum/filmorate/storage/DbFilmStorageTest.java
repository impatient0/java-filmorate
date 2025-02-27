package ru.yandex.practicum.filmorate.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.DbFilmStorage;
import ru.yandex.practicum.filmorate.repository.mappers.FilmWithGenresDataMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbFilmStorage.class, FilmWithGenresDataMapper.class})
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

    @Test
    void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1);
        film.setMpa(mpaRating);
        long filmId = filmStorage.addFilm(film);
        film.setId(filmId);

        assertThat(Boolean.TRUE.equals(
            jdbc.queryForObject(CHECK_FILM_QUERY, Boolean.class, film.getId(), film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId()))).isTrue();
    }

    @Test
    void testGetFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1);
        mpaRating.setName("G");
        film.setMpa(mpaRating);
        long filmId = filmStorage.addFilm(film);
        film.setId(filmId);

        Optional<Film> filmOptional = filmStorage.getFilmById(filmId);

        assertThat(filmOptional).isPresent().hasValueSatisfying(retrievedFilm -> {
            assertThat(retrievedFilm).hasFieldOrPropertyWithValue("id", filmId);
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
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1);
        film.setMpa(mpaRating);
        mpaRating.setName("G");
        film.setGenres(new HashSet<>());
        long filmId = filmStorage.addFilm(film);
        film.setId(filmId);
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        film.getGenres().add(genre);
        jdbc.update(ADD_GENRE_QUERY, filmId, genre.getId());

        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
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

        Optional<Film> retrievedFilm = filmStorage.getFilmById(filmId);
        assertThat(retrievedFilm).isPresent().get().isEqualTo(updatedFilm);
    }

    @Test
    void testCheckFilmExists() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1);
        mpaRating.setName("G");
        film.setMpa(mpaRating);
        long filmId = filmStorage.addFilm(film);

        assertThat(jdbc.queryForObject(CHECK_FILM_EXISTS_QUERY, Boolean.class, filmId)).isTrue();
        assertThat(
            jdbc.queryForObject(CHECK_FILM_EXISTS_QUERY, Boolean.class, filmId + 42L)).isFalse();
    }

    @Test
    void testGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Test Film 1");
        film1.setDescription("Test Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        MpaRating mpaRating1 = new MpaRating();
        mpaRating1.setId(1);
        mpaRating1.setName("G");
        film1.setMpa(mpaRating1);
        film1.setGenres(Set.of());
        long film1Id = filmStorage.addFilm(film1);
        film1.setId(film1Id);

        Film film2 = new Film();
        film2.setName("Test Film 2");
        film2.setDescription("Test Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 2, 2));
        film2.setDuration(150);
        MpaRating mpaRating2 = new MpaRating();
        mpaRating2.setId(2);
        mpaRating2.setName("PG");
        film2.setMpa(mpaRating2);
        film2.setGenres(Set.of());
        long film2Id = filmStorage.addFilm(film2);
        film2.setId(film2Id);

        Collection<Film> films = filmStorage.getAllFilms();
        assertThat(films.size()).isEqualTo(2);
        assertThat(films).contains(film1, film2);
    }
}
