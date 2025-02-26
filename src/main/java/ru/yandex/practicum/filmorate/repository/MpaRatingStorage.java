package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
public interface MpaRatingStorage {

    boolean checkRatingExists(long ratingId);

    Optional<MpaRating> getRatingById(long ratingId);

    Collection<MpaRating> getAllRatings();

}
