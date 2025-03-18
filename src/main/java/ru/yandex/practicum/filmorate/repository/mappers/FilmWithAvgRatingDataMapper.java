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
        Map<Long, FilmWithRating> filmWithRatingMap = new LinkedHashMap<>(); // Map to hold
        // FilmWithRating by filmId

        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            FilmWithRating filmWithRating = filmWithRatingMap.get(filmId);

            if (filmWithRating == null) {
                Film film = new Film();
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
                film.setDirectors(new HashSet<>()); // Initialize director set

                double avgRating = rs.getDouble("avg_rating");
                filmWithRating = new FilmWithRating(film, rs.wasNull() ? 0.0 : avgRating);
                filmWithRatingMap.put(filmId, filmWithRating); // Put FilmWithRating in the map
                filmWithRatings.add(filmWithRating); // Add FilmWithRating to the list
            } else {
                // FilmWithRating already exists, retrieve the Film object
                Film film = filmWithRating.getFilm();
            }

            int genreId = rs.getInt("genre_id");
            if (genreId != 0) {
                String genreName = rs.getString("genre_name");
                if (genreName != null) {
                    filmWithRating.getFilm().getGenres().add(new Genre(genreId, genreName));
                }
            }

            long directorId = rs.getLong("director_id");
            if (directorId != 0) {
                Director director = new Director();
                director.setId(directorId);
                director.setName(rs.getString("director_name"));
                filmWithRating.getFilm().getDirectors().add(director);
            }
        }
        return filmWithRatings;
    }
}