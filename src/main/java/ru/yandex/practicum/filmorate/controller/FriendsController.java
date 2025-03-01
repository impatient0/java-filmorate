package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.FriendsService;

@RestController
@RequestMapping("/users/{id}/friends")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getFriends(@PathVariable long id) {
        log.info("Request to get user {} friends received.", id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(friendsService.getUserFriends(id));
    }

    @GetMapping("/common/{otherId}")
    public ResponseEntity<Collection<UserDto>> getCommonFriends(@PathVariable long id,
        @PathVariable long otherId) {
        log.info("Request to get common friends of user {} and {} received.", id, otherId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(friendsService.getCommonFriends(id, otherId));
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Request to add friend {} to user {} received.", friendId, id);
        friendsService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Request to remove friend {} from user {} received.", friendId, id);
        friendsService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

}
