package ru.yandex.practicum.filmorate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.service.RecommendationService;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class RecommendationInitializer {

    private final RecommendationService recommendationService;

    @EventListener(ApplicationReadyEvent.class)
    public void calculateDiffAndFreqOnStartup() {
        recommendationService.calculateDiffAndFreq();
    }
}