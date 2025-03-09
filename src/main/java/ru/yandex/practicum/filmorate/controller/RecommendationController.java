package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.RecommendationService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{id}/recommendations")
    public Collection<FilmDto> getRecommendations(@PathVariable long id) {
        log.info("GET request: Getting recommendations for user with ID {}", id);
        return recommendationService.getRecommendations(id);
    }
}