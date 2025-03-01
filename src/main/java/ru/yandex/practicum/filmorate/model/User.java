package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
