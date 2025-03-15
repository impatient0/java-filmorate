package ru.yandex.practicum.filmorate.repository;

import java.util.HashSet;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

@Repository
@SuppressWarnings("unused")
public class DbEventStorage extends DbBaseStorage<Event> implements EventStorage {

    private static final String GET_USER_TAPE_QUERY = """
            SELECT
                t.created_at as created_at,
                t.user_id as user_id,
                e.name as event_name,
                o.name as operation_name,
                t.tape_id as tape_id,
                t.entity_id as entity_id
            FROM user_tape t
            LEFT JOIN events e ON t.event_id = e.event_id
            LEFT JOIN operations o ON t.operation_id = o.operation_id
            WHERE user_id = ?
            """;

    private static final String INSERT_USER_TAPE_QUERY = "INSERT INTO user_tape "
            + "(user_id, event_id, operation_id, entity_id, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";


    public DbEventStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void insertUserTapeQuery(long userId, int eventId, int operationId, long entityId) {
        jdbc.update(INSERT_USER_TAPE_QUERY, userId, eventId, operationId, entityId);
    }

    @Override
    public Set<Event> getUserEvents(long userId) {
        return new HashSet<>(getMultiple(GET_USER_TAPE_QUERY, userId));
    }
}