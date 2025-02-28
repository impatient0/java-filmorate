package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.MpaRatingStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaRatingService {

    private final MpaRatingStorage mpaRatingStorage;

    public MpaRating getMpaRatingById(long id) {
        log.debug("Getting MPA rating with ID {}", id);
        return mpaRatingStorage.getMpaRatingById(id).orElseThrow(() -> {
            log.warn("Retrieving MPA rating failed: rating with ID {} not found", id);
            return new MpaRatingNotFoundException("Error when retrieving MPA rating", id);
        });
    }

    public Collection<MpaRating> getAllMpaRatings() {
        log.debug("Getting all MPA ratings");
        return mpaRatingStorage.getAllMpaRatings();
    }
}
