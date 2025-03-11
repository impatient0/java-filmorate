package ru.yandex.practicum.filmorate.controller;

import java.net.URI;
import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public ResponseEntity<Collection<Director>> getAllDirectors() {
        log.info("Request to get all directors received.");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(directorService.getAllDirectors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable long id) {
        log.info("Request to get director with ID {} received.", id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(directorService.getDirectorById(id));
    }

    @PostMapping
    public ResponseEntity<DirectorDto> create(@RequestBody NewDirectorRequest newDirectorRequest) {
        log.info("Request to create new director received: {}", newDirectorRequest);
        DirectorDto createdDirector = directorService.addDirector(newDirectorRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdDirector.getId()).toUri();
        log.info("New director created with ID {}", createdDirector.getId());
        return ResponseEntity.created(location).contentType(MediaType.APPLICATION_JSON).body(createdDirector);
    }

    @PutMapping
    public ResponseEntity<DirectorDto> update(@RequestBody UpdateDirectorRequest updateDirectorRequest) {
        log.info("Request to update director received: {}", updateDirectorRequest);
        DirectorDto updatedDirector = directorService.updateDirector(updateDirectorRequest);
        log.info("Director with ID {} updated", updatedDirector.getId());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updatedDirector);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeDirector(@PathVariable long id) {
        log.info("Request to remove director with id {} received.", id);
        directorService.delDirector(id);
        return ResponseEntity.ok().build();
    }
}