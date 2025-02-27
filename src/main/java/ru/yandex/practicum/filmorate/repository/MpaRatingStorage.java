package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
public interface MpaRatingStorage {

    boolean checkMpaRatingExists(long ratingId);

    Optional<MpaRating> getMpaRatingById(long ratingId);

    Collection<MpaRating> getAllMpaRatings();

}
