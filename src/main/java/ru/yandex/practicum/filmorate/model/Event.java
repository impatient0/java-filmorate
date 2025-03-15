package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Event {

    private LocalDate timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long eventId;
    private Long entityId;
}
