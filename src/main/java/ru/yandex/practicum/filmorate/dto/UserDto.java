package ru.yandex.practicum.filmorate.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}