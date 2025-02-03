package ru.yandex.practicum.filmorate.controller;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.yandex.practicum.filmorate.model.ResponseBody;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private final UserService userService;
    private final long currentId = 1L;

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        log.info("Request to get all users received.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        log.info("Request to get user with ID {} received.", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> create(@RequestBody User newUser) {
        log.info("Request to create new user received: {}", newUser);
        User createdUser = userService.addUser(newUser);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(createdUser.getId()).toUri();
        log.info("New user created with ID {}", createdUser.getId());
        return ResponseEntity.created(location).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<ResponseBody> update(@RequestBody User newUser) {
        log.info("Request to update user received: {}", newUser);
        userService.updateUser(newUser);
        log.info("User with ID {} updated", newUser.getId());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(newUser);
    }
}