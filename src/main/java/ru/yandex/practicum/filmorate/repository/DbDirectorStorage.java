package ru.yandex.practicum.filmorate.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

@Repository
@Primary
@SuppressWarnings("unused")
public class DbDirectorStorage extends DbBaseStorage<Director> implements DirectorStorage {

    private static final String CHECK_EXISTS_QUERY = "SELECT EXISTS (SELECT 1 FROM directors WHERE " + "director_id = ?)";
    private static final String GET_ALL_QUERY = "SELECT * FROM directors ORDER BY director_id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors (name) " + "VALUES (?)";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? " + "WHERE director_id = ?";



    public DbDirectorStorage(JdbcTemplate jdbc, RowMapper<Director> mapper)  {
        super(jdbc, mapper);
    }

    @Override
    public boolean checkDirectorExists(long directorId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, directorId));
    }

    @Override
    public Optional<Director> getDirectorById(long directorId)  {
        return getSingle(GET_BY_ID_QUERY, directorId);
    }

    @Override
    public long addDirector(Director director) {
        long assignedId = insert(INSERT_QUERY, director.getName());
        return assignedId;
    }

    @Override
    public void updateDirector(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
    }

    @Override
    public Collection<Director> getAllDirectors()  {
        return new ArrayList<>(getMultiple(GET_ALL_QUERY));
    }

    @Override
    public void removeDirector(long directorId) {
        jdbc.update(DELETE_QUERY, directorId);
    }
}
