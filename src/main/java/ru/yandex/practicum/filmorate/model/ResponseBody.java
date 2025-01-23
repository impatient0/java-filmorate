package ru.yandex.practicum.filmorate.model;

public sealed interface ResponseBody permits User, Film, ErrorMessage {

}
