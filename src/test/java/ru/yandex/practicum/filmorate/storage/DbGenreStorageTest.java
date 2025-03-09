package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.DbGenreStorage;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbGenreStorage.class, GenreRowMapper.class})
class DbGenreStorageTest {

    private final DbGenreStorage genreStorage;

    @Test
    void testCheckGenreExists() {
        assertThat(genreStorage.checkGenreExists(1)).isTrue();
        assertThat(genreStorage.checkGenreExists(2)).isTrue();
        assertThat(genreStorage.checkGenreExists(3)).isTrue();
        assertThat(genreStorage.checkGenreExists(4)).isTrue();
        assertThat(genreStorage.checkGenreExists(5)).isTrue();
        assertThat(genreStorage.checkGenreExists(6)).isTrue();
    }

    @Test
    void testGetGenreById() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        Optional<Genre> genreOptional = genreStorage.getGenreById(genre.getId());

        assertThat(genreOptional).isPresent().hasValueSatisfying(retrievedGenre -> {
            assertThat(retrievedGenre).hasFieldOrPropertyWithValue("id", genre.getId());
            assertThat(retrievedGenre).hasFieldOrPropertyWithValue("name", "Комедия");
        });
    }

    @Test
    void testGetAllGenres() {
        List<Genre> genres = (List<Genre>) genreStorage.getAllGenres();
        assertThat(genres.size()).isEqualTo(6);
    }
}