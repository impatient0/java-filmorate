package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public non-sealed class User implements ResponseBody {

    long id;
    @Email(message = "E-mail must be valid.")
    private String email;
    @Pattern(regexp = "\\S+", message = "Login must not be blank or contain whitespaces.")
    private String login;
    private String name;
    @PastOrPresent(message = "Birthdate must not be in the future.")
    private LocalDate birthday;
}
