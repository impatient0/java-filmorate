package ru.yandex.practicum.filmorate.controller;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private final UserService userService;
    private final EventService eventService;
    private final long currentId = 1L;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAllUsers() {
        log.info("Request to get all users received.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable long id) {
        log.info("Request to get user with ID {} received.", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/feed")
    public ResponseEntity<Collection<Event>> getEventsById(@PathVariable long id) {
        log.info("Request to get all events of the user with ID {} received.", id);
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody NewUserRequest newUserRequest) {
        log.info("Request to create new user received: {}", newUserRequest);
        UserDto createdUser = userService.addUser(newUserRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(createdUser.getId()).toUri();
        log.info("New user created with ID {}", createdUser.getId());
        return ResponseEntity.created(location).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<UserDto> update(@RequestBody UpdateUserRequest updateUserRequest) {
        log.info("Request to update user received: {}", updateUserRequest);
        UserDto updatedUser = userService.updateUser(updateUserRequest);
        log.info("User with ID {} updated", updateUserRequest.getId());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        log.info("Request to delete user with ID {} received.", id);
        userService.deleteUser(id);
        log.info("User with ID {} deleted.", id);
        return ResponseEntity.noContent().build();
    }
}