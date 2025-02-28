package ru.yandex.practicum.filmorate.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.DbMpaRatingStorage;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRatingRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbMpaRatingStorage.class, MpaRatingRowMapper.class})
class DbMpaRatingStorageTest {

    private final DbMpaRatingStorage mpaRatingStorage;

    @Test
    void testCheckMpaRatingExists() {
        assertThat(mpaRatingStorage.checkMpaRatingExists(1)).isTrue();
        assertThat(mpaRatingStorage.checkMpaRatingExists(2)).isTrue();
        assertThat(mpaRatingStorage.checkMpaRatingExists(3)).isTrue();
        assertThat(mpaRatingStorage.checkMpaRatingExists(4)).isTrue();
        assertThat(mpaRatingStorage.checkMpaRatingExists(5)).isTrue();
    }

    @Test
    void testGetMpaRatingById() {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(1);
        mpaRating.setName("G");
        Optional<MpaRating> mpaRatingOptional = mpaRatingStorage.getMpaRatingById(
            mpaRating.getId());

        assertThat(mpaRatingOptional).isPresent().hasValueSatisfying(retrievedMpaRating -> {
            assertThat(retrievedMpaRating).hasFieldOrPropertyWithValue("id", mpaRating.getId());
            assertThat(retrievedMpaRating).hasFieldOrPropertyWithValue("name", "G");
        });
    }

    @Test
    void testGetAllMpaRatings() {
        List<MpaRating> mpaRatings = (List<MpaRating>) mpaRatingStorage.getAllMpaRatings();
        assertThat(mpaRatings.size()).isEqualTo(5);
    }
}