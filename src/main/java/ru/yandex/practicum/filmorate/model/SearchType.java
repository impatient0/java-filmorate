package ru.yandex.practicum.filmorate.model;

public enum SearchType {
    TITLE("title"),
    DIRECTOR("director");

    private final String value;

    SearchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SearchType fromString(String value) {
        for (SearchType type : SearchType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Недопустимый тип поиска: " + value);
    }
}