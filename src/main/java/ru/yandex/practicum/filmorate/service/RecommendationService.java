package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.repository.LikesStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final LikesStorage likesStorage;
    private final FilmMapper filmMapper;

    void calculateDiffAndFreq() {
    }

    void updateDiffAndFreq(long userId, long filmId) {
    }

    Collection<FilmDto> getRecommendations(long userId) {
        return List.of();
    }

}
