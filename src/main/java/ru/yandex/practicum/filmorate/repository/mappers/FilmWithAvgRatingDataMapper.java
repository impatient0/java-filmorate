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
import ru.yandex.practicum.filmorate.model.FilmWithRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

@Component
@SuppressWarnings("unused")
public class FilmWithAvgRatingDataMapper implements ResultSetExtractor<List<FilmWithRating>> {

    @Override
    public List<FilmWithRating> extractData(ResultSet rs) throws SQLException {
        List<FilmWithRating> filmWithRatings = new ArrayList<>();
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("description"));

                java.sql.Date sqlDate = rs.getDate("release_date");
                film.setReleaseDate(sqlDate != null ? sqlDate.toLocalDate() : null);
                film.setDuration(rs.getInt("duration"));

                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(rs.getInt("mpa_id"));
                mpaRating.setName(rs.getString("mpa_name"));
                film.setMpa(mpaRating.getId() == 0 ? null : mpaRating);

                film.setGenres(new HashSet<>());
                film.setDirector(new HashSet<>()); // Initialize director set
                filmMap.put(filmId, film);

                double avgRating = rs.getDouble("avg_rating");
                filmWithRatings.add(new FilmWithRating(film,
                    rs.wasNull() ? 0.0 : avgRating)); // Handle null avg_rating
            }

            int genreId = rs.getInt("genre_id");
            if (genreId != 0) {
                String genreName = rs.getString("genre_name");
                if (genreName != null) {
                    film.getGenres().add(new Genre(genreId, genreName));
                }
            }

            long directorId = rs.getLong("director_id");
            if (directorId != 0) {
                Director director = new Director();
                director.setId(directorId);
                director.setName(rs.getString("director_name"));
                film.getDirector().add(director);
            }
        }
        return filmWithRatings;
    }
}