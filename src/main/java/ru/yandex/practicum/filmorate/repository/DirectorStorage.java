package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorStorage {

    boolean checkDirectorExists(long directorId);

    Optional<Director> getDirectorById(long directorId);

    long addDirector(Director director);

    void updateDirector(Director director);

    void removeDirector(long directorId);

    Collection<Director> getAllDirectors();

}