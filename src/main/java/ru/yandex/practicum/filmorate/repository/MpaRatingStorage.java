package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.MpaRating;

public interface MpaRatingStorage {

    boolean checkMpaRatingExists(long ratingId);

    Optional<MpaRating> getMpaRatingById(long ratingId);

    Collection<MpaRating> getAllMpaRatings();

}
