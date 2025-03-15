package ru.yandex.practicum.filmorate.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

@Component
@SuppressWarnings("unused")
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setTimestamp(rs.getDate("created_at").toLocalDate());
        event.setUserId(rs.getLong("user_id"));
        event.setEventType(rs.getString("event_name"));
        event.setOperation(rs.getString("operation_name"));
        event.setEventId(rs.getLong("tape_id"));
        event.setEntityId(rs.getLong("entity_id"));
        return event;
    }
}