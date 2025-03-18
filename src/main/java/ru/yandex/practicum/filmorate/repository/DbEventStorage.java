package ru.yandex.practicum.filmorate.repository;

import java.util.ArrayList;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

@Repository
@SuppressWarnings("unused")
public class DbEventStorage extends DbBaseStorage<Event> implements EventStorage {

    private static final String GET_USER_TAPE_QUERY = """
            SELECT
                created_at,
                user_id,
                event_name,
                operation_name,
                feed_id,
                entity_id
            FROM user_feed
            WHERE user_id = ?
            ORDER BY created_at
            """;

    private static final String INSERT_USER_FEED_QUERY = "INSERT INTO user_feed "
            + "(user_id, event_name, operation_name, entity_id, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";


    public DbEventStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void insertUserFeedQuery(long userId, String eventName, String operationName, long entityId) {
        jdbc.update(INSERT_USER_FEED_QUERY, userId, eventName, operationName, entityId);
    }

    @Override
    public ArrayList<Event> getUserEvents(long userId) {
        return new ArrayList<>(getMultiple(GET_USER_TAPE_QUERY, userId));
    }
}