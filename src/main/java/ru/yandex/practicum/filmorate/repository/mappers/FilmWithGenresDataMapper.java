package ru.yandex.practicum.filmorate.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
@SuppressWarnings("unused")
public class FilmWithGenresDataMapper implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        Map<Long, Film> filmMap = new LinkedHashMap<>();
        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(rs.getInt("mpa_id"));
                mpaRating.setName(rs.getString("mpa_name"));
                if (mpaRating.getId() == 0) {
                    film.setMpa(null);
                } else {
                    film.setMpa(mpaRating);
                }
                film.setGenres(new HashSet<>());
                film.setDirector(new HashSet<>());
                filmMap.put(filmId, film);
            }
            int genreId = rs.getInt("genre_id");
            if (genreId != 0) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
            long directorId = rs.getLong("director_id");
            if (directorId != 0) {
                Director director = new Director();
                director.setId(directorId);
                director.setName(rs.getString("director_name"));
                film.getDirector().add(director);
            }
        }
        return new ArrayList<>(filmMap.values());
    }
}
