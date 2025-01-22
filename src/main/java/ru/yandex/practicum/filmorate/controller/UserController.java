package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

@RestController
@RequestMapping("/users")
@SuppressWarnings("unused")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        log.info("Request to get all users received.");
        return ResponseEntity.ok(users.values());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User newUser, BindingResult result) {
        log.info("Request to create new user received: {}", newUser);
        if (result.hasErrors()) {
            log.warn("Validation error for creating new user: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        newUser.setId(getNextId());
        if (newUser.getName() == null) {
            log.debug("User name is not provided. Using login as name.");
            newUser.setName(newUser.getLogin());
        }
        users.put(newUser.getId(), newUser);
        log.info("New user created with ID {}", newUser.getId());
        return ResponseEntity.ok(newUser);
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody User newUser, BindingResult result) {
        log.info("Request to update user received: {}", newUser);
        if (result.hasErrors()) {
            log.warn("Validation error for updating user: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        if (users.get(newUser.getId()) != null) {
            if (newUser.getName() == null) {
                log.debug("User name is not provided. Using login as name.");
                newUser.setName(newUser.getLogin());
            }
            users.put(newUser.getId(), newUser);
            log.info("User with ID {} updated", newUser.getId());
            return ResponseEntity.ok(newUser);
        }
        log.warn("User with ID {} not found for updating", newUser.getId());
        Map<String, String> message = Map.of("message",
            String.format("User with ID %d not found for updating", newUser.getId()));
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
            .stream()
            .mapToLong(id -> id)
            .max()
            .orElse(0);
        log.debug("Generated next ID: {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}