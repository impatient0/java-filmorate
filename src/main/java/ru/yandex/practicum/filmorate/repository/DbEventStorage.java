package ru.yandex.practicum.filmorate.repository;

import java.util.LinkedHashSet;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

@Repository
@SuppressWarnings("unused")
public class DbEventStorage extends DbBaseStorage<Event> implements EventStorage {

    private static final String GET_USER_TAPE_QUERY = """
            SELECT
                f.created_at as created_at,
                f.user_id as user_id,
                e.name as event_name,
                o.name as operation_name,
                f.feed_id as feed_id,
                f.entity_id as entity_id
            FROM user_feed f
            LEFT JOIN events e ON f.event_id = e.event_id
            LEFT JOIN operations o ON f.operation_id = o.operation_id
            WHERE user_id = ?
            ORDER BY created_at
            """;

    private static final String INSERT_USER_TAPE_QUERY = "INSERT INTO user_feed "
            + "(user_id, event_id, operation_id, entity_id, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";


    public DbEventStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void insertUserFeedQuery(long userId, int eventId, int operationId, long entityId) {
        jdbc.update(INSERT_USER_TAPE_QUERY, userId, eventId, operationId, entityId);
    }

    @Override
    public LinkedHashSet<Event> getUserEvents(long userId) {
        return new LinkedHashSet<>(getMultiple(GET_USER_TAPE_QUERY, userId));
    }
}