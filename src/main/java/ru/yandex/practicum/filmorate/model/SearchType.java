package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    TITLE("title"),
    DIRECTOR("director");

    private final String value;

    public static SearchType fromString(String value) {
        for (SearchType type : SearchType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid search type: " + value);
    }
}