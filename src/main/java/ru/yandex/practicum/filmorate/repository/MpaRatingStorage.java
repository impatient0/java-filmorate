package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;
import ru.yandex.practicum.filmorate.model.MpaRating;

public interface MpaRatingStorage {

    Optional<MpaRating> getRatingById(long ratingId);

    Collection<MpaRating> getAllRatings();

}
